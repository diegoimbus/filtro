package co.moviired.transaction.repository;

import co.moviired.transaction.conf.database.DatabasePool;
import co.moviired.transaction.domain.request.RequestManager;
import co.moviired.transaction.helper.HistoryConstants;
import co.moviired.transaction.helper.UtilitiesHelper;
import co.moviired.transaction.model.dto.TransactionDTO;
import co.moviired.transaction.properties.GlobalParameters;
import co.moviired.transaction.properties.MahindraDbProperties;
import co.moviired.transaction.properties.MoviiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@PropertySource("classpath:transaction.properties")
public class TransactionNewRepository {

    private static final String SEPARATOR = "\\|";
    private final GlobalParameters globalParameters;
    private final MoviiService moviiService;
    private final MahindraDbProperties mahindraDbProperties;
    private final DatabasePool poolConvenioDB;
    private final DataSource dataSourceMH;
    private final Map<String, String> hashMapCategory;
    @Value("${history.moviired.transactions}")
    private String moviiredTransactionsQuery;
    @Value("${history.moviired.transactions.detail}")
    private String moviiredTransactionsQueryDetail;

    public TransactionNewRepository(@NotNull GlobalParameters pGlobalParameters,
                                    @NotNull MahindraDbProperties pMahindraDbProperties,
                                    @NotNull MoviiService pMoviiService,
                                    @NotNull DatabasePool pPoolConvenioDB,
                                    DataSource pDataSource) {
        this.globalParameters = pGlobalParameters;
        this.moviiService = pMoviiService;
        this.mahindraDbProperties = pMahindraDbProperties;
        this.poolConvenioDB = pPoolConvenioDB;
        this.dataSourceMH = pDataSource;
        this.hashMapCategory = findTransactionConvenio();
    }

    public List<TransactionDTO> findTransaction(RequestManager requestManager) {

        List<TransactionDTO> listTransaction = new ArrayList<>();
        TransactionDTO transaction;
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        StopWatch watch = new StopWatch();

        String query = moviiredTransactionsQuery.replace("##SCHEMA##", this.mahindraDbProperties.getSchema());
        query = applyFilters(requestManager, query);
        log.debug(query);

        try {

            con = this.dataSourceMH.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(HistoryConstants.PARAM_1, requestManager.getCreatedBy());
            ps.setString(HistoryConstants.PARAM_2, requestManager.getCreatedBy());
            ps.setString(HistoryConstants.PARAM_3, requestManager.getCreatedBy());
            ps.setString(HistoryConstants.PARAM_4, requestManager.getStartDate());
            ps.setString(HistoryConstants.PARAM_5, requestManager.getEndDate());
            ps.setInt(HistoryConstants.PARAM_6, Integer.parseInt(requestManager.getPageNumber()) - 1);
            ps.setString(HistoryConstants.PARAM_7, requestManager.getPageSize());

            watch.start();
            rs = ps.executeQuery();
            watch.stop();
            log.info("QUERY LIST MOVIIRED MH: {} milliseconds", watch.getTime());

            while (rs.next()) {

                transaction = new TransactionDTO(rs);

                //Transformacion de data
                this.dataTransformation(transaction, rs);

                listTransaction.add(transaction);
            }
            log.info("Cantidad de transacciones Moviired MH: [" + listTransaction.size() + "]");

        } catch (Exception e) {
            log.error("Error [Exception]:" + e.getMessage());
        } finally {
            UtilitiesHelper.closeConnection(con, ps, rs);
        }

        return listTransaction;
    }

    public List<TransactionDTO> findTransactionDetail(RequestManager requestManager) {

        List<TransactionDTO> listTransaction = new ArrayList<>();
        TransactionDTO transaction;
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement ps = null;
        StopWatch watch = new StopWatch();

        String query = moviiredTransactionsQueryDetail.replace("##SCHEMA##", this.mahindraDbProperties.getSchema());
        query = applyFilters(requestManager, query);
        log.debug(query);

        try {

            con = this.dataSourceMH.getConnection();
            ps = con.prepareStatement(query);

            ps.setString(HistoryConstants.PARAM_1, requestManager.getCreatedBy());
            ps.setString(HistoryConstants.PARAM_2, this.moviiService.getPaymentTypes());
            ps.setString(HistoryConstants.PARAM_3, this.moviiService.getCashInPurposeCategoryCode());
            ps.setString(HistoryConstants.PARAM_4, this.moviiService.getCashInPurposeEntryType());
            ps.setString(HistoryConstants.PARAM_5, this.moviiService.getCashOutPurposeServiceType());
            ps.setString(HistoryConstants.PARAM_6, this.moviiService.getCashOutPurposeCategoryCode());
            ps.setString(HistoryConstants.PARAM_7, this.moviiService.getCashOutPurposeEntryType());
            ps.setString(HistoryConstants.PARAM_8, requestManager.getTransactionId());

            watch.start();
            rs = ps.executeQuery();
            watch.stop();
            log.info("QUERY DETAIL MOVIIRED MH: {} milliseconds", watch.getTime());

            // Procesar los resultados
            while (rs.next()) {

                transaction = new TransactionDTO(rs);

                //Transformacion de data
                this.dataTransformation(transaction, rs);

                listTransaction.add(transaction);
            }
            log.info("Cantidad de transacciones Moviired MH: [" + listTransaction.size() + "]");

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            UtilitiesHelper.closeConnection(con, ps, rs);
        }

        return listTransaction;
    }

    private String applyFilters(RequestManager filters, String query) {

        String sql = query;

        // Filtro service_type
        if (filters.getServiceSubType() != null && !filters.getServiceSubType().isEmpty()) {
            sql = sql.replace("##FILTER_1##", "th.TRANSFER_SUBTYPE = '" + filters.getServiceSubType() + "'");
        } else {
            sql = sql.replace("##FILTER_1##", "th.TRANSFER_SUBTYPE in ( 'RC', 'CONTPRCHS', 'ONLINEBP', 'O2C', 'C2C', 'CASHIN', 'CASHOUT', 'SRBILPAY', 'CINBYAGNT', 'COBYPCODE') ");
        }

        // Filtro para banco bbva y agrario
        if (filters.getServiceSubType() != null && filters.getServiceSubType().equals("O2C")) {
            sql = sql.replace("##FILTER_2##", " AND ( th.REMARKS LIKE '%|8036|%' OR th.REMARKS LIKE '%|8060|%' ) ");
        } else {
            sql = sql.replace("##FILTER_2##", "");
        }

        return sql;
    }

    private Map<String, String> findTransactionConvenio() {

        ResultSet rs = null;
        PreparedStatement ps = null;
        Map<String, String> hashDetail = new HashMap<>();
        StopWatch watch = new StopWatch();

        String query = "SELECT mnc.ean_code, CONCAT(COALESCE(c.name, mnc.operator_name),\"|\", mnc.name) detail FROM movii_nuevos_convenios.ws_operator mnc LEFT JOIN movii_nuevos_convenios.ws_category c ON c.id = mnc.category_id";
        log.debug("SQL Convenio:" + query);

        try {
            ps = this.poolConvenioDB.getConnection().prepareStatement(query);

            watch.start();
            rs = ps.executeQuery();
            watch.stop();

            log.info("QUERY Convenios: {} milliseconds", watch.getTime());

            while (rs.next()) {
                hashDetail.put(rs.getString("ean_code"), rs.getString("detail"));
            }
            log.info("Cantidad de Convenios: [" + hashDetail.size() + "]");

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            UtilitiesHelper.closeConnection(null, ps, rs);
        }

        return hashDetail;
    }

    private void dataTransformation(TransactionDTO transaction, ResultSet rs) throws SQLException {

        String eanCode;
        Map<String, String> services = this.moviiService.getTypes();
        Map<String, String> states = this.moviiService.getStates();
        Map<String, String> managers = this.moviiService.getManagers();

        //Inicio transformacion de data
        if (transaction.getServiceSubType().equals("CONTPRCHS")) {//Contenido digital

            eanCode = rs.getString(HistoryConstants.FLD_ATTR_6_VALUE).split(SEPARATOR)[0];

            if (this.hashMapCategory.containsKey(eanCode)) {
                transaction.setName(this.hashMapCategory.get(eanCode).split(SEPARATOR)[0]);
                transaction.setNameDetail(this.hashMapCategory.get(eanCode).split(SEPARATOR)[1]);
            }
        }

        if (transaction.getServiceSubType().equals("RC")) {//Recargas

            eanCode = rs.getString(HistoryConstants.FLD_ATTR_4_VALUE).split("_")[1];

            if (this.hashMapCategory.containsKey(eanCode)) {
                transaction.setName(this.hashMapCategory.get(eanCode).split(SEPARATOR)[0]);
                transaction.setNameDetail(this.hashMapCategory.get(eanCode).split(SEPARATOR)[1]);
            }
        }

        if ((transaction.getServiceSubType().equals("C2C") && (transaction.getServiceType().equals("LOANREPAY") || transaction.getServiceType().equals("LOANDISB")))
                || (transaction.getServiceSubType().equals("O2C") && transaction.getServiceType().equals("AUTOO2C"))) {
            transaction.setServiceType(services.get(transaction.getServiceType()));
        } else {
            transaction.setServiceType(services.get(transaction.getServiceSubType()));
        }

        if (states.containsKey(transaction.getTransferStatus())) {
            transaction.setTransferStatus(states.get(transaction.getTransferStatus()));
        }

        if (managers.containsKey(transaction.getGestorId())) {
            transaction.setManager(managers.get(transaction.getGestorId()));
        }

        if (managers.containsKey(transaction.getName())) {
            transaction.setName(managers.get(transaction.getName()));
        } else if (transaction.getName().matches(globalParameters.getRegexIgnoreNameTranfer())) {
            transaction.setName(transaction.getServiceSubType());
        }
        //Fin transformacion de data
    }

}


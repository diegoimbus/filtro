package co.moviired.transaction.service;

import co.moviired.base.domain.exception.DataException;
import co.moviired.transaction.conf.client.connectors.mahindra.IMahindraClient;
import co.moviired.transaction.domain.request.Request;
import co.moviired.transaction.domain.request.RequestManager;
import co.moviired.transaction.domain.response.TransactionResponse;
import co.moviired.transaction.helper.UtilitiesHelper;
import co.moviired.transaction.model.dto.TransactionDTO;
import co.moviired.transaction.properties.GlobalParameters;
import co.moviired.transaction.properties.MoviiService;
import co.moviired.transaction.properties.MoviiServiceItem;
import co.moviired.transaction.repository.TransactionNewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@AllArgsConstructor
@Service
public class TransactionService {

    private static final String LBL_SUCCESS_TRANSACTION_MESSAGE = "Transaccion exitosa";

    private final GlobalParameters globalParameters;
    private final MoviiService moviiService;
    private final TransactionNewRepository transactionNewRepository;
    private final IMahindraClient mahindraClient;

    public String ping() {
        log.info("I'm alive!!!");
        return "OK";
    }

    //Lista de los servicios que ofrece Moviired y Movii
    public Mono<TransactionResponse> getServiceTypes(Mono<String> autorization) {

        return autorization.flatMap(request -> {

            List<MoviiServiceItem> list = new ArrayList<>();
            moviiService.getTypes().forEach((key, value) -> list.add(new MoviiServiceItem(key, value)));

            return Mono.just(getSuccessResponse(list));

        });
    }

    //Consulta transacciones MoviiRed
    public Mono<TransactionResponse> getTransactions(@NotNull Mono<RequestManager> pRequestManager, @NotNull String autorization) {

        return pRequestManager
                .flatMap(requestManager -> {

                    log.info("**************Iniciando el servicio getTransactions **************");
                    log.info("Request:[" + requestManager.toString() + "]");

                    return validateAutenticationMH(requestManager, autorization).flatMap(request -> {

                        try {

                            validateFilters(requestManager);

                            List<TransactionDTO> transactions = this.transactionNewRepository.findTransaction(request);
                            TransactionResponse response = getSuccessResponse(transactions);
                            return Mono.just(response);

                        } catch (DataException | Exception e) {
                            log.error(e.getMessage());
                            return Mono.error(e);
                        }

                    });

                }).onErrorResume(e -> Mono.just(getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getMessage()))
                ).doOnTerminate(() -> log.info("**************Finalizando el servicio getTransactions **************"));

    }

    //Detalle transaccion MoviiRed
    public Mono<TransactionResponse> getTransactionDetail(@NotNull String transactionId, @NotNull String authorization, @NotNull String origin) {

        return Mono.just(new RequestManager()).flatMap(requestManager -> {

            log.info("************** Iniciando el servicio getTransactionsDetail **************");

            requestManager.setGetTransactionOrigin(origin);
            requestManager.setTransactionId(transactionId);

            log.info("Request:[" + requestManager.toString() + "]");

            return validateAutenticationMH(requestManager, authorization).flatMap(request -> {

                try {

                    validateFiltersDetail(requestManager);

                    List<TransactionDTO> transactionsDetail = this.transactionNewRepository.findTransactionDetail(request);
                    TransactionResponse response = getSuccessResponse(transactionsDetail);
                    return Mono.just(response);

                } catch (DataException | Exception e) {
                    log.error(e.getMessage());
                    return Mono.error(e);
                }

            });

        }).onErrorResume(e -> Mono.just(getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getMessage()))
        ).doOnTerminate(() -> log.info("**************Finalizando el servicio getTransactionsDetail **************"));

    }

    private void validateFilters(RequestManager filters) throws DataException {

        boolean error = false;

        // Verificar los parámetros de entrada
        if (filters.getGetTransactionOrigin() == null || filters.getGetTransactionOrigin().isEmpty()) {
            throw new DataException("10", "Filter getTransactionOrigin is required!");
        }

        if (filters.getCreatedBy() == null || filters.getCreatedBy().isEmpty()) {
            throw new DataException("10", "Filter createdBy is required!");
        }

        if (filters.getStartDate() == null || filters.getStartDate().isEmpty()) {
            throw new DataException("10", "Filter startDate is required!");
        }

        if (filters.getEndDate() == null || filters.getEndDate().isEmpty()) {
            throw new DataException("10", "Filter endDate is required!");
        }

        if (filters.getPageNumber() == null || filters.getPageNumber().isEmpty()) {
            throw new DataException("10", "Filter pageNumber is required!");
        }

        if (filters.getPageSize() == null || filters.getPageSize().isEmpty()) {
            throw new DataException("10", "Filter pageSize is required!");
        }

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            final LocalDate dateBefore = LocalDate.parse(filters.getStartDate(), dtf);
            final LocalDate dateAfter = LocalDate.parse(filters.getEndDate(), dtf);

            long daysBetween = DAYS.between(dateBefore, dateAfter);

            if (this.globalParameters.getDateFilterValueAllowed() < daysBetween) {
                throw new DataException("10", "El rango de fechas para la consulta por fecha de creación, no es válido, la diferencia en días supera los " + this.globalParameters.getDateFilterValueAllowed() + " permitidos. ");
            }

        } catch (DateTimeParseException e) {
            log.error("Error format date!");
            error = true;
        }

        if (error) {
            throw new DataException("10", "Error format date!");
        }

    }

    private void validateFiltersDetail(RequestManager filters) throws DataException {

        // Verificar los parámetros de entrada
        if (filters.getGetTransactionOrigin() == null || filters.getGetTransactionOrigin().isEmpty()) {
            throw new DataException("10", "Filter getTransactionOrigin is required!");
        }

        if (filters.getTransactionId() == null || filters.getTransactionId().isEmpty()) {
            throw new DataException("10", "Filter transactionId is required!");
        }

    }

    private TransactionResponse getSuccessResponse(Object data) {
        return new TransactionResponse(data, "OK", "00", LBL_SUCCESS_TRANSACTION_MESSAGE);
    }

    private TransactionResponse getErrorResponse(String responseType, String responseCode, String responseMsj) {
        return new TransactionResponse(null, responseType, responseCode, responseMsj);
    }

    private Mono<RequestManager> validateAutenticationMH(RequestManager requestManager, String autorization) {

        try {

            UtilitiesHelper.validateAuthorization(requestManager, autorization);
            Request autenticationRequest = this.createRequestMH(autorization);

            return mahindraClient.sendMahindraRequest(autenticationRequest).flatMap(response -> {

                requestManager.setCreatedBy(response.getUserid());
                return Mono.just(requestManager);

            });

        } catch (IOException | DataException e) {
            log.error(e.getMessage());
            return Mono.error(e);
        }

    }

    private Request createRequestMH(String authorization) {

        String[] items = authorization.split(":");
        Request request = new Request();
        request.setType("AUTHPINREQ");
        request.setProvider("101");
        request.setMsisdn(items[0]);
        request.setMpin(items[1]);
        request.setOtpreq("N");
        request.setIspincheckreq("Y");
        request.setSource("BROWSER");

        return request;
    }


}


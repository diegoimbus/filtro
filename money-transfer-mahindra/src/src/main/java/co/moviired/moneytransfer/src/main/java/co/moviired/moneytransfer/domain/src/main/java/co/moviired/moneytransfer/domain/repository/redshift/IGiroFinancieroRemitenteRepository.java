package co.moviired.moneytransfer.domain.repository.redshift;

import co.moviired.moneytransfer.domain.entity.redshift.GiroFinancieroRemitente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public interface IGiroFinancieroRemitenteRepository extends CrudRepository<GiroFinancieroRemitente, Integer>, Serializable {

    @Query(value = "SELECT COALESCE(SUM(cant_trx_mayor_tope), 0) AS total_giros " +
            "FROM giro_financiero_remitente " +
            "WHERE tipo_documento = ?1 AND numero_documento = ?2 AND celular = ?3 AND fecha between DATEADD(MONTH,(?4*-1),CURRENT_DATE) AND CURRENT_DATE ",
            nativeQuery = true)
    int findCountTop(String tipoDocumento, String numeroDocumento, String celular, int month);

}


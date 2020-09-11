package co.moviired.support.domain.repository.redshift;

import co.moviired.support.domain.entity.redshift.StatementAccounts;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@SuppressWarnings("unchecked")
public interface IStatementAccountsRepository extends CrudRepository<StatementAccounts, String>, Serializable {

    List<StatementAccounts> findByCelular(String celular);

    List<StatementAccounts> findByEstadoCartera(String estado);

    List<StatementAccounts> findByEstadoCarteraAndTipoBloqueoIsNotNullAndTipoBloqueoIsNot(String estado, String valor);
}


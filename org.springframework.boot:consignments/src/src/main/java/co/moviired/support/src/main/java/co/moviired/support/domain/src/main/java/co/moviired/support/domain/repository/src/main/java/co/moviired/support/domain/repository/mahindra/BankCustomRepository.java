package co.moviired.support.domain.repository.mahindra;

import co.moviired.support.domain.dto.BankDetailDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Copyright @2019 MOVIIRED. Todos los derechos reservados.
 *
 * @author Rodolfo.rivas
 * @category Consinments
 */
@SuppressWarnings("unchecked")
@Repository
public class BankCustomRepository implements Serializable {

    private static final long serialVersionUID = -4351094487498311258L;

    @Value("${properties.find-bank-list}")
    private String findBankQuery;

    @Value("${properties.find-bankname-byid}")
    private String findBankNameByIdQuery;

    @PersistenceContext(unitName = "bank")
    private EntityManager entityManager;

    public List<BankDetailDTO> findAll() {
        return entityManager.createNativeQuery(findBankQuery, "return_bankList").getResultList();
    }

    public String getBankNameById(String bankId) {
        return (String) entityManager.createNativeQuery(findBankNameByIdQuery).setParameter("bankId", bankId)
                .getSingleResult();
    }

}


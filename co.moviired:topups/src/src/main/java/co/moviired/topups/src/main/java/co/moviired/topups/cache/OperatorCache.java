package co.moviired.topups.cache;

import co.moviired.topups.model.domain.Operator;
import co.moviired.topups.model.domain.repository.IOperatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */
@Slf4j
@Service
@SuppressWarnings("unused")
public class OperatorCache implements Serializable {

    private static final long serialVersionUID = -7326159704468560673L;

    private static final String NOT_IMPLEMENTED = "Not implemented";

    private final IOperatorRepository operatorRepository;

    public OperatorCache(@NotNull IOperatorRepository operatorRepository) {
        super();
        this.operatorRepository = operatorRepository;
    }

    @Cacheable(cacheNames = "OptByEanCodeAndProdCode", unless = "#result == null")
    public Operator getOperatorByEanCodeAndProductCode(String eanCode, String productCode) {
        return operatorRepository.findByEanCodeAndProductCode(eanCode, productCode);
    }

    @Cacheable(cacheNames = "OptByEanCode", unless = "#result == null")
    public Operator getOperatorByEanCode(String eanCode) {
        return operatorRepository.findByEanCode(eanCode);
    }

    @Cacheable(cacheNames = "OptByProdCodeAndType", unless = "#result == null")
    public Operator getOperatorByProductCodeAndType(String productCode, int type) {
        return operatorRepository.findByProductCodeAndType(productCode, type);
    }

    @CacheEvict(cacheNames = "OptByEanCodeAndProdCode")
    public void releaseGetOperatorByEanCodeAndProductCode(String eanCode, String productCode) {
        log.debug(NOT_IMPLEMENTED);
    }

    @CacheEvict(cacheNames = "OptByEanCodeAndProdCode", allEntries = true)
    public void releaseAllOperatorsCacheByEanCodeAndProductCode() {
        log.debug(NOT_IMPLEMENTED);
    }

    @CacheEvict(cacheNames = "OptByEanCode")
    public void releaseGetOperatorByEanCode(String eanCode) {
        log.debug(NOT_IMPLEMENTED);
    }

    @CacheEvict(cacheNames = "OptByEanCode", allEntries = true)
    public void releaseAllOperatorsCacheByEanCode() {
        log.debug(NOT_IMPLEMENTED);
    }

    @CacheEvict(cacheNames = "OptByProdCodeAndType")
    public void releaseGetOperatorByProductCodeAndType(String productCode, int type) {
        log.debug(NOT_IMPLEMENTED);
    }

    @CacheEvict(cacheNames = "OptByProdCodeAndType", allEntries = true)
    public void releaseAllOperatorsCacheByProductCodeAndType() {
        log.debug(NOT_IMPLEMENTED);
    }

}


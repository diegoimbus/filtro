package co.moviired.topups.cache.controller;

import co.moviired.topups.cache.OperatorCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Slf4j
@RestController
public class CacheAdminController {

    private final OperatorCache operatorCache;

    public CacheAdminController(@NotNull final OperatorCache operatorCache) {
        super();
        this.operatorCache = operatorCache;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorByEanCodeAndProd}")
    public String cleanCacheOperatorByEanCodeAndProductCode(@RequestParam String eanCode, @RequestParam String productCode) {
        operatorCache.releaseGetOperatorByEanCodeAndProductCode(eanCode, productCode);
        String response = String.format("{Cache Erased}{ProductCode=%s}{EanCode=%s}", productCode, eanCode);
        log.debug(response);
        return response;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorAllByEanCodeAndProd}")
    public String cleanCacheOperatorReleaseAllByEanCodeAndProductCode() {
        operatorCache.releaseAllOperatorsCacheByEanCodeAndProductCode();
        String response = "{All Cache Erased By EanCodeAndProductCode}";
        log.debug(response);
        return response;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorByEanCode}")
    public String cleanCacheOperatorByEanCode(@RequestParam String eanCode) {
        operatorCache.releaseGetOperatorByEanCode(eanCode);
        String response = String.format("{Cache Erased}{EanCode=%s}", eanCode);
        log.debug(response);
        return response;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorAllByEanCode}")
    public String cleanCacheOperatorReleaseAllByEanCode() {
        operatorCache.releaseAllOperatorsCacheByEanCode();
        String response = "[All Cache Erased By EanCode]";
        log.debug(response);
        return response;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorByProdAndType}")
    public String cleanCacheOperatorByProductCodeAndType(@RequestParam int type, @RequestParam String productCode) {
        operatorCache.releaseGetOperatorByProductCodeAndType(productCode, type);
        String response = String.format("{Cache Erased}{ProductCode=%s}{EanCode=%d}", productCode, type);
        log.debug(response);
        return response;
    }

    @GetMapping(value = "${spring.application.services.cacheClean.operatorAllByProdAndType}")
    public String cleanCacheOperatorReleaseAllByProductCodeAndType() {
        operatorCache.releaseAllOperatorsCacheByProductCodeAndType();
        String response = "{All Cache Erased By ProductCodeAndType}";
        log.debug(response);
        return response;
    }
}


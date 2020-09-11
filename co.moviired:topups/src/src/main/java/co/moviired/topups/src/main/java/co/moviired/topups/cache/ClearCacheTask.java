package co.moviired.topups.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author carlossaul.ramirez
 * @version 0.0.1
 */

@Slf4j
@Component
@EnableScheduling
public class ClearCacheTask {

    private final CacheManager cacheManager;

    public ClearCacheTask(@NotNull CacheManager cacheManager) {
        super();
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "${spring.application.clear.cache.cron}")
    public void reportCurrentTime() {
        //log.info("Cleaning cache from {}", cacheManager.getCacheNames());
        log.info("Cleaning cache ");
        cacheManager.getCacheNames().parallelStream().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

}


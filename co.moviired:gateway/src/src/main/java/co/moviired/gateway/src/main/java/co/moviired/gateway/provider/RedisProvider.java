package co.moviired.gateway.provider;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Class that contains all operations realted to redis, in this case
 * is used to read and write redis properties one by one
 **/
@Component
public final class RedisProvider implements Serializable {

    private final StringRedisTemplate template;

    /***
     * Used to initialize redistTemplate
     * which contains all operations.
     * */
    public RedisProvider(@NotNull StringRedisTemplate ptemplate) {
        this.template = ptemplate;
    }

    public String getBy(String key) {
        return template.opsForValue().get(key);
    }

    public void add(String key, String value) {
        template.opsForValue().set(key, value);
    }
}


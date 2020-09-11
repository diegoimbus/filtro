package co.moviired.support.conf;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class RedShiftHikariDatasource extends HikariDataSource {

    public RedShiftHikariDatasource(HikariConfig configuration) {
        super(configuration);
    }

}


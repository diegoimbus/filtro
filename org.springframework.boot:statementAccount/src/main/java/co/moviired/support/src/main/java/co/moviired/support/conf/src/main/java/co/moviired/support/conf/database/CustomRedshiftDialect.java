package co.moviired.support.conf.database;

import org.hibernate.dialect.PostgreSQL81Dialect;

public final class CustomRedshiftDialect extends PostgreSQL81Dialect {

    @Override
    public String getQuerySequencesString() {
        return null;
    }
}


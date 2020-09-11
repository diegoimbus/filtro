package co.moviired.transaction.conf.database;

public interface IDatabaseProperties {

    String getDriverDb();

    String getPoolNameDb();

    String getUrlDb();

    String getUserDb();

    String getPassDb();

    Integer getMinimumIdle();

    Integer getMaximumPoolSize();

    Integer getConnectionTimeout();

    Integer getIdleTimeout();

    Integer getMaxLifetime();

    boolean isAutoCommit();

    String getTestQuery();

}


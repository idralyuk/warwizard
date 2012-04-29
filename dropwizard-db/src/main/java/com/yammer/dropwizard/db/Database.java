package com.yammer.dropwizard.db;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yammer.dropwizard.db.args.OptionalArgumentFactory;
import com.yammer.dropwizard.db.logging.LogbackLog;
import com.yammer.dropwizard.lifecycle.Lifecycle;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.jdbi.InstrumentedTimingCollector;
import org.apache.tomcat.dbcp.dbcp.DriverManagerConnectionFactory;
import org.apache.tomcat.dbcp.dbcp.PoolableConnectionFactory;
import org.apache.tomcat.dbcp.dbcp.PoolingDataSource;
import org.apache.tomcat.dbcp.pool.ObjectPool;
import org.apache.tomcat.dbcp.pool.impl.GenericObjectPool;
import org.skife.jdbi.v2.ColonPrefixNamedParamStatementRewriter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

@Singleton
public class Database extends DBI implements Managed {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Database.class);

    private final ObjectPool pool;
    private final String validationQuery;

    @Inject
    public Database(DatabaseConfiguration configuration, Lifecycle lifecycle) throws ClassNotFoundException {
        this(buildPoolAndDataSource(configuration), lifecycle, configuration.getValidationQuery());
    }

    private Database (PoolAndDataSource poolAndDataSource, Lifecycle lifecycle, String validationQuery) {
        super(poolAndDataSource.source);
        lifecycle.addManaged(this);
        this.pool = poolAndDataSource.pool;
        this.validationQuery = validationQuery;
        setSQLLog(new LogbackLog(LOGGER, Level.TRACE));
        setTimingCollector(new InstrumentedTimingCollector(Metrics.defaultRegistry()));
        setStatementRewriter(new NamePrependingStatementRewriter(new ColonPrefixNamedParamStatementRewriter()));
        registerArgumentFactory(new OptionalArgumentFactory());
        registerContainerFactory(new ImmutableListContainerFactory());
    }

    @Override
    public void stop() throws Exception {
        pool.close();
    }

    public void ping() throws SQLException {
        final Handle handle = open();
        try {
            handle.execute(validationQuery);
        } finally {
            handle.close();
        }
    }

    private static class PoolAndDataSource {
        public final ObjectPool pool;
        public final DataSource source;

        private PoolAndDataSource(ObjectPool pool, DataSource source) {
            this.pool = pool;
            this.source = source;
        }
    }

    private static PoolAndDataSource buildPoolAndDataSource(DatabaseConfiguration connectionConfig) throws ClassNotFoundException {
        Class.forName(connectionConfig.getDriverClass());
        ObjectPool pool = buildPool(connectionConfig);
        return new PoolAndDataSource(pool, buildDataSource(connectionConfig, pool));
    }

    private static DataSource buildDataSource(DatabaseConfiguration connectionConfig, ObjectPool pool) {
        final Properties properties = new Properties();
        for (Map.Entry<String, String> property : connectionConfig.getProperties().entrySet()) {
            properties.setProperty(property.getKey(), property.getValue());
        }
        properties.setProperty("user", connectionConfig.getUser());
        properties.setProperty("password", connectionConfig.getPassword());

        final DriverManagerConnectionFactory factory = new DriverManagerConnectionFactory(connectionConfig.getUrl(),
                properties);


        final PoolableConnectionFactory connectionFactory = new PoolableConnectionFactory(factory,
                pool,
                null,
                connectionConfig.getValidationQuery(),
                connectionConfig.isDefaultReadOnly(),
                true);
        connectionFactory.setPool(pool);

        return new PoolingDataSource(pool);
    }

    private static ObjectPool buildPool(DatabaseConfiguration configuration) {
        final GenericObjectPool pool = new GenericObjectPool(null);
        pool.setMaxWait(configuration.getMaxWaitForConnection().toMilliseconds());
        pool.setMinIdle(configuration.getMinSize());
        pool.setMaxActive(configuration.getMaxSize());
        pool.setMaxIdle(configuration.getMaxSize());
        pool.setTestWhileIdle(configuration.isCheckConnectionWhileIdle());
        pool.setTimeBetweenEvictionRunsMillis(configuration.getCheckConnectionHealthWhenIdleFor().toMilliseconds());
        pool.setMinEvictableIdleTimeMillis(configuration.getCloseConnectionIfIdleFor()
                .toMilliseconds());
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        return pool;
    }
}

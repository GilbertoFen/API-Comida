package com.demoapi.apicomida.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaFixRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SchemaFixRunner.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public SchemaFixRunner(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProduct = metaData.getDatabaseProductName();

            fixRefreshTokens(metaData, databaseProduct);
            fixFoods(metaData, databaseProduct);
        } catch (SQLException exception) {
            log.warn("Could not inspect schema metadata for compatibility fixes", exception);
        }
    }

    private void fixRefreshTokens(DatabaseMetaData metaData, String databaseProduct) throws SQLException {
        if (!refreshTokenColumnNeedsResize(metaData)) {
            return;
        }

        if ("PostgreSQL".equalsIgnoreCase(databaseProduct)) {
            jdbcTemplate.execute("ALTER TABLE refresh_tokens ALTER COLUMN token TYPE TEXT");
            log.info("Updated refresh_tokens.token column to TEXT for PostgreSQL");
            return;
        }

        if ("H2".equalsIgnoreCase(databaseProduct)) {
            jdbcTemplate.execute("ALTER TABLE refresh_tokens ALTER COLUMN token CLOB");
            log.info("Updated refresh_tokens.token column to CLOB for H2");
        }
    }

    private void fixFoods(DatabaseMetaData metaData, String databaseProduct) throws SQLException {
        ensureColumn(metaData, "foods", "user_id", columnSql(databaseProduct, "UUID"));
        ensureColumn(metaData, "foods", "external_id", columnSql(databaseProduct, "VARCHAR(255)"));
        ensureColumn(metaData, "foods", "raw_data", columnSql(databaseProduct, "TEXT"));
        ensureColumn(metaData, "foods", "verified", columnSql(databaseProduct, "BOOLEAN DEFAULT FALSE"));

        if (hasColumn(metaData, "foods", "verified")) {
            jdbcTemplate.execute("UPDATE foods SET verified = FALSE WHERE verified IS NULL");
            jdbcTemplate.execute("ALTER TABLE foods ALTER COLUMN verified SET NOT NULL");
        }
    }

    private boolean refreshTokenColumnNeedsResize(DatabaseMetaData metaData) throws SQLException {
        try (ResultSet columns = metaData.getColumns(null, null, "refresh_tokens", "token")) {
            if (!columns.next()) {
                return false;
            }

            int size = columns.getInt("COLUMN_SIZE");
            String typeName = columns.getString("TYPE_NAME");
            return size > 0 && size <= 255 && !"TEXT".equalsIgnoreCase(typeName) && !"CLOB".equalsIgnoreCase(typeName);
        }
    }

    private void ensureColumn(DatabaseMetaData metaData, String tableName, String columnName, String columnDefinition)
            throws SQLException {
        if (hasColumn(metaData, tableName, columnName)) {
            return;
        }

        jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN IF NOT EXISTS " + columnName + " " + columnDefinition);
        log.info("Added missing column {}.{} with definition {}", tableName, columnName, columnDefinition);
    }

    private boolean hasColumn(DatabaseMetaData metaData, String tableName, String columnName) throws SQLException {
        try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
            return columns.next();
        }
    }

    private String columnSql(String databaseProduct, String genericDefinition) {
        if ("H2".equalsIgnoreCase(databaseProduct) && "UUID".equalsIgnoreCase(genericDefinition)) {
            return "UUID";
        }
        return genericDefinition;
    }
}

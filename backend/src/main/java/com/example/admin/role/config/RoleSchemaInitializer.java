package com.example.admin.role.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repairs the learning H2 role table when an old local database file is reused.
 *
 * We added permission_codes after sys_role already existed on your computer.
 * A fresh database has this column automatically, but the old H2 file may not.
 */
@Component
@Order(2)
public class RoleSchemaInitializer implements CommandLineRunner {

    /**
     * JdbcTemplate runs direct SQL.
     *
     * Schema repair is table-level work, so direct SQL is simpler than JPA entities here.
     */
    private final JdbcTemplate jdbcTemplate;

    public RoleSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!tableExists()) {
            return;
        }

        addVarcharColumnIfMissing("permission_codes", 200);
    }

    /**
     * Adds one varchar column only when it does not already exist.
     *
     * Running this method many times is safe because it checks metadata first.
     */
    private void addVarcharColumnIfMissing(String columnName, int length) {
        if (!columnExists(columnName)) {
            jdbcTemplate.execute("alter table sys_role add column " + columnName + " varchar(" + length + ")");
        }
    }

    /**
     * Checks whether the role table already exists.
     *
     * On a fresh database Hibernate can create the table, so this repair class
     * only touches old databases where sys_role is already present.
     */
    private boolean tableExists() {
        return Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) ->
                hasTable(connection, "sys_role") || hasTable(connection, "SYS_ROLE")));
    }

    /**
     * Checks whether one column already exists.
     *
     * H2 can store identifiers in different cases, so we check lower and upper case.
     */
    private boolean columnExists(String columnName) {
        return Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) ->
                hasColumn(connection, columnName) || hasColumn(connection, columnName.toUpperCase())));
    }

    /**
     * Reads the database table list from JDBC metadata.
     */
    private boolean hasTable(Connection connection, String tableName) throws SQLException {
        try (ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        }
    }

    /**
     * Reads the role table column list from JDBC metadata.
     */
    private boolean hasColumn(Connection connection, String columnName) throws SQLException {
        try (ResultSet columns = connection.getMetaData().getColumns(null, null, "sys_role", columnName)) {
            if (columns.next()) {
                return true;
            }
        }

        try (ResultSet columns = connection.getMetaData().getColumns(null, null, "SYS_ROLE", columnName)) {
            return columns.next();
        }
    }
}

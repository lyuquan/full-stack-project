package com.example.admin.user.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repairs the learning H2 database schema when an old local database file is reused.
 *
 * We added createdAt and updatedAt after the sys_user table already existed on your computer.
 * A fresh database has these columns automatically, but the old H2 file may not.
 * This initializer keeps the old data and only adds the missing columns.
 */
@Component
@Order(1)
public class UserSchemaInitializer implements CommandLineRunner {

    /**
     * JdbcTemplate is Spring's helper for running direct SQL.
     * We use it here because this task is about changing table structure, not querying users as objects.
     */
    private final JdbcTemplate jdbcTemplate;

    public UserSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        addTimestampColumnIfMissing("created_at");
        addTimestampColumnIfMissing("updated_at");

        /*
         * Old rows did not have create/update time values.
         * After adding the columns, fill empty values so UserEntity can read non-null times.
         */
        jdbcTemplate.update("update sys_user set created_at = current_timestamp where created_at is null");
        jdbcTemplate.update("update sys_user set updated_at = current_timestamp where updated_at is null");
    }

    /**
     * Adds one timestamp column only when it does not already exist.
     * This makes the startup code safe to run many times.
     */
    private void addTimestampColumnIfMissing(String columnName) {
        if (!columnExists(columnName)) {
            jdbcTemplate.execute("alter table sys_user add column " + columnName + " timestamp");
        }
    }

    /**
     * Checks database metadata instead of guessing from error text.
     *
     * H2 can store names in upper or lower case depending on configuration, so we check both forms.
     */
    private boolean columnExists(String columnName) {
        return Boolean.TRUE.equals(jdbcTemplate.execute((Connection connection) ->
                hasColumn(connection, columnName) || hasColumn(connection, columnName.toUpperCase())));
    }

    /**
     * Reads the table column list from JDBC metadata.
     */
    private boolean hasColumn(Connection connection, String columnName) throws SQLException {
        try (ResultSet columns = connection.getMetaData().getColumns(null, null, "sys_user", columnName)) {
            if (columns.next()) {
                return true;
            }
        }

        try (ResultSet columns = connection.getMetaData().getColumns(null, null, "SYS_USER", columnName)) {
            return columns.next();
        }
    }
}

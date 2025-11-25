package com.example.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for querying CSV data using DuckDB
 */
public class DuckDBService {
    private static final Logger logger = LoggerFactory.getLogger(DuckDBService.class);
    private Connection connection;

    public DuckDBService() throws SQLException {
        // Create in-memory DuckDB database
        this.connection = DriverManager.getConnection("jdbc:duckdb:");
        logger.info("DuckDB connection established");
    }

    /**
     * Load CSV file from local path into DuckDB
     */
    public void loadCsvFromFile(String tableName, String filePath) throws SQLException {
        String sql = String.format(
            "CREATE TABLE %s AS SELECT * FROM read_csv_auto('%s')",
            tableName,
            filePath.replace("\\", "/")
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Loaded CSV file {} into table {}", filePath, tableName);
        }
    }

    /**
     * Load CSV data from InputStream
     */
    public void loadCsvFromStream(String tableName, InputStream inputStream, String[] headers) throws Exception {
        // Save stream to temporary file
        File tempFile = File.createTempFile("duckdb_", ".csv");
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        loadCsvFromFile(tableName, tempFile.getAbsolutePath());
    }

    /**
     * Execute SELECT query and return results as list of maps
     */
    public List<String[]> executeQuery(String query) throws SQLException {
        List<String[]> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            // Add header row
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = metadata.getColumnName(i);
            }
            results.add(headers);

            // Add data rows
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                results.add(row);
            }

            logger.info("Query executed successfully, returned {} rows", results.size() - 1);
        }

        return results;
    }

    /**
     * Execute query and export results to CSV file
     */
    public void executeQueryToCSV(String query, String outputPath) throws Exception {
        List<String[]> results = executeQuery(query);

        try (FileWriter writer = new FileWriter(outputPath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            for (String[] row : results) {
                csvPrinter.printRecord((Object[]) row);
            }

            logger.info("Query results exported to: {}", outputPath);
        }
    }

    /**
     * Get table information
     */
    public void showTableInfo(String tableName) throws SQLException {
        String query = "DESCRIBE " + tableName;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== Table Info: " + tableName + " ===");
            while (rs.next()) {
                String columnName = rs.getString("column_name");
                String columnType = rs.getString("column_type");
                System.out.printf("Column: %s, Type: %s%n", columnName, columnType);
            }
        }
    }

    /**
     * List all tables in the database
     */
    public List<String> listTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        String query = "SHOW TABLES";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }

        return tables;
    }

    /**
     * Drop table if exists
     */
    public void dropTable(String tableName) throws SQLException {
        String sql = "DROP TABLE IF EXISTS " + tableName;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Dropped table: {}", tableName);
        }
    }

    /**
     * Execute aggregate query
     */
    public void executeAggregateQuery(String tableName, String groupByColumn, String aggregateColumn) throws SQLException {
        String query = String.format(
            "SELECT %s, COUNT(*) as count, AVG(%s) as avg_value FROM %s GROUP BY %s ORDER BY count DESC",
            groupByColumn, aggregateColumn, tableName, groupByColumn
        );

        List<String[]> results = executeQuery(query);

        System.out.println("\n=== Aggregate Query Results ===");
        for (String[] row : results) {
            System.out.println(String.join(" | ", row));
        }
    }

    /**
     * Join multiple tables
     */
    public List<String[]> joinTables(String table1, String table2, String joinColumn) throws SQLException {
        String query = String.format(
            "SELECT * FROM %s JOIN %s ON %s.%s = %s.%s",
            table1, table2, table1, joinColumn, table2, joinColumn
        );

        return executeQuery(query);
    }

    /**
     * Execute custom SQL (for CREATE, INSERT, UPDATE, DELETE)
     */
    public void executeUpdate(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            logger.info("Executed SQL: {}", sql);
        }
    }

    /**
     * Print query results to console
     */
    public void printQueryResults(String query) throws SQLException {
        List<String[]> results = executeQuery(query);

        System.out.println("\n=== Query Results ===");
        System.out.println("Query: " + query);
        System.out.println();

        for (int i = 0; i < results.size(); i++) {
            String[] row = results.get(i);
            if (i == 0) {
                System.out.println(String.join(" | ", row));
                System.out.println("-".repeat(80));
            } else {
                System.out.println(String.join(" | ", row));
            }
        }
        System.out.println("\nTotal rows: " + (results.size() - 1));
    }

    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("DuckDB connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
}


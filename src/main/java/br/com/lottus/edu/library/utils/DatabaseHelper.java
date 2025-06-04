package br.com.lottus.edu.library.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class DatabaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class); // Adicionado logger

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DatabaseHelper(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public String getDatabaseProductName() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            if (productName != null) {
                return productName.toLowerCase();
            }
            logger.warn("Não foi possível obter o nome do produto do banco de dados a partir dos metadados.");
            return "unknown"; // Retornar um valor padrão ou lançar uma exceção mais específica
        } catch (SQLException e) {
            logger.error("Erro ao obter o nome do produto do banco de dados: {}", e.getMessage(), e);
            // Retornar a mensagem de erro como string não é ideal para tratamento de erro.
            // Melhor seria logar e retornar um valor padrão ou relançar uma exceção customizada.
            return "error_retrieving_db_product_name";
        }
    }

    public void disableForeignKeyChecks(String databaseProductName) {
        try {
            if ("mysql".equals(databaseProductName)) { // Comparação mais segura
                logger.info("Desabilitando verificação de chaves estrangeiras para MySQL.");
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            } else if ("h2".equals(databaseProductName)) {
                logger.info("Desabilitando verificação de chaves estrangeiras para H2.");
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE;");
            }
            // Adicione lógica para outros bancos se necessário
        } catch (Exception e) {
            // Usar logger em vez de System.out.println
            logger.warn("Falha ao tentar desabilitar verificação de chaves estrangeiras para '{}': {}", databaseProductName, e.getMessage());
        }
    }

    public void enableForeignKeyChecks(String databaseProductName) {
        try {
            if ("mysql".equals(databaseProductName)) {
                logger.info("Reabilitando verificação de chaves estrangeiras para MySQL.");
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            } else if ("h2".equals(databaseProductName)) {
                logger.info("Reabilitando verificação de chaves estrangeiras para H2.");
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE;");
            }
            // Adicione lógica para outros bancos se necessário
        } catch (Exception e) {
            logger.warn("Falha ao tentar reabilitar verificação de chaves estrangeiras para '{}': {}", databaseProductName, e.getMessage());
        }
    }

    // Renomeado para getTruncateTableSql e com lógica para diferentes BDs
    public String getTruncateTableSql(String tableName, String databaseProductName) {
        // Adicionar aspas/crases é uma boa prática para nomes de tabelas, especialmente se tiverem caracteres especiais
        // ou forem palavras reservadas (embora 'tableName' aqui deva ser simples).
        String quotedTableName = databaseProductName.equals("mysql") ? "`" + tableName + "`" : "\"" + tableName + "\"";


        if (databaseProductName.contains("postgresql") || databaseProductName.contains("h2")) {
            return "TRUNCATE TABLE " + quotedTableName + " RESTART IDENTITY CASCADE;";
        } else if (databaseProductName.contains("mysql")) {
            // MySQL não tem CASCADE no TRUNCATE padrão. FKs devem ser desabilitadas separadamente.
            return "TRUNCATE TABLE " + quotedTableName + ";";
        } else if (databaseProductName.contains("sql server")) {
            return "TRUNCATE TABLE " + quotedTableName + ";"; // SQL Server também não usa CASCADE no TRUNCATE diretamente
        } else {
            logger.warn("Usando comando TRUNCATE genérico para banco de dados '{}': {}", databaseProductName, tableName);
            return "TRUNCATE TABLE " + tableName + ";"; // Fallback genérico
        }
    }
} // A chave de fechamento da classe DatabaseHelper estava faltando no seu exemplo original para os métodos.
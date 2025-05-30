package br.com.lottus.edu.library.controller;

import br.com.lottus.edu.library.utils.DatabaseHelper;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.fasterxml.jackson.databind.cfg.CoercionInputShape.Array;

@RestController
@RequestMapping("/admin/db")
@Profile("test")
public class AdmController {


    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final Environment environment;
    private final DatabaseHelper databaseHelper;

    @Autowired
    public AdmController(JdbcTemplate jdbcTemplate, DataSource dataSource, Environment environment, DatabaseHelper databaseHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.environment = environment;
        this.databaseHelper = databaseHelper;
    }

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    private static final List<String> TABLES_TO_CLEAN = Arrays.asList(

            "agendamento",
            "aluno",
            "turma",
            "livro",
            "categoria"
    );



    private boolean isSafeToCleanup() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            return false;
        }

        String dbUrl = datasourceUrl.toLowerCase();

        if (!dbUrl.contains("localhost") || !dbUrl.contains("/testdb")) {
            return false;
        }

        return true;
    }

    @Transactional
    @PostMapping("/cleanup/all-data")
    public ResponseEntity<String> cleanuppAllTests() {
        if (!isSafeToCleanup()) {
            return ResponseEntity.status(403).body("Limpeza de dados bloqueada por razões de segurança");
        }

        String databaseProductName = databaseHelper.getDatabaseProductName();

        databaseHelper.disableForeignKeyChecks(databaseProductName);


        for (String tableName : TABLES_TO_CLEAN) {
            try {
                String truncateSql = databaseHelper.getTruncateTableSql(tableName, databaseProductName);
                jdbcTemplate.execute(truncateSql);

            } catch (Exception e) {
                databaseHelper.enableForeignKeyChecks(databaseProductName);
                return ResponseEntity.status(500).body("Erro ao limpar tabela '" + tableName + "'. Limpeza parcial pode ter ocorrido. Causa: " + e.getMessage());
            }
        }

        databaseHelper.enableForeignKeyChecks(databaseProductName);
        return ResponseEntity.ok("Todas as tabelas de teste permitidas foram limpas.");
    }
}

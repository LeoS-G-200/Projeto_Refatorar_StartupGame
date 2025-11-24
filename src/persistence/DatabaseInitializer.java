package persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() {
        System.out.println("[DB] Verificando esquema do banco de dados...");
        
        try (Connection conn = DataSourceProvider.get();
             Statement stmt = conn.createStatement()) {
            
            // Lê o arquivo schema.sql dos resources
            String sql = lerSchemaSql();
            
            // Executa o script
            stmt.execute(sql);
            
            System.out.println("[DB] Tabelas verificadas/criadas com sucesso.");
            
        } catch (Exception e) {
            System.err.println("[DB] ❌ Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Abortar o programa se o banco não subir
            // System.exit(1); 
        }
    }

    private static String lerSchemaSql() {
        try (InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo resources/schema.sql não encontrado!");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler schema.sql", e);
        }
    }
}
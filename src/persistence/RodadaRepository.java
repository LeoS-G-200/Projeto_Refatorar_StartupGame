package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RodadaRepository {

    /**
     * Registra o início ou fim de uma rodada.
     * @param startupId O UUID da startup (retornado pelo StartupRepository).
     * @param numeroRodada O número da rodada atual (1 a 8).
     * @return O ID gerado (Primary Key) da tabela rodada, para vincular as decisões.
     */
    public long registrarRodada(String startupId, int numeroRodada) {
        String sql = "INSERT INTO rodada (startup_id, numero_rodada) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.get();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, startupId);
            stmt.setInt(2, numeroRodada);
            
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Falha ao obter ID da rodada.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar Rodada", e);
        }
    }
}
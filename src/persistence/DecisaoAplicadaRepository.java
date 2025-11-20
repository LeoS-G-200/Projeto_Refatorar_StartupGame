package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DecisaoAplicadaRepository {

    /**
     * Salva uma decisão tomada pelo jogador.
     * @param rodadaId O ID da rodada (retornado pelo RodadaRepository).
     * @param tipoDecisao A String que identifica a decisão (ex: "MARKETING").
     */
    public void salvar(long rodadaId, String tipoDecisao) {
        String sql = "INSERT INTO decisao_aplicada (rodada_id, tipo_decisao) VALUES (?, ?)";

        try (Connection conn = DataSourceProvider.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rodadaId);
            stmt.setString(2, tipoDecisao);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar Decisão Aplicada", e);
        }
    }
}
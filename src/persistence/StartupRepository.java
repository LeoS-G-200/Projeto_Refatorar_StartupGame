package persistence; 

import model.Startup;
import model.vo.Dinheiro;
import model.vo.Humor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StartupRepository {

    /**
     * Salva o estado atual da Startup no banco de dados.
     * @param startup O objeto startup com os dados atuais.
     * @return O ID (UUID) gerado para este registro no banco.
     */
    public String salvar(Startup startup) {
        String sql = "INSERT INTO startup (id, nome, caixa, receita_base, reputacao, moral, rodada_atual) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String id = UUID.randomUUID().toString();

        try (Connection conn = DataSourceProvider.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, startup.getNome());
            stmt.setDouble(3, startup.getCaixa().valor());
            stmt.setDouble(4, startup.getReceitaBase().valor());
            stmt.setInt(5, startup.getReputacao().valor());
            stmt.setInt(6, startup.getMoral().valor());
            stmt.setInt(7, startup.getRodadaAtual());

            stmt.executeUpdate();
            return id;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar Startup", e);
        }
    }

    /**
     * Busca uma startup pelo ID (útil para carregar jogo).
     */
    public Startup carregar(String id) {
        String sql = "SELECT * FROM startup WHERE id = ?";
        
        try (Connection conn = DataSourceProvider.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    double caixa = rs.getDouble("caixa");
                    double receita = rs.getDouble("receita_base");
                    int rep = rs.getInt("reputacao");
                    int moral = rs.getInt("moral");
                    int rodada = rs.getInt("rodada_atual");

                    Startup s = new Startup(
                        nome, 
                        new Dinheiro(caixa), 
                        new Dinheiro(receita), 
                        new Humor(rep), 
                        new Humor(moral)
                    );
                    s.setRodadaAtual(rodada);
                    return s;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar Startup", e);
        }
        return null;
    }
/**
     * Atualiza os dados de uma startup existente no banco.
     * @param id O UUID da startup.
     * @param startup O objeto com os dados atualizados.
     */
    public void atualizar(String id, Startup startup) {
        String sql = "UPDATE startup SET caixa=?, receita_base=?, reputacao=?, moral=?, rodada_atual=? WHERE id=?";

        try (Connection conn = DataSourceProvider.get();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, startup.getCaixa().valor());
            stmt.setDouble(2, startup.getReceitaBase().valor());
            stmt.setInt(3, startup.getReputacao().valor());
            stmt.setInt(4, startup.getMoral().valor());
            stmt.setInt(5, startup.getRodadaAtual());
            stmt.setString(6, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar Startup", e);
        }
    }
}
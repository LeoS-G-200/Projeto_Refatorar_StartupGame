package observers;

import engine.GameObserver;
import model.Startup;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Observador responsável por recolher estatísticas e gerar um relatório CSV ao fim do jogo.
 */
public class MatchStatsObserver implements GameObserver {

    // Armazena as linhas em memória até o final do jogo
    private final List<String> linhasCSV = new ArrayList<>();

    public MatchStatsObserver() {
        // Define o cabeçalho do CSV (separado por ponto e vírgula para Excel em PT)
        linhasCSV.add("Rodada;Caixa;Reputacao;Moral;Faturamento;Decisoes");
    }

    @Override
    public void onJogoIniciado(Startup startup) {
        // Opcional: Poderíamos adicionar uma linha de cabeçalho com nome da empresa
        System.out.println("[Relatorio] Monitorização iniciada para exportação CSV.");
    }

    @Override
    public void onRodadaFinalizada(int numeroRodada, Startup startup, double faturamento, List<String> decisoes) {
        // Formata os dados da rodada numa linha CSV
        // Substituímos o ponto por vírgula nos decimais se o sistema for PT, 
        // mas aqui forçamos o padrão US (.) para garantir compatibilidade geral ou usamos Locale se preferir.
        // O formato abaixo usa String.format padrão.
        
        String decisoesFormatadas = String.join(" | ", decisoes);
        
        String linha = String.format("%d;%.2f;%d;%d;%.2f;%s",
                numeroRodada,
                startup.getCaixa().valor(),
                startup.getReputacao().valor(),
                startup.getMoral().valor(),
                faturamento,
                decisoesFormatadas
        );
        
        // Adiciona à lista
        linhasCSV.add(linha);
    }

    @Override
    public void onFimDeJogo(Startup startup, String motivo) {
        salvarArquivoCsv(startup.getNome());
    }

    private void salvarArquivoCsv(String nomeStartup) {
        // Sanitiza o nome do ficheiro (remove espaços e caracteres estranhos)
        String nomeArquivo = "relatorio_" + nomeStartup.replaceAll("[^a-zA-Z0-9]", "") + ".csv";

        try (FileWriter fileWriter = new FileWriter(nomeArquivo);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            for (String linha : linhasCSV) {
                printWriter.println(linha);
            }

            System.out.println("\n[Relatorio] ✅ Relatório CSV exportado com sucesso: " + nomeArquivo);
            System.out.println("[Relatorio] Caminho: " + System.getProperty("user.dir") + "/" + nomeArquivo);

        } catch (IOException e) {
            System.err.println("[Relatorio] ❌ Erro ao salvar relatório CSV: " + e.getMessage());
        }
    }
}
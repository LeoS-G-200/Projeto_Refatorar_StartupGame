package engine;

import model.Startup;

public class ScoreService {
    
    public static String calcularRelatorioFinal(Startup startup) {
        double score = startup.scoreFinal();
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== RELATÓRIO FINAL ===\n");
        sb.append("Startup: ").append(startup.getNome()).append("\n");
        sb.append("Caixa Final: ").append(startup.getCaixa()).append("\n");
        sb.append("Reputação: ").append(startup.getReputacao().valor()).append("\n");
        sb.append("Moral: ").append(startup.getMoral().valor()).append("\n");
        sb.append("-----------------------\n");
        sb.append(String.format("SCORE FINAL: %.2f pontos\n", score));
        
        if (score > 80) sb.append("Classificação: UNICÓRNIO! 🦄\n");
        else if (score > 50) sb.append("Classificação: Promissora 🚀\n");
        else sb.append("Classificação: Precisa melhorar 📉\n");
        
        return sb.toString();
    }
}
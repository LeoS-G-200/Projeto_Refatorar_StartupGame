package observers;

import engine.GameObserver;
import model.Startup;
import java.util.List;

public class EventLoggerObserver implements GameObserver {
    @Override
    public void onJogoIniciado(Startup startup) {
        System.out.println("\n[LOG] Jogo iniciado para a startup: " + startup.getNome());
    }

    @Override
    public void onRodadaFinalizada(int numeroRodada, Startup startup, double faturamento, List<String> decisoes) {
        System.out.println("[LOG] Rodada " + numeroRodada + " finalizada.");
        System.out.println("[LOG] Decisões tomadas: " + decisoes);
        System.out.printf("[LOG] Faturamento gerado: R$ %.2f\n", faturamento);
    }

    @Override
    public void onFimDeJogo(Startup startup, String motivo) {
        System.out.println("\n[LOG] O jogo acabou! Motivo: " + motivo);
    }
}
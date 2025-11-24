package engine;

import model.Startup;
import java.util.List;

public interface GameObserver {
    void onJogoIniciado(Startup startup);
    void onRodadaFinalizada(int numeroRodada, Startup startup, double faturamento, List<String> decisoes);
    void onFimDeJogo(Startup startup, String motivo);
}
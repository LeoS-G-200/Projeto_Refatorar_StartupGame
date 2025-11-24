package engine;

import actions.DecisaoFactory;
import actions.DecisaoStrategy;
import config.Config;
import model.Deltas;
import model.Startup;
import model.vo.Dinheiro;
import model.vo.Humor;
import persistence.DecisaoAplicadaRepository;
import persistence.RodadaRepository;
import persistence.StartupRepository;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    // ... atributos existentes ...
    private final Config config;
    private Startup startup;
    private String startupId;
    private boolean gameOver = false;
    private String statusMessage = "";

    // Repositories existentes...
    private final StartupRepository startupRepo = new StartupRepository();
    private final RodadaRepository rodadaRepo = new RodadaRepository();
    private final DecisaoAplicadaRepository decisaoRepo = new DecisaoAplicadaRepository();

    // === NOVO: Lista de Observadores ===
    private final List<GameObserver> observers = new ArrayList<>();

    public GameEngine() {
        this.config = new Config();
    }

    // === NOVO: Método para adicionar observadores ===
    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    public void iniciarNovoJogo(String nomeEmpresa) {
        // ... (código existente de criação da startup) ...
        this.startup = new Startup(
            nomeEmpresa,
            new Dinheiro(10000), 
            new Dinheiro(5000),  
            new Humor(50),       
            new Humor(50)        
        );
        this.startupId = startupRepo.salvar(this.startup);
        this.gameOver = false;

        // === NOVO: Notificar início ===
        notificarInicioJogo();
    }

    public void processarRodada(List<String> nomesDecisoes) {
        if (gameOver) return;

        // ... (validações existentes) ...
        if (nomesDecisoes.size() > config.maxDecisoesPorRodada()) {
            throw new IllegalArgumentException("Muitas decisões! Máximo permitido: " + config.maxDecisoesPorRodada());
        }

        int rodadaAtual = startup.getRodadaAtual();
        long rodadaId = rodadaRepo.registrarRodada(startupId, rodadaAtual);
        List<String> logRodada = new ArrayList<>();

        // ... (loop de decisões existente) ...
        for (String nomeDecisao : nomesDecisoes) {
            try {
                DecisaoStrategy strategy = DecisaoFactory.criar(nomeDecisao);
                Deltas d = strategy.aplicar(startup);
                aplicarDeltas(d);
                decisaoRepo.salvar(rodadaId, nomeDecisao);
                logRodada.add(String.format("[%s] %s", nomeDecisao, formatarDeltas(d)));
            } catch (IllegalArgumentException e) {
                statusMessage = "ERRO: " + e.getMessage();
                gameOver = true;
                // === NOVO: Notificar Game Over por erro ===
                notificarFimDeJogo("Falência/Erro: " + e.getMessage());
                return;
            }
        }

        // ... (finalização de rodada existente) ...
        double receitaEntrada = startup.receitaRodada();
        startup.setCaixa(startup.getCaixa().somar(new Dinheiro(receitaEntrada)));
        
        logRodada.add(String.format("Faturamento da rodada: R$ %.2f", receitaEntrada));
        logRodada.forEach(startup::registrar);
        
        // === NOVO: Notificar fim da rodada ANTES de incrementar ===
        // Passamos uma cópia da lista de decisões para o observer saber o que houve
        notificarRodadaFinalizada(rodadaAtual, receitaEntrada, new ArrayList<>(nomesDecisoes));

        // Prepara próxima rodada
        startup.setRodadaAtual(rodadaAtual + 1);
        startupRepo.atualizar(startupId, startup);

        verificarFimDeJogo();
    }

    private void verificarFimDeJogo() {
        if (startup.getRodadaAtual() > config.totalRodadas()) {
            gameOver = true;
            statusMessage = "Fim de jogo! Rodadas finalizadas.";
            // === NOVO: Notificar Fim de Jogo ===
            notificarFimDeJogo("Rodadas finalizadas com sucesso!");
        }
    }

    // === NOVOS: Métodos auxiliares de notificação ===
    private void notificarInicioJogo() {
        for (GameObserver obs : observers) {
            obs.onJogoIniciado(startup);
        }
    }

    private void notificarRodadaFinalizada(int rodada, double faturamento, List<String> decisoes) {
        for (GameObserver obs : observers) {
            obs.onRodadaFinalizada(rodada, startup, faturamento, decisoes);
        }
    }

    private void notificarFimDeJogo(String motivo) {
        for (GameObserver obs : observers) {
            obs.onFimDeJogo(startup, motivo);
        }
    }

    // ... (restante dos métodos privados e getters existentes: aplicarDeltas, etc) ...
    private void aplicarDeltas(Deltas d) {
        // Atualiza Caixa (pode lançar exceção se ficar negativo)
        double novoCaixa = startup.getCaixa().valor() + d.caixaDelta();
        startup.setCaixa(new Dinheiro(novoCaixa));

        // Atualiza Humor (VO já trata limites 0-100)
        if (d.reputacaoDelta() > 0) startup.setReputacao(startup.getReputacao().aumentar(d.reputacaoDelta()));
        else startup.setReputacao(startup.getReputacao().reduzir(Math.abs(d.reputacaoDelta())));

        if (d.moralDelta() > 0) startup.setMoral(startup.getMoral().aumentar(d.moralDelta()));
        else startup.setMoral(startup.getMoral().reduzir(Math.abs(d.moralDelta())));

        // Atualiza Bônus de Receita para próxima rodada
        startup.addBonusPercentReceitaProx(d.bonusDelta());
    }

    private String formatarDeltas(Deltas d) {
        return String.format("Caixa: %+.0f, Rep: %+d, Moral: %+d", 
                d.caixaDelta(), d.reputacaoDelta(), d.moralDelta());
    }

    public Startup getStartup() { return startup; }
    public boolean isGameOver() { return gameOver; }
    public String getStatusMessage() { return statusMessage; }
}
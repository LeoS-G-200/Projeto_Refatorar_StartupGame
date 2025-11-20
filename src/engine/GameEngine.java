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
    private final Config config;
    private Startup startup;
    private String startupId; // ID do banco de dados
    private boolean gameOver = false;
    private String statusMessage = "";

    // Repositories
    private final StartupRepository startupRepo = new StartupRepository();
    private final RodadaRepository rodadaRepo = new RodadaRepository();
    private final DecisaoAplicadaRepository decisaoRepo = new DecisaoAplicadaRepository();

    public GameEngine() {
        this.config = new Config();
    }

    /**
     * Inicia um novo jogo criando uma Startup do zero.
     */
    public void iniciarNovoJogo(String nomeEmpresa) {
        // Valores iniciais padrão
        this.startup = new Startup(
            nomeEmpresa,
            new Dinheiro(10000), // R$ 10k iniciais
            new Dinheiro(5000),  // R$ 5k receita base
            new Humor(50),       // 50% Reputação
            new Humor(50)        // 50% Moral
        );
        
        // Salva no banco e recupera o ID gerado
        this.startupId = startupRepo.salvar(this.startup);
        this.gameOver = false;
    }

    /**
     * Processa uma rodada completa com base nas decisões do jogador.
     * @param nomesDecisoes Lista de strings (ex: "MARKETING", "EQUIPE")
     */
    public void processarRodada(List<String> nomesDecisoes) {
        if (gameOver) return;

        // 1. Validação
        if (nomesDecisoes.size() > config.maxDecisoesPorRodada()) {
            throw new IllegalArgumentException("Muitas decisões! Máximo permitido: " + config.maxDecisoesPorRodada());
        }

        int rodadaAtual = startup.getRodadaAtual();

        // 2. Registrar a Rodada no Banco
        long rodadaId = rodadaRepo.registrarRodada(startupId, rodadaAtual);

        // 3. Aplicar Decisões (Strategy Pattern)
        List<String> logRodada = new ArrayList<>();
        
        for (String nomeDecisao : nomesDecisoes) {
            try {
                // Factory cria a estratégia correta
                DecisaoStrategy strategy = DecisaoFactory.criar(nomeDecisao);
                
                // Aplica e recebe os Deltas (mudanças)
                Deltas d = strategy.aplicar(startup);
                
                // Atualiza o Modelo (Startup)
                aplicarDeltas(d);

                // Persistência e Log
                decisaoRepo.salvar(rodadaId, nomeDecisao);
                logRodada.add(String.format("[%s] %s", nomeDecisao, formatarDeltas(d)));

            } catch (IllegalArgumentException e) {
                // Se faltar dinheiro (Dinheiro VO joga exceção se negativo)
                statusMessage = "ERRO: " + e.getMessage();
                gameOver = true;
                return;
            }
        }

        // 4. Finalizar Rodada (Receita + Updates)
        double receitaEntrada = startup.receitaRodada(); // Já aplica bônus e reseta percentual
        startup.setCaixa(startup.getCaixa().somar(new Dinheiro(receitaEntrada)));
        
        logRodada.add(String.format("Faturamento da rodada: R$ %.2f", receitaEntrada));
        
        // Atualiza histórico interno da classe Startup
        logRodada.forEach(startup::registrar);

        // Prepara próxima rodada
        startup.setRodadaAtual(rodadaAtual + 1);
        
        // 5. Salvar Estado Atualizado no Banco
        startupRepo.atualizar(startupId, startup);

        // 6. Checar Fim de Jogo
        verificarFimDeJogo();
    }

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

    private void verificarFimDeJogo() {
        if (startup.getRodadaAtual() > config.totalRodadas()) {
            gameOver = true;
            statusMessage = "Fim de jogo! Rodadas finalizadas.";
        }
    }
    
    private String formatarDeltas(Deltas d) {
        return String.format("Caixa: %+.0f, Rep: %+d, Moral: %+d", 
                d.caixaDelta(), d.reputacaoDelta(), d.moralDelta());
    }

    // Getters para a UI
    public Startup getStartup() { return startup; }
    public boolean isGameOver() { return gameOver; }
    public String getStatusMessage() { return statusMessage; }
    public int getMaxDecisoes() { return config.maxDecisoesPorRodada(); }
}
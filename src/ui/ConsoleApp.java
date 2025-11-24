package ui;

import config.Config;
import engine.GameEngine;
import engine.ScoreService;
import model.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final GameEngine engine = new GameEngine();
    private final Config config = new Config();

    public void start() {
        System.out.println("========================================");
        System.out.println("      🚀 STARTUP GAME - CONSOLE 🚀      ");
        System.out.println("========================================");
        
        boolean executando = true;
        while (executando) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Novo Jogo");
            System.out.println("2. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1" -> iniciarNovoJogo();
                case "2" -> {
                    executando = false;
                    System.out.println("Saindo... Até a próxima!");
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private void iniciarNovoJogo() {
        System.out.print("\nDigite o nome da sua Startup: ");
        String nome = scanner.nextLine();
        
        engine.addObserver(new observers.EventLoggerObserver());
        engine.addObserver(new observers.MatchStatsObserver());

        // Inicializa a engine
        engine.iniciarNovoJogo(nome);
        
        System.out.println("\nStartup " + nome + " criada com sucesso!");
        System.out.println("O jogo tem " + config.totalRodadas() + " rodadas.");
        System.out.println("Você pode tomar até " + config.maxDecisoesPorRodada() + " decisões por rodada.");
        
        loopDoJogo();
    }

    private void loopDoJogo() {
        while (!engine.isGameOver()) {
            exibirStatusRodada();
            List<String> decisoes = lerDecisoesDoJogador();
            
            System.out.println("\nProcessando rodada...");
            engine.processarRodada(decisoes);
            
            // Exibe mensagens de feedback da engine (erros ou status)
            if (!engine.getStatusMessage().isEmpty()) {
                System.out.println("⚠️ " + engine.getStatusMessage());
            }
        }
        
        exibirFimDeJogo();
    }

    private void exibirStatusRodada() {
        Startup s = engine.getStartup();
        System.out.println("\n========================================");
        System.out.println(" RODADA ATUAL: " + s.getRodadaAtual() + " / " + config.totalRodadas());
        System.out.println("========================================");
        System.out.println(s); // Usa o toString() da Startup
        System.out.println("----------------------------------------");
        System.out.println("Decisões Disponíveis:");
        System.out.println(" [MARKETING]     - Custo alto, aumenta Receita e Reputação");
        System.out.println(" [EQUIPE]        - Custo médio, aumenta muito Reputação");
        System.out.println(" [PRODUTO]       - Custo médio, aumenta Receita");
        System.out.println(" [INVESTIDORES]  - Ganha muito Caixa (com risco de perder Reputação)");
        System.out.println(" [CORTAR_CUSTOS] - Ganha pouco Caixa (perde Moral)");
        System.out.println("----------------------------------------");
    }

    private List<String> lerDecisoesDoJogador() {
        List<String> decisoes = new ArrayList<>();
        int max = config.maxDecisoesPorRodada();

        System.out.println("Digite até " + max + " decisões separadas por vírgula (ou ENTER para pular):");
        System.out.print("> ");
        
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) {
            return decisoes;
        }

        String[] partes = input.split(",");
        for (String p : partes) {
            if (decisoes.size() < max) {
                decisoes.add(p.trim().toUpperCase()); // Converte para maiúsculo para bater com a Factory
            }
        }
        return decisoes;
    }

    private void exibirFimDeJogo() {
        System.out.println("\n🏁 FIM DE JOGO 🏁");
        String relatorio = ScoreService.calcularRelatorioFinal(engine.getStartup());
        System.out.println(relatorio);
        
        System.out.println("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }
}
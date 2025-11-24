import persistence.DatabaseInitializer;
import ui.ConsoleApp;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializa o Banco de Dados (Cria tabelas se não existirem)
        DatabaseInitializer.initialize();

        // 2. Inicia a Aplicação
        new ConsoleApp().start();
    }
}
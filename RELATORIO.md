# Relatório de Implementação - Startup Game

## Integrantes do Grupo
* Leonardo Siqueira Gonçalves
* Bruno Turim Meneguetti

---

## 1. Itens Obrigatórios Implementados

O projeto foi refatorado seguindo estritamente os princípios de Orientação a Objetos e boas práticas de arquitetura. Abaixo, detalham-se as implementações obrigatórias:

### 1.1. Arquitetura e Modularização
O código monolítico original foi separado em pacotes com responsabilidades definidas (SRP - Single Responsibility Principle):
* `model` e `model.vo`: Classes de domínio e Value Objects (`Startup`, `Dinheiro`, `Humor`, etc.).
* `actions`: Implementação do padrão **Strategy** para as decisões do jogo.
* `persistence`: Camada de acesso a dados (DAO/Repository) e configuração do banco H2.
* `engine`: Lógica de negócio principal (`GameEngine`) e pontuação (`ScoreService`).
* `ui`: Interação com o utilizador via consola (`ConsoleApp`).
* `config`: Leitura de configurações externas.

### 1.2. Persistência de Dados (H2)
* Foi utilizado o banco de dados **H2** em modo ficheiro (`./data/game`).
* Implementados repositórios para salvar e recuperar o estado da **Startup**, histórico de **Rodadas** e **Decisões**.
* Criada a classe `DatabaseInitializer` para executar automaticamente o script `schema.sql` na primeira execução, garantindo que as tabelas existam.

### 1.3. Configuração Externa
* O sistema lê as configurações do ficheiro `resources/game.properties`.
* Parâmetros configuráveis: `total.rodadas` e `max.decisoes.por.rodada`.

### 1.4. Padrões de Projeto (Obrigatórios)
* **Strategy:** Utilizado para as decisões de jogo (ex: `MarketingStrategy`, `InvestidoresStrategy`), permitindo adicionar novas decisões sem alterar a engine.
* **Factory:** `DecisaoFactory` para instanciar as estratégias com base na string de entrada.
* **Value Objects (VO):** Uso de `Dinheiro`, `Humor` e `Percentual` para encapsular regras de negócio e validações.

---

## 2. Itens Opcionais Escolhidos

Foram implementados **2 itens opcionais** conforme o enunciado:

### Opção A: Padrão Observer
* **Objetivo:** Desacoplar a lógica do jogo (`GameEngine`) da interface e dos sistemas de log.
* **Implementação:** Interface `GameObserver` criada no pacote `engine`.
* **Classes:**
    * `EventLoggerObserver`: Imprime logs detalhados no console em tempo real.
    * `MatchStatsObserver`: Coleta dados silenciosamente para geração de relatórios.

### Opção B: Relatórios Avançados (Exportação CSV)
* **Objetivo:** Permitir a análise de dados da partida em ferramentas externas (Excel/Sheets).
* **Implementação:** O `MatchStatsObserver` acumula o histórico de cada rodada e, ao final do jogo (`onFimDeJogo`), exporta um ficheiro `.csv`.
* **Resultado:** Gera um arquivo `relatorio_[NomeStartup].csv` na raiz do projeto com colunas para Rodada, Caixa, Reputação, Moral e Faturamento.

---

## 3. Como Rodar o Projeto

### Pré-requisitos
* JDK instalado.
* Arquivo do driver H2 (`h2.jar`) na pasta `lib/`.

### Compilação e Execução (Terminal)
Certifique-se de estar na raiz do projeto.

**Windows:**
```powershell
# Compilar
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Executar (atenção às aspas no classpath)
java -cp "out;resources;lib/h2.jar" Main
# Startup Game - Refatoração POO Avançado

## 📝 Sobre o Projeto
Este projeto consiste na refatoração completa de um jogo de simulação de startup, aplicando princípios avançados de **Programação Orientada a Objetos (POO)** e **Padrões de Projeto**.

O objetivo foi transformar um código monolítico em uma arquitetura em camadas, testável e extensível, utilizando Java puro e banco de dados H2.

### 🚀 Funcionalidades do Jogo
* **Simulação de Turnos:** O jogador gerencia uma startup ao longo de 8 rodadas.
* **Tomada de Decisões:** Escolha entre Marketing, Equipe, Produto, Investidores, etc.
* **Gestão de Recursos:** Controle de Caixa, Reputação e Moral.
* **Persistência Automática:** Todo o progresso é salvo automaticamente em banco de dados (H2).
* **Relatórios (Nova Feature):** Exportação automática de histórico da partida para CSV ao final do jogo.

---

## 🛠️ Arquitetura e Padrões Implementados

### 1. Arquitetura em Camadas
O código foi organizado para separar responsabilidades (SRP):
* **`model`**: Regras de negócio e Value Objects (`Dinheiro`, `Humor`, `Percentual`).
* **`actions`**: Lógica das decisões (Pattern Strategy).
* **`persistence`**: Acesso a dados (DAO/Repository) e inicialização do banco.
* **`engine`**: Motor do jogo e gerenciamento de estado (`GameEngine`).
* **`ui`**: Interface de console (`ConsoleApp`).
* **`observers`**: Monitoramento de eventos e logs.
* **`config`**: Leitura de configurações externas (`game.properties`).

### 2. Padrões de Projeto (Design Patterns)
* **Strategy:** Utilizado para implementar as diferentes decisões (`MarketingStrategy`, `EquipeStrategy`, etc.), permitindo adicionar novas opções sem alterar a engine.
* **Observer:** Utilizado para desacoplar a interface da lógica de logs e relatórios (`GameObserver`, `MatchStatsObserver`).
* **Factory:** Criação dinâmica das estratégias de decisão (`DecisaoFactory`).
* **Singleton/Static:** Gerenciamento único da conexão com o banco de dados (`DataSourceProvider`).

---

## ⚙️ Pré-requisitos e Configuração
Para rodar este projeto, você precisará de:
1. **Java JDK** (Versão 17 ou superior recomendada).
2. **Driver do Banco H2**.

### 📥 Instalação da Dependência (H2)
1. Crie uma pasta chamada `lib` na raiz do projeto (no mesmo nível de `src`).
2. Baixe o arquivo `.jar` do banco de dados H2 (ex: do site oficial ou repositório Maven).
3. **Importante:** Renomeie o arquivo baixado para **`h2.jar`** e coloque-o dentro da pasta `lib`.

---

## ▶️ Como Rodar o Projeto

Abra o terminal na **raiz do projeto** (a pasta que contém `src`, `resources` e `lib`) e execute os comandos abaixo conforme seu sistema operacional.

### Windows (PowerShell ou CMD)



---

---

## Pré-requisitos (Biblioteca)
Para executar o projeto, é necessário adicionar o driver do banco de dados:
1. Crie uma pasta chamada `lib` na raiz do projeto.
2. Baixe o arquivo `.jar` do H2 Database e renomeie-o para **`h2.jar`**.
3. Coloque o arquivo em `lib/h2.jar`.

---

## Como Rodar (console)
Compile e execute o `Main` **incluindo `resources` e `lib` no classpath**:

```bash
# Compilar (Linux/Mac)
javac -d out $(find src -name "*.java")

# Compilar (Windows PowerShell)
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Executar (Linux/Mac)
java -cp out:resources:lib/h2.jar Main

# Executar (Windows PowerShell)
java -cp "out;resources;lib/h2.jar" Main

---

## Configurações
O arquivo `resources/game.properties` já vem configurado com:
- `total.rodadas=8`
- `max.decisoes.por.rodada=3`

---

## Banco de Dados
- **H2 (arquivo)**: URL padrão `jdbc:h2:file:./data/game;AUTO_SERVER=TRUE` (ver `DataSourceProvider`).
- Execute o SQL de `resources/schema.sql` na inicialização para criar as tabelas necessárias.

---

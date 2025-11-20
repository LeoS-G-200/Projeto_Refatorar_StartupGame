-- T-- Criação da tabela STARTUP para salvar o estado do jogo
CREATE TABLE IF NOT EXISTS startup (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    caixa DOUBLE NOT NULL,        -- Representa model.vo.Dinheiro
    receita_base DOUBLE NOT NULL, -- Representa model.vo.Dinheiro
    reputacao INT NOT NULL,       -- Representa model.vo.Humor (0-100)
    moral INT NOT NULL,           -- Representa model.vo.Humor (0-100)
    rodada_atual INT NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela RODADA para histórico
-- Vincula uma startup a um número de rodada específico
CREATE TABLE IF NOT EXISTS rodada (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    startup_id UUID NOT NULL,
    numero_rodada INT NOT NULL,
    faturamento_nesta_rodada DOUBLE DEFAULT 0.0, -- Opcional: para histórico financeiro
    FOREIGN KEY (startup_id) REFERENCES startup(id)
);

-- Criação da tabela DECISAO_APLICADA
-- Registra quais estratégias (Marketing, Equipe, etc) foram escolhidas em cada rodada
CREATE TABLE IF NOT EXISTS decisao_aplicada (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rodada_id BIGINT NOT NULL,
    tipo_decisao VARCHAR(50) NOT NULL, -- Ex: "MARKETING", "CORTAR_CUSTOS"
    FOREIGN KEY (rodada_id) REFERENCES rodada(id)
);
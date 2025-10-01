-- Tabela de Produtos
CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ex: 'Cookie Normal', 'Cookie Adicional', 'Bebida', 'Resgatavel'
    estoque INTEGER NOT NULL DEFAULT 0,
    custo_pontos INTEGER DEFAULT 0 -- 0 para produtos normais, > 0 para resgatáveis
);

-- Tabela de Clientes
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    pontos_acumulados INTEGER NOT NULL DEFAULT 0
);

-- Tabela de Vendas
CREATE TABLE venda (
    id SERIAL PRIMARY KEY,
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id INTEGER REFERENCES cliente(id), -- Nullable, se a venda for anônima
    valor_total NUMERIC(10, 2) NOT NULL,
    pontos_gerados INTEGER NOT NULL DEFAULT 0,
    tipo_transacao VARCHAR(20) NOT NULL -- 'VENDA' ou 'RESGATE'
);

-- Tabela de Itens da Venda (Para múltiplos produtos na venda)
CREATE TABLE item_venda (
    id SERIAL PRIMARY KEY,
    venda_id INTEGER REFERENCES venda(id) ON DELETE CASCADE,
    produto_id INTEGER REFERENCES produto(id),
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL
);
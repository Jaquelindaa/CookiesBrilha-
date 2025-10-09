-- Tabela de Produtos
CREATE TABLE produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ex: 'Cookie Normal', 'Cookie Adicional', 'Bebida', 'Resgatavel'
    estoque INT NOT NULL DEFAULT 0,
    custo_pontos INTEGER DEFAULT 0 -- 0 para produtos normais, > 0 para resgatáveis
);

-- Tabela de Clientes
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(15),
    pontos_acumulados INTEGER NOT NULL DEFAULT 0
);

-- Tabela de Vendas
CREATE TABLE venda (
    id SERIAL PRIMARY KEY,
    data_venda TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id INTEGER REFERENCES cliente(id), -- Nullable, se a venda for anônima
    valor_total DECIMAL(10, 2) NOT NULL,
    pontos_gerados INTEGER NOT NULL DEFAULT 0,
    tipo_transacao VARCHAR(10) NOT NULL, -- 'VENDA' ou 'RESGATE'
	FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- Tabela de Itens da Venda (Para múltiplos produtos na venda)
CREATE TABLE item_venda (
    id SERIAL PRIMARY KEY,
    venda_id INTEGER REFERENCES venda(id) ON DELETE CASCADE,
    produto_id INTEGER REFERENCES produto(id),
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
	FOREIGN KEY (venda_id) REFERENCES venda(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);
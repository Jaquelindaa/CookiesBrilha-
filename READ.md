# SISTEMA DE GESTÃO DE PADARIA - COOKIES BRILHA

## FUNCIONALIDADE
1.  **Gestão de Produtos:** CRUD completo, com tipo de produto e estoque.
2.  **Gestão de Clientes:** CRUD, com registro de pontos acumulados.
3.  **Gestão de Vendas:** Registro de vendas, cálculo de pontos e atualização de estoque.
4.  **Troca de Pontos:** Resgate de produtos "resgatáveis" com base no saldo de pontos.

## REQUISITOS TÉCNICOS
* **Linguagem:** Java
* **Banco de Dados:** PostgreSQL
* **Arquitetura:** MVC (Model-View-Controller)
* **Interface:** Swing (NetBeans JForms)
* **IDE:** NetBeans

## ESTRUTURA DO PROJETO
| Pacote | Responsabilidade |
| :--- | :--- |
| `model` | Classes de domínio (Produto, Cliente, Venda, ItemVenda) |
| `dao` | Classes para interação com o PostgreSQL (CRUD) |
| `view` | Telas (JFrames/JInternalFrames) e componentes Swing |
| `controller` | Lógica de negócio e coordenação entre View e Model/DAO |
| `util` | Classes de utilidade (Ex: Gerenciador de Conexão com DB) |

## INSTRIÇÕES DE EXECUÇÃO
1.  **Configuração do Banco de Dados:**
    * Crie o banco de dados `cookiesbrilha` no PostgreSQL.
    * Execute o script de criação de tabelas (localizado em `/sql/schema.sql`).
2.  **Dependências (JDBC):**
    * Adicione o driver JDBC do PostgreSQL (`postgresql-*.jar`) ao classpath do projeto.
3.  **Execução:**
    * Abra o projeto no NetBeans e execute a classe principal (main).
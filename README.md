# SISTEMA DE GESTÃO DE PADARIA - COOKIES BRILHA

Este projeto implementa um sistema de venda e gestão para uma padaria fictícia, incluindo cadastro de produtos, clientes, registro de vendas e um sistema básico de fidelidade por pontos.

## FUNCIONALIDADES

1.  **Gestão de Produtos:** CRUD (Criar, Ler, Atualizar, Deletar)
2.  **Gestão de Clientes:** CRUD (Criar, Ler, Atualizar, Deletar)
3.  **Registro de Vendas:** Permite registrar vendas associadas a um cliente
4.  **Sistema de Pontos:**
    * Calcula pontos ganhos com base no valor da venda (10 pontos por real gasto, configurável no `VendaController`).
    * Permite o resgate de produtos específicos utilizando os pontos acumulados pelo cliente.
5.  **Relatórios:** Exibe um histórico de todas as transações (vendas e resgates).

## REQUISITOS TÉCNICOS

* **Linguagem:** Java
* **Banco de Dados:** PostgreSQL
* **Interface Gráfica:** Java Swing
* **IDE:** Apache NetBeans
* **Bibliotecas Externas:**
    * PostgreSQL JDBC Driver [Instale - JDBC.jar](https://mvnrepository.com/artifact/org.postgresql/postgresql/42.7.7)
    * FlatLaf Look and Feel [Instale - FlatLaf.jar](https://mvnrepository.com/artifact/com.formdev/flatlaf/3.6.1)

## ESTRUTURA DO PROJETO

O projeto segue uma arquitetura baseada no padrão MVC (Model-View-Controller):

| Pacote     | Responsabilidade                                                                                                                               |
| :--------- | :--------------------------------------------------------------------------------------------------------------------------------------------- |
| `model`    | Classes de domínio: `Produto`, `Cliente`, `Venda`, `ItemVenda`. Representam os dados.                                                 |
| `dao`      | Data Access Objects: Classes responsáveis pela interação direta com o banco de dados PostgreSQL (CRUD) |
| `view`     | Telas da interface gráfica        |
| `controller` | Controladores: Classes que contêm a lógica de negócio, validam dados e fazem a mediação entre a `View` e o `DAO`/`Model`|
| `util`     | Classes utilitárias: Ex: `ConectionFactory` para gerenciar a conexão com o banco de dados.                                                    |
| `main`     | Classe principal (`Main.java`) que inicializa a aplicação e a tela principal.                                                                 |
| `sql`      | Contém o script `schema.sql` para criação das tabelas no banco de dados.                                                                       |

## CONFIGURAÇÃO E EXECUÇÃO (Passo a Passo)

Siga estas etapas para configurar e executar o projeto em sua máquina:

### 1. Pré-requisitos
* **Java Development Kit (JDK):** 
* **PostgreSQL:** 
* **Apache NetBeans IDE:**
* **Git:**

### 2. Configurações dos arquivos
Nessa mesma página, clique em `<>Code` e depois em `Download ZIP`
1.  **Extrair arquivos:** no seu gerenciador, estrai os arquivos do pacote zipado
2.  **Abrir NetBeans:** no netbeans abra o projeto, selecionando a pasta que você extraiu
2.  **Add Libraries:** na pasta `Libraries` com o botão direito `Add JAR/Folder` e adicione os dois arquivos `JDBC.jar` e `FlatLaf.jar`

### 3. Configuração do Banco de Dados
* **Crie o Banco:** 
* **Execute o Script:** Conecte-se ao banco `cookiesbrilha` e execute o script `schema.sql` (localizado na pasta `sql` do projeto)
* **Verifique as Credenciais:** O projeto está configurado para conectar-se usando:
    * **URL:** `jdbc:postgresql://localhost:5432/cookiesbrilha`
    * **Usuário:** `postgres`
    * **Senha:** `123456`
    Se sua configuração do PostgreSQL for diferente (porta, nome de usuário ou senha), ajuste os valores no arquivo `src/util/ConectionFactory.java`.

### 4. Executar o Projeto
**Iniciar aplicação:** Execute o arquivos `\src\main\Main.java`.

## DIAGRAMA DE CLASSES
O diagrama também está disponível em `\CookiesBrilha\Diagramas`
![Diagrama de Classes]("/Diagramas/DiagramaClasse.png")

## DIAGRAMA DE CASOS DE USO
O diagrama também está disponível em `\CookiesBrilha\Diagramas`
![Diagrama de Classes]("/Diagramas/DiagramaCasosDeUso.png")
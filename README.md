# Projeto Biblioteca

Sistema de gerenciamento de biblioteca desenvolvido em Java, com persistência de dados em **PostgreSQL**. O projeto permite organizar o acervo, os usuários e os empréstimos de uma biblioteca.

---

## Funcionalidades

- [ ] Cadastro, consulta, atualização e remoção de livros
- [ ] Cadastro e gerenciamento de usuários/leitores
- [ ] Registro de empréstimos e devoluções

---

## Tecnologias

| Tecnologia | Uso |
|---|---|
| **Java 25** | Linguagem principal |
| **Maven** | Gerenciamento de dependências e build |
| **PostgreSQL** | Banco de dados relacional |
| **PostgreSQL JDBC Driver** (`42.7.14`) | Conexão da aplicação com o banco |
| **java-dotenv** (`5.2.2`) | Leitura de variáveis de ambiente a partir de um arquivo `.env` |

---

## Estrutura do projeto

```
Projeto_Biblioteca/
├── sql/                
├── src/
│   └── main/
│       └── java/       
├── .gitignore
└── pom.xml              
```

---

## Pré-requisitos

Antes de começar, você precisa ter instalado:

- [JDK 25](https://jdk.java.net/) (ou superior)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [PostgreSQL](https://www.postgresql.org/download/)
- Git

---

## Instalação e configuração

### 1. Clone o repositório

```bash
git clone https://github.com/R-SNeto/Projeto_Biblioteca.git
cd Projeto_Biblioteca
```

### 2. Crie o banco de dados

No PostgreSQL, crie um banco para o projeto:

```sql
CREATE DATABASE biblioteca;
```

Depois, execute os scripts da pasta `sql/` no banco criado:

```bash
psql -U <seu_usuario> -d biblioteca -f sql/<nome_do_script>.sql
```

### 3. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com as credenciais de acesso ao banco:

```env
DB_URL=jdbc:postgresql://localhost:5432/biblioteca
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

### 4. Instale as dependências

```bash
mvn clean install
```

---

## Como executar

Compile e execute a aplicação com o Maven:

```bash
mvn compile exec:java -Dexec.mainClass="<pacote.ClassePrincipal>"
```

Ou, pela sua IDE (IntelliJ IDEA, Eclipse, VS Code), abra o projeto como um projeto Maven e execute a classe principal que contém o método `main`.

---

## Banco de dados

Os scripts SQL estão na pasta [`sql/`](./sql). Eles contêm a estrutura das tabelas usadas pela aplicação.

Descreva aqui o modelo de dados, por exemplo:

| Tabela | Descrição |
|---|---|
| `livros` | Acervo da biblioteca |
| `usuarios` | Leitores cadastrados |
| `emprestimos` | Registro de empréstimos e devoluções |

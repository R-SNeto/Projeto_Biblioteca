import dao.AutorDAO;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Autor;
import model.Livro;
import model.Usuario;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner in = new Scanner(System.in);

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private static final AutorDAO autorDAO = new AutorDAO();
    private static final LivroDAO livroDAO = new LivroDAO();

    static void main() {
            try {
                if (usuarioDAO.contar() == 0) {
                    usuarioDAO.inserir("Administrador", "admin", "admin123");
                    System.out.println("Usuário padrão criado -> login: admin | senha: admin123");
                }
            } catch (SQLException e) {
                System.out.println("Erro ao conectar no banco: " + e.getMessage());
                System.out.println("Verifique se o PostgreSQL está rodando e se o schema.sql foi executado.");
                return;
            }

            Usuario logado = login();
            if (logado == null) {
                System.out.println("Número máximo de tentativas excedido.");
                return;
            }
            System.out.println("\nBem-vindo(a), " + logado.nome() + "!");
            menuPrincipal();
            System.out.println("Até logo!");
        }

        private static Usuario login() {
            System.out.println("===== BIBLIOTECA - LOGIN =====");
            for (int tentativa = 1; tentativa <= 3; tentativa++) {
                String login = lerTexto("Login: ");
                String senha = lerTexto("Senha: ");
                try {
                    Usuario u = usuarioDAO.autenticar(login, senha);
                    if (u != null) return u;
                    System.out.println("Login ou senha inválidos (" + tentativa + "/3).");
                } catch (SQLException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
            return null;
        }

        private static void menuPrincipal() {
            int op;
            do {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1 - Livros");
                System.out.println("2 - Autores");
                System.out.println("3 - Usuários do sistema");
                System.out.println("4 - Relatório: livros por autor");
                System.out.println("0 - Sair");
                op = lerInt("Opção: ");
                switch (op) {
                    case 1 -> menuLivros();
                    case 2 -> menuAutores();
                    case 3 -> menuUsuarios();
                    case 4 -> executar(() -> autorDAO.relatorioLivrosPorAutor().forEach(System.out::println));
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        private static void menuLivros() {
            int op;
            do {
                System.out.println("\n--- LIVROS ---");
                System.out.println("1 - Cadastrar");
                System.out.println("2 - Listar todos");
                System.out.println("3 - Filtrar");
                System.out.println("4 - Atualizar");
                System.out.println("5 - Excluir");
                System.out.println("6 - Emprestar / Devolver");
                System.out.println("0 - Voltar");
                op = lerInt("Opção: ");
                switch (op) {
                    case 1 -> cadastrarLivro();
                    case 2 -> executar(() -> imprimirLivros(livroDAO.listar()));
                    case 3 -> filtrarLivros();
                    case 4 -> atualizarLivro();
                    case 5 -> excluirLivro();
                    case 6 -> alterarDisponibilidade();
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        private static void cadastrarLivro() {
            executar(() -> {
                Autor autor = escolherAutor();
                if (autor == null) return;
                String titulo = lerTexto("Título: ");
                String isbn = lerTexto("ISBN (opcional): ");
                int ano = lerInt("Ano de publicação: ");
                livroDAO.inserir(titulo, isbn, ano, true, autor.id());
                System.out.println("Livro cadastrado com sucesso!");
            });
        }

        private static void filtrarLivros() {
            executar(() -> {
                System.out.println("Deixe em branco os filtros que não quiser usar.");
                String titulo = lerTexto("Título contém: ");
                String autor = lerTexto("Autor contém: ");
                Integer anoMin = lerIntOpcional("Ano mínimo: ");
                Integer anoMax = lerIntOpcional("Ano máximo: ");
                String disp = lerTexto("Disponível? (s/n/vazio=todos): ");
                Boolean disponivel = disp.equalsIgnoreCase("s") ? Boolean.TRUE
                        : disp.equalsIgnoreCase("n") ? Boolean.FALSE : null;
                imprimirLivros(livroDAO.filtrar(titulo, autor, anoMin, anoMax, disponivel));
            });
        }

        private static void atualizarLivro() {
            executar(() -> {
                Livro atual = livroDAO.buscarPorId(lerInt("ID do livro: "));
                if (atual == null) { System.out.println("Livro não encontrado."); return; }

                System.out.println("(Enter mantém o valor atual)");
                String titulo = lerComPadrao("Título [" + atual.titulo() + "]: ", atual.titulo());
                String isbn = lerComPadrao("ISBN [" + nvl(atual.isbn()) + "]: ", atual.isbn());
                Integer ano = lerIntOpcional("Ano [" + atual.ano() + "]: ");
                int autorId = atual.autorId();
                if (lerTexto("Trocar autor? (s/n): ").equalsIgnoreCase("s")) {
                    Autor a = escolherAutor();
                    if (a != null) autorId = a.id();
                }
                livroDAO.atualizar(atual.id(), titulo, isbn, ano != null ? ano : atual.ano(),
                        atual.disponivel(), autorId);
                System.out.println("Livro atualizado!");
            });
        }

        private static void excluirLivro() {
            executar(() -> {
                int id = lerInt("ID do livro: ");
                System.out.println(livroDAO.excluir(id) ? "Livro excluído." : "Livro não encontrado.");
            });
        }

        private static void alterarDisponibilidade() {
            executar(() -> {
                Livro l = livroDAO.buscarPorId(lerInt("ID do livro: "));
                if (l == null) { System.out.println("Livro não encontrado."); return; }
                livroDAO.alterarDisponibilidade(l.id(), !l.disponivel());
                System.out.println(l.disponivel() ? "Livro emprestado." : "Livro devolvido.");
            });
        }

        private static void imprimirLivros(List<Livro> livros) {
            if (livros.isEmpty()) { System.out.println("Nenhum livro encontrado."); return; }
            System.out.printf("%-4s %-35s %-25s %-6s %-20s %s%n", "ID", "TÍTULO", "AUTOR", "ANO", "ISBN", "STATUS");
            for (Livro l : livros) {
                System.out.printf("%-4d %-35s %-25s %-6d %-20s %s%n", l.id(), cortar(l.titulo(), 35),
                        cortar(l.autorNome(), 25), l.ano(), nvl(l.isbn()),
                        l.disponivel() ? "Disponível" : "Emprestado");
            }
        }

        private static void menuAutores() {
            int op;
            do {
                System.out.println("\n--- AUTORES ---");
                System.out.println("1 - Cadastrar");
                System.out.println("2 - Listar todos");
                System.out.println("3 - Filtrar por nome");
                System.out.println("4 - Atualizar");
                System.out.println("5 - Excluir");
                System.out.println("0 - Voltar");
                op = lerInt("Opção: ");
                switch (op) {
                    case 1 -> executar(() -> {
                        autorDAO.inserir(lerTexto("Nome: "), lerTexto("Nacionalidade: "));
                        System.out.println("Autor cadastrado!");
                    });
                    case 2 -> executar(() -> imprimirAutores(autorDAO.listar()));
                    case 3 -> executar(() -> imprimirAutores(autorDAO.filtrarPorNome(lerTexto("Nome contém: "))));
                    case 4 -> executar(() -> {
                        Autor a = autorDAO.buscarPorId(lerInt("ID do autor: "));
                        if (a == null) { System.out.println("Autor não encontrado."); return; }
                        String nome = lerComPadrao("Nome [" + a.nome() + "]: ", a.nome());
                        String nac = lerComPadrao("Nacionalidade [" + nvl(a.nacionalidade()) + "]: ", a.nacionalidade());
                        autorDAO.atualizar(a.id(), nome, nac);
                        System.out.println("Autor atualizado!");
                    });
                    case 5 -> executar(() -> {
                        int id = lerInt("ID do autor: ");
                        System.out.println(autorDAO.excluir(id) ? "Autor excluído." : "Autor não encontrado.");
                    });
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        private static void imprimirAutores(List<Autor> autores) {
            if (autores.isEmpty()) { System.out.println("Nenhum autor encontrado."); return; }
            System.out.printf("%-4s %-35s %s%n", "ID", "NOME", "NACIONALIDADE");
            autores.forEach(a -> System.out.printf("%-4d %-35s %s%n", a.id(), a.nome(), nvl(a.nacionalidade())));
        }

        private static Autor escolherAutor() throws SQLException {
            List<Autor> autores = autorDAO.listar();
            if (autores.isEmpty()) {
                System.out.println("Cadastre um autor antes.");
                return null;
            }
            imprimirAutores(autores);
            Autor a = autorDAO.buscarPorId(lerInt("ID do autor: "));
            if (a == null) System.out.println("Autor não encontrado.");
            return a;
        }

        private static void menuUsuarios() {
            int op;
            do {
                System.out.println("\n--- USUÁRIOS ---");
                System.out.println("1 - Cadastrar");
                System.out.println("2 - Listar");
                System.out.println("3 - Excluir");
                System.out.println("0 - Voltar");
                op = lerInt("Opção: ");
                switch (op) {
                    case 1 -> executar(() -> {
                        usuarioDAO.inserir(lerTexto("Nome: "), lerTexto("Login: "), lerTexto("Senha: "));
                        System.out.println("Usuário cadastrado!");
                    });
                    case 2 -> executar(() -> usuarioDAO.listar().forEach(u ->
                            System.out.printf("%-4d %-30s %s%n", u.id(), u.nome(), u.login())));
                    case 3 -> executar(() -> {
                        int id = lerInt("ID do usuário: ");
                        System.out.println(usuarioDAO.excluir(id) ? "Usuário excluído." : "Usuário não encontrado.");
                    });
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        @FunctionalInterface
        private interface Acao { void run() throws SQLException; }

        /** Executa uma ação tratando erros de SQL com mensagens amigáveis. */
        private static void executar(Acao acao) {
            try {
                acao.run();
            } catch (SQLException e) {
                switch (String.valueOf(e.getSQLState())) {
                    case "23503" -> System.out.println("Operação bloqueada: existem registros vinculados (chave estrangeira).");
                    case "23505" -> System.out.println("Registro duplicado (valor único já existe).");
                    case "23514" -> System.out.println("Valor inválido (violou uma restrição CHECK).");
                    default -> System.out.println("Erro de banco: " + e.getMessage());
                }
            }
        }

        private static String lerTexto(String msg) {
            System.out.print(msg);
            return in.nextLine().trim();
        }

        private static String lerComPadrao(String msg, String padrao) {
            String v = lerTexto(msg);
            return v.isEmpty() ? padrao : v;
        }

        private static int lerInt(String msg) {
            while (true) {
                try {
                    return Integer.parseInt(lerTexto(msg));
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número válido.");
                }
            }
        }

        private static Integer lerIntOpcional(String msg) {
            while (true) {
                String v = lerTexto(msg);
                if (v.isEmpty()) return null;
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número válido ou deixe em branco.");
                }
            }
        }

        private static String nvl(String s) { return s == null ? "-" : s; }

        private static String cortar(String s, int max) {
            return s.length() <= max ? s : s.substring(0, max - 1) + "…";
        }
}

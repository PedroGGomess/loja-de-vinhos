/**
 * MenuStock — menu de gestão de stock (para gerentes/funcionários).
 * Adicionar, editar, remover e listar vinhos.
 */
package main.ui;

import main.model.Vinho;
import main.service.StockService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuStock {

    private final Scanner sc;
    private final StockService stockService;

    public MenuStock(Scanner sc, StockService stockService) {
        this.sc = sc;
        this.stockService = stockService;
    }

    /** Inicia o loop do menu de stock. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> listarStock();
                case 2 -> adicionarVinho();
                case 3 -> editarVinho();
                case 4 -> removerVinho();
                case 5 -> stockService.alertarStockCritico();
                case 6 -> pesquisarVinho();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       📦 GESTÃO DE STOCK         ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Listar stock completo        ║");
        System.out.println("║  2. Adicionar novo vinho         ║");
        System.out.println("║  3. Editar vinho                 ║");
        System.out.println("║  4. Remover vinho                ║");
        System.out.println("║  5. Alertas de stock mínimo      ║");
        System.out.println("║  6. Pesquisar vinho              ║");
        System.out.println("║  0. Voltar                       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Lista todos os vinhos em stock. */
    private void listarStock() {
        List<Vinho> lista = stockService.listarTodosVinhos();
        imprimirTabela(lista);
    }

    /** Adiciona um novo vinho ao catálogo. */
    private void adicionarVinho() {
        System.out.println("\n--- Adicionar Novo Vinho ---");
        int id = stockService.gerarProximoId();
        System.out.print("Nome: "); String nome = sc.nextLine().trim();
        System.out.print("Tipo (Tinto/Branco/Rosé/Espumante/Porto): "); String tipo = sc.nextLine().trim();
        System.out.print("Região: "); String regiao = sc.nextLine().trim();
        int ano = lerInteiro("Ano de colheita: ");
        double preco = lerDouble("Preço (€): ");
        int stock = lerInteiro("Quantidade em stock: ");
        System.out.print("Descrição: "); String descricao = sc.nextLine().trim();
        double teor = lerDouble("Teor alcoólico (%): ");
        System.out.print("Produtor: "); String produtor = sc.nextLine().trim();

        Vinho vinho = new Vinho(id, nome, tipo, regiao, ano, preco, stock, descricao, teor, produtor);
        try {
            stockService.adicionarVinho(vinho);
            System.out.println("✅ Vinho adicionado com sucesso! ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /** Edita um vinho existente. */
    private void editarVinho() {
        listarStock();
        int id = lerInteiro("ID do vinho a editar: ");
        Optional<Vinho> opt = stockService.buscarVinhoPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Vinho não encontrado."); return; }
        Vinho v = opt.get();

        System.out.println("O que deseja alterar?");
        System.out.println("1. Preço  2. Quantidade em stock  3. Descrição  4. Tudo");
        int op = lerInteiro("Opção: ");
        switch (op) {
            case 1 -> {
                double novoPreco = lerDouble("Novo preço (€): ");
                try { stockService.atualizarPreco(id, novoPreco);
                    System.out.println("✅ Preço atualizado."); }
                catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
            }
            case 2 -> {
                int novaQtd = lerInteiro("Nova quantidade: ");
                try { stockService.atualizarQuantidade(id, novaQtd);
                    System.out.println("✅ Quantidade atualizada."); }
                catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
            }
            case 3 -> {
                System.out.print("Nova descrição: ");
                v.setDescricao(sc.nextLine().trim());
                stockService.guardar();
                System.out.println("✅ Descrição atualizada.");
            }
            case 4 -> {
                System.out.print("Nome [" + v.getNome() + "]: ");
                String nome = sc.nextLine().trim();
                if (!nome.isEmpty()) v.setNome(nome);
                System.out.print("Tipo [" + v.getTipo() + "]: ");
                String tipo = sc.nextLine().trim();
                if (!tipo.isEmpty()) v.setTipo(tipo);
                System.out.print("Região [" + v.getRegiao() + "]: ");
                String regiao = sc.nextLine().trim();
                if (!regiao.isEmpty()) v.setRegiao(regiao);
                double novoPreco = lerDouble("Preço [" + v.getPreco() + "€]: ");
                if (novoPreco > 0) v.setPreco(novoPreco);
                int novaQtd = lerInteiro("Stock [" + v.getQuantidadeStock() + "]: ");
                if (novaQtd >= 0) v.setQuantidadeStock(novaQtd);
                stockService.guardar();
                System.out.println("✅ Vinho atualizado.");
            }
            default -> System.out.println("❌ Opção inválida.");
        }
    }

    /** Remove um vinho do catálogo. */
    private void removerVinho() {
        listarStock();
        int id = lerInteiro("ID do vinho a remover: ");
        System.out.print("Tem a certeza? (s/n): ");
        if ("s".equalsIgnoreCase(sc.nextLine().trim())) {
            try {
                stockService.removerVinho(id);
                System.out.println("✅ Vinho removido com sucesso.");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    /** Pesquisa vinhos por nome ou ID. */
    private void pesquisarVinho() {
        System.out.print("Pesquisar por nome ou ID: ");
        String termo = sc.nextLine().trim();
        try {
            int id = Integer.parseInt(termo);
            stockService.buscarVinhoPorId(id).ifPresentOrElse(
                    v -> imprimirTabela(List.of(v)),
                    () -> System.out.println("❌ Vinho não encontrado.")
            );
        } catch (NumberFormatException e) {
            // pesquisa por nome
            List<Vinho> resultado = stockService.listarTodosVinhos().stream()
                    .filter(v -> v.getNome().toLowerCase().contains(termo.toLowerCase()))
                    .toList();
            imprimirTabela(resultado);
        }
    }

    /** Imprime tabela formatada de vinhos. */
    private void imprimirTabela(List<Vinho> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("\n  Nenhum vinho encontrado.");
            return;
        }
        System.out.println("\n╔══════╦══════════════════════════════╦══════════╦════════════╦═════════╦═══════╗");
        System.out.println("║  ID  ║ Nome                         ║ Tipo     ║ Região     ║ Preço   ║ Stock ║");
        System.out.println("╠══════╬══════════════════════════════╬══════════╬════════════╬═════════╬═══════╣");
        for (Vinho v : lista) {
            System.out.printf("║ %4d ║ %-28s ║ %-8s ║ %-10s ║ %6.2f€ ║ %5d ║%n",
                    v.getId(), truncar(v.getNome(), 28), truncar(v.getTipo(), 8),
                    truncar(v.getRegiao(), 10), v.getPreco(), v.getQuantidadeStock());
        }
        System.out.println("╚══════╩══════════════════════════════╩══════════╩════════════╩═════════╩═══════╝");
    }

    private String truncar(String texto, int max) {
        return UIHelper.truncar(texto, max);
    }

    private int lerInteiro(String prompt) {
        return UIHelper.lerInteiro(sc, prompt);
    }

    private double lerDouble(String prompt) {
        return UIHelper.lerDouble(sc, prompt);
    }
}

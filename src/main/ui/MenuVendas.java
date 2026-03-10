/**
 * MenuVendas — menu de consulta e gestão de vendas (para gerentes).
 */
package main.ui;

import main.model.ItemCarrinho;
import main.model.Venda;
import main.service.VendaService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuVendas {

    private final Scanner sc;
    private final VendaService vendaService;

    public MenuVendas(Scanner sc, VendaService vendaService) {
        this.sc = sc;
        this.vendaService = vendaService;
    }

    /** Inicia o loop do menu de vendas. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> listarTodasVendas();
                case 2 -> pesquisarVenda();
                case 3 -> cancelarVenda();
                case 4 -> detalhesVenda();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       🧾 GESTÃO DE VENDAS        ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Listar todas as vendas       ║");
        System.out.println("║  2. Pesquisar venda por ID       ║");
        System.out.println("║  3. Cancelar venda               ║");
        System.out.println("║  4. Ver detalhes de uma venda    ║");
        System.out.println("║  0. Voltar                       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Lista todas as vendas numa tabela resumida. */
    private void listarTodasVendas() {
        List<Venda> vendas = vendaService.listarTodasVendas();
        if (vendas.isEmpty()) { System.out.println("\n  Nenhuma venda registada."); return; }
        System.out.println("\n╔═══════╦══════════════════════╦════════════════════╦═══════════╦════════════╗");
        System.out.println("║  ID   ║ Data                 ║ Cliente            ║ Total     ║ Status     ║");
        System.out.println("╠═══════╬══════════════════════╬════════════════════╬═══════════╬════════════╣");
        for (Venda v : vendas) {
            String data = v.getDataVenda() != null
                    ? v.getDataVenda().toString().replace("T", " ").substring(0, 16) : "N/A";
            String cliente = v.getCliente() != null ? v.getCliente().getNome() : "N/A";
            System.out.printf("║ %5d ║ %-20s ║ %-18s ║ %8.2f€ ║ %-10s ║%n",
                    v.getId(), data, truncar(cliente, 18), v.getTotalVenda(), v.getStatus());
        }
        System.out.println("╚═══════╩══════════════════════╩════════════════════╩═══════════╩════════════╝");
    }

    /** Pesquisa uma venda pelo ID. */
    private void pesquisarVenda() {
        int id = lerInteiro("ID da venda: ");
        Optional<Venda> opt = vendaService.listarTodasVendas().stream()
                .filter(v -> v.getId() == id).findFirst();
        if (opt.isEmpty()) { System.out.println("❌ Venda não encontrada."); return; }
        imprimirDetalhes(opt.get());
    }

    /** Cancela uma venda. */
    private void cancelarVenda() {
        listarTodasVendas();
        int id = lerInteiro("ID da venda a cancelar: ");
        System.out.print("Tem a certeza? (s/n): ");
        if ("s".equalsIgnoreCase(sc.nextLine().trim())) {
            try {
                Venda v = vendaService.cancelarVenda(id);
                System.out.println("✅ Venda #" + v.getId() + " cancelada.");
            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /** Mostra detalhes completos de uma venda. */
    private void detalhesVenda() {
        int id = lerInteiro("ID da venda: ");
        Optional<Venda> opt = vendaService.listarTodasVendas().stream()
                .filter(v -> v.getId() == id).findFirst();
        if (opt.isEmpty()) { System.out.println("❌ Venda não encontrada."); return; }
        imprimirDetalhes(opt.get());
    }

    /** Imprime detalhes completos de uma venda. */
    private void imprimirDetalhes(Venda v) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("  DETALHES DA VENDA #" + v.getId());
        System.out.println("══════════════════════════════════════");
        System.out.println("  Cliente:    " + (v.getCliente() != null ? v.getCliente().getNome() : "N/A"));
        System.out.println("  Data:       " + (v.getDataVenda() != null
                ? v.getDataVenda().toString().replace("T", " ").substring(0, 16) : "N/A"));
        System.out.println("  Pagamento:  " + v.getMetodoPagamento());
        System.out.println("  Status:     " + v.getStatus());
        System.out.println("\n  Itens:");
        for (ItemCarrinho item : v.getItens()) {
            System.out.printf("    - %-28s x%d  @ %.2f€  = %.2f€%s%n",
                    item.getVinho() != null ? item.getVinho().getNome() : "N/A",
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal(),
                    item.temDesconto() ? " (-10%)" : "");
        }
        System.out.printf("%n  Total: %.2f€%n", v.getTotalVenda());
        System.out.println("══════════════════════════════════════");
    }

    private String truncar(String texto, int max) {
        return UIHelper.truncar(texto, max);
    }

    private int lerInteiro(String prompt) {
        return UIHelper.lerInteiro(sc, prompt);
    }
}

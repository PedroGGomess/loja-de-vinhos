/**
 * MenuLoja — menu da loja para clientes.
 * Listagem de vinhos, carrinho e finalização de compras.
 */
package main.ui;

import main.model.*;
import main.service.AuthService;
import main.service.LojaService;
import main.service.StockService;

import java.util.*;

public class MenuLoja {

    private final Scanner sc;
    private final AuthService authService;
    private final LojaService lojaService;
    private final StockService stockService;
    private final List<ItemCarrinho> carrinho;

    public MenuLoja(Scanner sc, AuthService authService,
                    LojaService lojaService, StockService stockService) {
        this.sc = sc;
        this.authService = authService;
        this.lojaService = lojaService;
        this.stockService = stockService;
        this.carrinho = new ArrayList<>();
    }

    /** Inicia o loop do menu da loja. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> listarTodosVinhos();
                case 2 -> filtrarVinhos();
                case 3 -> adicionarAoCarrinho();
                case 4 -> verCarrinho();
                case 5 -> finalizarCompra();
                case 6 -> verHistorico();
                case 7 -> avaliarVinho();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║         🍷 LOJA — CATÁLOGO       ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Ver todos os vinhos          ║");
        System.out.println("║  2. Filtrar/Pesquisar vinhos     ║");
        System.out.println("║  3. Adicionar ao carrinho        ║");
        System.out.println("║  4. Ver carrinho                 ║");
        System.out.println("║  5. Finalizar compra             ║");
        System.out.println("║  6. Histórico de compras         ║");
        System.out.println("║  7. Avaliar um vinho             ║");
        System.out.println("║  0. Sair / Voltar                ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Lista todos os vinhos disponíveis em formato de tabela. */
    private void listarTodosVinhos() {
        listarVinhos(stockService.listarTodosVinhos());
    }

    /** Menu de filtros de pesquisa. */
    private void filtrarVinhos() {
        System.out.println("\n--- Filtrar Vinhos ---");
        System.out.println("1. Por tipo   2. Por região   3. Por preço");
        System.out.println("4. Por nome   5. Pesquisa avançada   6. Ordenar");
        int op = lerInteiro("Opção: ");
        List<Vinho> resultado = new ArrayList<>();
        switch (op) {
            case 1 -> {
                System.out.print("Tipo (Tinto/Branco/Rosé/Espumante/Porto): ");
                resultado = lojaService.listarVinhosPorTipo(sc.nextLine().trim());
            }
            case 2 -> {
                System.out.print("Região: ");
                resultado = lojaService.listarVinhosPorRegiao(sc.nextLine().trim());
            }
            case 3 -> {
                double min = lerDouble("Preço mínimo (€): ");
                double max = lerDouble("Preço máximo (€): ");
                resultado = lojaService.listarVinhosPorFaixaPreco(min, max);
            }
            case 4 -> {
                System.out.print("Nome (ou parte do nome): ");
                resultado = lojaService.pesquisarVinhoPorNome(sc.nextLine().trim());
            }
            case 5 -> resultado = pesquisaAvancada();
            case 6 -> { ordenarVinhos(); return; }
            default -> { System.out.println("❌ Opção inválida."); return; }
        }
        listarVinhos(resultado);
    }

    /** Pesquisa avançada por múltiplos critérios. */
    private List<Vinho> pesquisaAvancada() {
        System.out.println("--- Pesquisa Avançada (deixe em branco para ignorar) ---");
        System.out.print("Nome: "); String nome = sc.nextLine().trim();
        System.out.print("Tipo: "); String tipo = sc.nextLine().trim();
        System.out.print("Região: "); String regiao = sc.nextLine().trim();
        System.out.print("Preço mínimo (0 para ignorar): ");
        double pmin = lerDoubleInLine();
        System.out.print("Preço máximo (0 para ignorar): ");
        double pmax = lerDoubleInLine();
        return lojaService.pesquisaAvancada(nome, tipo, regiao, pmin, pmax);
    }

    /** Ordena e lista vinhos por critério. */
    private void ordenarVinhos() {
        System.out.println("Ordenar por: 1. Preço  2. Nome  3. Ano  4. Região");
        int op = lerInteiro("Opção: ");
        List<Vinho> lista = new ArrayList<>(stockService.listarTodosVinhos());
        switch (op) {
            case 1 -> lista.sort(Comparator.comparingDouble(Vinho::getPreco));
            case 2 -> lista.sort(Comparator.comparing(Vinho::getNome));
            case 3 -> lista.sort(Comparator.comparingInt(Vinho::getAnoColheita).reversed());
            case 4 -> lista.sort(Comparator.comparing(Vinho::getRegiao));
            default -> { System.out.println("❌ Opção inválida."); return; }
        }
        listarVinhos(lista);
    }

    /** Adiciona um vinho ao carrinho. */
    private void adicionarAoCarrinho() {
        listarTodosVinhos();
        int id = lerInteiro("ID do vinho a adicionar: ");
        Optional<Vinho> opt = stockService.buscarVinhoPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Vinho não encontrado."); return; }
        int qtd = lerInteiro("Quantidade: ");
        try {
            lojaService.adicionarAoCarrinho(opt.get(), qtd, carrinho);
            System.out.printf("✅ %dx %s adicionado ao carrinho.%n", qtd, opt.get().getNome());
            if (qtd >= ItemCarrinho.QUANTIDADE_DESCONTO)
                System.out.println("🎉 Desconto de 10% aplicado por compra em volume!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /** Exibe o conteúdo atual do carrinho. */
    private void verCarrinho() {
        if (carrinho.isEmpty()) {
            System.out.println("\n🛒 O carrinho está vazio.");
            return;
        }
        System.out.println("\n🛒 CARRINHO DE COMPRAS:");
        System.out.println("┌──────────────────────────────────┬──────┬──────────┬───────────┐");
        System.out.println("│ Vinho                            │ Qtd  │ Preço/un │ Subtotal  │");
        System.out.println("├──────────────────────────────────┼──────┼──────────┼───────────┤");
        for (ItemCarrinho item : carrinho) {
            String desconto = item.temDesconto() ? "*" : " ";
            System.out.printf("│ %-32s │ %4d │ %7.2f€ │ %8.2f€ │%s%n",
                    item.getVinho().getNome(), item.getQuantidade(),
                    item.getPrecoUnitario(), item.getSubtotal(), desconto);
        }
        System.out.println("└──────────────────────────────────┴──────┴──────────┴───────────┘");
        System.out.printf("  Total: %.2f€%n", lojaService.calcularTotalCarrinho(carrinho));
        System.out.println("  * Desconto de 10% aplicado (compra ≥ 6 unidades)");
        System.out.println("\n  Opções: 1. Remover item   0. Voltar");
        int op = lerInteiro("Opção: ");
        if (op == 1) {
            int idRemover = lerInteiro("ID do vinho a remover: ");
            try {
                lojaService.removerDoCarrinho(idRemover, carrinho);
                System.out.println("✅ Item removido do carrinho.");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /** Finaliza a compra. */
    private void finalizarCompra() {
        if (carrinho.isEmpty()) { System.out.println("❌ Carrinho está vazio."); return; }
        System.out.printf("Total a pagar: %.2f€%n", lojaService.calcularTotalCarrinho(carrinho));
        System.out.println("Método de pagamento: 1. Cartão  2. MB Way  3. Transferência");
        int op = lerInteiro("Opção: ");
        String metodo = switch (op) {
            case 1 -> "Cartão";
            case 2 -> "MB Way";
            case 3 -> "Transferência";
            default -> "Cartão";
        };
        try {
            Venda venda = lojaService.finalizarCompra(authService.getClienteAtual(), carrinho, metodo);
            System.out.println("\n✅ Compra finalizada com sucesso!");
            System.out.printf("   Venda #%d | Total: %.2f€ | Pagamento: %s%n",
                    venda.getId(), venda.getTotalVenda(), venda.getMetodoPagamento());
        } catch (Exception e) {
            System.out.println("❌ Erro ao finalizar compra: " + e.getMessage());
        }
    }

    /** Exibe o histórico de compras do cliente. */
    private void verHistorico() {
        List<Venda> historico = authService.getClienteAtual().getHistoricoCompras();
        if (historico.isEmpty()) {
            System.out.println("\n📋 Ainda não tem compras anteriores.");
            return;
        }
        System.out.println("\n📋 HISTÓRICO DE COMPRAS:");
        System.out.println("┌───────┬──────────────────────┬───────────┬────────────┐");
        System.out.println("│  ID   │ Data                 │ Total     │ Status     │");
        System.out.println("├───────┼──────────────────────┼───────────┼────────────┤");
        for (Venda v : historico) {
            System.out.printf("│ %5d │ %-20s │ %8.2f€ │ %-10s │%n",
                    v.getId(),
                    v.getDataVenda().toString().replace("T", " ").substring(0, 16),
                    v.getTotalVenda(), v.getStatus());
        }
        System.out.println("└───────┴──────────────────────┴───────────┴────────────┘");
    }

    /** Permite ao cliente avaliar um vinho que comprou. */
    private void avaliarVinho() {
        List<Venda> historico = authService.getClienteAtual().getHistoricoCompras();
        if (historico.isEmpty()) {
            System.out.println("❌ Precisa de ter pelo menos uma compra para avaliar vinhos.");
            return;
        }
        listarTodosVinhos();
        int id = lerInteiro("ID do vinho a avaliar: ");
        Optional<Vinho> opt = stockService.buscarVinhoPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Vinho não encontrado."); return; }
        // Verifica se o cliente comprou este vinho
        boolean comprou = historico.stream()
                .flatMap(v -> v.getItens().stream())
                .anyMatch(item -> item.getVinho().getId() == id);
        if (!comprou) {
            System.out.println("❌ Só pode avaliar vinhos que comprou.");
            return;
        }
        int estrelas = lerInteiro("Avaliação (1-5 estrelas): ");
        if (estrelas < 1 || estrelas > 5) {
            System.out.println("❌ Avaliação deve ser entre 1 e 5.");
            return;
        }
        opt.get().adicionarAvaliacao(estrelas);
        stockService.guardar();
        System.out.printf("✅ Avaliação de %d ⭐ registada para '%s'.%n", estrelas, opt.get().getNome());
    }

    /** Imprime tabela de vinhos. */
    private void listarVinhos(List<Vinho> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("\n  Nenhum vinho encontrado.");
            return;
        }
        System.out.println("\n╔══════╦══════════════════════════════╦══════════╦════════════╦═════════╦═══════╦═══════╗");
        System.out.println("║  ID  ║ Nome                         ║ Tipo     ║ Região     ║ Preço   ║ Stock ║ Aval. ║");
        System.out.println("╠══════╬══════════════════════════════╬══════════╬════════════╬═════════╬═══════╬═══════╣");
        for (Vinho v : lista) {
            String aval = v.getAvaliacoes().isEmpty() ? "  —  "
                    : String.format("%.1f⭐", v.getMediaAvaliacoes());
            System.out.printf("║ %4d ║ %-28s ║ %-8s ║ %-10s ║ %6.2f€ ║ %5d ║ %-5s ║%n",
                    v.getId(), truncar(v.getNome(), 28), truncar(v.getTipo(), 8),
                    truncar(v.getRegiao(), 10), v.getPreco(), v.getQuantidadeStock(), aval);
        }
        System.out.println("╚══════╩══════════════════════════════╩══════════╩════════════╩═════════╩═══════╩═══════╝");
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

    private double lerDoubleInLine() {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim().replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("❌ Valor inválido. Tente novamente: "); }
        }
    }
}

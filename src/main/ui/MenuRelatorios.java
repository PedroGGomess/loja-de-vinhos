/**
 * MenuRelatorios — menu de relatórios de vendas para o gerente.
 * Apresenta faturação, produtos mais vendidos e exportação para ficheiro.
 */
package main.ui;

import main.model.Venda;
import main.model.Vinho;
import main.service.VendaService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuRelatorios {

    private final Scanner sc;
    private final VendaService vendaService;

    public MenuRelatorios(Scanner sc, VendaService vendaService) {
        this.sc = sc;
        this.vendaService = vendaService;
    }

    /** Inicia o loop do menu de relatórios. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> relatorioPorPeriodo();
                case 2 -> produtosMaisVendidos();
                case 3 -> faturacaoMensal();
                case 4 -> exportarRelatorio();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       📊 RELATÓRIOS              ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Vendas por período           ║");
        System.out.println("║  2. Produtos mais vendidos       ║");
        System.out.println("║  3. Faturação mensal             ║");
        System.out.println("║  4. Exportar relatório (.txt)    ║");
        System.out.println("║  0. Voltar                       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Relatório de vendas por período. */
    private void relatorioPorPeriodo() {
        System.out.println("\n--- Relatório por Período ---");
        System.out.print("Data início (dd/MM/yyyy): ");
        LocalDate inicio = lerData();
        System.out.print("Data fim (dd/MM/yyyy): ");
        LocalDate fim = lerData();

        List<Venda> vendas = vendaService.listarVendasPorPeriodo(inicio, fim);
        double total = vendas.stream().mapToDouble(Venda::getTotalVenda).sum();

        System.out.printf("%n📅 Período: %s a %s%n",
                inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.printf("   Total de vendas: %d%n", vendas.size());
        System.out.printf("   Faturação: %.2f€%n", total);

        if (!vendas.isEmpty()) {
            System.out.println("\n   Detalhe das vendas:");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Venda v : vendas) {
                System.out.printf("   #%d | %s | %s | %.2f€ | %s%n",
                        v.getId(),
                        v.getDataVenda() != null ? v.getDataVenda().format(fmt) : "N/A",
                        v.getCliente() != null ? v.getCliente().getNome() : "N/A",
                        v.getTotalVenda(), v.getStatus());
            }
        }
    }

    /** Top 5 produtos mais vendidos. */
    private void produtosMaisVendidos() {
        Map<Vinho, Integer> ranking = vendaService.produtosMaisVendidos();
        System.out.println("\n🏆 TOP PRODUTOS MAIS VENDIDOS:");
        System.out.println("┌──────────────────────────────────┬──────────┐");
        System.out.println("│ Vinho                            │ Vendidas │");
        System.out.println("├──────────────────────────────────┼──────────┤");
        int pos = 1;
        for (Map.Entry<Vinho, Integer> e : ranking.entrySet()) {
            if (pos > 5) break;
            System.out.printf("│ %d. %-29s │ %8d │%n",
                    pos++, truncar(e.getKey().getNome(), 29), e.getValue());
        }
        System.out.println("└──────────────────────────────────┴──────────┘");
        if (ranking.isEmpty()) System.out.println("  Nenhuma venda registada ainda.");
    }

    /** Faturação de um mês específico. */
    private void faturacaoMensal() {
        int mes = lerInteiro("Mês (1-12): ");
        int ano = lerInteiro("Ano: ");
        double total = vendaService.calcularFaturacaoMensal(mes, ano);
        System.out.printf("%n💰 Faturação de %02d/%d: %.2f€%n", mes, ano, total);
    }

    /** Exporta um relatório completo para ficheiro .txt. */
    private void exportarRelatorio() {
        String nomeFile = "relatorio_" + LocalDate.now().toString() + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nomeFile))) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            pw.println("╔══════════════════════════════════════════════════╗");
            pw.println("║           RELATÓRIO — LOJA DE VINHOS            ║");
            pw.println("╚══════════════════════════════════════════════════╝");
            pw.println("Gerado em: " + LocalDateTime.now().format(fmt));
            pw.println();

            List<Venda> todasVendas = vendaService.listarTodasVendas();
            pw.println("=== RESUMO GERAL ===");
            pw.println("Total de vendas: " + todasVendas.size());
            pw.printf("Faturação total: %.2f€%n", vendaService.calcularFaturacaoTotal());
            pw.println();

            pw.println("=== TODAS AS VENDAS ===");
            for (Venda v : todasVendas) {
                pw.printf("#%d | %s | %s | %.2f€ | %s%n",
                        v.getId(),
                        v.getDataVenda() != null ? v.getDataVenda().format(fmt) : "N/A",
                        v.getCliente() != null ? v.getCliente().getNome() : "N/A",
                        v.getTotalVenda(), v.getStatus());
            }
            pw.println();

            pw.println("=== TOP 5 PRODUTOS MAIS VENDIDOS ===");
            int pos = 1;
            for (Map.Entry<Vinho, Integer> e : vendaService.produtosMaisVendidos().entrySet()) {
                if (pos > 5) break;
                pw.printf("%d. %s — %d unidades%n", pos++, e.getKey().getNome(), e.getValue());
            }

            System.out.println("✅ Relatório exportado para: " + nomeFile);
        } catch (IOException e) {
            System.out.println("❌ Erro ao exportar relatório: " + e.getMessage());
        }
    }

    /** Lê uma data no formato dd/MM/yyyy. */
    private LocalDate lerData() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                return LocalDate.parse(sc.nextLine().trim(), fmt);
            } catch (Exception e) {
                System.out.print("❌ Formato inválido. Use dd/MM/yyyy: ");
            }
        }
    }

    private String truncar(String texto, int max) {
        return UIHelper.truncar(texto, max);
    }

    private int lerInteiro(String prompt) {
        return UIHelper.lerInteiro(sc, prompt);
    }
}

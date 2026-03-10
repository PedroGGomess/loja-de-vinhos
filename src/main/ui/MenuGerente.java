/**
 * MenuGerente — menu principal do gerente.
 * Acesso a stock, equipa, relatórios e vendas.
 */
package main.ui;

import main.service.*;

import java.util.Scanner;

public class MenuGerente {

    private final Scanner sc;
    private final AuthService authService;
    private final LojaService lojaService;
    private final StockService stockService;
    private final VendaService vendaService;
    private final EquipaService equipaService;

    public MenuGerente(Scanner sc, AuthService authService,
                       LojaService lojaService, StockService stockService,
                       VendaService vendaService, EquipaService equipaService) {
        this.sc = sc;
        this.authService = authService;
        this.lojaService = lojaService;
        this.stockService = stockService;
        this.vendaService = vendaService;
        this.equipaService = equipaService;
    }

    /** Inicia o loop do menu do gerente. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> new MenuStock(sc, stockService).iniciar();
                case 2 -> new MenuEquipa(sc, equipaService).iniciar();
                case 3 -> new MenuRelatorios(sc, vendaService).iniciar();
                case 4 -> new MenuVendas(sc, vendaService).iniciar();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        String nome = authService.getFuncionarioAtual() != null
                ? authService.getFuncionarioAtual().getNome() : "Gerente";
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.printf( "║  👔 %-29s║%n", "Olá, " + nome + "!");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Gestão de Stock              ║");
        System.out.println("║  2. Gestão de Equipa             ║");
        System.out.println("║  3. Relatórios                   ║");
        System.out.println("║  4. Vendas                       ║");
        System.out.println("║  0. Terminar sessão              ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    private int lerInteiro(String prompt) {
        return UIHelper.lerInteiro(sc, prompt);
    }
}

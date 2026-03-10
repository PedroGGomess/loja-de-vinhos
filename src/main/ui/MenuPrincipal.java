/**
 * MenuPrincipal — menu inicial da aplicação.
 * Permite login de cliente, login de gerente/funcionário e registo.
 */
package main.ui;

import main.model.Cliente;
import main.repository.ClienteRepository;
import main.repository.FuncionarioRepository;
import main.service.AuthService;
import main.service.EquipaService;
import main.service.LojaService;
import main.service.StockService;
import main.service.VendaService;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner sc;
    private final AuthService authService;
    private final LojaService lojaService;
    private final StockService stockService;
    private final VendaService vendaService;
    private final EquipaService equipaService;
    private final ClienteRepository clienteRepo;

    public MenuPrincipal(Scanner sc, AuthService authService,
                         LojaService lojaService, StockService stockService,
                         VendaService vendaService, EquipaService equipaService,
                         ClienteRepository clienteRepo) {
        this.sc = sc;
        this.authService = authService;
        this.lojaService = lojaService;
        this.stockService = stockService;
        this.vendaService = vendaService;
        this.equipaService = equipaService;
        this.clienteRepo = clienteRepo;
    }

    /** Inicia o loop do menu principal. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirCabecalho();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> loginCliente();
                case 2 -> loginGerente();
                case 3 -> registarCliente();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida. Tente novamente.");
            }
        }
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║  Obrigado por usar a Loja! 🍷    ║");
        System.out.println("╚══════════════════════════════════╝\n");
    }

    private void imprimirCabecalho() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║        🍷 LOJA DE VINHOS         ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Login como Cliente           ║");
        System.out.println("║  2. Login como Gerente/Funcionário║");
        System.out.println("║  3. Registar novo Cliente        ║");
        System.out.println("║  0. Sair                         ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Login de cliente por email e password. */
    private void loginCliente() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        if (authService.loginCliente(email, password)) {
            System.out.println("✅ Bem-vindo, " + authService.getClienteAtual().getNome() + "!");
            new MenuLoja(sc, authService, lojaService, stockService).iniciar();
            authService.logout();
        } else {
            System.out.println("❌ Email ou password incorretos.");
        }
    }

    /** Login de gerente/funcionário. */
    private void loginGerente() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        if (authService.loginGerente(email, password)) {
            System.out.println("✅ Bem-vindo, " + authService.getFuncionarioAtual().getNome() + "!");
            // Alerta de stock crítico ao entrar
            System.out.println();
            stockService.alertarStockCritico();
            new MenuGerente(sc, authService, lojaService, stockService,
                    vendaService, equipaService).iniciar();
            authService.logout();
        } else {
            System.out.println("❌ Email ou password incorretos.");
        }
    }

    /** Regista um novo cliente no sistema. */
    private void registarCliente() {
        System.out.println("\n--- Registo de novo Cliente ---");
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        if (!email.contains("@")) {
            System.out.println("❌ Email inválido.");
            return;
        }
        // Verifica se email já existe
        if (clienteRepo.findByEmail(email).isPresent()) {
            System.out.println("❌ Email já registado.");
            return;
        }

        System.out.print("Password: ");
        String password = sc.nextLine().trim();
        if (password.length() < 4) {
            System.out.println("❌ Password demasiado curta (mínimo 4 caracteres).");
            return;
        }

        System.out.print("Telefone (opcional): ");
        String telefone = sc.nextLine().trim();

        System.out.print("Morada (opcional): ");
        String morada = sc.nextLine().trim();

        java.util.List<Cliente> clientes = clienteRepo.findAll();
        int novoId = clientes.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
        Cliente novo = new Cliente(novoId, nome, email, password, telefone, morada);
        clientes.add(novo);
        clienteRepo.save(clientes);
        System.out.println("✅ Cliente registado com sucesso! Pode fazer login agora.");
    }

    /** Lê um inteiro do teclado, com tratamento de erros. */
    private int lerInteiro(String prompt) {
        return UIHelper.lerInteiro(sc, prompt);
    }
}

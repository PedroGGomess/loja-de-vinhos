/**
 * Main — ponto de entrada da aplicação Loja de Vinhos.
 *
 * Inicializa repositórios, popula dados de exemplo na primeira execução,
 * e inicia o menu principal em loop até o utilizador escolher sair.
 */
package main;

import main.model.*;
import main.repository.*;
import main.service.*;
import main.ui.MenuPrincipal;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // --- 1. Inicializar repositórios ---
        VinhoRepository vinhoRepo          = new VinhoRepository();
        ClienteRepository clienteRepo      = new ClienteRepository();
        VendaRepository vendaRepo          = new VendaRepository();
        FuncionarioRepository funcionarioRepo = new FuncionarioRepository();

        // --- 2. Popular dados de exemplo se for a primeira execução ---
        boolean primeiraExecucao = !new File("vinhos.dat").exists()
                || vinhoRepo.findAll().isEmpty();

        if (primeiraExecucao) {
            popularDadosExemplo(vinhoRepo, clienteRepo, funcionarioRepo);
        }

        // --- 3. Inicializar serviços ---
        StockService   stockService   = new StockService(vinhoRepo);
        VendaService   vendaService   = new VendaService(vendaRepo);
        EquipaService  equipaService  = new EquipaService(funcionarioRepo);
        LojaService    lojaService    = new LojaService(vinhoRepo, vendaRepo, clienteRepo);
        AuthService    authService    = new AuthService(clienteRepo, funcionarioRepo);

        // --- 4. Iniciar menu principal ---
        Scanner sc = new Scanner(System.in);
        MenuPrincipal menu = new MenuPrincipal(sc, authService, lojaService,
                stockService, vendaService, equipaService, clienteRepo);
        menu.iniciar();

        // --- 5. Guardar dados antes de terminar ---
        vinhoRepo.save(vinhoRepo.findAll());
        clienteRepo.save(clienteRepo.findAll());
        vendaRepo.save(vendaRepo.findAll());
        funcionarioRepo.save(funcionarioRepo.findAll());

        sc.close();
    }

    /**
     * Popula o sistema com dados de exemplo (vinhos, funcionários e clientes).
     * É chamado apenas na primeira execução, quando os ficheiros .dat não existem.
     */
    private static void popularDadosExemplo(VinhoRepository vinhoRepo,
                                             ClienteRepository clienteRepo,
                                             FuncionarioRepository funcionarioRepo) {
        System.out.println("🔧 Primeira execução — a carregar dados de exemplo...");

        // --- Vinhos de exemplo (15 vinhos) ---
        List<Vinho> vinhos = List.of(
            new Vinho(1,  "Porta 6 Tinto 2020",            "Tinto",     "Douro",        2020,   8.50, 30, "Vinho tinto frutado e encorpado do Douro.",              13.5, "Casa Santos Lima"),
            new Vinho(2,  "Pêra-Manca Tinto 2015",         "Tinto",     "Alentejo",     2015,  85.00, 10, "Icónico tinto alentejano de grande elegância.",          14.0, "Eugénio de Almeida"),
            new Vinho(3,  "Esporão Reserva 2019",          "Tinto",     "Alentejo",     2019,  12.00, 25, "Reserva alentejana com notas de frutos negros.",         14.5, "Herdade do Esporão"),
            new Vinho(4,  "Quinta do Crasto Reserva 2018", "Tinto",     "Douro",        2018,  22.00, 15, "Tinto duriense com taninos maduros e final longo.",      14.5, "Quinta do Crasto"),
            new Vinho(5,  "Vinha Paz Branco 2021",         "Branco",    "Palmela",      2021,   7.50, 20, "Branco fresco e aromático da Península de Setúbal.",     12.5, "José Maria da Fonseca"),
            new Vinho(6,  "Alvarinho Soalheiro 2022",      "Branco",    "Vinho Verde",  2022,  14.00, 18, "Alvarinho premium de Monção com acidez vibrante.",       12.0, "Quinta de Soalheiro"),
            new Vinho(7,  "Graham's 20 Anos Porto",        "Porto",     "Porto",        2003,  35.00,  8, "Tawny envelhecido com notas de frutos secos e mel.",     20.0, "Graham's"),
            new Vinho(8,  "Ferreira Vintage 2017",         "Porto",     "Porto",        2017,  55.00,  5, "Porto vintage de excecional qualidade.",                 20.5, "Ferreira"),
            new Vinho(9,  "Mateus Rosé 2022",              "Rosé",      "Bairrada",     2022,   6.00, 40, "O rosé português mais famoso do mundo.",                 11.0, "Sogrape"),
            new Vinho(10, "Quinta do Vale Meão 2019",      "Tinto",     "Douro",        2019, 120.00,  6, "Um dos melhores vinhos do Douro, complexo e potente.",   14.5, "Quinta do Vale Meão"),
            new Vinho(11, "Niepoort Redoma Branco 2021",   "Branco",    "Douro",        2021,  18.00, 12, "Branco duriense fresco e mineral de grande finesse.",    13.0, "Niepoort"),
            new Vinho(12, "Luis Pato Vinha Barrosa 2020",  "Tinto",     "Bairrada",     2020,  25.00, 10, "Baga puro, estruturado e de grande potencial.",          13.5, "Luís Pato"),
            new Vinho(13, "Muralhas de Moncão 2022",       "Branco",    "Vinho Verde",  2022,   9.50, 22, "Loureiro e Alvarinho frescos e frutados.",               11.5, "Adega de Monção"),
            new Vinho(14, "Quinta da Aveleda Espumante",   "Espumante", "Vinho Verde",  2022,  11.00, 16, "Espumante verde elegante com pérolas finas.",            11.0, "Aveleda"),
            new Vinho(15, "Cartuxa Évora Tinto 2020",      "Tinto",     "Alentejo",     2020,  14.50, 20, "Tinto alentejano equilibrado com notas de amora.",       14.0, "Fundação Eugénio de Almeida")
        );
        vinhoRepo.save(vinhos);

        // --- Gerente ---
        Gerente gerente = new Gerente(1, "Ana Gerente", "admin@loja.pt", "admin123", 2500.00);
        gerente.setDataAdmissao(LocalDate.of(2020, 1, 15));

        // --- Funcionários ---
        Funcionario func1 = new Funcionario(2, "João Silva",   "joao@loja.pt",   "func123", "Vendedor",  1200.00);
        Funcionario func2 = new Funcionario(3, "Maria Santos", "maria@loja.pt",  "func123", "Sommelier", 1500.00);
        Funcionario func3 = new Funcionario(4, "Carlos Neves", "carlos@loja.pt", "func123", "Armazém",   1100.00);

        func1.setDataAdmissao(LocalDate.of(2021, 3, 10));
        func2.setDataAdmissao(LocalDate.of(2021, 6, 20));
        func3.setDataAdmissao(LocalDate.of(2022, 1, 5));

        funcionarioRepo.save(List.of(gerente, func1, func2, func3));

        // --- Clientes ---
        Cliente c1 = new Cliente(1, "Pedro Cliente", "cliente@email.pt",  "cliente123", "912345678", "Rua das Flores 10, Lisboa");
        Cliente c2 = new Cliente(2, "Sofia Ferreira","sofia@email.pt",    "pass1234",   "961234567", "Av. da Liberdade 50, Porto");
        Cliente c3 = new Cliente(3, "Rui Oliveira",  "rui@email.pt",      "pass1234",   "931234567", "Travessa do Vinho 3, Évora");

        clienteRepo.save(List.of(c1, c2, c3));

        System.out.println("✅ Dados de exemplo carregados com sucesso!");
        System.out.println("   Gerente:    admin@loja.pt / admin123");
        System.out.println("   Funcionário: joao@loja.pt / func123");
        System.out.println("   Cliente:    cliente@email.pt / cliente123");
        System.out.println();
    }
}

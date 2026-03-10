/**
 * MenuEquipa — menu de gestão da equipa de funcionários.
 */
package main.ui;

import main.model.Funcionario;
import main.service.EquipaService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuEquipa {

    private final Scanner sc;
    private final EquipaService equipaService;

    public MenuEquipa(Scanner sc, EquipaService equipaService) {
        this.sc = sc;
        this.equipaService = equipaService;
    }

    /** Inicia o loop do menu de equipa. */
    public void iniciar() {
        boolean correr = true;
        while (correr) {
            imprimirMenu();
            int opcao = lerInteiro("Opção: ");
            switch (opcao) {
                case 1 -> listarFuncionarios();
                case 2 -> adicionarFuncionario();
                case 3 -> editarFuncionario();
                case 4 -> desativarFuncionario();
                case 5 -> verMassaSalarial();
                case 0 -> correr = false;
                default -> System.out.println("❌ Opção inválida.");
            }
        }
    }

    private void imprimirMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       👥 GESTÃO DE EQUIPA        ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Listar funcionários          ║");
        System.out.println("║  2. Adicionar funcionário        ║");
        System.out.println("║  3. Editar funcionário           ║");
        System.out.println("║  4. Desativar funcionário        ║");
        System.out.println("║  5. Ver massa salarial           ║");
        System.out.println("║  0. Voltar                       ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    /** Lista todos os funcionários ativos. */
    private void listarFuncionarios() {
        List<Funcionario> lista = equipaService.listarFuncionariosAtivos();
        if (lista.isEmpty()) { System.out.println("\n  Nenhum funcionário ativo."); return; }
        System.out.println("\n╔═══════╦════════════════════════════╦═══════════════╦═══════════╗");
        System.out.println("║  ID   ║ Nome                       ║ Cargo         ║ Salário   ║");
        System.out.println("╠═══════╬════════════════════════════╬═══════════════╬═══════════╣");
        for (Funcionario f : lista) {
            System.out.printf("║ %5d ║ %-26s ║ %-13s ║ %8.2f€ ║%n",
                    f.getId(), truncar(f.getNome(), 26), truncar(f.getCargo(), 13), f.getSalario());
        }
        System.out.println("╚═══════╩════════════════════════════╩═══════════════╩═══════════╝");
    }

    /** Adiciona um novo funcionário. */
    private void adicionarFuncionario() {
        System.out.println("\n--- Adicionar Funcionário ---");
        int id = equipaService.gerarProximoId();
        System.out.print("Nome: "); String nome = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();
        System.out.print("Password: "); String password = sc.nextLine().trim();
        System.out.print("Cargo: "); String cargo = sc.nextLine().trim();
        double salario = lerDouble("Salário (€): ");

        Funcionario f = new Funcionario(id, nome, email, password, cargo, salario);
        try {
            equipaService.adicionarFuncionario(f);
            System.out.println("✅ Funcionário adicionado com sucesso! ID: " + id);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /** Edita dados de um funcionário existente. */
    private void editarFuncionario() {
        listarFuncionarios();
        int id = lerInteiro("ID do funcionário a editar: ");
        Optional<Funcionario> opt = equipaService.buscarFuncionarioPorId(id);
        if (opt.isEmpty()) { System.out.println("❌ Funcionário não encontrado."); return; }
        Funcionario f = opt.get();

        System.out.print("Nome [" + f.getNome() + "]: ");
        String nome = sc.nextLine().trim();
        if (!nome.isEmpty()) f.setNome(nome);

        System.out.print("Cargo [" + f.getCargo() + "]: ");
        String cargo = sc.nextLine().trim();
        if (!cargo.isEmpty()) f.setCargo(cargo);

        System.out.print("Salário [" + f.getSalario() + "€] (0 para manter): ");
        try {
            double sal = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
            if (sal > 0) f.setSalario(sal);
        } catch (NumberFormatException e) { /* mantém salário atual */ }

        try {
            equipaService.atualizarFuncionario(f);
            System.out.println("✅ Funcionário atualizado.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    /** Desativa um funcionário. */
    private void desativarFuncionario() {
        listarFuncionarios();
        int id = lerInteiro("ID do funcionário a desativar: ");
        System.out.print("Tem a certeza? (s/n): ");
        if ("s".equalsIgnoreCase(sc.nextLine().trim())) {
            try {
                equipaService.desativarFuncionario(id);
                System.out.println("✅ Funcionário desativado.");
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    /** Mostra a massa salarial total. */
    private void verMassaSalarial() {
        double total = equipaService.calcularMassaSalarial();
        System.out.printf("%n💰 Massa salarial total (funcionários ativos): %.2f€%n", total);
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

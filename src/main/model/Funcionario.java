/**
 * Classe Funcionario — representa um funcionário da loja.
 * Herda da classe abstrata Pessoa.
 */
package main.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Funcionario extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cargo;
    private double salario;
    private LocalDate dataAdmissao;
    private boolean ativo;

    /** Construtor completo */
    public Funcionario(int id, String nome, String email, String password,
                       String cargo, double salario) {
        super(id, nome, email, password);
        this.cargo = cargo;
        this.salario = salario;
        this.dataAdmissao = LocalDate.now();
        this.ativo = true;
    }

    /** Construtor padrão */
    public Funcionario() {
        this.ativo = true;
        this.dataAdmissao = LocalDate.now();
    }

    // --- Getters e Setters ---

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("Funcionario{id=%d, nome='%s', cargo='%s', salario=%.2f€, ativo=%s}",
                getId(), getNome(), cargo, salario, ativo ? "Sim" : "Não");
    }
}

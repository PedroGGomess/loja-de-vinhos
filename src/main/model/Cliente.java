/**
 * Classe Cliente — representa um cliente da loja de vinhos.
 * Herda de Pessoa e mantém histórico de compras.
 */
package main.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String telefone;
    private String morada;
    private List<Venda> historicoCompras;
    private LocalDate dataCadastro;

    /** Construtor completo */
    public Cliente(int id, String nome, String email, String password,
                   String telefone, String morada) {
        super(id, nome, email, password);
        this.telefone = telefone;
        this.morada = morada;
        this.historicoCompras = new ArrayList<>();
        this.dataCadastro = LocalDate.now();
    }

    /** Construtor padrão */
    public Cliente() {
        this.historicoCompras = new ArrayList<>();
        this.dataCadastro = LocalDate.now();
    }

    /** Adiciona uma venda ao histórico do cliente. */
    public void adicionarCompra(Venda venda) {
        if (historicoCompras == null) historicoCompras = new ArrayList<>();
        historicoCompras.add(venda);
    }

    // --- Getters e Setters ---

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }

    public List<Venda> getHistoricoCompras() {
        if (historicoCompras == null) historicoCompras = new ArrayList<>();
        return historicoCompras;
    }
    public void setHistoricoCompras(List<Venda> historicoCompras) { this.historicoCompras = historicoCompras; }

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    @Override
    public String toString() {
        return String.format("Cliente{id=%d, nome='%s', email='%s', telefone='%s', cadastro=%s}",
                getId(), getNome(), getEmail(), telefone, dataCadastro);
    }
}

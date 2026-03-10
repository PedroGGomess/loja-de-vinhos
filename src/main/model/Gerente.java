/**
 * Classe Gerente — representa o gerente da loja.
 * Herda de Funcionario e tem acesso privilegiado ao sistema.
 */
package main.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Gerente extends Funcionario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nível de acesso do gerente. */
    public static final String NIVEL_ACESSO = "ADMIN";

    private String nivelAcesso;
    private List<Funcionario> equipa;

    /** Construtor completo */
    public Gerente(int id, String nome, String email, String password, double salario) {
        super(id, nome, email, password, "Gerente", salario);
        this.nivelAcesso = NIVEL_ACESSO;
        this.equipa = new ArrayList<>();
    }

    /** Construtor padrão */
    public Gerente() {
        super();
        this.nivelAcesso = NIVEL_ACESSO;
        this.equipa = new ArrayList<>();
    }

    /**
     * Gera uma linha de relatório com informações básicas da equipa e vendas.
     * @return texto resumo do relatório
     */
    public String gerarRelatorio() {
        return String.format("Relatório do Gerente %s | Equipa: %d funcionários | Nível: %s",
                getNome(), equipa != null ? equipa.size() : 0, nivelAcesso);
    }

    /**
     * Aprova uma venda, marcando o status como CONCLUIDA.
     * @param venda a venda a aprovar
     */
    public void aprovarVenda(Venda venda) {
        if (venda != null) {
            venda.setStatus(Venda.STATUS_CONCLUIDA);
        }
    }

    /**
     * Gere o stock — verifica vinhos com quantidade crítica.
     * @param vinhos lista de vinhos para verificar
     */
    public void gerirStock(List<Vinho> vinhos) {
        if (vinhos == null) return;
        System.out.println("⚠  Verificação de stock crítico pelo Gerente " + getNome() + ":");
        vinhos.stream()
              .filter(v -> v.getQuantidadeStock() < 5)
              .forEach(v -> System.out.println("   ❗ Stock crítico: " + v.getNome()
                      + " — apenas " + v.getQuantidadeStock() + " unidades"));
    }

    // --- Getters e Setters ---

    public String getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(String nivelAcesso) { this.nivelAcesso = nivelAcesso; }

    public List<Funcionario> getEquipa() {
        if (equipa == null) equipa = new ArrayList<>();
        return equipa;
    }
    public void setEquipa(List<Funcionario> equipa) { this.equipa = equipa; }

    @Override
    public String toString() {
        return String.format("Gerente{id=%d, nome='%s', nivelAcesso='%s', equipa=%d}",
                getId(), getNome(), nivelAcesso, equipa != null ? equipa.size() : 0);
    }
}

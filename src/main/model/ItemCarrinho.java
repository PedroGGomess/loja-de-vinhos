/**
 * Classe ItemCarrinho — representa um item no carrinho de compras.
 * Guarda o vinho, a quantidade e calcula o subtotal com possível desconto.
 */
package main.model;

import java.io.Serializable;

public class ItemCarrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Quantidade mínima para desconto por volume. */
    public static final int QUANTIDADE_DESCONTO = 6;
    /** Percentagem de desconto por volume (10%). */
    public static final double PERCENTAGEM_DESCONTO = 0.10;

    private Vinho vinho;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    /** Construtor completo */
    public ItemCarrinho(Vinho vinho, int quantidade) {
        this.vinho = vinho;
        this.quantidade = quantidade;
        this.precoUnitario = vinho.getPreco();
        calcularSubtotal();
    }

    /** Construtor padrão */
    public ItemCarrinho() {}

    /**
     * Calcula o subtotal aplicando desconto de 10% se quantidade >= 6.
     */
    public void calcularSubtotal() {
        double total = precoUnitario * quantidade;
        if (quantidade >= QUANTIDADE_DESCONTO) {
            total *= (1 - PERCENTAGEM_DESCONTO);
        }
        this.subtotal = total;
    }

    /** Verifica se este item tem desconto aplicado. */
    public boolean temDesconto() {
        return quantidade >= QUANTIDADE_DESCONTO;
    }

    // --- Getters e Setters ---

    public Vinho getVinho() { return vinho; }
    public void setVinho(Vinho vinho) { this.vinho = vinho; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
        calcularSubtotal();
    }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
        calcularSubtotal();
    }

    public double getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        String desconto = temDesconto() ? " (-10%)" : "";
        return String.format("ItemCarrinho{vinho='%s', qtd=%d, precoUnit=%.2f€%s, subtotal=%.2f€}",
                vinho != null ? vinho.getNome() : "N/A", quantidade, precoUnitario, desconto, subtotal);
    }
}

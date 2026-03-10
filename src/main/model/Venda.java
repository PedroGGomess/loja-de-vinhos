/**
 * Classe Venda — representa uma venda realizada na loja.
 * Contém o cliente, os itens comprados, total e estado da venda.
 */
package main.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Estados possíveis de uma venda. */
    public static final String STATUS_CONCLUIDA = "CONCLUIDA";
    public static final String STATUS_CANCELADA = "CANCELADA";
    public static final String STATUS_PENDENTE  = "PENDENTE";

    private int id;
    private Cliente cliente;
    private List<ItemCarrinho> itens;
    private LocalDateTime dataVenda;
    private double totalVenda;
    private String metodoPagamento;
    private String status;
    private Funcionario vendedor;

    /** Construtor completo */
    public Venda(int id, Cliente cliente, List<ItemCarrinho> itens,
                 String metodoPagamento, Funcionario vendedor) {
        this.id = id;
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.metodoPagamento = metodoPagamento;
        this.vendedor = vendedor;
        this.dataVenda = LocalDateTime.now();
        this.status = STATUS_CONCLUIDA;
        this.totalVenda = calcularTotal();
    }

    /** Construtor padrão */
    public Venda() {
        this.itens = new ArrayList<>();
        this.dataVenda = LocalDateTime.now();
        this.status = STATUS_PENDENTE;
    }

    /**
     * Calcula o total da venda somando todos os subtotais dos itens.
     * @return total da venda em euros
     */
    public double calcularTotal() {
        if (itens == null) return 0.0;
        return itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<ItemCarrinho> getItens() {
        if (itens == null) itens = new ArrayList<>();
        return itens;
    }
    public void setItens(List<ItemCarrinho> itens) { this.itens = itens; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public double getTotalVenda() { return totalVenda; }
    public void setTotalVenda(double totalVenda) { this.totalVenda = totalVenda; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Funcionario getVendedor() { return vendedor; }
    public void setVendedor(Funcionario vendedor) { this.vendedor = vendedor; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("Venda{id=%d, cliente='%s', total=%.2f€, data=%s, status=%s}",
                id,
                cliente != null ? cliente.getNome() : "N/A",
                totalVenda,
                dataVenda != null ? dataVenda.format(fmt) : "N/A",
                status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venda)) return false;
        Venda v = (Venda) o;
        return id == v.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

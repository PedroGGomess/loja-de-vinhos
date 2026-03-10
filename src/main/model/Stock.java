/**
 * Classe Stock — representa o estado geral do stock da loja.
 * Agrupa os vinhos disponíveis e fornece métodos utilitários de consulta.
 */
package main.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stock implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Limite mínimo de stock antes de gerar alerta. */
    public static final int LIMITE_STOCK_MINIMO = 5;

    private List<Vinho> vinhos;

    /** Construtor padrão */
    public Stock() {
        this.vinhos = new ArrayList<>();
    }

    /** Construtor com lista inicial */
    public Stock(List<Vinho> vinhos) {
        this.vinhos = new ArrayList<>(vinhos);
    }

    /** Adiciona um vinho ao stock. */
    public void adicionarVinho(Vinho vinho) {
        vinhos.add(vinho);
    }

    /** Remove um vinho do stock pelo ID. */
    public boolean removerVinho(int id) {
        return vinhos.removeIf(v -> v.getId() == id);
    }

    /** Retorna lista de vinhos com stock abaixo do limite mínimo. */
    public List<Vinho> getVinhosStockBaixo() {
        List<Vinho> resultado = new ArrayList<>();
        for (Vinho v : vinhos) {
            if (v.getQuantidadeStock() < LIMITE_STOCK_MINIMO) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    public List<Vinho> getVinhos() { return vinhos; }
    public void setVinhos(List<Vinho> vinhos) { this.vinhos = vinhos; }

    @Override
    public String toString() {
        return "Stock{totalVinhos=" + vinhos.size() + "}";
    }
}

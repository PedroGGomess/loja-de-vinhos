/**
 * Classe Vinho — representa um vinho disponível na loja.
 * Implementa Comparable (por preço) e Serializable para persistência.
 */
package main.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vinho implements Comparable<Vinho>, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    /** Tipo: Tinto, Branco, Rosé, Espumante, Porto */
    private String tipo;
    /** Região: Douro, Alentejo, Vinho Verde, Dão, etc. */
    private String regiao;
    private int anoColheita;
    private double preco;
    private int quantidadeStock;
    private String descricao;
    private double teorAlcoolico;
    private String produtor;
    /** Avaliações dos clientes (1-5 estrelas) */
    private List<Integer> avaliacoes;

    /** Construtor completo */
    public Vinho(int id, String nome, String tipo, String regiao, int anoColheita,
                 double preco, int quantidadeStock, String descricao,
                 double teorAlcoolico, String produtor) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.regiao = regiao;
        this.anoColheita = anoColheita;
        this.preco = preco;
        this.quantidadeStock = quantidadeStock;
        this.descricao = descricao;
        this.teorAlcoolico = teorAlcoolico;
        this.produtor = produtor;
        this.avaliacoes = new ArrayList<>();
    }

    /** Construtor cópia */
    public Vinho(Vinho outro) {
        this(outro.id, outro.nome, outro.tipo, outro.regiao, outro.anoColheita,
             outro.preco, outro.quantidadeStock, outro.descricao,
             outro.teorAlcoolico, outro.produtor);
        this.avaliacoes = new ArrayList<>(outro.avaliacoes);
    }

    /** Construtor padrão */
    public Vinho() {
        this.avaliacoes = new ArrayList<>();
    }

    /** Adiciona uma avaliação (1-5 estrelas) ao vinho. */
    public void adicionarAvaliacao(int estrelas) {
        if (estrelas >= 1 && estrelas <= 5) {
            avaliacoes.add(estrelas);
        }
    }

    /** Calcula a média das avaliações. Retorna 0 se não houver avaliações. */
    public double getMediaAvaliacoes() {
        if (avaliacoes == null || avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    /** Compara vinhos por preço (crescente). */
    @Override
    public int compareTo(Vinho outro) {
        return Double.compare(this.preco, outro.preco);
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }

    public int getAnoColheita() { return anoColheita; }
    public void setAnoColheita(int anoColheita) { this.anoColheita = anoColheita; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidadeStock() { return quantidadeStock; }
    public void setQuantidadeStock(int quantidadeStock) { this.quantidadeStock = quantidadeStock; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getTeorAlcoolico() { return teorAlcoolico; }
    public void setTeorAlcoolico(double teorAlcoolico) { this.teorAlcoolico = teorAlcoolico; }

    public String getProdutor() { return produtor; }
    public void setProdutor(String produtor) { this.produtor = produtor; }

    public List<Integer> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<Integer> avaliacoes) { this.avaliacoes = avaliacoes; }

    @Override
    public String toString() {
        return String.format("Vinho{id=%d, nome='%s', tipo='%s', regiao='%s', ano=%d, preco=%.2f€, stock=%d}",
                id, nome, tipo, regiao, anoColheita, preco, quantidadeStock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vinho)) return false;
        Vinho v = (Vinho) o;
        return id == v.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

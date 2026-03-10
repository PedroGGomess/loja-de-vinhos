/**
 * StockService — serviço de gestão de stock de vinhos.
 * Operações de CRUD sobre o catálogo de vinhos.
 */
package main.service;

import main.model.Stock;
import main.model.Vinho;
import main.repository.VinhoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockService {

    /** Stock mínimo para alerta crítico. */
    public static final int LIMITE_CRITICO = 5;

    private final VinhoRepository vinhoRepo;
    private final List<Vinho> vinhos;

    public StockService(VinhoRepository vinhoRepo) {
        this.vinhoRepo = vinhoRepo;
        this.vinhos = vinhoRepo.findAll();
    }

    /**
     * Adiciona um novo vinho ao catálogo.
     * @param vinho vinho a adicionar
     */
    public void adicionarVinho(Vinho vinho) {
        if (vinho == null) throw new IllegalArgumentException("Vinho não pode ser nulo.");
        if (vinho.getPreco() <= 0) throw new IllegalArgumentException("Preço deve ser maior que 0.");
        if (vinho.getQuantidadeStock() < 0) throw new IllegalArgumentException("Stock não pode ser negativo.");
        vinhos.add(vinho);
        vinhoRepo.save(vinhos);
    }

    /**
     * Remove um vinho pelo ID.
     * @param id identificador do vinho
     */
    public void removerVinho(int id) {
        boolean removido = vinhos.removeIf(v -> v.getId() == id);
        if (!removido) throw new IllegalArgumentException("Vinho com ID " + id + " não encontrado.");
        vinhoRepo.save(vinhos);
    }

    /**
     * Atualiza a quantidade em stock de um vinho.
     * @param id identificador do vinho
     * @param novaQuantidade nova quantidade (>= 0)
     */
    public void atualizarQuantidade(int id, int novaQuantidade) {
        if (novaQuantidade < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        Vinho v = buscarVinhoPorId(id).orElseThrow(
                () -> new IllegalArgumentException("Vinho não encontrado: " + id));
        v.setQuantidadeStock(novaQuantidade);
        vinhoRepo.save(vinhos);
    }

    /**
     * Atualiza o preço de um vinho.
     * @param id identificador do vinho
     * @param novoPreco novo preço (> 0)
     */
    public void atualizarPreco(int id, double novoPreco) {
        if (novoPreco <= 0) throw new IllegalArgumentException("Preço deve ser maior que 0.");
        Vinho v = buscarVinhoPorId(id).orElseThrow(
                () -> new IllegalArgumentException("Vinho não encontrado: " + id));
        v.setPreco(novoPreco);
        vinhoRepo.save(vinhos);
    }

    /**
     * Lista os vinhos com stock abaixo do limite mínimo.
     * @param limiteMinimo quantidade mínima de referência
     * @return lista de vinhos com stock baixo
     */
    public List<Vinho> listarStockBaixo(int limiteMinimo) {
        List<Vinho> resultado = new ArrayList<>();
        for (Vinho v : vinhos) {
            if (v.getQuantidadeStock() < limiteMinimo) resultado.add(v);
        }
        return resultado;
    }

    /**
     * Procura um vinho pelo ID.
     * @param id identificador
     * @return Optional com o vinho, ou vazio se não encontrado
     */
    public Optional<Vinho> buscarVinhoPorId(int id) {
        return vinhos.stream().filter(v -> v.getId() == id).findFirst();
    }

    /**
     * Retorna todos os vinhos em catálogo.
     * @return lista de todos os vinhos
     */
    public List<Vinho> listarTodosVinhos() {
        return new ArrayList<>(vinhos);
    }

    /**
     * Imprime alertas para vinhos com stock crítico (< LIMITE_CRITICO).
     */
    public void alertarStockCritico() {
        List<Vinho> criticos = listarStockBaixo(LIMITE_CRITICO);
        if (criticos.isEmpty()) {
            System.out.println("✅ Nenhum vinho com stock crítico.");
        } else {
            System.out.println("⚠  ALERTAS DE STOCK CRÍTICO:");
            for (Vinho v : criticos) {
                System.out.printf("   ❗ %-30s — %d unidades restantes%n",
                        v.getNome(), v.getQuantidadeStock());
            }
        }
    }

    /**
     * Gera um ID único para um novo vinho.
     * @return próximo ID disponível
     */
    public int gerarProximoId() {
        return vinhos.stream().mapToInt(Vinho::getId).max().orElse(0) + 1;
    }

    /** Persiste o estado atual no repositório. */
    public void guardar() {
        vinhoRepo.save(vinhos);
    }
}

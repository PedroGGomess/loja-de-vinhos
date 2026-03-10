/**
 * LojaService — serviço principal da loja.
 * Pesquisa de vinhos, gestão de carrinho e finalização de compras.
 */
package main.service;

import main.model.*;
import main.repository.ClienteRepository;
import main.repository.VendaRepository;
import main.repository.VinhoRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LojaService {

    private final VinhoRepository vinhoRepo;
    private final VendaRepository vendaRepo;
    private final ClienteRepository clienteRepo;

    public LojaService(VinhoRepository vinhoRepo, VendaRepository vendaRepo,
                       ClienteRepository clienteRepo) {
        this.vinhoRepo = vinhoRepo;
        this.vendaRepo = vendaRepo;
        this.clienteRepo = clienteRepo;
    }

    /**
     * Lista vinhos filtrados por tipo.
     * @param tipo tipo do vinho (ex: "Tinto", "Branco")
     * @return lista de vinhos do tipo especificado
     */
    public List<Vinho> listarVinhosPorTipo(String tipo) {
        return vinhoRepo.findAll().stream()
                .filter(v -> v.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    /**
     * Lista vinhos filtrados por região.
     * @param regiao região produtora
     * @return lista de vinhos da região
     */
    public List<Vinho> listarVinhosPorRegiao(String regiao) {
        return vinhoRepo.findAll().stream()
                .filter(v -> v.getRegiao().equalsIgnoreCase(regiao))
                .collect(Collectors.toList());
    }

    /**
     * Lista vinhos dentro de uma faixa de preço.
     * @param min preço mínimo
     * @param max preço máximo
     * @return lista de vinhos na faixa
     */
    public List<Vinho> listarVinhosPorFaixaPreco(double min, double max) {
        return vinhoRepo.findAll().stream()
                .filter(v -> v.getPreco() >= min && v.getPreco() <= max)
                .collect(Collectors.toList());
    }

    /**
     * Pesquisa vinhos cujo nome contém a string fornecida (case insensitive).
     * @param nome texto a pesquisar
     * @return lista de vinhos correspondentes
     */
    public List<Vinho> pesquisarVinhoPorNome(String nome) {
        String nomeLower = nome.toLowerCase();
        return vinhoRepo.findAll().stream()
                .filter(v -> v.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

    /**
     * Pesquisa avançada por múltiplos critérios simultaneamente.
     * Parâmetros nulos ou vazios são ignorados.
     */
    public List<Vinho> pesquisaAvancada(String nome, String tipo, String regiao,
                                        double precoMin, double precoMax) {
        return vinhoRepo.findAll().stream()
                .filter(v -> nome == null || nome.isEmpty()
                        || v.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(v -> tipo == null || tipo.isEmpty()
                        || v.getTipo().equalsIgnoreCase(tipo))
                .filter(v -> regiao == null || regiao.isEmpty()
                        || v.getRegiao().equalsIgnoreCase(regiao))
                .filter(v -> precoMin <= 0 || v.getPreco() >= precoMin)
                .filter(v -> precoMax <= 0 || v.getPreco() <= precoMax)
                .collect(Collectors.toList());
    }

    /**
     * Adiciona um vinho ao carrinho com a quantidade especificada.
     * Verifica se há stock suficiente.
     */
    public void adicionarAoCarrinho(Vinho vinho, int quantidade, List<ItemCarrinho> carrinho) {
        if (vinho == null) throw new IllegalArgumentException("Vinho inválido.");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que 0.");
        if (vinho.getQuantidadeStock() < quantidade)
            throw new IllegalArgumentException("Stock insuficiente. Disponível: "
                    + vinho.getQuantidadeStock());

        // Verifica se o vinho já está no carrinho
        for (ItemCarrinho item : carrinho) {
            if (item.getVinho().getId() == vinho.getId()) {
                int novaQtd = item.getQuantidade() + quantidade;
                if (vinho.getQuantidadeStock() < novaQtd)
                    throw new IllegalArgumentException("Stock insuficiente para " + novaQtd + " unidades.");
                item.setQuantidade(novaQtd);
                return;
            }
        }
        carrinho.add(new ItemCarrinho(vinho, quantidade));
    }

    /**
     * Remove um vinho do carrinho pelo ID.
     */
    public void removerDoCarrinho(int vinhoId, List<ItemCarrinho> carrinho) {
        boolean removido = carrinho.removeIf(item -> item.getVinho().getId() == vinhoId);
        if (!removido) throw new IllegalArgumentException("Vinho não encontrado no carrinho.");
    }

    /**
     * Calcula o total do carrinho.
     * @return soma dos subtotais de todos os itens
     */
    public double calcularTotalCarrinho(List<ItemCarrinho> carrinho) {
        return carrinho.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    /**
     * Finaliza a compra: cria a venda, baixa o stock e guarda tudo.
     * @param cliente cliente que compra
     * @param carrinho itens a comprar
     * @param metodoPagamento método de pagamento
     * @return venda criada
     */
    public Venda finalizarCompra(Cliente cliente, List<ItemCarrinho> carrinho,
                                  String metodoPagamento) {
        if (carrinho == null || carrinho.isEmpty())
            throw new IllegalArgumentException("Carrinho está vazio.");

        // Baixa o stock de cada vinho
        List<Vinho> todosVinhos = vinhoRepo.findAll();
        for (ItemCarrinho item : carrinho) {
            Vinho v = item.getVinho();
            int novoStock = v.getQuantidadeStock() - item.getQuantidade();
            if (novoStock < 0) throw new IllegalStateException("Stock insuficiente para: " + v.getNome());
            v.setQuantidadeStock(novoStock);
        }
        vinhoRepo.save(todosVinhos);

        // Cria a venda
        List<Venda> vendas = vendaRepo.findAll();
        int novoId = vendas.stream().mapToInt(Venda::getId).max().orElse(0) + 1;
        Venda venda = new Venda(novoId, cliente, new ArrayList<>(carrinho), metodoPagamento, null);
        vendas.add(venda);
        vendaRepo.save(vendas);

        // Atualiza histórico do cliente
        cliente.adicionarCompra(venda);
        List<Cliente> clientes = clienteRepo.findAll();
        clienteRepo.save(clientes);

        // Regista no log
        registarLog(venda);

        carrinho.clear();
        return venda;
    }

    /** Regista a venda no ficheiro log.txt */
    private void registarLog(Venda venda) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("log.txt", true))) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            pw.printf("[%s] VENDA #%d | Cliente: %s | Total: %.2f€ | Pagamento: %s%n",
                    LocalDateTime.now().format(fmt),
                    venda.getId(),
                    venda.getCliente() != null ? venda.getCliente().getNome() : "N/A",
                    venda.getTotalVenda(),
                    venda.getMetodoPagamento());
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível registar no log: " + e.getMessage());
        }
    }
}

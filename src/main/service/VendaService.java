/**
 * VendaService — serviço de gestão de vendas.
 * Consulta, filtragem e relatórios de vendas.
 */
package main.service;

import main.model.Venda;
import main.model.Vinho;
import main.repository.VendaRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VendaService {

    private final VendaRepository vendaRepo;

    public VendaService(VendaRepository vendaRepo) {
        this.vendaRepo = vendaRepo;
    }

    /**
     * Regista uma venda no repositório.
     * @param venda venda a registar
     */
    public void registarVenda(Venda venda) {
        List<Venda> vendas = vendaRepo.findAll();
        vendas.add(venda);
        vendaRepo.save(vendas);
    }

    /**
     * Lista vendas num intervalo de datas.
     * @param inicio data de início (inclusive)
     * @param fim data de fim (inclusive)
     * @return lista de vendas no período
     */
    public List<Venda> listarVendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        return vendaRepo.findAll().stream()
                .filter(v -> {
                    LocalDate data = v.getDataVenda().toLocalDate();
                    return !data.isBefore(inicio) && !data.isAfter(fim);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula a faturação total de todas as vendas concluídas.
     * @return total faturado em euros
     */
    public double calcularFaturacaoTotal() {
        return vendaRepo.findAll().stream()
                .filter(v -> Venda.STATUS_CONCLUIDA.equals(v.getStatus()))
                .mapToDouble(Venda::getTotalVenda)
                .sum();
    }

    /**
     * Calcula a faturação de um mês e ano específicos.
     * @param mes mês (1-12)
     * @param ano ano com 4 dígitos
     * @return total faturado no mês
     */
    public double calcularFaturacaoMensal(int mes, int ano) {
        return vendaRepo.findAll().stream()
                .filter(v -> Venda.STATUS_CONCLUIDA.equals(v.getStatus()))
                .filter(v -> v.getDataVenda().getMonthValue() == mes
                        && v.getDataVenda().getYear() == ano)
                .mapToDouble(Venda::getTotalVenda)
                .sum();
    }

    /**
     * Retorna os vinhos mais vendidos com a quantidade total vendida.
     * @return mapa de Vinho → quantidade total vendida, ordenado por quantidade (desc)
     */
    public Map<Vinho, Integer> produtosMaisVendidos() {
        Map<Vinho, Integer> contagem = new HashMap<>();
        for (Venda venda : vendaRepo.findAll()) {
            if (Venda.STATUS_CANCELADA.equals(venda.getStatus())) continue;
            for (var item : venda.getItens()) {
                contagem.merge(item.getVinho(), item.getQuantidade(), Integer::sum);
            }
        }
        // Ordena por quantidade descendente
        Map<Vinho, Integer> ordenado = new LinkedHashMap<>();
        contagem.entrySet().stream()
                .sorted(Map.Entry.<Vinho, Integer>comparingByValue().reversed())
                .forEach(e -> ordenado.put(e.getKey(), e.getValue()));
        return ordenado;
    }

    /**
     * Cancela uma venda pelo ID.
     * @param id identificador da venda
     * @return venda cancelada
     */
    public Venda cancelarVenda(int id) {
        Venda venda = vendaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda não encontrada: " + id));
        if (Venda.STATUS_CANCELADA.equals(venda.getStatus()))
            throw new IllegalStateException("Venda já está cancelada.");
        venda.setStatus(Venda.STATUS_CANCELADA);
        vendaRepo.save(vendaRepo.findAll());
        return venda;
    }

    /**
     * Lista todas as vendas de um cliente.
     * @param clienteId ID do cliente
     * @return lista de vendas do cliente
     */
    public List<Venda> listarVendasPorCliente(int clienteId) {
        return vendaRepo.findAll().stream()
                .filter(v -> v.getCliente() != null && v.getCliente().getId() == clienteId)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas as vendas registadas.
     * @return lista de todas as vendas
     */
    public List<Venda> listarTodasVendas() {
        return vendaRepo.findAll();
    }
}

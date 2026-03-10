/**
 * EquipaService — serviço de gestão da equipa de funcionários.
 */
package main.service;

import main.model.Funcionario;
import main.repository.FuncionarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EquipaService {

    private final FuncionarioRepository funcionarioRepo;

    public EquipaService(FuncionarioRepository funcionarioRepo) {
        this.funcionarioRepo = funcionarioRepo;
    }

    /**
     * Adiciona um novo funcionário.
     * @param f funcionário a adicionar
     */
    public void adicionarFuncionario(Funcionario f) {
        if (f == null) throw new IllegalArgumentException("Funcionário não pode ser nulo.");
        List<Funcionario> lista = funcionarioRepo.findAll();
        lista.add(f);
        funcionarioRepo.save(lista);
    }

    /**
     * Remove um funcionário pelo ID.
     * @param id identificador
     */
    public void removerFuncionario(int id) {
        List<Funcionario> lista = funcionarioRepo.findAll();
        boolean removido = lista.removeIf(f -> f.getId() == id);
        if (!removido) throw new IllegalArgumentException("Funcionário não encontrado: " + id);
        funcionarioRepo.save(lista);
    }

    /**
     * Atualiza os dados de um funcionário existente.
     * @param f funcionário com dados atualizados
     */
    public void atualizarFuncionario(Funcionario f) {
        List<Funcionario> lista = funcionarioRepo.findAll();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == f.getId()) {
                lista.set(i, f);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) throw new IllegalArgumentException("Funcionário não encontrado: " + f.getId());
        funcionarioRepo.save(lista);
    }

    /**
     * Lista todos os funcionários ativos.
     * @return lista de funcionários com ativo=true
     */
    public List<Funcionario> listarFuncionariosAtivos() {
        return funcionarioRepo.findAll().stream()
                .filter(Funcionario::isAtivo)
                .collect(Collectors.toList());
    }

    /**
     * Procura um funcionário pelo ID.
     * @param id identificador
     * @return Optional com o funcionário
     */
    public Optional<Funcionario> buscarFuncionarioPorId(int id) {
        return funcionarioRepo.findById(id);
    }

    /**
     * Calcula a massa salarial total dos funcionários ativos.
     * @return soma dos salários dos funcionários ativos
     */
    public double calcularMassaSalarial() {
        return funcionarioRepo.findAll().stream()
                .filter(Funcionario::isAtivo)
                .mapToDouble(Funcionario::getSalario)
                .sum();
    }

    /**
     * Desativa um funcionário (não o remove, apenas marca como inativo).
     * @param id identificador do funcionário
     */
    public void desativarFuncionario(int id) {
        Funcionario f = funcionarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado: " + id));
        f.setAtivo(false);
        funcionarioRepo.save(funcionarioRepo.findAll());
    }

    /**
     * Gera um ID único para um novo funcionário.
     * @return próximo ID disponível
     */
    public int gerarProximoId() {
        return funcionarioRepo.findAll().stream()
                .mapToInt(Funcionario::getId).max().orElse(0) + 1;
    }
}

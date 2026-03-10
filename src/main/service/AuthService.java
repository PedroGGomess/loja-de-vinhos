/**
 * AuthService — serviço de autenticação.
 * Gere login/logout de clientes e gerentes/funcionários.
 */
package main.service;

import main.model.Cliente;
import main.model.Funcionario;
import main.model.Gerente;
import main.model.Pessoa;
import main.repository.ClienteRepository;
import main.repository.FuncionarioRepository;

import java.util.Optional;

public class AuthService {

    public static final String TIPO_CLIENTE     = "CLIENTE";
    public static final String TIPO_FUNCIONARIO = "FUNCIONARIO";
    public static final String TIPO_GERENTE     = "GERENTE";

    private final ClienteRepository clienteRepo;
    private final FuncionarioRepository funcionarioRepo;

    private Cliente clienteAtual;
    private Funcionario funcionarioAtual;
    private String tipoUtilizador;

    public AuthService(ClienteRepository clienteRepo, FuncionarioRepository funcionarioRepo) {
        this.clienteRepo = clienteRepo;
        this.funcionarioRepo = funcionarioRepo;
    }

    /**
     * Realiza login de um cliente com email e password.
     * @return true se autenticado com sucesso
     */
    public boolean loginCliente(String email, String password) {
        Optional<Cliente> opt = clienteRepo.findByEmail(email);
        if (opt.isPresent() && opt.get().verificarPassword(password)) {
            clienteAtual = opt.get();
            tipoUtilizador = TIPO_CLIENTE;
            return true;
        }
        return false;
    }

    /**
     * Realiza login de um gerente com email e password.
     * @return true se autenticado com sucesso
     */
    public boolean loginGerente(String email, String password) {
        Optional<Funcionario> opt = funcionarioRepo.findByEmail(email);
        if (opt.isPresent() && opt.get().verificarPassword(password)) {
            funcionarioAtual = opt.get();
            tipoUtilizador = (funcionarioAtual instanceof Gerente) ? TIPO_GERENTE : TIPO_FUNCIONARIO;
            return true;
        }
        return false;
    }

    /** Termina a sessão atual. */
    public void logout() {
        clienteAtual = null;
        funcionarioAtual = null;
        tipoUtilizador = null;
    }

    /** Verifica se existe um utilizador autenticado. */
    public boolean isAutenticado() {
        return tipoUtilizador != null;
    }

    /**
     * Retorna o tipo do utilizador autenticado.
     * @return "CLIENTE", "FUNCIONARIO" ou "GERENTE", ou null se não autenticado
     */
    public String getTipoUtilizador() {
        return tipoUtilizador;
    }

    /** Retorna o cliente atualmente autenticado. */
    public Cliente getClienteAtual() {
        return clienteAtual;
    }

    /** Retorna o gerente atualmente autenticado (ou null se for funcionário). */
    public Gerente getGerenteAtual() {
        if (funcionarioAtual instanceof Gerente) return (Gerente) funcionarioAtual;
        return null;
    }

    /** Retorna o funcionário atualmente autenticado. */
    public Funcionario getFuncionarioAtual() {
        return funcionarioAtual;
    }
}

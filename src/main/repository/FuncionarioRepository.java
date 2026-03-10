/**
 * FuncionarioRepository — persistência de funcionários em ficheiro "funcionarios.dat".
 */
package main.repository;

import main.model.Funcionario;
import main.model.Gerente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioRepository implements Repository<Funcionario> {

    private static final String FICHEIRO = "funcionarios.dat";

    private List<Funcionario> cache;

    public FuncionarioRepository() {
        this.cache = load();
    }

    @Override
    public void save(List<Funcionario> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHEIRO))) {
            oos.writeObject(new ArrayList<>(items));
            this.cache = new ArrayList<>(items);
        } catch (IOException e) {
            System.err.println("Erro ao guardar funcionários: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Funcionario> load() {
        File f = new File(FICHEIRO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Funcionario> lista = (List<Funcionario>) ois.readObject();
            this.cache = lista;
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar funcionários: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Funcionario> findById(int id) {
        return findAll().stream().filter(f -> f.getId() == id).findFirst();
    }

    @Override
    public List<Funcionario> findAll() {
        if (cache == null) cache = load();
        return cache;
    }

    /** Procura funcionário (incluindo gerentes) por email. */
    public Optional<Funcionario> findByEmail(String email) {
        return findAll().stream()
                .filter(f -> f.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /** Retorna apenas os gerentes. */
    public List<Gerente> findAllGerentes() {
        List<Gerente> gerentes = new ArrayList<>();
        for (Funcionario f : findAll()) {
            if (f instanceof Gerente) gerentes.add((Gerente) f);
        }
        return gerentes;
    }
}

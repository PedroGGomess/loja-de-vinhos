/**
 * ClienteRepository — persistência de clientes em ficheiro "clientes.dat".
 */
package main.repository;

import main.model.Cliente;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository implements Repository<Cliente> {

    private static final String FICHEIRO = "clientes.dat";

    private List<Cliente> cache;

    public ClienteRepository() {
        this.cache = load();
    }

    @Override
    public void save(List<Cliente> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHEIRO))) {
            oos.writeObject(new ArrayList<>(items));
            this.cache = new ArrayList<>(items);
        } catch (IOException e) {
            System.err.println("Erro ao guardar clientes: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Cliente> load() {
        File f = new File(FICHEIRO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Cliente> lista = (List<Cliente>) ois.readObject();
            this.cache = lista;
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar clientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Cliente> findById(int id) {
        return findAll().stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public List<Cliente> findAll() {
        if (cache == null) cache = load();
        return cache;
    }

    /** Procura cliente por email. */
    public Optional<Cliente> findByEmail(String email) {
        return findAll().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}

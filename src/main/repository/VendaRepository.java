/**
 * VendaRepository — persistência de vendas em ficheiro "vendas.dat".
 */
package main.repository;

import main.model.Venda;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendaRepository implements Repository<Venda> {

    private static final String FICHEIRO = "vendas.dat";

    private List<Venda> cache;

    public VendaRepository() {
        this.cache = load();
    }

    @Override
    public void save(List<Venda> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHEIRO))) {
            oos.writeObject(new ArrayList<>(items));
            this.cache = new ArrayList<>(items);
        } catch (IOException e) {
            System.err.println("Erro ao guardar vendas: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Venda> load() {
        File f = new File(FICHEIRO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Venda> lista = (List<Venda>) ois.readObject();
            this.cache = lista;
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar vendas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Venda> findById(int id) {
        return findAll().stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public List<Venda> findAll() {
        if (cache == null) cache = load();
        return cache;
    }
}

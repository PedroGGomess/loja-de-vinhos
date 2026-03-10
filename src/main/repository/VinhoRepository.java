/**
 * VinhoRepository — persistência de vinhos em ficheiro "vinhos.dat".
 * Usa serialização Java para guardar e carregar a lista de vinhos.
 */
package main.repository;

import main.model.Vinho;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VinhoRepository implements Repository<Vinho> {

    private static final String FICHEIRO = "vinhos.dat";

    private List<Vinho> cache;

    public VinhoRepository() {
        this.cache = load();
    }

    @Override
    public void save(List<Vinho> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FICHEIRO))) {
            oos.writeObject(new ArrayList<>(items));
            this.cache = new ArrayList<>(items);
        } catch (IOException e) {
            System.err.println("Erro ao guardar vinhos: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Vinho> load() {
        File f = new File(FICHEIRO);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            List<Vinho> lista = (List<Vinho>) ois.readObject();
            this.cache = lista;
            return lista;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar vinhos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Vinho> findById(int id) {
        return findAll().stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public List<Vinho> findAll() {
        if (cache == null) cache = load();
        return cache;
    }
}

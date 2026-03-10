/**
 * Interface genérica Repository<T> — define operações CRUD básicas para persistência.
 */
package main.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    /** Guarda a lista completa no ficheiro de dados. */
    void save(List<T> items);

    /** Carrega a lista a partir do ficheiro de dados. */
    List<T> load();

    /** Procura um elemento pelo seu ID. */
    Optional<T> findById(int id);

    /** Retorna todos os elementos. */
    List<T> findAll();
}

/**
 * Classe abstrata Pessoa — representa uma pessoa no sistema (base para Cliente e Funcionario).
 * Contém os atributos comuns a qualquer pessoa.
 */
package main.model;

import java.io.Serializable;

public abstract class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private String password;

    /** Construtor completo */
    public Pessoa(int id, String nome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = hashPassword(password);
    }

    /** Construtor padrão */
    public Pessoa() {}

    /**
     * Hash simples da password (soma dos chars × posição).
     * @param password a password em texto simples
     * @return representação em hash da password
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        long hash = 0;
        for (int i = 0; i < password.length(); i++) {
            hash = (hash * 31 + password.charAt(i)) & 0xFFFFFFFFL;
        }
        return Long.toHexString(hash);
    }

    /** Verifica se a password fornecida corresponde à guardada. */
    public boolean verificarPassword(String password) {
        return this.password.equals(hashPassword(password));
    }

    // --- Getters e Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = hashPassword(password); }

    /** Define a password já em hash (para deserialização). */
    public void setPasswordHash(String hash) { this.password = hash; }

    @Override
    public String toString() {
        return "Pessoa{id=" + id + ", nome='" + nome + "', email='" + email + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa)) return false;
        Pessoa p = (Pessoa) o;
        return id == p.id && email.equals(p.email);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id) * 31 + email.hashCode();
    }
}

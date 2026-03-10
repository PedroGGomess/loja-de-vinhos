/**
 * UIHelper — utilitários partilhados pelos menus do terminal.
 * Métodos de formatação de texto e leitura de input do utilizador.
 */
package main.ui;

import java.util.Scanner;

public final class UIHelper {

    private UIHelper() { /* utilitário, não instanciável */ }

    /**
     * Trunca um texto ao tamanho máximo, acrescentando "…" se necessário.
     * @param texto texto de entrada
     * @param max número máximo de caracteres
     * @return texto truncado
     */
    public static String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max - 1) + "…" : texto;
    }

    /**
     * Lê um número inteiro do teclado, repetindo o prompt em caso de erro.
     * @param sc Scanner ativo
     * @param prompt texto do prompt
     * @return inteiro lido
     */
    public static int lerInteiro(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("❌ Número inválido."); }
        }
    }

    /**
     * Lê um número decimal do teclado, repetindo o prompt em caso de erro.
     * Aceita vírgula ou ponto como separador decimal.
     * @param sc Scanner ativo
     * @param prompt texto do prompt
     * @return double lido
     */
    public static double lerDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(sc.nextLine().trim().replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("❌ Valor inválido."); }
        }
    }
}

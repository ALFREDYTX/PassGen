package com.safepass.gen;

import java.util.Random;

public class GeneradorPassword {

    public String generar(int longitud, boolean usarSimbolos, boolean usarNumeros, boolean usarMayusculas) {
        String letras = "abcdefghijklmnopqrstuvwxyz";
        String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeros = "0123456789";
        String simbolos = "!@#$%^&*()-_=+[]{}";

        StringBuilder bancoCaracteres = new StringBuilder(letras);
        if (usarMayusculas) bancoCaracteres.append(mayus);
        if (usarNumeros) bancoCaracteres.append(numeros);
        if (usarSimbolos) bancoCaracteres.append(simbolos);

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < longitud; i++) {
            int indice = random.nextInt(bancoCaracteres.length());
            password.append(bancoCaracteres.charAt(indice));
        }

        return password.toString();
    }
}
package com.safepass.gen;

import java.util.Random;
import com.safepass.excepciones.ConfiguracionInvalidaException;

public class GeneradorPassword {

    private String letras = "abcdefghijklmnopqrstuvwxyz";
    private String mayus = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String numeros = "0123456789";
    private String simbolos = "!@#$%^&*()-_=+[]{}";

    public String getLetras() {
        return letras;
    }

    public void setLetras(String letras) {
        this.letras = letras;
    }

    public String getMayus() {
        return mayus;
    }

    public void setMayus(String mayus) {
        this.mayus = mayus;
    }

    public String getNumeros() {
        return numeros;
    }

    public void setNumeros(String numeros) {
        this.numeros = numeros;
    }

    public String getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(String simbolos) {
        this.simbolos = simbolos;
    }

    public String generar(Configuracion config) throws ConfiguracionInvalidaException {
        if (config.getLongitud() <= 0) {
            throw new ConfiguracionInvalidaException("La longitud debe ser mayor a 0");
        }
        if (!config.isUsarMayusculas() && !config.isUsarNumeros() && !config.isUsarSimbolos()) {
        }

        return generar(config.getLongitud(), config.isUsarSimbolos(), config.isUsarNumeros(), config.isUsarMayusculas());
    }

    public String generar(int longitud, boolean usarSimbolos, boolean usarNumeros, boolean usarMayusculas) {
        StringBuilder bancoCaracteres = new StringBuilder(this.letras);
        if (usarMayusculas) bancoCaracteres.append(this.mayus);
        if (usarNumeros) bancoCaracteres.append(this.numeros);
        if (usarSimbolos) bancoCaracteres.append(this.simbolos);

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < longitud; i++) {
            int indice = random.nextInt(bancoCaracteres.length());
            password.append(bancoCaracteres.charAt(indice));
        }

        return password.toString();
    }
}
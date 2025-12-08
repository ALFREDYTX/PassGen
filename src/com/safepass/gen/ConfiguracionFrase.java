package com.safepass.gen;

import java.io.Serializable;

public class ConfiguracionFrase implements Serializable {
    private int numeroPalabras;
    private String separador;

    public ConfiguracionFrase() {
        this.numeroPalabras = 4;
        this.separador = "-";
    }

    public ConfiguracionFrase(int numeroPalabras, String separador) {
        this.numeroPalabras = numeroPalabras;
        this.separador = separador;
    }

    public int getNumeroPalabras() {
        return numeroPalabras;
    }

    public void setNumeroPalabras(int numeroPalabras) {
        this.numeroPalabras = numeroPalabras;
    }

    public String getSeparador() {
        return separador;
    }

    public void setSeparador(String separador) {
        this.separador = separador;
    }

    @Override
    public String toString() {
        return "ConfiguracionFrase [numeroPalabras=" + numeroPalabras + ", separador=" + separador + "]";
    }
}

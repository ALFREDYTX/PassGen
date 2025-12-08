package com.safepass.gen;

import java.io.Serializable;

public class ConfiguracionPassword implements Serializable {
    private int longitud;
    private boolean usarSimbolos;
    private boolean usarNumeros;
    private boolean usarMayusculas;

    public ConfiguracionPassword() {
        // Valores por defecto
        this.longitud = 8;
        this.usarSimbolos = false;
        this.usarNumeros = true;
        this.usarMayusculas = true;
    }

    public ConfiguracionPassword(int longitud, boolean usarSimbolos, boolean usarNumeros, boolean usarMayusculas) {
        this.longitud = longitud;
        this.usarSimbolos = usarSimbolos;
        this.usarNumeros = usarNumeros;
        this.usarMayusculas = usarMayusculas;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }

    public boolean isUsarSimbolos() {
        return usarSimbolos;
    }

    public void setUsarSimbolos(boolean usarSimbolos) {
        this.usarSimbolos = usarSimbolos;
    }

    public boolean isUsarNumeros() {
        return usarNumeros;
    }

    public void setUsarNumeros(boolean usarNumeros) {
        this.usarNumeros = usarNumeros;
    }

    public boolean isUsarMayusculas() {
        return usarMayusculas;
    }

    public void setUsarMayusculas(boolean usarMayusculas) {
        this.usarMayusculas = usarMayusculas;
    }

    @Override
    public String toString() {
        return "ConfiguracionPassword [longitud=" + longitud + ", usarSimbolos=" + usarSimbolos + ", usarNumeros=" + usarNumeros
                + ", usarMayusculas=" + usarMayusculas + "]";
    }
}

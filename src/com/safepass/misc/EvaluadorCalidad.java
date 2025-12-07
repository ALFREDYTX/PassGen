package com.safepass.misc;

public class EvaluadorCalidad {

    private int longitudMinima = 8;
    private int longitudIdeal = 12;

    public int getLongitudMinima() {
        return longitudMinima;
    }

    public void setLongitudMinima(int longitudMinima) {
        this.longitudMinima = longitudMinima;
    }

    public int getLongitudIdeal() {
        return longitudIdeal;
    }

    public void setLongitudIdeal(int longitudIdeal) {
        this.longitudIdeal = longitudIdeal;
    }

    public NivelSeguridad evaluar(String password) {

        int puntuacion = 0;

        if (password.length() >= longitudMinima) puntuacion++;
        if (password.length() >= longitudIdeal) puntuacion++;
        if (password.matches(".*[A-Z].*")) puntuacion++;
        if (password.matches(".*[0-9].*")) puntuacion++;
        if (password.matches(".*[!@#$%^&*].*")) puntuacion++;

        if (puntuacion <= 1) return NivelSeguridad.MUY_DEBIL;
        if (puntuacion == 2) return NivelSeguridad.DEBIL;
        if (puntuacion == 3) return NivelSeguridad.DECENTE;
        if (puntuacion == 4) return NivelSeguridad.BUENA;

        return NivelSeguridad.EXCELENTE;
    }
}
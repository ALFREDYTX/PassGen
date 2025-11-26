package com.safepass.misc;

public class EvaluadorCalidad {
    public NivelSeguridad evaluar(String password) {

        int puntuacion = 0;

        if (password.length() >= 8) puntuacion++;
        if (password.length() >= 12) puntuacion++;
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
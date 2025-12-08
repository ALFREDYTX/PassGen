package com.safepass.gen;

import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneradorFrase {
    private List<String> diccionario;
    private SecureRandom random = new SecureRandom();
    private static final String ARCHIVO_DICCIONARIO = "diccionario.txt";

    public GeneradorFrase() {
        diccionario = new ArrayList<>();
        cargarDiccionario();
    }

    private void cargarDiccionario() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_DICCIONARIO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    diccionario.add(linea.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar diccionario.txt: " + e.getMessage());
            // Fallback por si falla la carga
            diccionario.add("diccionario");
            diccionario.add("no");
            diccionario.add("encontrado");
            diccionario.add("error");
        }
    }

    public String generar(ConfiguracionFrase config) {
        return generar(config.getNumeroPalabras(), config.getSeparador());
    }

    public String generar(int numeroPalabras, String separador) {
        if (diccionario.isEmpty()) return "error-diccionario-vacio";

        if (numeroPalabras < 1) numeroPalabras = 4;
        if (separador == null) separador = "-";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroPalabras; i++) {
            int index = random.nextInt(diccionario.size());
            sb.append(diccionario.get(index));
            if (i < numeroPalabras - 1) {
                sb.append(separador);
            }
        }
        return sb.toString();
    }
}

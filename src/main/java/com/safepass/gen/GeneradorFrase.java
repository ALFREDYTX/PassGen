package com.safepass.gen;

import net.datafaker.Faker;
import java.util.Locale;

public class GeneradorFrase {
    private Faker faker;

    public GeneradorFrase() {
        this.faker = new Faker(new Locale("es"));
    }

    public String generar(ConfiguracionFrase config) {
        return generar(config.getNumeroPalabras(), config.getSeparador());
    }

    public String generar(int numeroPalabras, String separador) {
        if (numeroPalabras < 1) numeroPalabras = 4;
        if (separador == null) separador = "-";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroPalabras; i++) {
            String palabra;
            int tipo = faker.random().nextInt(3);
            switch (tipo) {
                case 0:
                    palabra = faker.animal().name();
                    break;
                case 1:
                    palabra = faker.color().name();
                    break;
                default:
                    palabra = faker.lorem().word();
                    break;
            }
            
            palabra = palabra.replaceAll("\\s+", "").toLowerCase();
            
            sb.append(palabra);
            if (i < numeroPalabras - 1) {
                sb.append(separador);
            }
        }
        return sb.toString();
    }
}

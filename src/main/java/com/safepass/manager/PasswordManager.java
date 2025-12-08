package com.safepass.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.safepass.excepciones.ManagerException;
import com.safepass.crypto.EncriptadorCSV;
import com.safepass.crypto.DesencriptadorCSV;

public class PasswordManager implements IManager<PasswordEntry> {

    private static PasswordManager instance;
    
    private List<PasswordEntry> misPasswords;

    private PasswordManager() {
        this.misPasswords = new ArrayList<>();
    }

    public static PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    @Override
    public void agregar(PasswordEntry elemento) throws ManagerException {
        if (elemento == null) {
            throw new ManagerException("No se puede agregar una entrada nula");
        }
        for (PasswordEntry entry : misPasswords) {
            if (entry.equals(elemento)) {
                throw new ManagerException("La entrada para " + elemento.getSitio() + " ya existe");
            }
        }
        misPasswords.add(elemento);
        System.out.println("Entrada agregada: " + elemento.getSitio());
    }

    @Override
    public void eliminar(PasswordEntry elemento) throws ManagerException {
        if (!misPasswords.contains(elemento)) {
            throw new ManagerException("No se puede eliminar: la entrada no existe");
        }
        misPasswords.remove(elemento);
        System.out.println("Entrada eliminada: " + elemento.getSitio());
    }

    @Override
    public List<PasswordEntry> listar() {
        return misPasswords;
    }

    private String secretKey = "miSecreto123";



    @Override
    public void guardarDatos(String archivo) throws ManagerException {
        try {
            String archivoTemp = "temp_" + archivo;
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivoTemp))) {
                for (PasswordEntry entry : misPasswords) {
                    writer.println(entry.getSitio() + "," +
                                   entry.getUsuario() + "," +
                                   entry.getPassword() + "," +
                                   entry.getCategoria() + "," +
                                   entry.getFechaCreacion());
                }
            }

            EncriptadorCSV encriptador = new EncriptadorCSV();
            encriptador.procesar(archivoTemp, archivo, secretKey);

            new File(archivoTemp).delete();

            System.out.println("Datos guardados y encriptados en " + archivo);

        } catch (Exception e) {
            throw new ManagerException("Error al guardar datos: " + e.getMessage());
        }
    }

    @Override
    public void cargarDatos(String archivo) throws ManagerException {
        File f = new File(archivo);
        if (f.exists()) {
            try {
                String archivoTemp = "temp_dec_" + archivo;
                DesencriptadorCSV desencriptador = new DesencriptadorCSV();
                desencriptador.procesar(archivo, archivoTemp, secretKey);

                try (BufferedReader br = new BufferedReader(new FileReader(archivoTemp))) {
                    String line;
                    misPasswords.clear();
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length >= 4) {
                            String sitio = parts[0];
                            String usuario = parts[1];
                            String password = parts[2];
                            Categoria categoria = Categoria.valueOf(parts[3]);
                            java.time.LocalDate fecha = java.time.LocalDate.now();
                            
                            if (parts.length >= 5) {
                                try {
                                    fecha = java.time.LocalDate.parse(parts[4]);
                                } catch (Exception e) {
                                }
                            }
                            
                            misPasswords.add(new PasswordEntry(sitio, usuario, password, categoria, fecha));
                        }
                    }
                }

                new File(archivoTemp).delete();

                System.out.println("Datos cargados y desencriptados de " + archivo);

            } catch (Exception e) {
                throw new ManagerException("Error al cargar datos: " + e.getMessage());
            }
        }
    }

    public void modificar(int indice, PasswordEntry nuevaEntrada) throws ManagerException {
        if (indice < 0 || indice >= misPasswords.size()) {
            throw new ManagerException("Índice inválido");
        }
        misPasswords.set(indice, nuevaEntrada);
        System.out.println("Entrada modificada exitosamente.");
    }
}

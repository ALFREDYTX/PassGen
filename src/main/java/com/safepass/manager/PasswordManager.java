package com.safepass.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.safepass.excepciones.ManagerException;

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

    private javax.crypto.spec.SecretKeySpec generarClave(String password) throws Exception {
        java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        return new javax.crypto.spec.SecretKeySpec(key, "AES");
    }

    @Override
    public void guardarDatos(String archivo) throws ManagerException {
        try {
            StringBuilder sb = new StringBuilder();
            for (PasswordEntry entry : misPasswords) {
                sb.append(entry.getSitio()).append(",")
                  .append(entry.getUsuario()).append(",")
                  .append(entry.getPassword()).append(",")
                  .append(entry.getCategoria()).append(",")
                  .append(entry.getFechaCreacion()).append("\n");
            }
            byte[] inputBytes = sb.toString().getBytes("UTF-8");

            javax.crypto.spec.SecretKeySpec key = generarClave(secretKey);
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] outputBytes = cipher.doFinal(inputBytes);

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(outputBytes);
            }
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
                FileInputStream fis = new FileInputStream(archivo);
                byte[] inputBytes = new byte[(int) f.length()];
                fis.read(inputBytes);
                fis.close();

                javax.crypto.spec.SecretKeySpec key = generarClave(secretKey);
                javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES");
                cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
                byte[] decryptedBytes = cipher.doFinal(inputBytes);
                
                String csvContent = new String(decryptedBytes, "UTF-8");

                misPasswords.clear();
                String[] lines = csvContent.split("\n");
                for (String line : lines) {
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

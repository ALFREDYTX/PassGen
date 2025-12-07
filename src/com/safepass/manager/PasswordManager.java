package com.safepass.manager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.safepass.excepciones.ManagerException;

public class PasswordManager implements IManager<PasswordEntry> {

    // Singleton
    private static PasswordManager instance;
    
    // Agregación/Colección
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
        // Validación simple: no duplicar sitio+usuario
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

    @Override
    public void guardarDatos(String archivo) throws ManagerException {
        try {
            FileOutputStream fos = new FileOutputStream(archivo);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(misPasswords);
            oos.close();
            fos.close();
            System.out.println("Datos guardados en " + archivo);
        } catch (IOException e) {
            throw new ManagerException("Error al guardar datos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void cargarDatos(String archivo) throws ManagerException {
        File f = new File(archivo);
        if (f.exists()) {
            try {
                FileInputStream fis = new FileInputStream(archivo);
                ObjectInputStream ois = new ObjectInputStream(fis);
                misPasswords = (List<PasswordEntry>) ois.readObject();
                ois.close();
                fis.close();
                System.out.println("Datos cargados de " + archivo);
            } catch (IOException | ClassNotFoundException e) {
                throw new ManagerException("Error al cargar datos: " + e.getMessage());
            }
        }
    }
}

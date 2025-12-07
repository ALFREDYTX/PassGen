package com.safepass.manager;

import java.util.List;
import com.safepass.excepciones.ManagerException;

public interface IManager<T> {
    void agregar(T elemento) throws ManagerException;
    void eliminar(T elemento) throws ManagerException;
    List<T> listar();
    void guardarDatos(String archivo) throws ManagerException;
    void cargarDatos(String archivo) throws ManagerException;
}

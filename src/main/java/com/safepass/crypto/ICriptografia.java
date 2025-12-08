package com.safepass.crypto;

public interface ICriptografia {
    void procesar(String archivoEntrada, String archivoSalida, String password) throws Exception;
}

package com.safepass.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class DesencriptadorCSV extends CriptografiaBase implements ICriptografia {

    @Override
    public void procesar(String archivoEntrada, String archivoSalida, String password) throws Exception {
        SecretKeySpec secretKey = generarClave(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream inputStream = new FileInputStream(archivoEntrada);
        byte[] inputBytes = new byte[(int) new java.io.File(archivoEntrada).length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream outputStream = new FileOutputStream(archivoSalida);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        System.out.println("Archivo desencriptado exitosamente: " + archivoSalida);
    }
}

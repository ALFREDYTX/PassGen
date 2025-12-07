import com.safepass.gen.GeneradorPassword;
import com.safepass.gen.Configuracion;
import com.safepass.misc.EvaluadorCalidad;
import com.safepass.misc.NivelSeguridad;
import com.safepass.excepciones.ConfiguracionInvalidaException;
import com.safepass.crypto.EncriptadorCSV;
import com.safepass.crypto.DesencriptadorCSV;
import com.safepass.manager.PasswordManager;
import com.safepass.manager.PasswordEntry;
import com.safepass.manager.Categoria;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Crear configuración
        Configuracion config = new Configuracion();
        config.setLongitud(12);
        config.setUsarSimbolos(true);
        config.setUsarNumeros(true);
        config.setUsarMayusculas(true);

        System.out.println("Configuración usada: " + config.toString());

        GeneradorPassword generador = new GeneradorPassword();
        EvaluadorCalidad evaluador = new EvaluadorCalidad();

        try {
            // 2. Generar contraseña
            String password = generador.generar(config);
            System.out.println("Contraseña generada: " + password);

            // 3. Evaluar calidad
            NivelSeguridad nivel = evaluador.evaluar(password);
            System.out.println("Nivel de seguridad: " + nivel);

            // 4. Guardar en archivo (Archivos y flujos)
            guardarPassword(password, nivel);

            // 5. Encriptar el archivo
            String claveSecreta = "miSecreto123";
            EncriptadorCSV encriptador = new EncriptadorCSV();
            encriptador.procesar("password.txt", "password.enc", claveSecreta);

            // 6. Desencriptar el archivo (prueba)
            DesencriptadorCSV desencriptador = new DesencriptadorCSV();
            desencriptador.procesar("password.enc", "password_decrypted.txt", claveSecreta);

            // --- NUEVO: GESTOR DE CONTRASEÑAS ---
            System.out.println("\n--- GESTOR DE CONTRASEÑAS ---");
            PasswordManager manager = PasswordManager.getInstance(); // Singleton

            // Crear entradas (Agregación)
            PasswordEntry entrada1 = new PasswordEntry("google.com", "usuario1", password, Categoria.PERSONAL);
            PasswordEntry entrada2 = new PasswordEntry("banco.com", "admin", "SuperSecret123!", Categoria.BANCO);

            // Agregar
            manager.agregar(entrada1);
            manager.agregar(entrada2);

            // Guardar datos (Serialización)
            manager.guardarDatos("mis_passwords.dat");

            // Listar
            System.out.println("Listado de contraseñas:");
            List<PasswordEntry> lista = manager.listar(); // Colección genérica
            for (PasswordEntry entry : lista) {
                System.out.println(entry); // toString
            }

        } catch (ConfiguracionInvalidaException e) {
            System.err.println("Error en la configuración: " + e.getMessage());
        } catch (com.safepass.excepciones.ManagerException e) {
            System.err.println("Error en el gestor de contraseñas: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método estático para guardar en archivo
    public static void guardarPassword(String password, NivelSeguridad nivel) throws IOException {
        FileWriter escritor = null;
        try {
            escritor = new FileWriter("password.txt", true); // true para append
            escritor.write("Password: " + password + " | Nivel: " + nivel + "\n");
            System.out.println("Contraseña guardada en password.txt");
        } finally {
            if (escritor != null) {
                escritor.close();
            }
        }
    }
}
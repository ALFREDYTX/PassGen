import com.safepass.gen.GeneradorPassword;
import com.safepass.gen.GeneradorFrase;
import com.safepass.gen.ConfiguracionPassword;
import com.safepass.gen.ConfiguracionFrase;
import com.safepass.misc.EvaluadorCalidad;
import com.safepass.misc.NivelSeguridad;
import com.safepass.manager.PasswordManager;
import com.safepass.manager.PasswordEntry;
import com.safepass.manager.Categoria;
import com.safepass.excepciones.ManagerException;

import java.util.Scanner;
import java.util.List;

public class Main {
    private static final String ARCHIVO_DATOS = "passwords.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordManager manager = PasswordManager.getInstance();

        // Cargar datos al inicio
        try {
            manager.cargarDatos(ARCHIVO_DATOS);
        } catch (ManagerException e) {
            System.out.println("Nota: No se pudieron cargar datos previos (" + e.getMessage() + ")");
        }

        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== SAFEPASS MENU ===");
            System.out.println("1. Generar nueva contraseña");
            System.out.println("2. Ver contraseñas guardadas");
            System.out.println("3. Modificar contraseña");
            System.out.println("4. Eliminar contraseña");
            System.out.println("5. Evaluar contraseña");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String input = scanner.nextLine();
            
            switch (input) {
                case "1":
                    opcionGenerar(scanner, manager);
                    break;
                case "2":
                    opcionListar(manager);
                    break;
                case "3":
                    opcionModificar(scanner, manager);
                    break;
                case "4":
                    opcionEliminar(scanner, manager);
                    break;
                case "5":
                    opcionEvaluar(scanner);
                    break;
                case "6":
                    salir = true;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
    }

    private static void opcionGenerar(Scanner scanner, PasswordManager manager) {
        try {
            System.out.println("\n--- Generar Contraseña ---");
            System.out.println("1. Contraseña Aleatoria (Carácteres)");
            System.out.println("2. Frase de Contraseña (Palabras)");
            System.out.print("Seleccione tipo (1/2): ");
            String tipo = scanner.nextLine();

            String password = "";

            if (tipo.equals("2")) {
                // Generación de Frase
                ConfiguracionFrase configFrase = new ConfiguracionFrase();
                
                System.out.print("Número de palabras (Enter para 4): ");
                String numStr = scanner.nextLine();
                if (!numStr.trim().isEmpty()) {
                    try {
                        configFrase.setNumeroPalabras(Integer.parseInt(numStr));
                    } catch (NumberFormatException e) {
                        System.out.println("Valor no válido, usando 4.");
                    }
                }

                System.out.print("Separador (Enter para '-'): ");
                String separador = scanner.nextLine();
                if (!separador.isEmpty()) configFrase.setSeparador(separador);

                GeneradorFrase generadorFrase = new GeneradorFrase();
                password = generadorFrase.generar(configFrase);

            } else {
                // Generación Estándar
                System.out.println("\n--- Configuración de Carácteres ---");
                ConfiguracionPassword configPass = new ConfiguracionPassword();
                
                // 1. Longitud
                System.out.print("Longitud (Enter para 12): ");
                String lenStr = scanner.nextLine();
                int longitud = 12;
                if (!lenStr.trim().isEmpty()) {
                    try {
                        longitud = Integer.parseInt(lenStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Valor no válido, usando 12.");
                    }
                }
                configPass.setLongitud(longitud);

                // 2. Opciones booleanas
                configPass.setUsarMayusculas(preguntarSiNo(scanner, "¿Incluir Mayúsculas?"));
                configPass.setUsarNumeros(preguntarSiNo(scanner, "¿Incluir Números?"));
                configPass.setUsarSimbolos(preguntarSiNo(scanner, "¿Incluir Símbolos?"));

                GeneradorPassword generador = new GeneradorPassword();
                password = generador.generar(configPass);
            }
            
            EvaluadorCalidad evaluador = new EvaluadorCalidad();
            NivelSeguridad nivel = evaluador.evaluar(password);

            System.out.println("\n------------------------------------------------");
            System.out.println("Contraseña generada: " + password);
            System.out.println("Nivel de seguridad: " + nivel);
            System.out.println("------------------------------------------------");

            if (preguntarSiNo(scanner, "¿Desea guardar esta contraseña?")) {
                System.out.print("Ingrese el sitio web: ");
                String sitio = scanner.nextLine();
                
                System.out.print("Ingrese el usuario: ");
                String usuario = scanner.nextLine();

                System.out.println("Categorías: TRABAJO, PERSONAL, REDES_SOCIALES, BANCO, OTRO");
                System.out.print("Ingrese la categoría: ");
                String catStr = scanner.nextLine().toUpperCase();
                
                Categoria categoria;
                try {
                    categoria = Categoria.valueOf(catStr);
                } catch (IllegalArgumentException e) {
                    categoria = Categoria.OTRO;
                    System.out.println("Categoría no válida, se asignó OTRO.");
                }

                PasswordEntry entrada = new PasswordEntry(sitio, usuario, password, categoria);
                manager.agregar(entrada);
                manager.guardarDatos(ARCHIVO_DATOS);
            }

        } catch (Exception e) {
            System.out.println("Error al generar/guardar: " + e.getMessage());
        }
    }

    private static void opcionListar(PasswordManager manager) {
        System.out.println("\n--- Contraseñas Guardadas ---");
        List<PasswordEntry> lista = manager.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay contraseñas guardadas.");
        } else {
            for (int i = 0; i < lista.size(); i++) {
                System.out.println((i + 1) + ". " + lista.get(i));
            }
        }
    }

    private static void opcionModificar(Scanner scanner, PasswordManager manager) {
        opcionListar(manager);
        List<PasswordEntry> lista = manager.listar();
        if (lista.isEmpty()) return;

        System.out.print("Ingrese el número de la entrada a modificar: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < lista.size()) {
                PasswordEntry actual = lista.get(indice);
                System.out.println("Modificando: " + actual);

                System.out.print("Nuevo sitio (Enter para mantener '" + actual.getSitio() + "'): ");
                String sitio = scanner.nextLine();
                if (sitio.isEmpty()) sitio = actual.getSitio();

                System.out.print("Nuevo usuario (Enter para mantener '" + actual.getUsuario() + "'): ");
                String usuario = scanner.nextLine();
                if (usuario.isEmpty()) usuario = actual.getUsuario();

                System.out.print("Nueva contraseña (Enter para mantener actual): ");
                String password = scanner.nextLine();
                if (password.isEmpty()) password = actual.getPassword();

                System.out.print("Nueva categoría (Enter para mantener '" + actual.getCategoria() + "'): ");
                String catStr = scanner.nextLine().toUpperCase();
                Categoria categoria = actual.getCategoria();
                if (!catStr.isEmpty()) {
                    try {
                        categoria = Categoria.valueOf(catStr);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Categoría inválida, manteniendo la anterior.");
                    }
                }

                // Mantener la fecha de creación original
                PasswordEntry nuevaEntrada = new PasswordEntry(sitio, usuario, password, categoria, actual.getFechaCreacion());
                manager.modificar(indice, nuevaEntrada);
                manager.guardarDatos(ARCHIVO_DATOS);
            } else {
                System.out.println("Índice inválido.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void opcionEliminar(Scanner scanner, PasswordManager manager) {
        opcionListar(manager);
        List<PasswordEntry> lista = manager.listar();
        if (lista.isEmpty()) return;

        System.out.print("Ingrese el número de la entrada a eliminar: ");
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < lista.size()) {
                PasswordEntry aEliminar = lista.get(indice);
                if (preguntarSiNo(scanner, "¿Seguro que desea eliminar " + aEliminar.getSitio() + "?")) {
                    manager.eliminar(aEliminar);
                    manager.guardarDatos(ARCHIVO_DATOS);
                }
            } else {
                System.out.println("Índice inválido.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void opcionEvaluar(Scanner scanner) {
        System.out.print("\nIngrese la contraseña a evaluar: ");
        String password = scanner.nextLine();
        EvaluadorCalidad evaluador = new EvaluadorCalidad();
        NivelSeguridad nivel = evaluador.evaluar(password);
        System.out.println("Nivel de seguridad: " + nivel);
    }

    private static boolean preguntarSiNo(Scanner scanner, String pregunta) {
        System.out.print(pregunta + " (S/n): ");
        String input = scanner.nextLine().trim();
        return !input.equalsIgnoreCase("n");
    }
}
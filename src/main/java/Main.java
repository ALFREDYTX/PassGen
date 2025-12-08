import com.safepass.gen.GeneradorPassword;
import com.safepass.gen.GeneradorFrase;
import com.safepass.gen.ConfiguracionPassword;
import com.safepass.gen.ConfiguracionFrase;
import com.safepass.misc.EvaluadorCalidad;
import com.safepass.misc.NivelSeguridad;
import com.safepass.misc.VulnerabilityCheck;
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

        try {
            manager.cargarDatos(ARCHIVO_DATOS);
        } catch (ManagerException e) {
            System.out.println("Nota: No se pudieron cargar datos previos (" + e.getMessage() + ")");
        }

        boolean salir = false;
        while (!salir) {
            System.out.println("\nSAFEPASS GENERATOR");
            System.out.println("1. Generar nueva contraseña");
            System.out.println("2. Ver contraseñas guardadas");
            System.out.println("3. Evaluar seguridad (Local)");
            System.out.println("4. Verificar contraseña filtrada (Online)");
            System.out.println("5. Verificar correo filtrado (Requiere API Key)");
            System.out.println("6. Gestor de Contraseñas (Modificar/Eliminar)");
            System.out.println("7. Salir");
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
                    opcionEvaluar(scanner);
                    break;
                case "4":
                    opcionVerificarVulnerabilidad(scanner);
                    break;
                case "5":
                    opcionVerificarCorreo(scanner);
                    break;
                case "6":
                    opcionGestor(scanner, manager);
                    break;
                case "7":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
    }

    private static void opcionGestor(Scanner scanner, PasswordManager manager) {
        boolean volver = false;
        while (!volver) {
            opcionListar(manager);
            System.out.println("\nGESTOR DE CONTRASEÑAS");
            System.out.println("1. Modificar contraseña");
            System.out.println("2. Eliminar contraseña");
            System.out.println("3. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    opcionModificar(scanner, manager);
                    break;
                case "2":
                    opcionEliminar(scanner, manager);
                    break;
                case "3":
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void opcionGenerar(Scanner scanner, PasswordManager manager) {
        try {
            System.out.println("\nGenerar Contraseña");
            System.out.println("1. Contraseña Aleatoria (Carácteres)");
            System.out.println("2. Frase de Contraseña (Palabras)");
            System.out.print("Seleccione tipo (1/2): ");
            String tipo = scanner.nextLine();

            String password = "";

            if (tipo.equals("2")) {
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
                System.out.println("\nConfiguración de Carácteres");
                ConfiguracionPassword configPass = new ConfiguracionPassword();
                
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

                configPass.setUsarMayusculas(preguntarSiNo(scanner, "¿Incluir Mayúsculas?"));
                configPass.setUsarNumeros(preguntarSiNo(scanner, "¿Incluir Números?"));
                configPass.setUsarSimbolos(preguntarSiNo(scanner, "¿Incluir Símbolos?"));

                GeneradorPassword generador = new GeneradorPassword();
                password = generador.generar(configPass);
            }
            
            EvaluadorCalidad evaluador = new EvaluadorCalidad();
            NivelSeguridad nivel = evaluador.evaluar(password);

            System.out.println("Contraseña generada: " + password);
            System.out.println("Nivel de seguridad: " + nivel);

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
        System.out.println("\nContraseñas Guardadas");
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

    private static void opcionVerificarVulnerabilidad(Scanner scanner) {
        System.out.print("\nIngrese la contraseña a verificar en HIBP: ");
        String password = scanner.nextLine();
        System.out.println("Verificando si la contraseña ha sido filtrada en brechas de datos...");
        try {
            VulnerabilityCheck vulnCheck = new VulnerabilityCheck();
            int pwnCount = vulnCheck.checkPassword(password);
            if (pwnCount > 0) {
                System.out.println("Esta contraseña ha aparecido en " + pwnCount + " brechas de datos.");
            } else {
                System.out.println("Esta contraseña no se ha encontrado en la base de datos de Have I Been Pwned.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo verificar la vulnerabilidad en línea: " + e.getMessage());
        }
    }

    private static void opcionVerificarCorreo(Scanner scanner) {
        System.out.print("\nIngrese el correo electrónico a verificar: ");
        String email = scanner.nextLine();
        
        System.out.println("Consultando brechas de datos para " + email + "...");
        try {
            VulnerabilityCheck vulnCheck = new VulnerabilityCheck();
            List<VulnerabilityCheck.BreachInfo> breaches = vulnCheck.checkEmail(email);

            if (!breaches.isEmpty()) {
                System.out.println("El correo " + email + " ha sido encontrado en " + breaches.size() + " brechas:");
                for (VulnerabilityCheck.BreachInfo breach : breaches) {
                    System.out.println(breach);
                }
                System.out.println("\nSe recomienda cambiar la contraseña de este correo y de cualquier sitio donde la hayas usado.");
            } else {
                System.out.println("No se encontraron brechas asociadas a este correo.");
            }
        } catch (Exception e) {
            System.out.println("Error al verificar el correo: " + e.getMessage());
        }
    }

    private static boolean preguntarSiNo(Scanner scanner, String pregunta) {
        System.out.print(pregunta + " (S/n): ");
        String input = scanner.nextLine().trim();
        return !input.equalsIgnoreCase("n");
    }
}
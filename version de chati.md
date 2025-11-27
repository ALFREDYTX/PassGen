Va, esto se pone bueno 😎

Te voy a armar el **núcleo de SafePass como “API”**: clases y métodos que luego puedes usar desde una consola, GUI, REST, lo que quieras, sin que el código esté amarrado a “un programa” concreto.

### Antes de mostrar código, ojo con esto (cosas “extra” que *podríamos* meter):

* **NO** voy a meter por ahora:

  * Persistencia (CSV/JSON).
  * Cifrado.
  * Llamadas HTTP a APIs externas.
* SÍ voy a meter (porque dan modularidad, pero siguen siendo sencillos):

  * `PasswordStrength` (enum).
  * `PasswordStrengthEvaluator` (clase que evalúa la contraseña).
  * `PasswordGenerator` (configurable).
  * `Password` (valor + fecha + fuerza).
  * `AccountEntry` (sitio + usuario + password).
  * `PasswordManager` (interfaz) + `InMemoryPasswordManager` (implementación sencilla en memoria).

Todo esto sigue la estructura que tú mismo pusiste en el PDF de SafePass , pero **en versión API modular**, sin menú, sin interfaz, puro “motor”.

Te lo dejo como si fuera un package `safepass.core`. Puedes cambiar el nombre.

---

## 1. `PasswordStrength.java` (enum)

```java
package safepass.core;

// Nivel de seguridad de una contraseña
public enum PasswordStrength {
    MUY_DEBIL,
    DEBIL,
    MEDIA,
    FUERTE,
    MUY_FUERTE
}
```

---

## 2. `PasswordStrengthEvaluator.java`

Regla sencilla pero decente para tu nivel: puntos por longitud y variedad de caracteres.

```java
package safepass.core;

// Evalúa la fuerza de una contraseña
public class PasswordStrengthEvaluator {

    /**
     * Regresa un puntaje de 0 a 6 según las características de la contraseña.
     */
    public int score(String pwd) {
        if (pwd == null) return 0;

        int score = 0;

        if (pwd.length() >= 8)  score++;
        if (pwd.length() >= 12) score++;

        if (pwd.matches(".*[A-Z].*"))        score++; // mayúsculas
        if (pwd.matches(".*[a-z].*"))        score++; // minúsculas
        if (pwd.matches(".*\\d.*"))          score++; // dígitos
        if (pwd.matches(".*[!@#$%^&*].*"))   score++; // símbolos básicos

        return score;
    }

    /**
     * Convierte el puntaje en una categoría de fuerza.
     */
    public PasswordStrength evaluate(String pwd) {
        int s = score(pwd);

        if (s <= 1) return PasswordStrength.MUY_DEBIL;
        if (s == 2) return PasswordStrength.DEBIL;
        if (s == 3) return PasswordStrength.MEDIA;
        if (s == 4 || s == 5) return PasswordStrength.FUERTE;
        return PasswordStrength.MUY_FUERTE; // s == 6
    }
}
```

---

## 3. `Password.java`

Representa una contraseña ya evaluada.

```java
package safepass.core;

import java.time.LocalDateTime;

// Representa una contraseña almacenada en el sistema
public class Password {
    private final String value;
    private final LocalDateTime createdAt;
    private final PasswordStrength strength;

    public Password(String value, PasswordStrength strength) {
        if (value == null) {
            throw new IllegalArgumentException("Password value cannot be null.");
        }
        this.value = value;
        this.strength = strength;
        this.createdAt = LocalDateTime.now();
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return "Password{" +
                "createdAt=" + createdAt +
                ", strength=" + strength +
                '}';
    }
}
```

> Nota: aquí **no enmascaro** el value; si después quieres evitar imprimirla o cifrarla, se puede extender sin cambiar esta API.

---

## 4. `PasswordGeneratorConfig.java` (opcional pero muy útil)

Config sencilla para no llenar el constructor de mil booleanos.

```java
package safepass.core;

// Configuración para generar contraseñas
public class PasswordGeneratorConfig {
    private int length = 12;
    private boolean useUpper = true;
    private boolean useLower = true;
    private boolean useDigits = true;
    private boolean useSymbols = true;

    public int getLength() {
        return length;
    }

    public PasswordGeneratorConfig setLength(int length) {
        if (length < 4 || length > 64) {
            throw new IllegalArgumentException("Length must be between 4 and 64.");
        }
        this.length = length;
        return this;
    }

    public boolean isUseUpper() {
        return useUpper;
    }

    public PasswordGeneratorConfig setUseUpper(boolean useUpper) {
        this.useUpper = useUpper;
        return this;
    }

    public boolean isUseLower() {
        return useLower;
    }

    public PasswordGeneratorConfig setUseLower(boolean useLower) {
        this.useLower = useLower;
        return this;
    }

    public boolean isUseDigits() {
        return useDigits;
    }

    public PasswordGeneratorConfig setUseDigits(boolean useDigits) {
        this.useDigits = useDigits;
        return this;
    }

    public boolean isUseSymbols() {
        return useSymbols;
    }

    public PasswordGeneratorConfig setUseSymbols(boolean useSymbols) {
        this.useSymbols = useSymbols;
        return this;
    }
}
```

---

## 5. `PasswordGenerator.java`

```java
package safepass.core;

import java.security.SecureRandom;

// Genera contraseñas aleatorias con base en una configuración
public class PasswordGenerator {
    private static final String UPPER   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER   = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS  = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*";

    private final SecureRandom random = new SecureRandom();

    public String generate(PasswordGeneratorConfig config) {
        StringBuilder pool = new StringBuilder();

        if (config.isUseUpper())   pool.append(UPPER);
        if (config.isUseLower())   pool.append(LOWER);
        if (config.isUseDigits())  pool.append(DIGITS);
        if (config.isUseSymbols()) pool.append(SYMBOLS);

        if (pool.length() == 0) {
            throw new IllegalStateException("At least one character set must be enabled.");
        }

        String chars = pool.toString();
        StringBuilder pwd = new StringBuilder(config.getLength());

        for (int i = 0; i < config.getLength(); i++) {
            int index = random.nextInt(chars.length());
            pwd.append(chars.charAt(index));
        }

        return pwd.toString();
    }
}
```

---

## 6. `AccountEntry.java`

Un registro: sitio + usuario + password (objeto).

```java
package safepass.core;

// Representa una cuenta (sitio + usuario + password)
public class AccountEntry {
    private String site;
    private String username;
    private Password password;

    public AccountEntry(String site, String username, Password password) {
        if (site == null || site.isEmpty()) {
            throw new IllegalArgumentException("Site cannot be null or empty.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        this.site = site;
        this.username = username;
        this.password = password;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        if (site == null || site.isEmpty()) return;
        this.site = site;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        if (password == null) return;
        this.password = password;
    }

    @Override
    public String toString() {
        return "AccountEntry{" +
                "site='" + site + '\'' +
                ", username='" + username + '\'' +
                ", password=" + password +
                '}';
    }
}
```

---

## 7. `PasswordManager.java` (interfaz tipo API)

```java
package safepass.core;

import java.util.List;

// API principal para gestionar contraseñas
public interface PasswordManager {
    AccountEntry addEntry(String site, String username, String rawPassword);
    AccountEntry addGeneratedEntry(String site, String username, PasswordGeneratorConfig config);
    List<AccountEntry> listEntries();
    boolean removeEntry(String site, String username);
    PasswordStrength evaluatePassword(String rawPassword);
}
```

---

## 8. `InMemoryPasswordManager.java` (implementación básica)

```java
package safepass.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Implementación simple en memoria (sin archivos todavía)
public class InMemoryPasswordManager implements PasswordManager {
    private final List<AccountEntry> entries = new ArrayList<>();
    private final PasswordStrengthEvaluator evaluator = new PasswordStrengthEvaluator();
    private final PasswordGenerator generator = new PasswordGenerator();

    @Override
    public AccountEntry addEntry(String site, String username, String rawPassword) {
        PasswordStrength strength = evaluator.evaluate(rawPassword);
        Password password = new Password(rawPassword, strength);
        AccountEntry entry = new AccountEntry(site, username, password);
        entries.add(entry);
        return entry;
    }

    @Override
    public AccountEntry addGeneratedEntry(String site, String username, PasswordGeneratorConfig config) {
        String generated = generator.generate(config);
        PasswordStrength strength = evaluator.evaluate(generated);
        Password password = new Password(generated, strength);
        AccountEntry entry = new AccountEntry(site, username, password);
        entries.add(entry);
        return entry;
    }

    @Override
    public List<AccountEntry> listEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public boolean removeEntry(String site, String username) {
        return entries.removeIf(e ->
                e.getSite().equalsIgnoreCase(site) &&
                (username == null || username.equalsIgnoreCase(e.getUsername())));
    }

    @Override
    public PasswordStrength evaluatePassword(String rawPassword) {
        return evaluator.evaluate(rawPassword);
    }
}
```

---

## 9. Ejemplo de uso (no es parte de la API, solo para probar)

Si quieres hacer una clase `Main` para probar en consola:

```java
package safepass;

import safepass.core.*;

public class Main {
    public static void main(String[] args) {
        PasswordManager manager = new InMemoryPasswordManager();

        // Config para generar passwords
        PasswordGeneratorConfig config = new PasswordGeneratorConfig()
                .setLength(16)
                .setUseUpper(true)
                .setUseLower(true)
                .setUseDigits(true)
                .setUseSymbols(true);

        // Crear una entrada con password generada
        AccountEntry e1 = manager.addGeneratedEntry("gmail.com", "juan", config);
        System.out.println(e1);

        // Crear una entrada con password manual
        AccountEntry e2 = manager.addEntry("github.com", "juan", "Abc123!!");
        System.out.println(e2);

        // Evaluar una contraseña suelta
        System.out.println(manager.evaluatePassword("12345"));

        // Listar todo
        for (AccountEntry entry : manager.listEntries()) {
            System.out.println(entry);
        }
    }
}
```

---

## Qué tienes ahora

* Un **núcleo modular** (tipo librería/API) que:

  * Genera contraseñas configurables.
  * Evalúa la fuerza.
  * Maneja entradas (sitio/usuario/password) en memoria.
* Nada de UI metida, nada de archivos todavía → se puede conectar a:

  * consola,
  * Swing,
  * REST,
  * lo que pida el profe.

Si quieres, el siguiente paso puede ser:

* agregar **persistencia** con una interfaz `PasswordStore` y una implementación `CsvPasswordStore` o `JsonPasswordStore`,
  pero eso ya lo vemos solo si tú me dices “sí, agrégalo”. 💾

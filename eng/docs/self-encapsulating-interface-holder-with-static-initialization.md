# Self-encapsulating interface holder with static initialization

A self-encapsulating interface holder with static initialization is a Java design pattern where:

1. A class defines an interface inside itself (the contract is nested within the class).
2. The outer class holds a static reference to an implementation of that interface.
3. The outer class exposes methods that mirror the interface’s behavior and delegate to the implementation.
4. The implementation is resolved statically, often via:
- Reflection (Class.forName)
- SPI (ServiceLoader)
- Environment config (System.getProperty)
- Direct registration (e.g., setImplementation(...))

This pattern effectively bundles the contract, the entry point, and the bootstrap logic into one self-contained type.

## Structure

```java
public class Host {
    // 1️⃣ Inner interface defines the contract
    public interface Extension {
        void execute(String input);
    }

    // 2️⃣ Static instance holds the bound implementation
    private static Extension instance;

    // 3️⃣ Static initializer bootstraps the implementation
    static {
        try {
            Class<?> impl = Class.forName(System.getProperty("host.extension.class"));
            instance = (Extension) impl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            instance = new DefaultExtension();
        }
    }

    // 4️⃣ Static façade that mirrors the interface behavior
    public static void execute(String input) {
        instance.execute(input);
    }

    // Default fallback
    private static class DefaultExtension implements Extension {
        public void execute(String input) {
            System.out.println("Default execution: " + input);
        }
    }
}
```

## Key Characterstics

| Characteristic            | Description                                                         |
| ------------------------- | ------------------------------------------------------------------- |
| **Self-contained**        | Interface, default behavior, and loading logic live together.       |
| **Encapsulated contract** | Consumers see only `Host`, not the interface or loaders.            |
| **Static dispatch**       | Simple `Host.method()` calls, no injection required.                |
| **Lazy or eager init**    | Can use static initializer or lazy getter.                          |
| **Extensible**            | New implementations can be loaded via classpath or system property. |

## Why It’s Useful
- Simplifies bootstrap - No external wiring — the system initializes itself on class load.

- Stable API Surface - The outer class’s static API remains consistent even if internal behavior changes.

- Runtime flexibility - You can redirect behavior (e.g., to your agent, sidecar, or plugin) without changing the caller.

- Ideal for instrumentation - Bytecode-injected or auto-generated code can safely call Host.doSomething(), knowing that the backend may be dynamically replaced.

## Variations
### Lazy Initialization
```java
private static volatile Extension instance;

public static Extension get() {
    if (instance == null) {
        synchronized (Host.class) {
            if (instance == null) instance = loadImplementation();
        }
    }
    return instance;
}
```
### SPI-based Loading
```java
static {
    ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class);
    instance = loader.findFirst().orElse(new DefaultExtension());
}
```
### Dynamic Reloading
```java
public static void setExtension(Extension newImpl) {
    instance = newImpl;
}
```
Then your agent or sidecar can call:
```java
Host.setExtension(new RemoteProxyExtension());
```


package com.demo.shell;

import com.demo.contract.Concept;
import com.demo.contract.Fixture;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ConceptREPL {
    private final Scanner scanner;
    private final Map<String, Class<?>> conceptClasses;
    private final Map<String, Object> conceptInstances;
    private boolean running;

    public ConceptREPL() {
        this.scanner = new Scanner(System.in);
        this.conceptClasses = new HashMap<>();
        this.conceptInstances = new HashMap<>();
        this.running = true;
        loadConcepts();
    }

    private void loadConcepts() {
        Reflections reflections = new Reflections("com.demo");
        Set<Class<?>> concepts = reflections.getTypesAnnotatedWith(Concept.class);

        for (Class<?> conceptClass : concepts) {
            String conceptName = conceptClass.getSimpleName().toLowerCase();
            conceptClasses.put(conceptName, conceptClass);

            try {
                Object instance = conceptClass.getDeclaredConstructor().newInstance();
                conceptInstances.put(conceptName, instance);
            } catch (Exception e) {
                System.err.println("Failed to instantiate " + conceptClass.getSimpleName() + ": " + e.getMessage());
            }
        }

        System.out.println("Loaded " + conceptClasses.size() + " concepts");
    }

    public void start() {
        printWelcome();

        while (running) {
            System.out.print("concept> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            processCommand(input);
        }

        scanner.close();
    }

    private void printWelcome() {
        System.out.println("=== Concept Discovery REPL ===");
        System.out.println("Type 'help' for available commands");
        System.out.println("Type 'exit' to quit");
        System.out.println();
    }

    private void processCommand(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case "help":
                    showHelp();
                    break;
                case "list":
                    listConcepts(parts.length == 2 ? parts[1] : null);
                    System.out.println("Usage: list <concept_name_prefix>");
                    break;
                case "show":
                    if (parts.length > 1) {
                        showConcept(parts[1]);
                    } else {
                        System.out.println("Usage: show <concept_name>");
                    }
                    break;
                case "run":
                    if (parts.length > 1) {
                        runFixture(parts[1], parts.length > 2 ? parts[2] : null);
                    } else {
                        System.out.println("Usage: run <concept_name> [fixture_name]");
                    }
                    break;
                case "search":
                    if (parts.length > 1) {
                        searchConcepts(parts[1]);
                    } else {
                        System.out.println("Usage: search <term>");
                    }
                    break;
                case "reload":
                    reload();
                    break;
                case "clear":
                    clearScreen();
                    break;
                case "exit":
                case "quit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
            }
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help                    - Show this help message");
        System.out.println("  list                    - List all available concepts");
        System.out.println("  show <concept>          - Show details of a specific concept");
        System.out.println("  run <concept> [fixture] - Run a concept or specific fixture");
        System.out.println("  search <term>           - Search concepts by name");
        System.out.println("  reload                  - Reload all concepts");
        System.out.println("  clear                   - Clear screen");
        System.out.println("  exit/quit               - Exit the REPL");
    }

    private void listConcepts(String conceptName) {
        if (conceptClasses.isEmpty()) {
            System.out.println("No concepts found.");
            return;
        }

        if(conceptName == null || conceptName.isEmpty())
        {
            conceptClasses.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        Concept annotation = entry.getValue().getAnnotation(Concept.class);
                        String description = annotation != null && !annotation.description().isEmpty()
                                ? annotation.description()
                                : "No description";

                        System.out.println("  " + entry.getKey() + " - " + description);
                    });
        }
        else {
            conceptClasses.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(conceptName.toLowerCase()))
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        Concept annotation = entry.getValue().getAnnotation(Concept.class);
                        String description = annotation != null && !annotation.description().isEmpty()
                                ? annotation.description()
                                : "No description";

                        System.out.println("  " + entry.getKey() + " - " + description);
                    });
        }
    }

    private void showConcept(String conceptName) {
        Class<?> conceptClass = conceptClasses.get(conceptName.toLowerCase());

        if (conceptClass == null) {
            System.out.println("Concept '" + conceptName + "' not found.");
            return;
        }

        System.out.println("Concept: " + conceptClass.getSimpleName());

        Concept annotation = conceptClass.getAnnotation(Concept.class);
        if (annotation != null && !annotation.description().isEmpty()) {
            System.out.println("Description: " + annotation.description());
        }

        System.out.println("Package: " + conceptClass.getPackageName());

        // List fixtures
        Method[] fixtures = Arrays.stream(conceptClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Fixture.class))
                .toArray(Method[]::new);

        if (fixtures.length > 0) {
            System.out.println("Fixtures:");
            for (Method fixture : fixtures) {
                Fixture fixtureAnnotation = fixture.getAnnotation(Fixture.class);
                String description = fixtureAnnotation.description().isEmpty()
                        ? "No description"
                        : fixtureAnnotation.description();

                System.out.println("  " + fixture.getName() + " - " + description);
            }
        } else {
            System.out.println("No fixtures found.");
        }
    }

    private void runFixture(String conceptName, String fixtureName) {
        Class<?> conceptClass = conceptClasses.get(conceptName.toLowerCase());
        Object conceptInstance = conceptInstances.get(conceptName.toLowerCase());

        if (conceptClass == null || conceptInstance == null) {
            System.out.println("Concept '" + conceptName + "' not found.");
            return;
        }

        try {
            // Get all fixtures in that concept
            var fixtures = Arrays.stream(conceptClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Fixture.class))
                    .toArray(Method[]::new);

            if (fixtureName == null) {
                // Run all fixtures

                if (fixtures.length == 0) {
                    System.out.println("No fixtures found in " + conceptName);
                    return;
                }

                System.out.println("Running all fixtures for " + conceptName + ":");
                for (Method fixture : fixtures) {
                    System.out.println("\n--- Running " + fixture.getName() + " ---");
                    fixture.setAccessible(true);
                    fixture.invoke(conceptInstance);
                }
            } else {
                // Run specific fixture
                var fixturesToRun = Arrays.stream(fixtures)
                        .filter(method -> method.getName().toLowerCase().startsWith(fixtureName.toLowerCase()))
                        .toList();

                if (fixturesToRun.isEmpty()) {
                    System.out.println("Fixture '" + fixtureName + "' not found in " + conceptName);
                    return;
                }

                for (Method fixture : fixturesToRun) {
                    System.out.println("Running " + fixture.getName() + " from " + conceptName + ":");
                    fixture.setAccessible(true);
                    fixture.invoke(conceptInstance);
                }
            }
        } catch (Exception e) {
            System.err.println("Error running fixture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchConcepts(String searchTerm) {
        List<String> matches = conceptClasses.keySet().stream()
                .filter(name -> name.contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            System.out.println("No concepts found matching '" + searchTerm + "'");
        } else {
            System.out.println("Concepts matching '" + searchTerm + "':");
            matches.forEach(name -> System.out.println("  " + name));
        }
    }

    private void reload() {
        conceptClasses.clear();
        conceptInstances.clear();
        loadConcepts();
        System.out.println("Concepts reloaded.");
    }

    private void clearScreen() {
        // Clear screen for most terminals
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}

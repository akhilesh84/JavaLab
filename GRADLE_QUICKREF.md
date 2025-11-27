# Gradle Quick Reference - Demo Project

## Essential Commands

### Build Commands
```bash
./gradlew build                    # Build all modules
./gradlew clean build              # Clean and build
./gradlew build -x test            # Build without tests
./gradlew :webapi:build            # Build specific module
```

### Run Commands
```bash
./gradlew :webapi:bootRun          # Run Spring Boot app (with agent)
./gradlew :playground:run          # Run playground app (with agent)
./gradlew :playground:runWithAgent # Explicit agent run
```

### Common Tasks
```bash
./gradlew tasks                    # List all tasks
./gradlew :webapi:tasks            # List module tasks
./gradlew projects                 # Show project structure
./gradlew dependencies             # Show all dependencies
./gradlew :webapi:dependencies     # Show module dependencies
```

### Testing
```bash
./gradlew test                     # Run all tests
./gradlew :webapi:test             # Run module tests
./gradlew test --tests ClassName   # Run specific test
```

### Cleaning
```bash
./gradlew clean                    # Clean all builds
./gradlew :webapi:clean            # Clean specific module
./gradlew --stop                   # Stop Gradle daemon
```

## Module Structure

```
demo-parent (root)
├── agent      → Java agent with ByteBuddy
├── mylib      → Shared library
├── playground → Console app with Kafka
└── webapi     → Spring Boot web app
```

## Key Files

| File | Purpose |
|------|---------|
| `settings.gradle` | Declares modules |
| `build.gradle` (root) | Shared configuration |
| `*/build.gradle` | Module-specific config |
| `gradle.properties` | Build settings |
| `gradlew` | Gradle wrapper (Unix) |

## IntelliJ IDEA

1. **Import:** File → Open → Select project folder
2. **Sync:** Gradle tool window → Reload
3. **Run:** Use Gradle tasks or create Run Configuration

## Troubleshooting

```bash
# Refresh dependencies
./gradlew build --refresh-dependencies

# Clear cache
rm -rf ~/.gradle/caches/
./gradlew clean build

# View build info
./gradlew build --info
./gradlew build --debug

# View dependency conflicts
./gradlew :webapi:dependencies --configuration runtimeClasspath
```

## Maven → Gradle Mapping

| Maven | Gradle |
|-------|--------|
| `mvn clean install` | `./gradlew build` |
| `mvn clean` | `./gradlew clean` |
| `mvn test` | `./gradlew test` |
| `mvn spring-boot:run` | `./gradlew :webapi:bootRun` |
| `mvn dependency:tree` | `./gradlew dependencies` |
| `target/` | `build/` |

## Build Output Locations

```
agent/build/libs/agent.jar              # Agent JAR
playground/build/libs/playground.jar    # Playground fat JAR
webapi/build/libs/webapi-0.0.1.jar      # Spring Boot JAR
webapi/build/libs/agent-0.0.1.jar       # Agent copy
```

## Running Standalone JARs

```bash
# Webapi
java -javaagent:webapi/build/libs/agent-0.0.1.jar \
     -jar webapi/build/libs/webapi-0.0.1.jar

# Playground
java -javaagent:agent/build/libs/agent.jar \
     -jar playground/build/libs/playground.jar
```

---
📚 **Full Documentation:** See [GRADLE_README.md](GRADLE_README.md)  
📋 **Conversion Details:** See [CONVERSION_SUMMARY.md](CONVERSION_SUMMARY.md)


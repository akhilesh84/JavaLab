# Demo Project - Gradle Build

This project has been converted from Maven to Gradle. It is a multi-module Java project with the following modules:

## Modules

- **agent** - Java agent with ByteBuddy for runtime class transformation
- **mylib** - Shared library module
- **playground** - Console application with Kafka support
- **webapi** - Spring Boot web application with REST API, Kafka, and Spring Shell

## Prerequisites

- Java 21 or later (uses Java toolchain)
- Gradle 8.14+ (included via Gradle Wrapper)

## Building the Project

### Build all modules
```bash
./gradlew build
```

### Build a specific module
```bash
./gradlew :webapi:build
./gradlew :agent:build
./gradlew :playground:build
```

### Clean build
```bash
./gradlew clean build
```

## Running Applications

### Run the webapi (Spring Boot)
```bash
./gradlew :webapi:bootRun
```

The webapi will start with the Java agent automatically attached.

### Run the playground application
```bash
./gradlew :playground:run
```

Or run with explicit agent support:
```bash
./gradlew :playground:runWithAgent
```

### Run the standalone JAR files

**Webapi:**
```bash
java -javaagent:webapi/build/libs/agent-0.0.1.jar -jar webapi/build/libs/webapi-0.0.1.jar
```

**Playground:**
```bash
java -javaagent:agent/build/libs/agent.jar -jar playground/build/libs/playground.jar
```

## Project Structure

```
.
├── build.gradle              # Root build configuration
├── settings.gradle           # Multi-module settings
├── gradle.properties         # Gradle properties
├── gradlew                   # Gradle wrapper script (Unix)
├── gradlew.bat              # Gradle wrapper script (Windows)
├── gradle/
│   └── wrapper/             # Gradle wrapper files
├── agent/
│   ├── build.gradle         # Agent module build
│   └── src/
├── mylib/
│   ├── build.gradle         # Library module build
│   └── src/
├── playground/
│   ├── build.gradle         # Playground module build
│   └── src/
└── webapi/
    ├── build.gradle         # Webapi module build
    └── src/
```

## Key Features

### Agent Module
- Produces a fat JAR with all dependencies (using Shadow plugin)
- Configured with proper MANIFEST entries for Java agent (Premain-Class)
- Can be used with `-javaagent` flag at runtime

### Webapi Module
- Spring Boot application with embedded Tomcat
- Automatically copies agent JAR to build/libs during build
- Configured to run with agent when using `bootRun`
- Includes Spring Shell, Kafka, H2 database support

### Playground Module
- Console application with reflection and Kafka support
- Produces fat JAR with all dependencies
- Custom `runWithAgent` task for running with Java agent

## Common Gradle Tasks

```bash
# List all available tasks
./gradlew tasks

# List tasks for specific module
./gradlew :webapi:tasks

# Show project dependencies
./gradlew :webapi:dependencies

# Run tests
./gradlew test

# Run tests for specific module
./gradlew :webapi:test

# Build without tests
./gradlew build -x test

# Clean all builds
./gradlew clean
```

## IntelliJ IDEA Integration

1. Open the project in IntelliJ IDEA
2. IntelliJ will automatically detect the Gradle build
3. Wait for Gradle sync to complete
4. To configure Run/Debug configurations:
   - For **webapi**: Use the Spring Boot run configuration or Gradle's `bootRun` task
   - For **playground**: Use the Gradle `run` or `runWithAgent` task
   - Both are pre-configured to attach the Java agent

## Dependency Management

- All dependency versions are managed in the root `build.gradle`
- Spring Boot BOM is imported via the Spring Boot plugin
- Common versions (Lombok, Kafka, etc.) are defined in `ext` block in root build

## Migration Notes from Maven

- Maven `pom.xml` files are preserved but no longer used
- `target/` directories are now `build/`
- `mvn clean install` → `./gradlew build`
- `mvn spring-boot:run` → `./gradlew :webapi:bootRun`
- Maven wrapper (`mvnw`) → Gradle wrapper (`gradlew`)

## Troubleshooting

### Gradle Daemon Issues
```bash
# Stop all Gradle daemons
./gradlew --stop

# Run without daemon
./gradlew build --no-daemon
```

### Clean Gradle Cache
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Reload dependencies
./gradlew build --refresh-dependencies
```

### IntelliJ Not Syncing
```bash
# Reimport Gradle project in IntelliJ
File → Invalidate Caches → Invalidate and Restart
```


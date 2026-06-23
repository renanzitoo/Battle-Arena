# Battle Arena 🎮

A multi-threaded **matchmaking and battle scheduling system** implemented in Java, developed for educational purposes in Object-Oriented Programming (OOP) and Operating Systems (OS).

## 📋 Overview

Battle Arena is a simulator that manages queues of players waiting for battles, pairs them up (matchmaking), and runs the battles concurrently. The system is fully thread-safe and implements synchronization patterns to guarantee data consistency in a multi-threaded environment.

### Supported Battle Types
- **`CASUAL_MATCH`**: Informal battles without ranking
- **`RANKED_MATCH`**: Competitive battles with points
- **`TOURNAMENT_MATCH`**: Tournament format battles

## 🏗️ Architecture

### Directory Structure

```
Battle-Arena/
├── README.md
├── BattleArena.iml
├── src/
│   └── main/
│       └── java/
│           ├── app/
│           │   └── Main.java                       # Application entry point
│           ├── model/                              # Data models
│           │   ├── Battle.java                     # Represents a battle
│           │   ├── BattleRequest.java              # Battle request
│           │   ├── BattleStatus.java               # Battle states
│           │   ├── BattleType.java                 # Battle types (enum)
│           │   ├── Player.java                     # Player
│           │   └── QueueRequest.java               # Queue request
│           ├── service/                            # Services and business logic
│           │   ├── MatchmakingService.java         # Manages waiting queues
│           │   └── BattleSchedulerService.java     # Schedules and executes battles
│           └── thread/                             # Execution threads
│               ├── MatchmakingThread.java          # Thread that creates matches
│               ├── SchedulerThread.java            # Thread that executes battles
│               ├── MonitorThread.java              # Thread that monitors the system
│               └── BattleThread.java               # Thread that executes a single battle
```

## 🚀 How to Run

### Prerequisites
- **Java 17+** (Java Development Kit - JDK)
- **Maven 3.6+** (dependency manager and build tool)
- IDE of your choice (IntelliJ IDEA, Eclipse, VS Code with Java extensions)

### Execution Steps

1. **Clone or download the project**
   ```bash
   git clone https://github.com/renanzitoo/Battle-Arena.git
   cd Battle-Arena
   ```

2. **Run the application with Maven**
   
   **Option A - Run with JavaFX GUI (recommended):**
   ```bash
   mvn javafx:run
   ```
   
   **Option B - Run via Command Line Interface (CLI):**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="app.Main"
   ```
   
   **Option C - Create an executable JAR and run it:**
   ```bash
   mvn clean package
   java -cp target/BattleArena-1.0-SNAPSHOT.jar app.Main
   ```

### Expected Output (CLI)
```
===== SYSTEM READY =====
Commands: 'start' (generate players), 'stop' (pause generation), 'exit' (finish simulation)
```

### Using the CLI Application

**Available Commands:**
- `start` - Starts automatic player generation
- `stop` - Pauses player generation
- `exit` - Ends the simulation and displays the technical report

---

## 🔒 Thread Safety

The project implements **robust synchronization** to ensure data consistency in a multi-threaded environment:

### Techniques Used

1. **Synchronized Blocks** 🔐
   - Critical methods inside [MatchmakingService.java](file:///C:/Users/renan/IdeaProjects/Battle-Arena/src/main/java/service/MatchmakingService.java) are synchronized using a dedicated lock.
   - Prevents race conditions when multiple threads access the waiting queues.

2. **Private Object Lock**
   - Uses a dedicated private `Object` for synchronization.
   - Ensures atomicity of critical queue operations.

3. **Thread Pool / Concurrent Execution**
   - Manages the execution of concurrent battles using distinct [BattleThread.java](file:///C:/Users/renan/IdeaProjects/Battle-Arena/src/main/java/thread/BattleThread.java) instances.
   - Avoids overloading system CPU resources.

---

## 🎯 Features

✅ Fully thread-safe system using synchronization  
✅ Automatic matchmaking between players in waiting queues  
✅ Multiple battle types with unique properties (cost, priorities)  
✅ Concurrent battle execution simulating variable durations  
✅ Real-time system monitoring and health checks  
✅ Automatic player removal from queues upon timeout (abandonment)  
✅ Realistic performance and load metrics reporting  

---

## 📚 OOP & Operating Systems Concepts Applied

- **Encapsulamento (Encapsulation)**: Private data fields accessed only via public getters/setters in models like [Player.java](file:///C:/Users/renan/IdeaProjects/Battle-Arena/src/main/java/model/Player.java).
- **Polimorfismo (Polymorphism)**: Dynamic task execution using Java's standard `Runnable` interface.
- **Abstração (Abstraction)**: Service layer hiding concurrency complexity from the user interface.
- **Exclusão Mútua (Mutual Exclusion)**: Protecting shared resources from race conditions.
- **Escalonamento de Prioridades (Priority Scheduling)**: Ordering battles based on type and demand.
- **Aging (Envelhecimento)**: Dynamic priority calculation in [BattleRequest.java](file:///C:/Users/renan/IdeaProjects/Battle-Arena/src/main/java/model/BattleRequest.java) to prevent **Starvation** of casual matches.
- **Sincronização Ativa**: Using `wait()` and `notifyAll()` to avoid CPU waste via **Busy Waiting**.

---

## 🔧 Troubleshooting

### Problem: "package model does not exist"
**Solution**: Do not compile `Main.java` alone with `javac`. Use Maven to compile the whole project properly:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="app.Main"
```

### Problem: "Cannot find symbol" when compiling
**Solution**: Ensure you are using Maven and not compiling individual files manually. Maven resolves all internal packages automatically.

### Problem: Slow compilation on first run
**Expected**: On the first execution, Maven downloads all required dependencies. This might take a few minutes. Subsequent builds will be near-instant.

### Problem: JavaFX UI does not start
**Solution**: Make sure you have Java 17+ installed:
```bash
java -version
```
If you need to re-install Maven plugins or clear target directories:
```bash
mvn clean install
```

---

**Battle Arena** - Matchmaking and Battle Scheduling System 🎮⚔️

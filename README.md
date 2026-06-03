# Battle Arena 🎮

Um sistema de **matchmaking e agendamento de batalhas** multi-thread implementado em Java, desenvolvido para fins educacionais em Programação Orientada a Objetos (POO).

## 📋 Visão Geral

O Battle Arena é um simulador que gerencia filas de jogadores esperando por batalhas, cria matches entre eles e executa as batalhas de forma concorrente. O sistema é totalmente thread-safe e implementa padrões de sincronização para garantir consistência de dados em um ambiente multi-thread.

### Tipos de Batalha Suportados
- **CASUAL_MATCH**: Batalhas informais sem ranking
- **RANKED_MATCH**: Batalhas competitivas com pontuação
- **TOURNAMENT_MATCH**: Batalhas em formato de torneio

## 🏗️ Arquitetura

### Estrutura de Diretórios

```
Battle-Arena/
├── README.md
├── BattleArena.iml
├── src/
│   ├── Main.java                           # Ponto de entrada da aplicação
│   ├── model/                              # Modelos de dados
│   │   ├── Battle.java                     # Representa uma batalha
│   │   ├── BattleRequest.java              # Requisição de batalha
│   │   ├── BattleStatus.java               # Estados da batalha
│   │   ├── BattleType.java                 # Tipos de batalha (enum)
│   │   ├── Player.java                     # Jogador
│   │   └── QueueRequest.java               # Requisição na fila
│   ├── service/                            # Serviços e lógica de negócio
│   │   ├── MatchmakingService.java         # Gerencia filas de espera
│   │   └── BattleSchedulerService.java     # Agenda e executa batalhas
│   └── thread/                             # Threads de execução
│       ├── MatchmakingThread.java          # Thread que cria matches
│       ├── SchedulerThread.java            # Thread que executa batalhas
│       ├── MonitorThread.java              # Thread que monitora o sistema
│       └── BattleThread.java               # Thread que executa uma batalha
```

## 🚀 Como Executar

### Pré-requisitos
- **Java 17+** (Java Development Kit - JDK)
- **Maven 3.6+** (gerenciador de dependências e build)
- IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code com extensões Java)

### Passos

1. **Clone ou baixe o projeto**
   ```bash
   git clone https://github.com/renanzitoo/Battle-Arena.git
   cd Battle-Arena
   ```

2. **Execute a aplicação com Maven**
   
   Opção A - Executar com interface JavaFX (recomendado):
   ```bash
   mvn javafx:run
   ```
   
   Opção B - Executar via linha de comando:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="app.Main"
   ```
   
   Opção C - Criar JAR executável e rodar:
   ```bash
   mvn clean package
   java -cp target/BattleArena-1.0-SNAPSHOT.jar app.Main
   ```

### Saída Esperada
```
===== SYSTEM READY =====
Commands: 'start' (generate players), 'stop' (pause generation), 'exit' (finish simulation)
```

### Usando a Aplicação

**Linhas de Comando Disponíveis:**
- `start` - Inicia a geração automática de jogadores
- `stop` - Para a geração de jogadores
- `exit` - Finaliza a simulação

## 🔒 Thread Safety

O projeto implementa **sincronização robusta** para garantir consistência em ambiente multi-thread:

### Técnicas Utilizadas

1. **Synchronized Blocks** 🔐
   - Métodos críticos do `MatchmakingService` sincronizados com lock
   - Evita race conditions no acesso às filas

2. **Object Lock**
   - Uso de `Object` dedicado para sincronização
   - Garante atomicidade das operações

3. **Thread Pool Executor**
   - Gerencia limite de threads simultâneas
   - Evita sobrecarga do sistema

## 🎯 Funcionalidades

✅ Sistema multi-thread com sincronização segura  
✅ Matchmaking automático entre jogadores  
✅ Múltiplos tipos de batalha  
✅ Thread pool para execução concorrente  
✅ Monitoramento do sistema em tempo real  
✅ Remoção automática de jogadores inativos  
✅ Simulação realista de timings  

## 📚 Conceitos de POO Utilizados

- **Encapsulamento**: Dados privados com acesso via métodos públicos
- **Herança**: Estrutura de classes para diferentes tipos de requisições
- **Polimorfismo**: Implementação de Runnable para threads
- **Abstração**: Serviços abstraem complexidade do sistema
- **Enums**: Tipos de batalha e status
- **Sincronização**: Locks e synchronized blocks

## 🔧 Troubleshooting

### Problema: "package model does not exist"
**Solução**: Não compile apenas o `Main.java` com `javac`. Use Maven:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="app.Main"
```

### Problema: "Cannot find symbol" ao compilar
**Solução**: Certifique-se de estar usando Maven e não tentando compilar manualmente. O Maven resolve todas as dependências automaticamente.

### Problema: Compilação lenta na primeira vez
**Esperado**: Na primeira execução, Maven baixa todas as dependências. Isso pode levar alguns minutos. Execuções posteriores serão mais rápidas.

### Problema: JavaFX não funciona
**Solução**: Verifique se você tem Java 17+ instalado:
```bash
java -version
```

Se precisar reinstalar Maven ou resolver problemas de dependências:
```bash
mvn clean install
```

**Battle Arena** - Sistema de Matchmaking e Agendamento de Batalhas 🎮⚔️

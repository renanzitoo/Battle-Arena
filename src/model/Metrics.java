package model;

public class Metrics {
    private int totalPlayers;
    private int abandonedPlayers;
    private int startedBattles;
    private int finishedBattles;

    private long totalWaitingTime;

    public synchronized void incrementTotalPlayers() {
        totalPlayers++;
    }

    public synchronized void incrementAbandonedPlayers() {
        abandonedPlayers++;
    }

    public synchronized void registerStartedBattle(long waitingTime) {
        startedBattles++;
        totalWaitingTime += waitingTime;
    }

    public synchronized void incrementFinishedBattles() {
        finishedBattles++;
    }

    public synchronized double getAverageWaitingTime() {
        if (startedBattles == 0) {
            return 0;
        }

        return (double) totalWaitingTime / startedBattles;
    }

    public synchronized double getAbandonmentRate() {
        if (totalPlayers == 0) {
            return 0;
        }

        return (double) abandonedPlayers / totalPlayers * 100;
    }

    public synchronized void printFinalReport() {
        System.out.println("\n===== RELATÓRIO FINAL =====");
        System.out.println("Total de jogadores: " + totalPlayers);
        System.out.println("Jogadores que abandonaram: " + abandonedPlayers);
        System.out.println("Batalhas iniciadas: " + startedBattles);
        System.out.println("Batalhas finalizadas: " + finishedBattles);
        System.out.println("Tempo médio de espera: " + getAverageWaitingTime() + "ms");
        System.out.println("Taxa de abandono: " + getAbandonmentRate() + "%");
    }
}
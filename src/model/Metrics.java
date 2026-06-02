package model;

public class Metrics {
    private int totalPlayers;
    private int abandonedPlayers;
    private int startedBattles;
    private int finishedBattles;

    private long totalWaitingTime;
    private double cumulativeUtilization;
    private int utilizationSamples;

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

    public synchronized void updateUtilization(double currentUtilization) {
        cumulativeUtilization += currentUtilization;
        utilizationSamples++;
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

    public synchronized double getAverageUtilization() {
        if (utilizationSamples == 0) {
            return 0;
        }
        return cumulativeUtilization / utilizationSamples;
    }

    public synchronized void printFinalReport() {
        System.out.println("\n===== FINAL REPORT =====");
        System.out.println("Total players: " + totalPlayers);
        System.out.println("Abandoned players: " + abandonedPlayers);
        System.out.println("Started battles: " + startedBattles);
        System.out.println("Finished battles: " + finishedBattles);
        System.out.printf("Average waiting time: %.2fms\n", getAverageWaitingTime());
        System.out.printf("Abandonment rate: %.2f%%\n", getAbandonmentRate());
        System.out.printf("Average system utilization: %.2f%%\n", getAverageUtilization());
    }
}
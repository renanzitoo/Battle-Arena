package model;

public class BattleRequest {
    private QueueRequest playerOneRequest;
    private QueueRequest playerTwoRequest;
    private BattleType battleType;
    private long creationTime;

    public BattleRequest(QueueRequest playerOneRequest, QueueRequest playerTwoRequest) {
        this.playerOneRequest = playerOneRequest;
        this.playerTwoRequest = playerTwoRequest;
        this.battleType = playerOneRequest.getBattleType();
        this.creationTime = System.currentTimeMillis();
    }

    public QueueRequest getPlayerOneRequest() {
        return playerOneRequest;
    }

    public QueueRequest getPlayerTwoRequest() {
        return playerTwoRequest;
    }

    public Player getPlayerOne() {
        return playerOneRequest.getPlayer();
    }

    public Player getPlayerTwo() {
        return playerTwoRequest.getPlayer();
    }

    public BattleType getBattleType() {
        return battleType;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long calculateWaitingTime() {
        return System.currentTimeMillis() - creationTime;
    }

    public int calculatePriorityWithAging() {
        long waitingTimeInSecs = calculateWaitingTime() / 1000;
        int agingBonus = (int) waitingTimeInSecs / 5;

        return battleType.getPriority() + agingBonus;
    }

    public boolean shouldBeCancelled() {
        return calculateWaitingTime() > battleType.getMaxWaitingDurationInMs();
    }

    @Override
    public String toString() {
        return "BattleRequest{" +
                "playerOne=" + getPlayerOne().getName() +
                ", playerTwo=" + getPlayerTwo().getName() +
                ", battleType=" + battleType +
                ", actualPriority=" + calculatePriorityWithAging() +
                ", waitingTime=" + calculateWaitingTime() + "ms" +
                '}';
    }
}
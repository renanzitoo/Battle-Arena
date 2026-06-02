package model;

public enum BattleType {
    CASUAL_MATCH(1,1,5000,10000),
    RANKED_MATCH (2,2,8000,12000),
    TOURNAMENT_MATCH(3,3, 12000,15000);

    private final int priority;
    private final int resourceCost;
    private final long waitingDurationInMs;
    private final long maxWaitingDurationInMs;

    BattleType(
            int priority,
            int resourceCost,
            long durationInMs,
            long maxDurationInMs
    ) {
        this.priority = priority;
        this.resourceCost = resourceCost;
        this.waitingDurationInMs = durationInMs;
        this.maxWaitingDurationInMs = maxDurationInMs;
    }

    public int getPriority(){
        return priority;
    }

    public int getResourceCost(){
        return resourceCost;
    }

    public long getWaitingDurationInMs(){
        return waitingDurationInMs;
    }

    public long getMaxWaitingDurationInMs(){
        return maxWaitingDurationInMs;
    }
}

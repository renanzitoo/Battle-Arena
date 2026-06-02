package model;

public class Battle {
    private BattleRequest request;
    private BattleStatus status;
    private long startTime;
    private long endTime;

    public Battle(BattleRequest request){
        this.request = request;
        this.status = BattleStatus.WAITING;
    }

    public void start(){
        this.status = BattleStatus.RUNNING;
        this.startTime = System.currentTimeMillis();
    }

    public void finish(){
        this.status = BattleStatus.FINISHED;
        this.endTime = System.currentTimeMillis();
    }

    public BattleRequest getRequest(){
        return request;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime(){
        return endTime;
    }

    public long getDuration(){
        if(endTime == 0){
            return System.currentTimeMillis() - startTime;
        }

        return endTime - startTime;
    }

    @Override
    public String toString() {
        return "Battle{" +
                "playerOne=" + request.getPlayerOne().getName() +
                ", playerTwo=" + request.getPlayerTwo().getName() +
                ", type=" + request.getBattleType() +
                ", status=" + status +
                '}';
    }
}

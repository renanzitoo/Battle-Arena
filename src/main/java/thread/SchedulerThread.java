package thread;

import service.BattleSchedulerService;

public class SchedulerThread extends Thread{
    private BattleSchedulerService schedulerService;
    private boolean running = true;

    public SchedulerThread(BattleSchedulerService schedulerService){
        this.schedulerService = schedulerService;
    }

    @Override
    public void run(){
        while(running){
            schedulerService.tryStartNextBattle();
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopScheduler(){
        running = false;
        this.interrupt();
    }
}

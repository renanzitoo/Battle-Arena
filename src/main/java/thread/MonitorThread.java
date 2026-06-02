package thread;

import service.BattleSchedulerService;
import service.MatchmakingService;

public class MonitorThread extends Thread{
    private MatchmakingService matchmakingService;
    private BattleSchedulerService schedulerService;
    private boolean running = true;

    public MonitorThread(
            MatchmakingService matchmakingService,
            BattleSchedulerService schedulerService
    ){
        this.matchmakingService = matchmakingService;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run() {
        while (running) {
            matchmakingService.removeAbandonedRequests();

            matchmakingService.printQueuesStatus();
            schedulerService.printSchedulerStatus();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopMonitor() {
        running = false;
        this.interrupt();
    }
}

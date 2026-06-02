package thread;

import model.BattleRequest;
import model.BattleType;
import service.BattleSchedulerService;
import service.MatchmakingService;

public class MatchmakingThread extends Thread{
    private MatchmakingService matchmakingService;
    private BattleSchedulerService schedulerService;
    private boolean running = true;

    public MatchmakingThread(
            MatchmakingService matchmakingService,
            BattleSchedulerService schedulerService
    ){
        this.matchmakingService = matchmakingService;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run(){
        while(running){
            for (BattleType battleType: BattleType.values()){
                BattleRequest request = matchmakingService.tryCreateBattle(battleType);
                if(request != null){
                    schedulerService.addBattleRequest(request);
                }
            }
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                running = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopMatchmaking(){
        running = false;
        this.interrupt();
    }
}

package thread;

import model.Battle;
import service.BattleSchedulerService;

public class BattleThread extends Thread{
    private Battle battle;
    private BattleSchedulerService schedulerService;

    public BattleThread(Battle battle, BattleSchedulerService schedulerService){
        this.battle = battle;
        this.schedulerService = schedulerService;
    }

    @Override
    public void run(){
        try{
            battle.start();
            System.out.println("Battle begin: " + battle);

            Thread.sleep(
                    battle.getRequest().getBattleType().getWaitingDurationInMs()
            );

            battle.finish();

            System.out.println("Battle finished: " + battle);

            schedulerService.releaseResources(battle);
        } catch (InterruptedException e) {
            System.out.println("Battle interrupted: " + battle);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

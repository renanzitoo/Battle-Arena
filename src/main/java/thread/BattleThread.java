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

            long baseDuration = battle.getRequest().getBattleType().getWaitingDurationInMs();
            long randomDuration = baseDuration + (long) (Math.random() * (baseDuration * 0.5));

            Thread.sleep(randomDuration);

            battle.finish();

            System.out.println("Battle finished: " + battle + " (Duration: " + randomDuration + "ms)");

            schedulerService.releaseResources(battle);
        } catch (InterruptedException e) {
            System.out.println("Battle interrupted: " + battle);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}

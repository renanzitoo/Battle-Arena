import model.*;
import service.BattleSchedulerService;
import service.MatchmakingService;

public class Main {
    public static void main(String[] args) {
        MatchmakingService matchmakingService = new MatchmakingService();
        BattleSchedulerService schedulerService = new BattleSchedulerService(10);

        Player p1 = new Player(1, "xxDarkMage");
        Player p2 = new Player(2, "zzSuperKiller");

        QueueRequest r1 = new QueueRequest(p1, BattleType.RANKED_MATCH);
        QueueRequest r2 = new QueueRequest(p2, BattleType.RANKED_MATCH);

        matchmakingService.addToQueue(r1);
        matchmakingService.addToQueue(r2);

        BattleRequest requisition =
                matchmakingService.tryCreateBattle(BattleType.RANKED_MATCH);

        if(requisition != null){
            schedulerService.addBattleRequest(requisition);
        }

        schedulerService.tryStartNextBattle();

        System.out.println("Main continues!");
    }
}
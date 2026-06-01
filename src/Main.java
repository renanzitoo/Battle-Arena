import model.*;
import service.BattleSchedulerService;
import service.MatchmakingService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MatchmakingService matchmakingService = new MatchmakingService();
        BattleSchedulerService schedulerService = new BattleSchedulerService(5);

        Player p1 = new Player(1, "Renan");
        Player p2 = new Player(2, "Carlos");
        Player p3 = new Player(3, "Ana");
        Player p4 = new Player(4, "Bruno");
        Player p5 = new Player(5, "Julia");
        Player p6 = new Player(6, "Marcos");

        QueueRequest r1 = new QueueRequest(p1, BattleType.RANKED_MATCH);
        QueueRequest r2 = new QueueRequest(p2, BattleType.RANKED_MATCH);

        QueueRequest r3 = new QueueRequest(p3, BattleType.TOURNAMENT_MATCH);
        QueueRequest r4 = new QueueRequest(p4, BattleType.TOURNAMENT_MATCH);

        QueueRequest r5 = new QueueRequest(p5, BattleType.CASUAL_MATCH);
        QueueRequest r6 = new QueueRequest(p6, BattleType.CASUAL_MATCH);

        matchmakingService.addToQueue(r1);
        matchmakingService.addToQueue(r2);
        matchmakingService.addToQueue(r3);
        matchmakingService.addToQueue(r4);
        matchmakingService.addToQueue(r5);
        matchmakingService.addToQueue(r6);

        BattleRequest rankedBattle =
                matchmakingService.tryCreateBattle(BattleType.RANKED_MATCH);

        BattleRequest tournamentBattle =
                matchmakingService.tryCreateBattle(BattleType.TOURNAMENT_MATCH);

        BattleRequest quickBattle =
                matchmakingService.tryCreateBattle(BattleType.CASUAL_MATCH);

        if (rankedBattle != null) {
            schedulerService.addBattleRequest(rankedBattle);
        }

        if (tournamentBattle != null) {
            schedulerService.addBattleRequest(tournamentBattle);
        }

        if (quickBattle != null) {
            schedulerService.addBattleRequest(quickBattle);
        }

        System.out.println("\nTentando iniciar batalhas...\n");

        schedulerService.tryStartNextBattle();
        schedulerService.tryStartNextBattle();
        schedulerService.tryStartNextBattle();

        System.out.println("\nMain continua rodando enquanto as batalhas acontecem...\n");

        Thread.sleep(25000);

        System.out.println("\nTentando iniciar novamente após algumas batalhas finalizarem...\n");

        schedulerService.tryStartNextBattle();
        schedulerService.tryStartNextBattle();
        schedulerService.tryStartNextBattle();
    }
}
package thread;

import service.BattleSchedulerService;
import service.MatchmakingService;

public class MonitorThread extends Thread{
    private MatchmakingService matchmakingService;
    private BattleSchedulerService schedulerService;
    private boolean running = false;
}

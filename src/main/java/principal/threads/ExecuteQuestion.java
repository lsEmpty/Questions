package principal.threads;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import principal.QuestionsPlugin;
import principal.config.manager.MainCustomConfigManager;
import principal.entities.Question;
import principal.service.ShowQuestion;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecuteQuestion {

    private MainCustomConfigManager mainCustomConfigManager;
    private ShowQuestion showQuestion;
    private QuestionsPlugin plugin;
    public static Question lastQuestion;
    public static Long timeout;

    public ExecuteQuestion() {
    }

    public ExecuteQuestion(MainCustomConfigManager mainCustomConfigManager, QuestionsPlugin plugin) {
        this.mainCustomConfigManager = mainCustomConfigManager;
        this.plugin = plugin;
        timeout = mainCustomConfigManager.getWaiting_time() > 0 ? mainCustomConfigManager.getWaiting_time() * 1000L: 30000;
        showQuestion = new ShowQuestion(mainCustomConfigManager, plugin);
    }

    public static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static long startTime;
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int numbers_players = Bukkit.getServer().getOnlinePlayers().size();
                int numbers_of_users_to_run = mainCustomConfigManager.getNumber_of_users_to_run();
                numbers_of_users_to_run = numbers_of_users_to_run > 0 ? numbers_of_users_to_run : 1;
                if (numbers_players >= numbers_of_users_to_run){
                    QuestionsPlugin.changeStateInQuestion();
                    lastQuestion = showQuestion.show();
                    startTime = System.currentTimeMillis();

                    scheduler.schedule(()->{
                        //Show message that say "not answered this question"
                        if (QuestionsPlugin.InQuestion){
                            showQuestion.messageWhenTheUsersDoNotAnswerTheQuestion(lastQuestion);
                            QuestionsPlugin.changeStateInQuestion();
                        }
                    }, mainCustomConfigManager.getWaiting_time(), TimeUnit.SECONDS);
                }
            }
        }.runTaskTimer(plugin, 0, mainCustomConfigManager.getTime_range_to_execute() * 20L);
    }
}

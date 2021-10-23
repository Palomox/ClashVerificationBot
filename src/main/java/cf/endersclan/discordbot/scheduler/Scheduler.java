package cf.endersclan.discordbot.scheduler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
	private ScheduledExecutorService pool;

	public Scheduler() {
		this.pool = Executors.newScheduledThreadPool(1);
	}

	public void scheduleEveryDay(Runnable r, int hour, int minute, int second){
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(second);
		Duration difference = Duration.between(now, nextRun);
		if(difference.isNegative()) {
			nextRun = nextRun.plusDays(1);
		}
		difference = Duration.between(now, nextRun);
		this.pool.scheduleAtFixedRate(r,
				difference.toSeconds(),
				TimeUnit.DAYS.toSeconds(1),
				TimeUnit.SECONDS);
	}
	public void destroy() {
		this.pool.shutdown();
		try {
			this.pool.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

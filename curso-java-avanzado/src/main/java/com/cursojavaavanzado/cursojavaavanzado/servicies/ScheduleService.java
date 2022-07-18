package com.cursojavaavanzado.cursojavaavanzado.servicies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleService {
	//Esta clase se encargará de ejecutarse automaticamente para guardar las paginas en la bd

	@Autowired
	private SpiderService spiderService;
	

	
	/*
	 * se planifica la ejecucion del metodo de manera automatica. Los valores son
	 *
	 * @Schedule(cron = "second minute hour dayOfMonth month dayOfWeek)
	 *
	 *Los valores * indica que puede ser cualquier valor y el ? parece que va si o si
	*/
	@Scheduled(cron = "0 0 0 * * ?")
	public void scheduleIndexWebPages() {
		this.spiderService.indexWebPages();
	}
}

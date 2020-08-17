package com.rootmind.servlet;

//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.google.appengine.api.ThreadManager;
import com.rootmind.helper.FCMNotification;

//import org.quartz.CronScheduleBuilder;
//import org.quartz.JobBuilder;
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.Trigger;
//import org.quartz.TriggerBuilder;
//import org.quartz.ee.servlet.QuartzInitializerListener;
//import org.quartz.impl.StdSchedulerFactory;

//import com.rootmind.helper.FCMNotification;
//import com.rootmind.helper.JobScheduler;

@WebListener
public class QuartzListener implements ServletContextListener {

	
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	
	public void contextInitialized(ServletContextEvent sce) {
		// Your logic for starting the thread goes here - Use Executor Service
		
		System.out.println("QuartzListener  scheduledThreadPoolExecutor contextInitialized");
		
		//this was commented on 06JUL2019 while deploying in internal server to avoid error
//		ThreadFactory threadFactory = ThreadManager.currentRequestThreadFactory();
//		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, threadFactory);
//		scheduledThreadPoolExecutor.scheduleWithFixedDelay(new FCMNotification(), 0, 60, TimeUnit.SECONDS);
		
		
		//this is old comment
		//scheduledExecutorService = Executors.newFixedThreadPool(1, threadFactory);   //    newSingleThreadScheduledExecutor();
		//scheduledExecutorService.execute(new FCMNotification());
		//scheduledExecutorService.scheduleAtFixedRate(new FCMNotification(), 0, 30, TimeUnit.SECONDS);
		//ThreadManager.currentRequestThreadFactory().newThread(new FCMNotification()).start();
		//scheduledExecutorService = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS).
		//scheduledExecutorService.execute(new FCMNotification());
		
		
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// Your logic for shutting down thread goes here
		
		scheduledThreadPoolExecutor.shutdown();
		
		System.out.println("QuartzListener scheduledThreadPoolExecutor contextDestroyed");

		
	}
	
	
	// //extends QuartzInitializerListener
	// @Override
	// public void contextInitialized(ServletContextEvent sce) {
	// super.contextInitialized(sce);
	// ServletContext ctx = sce.getServletContext();
	// StdSchedulerFactory factory = (StdSchedulerFactory)
	// ctx.getAttribute(QUARTZ_FACTORY_KEY);
	// try {
	// Scheduler scheduler = factory.getScheduler();
	// JobDetail jobDetail = JobBuilder.newJob(JobScheduler.class).build();
	// Trigger trigger =
	// TriggerBuilder.newTrigger().withIdentity("simple").withSchedule(
	// CronScheduleBuilder.cronSchedule("0 0/1 * 1/1 * ? *")).startNow().build();
	// //the default number of threads is 10
	// scheduler.scheduleJob(jobDetail, trigger);
	// scheduler.start();
	// } catch (Exception e) {
	// ctx.log("There was an error scheduling the job.", e);
	// }
	// }

	

}

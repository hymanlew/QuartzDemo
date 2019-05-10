package quartzDemo.demo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;
import org.quartz.listeners.JobListenerSupport;
import quartzDemo.MyJob;

public class ListenerDemo {

	public static void main(String[] args) {
		/**
		 * TriggerListener 接口：
		 * Trigger 事件包括:触发触发，触发错误触发(在本文档的“触发器”部分中讨论)，触发完成(触发器触发的工作完成)。
		 *
		 * JobListener 接口：
		 * 与 Job 相关的事件包括:任务即将被执行的通知，以及任务完成执行时的通知。
		 *
		 * 自定义 Listeners：
		 * 要创建一个侦听器(Listener)，只需创建一个实现了 org.quartz.TriggerListener 或者 org.quartz.JobListener 接口。
		 * 在运行时侦听器会在调度程序中注册，并且必须给定一个名称(更确切地说，它们必须通过 getName() 方法为自己的名称做广告)。
		 * 除了实现这些接口之外，还可以继承 JobListenerSupport 或 TriggerListenerSupport，并简单地覆盖您感兴趣的事件。
		 *
		 * 侦听器在调度器的 ListenerManager 上注册，并附带一个 Matcher，用来描述侦听器想要接收事件的工作/触发器。侦听器在运行
		 * 时在调度器中注册，并且不会与作业和触发器一起存储在JobStore中。这是因为侦听器通常是应用程序的集成点。因此每次应用程序
		 * 运行时，都需要将侦听器重新注册到调度器中。
		 */
		JobDetail job = JobBuilder.newJob(MyJob.class)
				.withIdentity("job1","group1")
				.usingJobData("name","hyman")
				.usingJobData("value",331)
				.build();
	}

	public static void triggerlis(){

	}

	public static void joblis(){
		try {
			Matcher<JobKey> matcher = KeyMatcher.keyEquals(new JobKey("job1", "group1"));

			GroupMatcher<JobKey> matchers = GroupMatcher.jobGroupEquals("group1");
			GroupMatcher<JobKey> startsWithmatcher = GroupMatcher.groupStartsWith("g");
			GroupMatcher<JobKey> containsmatcher = GroupMatcher.groupContains("g");

			StdSchedulerFactory.getDefaultScheduler()
					.getListenerManager()

					// 全局注册,所有Job都会起作用
					// .addJobListener(new MyJobListener());
					// 指定具体的任务
					// .addJobListener(new MyJobListener(),matcher);
					// 指定一组任务
					// .addJobListener(new MyJobListener(),matchers);
					// 可以根据组的名字匹配开头和结尾或包含
					.addJobListener(new MyJobListener(),containsmatcher);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static void schedulelis(){
		/**
		 * 调度器很像触发监听器(TriggerListeners)和工作监听器(JobListeners)，除非它们接收到调度程序本身中的事件通知。即不一定
		 * 是与特定触发器或作业相关的事件。
		 * 与调度相关的事件包括:添加作业/触发器、删除作业/触发器、调度程序中的严重错误、调度程序被关闭的通知等。
		 * 它是在调度器的 ListenerManager 注册的。可调度器实际上也可以是实现 org.quartz.SchedulerListener 接口。
		 */
		// 添加
		// scheduler.getListenerManager().addSchedulerListener(mySchedListener);
		// 移除
		// scheduler.getListenerManager().removeSchedulerListener(mySchedListener);
	}
}

class MyJobListener extends JobListenerSupport {
	@Override
	public String getName() {
		return "MyJobListener";
	}

     /**
      * 任务执行之前执行
      * Called by the Scheduler when a JobDetail is about to be executed (an associated Trigger has occurred).
      */
     @Override
     public void jobToBeExecuted(JobExecutionContext context) {
         System.out.println("MyJobListener.jobToBeExecuted()");
     }

     /**
      * 这个方法正常情况下不执行,但是如果当TriggerListener中的vetoJobExecution方法返回true时,那么执行这个方法.
      * 需要注意的是 如果方法(2)执行 那么(1),(3)这个俩个方法不会执行,因为任务被终止了嘛.
      * Called by the Scheduler when a JobDetail was about to be executed (an associated Trigger has occurred),
      * but a TriggerListener vetoed it's execution.
      */
     @Override
     public void jobExecutionVetoed(JobExecutionContext context) {
         System.out.println("MyJobListener.jobExecutionVetoed()");
     }

     /**
      * (3)
      * 任务执行完成后执行,jobException如果它不为空则说明任务在执行过程中出现了异常
      * Called by the Scheduler after a JobDetail has been executed, and be for the associated Trigger's triggered(xx) method has been called.
      */
     @Override
     public void jobWasExecuted(JobExecutionContext context,
             JobExecutionException jobException) {
         System.out.println("MyJobListener.jobWasExecuted()");
     }
}
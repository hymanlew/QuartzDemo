package quartzDemo.demo;

import org.quartz.*;
import quartzDemo.MyJob;

import java.util.Date;

public class SimTriggerDemo {

	public static void main(String[] args) {
		JobDetail job = JobBuilder.newJob(MyJob.class)
				.withIdentity("job1","group1")
				.usingJobData("name","hyman")
				.usingJobData("value",331)
				.build();

		// SimpleTrigger
		simtrigger(job);
		// CronTrigger
		crontrigger(job);
	}

	public static void simtrigger(JobDetail job){
		/**
		 * SimpleTrigger，可以在特定的时间间隔内重复执行某个特定的任务，即在特定的时间内精确地执行一次任务。
		 * 该实例是使用 TriggerBuilder (用于触发器的主要属性)和 SimpleScheduleBuilder (针对SimpleTrigger特定属性)构建的。
		 * 一个简单触发器的属性包括:开始时间、结束时间、重复计数和重复间隔。
		 *
		 * 重复计数可以是零，一个正整数，或者常量值 SimpleTrigger.REPEAT_INDEFINITELY。
		 * 重复的间隔属性必须为零，或正长的值，并表示若干毫秒。注意，重复的零间隔将导致触发器的“重复计数”触发同时发生(或者与
		 * 调度程序可以同时执行的情况类似)。
		 *
		 * DateBuilder 类，有助于计算触发器的触发时间，这取决于您试图创建的startTime(或endTime)。
		 * endTime属性(如果指定的话)会覆盖重复计数属性。如果希望创建一个触发器每10秒触发一次,直到一个给定的时刻,而不是以计算的
		 * 次数结束，那么就可以简单地指定的 endTime, 然后使用重复计数REPEAT_INDEFINITELY(你甚至可以指定一些大量的重复计算,以保证
		 * endTime 先结束即可)。
		 */
		Date starttime = new Date();

		//	在特定的时间内建立一个触发点，不要重复:
		SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity("trigger1", "group1")
				.startAt(starttime) // some Date
				.forJob("job1", "group1") // identify job with name, group strings
				.build();

		//  在特定的时间内建立一个触发点，然后每10秒重复10次:
		trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
				.withIdentity("trigger3", "group1")
				.startAt(starttime)  // if a start time is not given (if this line were omitted), "now" is implied
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(10)
						.withRepeatCount(10)) // note that 10 repeats will give a total of 11 firings
				.forJob(job) // identify job with handle to its JobDetail itself
				.build();

		//	立刻执行。并且 5min 重复一次，直到 22:00
		trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger7", "group1")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes(5)
						.repeatForever())
				.endAt(dateOf(22, 0, 0))
				.build();

		/**
		 * SimpleTrigger 失败说明：
		 * SimpleTrigger有几个指令，可以用来告诉 Quartz 在发生错误时应该做什么。这些指令被定义为简单触发器本身的常量(包
		 * 括描述其行为的JavaDoc)。包括:
		 * MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY，此指令是所有触发器类型的默认指令。
		 * MISFIRE_INSTRUCTION_FIRE_NOW
		 * MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT
		 * MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
		 * MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT
		 * MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT
		 *
		 * 如果使用智能策略(smart policy)指令，基于给定的 SimpleTrigger实例的配置和状态，SimpleTrigger会在其各种错误指令
		 * 之间进行动态选择。SimpleTrigger.updateAfterMisfire() 方法解释了这种动态行为的具体细节。
		 * 在构建 SimpleTrigger 时，您可以将 misfire指令指定为简单调度的一部分，(通过SimpleSchedulerBuilder):
		 */
		trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger7", "group1")
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInMinutes(5)
						.repeatForever()
						.withMisfireHandlingInstructionNextWithExistingCount())
				.build();
	}

	public static void crontrigger(JobDetail job){
		/**
		 * CronTrigger 通常比 SimpleTrigger 更有用，它可以根据日历类的概念而不是精确指定的简单触发器间隔来重新定义作业调度。
		 * 你可以指定诸如“每个周五中午”，或“每个工作日和上午9:30”，甚至“每星期一、星期三和周五上午9:00到10:00之间的每5分钟”。
		 * 即使是这样，像SimpleTrigger一样，CronTrigger有一个startTime，它指定调度何时生效，以及一个(可选的)endTime，指定何时停止调度。
		 *
		 * cron 表达式用于配置 CronTrigger 的实例。cron 表达式是由七个子表达式组成的字符串，它们描述了日程的各个细节。这
		 * 些子表达式与空白区分开，并表示（其作用与 spring 的任务表达式 cron 是相同的）:
		 * Seconds，Minutes，Hours，Day-of-Month，Month，Day-of-Week，Year (optional field)
		 *
		 * CronTrigger 实例是使用 TriggerBuilder 和 CronScheduleBuilder (用于CronTrigger-specific属性)构建的。
		 */
		//	每隔两分钟，每天早上8点到下午5点之间，建立一个触发点。
		CronTrigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger3", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 8-17 * * ?"))
				.forJob("myJob", "group1")
				.build();

		// 建立一个触发器，在周三上午10:42，在指定的时间范围内触发（inTimeZone 方式不可用）。
		trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger3", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 42 10 ? * WED"))
				.startAt(dateOf(0,0,0))
				.endAt(dateOf(0,0,0))
				.forJob(job)
				.build();

		/**
		 * CronTrigger 失败说明，其指令被定义为CronTrigger本身的常量(包括描述其行为的JavaDoc)。
		 * MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY，此指令是所有触发器类型的默认指令。
		 * MISFIRE_INSTRUCTION_DO_NOTHING
		 * MISFIRE_INSTRUCTION_FIRE_NOW
		 *
		 * “智能策略”指令由 CronTrigger 也会在其各种错误指令之间进行动态选择。
		 * CronTrigger.updateAfterMisfire() 方法解释这种行为的具体细节。
		 * 在构建 CronTrigger 时，您可以将 misfire 指令指定为简单调度的一部分(通过cronschedule erbuilder):
		 */
		trigger = TriggerBuilder.newTrigger()
				.withIdentity("trigger3", "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 8-17 * * ?")
						.withMisfireHandlingInstructionFireAndProceed())
                .forJob("myJob", "group1")
				.build();

	}

	// 返回指定的时间
	public static Date dateOf(int a, int b, int c){
		return new Date();
	}
}

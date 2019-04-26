import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class AppMain {

    public static void main(String[] args) {
        // 定义 job 实例
        /**
         * JobDataMap 可以用来保存任何数量(可序列化)的数据对象，并且它执行时将会把自己提供给作业实例。它是 Java Map 接口
         * 的一个实现，提供了一些用于存储和检索原始类型数据的便利方法。
         */
        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity("job1","group1")
                .usingJobData("name","hyman")
                .usingJobData("value",331)
                .build();

        /**
         * 在 sql 中存储过程 procedure，相当于一个封装的方法，可以手动或自动调用。
         * 而 trigger 是一种特殊的存储过程，因为它是依托于事件，而自动触发执行的。
         */
        /**
         * 当作业(job) 的触发器(trigger) 触发时(startNow)，execute()方法由一个调度程序的工作线程调用。传递给该方法的
         * JobExecutionContext 对象提供了有关其“运行时”环境信息的作业实例（包括执行它的调度程序的句柄、触发执行的触
         * 发器的句柄、作业的 JobDetail 对象和其他一些项）。
         *
         * JobDetail 对象是由 Quartz 客户机(即本程序)在任务添加到调度器时创建的。它包含作业的各种属性设置，以及 JobDataMap
         * 它用于存储作业类的给定实例的状态信息。它本质上就是作业实例的定义。
         *
         * 触发器对象用于触发作业的执行。当希望调度作业时，要先实例化一个触发器并调整其属性，以定制化调度。触发器也可能
         * 有与它们相关联的JobDataMap（这对于将参数传递给特定于触发器触发的作业是有用的。带有少量不同触发器类型的Quartz
         * 船，但最常用的类型是SimpleTrigger和CronTrigger）。
         *
         * 如果需要一次性执行(仅在给定时刻执行一项任务)，或者需要在给定的时间内解雇一份工作，并且重复执行N次，在执行期
         * 间延迟执行，那么 SimpleTrigger 非常方便。
         * 如果希望基于日历的日程安排(比如“每个周五中午”或“每月10日10时15分”触发，则 CronTrigger 是很有用的。
         *
         * 在开发 Quartz 时，在时间表和在该计划上执行的工作之间创建分离是有意义的。例如可以在作业调度器中创建和存储作业，
         * 独立于触发器，并且许多触发器可以与相同的作业相关联。这种松耦合的另一个好处是，在相关触发器过期之后，可以配置
         * 在调度器中保留的作业，以便以后可以重新调度，而不必重新定义它。它还允许您修改或替换触发器，而不必重新定义其关
         * 联的作业。
         *
         * Identities
         * 当 Job 和 Triggers 在 Quartz 调度器中注册时，它们被赋予标识键(Identities)。并且此标识键就是它们的键( JobKey
         * 和 TriggerKey )，此键并允许它们被放置到“组”(Group)中，这些“组”对组织工作和触发“报告工作”和“维护工作”
         * 等类别非常有用。
         * 作业或触发器的键的名称部分必须在组内是惟一的，即作业或触发器的完整密钥(或标识符)是名称和组的复合。
         */
        // 触发器，每 5S 执行一次我们的 Job
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1","group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())
                .build();

        try {
            /**
             * 调度器：
             * Scheduler 的生命周期受它的创建限制，通过一个 SchedulerFactory 和对其 shutdown() 方法的调用。一旦创建了
             * 调度程序接口，就可以使用添加、删除和列出作业和触发器，并执行其他与调度相关的操作(例如暂停触发器)。但是
             * 调度器实际上不会在任何触发器(执行作业)上起作用，直到它从 start() 方法开始。
             *
             * 当调度程序执行任务时，每个(以及每个)时间都在调用它的 execute() 方法，并在调用之前都创建一个作业类（MyJob）
             * 的新实例。当执行完成时，对作业类实例的引用被删除，然后实例被垃圾收集。
             * 这种行为的一个分支是，作业必须有一个无参数的构造函数(当使用默认的 JobFactory 实现时)。
             * 另一个分支是，在作业类上定义状态数据字段是没有意义的，因为它们的值不会在作业执行之间保留。
             *
             * 而为一个作业实例提供属性或配置，并在执行过程中记录工作状态，这就要用到 JobDataMap，它是 JobDetail 对象的一部分。
             *
             */
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            scheduler.scheduleJob(job,trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
            System.out.println("========= error ============");
        }
    }
}

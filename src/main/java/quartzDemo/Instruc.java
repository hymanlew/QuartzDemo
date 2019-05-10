package quartzDemo;

public class Instruc {
    /**
     * JobDetail 的生命周期：
     * 当调度程序执行任务时，每个(以及每个)时间都在调用它的 execute() 方法，并在调用之前都创建一个作业类（quartzDemo.MyJob）的新实例。当
     * 执行完成时，对作业类实例的引用被删除，然后实例被垃圾收集。
     * 这种行为的一个分支是，作业必须有一个无参数的构造函数(当使用默认的 JobFactory 实现时)。
     * 另一个分支是，在作业类上定义状态数据字段是没有意义的，因为它们的值不会在作业执行之间保留。
     *
     * JobDetail 对象为作业实例定义的其他属性：
     * 1，Durability：如果一个工作是非持久的，它会自动从调度器中删除，一旦不再有任何与它相关联的活动触发器。换句话说，非耐
     *    久的工作的生命周期是由其触发器的存在所限制的。
     * 2，RequestsRecovery：如果一个作业“请求恢复”，并且它在调度程序的“硬关闭”(hard shutdown)期间执行(即它在崩溃中运
     *    行的进程，或者机器被关闭)，那么当调度程序再次启动时，它将被重新执行。在这种情况下 JobExecutionContext.isRecovering()
     *    方法将返回true。
     *
     * Trigger 属性:
     * 除了所有触发器类型都有触发键(TriggerKey)属性来跟踪它们的特性之外，还有许多其他属性对所有触发器类型都是通用的。 当您正在
     * 构建触发器定义时，这些公共属性设置为 TriggerBuilder。
     *
     * jobKey，指示在触发器触发时应该执行的作业的标识。
     * startTime，指示触发器的调度何时开始生效。该值是一个 java.util.date 对象，它在给定的日历日期上定义了一个时间点。对于某些
     *      触发器类型，触发器在开始时实际上会触发，而对于其他触发器，它只是标记了该调度应该开始执行的时间。这意味着你可以在1月
     *      份的时候用“每月5天”这样的时间表来存储一个触发器，如果 startTime 属性设置为4月1日，那么就会在第一次触发前几个月。
     * endTime，指示何时不再执行触发器的调度。即在6月5日的最后一段时间里，一个“每个月5日”和7月1日的结束时间的触发点将会被触发。
     *
     * Trigger Priority：
     * 当有许多触发器(或您的石英线程池中很少的工作线程)时，Quartz可能没有足够的资源来立即触发所有计划在同一时间触发的触发器。
     * 在这种情况下，需要控制哪些触发器在可用的Quartz工作线程上优先被执行（即在触发器上设置优先级属性）。如果 N 触发器同时触发，
     * 但是目前只有 Z 工作者线程可用，那么优先级最高的第一个 Z 触发器将首先被执行。
     * 如果您没有在触发器上设置优先级，那么它将使用默认的优先级5。任何整数值都被允许为优先级，正或负。
     *
     * 注意：
     * 优先级仅用于相同的触发时间。即预定在10:59的触发点总是在一个预定在11点首先被触发。
     * 恢复的优先级。当检测到触发器的工作需要恢复时，它的恢复计划与最初的触发器相同。
     *
     * 触发器的另一个重要特性是它的“错误指令”：
     * 如果一个持久的触发器“错过”了它的触发时间，因为调度程序正在被关闭，或者因为 Quartz 的线程池中没有可用的线程来执行任务，
     * 那么就会发生错误。不同的触发器类型有不同的错误指示。默认情况下他们使用“智能策略”指令——基于触发器类型和配置的动态行为。
     * 当调度器启动时，它会搜索任何被错误触发的持久触发器，然后根据各自配置的错误指示更新每个触发器。
     * 所以一定要熟悉在给定触发器类型上定义的错误指示，并在其JavaDoc中解释。关于错误指示的更详细的信息将在每个触发类型的教程中给出。
     *
     */
}
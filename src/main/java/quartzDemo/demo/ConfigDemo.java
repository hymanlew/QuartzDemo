package quartzDemo.demo;

/**
 * Quartz的架构是模块化的，因此要让它运行几个组件，需要将其“断开”。在 Quartz能够完成其工作之前，需要配置的主要组件是:
 *
 * 1，ThreadPool：提供了一组用于执行作业时使用 Quartz 的线程。池中线程越多，可以并发运行的作业数量就越多。但是太多的线程可能会
 *    使系统崩溃。5个左右的线程就足够多，因为在任何给定的时间内，他们的工作岗位都少于100个，而这些工作通常不会同时运行，而且工作
 *    时间很短(完成得很快)。如果需要10、15、50甚至100个线程，则是因为他们有成千上万个不同时间表的触发点，即在任何给定的时刻，他
 *    们平均要执行10到100个任务。为调度器的池找到合适的大小完全取决于您使用调度器的用途。
 *    除了保持线程数量尽可能小(为了系统资源)之外，没有真正的规则，但是要确保您有足够的时间让您的工作按时启动。
 *
 *    请注意，如果触发的触发时间到达并且没有可用的线程，Quartz将阻塞(暂停)，直到有一个线程可用，然后任务将执行。那么这样就比它应
 *    该执行的时间晚几毫秒。这甚至可能导致线程失火(MisFire)。如果调度程序配置的“misfire阈值”(misfire threshold)的持续时间没有
 *    可用线程的话。
 *    在 org.quartz.spi 包中定义了一个ThreadPool接口。可以用任何方式创建一个ThreadPool实现。Quartz 附带一个简单(但非常满意)线
 *    程池org.quartz.simpl.SimpleThreadPool。这个ThreadPool 在它的池中维护一组固定的线程，从不增长从不收缩。但是它是非常健壮
 *    的，并且经过了很好的测试——几乎所有使用 Quartz 的人都使用这个池。
 *
 * 2，JobStores & DataSources：值得注意的是，所有的 jobstore都实现了 org.quartz.spi.JobStore 接口。如果其中一个绑定的JobStore
 *    不符合需求，那么可以自己创建。
 *
 * 3，Scheduler：调度程序本身需要给定一个名称，告诉它的 RMI设置，以及一个JobStore和ThreadPool的实例。
 *    RMI设置包括：调度器是否应该创建自己作为 RMI的服务器对象(使自己可以用于远程连接)、主机和端口的使用等。StdSchedulerFactory
 *    也可以生成调度实例，它们实际上是远程进程中创建的调度程序的代理(RMI存根)。
 *
 * 4，StdSchedulerFactory 是 org.quartz.SchedulerFactory 的一个实现。它使用一组属性(java.util.Properties)来创建和初始化一个
 *    Quartz 调度器。属性通常存储在文件中并从文件中加载，但也可以由程序创建并直接交给工厂。在工厂中简单地调用 getScheduler()将
 *    生成调度程序、初始化它(以及它的ThreadPool、JobStore和DataSources)，并返回它的公共接口的句柄。
 *    在 Quartz发行版的 docs/config目录中，有一些示例配置和属性描述。在Quartz文档的“参考”部分的“配置”手册中找到完整的文档。
 *
 * 5，DirectSchedulerFactory 是另一个调度程序工厂实现。对于希望以更程序化的方式创建调度实例的人来说，这是很有用的。而它的使用通
 *    常是由于以下原因而被阻止的:
 *    (1)它要求用户对他们正在做的事情有一个更大的理解。(2)它不允许声明式配置，或者换句话说，你最终会硬编码所有调度器的设置。
 *
 * 6，Logging：Quartz使用 SLF4J框架来满足所有日志记录需求。获取额外信息触发解雇和执行工作，可以参考：
 *    org.quartz.plugins.history.LoggingJobHistoryPlugin & org.quartz.plugins.history.LoggingTriggerHistoryPlugin
 *
 * 高级特性：
 * 集群目前使用 JDBC-Jobstore（JobStoreTX 或 JobStoreCMT）和TerracottaJobStore。特性包括负载平衡和工作故障转移(如果 JobDetail
 * 的“请求恢复”标志设置为true)。
 * 通过设置“org.quartz.jobStore”，将集群与 JobStoreTX 或 JobStoreCMT 进行集群。isClustered”属性“true”。集群中的每个实例
 * 都应该使用相同的 quartz副本及属性文件。并使用以下允许的例外:不同的线程池大小，以及“org.quartz.scheduler”的不同值。instanceId
 * 属性。集群中的每个节点都必须具有唯一的instanceId，通过将“AUTO”作为该属性的值，可以轻松完成(不需要不同的属性文件)。
 *
 * 注意：
 * (1)不要在单独的机器上运行集群，除非它们的时钟是同步的，使用某种形式的时间同步服务(守护进程)，它运行得非常频繁(时钟必须在彼此
 *    之间的秒内)。见http://www.boulder.nist.gov/timefreq/service/its.htm 如果您不熟悉如何做到这一点。
 * (2)不要在任何其他实例运行的同一组表上启动非集群实例。您可能会得到严重的数据损坏，并且肯定会经历不稳定的行为。
 *
 * 只有一个节点会触发每次发射的任务。我的意思是,如果工作有重复触发告诉火每10秒,然后在12:00:00将运行一个节点工作,和12:00:10将运
 * 行一个节点工作等等。每次都不一定是相同的节点,它或多或少会随机节点运行它。对于繁忙的调度程序(很多触发器)，负载平衡机制几乎是随
 * 机的，但它倾向于只对非繁忙的(例如一个或两个触发器)调度程序进行活动的同一个节点。
 *
 * 使用 TerracottaJobStore集群，只需配置调度程序，使用 TerracottaJobStore 您的调度器将全部用于集群。可能还需要考虑如何设置
 * Terracotta服务器，特别是启用诸如持久性等特性的配置选项，以及为HA运行一系列 Terracotta服务器。
 * TerracottaJobStore的企业版提供了高级的Quartz，它允许将工作的智能目标定向到适当的集群节点。
 *
 * JTA Transactions:
 * JobStoreCMT 允许在较大的 JTA事务中执行 Quartz调度操作。通过设置 org.quartz.scheduler.wrapJobExecutionInUserTransaction
 * 属性为 true。使用此选项集，在调用作业执行方法之前将启动一个JTA事务，并在执行终止调用后提交。这适用于所有的工作。
 * 如果想要在每个作业中显示 JTA事务是否应该包装它的执行，那么应该在作业类上使用 @ExecuteInJTATransaction 注释。
 * 除了在 JTA事务中自动执行任务执行的 Quartz之外，在使用 JobStoreCMT时，在调度器接口上的调用也会参与事务。在调用调度程序的方法
 * 之前确保已经启动了一个事务。可以通过使用 UserTransaction直接完成这一操作，也可以使用容器管理事务,即 SessionBean中的调度器。
 *
 * Plug-Ins：Quartz 提供了一个接口(org.quartz.spi.SchedulerPlugin）插入识别 j2ee的附加功能。可以在 org.quartz.plugins 文件夹
 * 下找到与 Quartz一起提供各种实用功能的插件。
 * 它们提供了一些功能，例如在调度器启动时自动调度作业、记录作业历史和触发事件，并确保当 JVM退出时调度程序会自动关闭。
 *
 * JobFactory：
 * 当触发器触发时，它所关联的作业将通过调度程序中配置的 JobFactory实例化。默认的 JobFactory只调用作业类中的 newInstance()。如
 * 果希望创建自己的 JobFactory实现来完成一些事情（比如使用 IoC 或 DI容器生成/初始化作业实例），可以参考 org.quartz.spi.JobFactory
 * 接口和相关的 Scheduler.setJobFactory(fact) 方法。
 *
 * ‘Factory-Shipped’Jobs：
 * Quartz 还提供了一些实用的工作，您可以在应用程序中使用它来执行诸如发送电子邮件和调用 ejb 的工作。这些开箱即用的工作可以在
 * org.quartz..jobs 包中找到。
 *
 */
public class ConfigDemo {
}

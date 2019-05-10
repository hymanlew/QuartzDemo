package quartzDemo;

import org.quartz.*;

/**
 * @DisallowConcurrentExecution， 注解在工作类上,使 Quartz 不要执行一个给定的工作定义的多个实例(即给定的工作类)。
 * @PersistJobDataAfterExecution， 注解在工作类上,, 使 Quartz 更新存储（即复制JobDetail JobDataMap的 execute() 方法成功
 *      完成后的值(没有抛出异常)。这样下一个执行同样的工作(JobDetail)时就能收到更新后的值而不是原先存储的值。而 @Disallow
 *      ConcurrentExecution注释，它只适用于定义工作实例,而类实例不只是一份工作,即它可以创建多个实例。
 *
 * 并且如果使用 @PersistJobDataAfterExecution 注释,您还应该考虑使用 @DisallowConcurrentExecution 注释，以避免可能的混乱。
 * 即竞争条件下的数据被存储在相同的工作的两个实例(JobDetail)而又并发执行。
 */
public class MyJob implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobKey key = context.getJobDetail().getKey();
        // JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        /**
         * 如果是使用持久的 JobStore，则应该在确定JobDataMap中的位置时使用一些谨慎，因为它中的对象将被序列化，因此它们
         * 容易出现类版本问题。
         * 显然，标准的Java类型应该是非常安全的，但是除此之外，任何时候如果有人更改了您已经序列化实例的类的定义，就必须
         * 注意不要破坏兼容性。您可以选择将 JDBC-JobStore 和 JobDataMap 放到一个模式中，其中只有原语和字符串被允许存储
         * 在映射中，从而消除了以后序列化问题的可能。
         *
         * 如果添加了 setter 方法工作类,则 Quartz 的 JobFactory 会默认自动调用这些 setter 将工作实例化,从而防止需要显式
         * 地获得 map 的值在执行方法中。
         * 触发器还可以有与之关联的 JobDataMaps。如果您有一个在调度器中存储的作业，它可以通过多个触发器定期/重复地使用，
         * 但是每个独立触发，您想要提供不同数据输入的作业，这一点很有用。
         *
         * 在作业执行期间用 JobExecutionContext 找到 JobDataMap。
         */
        JobDataMap dataMap = context.getMergedJobDataMap();

        String name = dataMap.getString("name");
        int value = dataMap.getIntValue("value");
        System.out.println("Instance " + key +"："+name+ " = " +value);

        /**
         * JobStore 负责跟踪您给调度器的所有“工作数据”:作业、触发器、日历等等。为 Quartz scheduler实例选择合适的 JobStore
         * 是一个重要步骤。幸运的是，一旦你理解了它们之间的区别，选择应该是非常简单的。可以在属性文件(或对象)中声明您的
         * 调度器应该使用哪个 JobStore (以及它的配置设置)，并使用它来生成调度程序实例。
         *
         * 注意不要直接在代码中使用 JobStore实例。JobStore 是用于 Quartz本身的幕后使用的。所以必须通过配置告诉 Quartz 使
         * 用哪个JobStore，但是应该只在代码中使用调度器接口。
         *
         *
         * RAMJobStore 是最简单的使用的 JobStore，它也是性能最好的(在 CPU 时间方面)。它是以一种显而易见的方式获得它的名
         * 称，它将所有数据保存在 RAM 中。这就是为什么它是闪电般的快速，以及为什么它如此简单的配置。
         * 缺点是当您的应用程序结束(或崩溃)时所有的调度信息都将丢失，这意味着 RAMJobStore 不能对作业和触发器的“非波动”
         * 设置表示支持。对于某些应用程序，这是可以接受的，甚至是期望的行为，但对于其他应用程序，这可能是灾难性的。
         *
         * 要使用 RAMJobStore(并假设使用的是 StdSchedulerFactory)，只需指定类名称 org.quartz.simpl。RAMJobStore 作为 JobStore
         * 类属性，用于配置 quartz：
         * org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
         *
         *
         * JDBCJobStore 它通过 JDBC 将所有数据保存在一个数据库中。因此配置比 RAMJobStore要复杂一些，而且也不那么快。但是
         * 性能恢复比较好，特别是如果在主键上构建了带有索引的数据库表。在相当现代的具有良好 LAN(在调度器和数据库之间)的
         * 机器上，检索和更新触发触发器的时间通常不超过10毫秒。
         *
         * JDBCJobStore 几乎可以与任何数据库一起使用，已经广泛应用于Oracle、PostgreSQL、MySQL、MS SQLServer、HSQLDB和DB2。
         * 要使用JDBCJobStore，必须首先创建一组用于 Quartz的数据库表。可以在Quartz发行版的 docs/dbTables目录中找到创建表
         * 的SQL脚本。如果没有用于数据库类型的脚本，只需查看现有的数据库类型，并以任何必要的方式修改数据库。需要注意的一
         * 点是，在这些脚本中所有的表都以前缀QRTZ_”`开头。这个前缀实际上可以是任何东西，但前提是通知 JDBCJobStore前缀是
         * 什么(在您的Quartz属性中)。使用不同的前缀可能有助于在同一个数据库中为多个调度器实例创建多个表集。
         *
         * 一旦创建了表，在配置和激活 JDBCJobStore之前，需要做一个更重要的决策。您需要决定应用程序需要哪种类型的事务。如
         * 果不需要将调度命令(例如添加和删除触发器)绑定到其他事务，那么可以使用 JobStoreTX 作为 JobStore(这是最常见的选择)，
         * 让Quartz管理事务。
         *
         * 如果您需要Quartz与其他事务一起工作(例如在J2EE应用服务器中)，那么您应该使用 JobStoreCMT ——在这种情况下，Quartz 将让应用服务器容器管理事务。
         *
         * 最后一个问题是设置一个数据源，JDBCJobStore可以从该数据源连接到数据库。使用几种不同的方法之一，可以在您的 Quartz 属性中定义数据源。一种方法是让Quartz创建并管理数据源本身——通过提供数据库的所有连接信息。
         * 另一种方法是让Quartz使用一个由Quartz在其内部运行的应用服务器管理的数据源——通过提供JDBCJobStore JNDI名称的数据源。有关属性的详细信息，请参阅“docs/config”文件夹中的示例配置文件。
         *
         * 使用JDBCJobStore(假设你使用StdSchedulerFactory)首先需要设置JobStore类属性的配置为 org.quartz.impl.jdbcjobstore.JobStoreTX 或org.quartz.impl.jdbcjobstore.JobStoreCMT - 取决于你根据上面几段的解释所做的选择。
         *
         * 配置使用 JobStoreTx
         * org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
         * 1
         * 接下来选择 driver 委托
         *
         * 配置 DriverDelegate
         * org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
         * 1
         * 配置表名称前缀
         * org.quartz.jobStore.tablePrefix = QRTZ_
         * 1
         * 配置数据源
         * org.quartz.jobStore.dataSource = myDS
         * 1
         * 注意
         * 如果您的调度器很忙(即几乎总是执行与线程池大小相同的工作数，那么您应该将数据源中的连接数设置为线程池+2的大小)。
         *
         * “org.quartz.jobStore。useProperties“配置参数可以设置为“true”(默认为false)，以便指示JDBCJobStore, JobDataMaps中的所有值都是字符串，因此可以作为 key-value 对存储，而不是在BLOB列中以序列化形式存储更复杂的对象。
         *
         * 从长远来看，这样做更安全，因为您避免了类版本化问题，而将非string类序列化为BLOB。
         *
         * TerracottaJobStore
         * TerracottaJobStore 提供了一种无需数据库就可以进行缩放和健壮性的方法。这意味着您的数据库可以避免来自Quartz的负载，并且可以将所有的资源保存到您的应用程序的其他部分。
         *
         * TerracottaJobStore 可以是集群的或非集群的，在这两种情况下，都为您的工作数据提供了一个存储介质，在应用程序重新启动之间是持久的，因为数据存储在 Terracotta 服务器中。它的性能比通过 JDBCJobStore(大约一个数量级更好) 使用数据库要好得多，但是比RAMJobStore慢得多。
         *
         * 要使用TerracottaJobStore(假设您使用的是 StdSchedulerFactory)，只需指定类名称org.quartz.jobStore.class = org.terracotta.quartz.TerracottaJobStore。作为您用来配置quartz的JobStore类属性，并添加一个额外的配置行来指定Terracotta服务器的位置:
         *
         * org.quartz.jobStore.class = org.terracotta.quartz.TerracottaJobStore
         * org.quartz.jobStore.tcConfigUrl = localhost:9510
         * ---------------------
         * 作者：叶止水
         * 来源：CSDN
         * 原文：https://blog.csdn.net/ryo1060732496/article/details/79854286
         * 版权声明：本文为博主原创文章，转载请附上博文链接！
         */
    }
}

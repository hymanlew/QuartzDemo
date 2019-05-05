
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
    }
}

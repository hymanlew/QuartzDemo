
import org.quartz.*;

public class MyJob implements Job{

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobKey key = context.getJobDetail().getKey();
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        String name = dataMap.getString("name");
        int value = dataMap.getIntValue("value");
        System.out.println("Instance " + key +"："+name+ " = " +value);
    }
}

package quartzDemo.demo;

/**
 * Quartz Calendar：
 * 不是java.util。当触发器被定义并存储在调度程序中时，日历对象可以与触发器关联。它从触发器的发射时间表中获得且不包括时间块。
 * 例如创建一个触发器，在每个工作日的上午9点30分触发一个作业，然后添加一个不包括所有业务假期的日历。
 * 日历可以是任何实现日历接口的可序列化对象。
 *
 * 注意，下面方法的参数都是 long 类型，即它们是毫秒格式的时间戳。日历必须通过 addCalendar(..) 方法实例化并通过调度程序注册。
 * 如果您使用假期日历，在实例化之后，应该使用 addExcludedDate(日期日期) 方法来填充它，以在调度中排除指定的日期。同一个日历实例
 * 可以使用多个触发器，例如:
 *
 * HolidayCalendar cal = new HolidayCalendar();
 * cal.addExcludedDate( someDate );
 * cal.addExcludedDate( someOtherDate );
 *
 * sched.addCalendar("myHolidays", cal, false);
 *
 * Trigger t = newTrigger()
 *     .withIdentity("myTrigger")
 *     .forJob("myJob")
 *     .withSchedule(dailyAtHourAndMinute(9, 30)) // execute job daily at 9:30
 *     .modifiedByCalendar("myHolidays") // but not on holidays
 *     .build();
 *
 * // .. schedule job with trigger
 *
 * Trigger t2 = newTrigger()
 *     .withIdentity("myTrigger2")
 *     .forJob("myJob2")
 *     .withSchedule(dailyAtHourAndMinute(11, 30)) // execute job daily at 11:30
 *     .modifiedByCalendar("myHolidays") // but not on holidays
 *     .build();
 *
 * // .. schedule job with trigger2
 */
public interface CalendarDemo {

	boolean isTimeIncluded(long timeStamp);

	long getNextIncludedTime(long timeStamp);

}
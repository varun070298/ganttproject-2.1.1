/*
 * Created on 24.10.2004
 */
package net.sourceforge.ganttproject.test.task.dependency;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sourceforge.ganttproject.GanttCalendar;
import net.sourceforge.ganttproject.calendar.AlwaysWorkingTimeCalendarImpl;
import net.sourceforge.ganttproject.calendar.CalendarActivityImpl;
import net.sourceforge.ganttproject.calendar.GPCalendar;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.task.dependency.TaskDependency;
import net.sourceforge.ganttproject.task.dependency.constraint.FinishStartConstraintImpl;
import net.sourceforge.ganttproject.test.task.TaskTestCase;

/**
 * @author bard
 */
public class TestDependencyActivityBinding extends TaskTestCase {

    private Date myJanuaryFirst;

    private Date myJanuarySecond;

    protected void setUp() throws Exception {
        Calendar c = (Calendar) GregorianCalendar.getInstance().clone();
        c.clear();
        c.set(Calendar.YEAR, 2000);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        myJanuaryFirst = c.getTime();
        //
        c.add(Calendar.DAY_OF_MONTH, 1);
        myJanuarySecond = c.getTime();
        super.setUp();
    }

    private GPCalendar myJanuaryFirstIsHolidayCalendar = new AlwaysWorkingTimeCalendarImpl() {
        public List getActivities(Date startDate, Date endDate) {
            List result = new ArrayList();
            if (endDate.before(myJanuaryFirst)
                    || startDate.after(myJanuarySecond)) {
                result.add(new CalendarActivityImpl(startDate, endDate, true));
                return result;
            }
            if (startDate.after(myJanuaryFirst)
                    && endDate.before(myJanuarySecond)) {
                result.add(new CalendarActivityImpl(startDate, endDate, false));
                return result;
            }
            if (startDate.before(myJanuaryFirst)
                    && endDate.after(myJanuarySecond)) {
                result.add(new CalendarActivityImpl(myJanuaryFirst,
                        myJanuarySecond, false));
            }
            if (startDate.before(myJanuaryFirst)) {
                result.add(new CalendarActivityImpl(startDate, myJanuaryFirst,
                        true));
            } else {
                result.add(new CalendarActivityImpl(startDate, myJanuarySecond,
                        false));
            }
            if (endDate.after(myJanuarySecond)) {
                result.add(new CalendarActivityImpl(myJanuarySecond, endDate,
                        true));
            } else {
                result.add(new CalendarActivityImpl(myJanuaryFirst, endDate,
                        false));
            }
            if (result.size() == 0) {
                throw new RuntimeException("Noactivities for start date="
                        + startDate + " and end date=" + endDate);
            }
            return result;
        }

    };

    public GPCalendar getCalendar() {
        return myJanuaryFirstIsHolidayCalendar;
    }

    public void testFinishStartBindings() throws Exception {
        Task dependant = getTaskManager().createTask();
        Task dependee = getTaskManager().createTask();
        dependant.setStart(new GanttCalendar(1999, Calendar.DECEMBER, 30));
        dependant.setEnd(new GanttCalendar(2000, Calendar.JANUARY, 3));
        dependee.setStart(new GanttCalendar(1999, Calendar.NOVEMBER, 15));
        dependee.setEnd(new GanttCalendar(1999, Calendar.NOVEMBER, 16));
        //
        TaskDependency dep = getTaskManager().getDependencyCollection()
                .createDependency(dependant, dependee,
                        new FinishStartConstraintImpl());
        TaskDependency.ActivityBinding binding = dep.getActivityBinding();
        assertEquals(binding.getDependantActivity(),
                dependant.getActivities()[0]);
        assertEquals(binding.getDependeeActivity(), dependee.getActivities()[0]);
        //
        dependant.setStart(new GanttCalendar(2000, Calendar.JANUARY, 4));
        dependant.setEnd(new GanttCalendar(2000, Calendar.JANUARY, 5));
        dependee.setStart(new GanttCalendar(1999, Calendar.DECEMBER, 30));
        dependee.setEnd(new GanttCalendar(2000, Calendar.JANUARY, 3));
        binding = dep.getActivityBinding();
        assertEquals(binding.getDependantActivity(),
                dependant.getActivities()[0]);
        assertEquals(binding.getDependeeActivity(), dependee.getActivities()[1]);
    }
}

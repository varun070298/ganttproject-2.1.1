/*
 * Created on 18.10.2004
 */
package net.sourceforge.ganttproject.test.task.calendar;

import net.sourceforge.ganttproject.GanttCalendar;
import net.sourceforge.ganttproject.calendar.GPCalendar;
import net.sourceforge.ganttproject.calendar.WeekendCalendarImpl;
import net.sourceforge.ganttproject.task.Task;
import net.sourceforge.ganttproject.test.task.TaskTestCase;
import net.sourceforge.ganttproject.time.gregorian.GregorianTimeUnitStack;

/**
 * @author bard
 */
public class TestWeekendCalendar extends TaskTestCase {
    public void testTaskOverlappingWeekendIsTwoDaysShorter() {
        Task t = getTaskManager().createTask();
        t.setStart(newFriday());// Friday
        t.setEnd(newTuesday()); // Tuesday
        assertEquals("Unexpected length of task=" + t
                + " which overlaps weekend", 2f, t.getDuration().getLength(
                GregorianTimeUnitStack.DAY), 0.1);
    }


    public GPCalendar getCalendar() {
        return myWeekendCalendar;
    }

    private GPCalendar myWeekendCalendar = new WeekendCalendarImpl();
}

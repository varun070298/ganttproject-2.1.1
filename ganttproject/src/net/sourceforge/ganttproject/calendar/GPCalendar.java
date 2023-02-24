/*
 * Created on 18.10.2004
 */
package net.sourceforge.ganttproject.calendar;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.task.TaskLength;
import net.sourceforge.ganttproject.time.TimeUnit;

/**
 * @author bard
 */
public interface GPCalendar {
    List getActivities(Date startDate, Date endDate);

    List getActivities(Date startDate, TimeUnit timeUnit, long l);

    void setWeekDayType(int day, DayType type);

    DayType getWeekDayType(int day);

    void setPublicHoliDayType(int month, int date);

    public void setPublicHoliDayType(Date curDayStart);

    public boolean isPublicHoliDay(Date curDayStart);

    public boolean isNonWorkingDay(Date curDayStart);

    public DayType getDayTypeDate(Date curDayStart);

    public void setPublicHolidays(URL calendar, GanttProject gp);

    public Collection getPublicHolidays();

    final class DayType {
        public static final DayType WORKING = new DayType();

        public static final DayType WEEKEND = new DayType();

        public static final DayType HOLIDAY = new DayType();
    }

    Date findClosestWorkingTime(Date time);

    /**
     * Adds <code>shift</code> period to <code>input</code> date taking into
     * account this calendar working/non-working time If input date corresponds
     * to friday midnight and this calendar if configured to have a weekend on
     * saturday and sunday then adding a shift of "1 day" will result to the
     * midnight of the next monday
     */
    Date shiftDate(Date input, TaskLength shift);

    GPCalendar PLAIN = new AlwaysWorkingTimeCalendarImpl();
    String EXTENSION_POINT_ID = "net.sourceforge.ganttproject.calendar";

}

/*
 * Created on 08.11.2004
 */
package net.sourceforge.ganttproject.time.gregorian;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.ganttproject.calendar.CalendarFactory;
import net.sourceforge.ganttproject.time.DateFrameable;

public class WeekFramerImpl implements DateFrameable {
    private final FramerImpl myDayFramer = new FramerImpl(Calendar.DATE);

    public Date adjustRight(Date baseDate) {
        Calendar c = CalendarFactory.newCalendar();
        do {
            baseDate = myDayFramer.adjustRight(baseDate);
            c.setTime(baseDate);
        } while (c.get(Calendar.DAY_OF_WEEK) != c.getFirstDayOfWeek());
        return c.getTime();
    }

    public Date adjustLeft(Date baseDate) {
        Calendar c = CalendarFactory.newCalendar();
        c.setTime(myDayFramer.adjustLeft(baseDate));
        while (c.get(Calendar.DAY_OF_WEEK) != c.getFirstDayOfWeek()) {
            c
                    .setTime(myDayFramer.adjustLeft(myDayFramer.jumpLeft(c
                            .getTime())));
        }
        return c.getTime();
    }

    public Date jumpLeft(Date baseDate) {
        Calendar c = CalendarFactory.newCalendar();
        c.setTime(myDayFramer.adjustLeft(baseDate));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        do {
            baseDate = myDayFramer.jumpLeft(baseDate);
            c.setTime(baseDate);
        } while (c.get(Calendar.DAY_OF_WEEK) != dayOfWeek);
        return c.getTime();
    }
}
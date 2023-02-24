/*
 * Created on 10.05.2005
 */
package net.sourceforge.ganttproject.calendar;

import java.util.Date;
import java.util.List;

import net.sourceforge.ganttproject.task.TaskLength;
import net.sourceforge.ganttproject.time.TimeUnit;

/**
 * @author bard
 */
abstract class GPCalendarBase {
    public Date shiftDate(Date input, TaskLength shift) {
        List activities = getActivities(input, shift);
        if (activities.isEmpty()) {
            throw new RuntimeException(
                    "FIXME: Failed to compute calendar activities in time period="
                            + shift + " starting from " + input);
        }
        Date result;
        if (shift.getValue() >= 0) {
            GPCalendarActivity lastActivity = (GPCalendarActivity) activities
                    .get(activities.size() - 1);
            result = lastActivity.getEnd();
        } else {
            GPCalendarActivity firstActivity = (GPCalendarActivity) activities
                    .get(0);
            result = firstActivity.getStart();
        }
        return result;

    }

    public List getActivities(Date startDate, TimeUnit timeUnit, long unitCount) {
        return unitCount > 0 ? getActivitiesForward(startDate, timeUnit,
                unitCount) : getActivitiesBackward(startDate, timeUnit,
                -unitCount);
    }

    protected abstract List getActivitiesBackward(Date startDate,
            TimeUnit timeUnit, long l);

    protected abstract List getActivitiesForward(Date startDate,
            TimeUnit timeUnit, long l);

    public List/* <GPCalendarActivity> */getActivities(Date startingFrom,
            TaskLength period) {
        return getActivities(startingFrom, period.getTimeUnit(), period
                .getLength());
    }

}

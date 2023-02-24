/*
 * Created on 18.10.2004
 */
package net.sourceforge.ganttproject.task;

import java.util.Date;

/**
 * @author bard
 */
class TaskActivityImpl implements TaskActivity {

    private final Date myEndDate;

    private final Date myStartDate;

    private final TaskLength myDuration;

    private float myIntensity;

    private final TaskImpl myTask;

    TaskActivityImpl(TaskImpl task, Date startDate, Date endDate) {
        this(task, startDate, endDate, 1.0f);
    }

    TaskActivityImpl(TaskImpl task, Date startDate, Date endDate,
            float intensity) {
        myStartDate = startDate;
        myEndDate = endDate;
        myDuration = task.getManager().createLength(
                task.getDuration().getTimeUnit(), startDate, endDate);
        myIntensity = intensity;
        myTask = task;
    }

    public Date getStart() {
        return myStartDate;
    }

    public Date getEnd() {
        return myEndDate;
    }

    public TaskLength getDuration() {
        return myDuration;
    }

    public float getIntensity() {
        return myIntensity;
    }

    public String toString() {
        return myTask.toString() + "[" + getStart() + ", " + getEnd() + "]";
    }

    public Task getTask() {
        return myTask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.ganttproject.task.TaskActivity#isFirst()
     */
    public boolean isFirst() {
        return this == getTask().getActivities()[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.ganttproject.task.TaskActivity#isLast()
     */
    public boolean isLast() {
        TaskActivity[] all = getTask().getActivities();
        return this == all[all.length - 1];
    }
}

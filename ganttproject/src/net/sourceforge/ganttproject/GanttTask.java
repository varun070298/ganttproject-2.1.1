/***************************************************************************
 GanttTask.java  -  description
 -------------------
 begin                : dec 2002
 copyright            : (C) 2002 by Thomas Alexandre
 email                : alexthomas(at)ganttproject.org
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package net.sourceforge.ganttproject;

import java.io.Serializable;
import java.util.Vector;

import net.sourceforge.ganttproject.task.TaskImpl;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskMutator;
import net.sourceforge.ganttproject.task.dependency.TaskDependency;

/**
 * Class that generate a task
 */

public class GanttTask extends TaskImpl

implements Serializable {

    public static int LOW = 0;

    public static int NORMAL = 1;

    public static int HIGHT = 2;

    // ///////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     * 
     * @param taskID
     */

    public GanttTask(String name, GanttCalendar start, long length,
            TaskManager taskManager, int taskID) {
        super(taskManager, taskID);
        TaskMutator mutator = createMutator();
        mutator.setName(name);
        mutator.setStart(start);
        mutator.setDuration(taskManager.createLength(length));
        mutator.commit();
        enableEvents(true);
    }

    public GanttTask(GanttTask copy) {
        super(copy, false);
        // for (int i = 0; i < getPredecessorsOld().size(); i++) {
        // GanttTaskRelationship tempRel = (GanttTaskRelationship) ( (
        // GanttTaskRelationship) getPredecessorsOld().get(i)).clone();
        // addPredecessor(tempRel);
        // }

        // for (int i = 0; i < successors.size(); i++) {
        // GanttTaskRelationship tempRel = (GanttTaskRelationship) ( (
        // GanttTaskRelationship) successors.get(i)).clone();
        // addSuccessor(tempRel);
        // }
        enableEvents(true);

    }

    /**
     * @return a clone of the Task
     */
    public GanttTask Clone() {
        return new GanttTask(this);
    }

    /** @deprecated Use TimeUnit class istead and method getDuration() */ 
    public int getLength() {

        return (int) getDuration().getLength();

    }

    /**
     * @deprecated Use setDuration() 
     */
    public void setLength(int l) {
        if (l <= 0) {
            throw new IllegalArgumentException(
                    "Length of task must be >=0. You've passed length=" + l
                            + " to task=" + this);
        }
        TaskMutator mutator = createMutator();
        mutator.setDuration(getManager().createLength(
                getDuration().getTimeUnit(), l));
        mutator.commit();
    }

    /**
     * set the task ID. the uniquness of ID should be check before using this
     * method
     * 
     * @param taskID
     */
    public void setTaskID(int taskID) {
        setTaskIDHack(taskID);
    }
    
}

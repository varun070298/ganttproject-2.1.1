package net.sourceforge.ganttproject.test.task;

import java.awt.Color;
import java.net.URL;

import junit.framework.TestCase;
import net.sourceforge.ganttproject.GanttCalendar;
import net.sourceforge.ganttproject.calendar.AlwaysWorkingTimeCalendarImpl;
import net.sourceforge.ganttproject.calendar.GPCalendar;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.resource.ResourceManager;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.roles.RoleManagerImpl;
import net.sourceforge.ganttproject.task.TaskManager;
import net.sourceforge.ganttproject.task.TaskManagerConfig;
import net.sourceforge.ganttproject.time.TimeUnitStack;
import net.sourceforge.ganttproject.time.gregorian.GregorianTimeUnitStack;

/**
 * Created by IntelliJ IDEA. User: bard
 */
public abstract class TaskTestCase extends TestCase implements
        TaskManagerConfig {
    private TaskManager myTaskManager;

    private GPCalendar myFakeCalendar = new AlwaysWorkingTimeCalendarImpl();

    private TimeUnitStack myTimeUnitStack;

    private ResourceManager myResourceManager;

    private RoleManager myRoleManager;

    protected TaskManager getTaskManager() {
        return myTaskManager;
    }

    protected void setUp() throws Exception {
        super.setUp();
        myTimeUnitStack = new GregorianTimeUnitStack();
        myTaskManager = newTaskManager();
        myRoleManager = new RoleManagerImpl();
        myResourceManager = new HumanResourceManager(myRoleManager
                .getDefaultRole());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        myTaskManager = null;
    }
    protected GanttCalendar newFriday() {
        return new GanttCalendar(2004, 9, 15);
    }

    protected GanttCalendar newSaturday() {
        return new GanttCalendar(2004, 9, 16);
    }

    protected GanttCalendar newSunday() {
        return new GanttCalendar(2004, 9, 17);
    }

    protected GanttCalendar newTuesday() {
        return new GanttCalendar(2004, 9, 19);
    }

    protected GanttCalendar newMonday() {
        return new GanttCalendar(2004, 9, 18);
    }

    protected GanttCalendar newWendesday() {
        return new GanttCalendar(2004, 9, 20);
    }

    public Color getDefaultColor() {
        return null;
    }

    public GPCalendar getCalendar() {
        return myFakeCalendar;
    }

    public TimeUnitStack getTimeUnitStack() {
        return myTimeUnitStack;
    }

    public ResourceManager getResourceManager() {
        return myResourceManager;
    }

    protected TaskManager newTaskManager() {
        return TaskManager.Access.newInstance(null, this);
    }

    public URL getProjectDocumentURL() {
    	return null;
    }
}
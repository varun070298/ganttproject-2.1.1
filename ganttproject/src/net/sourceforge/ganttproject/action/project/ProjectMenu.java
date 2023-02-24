/*
 * Created on 26.09.2005
 */
package net.sourceforge.ganttproject.action.project;

import javax.swing.Action;
import javax.swing.JMenuItem;

import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.action.GPAction;

public class ProjectMenu {
    private GanttProject myMainFrame;
    private NewProjectAction myNewProjectAction;
    private OpenProjectAction myOpenProjectAction;
    private SaveProjectAction mySaveProjectAction;
    private SaveProjectAsAction mySaveProjectAsAction;
    private OpenURLAction myOpenURLAction;
    private ExitAction myExitAction;
    private GPAction mySaveURLAction;
    private GPAction myPrintAction;
    
    public ProjectMenu(GanttProject mainFrame) {
        myMainFrame = mainFrame;
        myNewProjectAction = new NewProjectAction(mainFrame);
        myOpenProjectAction = new OpenProjectAction(mainFrame);
        mySaveProjectAction =new SaveProjectAction(mainFrame);
        mySaveProjectAsAction =new SaveProjectAsAction(mainFrame);
        myOpenURLAction = new OpenURLAction(mainFrame);
        mySaveURLAction = new SaveURLAction(mainFrame);
        myPrintAction = new PrintAction(mainFrame);
        myExitAction = new ExitAction(mainFrame);
    }
    public GPAction getNewProjectAction() {
        return myNewProjectAction;
    }
    public GPAction getOpenProjectAction() {
        return myOpenProjectAction;
    }
    public GPAction getSaveProjectAction() {
        return mySaveProjectAction;
    }
    public GPAction getSaveProjectAsAction() {
        return mySaveProjectAsAction;
    }
    public GPAction getOpenURLAction() {
        return myOpenURLAction;
    }
    public GPAction getExitAction() {
        return myExitAction;
    }
    public GPAction getSaveURLAction() {
        return mySaveURLAction;
    }
    public GPAction getPrintAction() {
        return myPrintAction;
    }
}

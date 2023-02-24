/*
 * Created on 30.04.2005
 */
package net.sourceforge.ganttproject.importer;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.action.GPAction;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.language.GanttLanguage;

/**
 * @author bard
 */
public class ImportFileAction extends GPAction {

    private UIFacade myUIFacade;

    private GanttProject myProject;

    public ImportFileAction(UIFacade uiFacade, GanttProject project) {
        super(null, "16");
        /*putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I,
                MENU_MASK));*/
        myUIFacade = uiFacade;
        myProject = project;
    }

    public void actionPerformed(ActionEvent e) {
        ImportFileWizardImpl wizard = new ImportFileWizardImpl(myUIFacade,
                myProject);
        wizard.show();
    }

    protected String getIconFilePrefix() {
        return "import_";
    }

    public void isIconVisible(boolean isNull) {
    }

    protected String getLocalizedName() {
        return GanttProject.correctLabel(GanttLanguage.getInstance().getText(
                "import"));
    }
}

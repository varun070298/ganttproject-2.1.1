/*
 * Created on 30.04.2005
 */
package net.sourceforge.ganttproject.importer;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sourceforge.ganttproject.gui.options.GPOptionChoicePanel;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.gui.projectwizard.WizardPage;
import net.sourceforge.ganttproject.importer.ImportFileWizardImpl.State;
import net.sourceforge.ganttproject.language.GanttLanguage;

/**
 * @author bard
 */
class ImporterChooserPage implements WizardPage {
    private Importer[] myImporters;

    private State myState;

    ImporterChooserPage(Importer[] importers, State state) {
        myImporters = importers;
        myState = state;
    }

    public String getTitle() {
        return GanttLanguage.getInstance().getText("importerChooserPageTitle");
    }

    public Component getComponent() {
        Action[] choiceChangeActions = new Action[myImporters.length];
        GPOptionGroup[] choiceOptions = new GPOptionGroup[myImporters.length];
        for (int i = 0; i < myImporters.length; i++) {
            final Importer nextImporter = myImporters[i];
            Action nextAction = new AbstractAction(nextImporter
                    .getFileTypeDescription()) {
                public void actionPerformed(ActionEvent e) {
                    ImporterChooserPage.this.myState.myImporter = nextImporter;
                }
            };
            choiceChangeActions[i] = nextAction;
            choiceOptions[i] = null;
            if (i == 0) {
                myState.myImporter = nextImporter;
            }
        }
        GPOptionChoicePanel panel = new GPOptionChoicePanel();
        return panel.getComponent(choiceChangeActions, choiceOptions, 0);
    }

    public void setActive(boolean b) {
    }

}

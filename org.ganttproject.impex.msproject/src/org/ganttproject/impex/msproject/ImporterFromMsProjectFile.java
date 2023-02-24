package org.ganttproject.impex.msproject;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.Action;

import com.tapsterrock.mpx.MPXException;

import net.sourceforge.ganttproject.GanttProject;
import net.sourceforge.ganttproject.action.CancelAction;
import net.sourceforge.ganttproject.action.OkAction;
import net.sourceforge.ganttproject.document.Document;
import net.sourceforge.ganttproject.document.FileDocument;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.options.model.GPOption;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.importer.Importer;
import net.sourceforge.ganttproject.importer.ImporterBase;
import net.sourceforge.ganttproject.language.GanttLanguage;

public class ImporterFromMsProjectFile extends ImporterBase implements Importer {
    private LocaleOption myLanguageOption = new LocaleOption();
    
    private GPOptionGroup myMPXOptions = new GPOptionGroup("importer.msproject.mpx", new GPOption[] {myLanguageOption});
    
    public String getFileNamePattern() {
        return "mpp|mpx|xml";
    }

    public GPOptionGroup[] getSecondaryOptions() {
    	return new GPOptionGroup[] {myMPXOptions};
    }
    
    public String getFileTypeDescription() {
        return GanttLanguage.getInstance().getText("impex.msproject.description");
    }

    public void run(GanttProject project, UIFacade uiFacade, File selectedFile, boolean merge) {
        Document document = getDocument(selectedFile);
        openDocument(project, uiFacade, document);
    }

    protected Document getDocument(File selectedFile) {
        return new FileDocument(selectedFile);
    }

    GanttMPXJOpen open;

    protected void openDocument(final GanttProject project, final UIFacade uiFacade, Document document) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(
                    getClass().getClassLoader());
            if (document.getPath().toLowerCase().endsWith(".mpp")) {
                open = new GanttMPPOpen(project.getTree(), project);
            }
            else if (document.getPath().toLowerCase().endsWith(".mpx")) {
                open = null;
                Locale importlocale = myLanguageOption.getSelectedLocale();
                open = new GanttMPXOpen(project.getTree(), project,
                        importlocale);
            } else if (document.getPath().toLowerCase().endsWith(".xml"))
                open = new GanttMSPDIOpen(project.getTree(), project);
            else
                open = null;

            open.load(document.getInputStream());

        } catch (IOException e) {
            uiFacade.showErrorDialog(e);
        } catch (MPXException e) {
        	uiFacade.showErrorDialog(e);
		}
        finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);        	
        }
    }

}

package net.sourceforge.ganttproject.chart;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.Date;
import java.util.Map;

import javax.swing.Icon;

import net.sourceforge.ganttproject.GanttExportSettings;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.task.TaskManager;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public interface Chart extends IAdaptable {
	public RenderedImage getRenderedImage(GanttExportSettings settings);
	
	/** @deprecated Use getRenderedImage */
    public BufferedImage getChart(GanttExportSettings settings);

    public Date getStartDate();

    public Date getEndDate();

    public String getName();

    public void setTaskManager(TaskManager taskManager);

    public void reset();

    public Icon getIcon();
        
    public GPOptionGroup[] getOptionGroups();
    
    public Chart createCopy();

    public ChartSelection getSelection();
    
    public IStatus canPaste(ChartSelection selection);
    
    public void paste(ChartSelection selection);
    
    public void addSelectionListener(ChartSelectionListener listener);
    public void removeSelectionListener(ChartSelectionListener listener);
    
}

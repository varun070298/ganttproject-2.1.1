package net.sourceforge.ganttproject;

import java.util.Date;

/** Class to store 3 boolean values */
public class GanttExportSettings {
    private Date startDate = null;

    private Date endDate = null;

    public boolean name, percent, depend, border3d, ok;
    
    private boolean onlySelectedItems;

    public GanttExportSettings() {
        name = percent = depend = ok = true;
        onlySelectedItems = false;
    }

    public GanttExportSettings(boolean bName, boolean bPercent,
            boolean bDepend, boolean b3dBorders) {
        name = bName;
        percent = bPercent;
        depend = bDepend;
        border3d = b3dBorders;
        ok = true;
        onlySelectedItems = false;
    }
    
    public void setOnlySelectedItem(boolean selected){
        onlySelectedItems = selected;
    }
    
    public boolean isOnlySelectedItem(){
        return onlySelectedItems;
    }

    public void setStartDate(Date date) {
        startDate = date;
    }

    public void setEndDate(Date date) {
        endDate = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}

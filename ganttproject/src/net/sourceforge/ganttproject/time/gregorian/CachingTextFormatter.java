/*
 * Created on 06.03.2005
 */
package net.sourceforge.ganttproject.time.gregorian;

import java.util.Date;
import java.util.HashMap;

import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.language.GanttLanguage.Event;
import net.sourceforge.ganttproject.language.GanttLanguage.Listener;
import net.sourceforge.ganttproject.time.DateFrameable;
import net.sourceforge.ganttproject.time.TimeUnit;
import net.sourceforge.ganttproject.time.TimeUnitText;

/**
 * @author bard
 */
public abstract class CachingTextFormatter implements Listener {
    private final HashMap myTextCache = new HashMap();

    protected CachingTextFormatter() {
        GanttLanguage.getInstance().addListener(this);
    }

    public TimeUnitText format(TimeUnit timeUnit, Date baseDate) {
        TimeUnitText result = null;
        Date adjustedLeft = ((DateFrameable) timeUnit).adjustLeft(baseDate);
        result = getCachedText(adjustedLeft);
        if (result == null) {
            result = createTimeUnitText(adjustedLeft);
            myTextCache.put(adjustedLeft, result);
        }

        return result;
    }

    protected TimeUnitText getCachedText(Date startDate) {
        return (TimeUnitText) myTextCache.get(startDate);
    }

    public void languageChanged(Event event) {
        myTextCache.clear();
    }

    protected abstract TimeUnitText createTimeUnitText(Date adjustedLeft);

}

package net.sourceforge.ganttproject.task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.ganttproject.GPLogger;
import net.sourceforge.ganttproject.GanttCalendar;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.util.DateUtils;

/**
 * TODO Remove tha map Name->customColum to keep only the map Id -> CustomColumn
 * This class stores the CustomColumns.
 *
 * @author bbaranne (Benoit Baranne) Mar 2, 2005
 */
public class CustomColumnsStorage {
    public static List availableTypes = null;

    public static GanttLanguage language = GanttLanguage.getInstance();

    private static int nextId;

    private final static String ID_PREFIX = "tpc";
    private final List myListeners = new ArrayList();

    static {
        initTypes();
    }

    /**
     * Column name (String) -> CustomColumn
     */
    // private Map customColumns = null;
    private final Map mapIdCustomColum = new HashMap();

    /**
     * Creates an instance of CustomColumnsStorage.
     */
    public CustomColumnsStorage() {
    }

    public void reset() {
        initTypes();
        mapIdCustomColum.clear();
        nextId = 0;
    }

    // public CustomColumnsStorage(Map customCols)
    // {
    // customColumns = customCols;
    // }

    /**
     * Initialize the available types (text, integer, ...)
     */
    private static void initTypes() {
        availableTypes = new Vector(6);
        availableTypes.add(language.getText("text"));
        availableTypes.add(language.getText("integer"));
        availableTypes.add(language.getText("double"));
        availableTypes.add(language.getText("date"));
        availableTypes.add(language.getText("boolean"));
    }

    /**
     * Changes the language of the available types.
     *
     * @param lang
     *            New language.
     */
    public static void changeLanguage(GanttLanguage lang) {
        language = lang;
        initTypes();
    }

    /**
     * Add a custom column.
     *
     * @param col
     *            CustomColumn to be added.
     * @throws CustomColumnsException
     *             A CustomColumnsException of type
     *             CustomColumnsException.ALREADY_EXIST could be thrown if the
     *             CustomColumn that should be added has a name that already
     *             exists.
     */
    public void addCustomColumn(CustomColumn col) {
        if (getCustomColumnsNames().contains(col.getName())) {
        	return;
        }
        String id = col.getId();
        while (id == null) {
            id = ID_PREFIX + nextId++;
            if (existsForID(id))
                id = null;
        }
        mapIdCustomColum.put(id, col);
        col.setId(id);
        CustomColumEvent event = new CustomColumEvent(CustomColumEvent.EVENT_ADD, col);
        fireCustomColumnsChange(event);
    }

    void removeCustomColumn(String name) {
    	CustomColumn column = getCustomColumn(name);
    	if (column!=null) {
    		removeCustomColumn(column);
    	}
    }
    /**
     * Removes the CustomColumn whose name is given in parameter. If the column
     * name does not exist, nothing is done.
     *
     * @param name
     *            Name of the column to remove.
     */
    public void removeCustomColumn(CustomColumn column) {
        CustomColumEvent event = new CustomColumEvent(CustomColumEvent.EVENT_REMOVE, column);
        fireCustomColumnsChange(event);
        mapIdCustomColum.remove(column.getId());
    }

    /**
     * Returns the number of custom columns.
     *
     * @return The number of custom columns.
     */
    public int getCustomColumnCount() {
        // return customColumns.size();
        return mapIdCustomColum.size();
    }

    /**
     * Returns a collection containing the names of all the stored custom
     * columns.
     *
     * @return A collection containing the names of all the stored custom
     *         columns.
     */
    public List getCustomColumnsNames() {
        // return customColumns.keySet();
        // -----
        List c = new ArrayList();
        Iterator it = mapIdCustomColum.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            c.add(((CustomColumn) mapIdCustomColum.get(id)).getName());
        }
        return c;
    }

    /**
     * Returns a collection with all the stored custom columns.
     *
     * @return A collection with all the stored custom columns.
     */
    public Collection getCustomColums() {
        // return customColumns.values();
        return mapIdCustomColum.values();
    }

    /**
     * @param name
     * @return
     * @throws CustomColumnsException
     */
    public CustomColumn getCustomColumn(String name) {
        // if (!exists(name))
        // throw new CustomColumnsException(CustomColumnsException.DO_NOT_EXIST,
        // name);
        // return (CustomColumn) customColumns.get(name);
        String id = getIdFromName(name);
        if (id == null) {
        	return null;
        }
        return (CustomColumn) mapIdCustomColum.get(id);
    }

    public CustomColumn getCustomColumnByID(String id) {
        return (CustomColumn) mapIdCustomColum.get(id);
    }

    /**
     * Returns true if a custom column has the same name that the one given in
     * parameter, false otherwise.
     *
     * @param colName
     *            The name of the column to check the existence.
     * @return True if a custom column has the same name that the one given in
     *         parameter, false otherwise.
     */
    public boolean exists(String colName) {
        return getIdFromName(colName) != null;
    }

    public boolean existsForID(String id) {
        return this.mapIdCustomColum.keySet().contains(id);
    }

    public void renameCustomColumn(String name, String newName) {
        // if (customColumns.containsKey(name))
        // {
        // CustomColumn cc = (CustomColumn) customColumns.get(name);
        // cc.setName(newName);
        // customColumns.put(newName, cc);
        // customColumns.remove(name);
        // ((CustomColumn) mapIdCustomColum.get(cc.getId())).setName(newName);
        // }
        String id = getIdFromName(name);
        if (id != null) {
            CustomColumn cc = (CustomColumn) mapIdCustomColum.get(id);
            cc.setName(newName);
            CustomColumEvent event = new CustomColumEvent(name, cc);
            fireCustomColumnsChange(event);
        }

    }

    public void changeDefaultValue(String colName, Object newDefaultValue)
            throws CustomColumnsException {
        // if (customColumns.containsKey(colName))
        // {
        // CustomColumn cc = (CustomColumn) customColumns.get(colName);
        // cc.setDefaultValue(newDefaultValue);
        // ((CustomColumn)
        // mapIdCustomColum.get(cc.getId())).setDefaultValue(newDefaultValue);
        // }
        String id = getIdFromName(colName);
        if (id != null) {
            CustomColumn cc = (CustomColumn) mapIdCustomColum.get(id);

            if (newDefaultValue.getClass().isAssignableFrom(cc.getType()))
                cc.setDefaultValue(newDefaultValue);
            else {
                try {
                    if (cc.getType().equals(Boolean.class))
                        cc.setDefaultValue(Boolean.valueOf(newDefaultValue
                                .toString()));
                    else if (cc.getType().equals(Integer.class)) {
                    	try {
                    		cc.setDefaultValue(Integer.valueOf(newDefaultValue
                    				.toString()));
                    	}
                    	catch (NumberFormatException e) {
                    		cc.setDefaultValue(null);
                    	}
                    }
                    else if (cc.getType().equals(Double.class)) {
                    	try {
                            cc.setDefaultValue(Double.valueOf(newDefaultValue
                                    .toString()));
                    	}
                    	catch (NumberFormatException e) {
                    		cc.setDefaultValue(null);
                    	}

                    }
                    else if (GregorianCalendar.class.isAssignableFrom(cc
                            .getType())) {
                    	try {
	                    	Date dateValue =
	                    		DateUtils.parseDate(newDefaultValue.toString());
	                    	cc.setDefaultValue(new GanttCalendar(dateValue));
                    	}
                    	catch (ParseException e) {
                    		cc.setDefaultValue(null);
                    	}
                    }

                } catch (Exception ee) {
                	throw new CustomColumnsException(ee);
                }

            }
        }
    }

    public String getIdFromName(String name) {
        String id = null;
        Iterator it = mapIdCustomColum.values().iterator();
        while (it.hasNext()) {
            CustomColumn cc = (CustomColumn) it.next();
            if (cc.getName().equals(name)) {
                id = cc.getId();
                break;
            }
        }
        return id;
    }

    public String getNameFromId(String id) {
        return ((CustomColumn) mapIdCustomColum.get(id)).getName();
    }

    /**
     * Add all stored custom column to the given task. All custom column will
     * have the default value.
     *
     * @param task
     *            The task to process.
     */
    public void processNewTask(Task task) {
        Iterator it = mapIdCustomColum.values().iterator();
        while (it.hasNext()) {
            CustomColumn cc = (CustomColumn) it.next();
            try {
                task.getCustomValues().setValue(cc.getName(),
                        cc.getDefaultValue());
            } catch (CustomColumnsException e) {
            	if (!GPLogger.log(e)) {
            		e.printStackTrace(System.err);
            	}
            }
        }

    }

    public String toString() {
        return mapIdCustomColum.toString();
    }

	public void importData(CustomColumnsStorage source) {
		for (Iterator columns = source.getCustomColums().iterator();
			 columns.hasNext();) {
			CustomColumn nextColumn = (CustomColumn) columns.next();
			if (!exists(nextColumn.getName())) {
				addCustomColumn(nextColumn);
			}
		}
	}

    void addCustomColumnsListener(CustomColumsListener listener) {
        myListeners.add(listener);
    }

    private void fireCustomColumnsChange(CustomColumEvent event) {
        Iterator it = myListeners.iterator();
        while (it.hasNext()) {
            CustomColumsListener listener = (CustomColumsListener) it.next();
            listener.customColumsChange(event);
        }
    }

}

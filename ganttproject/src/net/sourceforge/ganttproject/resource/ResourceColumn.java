package net.sourceforge.ganttproject.resource;

import net.sourceforge.ganttproject.CustomPropertyDefinition;
import net.sourceforge.ganttproject.CustomPropertyManager;
import net.sourceforge.ganttproject.gui.TableHeaderUIFacade;

import org.jdesktop.swing.table.TableColumnExt;

/**@author nokiljevic
 *  
 * Describes one column in the resources table. */ 
    public class ResourceColumn implements CustomPropertyDefinition, TableHeaderUIFacade.Column
    {
    	/** Swing column composant representing the column */
    	TableColumnExt column;
    	/** Datatype of the column */
    	Class type;
    	/** Default value for the column */
    	Object defaultVal;
    	/** Visible on the screen */
    	boolean visible;
    	/** Default index in the table. When the column is shown
    	 *  that index will be forced. */
    	int defaultIndex;
		private int myOrder;
    	
//    	public ResourceColumn(String name, int index, Class type) {
//    		column = new TableColumnExt(index);
//    		this.type = type;
//    		this.setTitle(name);
//    		defaultIndex = index;
//    	}
//    	
    	public ResourceColumn(TableColumnExt col, int index){
    		this(col, index, String.class);
    	}
    	
    	public ResourceColumn(TableColumnExt col, int index, Class type){
    		column = col;
    		this.type = type;
    		defaultIndex = index;
    		visible = true;
    	}
    	
//    	public ResourceColumn(TableColumnExt col, int index, Class type, Object def) {
//    		column = col;
//    		defaultIndex = index;
//    		visible = true;
//    		this.type = type;
//    		defaultVal = def;
//    	}
    	
    	public boolean nameCmp(String name) {
    		System.out.println("comparing: "+name+" - "+column.getTitle()+" ");
    		return column.getTitle().equals(name);
    	}
    	
    	public void setTitle(String title) {
    		column.setTitle(title);
    	}
    	
    	public String getTitle() {
    		return column.getTitle();
    	}
    	
    	public boolean isVisible() {
    		return visible;
    	}
    	
    	public void setVisible(boolean vis) {
    		visible = vis;
    	}

		public int getIndex() {
			return defaultIndex;
		}

//		public void setIndex(int defaultIndex)  {
//			this.defaultIndex = defaultIndex;
//		}
//		
		public TableColumnExt getColumn() {
			return column;
		}
		
		public Object getDefaultVal() {
			return defaultVal;
		}
		
		public void setDefaultVal(Object defaultVal) {
			this.defaultVal = defaultVal;
		}
		
		public Class getType() {
			return type;
		}
		
		public void setType(Class type) {
			this.type = type;
		}

		public Object getDefaultValue() {
			return defaultVal;
		}

		public String getDefaultValueAsString() {
			return HumanResourceManager.getValueAsString(defaultVal);
		}
		public String getID() {
			return String.valueOf(defaultIndex);
		}

		public String getName() {
			return getTitle();
		}

		public String getTypeAsString() {
			return CustomPropertyManager.PropertyTypeEncoder.encodeFieldType(type);
		}

		public int getOrder() {
			return myOrder;
		}

		public int getWidth() {
			return column.getWidth();
		}
		
		public void setWidth(int width) {
			column.setWidth(width);
		}

		public void setOrder(int order) {
			myOrder = order;
		}
    }
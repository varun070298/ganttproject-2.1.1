package net.sourceforge.ganttproject.gui.options.model;

import java.awt.Color;

import net.sourceforge.ganttproject.util.ColorConvertion;

public class DefaultColorOption extends GPAbstractOption implements ColorOption {
    private Color myLockedValue;
    private Color myValue;

    public DefaultColorOption(String id) {
        super(id);
    }

    public Color getValue() {
        return myValue;
    }


    public void setValue(Color value) {
        if (!isLocked()) {
            throw new IllegalStateException("Lock option before setting value");
        }
        myLockedValue = value;
    }

    public void commit() {
        super.commit();
        myValue = myLockedValue;
    }
    
    public String getPersistentValue() {
        return getValue()==null ? null : ColorConvertion.getColor(getValue());
    }

    public void loadPersistentValue(String value) {
        myLockedValue = ColorConvertion.determineColor(value);
    }

    public boolean isChanged() {
        if (isLocked()) {
            if (myValue!=null) {
                return false==myValue.equals(myLockedValue);
            }
        }
        return false;
    }    

}

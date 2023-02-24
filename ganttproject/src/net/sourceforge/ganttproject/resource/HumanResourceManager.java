/*
 * HumanResourceManager.java
 *
 * Created on 27. Mai 2003, 22:13
 */

package net.sourceforge.ganttproject.resource;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.ganttproject.CustomProperty;
import net.sourceforge.ganttproject.CustomPropertyDefinition;
import net.sourceforge.ganttproject.CustomPropertyHolder;
import net.sourceforge.ganttproject.CustomPropertyManager;
import net.sourceforge.ganttproject.GanttCalendar;
import net.sourceforge.ganttproject.calendar.GanttDaysOff;
import net.sourceforge.ganttproject.roles.Role;
import net.sourceforge.ganttproject.undo.GPUndoManager;

/**
 * @author barmeier
 */
public class HumanResourceManager implements ResourceManager, CustomPropertyManager {

    private List myViews = new ArrayList();

    private List resources = new ArrayList();
    
    private int nextFreeId = 0;

    private final Role myDefaultRole;
    
    /* customFields maintains a list of custom field names
     * and their default values */
    private final Map customFields = new HashMap();

    private final List myListeners = new ArrayList();
    
    public HumanResourceManager(Role defaultRole) {
        myDefaultRole = defaultRole;
    }

    public HumanResource newHumanResource() {
        HumanResource result = new HumanResource(this);
        result.setRole(myDefaultRole);
        return result;
    }

    public ProjectResource create(String name, int i) {
        HumanResource hr = new HumanResource(name, i, this);
        hr.setRole(myDefaultRole);
        add(hr);
        return (hr);
    }

    public void add(ProjectResource resource) {
        if (resource.getId() == -1) {
            resource.setId(nextFreeId);
        }
        if (resource.getId() >= nextFreeId) {
            nextFreeId = resource.getId() + 1;
        }
        resources.add(resource);
        fireResourceAdded(resource);
    }

    public void addCustomField(CustomPropertyDefinition definition) {
    	customFields.put(definition.getName(), definition);
    	
    	/* all the existant resources are added the new property field */
        Iterator it = resources.iterator();
        while (it.hasNext()) {
            ((HumanResource)it.next()).addCustomField(definition.getName(), definition.getDefaultValue());
        }
    }
    
    public boolean checkCustomField(String title){
    	return customFields.containsKey(title);
    }
    
    public void removeCustomField(String title) {
    	customFields.remove(title);
    	
    	/* the property field is removed from all the existant resources */
    	Iterator it = resources.iterator();
    	while (it.hasNext()) {
    		((HumanResource)it.next()).removeCustomField(title);
        }
    }
    
    public ProjectResource getById(int id) {
        // Linear search is not really efficent, but we dont have so many
        // resources !?
        ProjectResource pr = null;
        for (int i = 0; i < resources.size(); i++)
            if (((ProjectResource) resources.get(i)).getId() == id) {
                pr = (ProjectResource) resources.get(i);
                break;
            }
        return pr;
    }

    public List getResources() {
        return resources;
    }
    
    public ProjectResource[] getResourcesArray() {
        return (ProjectResource[]) resources.toArray(new ProjectResource[resources.size()]);
    }

    public void remove(ProjectResource resource) {
        fireResourcesRemoved(new ProjectResource[] { resource });
        resources.remove(resource);
    }

    public void remove(ProjectResource resource, GPUndoManager myUndoManager) {
        final ProjectResource res = resource;
        myUndoManager.undoableEdit("Delete Human OK", new Runnable() {
            public void run() {
                fireResourcesRemoved(new ProjectResource[] { res });
                resources.remove(res);
            }
        });
    }

    public void save(OutputStream target) {
    }

    public void clear() {
        fireCleanup();
        resources.clear();
    }

    public void addView(ResourceView view) {
        myViews.add(view);
    }

    private void fireResourceAdded(ProjectResource resource) {
        ResourceEvent e = new ResourceEvent(this, resource);
        for (Iterator i = myViews.iterator(); i.hasNext();) {
            ResourceView nextView = (ResourceView) i.next();
            nextView.resourceAdded(e);
        }
    }

    void fireResourceChanged(ProjectResource resource) {
        ResourceEvent e = new ResourceEvent(this, resource);
        for (Iterator i = myViews.iterator(); i.hasNext();) {
            ResourceView nextView = (ResourceView) i.next();
            nextView.resourceChanged(e);
        }
    }
    
    private void fireResourcesRemoved(ProjectResource[] resources) {
        ResourceEvent e = new ResourceEvent(this, resources);
        for (int i = 0; i < myViews.size(); i++) {
            ResourceView nextView = (ResourceView) myViews.get(i);
            nextView.resourcesRemoved(e);
        }
    }

    public void fireAssignmentsChanged(ProjectResource resource) {
        ResourceEvent e = new ResourceEvent(this, resource);
        for (Iterator i = myViews.iterator(); i.hasNext();) {
            ResourceView nextView = (ResourceView) i.next();
            nextView.resourceAssignmentsChanged(e);
        }
    }
    
    private void fireCleanup() {
        fireResourcesRemoved((ProjectResource[]) resources
                .toArray(new ProjectResource[resources.size()]));
    }

    /** Move up the resource number index */
    public void up(HumanResource hr) {
        int index =  resources.indexOf(hr);
        assert index>=0;
        resources.remove(index);
        resources.add(index - 1, hr);
        fireResourceChanged(hr);
    }

    /** Move down the resource number index */
    public void down(HumanResource hr) {
        int index =  resources.indexOf(hr);
        assert index>=0;
        resources.remove(index);
        resources.add(index + 1, hr);
        fireResourceChanged(hr);

    }

    public Map importData(ResourceManager resourceManager) {
        Map original2imported = new HashMap();
        if (resourceManager instanceof HumanResourceManager == false) {
            throw new IllegalArgumentException(
                    "I expect resource manager to be HumanResourceManager");
        }
        HumanResourceManager hrManager = (HumanResourceManager) resourceManager;
        List resources = hrManager.getResources();
        for (int i = 0; i < resources.size(); i++) {
            HumanResource next = (HumanResource) resources.get(i);
            importData(next, original2imported);
        }
        return original2imported;
    }

    private void importData(HumanResource next, Map original2imported) {
        HumanResource imported = (HumanResource) create(next.getName(),
                nextFreeId);
        if (next.getDaysOff() != null)
            for (int i = 0; i < next.getDaysOff().size(); i++)
                imported.addDaysOff((GanttDaysOff) ((GanttDaysOff) next
                        .getDaysOff().get(i)).clone());
        imported.setName(next.getName());
        imported.setDescription(next.getDescription());
        imported.setMail(next.getMail());
        imported.setPhone(next.getPhone());
        imported.setRole(next.getRole());
        List/*<CustomProperty>*/ customProperties = next.getCustomProperties();
        for (int i=0; i<customProperties.size(); i++) {
        	CustomProperty nextProperty = (CustomProperty) customProperties.get(i);
        	imported.addCustomProperty(nextProperty.getDefinition(), nextProperty.getValueAsString());
        }
        original2imported.put(next, imported);
    }

	public CustomPropertyManager getCustomPropertyManager() {
		return this;
	}

	public List getDefinitions() {
		List result = new ArrayList(customFields.values());
		return result;
	}

	CustomPropertyDefinition getCustomPropertyDefinition(String nextName) {
		return (CustomPropertyDefinition) customFields.get(nextName);
	}

	static String getValueAsString(Object value) {
		final String result;
		if (value!=null) {
			if (value instanceof GanttCalendar) {
				result = ((GanttCalendar)value).toXMLString();
			}
			else {
				result = String.valueOf(value);
			}
		}
		else {
			result = null;
		}
		return result;
	}

	public CustomPropertyDefinition createDefinition(String id, String typeAsString, String name, String defaultValueAsString) {
		final CustomPropertyDefinition stubDefinition = CustomPropertyManager.PropertyTypeEncoder.decodeTypeAndDefaultValue(typeAsString, defaultValueAsString);
		CustomPropertyDefinition result = new CustomPropertyDefinitionImpl(name, id, stubDefinition);
		addCustomField(result);
		return result;
	}

	public void importData(CustomPropertyManager source) {
		// TODO Auto-generated method stub
		
	}
	
	static class CustomPropertyDefinitionImpl implements CustomPropertyDefinition {
		private final String myName;
		private final String myID;
		private final Object myDefaultValue;
		private final String myDefaultValueAsString;
		private final Class myType;
		private final String myTypeAsString;

		CustomPropertyDefinitionImpl(String name, String id, CustomPropertyDefinition stub) {
			myName = name;
			myID = id;
			myDefaultValue = stub.getDefaultValue();
			myDefaultValueAsString = stub.getDefaultValueAsString();
			myType = stub.getType();
			myTypeAsString = stub.getTypeAsString();
		}
		public Object getDefaultValue() {
			return myDefaultValue;
		}

		public String getDefaultValueAsString() {
			return myDefaultValueAsString;
		}

		public String getID() {
			return myID;
		}

		public String getName() {
			return myName;
		}

		public Class getType() {
			return myType;
		}

		public String getTypeAsString() {
			return myTypeAsString;
		}
		
	}
}

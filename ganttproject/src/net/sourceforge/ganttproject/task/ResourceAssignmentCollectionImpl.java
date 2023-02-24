package net.sourceforge.ganttproject.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.ganttproject.resource.ProjectResource;
import net.sourceforge.ganttproject.resource.ResourceManager;
import net.sourceforge.ganttproject.roles.Role;

class ResourceAssignmentCollectionImpl implements ResourceAssignmentCollection {
    private final Map myAssignments = new HashMap();

    private final TaskImpl myTask;

    private ResourceManager myResourceManager;

    public ResourceAssignmentCollectionImpl(TaskImpl task,
            ResourceManager resourceManager) {
        myTask = task;
        myResourceManager = resourceManager;
    }

    private ResourceAssignmentCollectionImpl(
            ResourceAssignmentCollectionImpl collection) {
        myTask = collection.myTask;
        ResourceAssignment[] assignments = collection.getAssignments();
        for (int i = 0; i < assignments.length; i++) {
            ResourceAssignment next = assignments[i];
            ResourceAssignment copy = new ResourceAssignmentImpl(next
                    .getResource());
            copy.setLoad(next.getLoad());
            copy.setCoordinator(next.isCoordinator());
            copy.setRoleForAssignment(next.getRoleForAssignment());
            addAssignment(copy);
        }
    }

    public void clear() {
        ResourceAssignment[] assignments = getAssignments();
        for (int i = 0; i < assignments.length; i++) {
            assignments[i].delete();
        }
    }

    public ResourceAssignment[] getAssignments() {
        return (ResourceAssignment[]) myAssignments.values().toArray(
                new ResourceAssignment[myAssignments.size()]);
    }

    public ResourceAssignment getAssignment(ProjectResource resource) {
        return (ResourceAssignment) myAssignments.get(resource);
    }

    public ResourceAssignmentMutator createMutator() {
        return new ResourceAssignmentMutatorImpl();
    }

    ResourceAssignmentCollectionImpl copy() {
        return new ResourceAssignmentCollectionImpl(this);
    }

    public ResourceAssignment addAssignment(ProjectResource resource) {
        return auxAddAssignment(resource);
    }

    public void deleteAssignment(ProjectResource resource) {
        myAssignments.remove(resource);
    }

    private ResourceAssignment auxAddAssignment(ProjectResource resource) {
        final ResourceAssignment result = new ResourceAssignmentImpl(resource);
        addAssignment(result);
        return result;
    }

    private void addAssignment(ResourceAssignment assignment) {
        myAssignments.put(assignment.getResource(), assignment);
    }

    /** 
     * Removes the assignments related to the given resource.
     *  
     * @param resource Assigned resource
     */
    public void removeAssignment(ProjectResource resource) {
        (new ResourceAssignmentImpl(resource)).delete();
    }

    private Task getTask() {
        return myTask;
    }

    private class ResourceAssignmentImpl implements ResourceAssignment {
        private ResourceAssignment myAssignmentToResource;

        public ResourceAssignmentImpl(ProjectResource resource) {
            myAssignmentToResource = resource.createAssignment(this);
//            resource
//                    .setAssignmentCollection(ResourceAssignmentCollectionImpl.this);
        }

        public Task getTask() {
            return ResourceAssignmentCollectionImpl.this.getTask();
        }

        public ProjectResource getResource() {
            return myAssignmentToResource.getResource();
        }

        public float getLoad() {
            return myAssignmentToResource.getLoad();
        }

        // todo: transaction
        public void setLoad(float load) {
            myAssignmentToResource.setLoad(load);
        }

        /**
         * Deletes all the assignments and all the related assignments
         */
        public void delete() {
            ResourceAssignmentCollectionImpl.this
                    .deleteAssignment(getResource());
            myAssignmentToResource.delete();
        }

        public void setCoordinator(boolean responsible) {
            myAssignmentToResource.setCoordinator(responsible);
        }

        public boolean isCoordinator() {
            return myAssignmentToResource.isCoordinator();
        }

        public Role getRoleForAssignment() {
            return myAssignmentToResource.getRoleForAssignment();
        }

        public void setRoleForAssignment(Role role) {
            myAssignmentToResource.setRoleForAssignment(role);

        }

        public String toString() {
            return this.getResource().getName() + " -> "
                    + this.getTask().getName();
        }
    }

    private class ResourceAssignmentStub implements ResourceAssignment {
        private final ProjectResource myResource;

        private float myLoad;

        private boolean myCoordinator;

        private Role myRoleForAssignment;

        public ResourceAssignmentStub(ProjectResource resource) {
            myResource = resource;
        }

        public Task getTask() {
            return ResourceAssignmentCollectionImpl.this.getTask();
        }

        public ProjectResource getResource() {
            return myResource;
        }

        public float getLoad() {
            return myLoad;
        }

        public void setLoad(float load) {
            myLoad = load;
        }

        public void delete() {
        }

        public void deleteLocal() {
        }

        public void setCoordinator(boolean responsible) {
            myCoordinator = responsible;
        }

        public boolean isCoordinator() {
            return myCoordinator;
        }

        public Role getRoleForAssignment() {

            return myRoleForAssignment;
        }

        public void setRoleForAssignment(Role role) {
            myRoleForAssignment = role;

        }

        public String toString() {
            return this.getResource().getName() + " -> "
                    + this.getTask().getName();
        }
    }

    private class ResourceAssignmentMutatorImpl implements
            ResourceAssignmentMutator {
        private Map myQueue = new HashMap();

        public ResourceAssignment addAssignment(ProjectResource resource) {
            ResourceAssignment result = new ResourceAssignmentStub(resource);
            myQueue.put(resource, new MutationInfo(result, MutationInfo.ADD));
            return result;
        }

        public void deleteAssignment(ProjectResource resource) {
            MutationInfo info = (MutationInfo) myQueue.get(resource);
            if (info == null) {
                myQueue.put(resource, new MutationInfo(resource,
                        MutationInfo.DELETE));
            } else if (info.myOperation == MutationInfo.ADD) {
                myQueue.remove(resource);
            }
        }

        public void commit() {
            List mutations = new ArrayList(myQueue.values());
            Collections.sort(mutations);
            for (int i = 0; i < mutations.size(); i++) {
                MutationInfo next = (MutationInfo) mutations.get(i);
                switch (next.myOperation) {
                case MutationInfo.DELETE: {
                    myAssignments.remove(next.myResource);
                    break;
                }
                case MutationInfo.ADD: {
                    ResourceAssignment result = auxAddAssignment(next.myResource);
                    result.setLoad(next.myAssignment.getLoad());
                    result.setCoordinator(next.myAssignment.isCoordinator());
                    result.setRoleForAssignment(next.myAssignment
                            .getRoleForAssignment());
                }
                default:
                    break;
                }
            }
        }

    }

    private static class MutationInfo implements Comparable {
        static final int ADD = 0;

        static final int DELETE = 1;

        private final ResourceAssignment myAssignment;

        private final int myOrder;

        private static int ourOrder;

        private int myOperation;

        private final ProjectResource myResource;

        public MutationInfo(ResourceAssignment assignment, int operation) {
            myAssignment = assignment;
            myOrder = ourOrder++;
            myOperation = operation;
            myResource = assignment.getResource();
        }

        public MutationInfo(ProjectResource resource, int operation) {
            this.myAssignment = null;
            this.myOrder = ourOrder++;
            this.myOperation = operation;
            this.myResource = resource;
        }

        public boolean equals(Object o) {
            boolean result = o instanceof MutationInfo;
            if (result) {
                result = myAssignment.getResource().equals(
                        ((MutationInfo) o).myAssignment.getResource());
            }
            return result;
        }

        public int compareTo(Object o) {
            if (!(o instanceof MutationInfo)) {
                throw new IllegalArgumentException();
            }
            return myOrder - ((MutationInfo) o).myOrder;
        }
    }

    public void importData(ResourceAssignmentCollection assignmentCollection) {
        if (myTask.isUnplugged()) {
            ResourceAssignment[] assignments = assignmentCollection
                    .getAssignments();
            for (int i = 0; i < assignments.length; i++) {
                ResourceAssignment next = assignments[i];
                addAssignment(next);
            }
        } else {
            ResourceAssignment[] assignments = assignmentCollection
                    .getAssignments();
            for (int i = 0; i < assignments.length; i++) {
                ResourceAssignment next = assignments[i];
                ProjectResource nextResource = next.getResource();
                ProjectResource nextImportedResource = myResourceManager
                        .getById(nextResource.getId());
                if (nextImportedResource != null) {
                    ResourceAssignment copy = new ResourceAssignmentImpl(
                            nextImportedResource);
                    copy.setLoad(next.getLoad());
                    copy.setCoordinator(next.isCoordinator());
                    copy.setRoleForAssignment(next.getRoleForAssignment());
                    addAssignment(copy);
                }
            }
        }
    }

	public ProjectResource getCoordinator() {
		for (Iterator assignments = myAssignments.values().iterator(); assignments.hasNext();) {
			ResourceAssignment next = (ResourceAssignment) assignments.next();
			if (next.isCoordinator()) {
				return next.getResource();
			}
		}
		return null;
	}

}

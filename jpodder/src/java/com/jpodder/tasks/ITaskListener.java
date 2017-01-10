package com.jpodder.tasks;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.0
 */
public interface ITaskListener {
    
    /**
     * A task failed.
     * @param e
     */
    public void taskCompleted(TaskEvent e);
    
    public void taskAborted(TaskEvent e);
    
    public void taskFailed(TaskEvent e);

}

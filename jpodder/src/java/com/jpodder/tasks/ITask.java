package com.jpodder.tasks;
/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier</a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @version 1.1
 */
public interface ITask {

    /**
     * Called to find out how much work needs to be done.
     * @return in The length of a task.
     */
    public int getLengthOfTask();

    public void setLengthOfTask(int pLength);
    
    /**
     * Called to find out how much has been done.
     * @return int The current value.
     */ 
    public int getCurrent();
    
    public void setCurrent(int pCurrent);

    
    public void stop();

    /**
     * Called to find out if the task has completed.
     * 
     * @return boolean If a task is completed.
     */
    public boolean isDone();
    
    public void setDone(boolean pDone);
    
    public boolean isCancelled();
    
    public void setCancelled(boolean pCancelled);
    
    /**
     * Returns the most recent status message, or null if there is no current
     * status message.
     * @return Get the message
     */
    public String getMessage();
    
    public void setMessage(String pMessage);
    
    
}

package com.jpodder.tasks;

/**
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @version 1.1
 */
public abstract class AbstractTaskWorker extends TaskWorker implements ITask {

    public AbstractTaskWorker() {
    }

    /**
     * The total number of bytes to download.
     */
    protected int mLength = 100;

    /**
     * The current number of downloaded bytes.
     */
    protected int mCurrent = 0;

    /**
     * The message of this download.
     */
    protected String mMessage;

    /**
     * If the task has been cancelled
     */
    protected boolean mCancelled = false;

    /**
     * If the task is completed
     */
    protected boolean mDone = false;

    /**
     * Called to find out how much work needs to be done.
     * 
     * @return int
     */
    public int getLengthOfTask() {
        return mLength;
    }

    /**
     * Called to find out how much has been done.
     * 
     * @return int
     */
    public int getCurrent() {
        return mCurrent;
    }

    /**
     * Cancel the task.
     */
    public void stop() {
        mMessage = null;        
    }

    /**
     * Called to find out if the task has completed.
     * 
     * @return boolean
     */
    public boolean isDone() {
        return mDone;
    }

    /**
     * Returns the most recent message, or null if there is no current status
     * message.
     * 
     * @return String
     */
    public String getMessage() {
        return mMessage;
    }

    public void setLengthOfTask(int pLength) {
        mLength = pLength;
    }

    public void setCurrent(int pCurrent) {
        mCurrent = pCurrent;
    }

    public void setMessage(String pMessage) {
        mMessage = pMessage;
    }

    public void setDone(boolean pDone) {
        mDone = pDone;
    }

    public boolean getDone() {
        return mDone;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void setCancelled(boolean pCancelled) {
        mCancelled = pCancelled;
    }

}

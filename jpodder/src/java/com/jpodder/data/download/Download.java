package com.jpodder.data.download;

import java.io.File;

import com.jpodder.data.feeds.IXPersonalEnclosure;
import com.jpodder.tasks.AbstractTask;

/**
 * A download task, maintaining the model and status of a download.
 *
 * @author <a href="mailto:christophe@kualasoft.com">Christophe Bouhier </a>
 * @author <a href="mailto:andreas.schaefer@madplanet.com">Andreas Schaefer</a>
 * @since 1.0
 * @version 1.1
 **/
public class Download extends AbstractTask {

    protected IXPersonalEnclosure mEnclosure;

    /**
     * The status of this download.
     */
    protected int mState;

    /**
     * The previous number of downloaded bytes.
     */
    protected int mPrevious = 0;

    /**
     * The starting offset of the download in bytes.
     */
    protected int start = 0;

    /**
     * The elapsed download time in miliseconds.
     */
    protected long timeElapsed = 0;

    /**
     * The speed in bytes per second.
     */
    protected float bytespersecond = 0;

    private int retryCounter = 0;
    
    private long mStateTime = 0;
    
    protected File mTempFile = null;
    
    public Download(IXPersonalEnclosure encl) {
        mEnclosure = encl;
    }

    /**
     * Get the enclosure assiociated with this downloads.
     * 
     * @return Enclosure
     */
    public IXPersonalEnclosure getEnclosure() {
        return mEnclosure;
    }

    /**
     * Cancel the task.
     */
    public void stop() {
        mState = DownloadLogic.CANCELLED;
    }

    /**
     * Called to find out if the task has completed.
     * 
     * @return boolean
     */
    public boolean isDone() {
        return mState == DownloadLogic.COMPLETED;
    }

    /**
     * Calculate the downloadspeed. This is an average speed measured over
     * the total download time. For resumed download the offset position is
     * subtracted from the position.
     */
    public void calculateSpeed() {
        // The average speed measured over the total download time.
        bytespersecond = ((float) mCurrent - (float) start) / (float) timeElapsed;
    }

    /**
     * @return Returns the retryCounter.
     */
    public int getRetryCounter() {
        return retryCounter;
    }

    /**
     * @param retryCounter
     *            The retryCounter to set.
     */
    public void setRetryCounter(int retryCounter) {
        this.retryCounter = retryCounter;
    }

    /**
     * @return Returns the state.
     */
    public int getState() {
        return mState;
    }
    
    public long getTimeElapsed(){
       return timeElapsed;
    }
    
    /**
     * @param pState
     *            The state to set.
     */
    public void setState(int pState) {
        mState = pState;
        mStateTime = 0; // reset the status period
    }
    
    public void setStart(int pStart){
        start = pStart;
    }
    
    
    public void incrementStateTime(long lTime){
        mStateTime += lTime;
    }
    
    public long getStateTime(){
        return mStateTime;
    }

    public float getBytesPerSecond() {
        return bytespersecond;
    }
    
    /**
     * Get the temporary storage file.
     * @return
     */
    public File getTempFile(){
        return mTempFile;
    }
}
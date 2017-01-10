package com.jpodder.tasks;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
public class TaskLogic implements ITaskListener {
	
	
	Logger mLog = Logger.getLogger(getClass().getName());
	
    CopyOnWriteArrayList<ITaskListener> mTaskListeners= new CopyOnWriteArrayList<ITaskListener>();
	CopyOnWriteArrayList<ITask> mTasks = new CopyOnWriteArrayList<ITask>();
	private static TaskLogic mSelf = new TaskLogic();

	public static TaskLogic getInstance(){
		return mSelf;
	}
	public TaskLogic(){
//		ExecutorCompletionService lCompletion = java.util.concurrent.
//		ExecutorService lExec = Executors.newSingleThreadExecutor();
//		((ThreadPoolExecutor)lExec).
	}
	
	public void addTask(){
		
	}
	
    public void addListener(ITaskListener pListener) {
        if (!mTaskListeners.contains(pListener)) {
            mTaskListeners.add(pListener);
        }
    }

    public void removeListener(ITaskListener pListener) {
        if (mTaskListeners.contains(pListener)) {
            mTaskListeners.remove(pListener);
        }
    }

    public boolean hasListener(ITaskListener pListener) {
        return mTaskListeners.contains(pListener);
    }

    public void fireTaskFailed(Object pSrc, Object pResult) {
        TaskEvent e = new TaskEvent(pSrc, pResult);
        Object[] lListeners = mTaskListeners.toArray();
        for (int i = 0; i < lListeners.length; i++) {
            ITaskListener lListener = (ITaskListener) lListeners[i];
            lListener.taskFailed(e);
        }
    }

    public void fireTaskCompleted(Object pSrc, Object pResult) {
        TaskEvent e = new TaskEvent(pSrc, pResult);
        Object[] lListeners = mTaskListeners.toArray();
        for (int i = 0; i < lListeners.length; i++) {
            ITaskListener lListener = (ITaskListener) lListeners[i];
            lListener.taskCompleted(e);
        }
    }

    public void fireTaskAborted(Object pSrc, Object pResult) {
        TaskEvent e = new TaskEvent(pSrc, pResult);
        Object[] lListeners = mTaskListeners.toArray();
        for (int i = 0; i < lListeners.length; i++) {
            ITaskListener lListener = (ITaskListener) lListeners[i];
            lListener.taskAborted(e);
        }
    }
    
    public void add(ITask pTask, final Object pTarget, Object pListener){
    	synchronized(mTasks){
    		mTasks.add(pTask);	
    	}
    }

    public void remove(ITask pTask, final Object pTarget, Object pListener){
    	synchronized(mTasks){
    		mTasks.remove(pTask);	
    	}
    }
    
    public Iterator getTasks(){
    	return mTasks.iterator();
    }
    
    
    // Set of generic listeners.
	public void taskCompleted(TaskEvent e) {
		mLog.info("Task Completed:" + e.getSource());
	}
	
	public void taskAborted(TaskEvent e) {
		mLog.info("Task Aborted:" + e.getSource());
	}
	
	public void taskFailed(TaskEvent e) {
		mLog.info("Task Failed:" + e.getSource());
	}
}
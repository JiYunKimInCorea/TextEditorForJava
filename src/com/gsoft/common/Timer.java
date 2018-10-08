package com.gsoft.common;

import com.gsoft.common.interfaces.TimerListener;

public class Timer /*extends CountDownTimer*/extends Thread {
	public long finishTime;
	public long interval;
	public boolean isStoped=true;
	TimerListener listener;
	private boolean exit;
	
	long startTime;
	long tickCurTime;
	
	//long sumOfTime=0;
	int iName;
	private long syncTimeNonStop = 200;
	private long syncTimeStop = 2000;
	
	
	public synchronized void setSyncTime(long syncTimeNonStop, long syncTimeStop) {
		this.syncTimeNonStop = syncTimeNonStop;
		this.syncTimeStop = syncTimeStop;
	}
	
	public synchronized void setInterval(long interval) {
		this.interval = interval;
	}
	
	public synchronized void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}
	
	synchronized boolean getIsStoped() {
		return isStoped;
	}
	
	synchronized void setIsStoped(boolean isStoped) {
		this.isStoped = isStoped;
	}
	
	public synchronized void setExit(boolean b) {
		// TODO Auto-generated method stub
		exit = b;
	}
    
    public synchronized boolean getExit() {
		// TODO Auto-generated method stub
		return exit;
	}
	
	public Timer(int iName, TimerListener listener, long countDownInterval, long finishTime) {		
		//super(finishTime, countDownInterval);
		// TODO Auto-generated constructor stub
		this.iName = iName;
		this.listener = listener;
		this.interval = countDownInterval;
		this.finishTime = finishTime;
		//sumOfTime = 0;
		isStoped = true;
		startTime = System.currentTimeMillis();
    	tickCurTime = System.currentTimeMillis();
		start();
	}
	
    public void cancelTimer() {
    	//sumOfTime = 0;
    	//isStoped = true;
    	/*while (true) {
    		setIsStoped(true);
    		if (getIsStoped()==true) {
    			break;
    		}
    	}*/
    	setIsStoped(true);
    	if (isAlive()) {
    		//interrupt();
    	}
    }
    
    public void startTimer(){
    	//start();
		/*while (true) {
			setIsStoped(false);
			if (getIsStoped()==false) {
				break;
			}
		}*/
		setIsStoped(false);
    	if (isAlive()) {
    		interrupt();
    	}
    	
    	startTime = System.currentTimeMillis();
    	tickCurTime = System.currentTimeMillis();
    }
    
    public void killTimer() {
    	/*while (true) {
    		setExit(true);
    		if (getExit()==true) {
    			break;
    		}
    	}*/
    	setExit(true);
    	cancelTimer();
    	/*try {
    		if (isAlive()) {
	    		interrupt();
				join();
    		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}*/
    }
    
    

	public void run() {    	
    	while(!getExit()) {
    		try {
    			if (!getIsStoped()) {
    				/*long diff = (finishTime - sumOfTime);
    				if (diff>500) {
						Thread.sleep(interval);
						sumOfTime += interval;
						listener.onTick(this);	// call back
    				}
    				else {
    					
    						listener.onFinish(this);	// call back
			    	}*/
    				
    				long curTime = System.currentTimeMillis();
    				long diffTime = curTime-tickCurTime;
    				
    				if (getIsStoped()) continue;
    				
    				if (interval<=diffTime) {
						listener.onTick(this);	// call back
						tickCurTime = curTime;
					}
    				
    				if (getIsStoped()) continue;
					
					diffTime = curTime-startTime;					
					if (finishTime<=diffTime) {
						listener.onFinish(this);	// call back
					}					
					
					Thread.sleep(syncTimeNonStop);					
    			}
    			else {
    				Thread.sleep(syncTimeStop);
    			}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//Thread.sleep(syncTimeStop);
			}
    		
    	}
    }
    
    static Timer Interval(int iName, TimerListener listener, Timer oldTimer, long interval, long finish) {
    	if (oldTimer!=null) oldTimer.cancelTimer();
    	oldTimer = null;
    	return new Timer(iName, listener, interval, finish);
    	
    }
}
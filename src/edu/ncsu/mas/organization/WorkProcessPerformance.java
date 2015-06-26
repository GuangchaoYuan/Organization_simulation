package edu.ncsu.mas.organization;

import java.util.ArrayList;

public class WorkProcessPerformance {
	private int wpId;
	private int reqId;
	private int deadline;
	private int startTime;
	private int endTime;
	private double wpPerf;
	private double efficiency;
	private int numDone;
	private ArrayList<TaskPrincipal> tPrincipalList;
	
	public WorkProcessPerformance(int wpId, int reqId, int startTime, int deadline){
		this.wpId = wpId;
		this.reqId = reqId;
		this.startTime = startTime;
		this.deadline = deadline;
		this.wpPerf = 0;
		this.efficiency = 0;
		this.numDone = 0;
		tPrincipalList = new ArrayList<TaskPrincipal>();
	}
	
	private class TaskPrincipal{
		int taskId;
		String agentName;
		boolean help; //whether the task is assisted by others
		int tStartTime;
		int tEndTime;
		double performance;
		ArrayList<String> helperList;//agent Id list who are responsible for the task
		
		private TaskPrincipal(int tId, String aName){
			this.taskId = tId;
			this.agentName = aName;
			this.help = false;
			this.tStartTime = 0;
			this.tEndTime = 0;
			this.performance =0;
			helperList = new ArrayList<String>();
		}
		
		private int getTaskId(){
			return taskId;
		}
		
	}
	
	
	
	public void addElement(int taskId, String agentName){
		TaskPrincipal e = new TaskPrincipal(taskId, agentName);
		tPrincipalList.add(e);
	}
	
	public int getStartTime(){
		return this.startTime;
	}
	
	public int getWorkProcessId(){
		return this.wpId;
	}
	
	//return value indicates whether the work process has been finished
	public boolean updateWorkProcess(int taskId, int tS, int tE, double perf){
		boolean done = false;
		for(int i = 0; i<tPrincipalList.size(); i++){
			TaskPrincipal tp = tPrincipalList.get(i);
			if(taskId == tp.getTaskId()){
				tp.tStartTime = tS;
				tp.tEndTime = tE;
				tp.performance = perf;
				numDone++;
				
				//the ending time of the work process is the ending time of the last finished task
				if(numDone==tPrincipalList.size()){
					//endTime = tE;
					done = true;
					calWpTime();
					calPerformance();
					calEfficiency();
				}
				break;
			}
		}
		return done;
	}
	
	//calculate the start time and end time of a work process
	private void calWpTime(){
		int sT = tPrincipalList.get(0).tStartTime;
		int eT = tPrincipalList.get(0).tEndTime;
		for(int i = 1; i<tPrincipalList.size(); i++){
			TaskPrincipal tp = tPrincipalList.get(i);
			if(tp.tStartTime<sT)
				sT = tp.tStartTime;
			if(tp.tEndTime>eT)
				eT = tp.tEndTime;
		}
		
		startTime = sT;
		endTime = eT;
	}
		
	
	//calculate the performance of the work process
	private void calPerformance(){
		double sum = 0;
		for(int i = 0; i<tPrincipalList.size(); i++){
			TaskPrincipal tp = tPrincipalList.get(i);
			sum+= tp.performance;
		}
		
		wpPerf = sum/tPrincipalList.size();
	}
	
	private void calEfficiency(){
		efficiency = (double) (endTime - startTime);
		System.out.println("startime: " + startTime + " endTime: " + endTime +
				" efficiency: " + efficiency);
	}
	
	public double getWorkProcessPerformance(){
		return this.wpPerf;
	}
	
	public double getWorkProcessEfficiency(){
		return this.efficiency;
	}
	
	
	

	
}

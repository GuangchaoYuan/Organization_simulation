package edu.ncsu.mas.organization;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskAssignMessage implements Serializable{
	private String assignId = "";
	private int wpId = 0;
	private int taskId = 0;
	private int deadline = 0;
	private int taskType = 0;
	private int lowerDL = 0;
	private int upperDL = 0;
	private int tStartTime = 0;
	private int estDuration = 0;
	private int tEndTime = 0;
	private double performance = 0;
	
	
	public TaskAssignMessage(int wpId, int taskId, int deadline, int taskType, int lowerDL, int upperDL){
		this.wpId = wpId;
		this.taskId = taskId;
		this.assignId = String.valueOf(wpId) + "-" + String.valueOf(taskId);
		this.deadline = deadline;
		this.taskType = taskType;
		this.lowerDL = lowerDL;
		this.upperDL = upperDL;
	}
	
	public TaskAssignMessage(String assignId, int tStartTime, int estDuration, double performance){
		this.assignId = assignId;
		this.tStartTime = tStartTime;
		this.estDuration = estDuration;
		this.performance = performance;
		splitAssignId();
	}
	
	private void splitAssignId(){
		String[] words = assignId.split("-");
		wpId = Integer.parseInt(words[0]);
		taskId = Integer.parseInt(words[1]);
	}
	
	public String getAssignId(){
		return assignId;
	}
	
	public int getDeadline(){
		return deadline;
	}
	
	public int getTaskType(){
		return taskType;
	}
	
	public int getWorkProcessId(){
		return wpId;
	}
	
	public int getTaskId(){
		return taskId;
	}
	
	public int getLowerDeadline(){
		return lowerDL;
	}
	
	public int getUpperDeadline(){
		return upperDL;
	}
	
	public int getStartTime(){
		return tStartTime;
	}
	
	public int getEndTime(){
		tEndTime = tStartTime + estDuration;
		return tEndTime;
	}
	
	public double getPerformance(){
		return performance;
	}

}

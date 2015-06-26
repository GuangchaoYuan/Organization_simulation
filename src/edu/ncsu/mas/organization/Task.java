package edu.ncsu.mas.organization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Task {
	//private double difficulty = 0; //difficulty of the tasks
	private int taskId = 0;
	private int wpDeadline = 0;
	private int deadline = 0;
	private int resource = 0; //number of required resources
	private int type = 0; //type of the tasks: [1, range]
	private int duration = 0;//duration of finishing a task
	private int lowerDuration = 0; //[lowerDuration, upperDuration] is an interval for the duration
	private int upperDuration = 0;
	private int requiredSkill = 0; //each task only has one required skill (automic)
	private double[] quality = new double[3];//quality arrays: quality[0] means probability of low..

	Random generator = new Random();
	//Global gb = Global.getInstance();
	
	//protected ArrayList<Integer> precedeList = new ArrayList<Integer>();//arraylist of task index numbers that should be completed in advance
	//protected ArrayList<Integer> followList = new ArrayList<Integer>();//arraylist of task index numbers that should be completed in advance
	
	public Task(int taskId, int wpDeadline, int numTask){
		this.taskId = taskId;
		this.wpDeadline = wpDeadline;
		Arrays.fill(quality, 0);
		//this.generateTaskDifficulty();
		this.generateTaskResource();
		this.generateTaskType();
		this.generateTaskDuration();
		this.generateRequiredSkill();
		this.generateTaskDurationInterval();
	}
	
	public Task(int taskId, int type, int dur, int lowerDur, int upperDur, int skill){
		this.taskId = taskId;
		this.type = type;
		this.duration = dur;
		this.lowerDuration = lowerDur;
		this.upperDuration = upperDur;
		this.requiredSkill = skill;
	}
	
	/*private void generateTaskDifficulty(){
		difficulty = generator.nextDouble();
	}*/
	
	private void generateTaskResource(){
		resource = generator.nextInt(Global.taskResourceRange);
	}
	
	//range: [1, taskTypeRange]
	private void generateTaskType(){
		type = generator.nextInt(Global.taskTypeRange)+1;
	}
	
	private void generateTaskDuration(){
		int interval = Global.deadlineUpperBound - Global.deadlineLowerBound;
		duration = generator.nextInt(interval+1) + Global.deadlineLowerBound;
	}
	
	//each task only has one required skill
	private void generateRequiredSkill(){
		requiredSkill = generator.nextInt(Global.taskAgentSkillRange);
	}
	
	//it is a range for the task duration, depending on the task duration and task type: type 1 means most difficult
	private void generateTaskDurationInterval(){
		double prob = 0;
		//Ensure that Prob is not zero
		while(prob==0)
			prob= generator.nextDouble();
		prob = prob/type;
		int extra = (int) (duration * prob);
		if(extra < 2)
			extra = 2;
		lowerDuration = duration-extra/2;
		upperDuration = lowerDuration + extra;
	}
	
	/*public double getDifficulty(){
		return this.difficulty;
	}*/
	
	public int getResource(){
		return this.resource;
	}
	
	public int getType(){
		return this.type;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public int getLowerDuration(){
		return this.lowerDuration;
	}
	
	public int getUpperDuration(){
		return this.upperDuration;
	}
	
	public int getDeadline(){
		return this.deadline;
	}
	public void setDeadline(int deadline){
		this.deadline = deadline;
	}
	
	public int getRequiredSkill(){
		return this.requiredSkill;
	}
	
	public int getTaskId(){
		return this.taskId;
	}

}

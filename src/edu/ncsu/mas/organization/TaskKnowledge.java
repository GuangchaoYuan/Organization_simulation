package edu.ncsu.mas.organization;

import java.util.Random;

import jsc.distributions.Beta;

public class TaskKnowledge {
	private int agentState = 0;
	private int agentTrait = 0;
	private int organizationState = 0;
	private int organizationTrait = 0;
	private Task task;
	private double timeTP = 0; //output, time to perform the task
	private double quality = 0; //output, quality of the task
	
	public TaskKnowledge(int aS, int aT, int oS, int oT, Task t){
		this.agentState = aS;
		this.agentTrait = aT;
		this.organizationState = oS;
		this.organizationTrait = oT;
		this.task = t;
	}
	
	public void computeTimeTP(){
		
	}
	
	public void computeQuality(){
		
	}
	
	public static void main(String[] args){
		Beta d3 = new Beta(1, 3);
		Random g = new Random();
		double x = g.nextDouble();
		double p = d3.cdf(x);
		double y = d3.pdf(x);
		System.out.println("x: "+ x + " cdf: " + p + " pdf: " + y);
	}

}

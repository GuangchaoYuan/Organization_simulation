package edu.ncsu.mas.organization;

import java.util.Random;

public class GlobalTimer {
	private static GlobalTimer singleton = new GlobalTimer();
	
	private static int counter = 0;
	//state transition matrix
		
	//private static int[][] stMatrix = new int[Global.taskTypeRange][Global.taskAgentStateRange];//state transition matrix
	private GlobalTimer(){
	}
	
	public static GlobalTimer getInstance(){
		return singleton;
	}
	
	
	public void increaseCounter(){
		counter++;
		//System.out.println("counter: " + counter);
	}
	
	public static int getCurrentTime(){
		return counter;
	}
	

}

package edu.ncsu.mas.organization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorkProcess {
	private int wpId = 0; // work process id
	private int numTask = 0;
	private final ArrayList<Task> allTasks = new ArrayList<Task>();
	
	private int deadline = 0; //deadline of the work process
	//private int[][] taskGraph; //a directed graph of executing tasks
	private boolean visited[];//whether a task has been visited
	Set<List<Integer>> taskSequence = new HashSet<List<Integer>>();
	
	Random generator = new Random();
	//Global gb = Global.getInstance();
	
	public WorkProcess(int wpId, int numTask){
		System.out.println("wpId: " + wpId + " numTask: " + numTask);
		this.wpId = wpId;
		this.numTask = numTask;
		visited = new boolean[numTask];
		System.out.println("visited length: " + visited.length);
		this.generateWorkProcessDeadline();
		//new task
		for(int i = 0; i < numTask; i++){
			allTasks.add(new Task(i, deadline, numTask));
			visited[i] = false;
		}
		this.generateTaskConstraint();
		this.generateTaskDeadline();
	}
	
	public WorkProcess(int wpId, int numTask, int deadline, ArrayList<String> line){
		this.wpId = wpId;
		this.numTask = numTask;
		this.deadline = deadline;
		int tId = 0;
		int type = 0;
		int dur = 0;
		int lowerDur = 0;
		int upperDur = 0;
		int skill = 0;
		for(int i = 0; i<line.size(); i++){
			String[] words = line.get(i).split(",");
			tId = Integer.parseInt(words[0]);
			type = Integer.parseInt(words[1]);
			dur = Integer.parseInt(words[2]);
			lowerDur = Integer.parseInt(words[3]);
			upperDur = Integer.parseInt(words[4]);
			skill = Integer.parseInt(words[5]);
			allTasks.add(new Task(tId, type, dur, lowerDur, upperDur, skill));
		}
	}
	
	public void printWorkProcess(){
		String storeFile = "D:\\Organization_Analysis\\simulation\\wp_" + String.valueOf(wpId) + ".csv";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(storeFile));
			writer.append(String.valueOf(wpId) + ",");
			writer.append(String.valueOf(numTask)+ ",");
			writer.append(String.valueOf(deadline));
			writer.newLine();
			writer.flush();
			
			for(int i = 0; i<allTasks.size();i++){
				Task t = allTasks.get(i);
				writer.append(String.valueOf(t.getTaskId()) + ",");
				writer.append(String.valueOf(t.getType()) + ",");
				writer.append(String.valueOf(t.getDuration()) + ",");
				writer.append(String.valueOf(t.getLowerDuration()) + ",");
				writer.append(String.valueOf(t.getUpperDuration()) + ",");
				writer.append(String.valueOf(t.getRequiredSkill()));
				writer.newLine();
				writer.flush();
			}
			
			writer.close();
			System.out.println("Finish writing the file for work process " + wpId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//generate the task order (which task should be completed before others)
	private void generateTaskConstraint(){
		//number of constraints for a work process
		if(numTask>1){
			int numConstraint = (int) (numTask * Global.wpTaskConstraintProb);
			if(numConstraint < 1)
				numConstraint = 1;
			
			//number of tasks in a sequence (constraint)
			int numTaskSequence = (int) (numTask * Global.wpTaskSequenceProb);
			if(numTaskSequence <2)
				numTaskSequence =2;
			
			List<Integer> template = new ArrayList<Integer>();
			for(int j = 0; j<numTask; j++)
				template.add(j);
			
			for(int i = 0; i < numConstraint; i++){
				List<Integer> items = new ArrayList<Integer>(template);
				List<Integer> subItems = new ArrayList<Integer>();
				do{
					Collections.shuffle(items);
					subItems = items.subList(0, numTaskSequence);
					//System.out.println("items size: " + items.size() + "num");
				}while(!taskSequence.add(subItems));
			} 
		}
		
			
	}
	
	private void generateWorkProcessDeadline(){
		this.deadline = generator.nextInt(Global.wpDeadlineRange);
	}
	
	//generate task deadline given their constraints and the work process deadline
	private void generateTaskDeadline(){
		Task current;
		Task after;
		int dl = 0;
		int index = 0;
		for(List<Integer> s: taskSequence){
			index = s.get(s.size()-1);
			current = allTasks.get(index);
			current.setDeadline(deadline);
			visited[index] = true;
			after = current;
			for(int i = (s.size()-2); i>=0; i--){
				index = s.get(i);
				current = allTasks.get(index);
				dl = after.getDeadline() - after.getUpperDuration();
				if(!visited[index]){
					current.setDeadline(dl);
					visited[index] = true;
				}
				else{
					int oldDL = current.getDeadline();
					if(dl < oldDL){
						current.setDeadline(dl);
					}
				}
				after = current;
			}
		}
		
		//check other unvisited tasks
		for(int j = 0; j<visited.length; j++){
			if(!visited[j]){
				current = allTasks.get(j);
				current.setDeadline(deadline);
				visited[j]=true;
			}
		}
		
	}
	
	public ArrayList<Task> getAllTasks(){
		return this.allTasks;
	}
	
	public int getWorkProcessId(){
		return this.wpId;
	}
	
	public int getDeadline(){
		return this.deadline;
	}

}

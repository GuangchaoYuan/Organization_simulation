package edu.ncsu.mas.organization;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Global {
	private static Global singleton = new Global(); 
	
	//parameters about task agents
	static final int numTaskAgent = 5;
	static final int taskAgentStateRange = 3;
	static final int taskAgentTraitRange = 3;
	static final int taskAgentSkillRange = 4; //maximum number of skills
	static final int taskAgentQueueLength = 5;
	
	//parameters about organization agents
	static final int numResource = 100000;
	static final int organStateRange = 2;
	static final int organTraitRange = 2;
	
	//parameters about tasks
	//static final int taskDifficultyRange = 5;
	static final int taskResourceRange = 10;
	static final int taskTypeRange = 5;
	static final int taskRange = 10; //maximum number of tasks for a work process
	static final int taskSkillRange = 2;//maximum number of required skills for a task
	
	//parameters about requirements
	static final int reqRange = 3; //maximum number of requirements
	static final int reqWpRange = 2; //maximum number of work process for one requirement: [1, reqWpRange]
	
	//parameters about distribution
	static final double betaFirst = 1; //first parameter for beta distribution
	static final double betaSecond = 3; //second parameter for beta distribution
	
	//parameters about time
	static final long milliSecondToHour = 3600*1000; //we use one millisecond in systems to represent one hour in real life 
	static final int deadlineLowerBound = 20;//minimum deadline for a task: 1 hour
	static final int deadlineUpperBound = 4380/(taskRange*reqRange*reqWpRange); //maximum deadline for a task; 8760 hours, representing 1 year
	
	//parameters about work processes
	static final double wpTaskConstraintProb = 0.2; // the proportion of tasks that have constraints for a work process
	static final double wpTaskSequenceProb = 0.6; // the proportion of tasks that have constraints for a sequence
	static final int wpRange = 5; //number of work processes
	static final int wpDeadlineRange = 4380/(reqRange*reqWpRange); //maximum deadline for a work process
	
	//parameters about performance
	static final double perfCoefficient = 0.7;
	static final double capabilityCoefficient = 0.3;
	
	//state transition matrix
	static int[][] stMatrix = new int[taskTypeRange][taskAgentStateRange];//state transition matrix
	
	//performance map
	static HashMap<String, Double> perfMap = new HashMap<String, Double>();

	static int counter = 0; //global timer
	
	static{
		/*Random generator = new Random();
		for(int row = 0; row < taskTypeRange; row++){
			for(int col = 0; col <taskAgentStateRange; col++){
				stMatrix[row][col] = generator.nextInt(taskAgentStateRange)+1;
			}
		}*/
		try {
			String storeFile1 = "D:\\Organization_Analysis\\simulation\\stateTransition.csv";
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(storeFile1));
			int p = 0;
			while((line = reader.readLine())!=null){
				String[] words = line.split(",");
				for(int q = 0; q < words.length; q++){
					stMatrix[p][q] = Integer.parseInt(words[q]);
				}
				p++;
			}
			reader.close();
			//System.out.println("Finish reading the state transition information");
			
			String storeFile2 = "D:\\Organization_Analysis\\simulation\\perf.csv";
			reader = new BufferedReader(new FileReader(storeFile2));
			
			String key = "";
			double perf = 0;
			while((line = reader.readLine())!=null){
				String[] words = line.split(",");
				key = words[0] + "-" + words[1] + "-" + words[2];
				perf = Double.parseDouble(words[3]);
				perf = perf * 0.01;
				perfMap.put(key, perf);
			}
			reader.close();
			//System.out.println("Finish reading the performance information");
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private Global(){
	}
	
	public static Global getInstance(){
		return singleton;
	}
	
	public static double getPerformance(int type, int state, int trait){
		double perf = 0;
		String key = String.valueOf(type) + "-" + String.valueOf(state) + "-" + String.valueOf(trait);
		perf = perfMap.get(key);
		return perf;
	}
	
	public void increaseCounter(){
		counter++;
		//System.out.println("counter: " + counter);
	}
	
	public int getCurrentTime(){
		return counter;
	}
	
	/*public int[][] getStMatrix(){
		return stMatrix;
	}
	public int getNumTaskAgent(){
		return numTaskAgent;
	}
	
	public int getTaskAgentStateRange(){
		return taskAgentStateRange;
	}
	
	public int getTaskAgentTraitRange(){
		return taskAgentTraitRange;
	}
	
	public int getTaskAgentSkillRange(){
		return taskAgentSkillRange;
	}
	
	public int getTaskAgentQueueLength(){
		return taskAgentQueueLength;
	}
	
	public int getNumResource(){
		return numResource;
	}
	
	public int getOrganStateRange(){
		return organStateRange;
	}
	
	public int getOrganTraitRange(){
		return organTraitRange;
	}
	
	public int getTaskResourceRange(){
		return taskResourceRange;
	}
	
	public int getTaskTypeRange(){
		return taskTypeRange;
	}
	
	public int getTaskRange(){
		return taskRange;
	}
	
	public int getTaskSkillRange(){
		return taskSkillRange;
	}
	
	public int getReqRange(){
		return reqRange;
	}
	
	public int getReqWpRange(){
		return reqWpRange;
	}
	
	public double getBetaFirst(){
		return betaFirst;
	}
	
	public double getBetaSecond(){
		return betaSecond;
	}
	
	public long getMilliSecondToHour(){
		return milliSecondToHour;
	}
	
	public int getDeadlineLowerBound(){
		return deadlineLowerBound;
	}
	
	public int getDeadlineUpperBound(){
		return deadlineUpperBound;
	}
	
	public double getWpTaskConstraintProb(){
		return wpTaskConstraintProb;
	}
	
	public double getWpTaskSequenceProb(){
		return wpTaskSequenceProb;
	}
	
	public int getWpRange(){
		return wpRange;
	}
	
	public int getWpDeadlineRange(){
		return wpDeadlineRange;
	}*/
}

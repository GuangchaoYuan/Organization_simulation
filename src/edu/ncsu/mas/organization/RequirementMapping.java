package edu.ncsu.mas.organization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class RequirementMapping {
	private static RequirementMapping singleton = new RequirementMapping();
	
	private HashMap<Integer, HashSet<Integer>> reMap = new HashMap<Integer, HashSet<Integer>>();//key: requirement; value: index of work process
	private ArrayList<WorkProcess> wpList = new ArrayList<WorkProcess>();
	
	//Global gb = Global.getInstance();
	
	private RequirementMapping(){
		//initializeWorkProcess();
		//reqMapWp();
		readWorkProcess();
	}
	
	public static RequirementMapping getInstance(){
		return singleton;
	}
	
	private void readWorkProcess(){
		try {
			BufferedReader reader = null;
			String line = "";
			int wpId = 0;
			int numTask = 0;
			int deadline = 0;
			String storeFile1 = "D:\\Organization_Analysis\\simulation\\wp_";
			for(int index = 0; index < 5; index++){
				String file = storeFile1 + String.valueOf(index)+ ".csv";
				reader = new BufferedReader(new FileReader(file));
				int num = 0;
				
				ArrayList<String> taskLine = new ArrayList<String>();
				while((line = reader.readLine())!=null){
					num++;
					if(num==1){
						String[] words = line.split(",");
						wpId = Integer.parseInt(words[0]);
						numTask = Integer.parseInt(words[1]);
						deadline = Integer.parseInt(words[2]);
					}
					else{
						taskLine.add(line);
					}	
				}
				WorkProcess temp = new WorkProcess(wpId, numTask, deadline, taskLine);
				wpList.add(temp);
				reader.close();
			}
			System.out.println("Finish reading the work process information");
			
			String storeFile2 = "D:\\Organization_Analysis\\simulation\\reqMap.csv";
			reader = new BufferedReader(new FileReader(storeFile2));
			int req = 0;
			
			while((line = reader.readLine())!=null){
				String[] words = line.split(",");
				req = Integer.parseInt(words[0]);
				HashSet<Integer> temp = new HashSet<Integer>();
				for(int i = 1; i<words.length;i++){
					temp.add(Integer.parseInt(words[i]));
				}
				reMap.put(req, temp);
			}
			reader.close();
			System.out.println("Finish reading the requirement mapping information");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	//generate work process
	private void initializeWorkProcess(){
		Random generator = new Random();
		int numTask = 0;
		for(int i = 0; i < Global.wpRange; i++){
			numTask = generator.nextInt(Global.taskRange) + 1;
			WorkProcess temp = new WorkProcess(i, numTask);
			//temp.printWorkProcess();//print the work process information
			wpList.add(temp);
		}
	}
	
	//generate mapping from requirements to work process
	private void reqMapWp(){
		Random generator = new Random();
		//String storeFile = "D:\\Organization_Analysis\\simulation\\reqMap.csv";
		
		//BufferedWriter writer = new BufferedWriter(new FileWriter(storeFile));
		int numReqWp = 0;
		int wpIndex = 0;
		for(int i = 0; i < Global.reqRange; i++){
			numReqWp = generator.nextInt(Global.reqWpRange)+1;
			HashSet<Integer> temp = new HashSet<Integer>();
			while(temp.size()<numReqWp){
				wpIndex = generator.nextInt(Global.wpRange);
				temp.add(wpIndex);
			}
			reMap.put(i, temp);
			
			/*ArrayList<Integer> list = new ArrayList<Integer>(temp);
			writer.append(String.valueOf(i)+",");
			writer.append(String.valueOf(list.get(0)));
			for(int j = 1; j<list.size();j++){
				writer.append("," + String.valueOf(list.get(j)));
			}
			writer.newLine();
			writer.flush();*/
		}
		//writer.close();
		//System.out.print("Finish writing the requirement mapping");
		
		
	}
	
	//get the work process set
	public ArrayList<WorkProcess> getWPSet(int req){
		ArrayList<WorkProcess> list = new ArrayList<WorkProcess>();
		HashSet<Integer> set = reMap.get(req);
		for(int index: set){
			list.add(wpList.get(index));
		}
		return list;
	}
	
	

}

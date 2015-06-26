package edu.ncsu.mas.organization;

import java.util.ArrayList;

public class TaskAgentInformationCollection {
	private static TaskAgentInformationCollection singleton = new TaskAgentInformationCollection();
	
	private ArrayList<TaskAgentInformation> tInfoList;
	
	private TaskAgentInformationCollection(){
		tInfoList = new ArrayList<TaskAgentInformation>();
	}

	public static TaskAgentInformationCollection getInstance(){
		return singleton;
	}
	
	public void addElement(TaskAgentInformation tInfo){
		tInfoList.add(tInfo);
	}
	
	public ArrayList<TaskAgentInformation> getInforList(){
		return tInfoList;
	}
	
	public void updateInforList(int index, TaskAgentInformation e){
		tInfoList.set(index, e);
	}
	
	public TaskAgentInformation getElement(String userName){
		TaskAgentInformation t = new TaskAgentInformation();
		for(int i = 0; i<tInfoList.size(); i++){
			TaskAgentInformation temp = tInfoList.get(i);
			if(temp.getUsername().equals(userName)){
				t = temp;
				break;
			}
		}
		return t;
	}
}

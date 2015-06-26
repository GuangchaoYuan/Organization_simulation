package edu.ncsu.mas.organization;

public class TaskAgentInformation {
	private String userName;
	private int state;
	private int trait;
	private int[] skillList;
	private double capability;
	private int numTaskProcess;
	
	public TaskAgentInformation(String userName, int state, int trait, int[] skillList, double capability, int numTaskProcess){
		this.userName = userName;
		this.state = state;
		this.trait = trait;
		this.skillList = skillList;
		this.capability = capability;
		this.numTaskProcess = numTaskProcess;
	}
	
	public TaskAgentInformation(){
		
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public void setTrait(int trait){
		this.trait = trait;
	}
	
	public void setSkillList(int[] skillList){
		this.skillList = skillList;
	}
	
	public void setCapability(double capability){
		this.capability = capability;
	}
	
	public void setNumTaskProcess(int numTaskProcess){
		this.numTaskProcess = numTaskProcess;
	}
	
	public int getState(){
		return this.state;
	}
	
	public int getTrait(){
		return this.trait;
	}
	
	public int[] getSkillList(){
		return this.skillList;
	}
	
	public double getCapability(){
		return this.capability;
	}
	
	public int getNumTaskProcess(){
		return this.numTaskProcess;
	}
	
	public String getUsername(){
		return this.userName;
	}
	
	public void decreaseNumTaskProcess(){
		this.numTaskProcess = numTaskProcess -1;
	}

}

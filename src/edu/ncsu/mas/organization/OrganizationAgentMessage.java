package edu.ncsu.mas.organization;

import java.io.Serializable;

public class OrganizationAgentMessage implements Serializable{
	private int state = 0;
	private int trait = 0;
	private int resource = 0;
	
	public void setState(int state){
		this.state = state;
	}
	
	public void setTrait(int trait){
		this.trait = trait;
	}
	
	public void setResource (int resource){
		this.resource = resource;
	}
	
	public int getState(){
		return this.state;
	}
	
	public int getTrait(){
		return this.trait;
	}
	
	public int getResource(){
		return this.resource;
	}


}

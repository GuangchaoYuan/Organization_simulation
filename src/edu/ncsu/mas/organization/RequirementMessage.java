package edu.ncsu.mas.organization;

import java.io.Serializable;

public class RequirementMessage implements Serializable{
	private int req = 0;
	
	public RequirementMessage(int r){
		this.req = r;
	}
	
	public int getReq(){
		return this.req;
	}

}

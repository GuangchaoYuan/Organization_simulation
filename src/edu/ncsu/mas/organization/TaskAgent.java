package edu.ncsu.mas.organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class TaskAgent extends Agent{
	private AID managerAgent;
	//Global gb = Global.getInstance();
	//private TaskAgentMessage taskMessage;
	private int state = 0; //we assume that each agent can only have one state and trait at one time
	private int trait = 0;
	private int[] skillList = new int[Global.taskAgentSkillRange]; // 1 means the agent has this skill
	private double capability = 0; // a higher number means stronger capability
	private int numTaskProcess = 0; //number of tasks for the agent in the queue
	private int[][] stMatrix = new int[Global.taskTypeRange][Global.taskAgentStateRange];//state transition matrix
	private int tCounter = 0;
	
	private ArrayList<Double> teamTrust = new ArrayList<Double>();// a trust is a value between 0 and 1
	private String userName = "0"; //username of the agent
	
	ArrayList<String> assignIdList = new ArrayList<String>();
	ArrayList<Integer> deadlineList = new ArrayList<Integer>();
	ArrayList<Integer> taskTypeList = new ArrayList<Integer>();
	ArrayList<Integer> lowerDLList = new ArrayList<Integer>();
	ArrayList<Integer> upperDLList = new ArrayList<Integer>();
	
	
	
	protected void setup(){
		// task agent set up
		System.out.println(getLocalName()+ "setting up");
		Object[] args = getArguments();
		if(args != null)
		{
			doDelete();
		}
		else{
			try {
				//task agent register service
				ServiceDescription sd = new ServiceDescription();
			    sd.setType("TaskAgent");
			    sd.setName("TaskAgentDescription");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				dfd.addServices(sd);
				DFService.register(this, dfd);
				
				//search for manager agent
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sdm = new ServiceDescription();
				sdm.setType("ManagerAgent");
				template.addServices(sdm);
				try{
					DFAgentDescription result[] = DFService.search(this, template); 
					System.out.println("Found the manager agents:");
					managerAgent = result[0].getName();
				}
				catch(Exception e){
					System.out.println("Saw exception in Manager Agent:" + e);
					e.printStackTrace();
				}
				
				//add task agents' behaviors
				addBehaviour(new Initialize());
				addBehaviour(new AcceptTask());
				addBehaviour(new ExecuteTask());
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			userName = getAID().getLocalName();
		}
	}
	
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Task agent: " + getAID().getName() + " terminating");
	}
	
	//initialize parameters
	private class Initialize extends Behaviour{
		int step = 0;
		double trust = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			//Random generator = new Random();
			
			//generate state and trait: [1, range]
			/*state = generator.nextInt(Global.taskAgentStateRange)+1;
			trait = generator.nextInt(Global.taskAgentTraitRange)+1;
			
			//generate skill: each agent just doesn't have one skill
			int index = generator.nextInt(Global.taskAgentSkillRange);
			for(int i = 0; i < Global.taskAgentSkillRange; i++){
				if(i!=index)
					skillList[i]=1;
				else
					skillList[i]=0;
			}
			
			//generate capability
			capability = generator.nextDouble();
			
			//generate state transition matrix
			/*for(int row = 0; row < gb.getTaskTypeRange(); row++){
				for(int col = 0; col < gb.getTaskAgentStateRange(); col++){
					stMatrix[row][col] = generator.nextInt(gb.getTaskAgentStateRange())+1;
				}
			}*/
			///GlobalTimer gb = GlobalTimer.getInstance();
			stMatrix = Global.stMatrix;
			
			//generate trust
			/*for(int j = 0; j <Global.numTaskAgent; j++ ){
				trust = generator.nextDouble();
				teamTrust.add(trust);
			}*/
			
			//add information into TaskAgentInformation
			/*TaskAgentInformation tInfo = new TaskAgentInformation(userName, state, trait, skillList, capability, numTaskProcess);
			TaskAgentInformationCollection tInfoList = TaskAgentInformationCollection.getInstance();
			tInfoList.addElement(tInfo);
			
			//send a message to the manager
			/*DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("ManagerAgent");
			template.addServices(sd);
			try{
				DFAgentDescription result[] = DFService.search(myAgent, template); 
				System.out.println("Found the manager agents:");
				managerAgent = result[0].getName();
		    }
			catch(Exception e){
				System.out.println("Saw exception in Manager Agent:" + e);
				e.printStackTrace();
			}
			
			//inform manager agent
			try{
				ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
				cfp.addReceiver(managerAgent);
				taskMessage = new TaskAgentMessage();
				taskMessage.setState(state);
				taskMessage.setTrait(trait);
				taskMessage.setSkill(skillList);
				cfp.setContentObject(taskMessage);
				myAgent.send(cfp);
			}
			catch(Exception e){
				e.printStackTrace();
			}*/
			System.out.println("Initialization of task agent: " + userName + " done");
			step = 1;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return (step == 1);
		}
		
	}
	
	//ask for role and position from organization agent
	private class AskforRole extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class UpdateTaskQueue extends Behaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class AcceptTask extends Behaviour{
		private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		String assignId = "";
		int deadline = 0;
		int taskType = 0;
		int lowerDL = 0;
		int upperDL = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage receiveAssign = myAgent.receive(mt);
			if(receiveAssign!=null){
				try{
					TaskAssignMessage tAssign = (TaskAssignMessage) receiveAssign.getContentObject();
					assignId = tAssign.getAssignId();
					deadline = tAssign.getDeadline();
					taskType = tAssign.getTaskType();
					lowerDL = tAssign.getLowerDeadline();
					upperDL = tAssign.getUpperDeadline();
					
					assignIdList.add(assignId);
					deadlineList.add(deadline);
					taskTypeList.add(taskType);
					lowerDLList.add(lowerDL);
					upperDLList.add(upperDL);
					System.out.println("Task agent: " + getAID().getName() + " has accepted assignId: " + assignId);
					
					//set information
					TaskAgentInformationCollection tInfo = TaskAgentInformationCollection.getInstance();
					TaskAgentInformation t = tInfo.getElement(userName);
					state = t.getState();
					trait = t.getTrait();
					skillList = t.getSkillList();
					capability = t.getCapability();
					numTaskProcess = 0;
					//System.out.println("Task agent: " + userName + " has finished the parameter resetting");
				}catch (UnreadableException e) {
					// TODO Auto-generated catch block
					 e.printStackTrace();
				 }
		    }
			else{
		    	block();
		    }
		
		}
		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private class ExecuteTask extends Behaviour{
		int maxIndex = 0;
		double maxPerf = 0;
		int type = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			if(assignIdList.size()>0){
				//choose a task from the task queue
				maxIndex = 0;
				type = taskTypeList.get(0);
				//System.out.println("assignId: " + assignIdList.get(0) + " type: " + type);
				maxPerf =Global.getPerformance(type, state, trait);
				for(int i = 1; i < assignIdList.size(); i++ ){
					//double perf = getPerformance(taskTypeList.get(i));
					double perf = Global.getPerformance(taskTypeList.get(i), state, trait);
					if(perf>maxPerf){
						maxPerf = perf;
						maxIndex = i;
					}
				}
				
				//choose tasks according to their order
				//maxPerf =Global.getPerformance(type, state, trait);
				
				//do the task
				int est = getEstimatedDuration(lowerDLList.get(maxIndex), upperDLList.get(maxIndex), maxPerf);
				int tStartTime = Global.counter;
				type = taskTypeList.get(maxIndex);
				int count = 0;
				
				//count the elapsed time
				while(count<est){
					//try { 
						//Thread.sleep(1000); 
						count ++; 
					//} catch (InterruptedException e) { 
						// TODO Auto-generated catch block 
					//	e.printStackTrace(); 
					//} 
				}
				
				//send message to the manager
				try{
					ACLMessage cfp = new ACLMessage(ACLMessage.CONFIRM);
					cfp.addReceiver(managerAgent);
					TaskAssignMessage taM = new TaskAssignMessage(assignIdList.get(maxIndex), tStartTime, est, maxPerf);
					cfp.setContentObject(taM);
					myAgent.send(cfp);
					
					System.out.println("Task agent: " + userName + " has done with the task: " + assignIdList.get(maxIndex));
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				//decrease the number of process
				TaskAgentInformationCollection tInfo = TaskAgentInformationCollection.getInstance();
				ArrayList<TaskAgentInformation> infoList = tInfo.getInforList();
				TaskAgentInformation ta;
				for(int j = 0; j<infoList.size(); j++){
					ta = infoList.get(j);
					if(ta.getUsername().equals(userName)){
						ta.decreaseNumTaskProcess();
						tInfo.updateInforList(j, ta);
						break;
					}
				}
				
				//update state
				//System.out.println("type: " + type + " state: " + state);
				//System.out.println(" nexState: " + stMatrix[type-1][state-1]);
				int nextState = stMatrix[type-1][state-1];
				state = nextState;
						
				//delete the task from the queue
				assignIdList.remove(maxIndex);
				deadlineList.remove(maxIndex);
				taskTypeList.remove(maxIndex);
				lowerDLList.remove(maxIndex);
				upperDLList.remove(maxIndex);
				
				
		    }
			else{
		    	block();
		    }
		}
		
		//get the performance of a task given the agents' state, trait, and the task's task type
		private double getPerformance(int taskType){
			double performance = 0;
			double s = (double) state/Global.taskAgentStateRange;
			double t = (double) trait/Global.taskAgentTraitRange;
			double y = (double) taskType/Global.taskTypeRange;
			performance = s*t*y;
			return performance;
		}
		
		//the higher the capability, the closer to the lowerDL 
		private int getEstimatedDuration(int lowerDL, int upperDL, double perf){
			int est = 0;
			int extra = upperDL - lowerDL;
			double prob = (1-capability)*Global.capabilityCoefficient + (1-perf)*Global.perfCoefficient;
			est = lowerDL + (int)(extra*prob);
			return est;
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	


}

package edu.ncsu.mas.organization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

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
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import jsc.distributions.Beta;

public class ManagerAgent extends Agent {
	protected ArrayList<AID> userList = new ArrayList<AID>();    // users
	//private int req = 0; //requirement from customers
	protected AID organization;
	protected AID customer;
	protected AID timerAgent;
	//protected ArrayList<TaskAgent> taskAgentList = new ArrayList<TaskAgent>();
	//protected ArrayList<int[]> skillList = new ArrayList<int[]>();
	protected ArrayList<WorkProcessPerformance> wpPerfList = new ArrayList<WorkProcessPerformance>();
	protected int numDone = 0;
	
	//Global gb = Global.getInstance();
	
	protected void setup(){
		// manager agent set up
		System.out.println(getLocalName()+ "setting up");
		Object[] args = getArguments();
		
		try {
			// manager agent register service
			ServiceDescription sd = new ServiceDescription();
		    sd.setType("ManagerAgent");
		    sd.setName("ManagerAgentDescription");
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			dfd.addServices(sd);
			DFService.register(this, dfd);
			
			System.out.println("Initialize agents");
			PlatformController container = getContainerController(); // get a container controller for creating new agents
			
			try {		
				//initialize task agents
				//System.out.println("before initilization of task agents: " + Global.numTaskAgent);
	        	for (int i = 0;  i < Global.numTaskAgent;  i++) {
	        		String localName = "task_";
	        		if(i>=10)
				    	localName=localName+i;
				    else
				    	localName=localName+"0"+i;
				    AgentController user = container.createNewAgent(localName, "edu.ncsu.mas.organization.TaskAgent", null);
				    user.start();
		            userList.add( new AID(localName, AID.ISLOCALNAME) );
	            }
				
				//initialize organizational agent
				String lName = "organization";
				AgentController organ = container.createNewAgent(lName, "edu.ncsu.mas.organization.OrganizationAgent", null);
				organ.start();
				organization = new AID(lName, AID.ISLOCALNAME);
				
				//initialize customer agent
				String cName = "customer";
				AgentController cust = container.createNewAgent(cName, "edu.ncsu.mas.organization.CustomerAgent", null);
				cust.start();
				customer = new AID(cName, AID.ISLOCALNAME);
				
				//initialize timer agent
				String tName = "timer";
				AgentController timer = container.createNewAgent(tName, "edu.ncsu.mas.organization.TimerAgent", null);
				timer.start();
				timerAgent = new AID(tName, AID.ISLOCALNAME);
	        }
	        catch (Exception e) {
	            System.err.println( "Exception while adding users: " + e );
	            e.printStackTrace();
	        }
			
			addBehaviour(new AnswerRequirement());
			addBehaviour(new ReceiveTaskAgentInformation());
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Manager agent: " + getAID().getName() + " terminating");
	}
	
	//manager's behavior of answering requirement from the customer
	private class AnswerRequirement extends Behaviour{
		private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
		boolean accept = false; //accept the offer or not
		boolean finish = false;
		int req = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			//receive message from customers 
			ACLMessage msg = receive(mt);
			if(msg!=null){
				String reqString = msg.getContent();
				req = Integer.valueOf(reqString);
				//accept the offer of not
				/*Beta dist = new Beta(Global.betaFirst, Global.betaSecond);
				Random generator = new Random();
				double x = generator.nextDouble();
				double y = dist.cdf(x);
				if(y>=0.5){
					accept = true;
				}
				else
					accept = false;*/
				accept = true;
				
				//inform the customer
				try{
					String replyContent = "";
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.INFORM);
					if(accept)
						replyContent = "accept";
					else
						replyContent = "refuse";
					reply.setContent(replyContent);
					myAgent.send(reply);
					
					System.out.println("Manager agent " + replyContent + " the requirement: " + req);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				//assign task
				if(accept){
					scheduleTask(req);
					finish = true;
				}
				
			}
			else{
				block();
			}

		}
		
		//schedule a work process
		public void scheduleTask(int req){
			System.out.println("Start to schedule requirement " + req);
			
			TaskAgentInformationCollection tInfo = TaskAgentInformationCollection.getInstance();
			ArrayList<TaskAgentInformation> infoList = tInfo.getInforList();
			
			//read task agent information
			try {
				String storeFile1 = "D:\\Organization_Analysis\\simulation\\agentInformation.csv";
				String line = "";
				BufferedReader reader = new BufferedReader(new FileReader(storeFile1));
				String userName = "";
				int state = 0;
				int trait = 0;
				
				double cap = 0;
				while((line = reader.readLine())!=null){
					int[] skill = new int[Global.taskAgentSkillRange];
					String[] words = line.split(",");
					if(words.length!=5){
						System.out.println("There is an error in task agent information line");
						continue;
					}
					userName = words[0];
					state = Integer.parseInt(words[1]);
					trait = Integer.parseInt(words[2]);
					
					String[] sk = words[3].split(";");
					for(int i = 0; i < sk.length; i++){
						skill[i] = Integer.parseInt(sk[i]);
					}
					
					cap = Double.parseDouble(words[4]);
					
					TaskAgentInformation e = new TaskAgentInformation(userName, state, trait, skill, cap, 0);
					tInfo.addElement(e);
				}
				reader.close();
				//System.out.println("Finish reading the task agent information");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
			
			
			//write task agent information and state transition information into file
			/*try {
				String storeFile1 = "D:\\Organization_Analysis\\simulation\\agentInformation.csv";
				
				String storeFile2 = "D:\\Organization_Analysis\\simulation\\stateTransition.csv";
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(storeFile1));
				for(int index = 0; index < infoList.size(); index ++){
					TaskAgentInformation temp = infoList.get(index);
					writer.append(temp.getUsername() + ",");
					writer.append(String.valueOf(temp.getState()) + ",");
					writer.append(String.valueOf(temp.getTrait()) + ",");
					
					int[] skill = temp.getSkillList();
					writer.append(String.valueOf(skill[0]));
					for(int i = 1; i < skill.length; i++){
						writer.append(";"+String.valueOf(skill[i]));
					}
					writer.append(",");
					writer.append(String.valueOf(temp.getCapability()));
					writer.newLine();
					writer.flush();
				}
				writer.close();
				System.out.println("Finish writing the task agent information");
				
				writer = new BufferedWriter(new FileWriter(storeFile2));
				for(int p = 0; p < Global.taskTypeRange; p++){
					for(int q = 0; q < (Global.taskAgentStateRange-1); q++){
						writer.append(String.valueOf(Global.stMatrix[p][q]) + ",");
					}
					writer.append(String.valueOf(Global.stMatrix[p][Global.taskAgentStateRange-1]));
					writer.newLine();
					writer.flush();
				}
				writer.close();
				System.out.println("Finish writing the state transition information");
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			
			RequirementMapping rm = RequirementMapping.getInstance();
			ArrayList<WorkProcess> list = rm.getWPSet(req);
			
			//System.out.println("There are " + list.size() + " work process for requirement " + req);
			//each work process
			for(int i = 0; i< list.size(); i++){			
				WorkProcess current = list.get(i);
				int wpId = current.getWorkProcessId();
				System.out.println("Start to schedule work process: " + wpId);
				ArrayList<Task> taskList = current.getAllTasks();
				//System.out.println("wpId: " + wpId + " task list size: " + taskList.size());
				int startTime = Global.counter;
				WorkProcessPerformance wPerf = new WorkProcessPerformance(wpId, req, startTime, current.getDeadline());
				//one work process: each task
				for(int j = 0; j < taskList.size(); j++){
					Task t = taskList.get(j);
					int taskId = t.getTaskId();
					int reSkill = t.getRequiredSkill();
					int deadline = t.getDeadline();
					int taskType = t.getType();
					int lowerDL = t.getLowerDuration();
					int upperDL = t.getUpperDuration();
					
					int minProcess = 0;
					String minAgentName = "";
					int minIndex = 0;
					int num = 1;
					TaskAgentInformation taInfo;
					//one task: each task agent
					for(int m = 0; m < infoList.size(); m++){
						taInfo = infoList.get(m);
						int[] skillList = taInfo.getSkillList();
						
						//if the agent has the required skill
						if(skillList[reSkill]==1){
							if(wpId==4)
								System.out.print("mapping!!! ");
							if(num==1){
								minProcess = taInfo.getNumTaskProcess();
								minAgentName = taInfo.getUsername();
								minIndex = m;
							}
							else{
								if(taInfo.getNumTaskProcess()<minProcess){
									minProcess = taInfo.getNumTaskProcess();
									minAgentName = taInfo.getUsername();
									minIndex = m;
								}
							}
							num++;	
						}
					}
					
					//increase the number of processing tasks for the responsible agent
					if(!minAgentName.isEmpty()){
						taInfo = infoList.get(minIndex);
						taInfo.setNumTaskProcess(minProcess+1);
						tInfo.updateInforList(minIndex, taInfo);
						
						wPerf.addElement(taskId, minAgentName);
						
						//send a message to the agent
						ACLMessage taskAssignM = new ACLMessage(ACLMessage.INFORM);
						taskAssignM.addReceiver(new AID(minAgentName,AID.ISLOCALNAME));
						
						TaskAssignMessage ta = new TaskAssignMessage(wpId, taskId, deadline, taskType, lowerDL, upperDL);
						try {
							taskAssignM.setContentObject(ta);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						myAgent.send(taskAssignM);
						System.out.println("Manager has assigned wpId: " + wpId + " taskId: " + taskId + " to task agent: " + minAgentName);
					}
					else{//no agent has the required skill
						System.out.println("No task agents have the required skill for wpId: " + wpId + " task: " + taskId);
					}
					
				}
				wpPerfList.add(wPerf);
			}
			
		}
		

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return finish;
		}
		
	}
	
	//Manager's behavior of receiving message from task agents
	private class ReceiveTaskAgentInformation extends Behaviour{
		private MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
		int wpId = 0;
		int taskId = 0;
		int tStartTime = 0;
		int tEndTime = 0;
		double perf = 0;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage msg = receive(mt);
			if(msg!=null){
				try{
					TaskAssignMessage tConfirm = (TaskAssignMessage) msg.getContentObject();
					wpId = tConfirm.getWorkProcessId();
					taskId = tConfirm.getTaskId();
					tStartTime = tConfirm.getStartTime();
					tEndTime = tConfirm.getEndTime();
					perf = tConfirm.getPerformance();
					
					
					for(int i  = 0; i < wpPerfList.size(); i++){
						WorkProcessPerformance wp = wpPerfList.get(i);
						if(wp.getWorkProcessId() == wpId){
							if(wp.updateWorkProcess(taskId, tStartTime, tEndTime, perf)){
								numDone++;
								System.out.println("wpId: " + wpId + " performance: " + wp.getWorkProcessPerformance() 
										+" efficiency: " + wp.getWorkProcessEfficiency());
							}
							
							break;
						}
					}
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
			//return (numDone == wpPerfList.size());
			return false;
		}
		
		
	}

}

package edu.ncsu.mas.organization;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CustomerAgent extends Agent{
	private AID managerAgent;
	private int req = 0;
	private RequirementMessage reMessage;
	
	//Global gb = Global.getInstance();
	
	protected void setup(){
		// customer agent set up
		System.out.println(getLocalName()+ "setting up");
		Object[] args = getArguments();
		if(args != null)
		{
			doDelete();
		}
		else{
			try {
				// customer agent register service
				ServiceDescription sd = new ServiceDescription();
			    sd.setType("CustomerAgent");
			    sd.setName("CustomerAgentDescription");
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
				
				//add proposing requirement behavior
				addBehaviour(new ProposeRequirement());
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//customer's behavior of proposing requirement
	private class ProposeRequirement extends Behaviour{
		int step = 0;
		private MessageTemplate mt;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch(step){
			case 0:
				//generate requirement
				Random generator = new Random();
				//req = generator.nextInt(Global.reqRange);
				req = 0;
				
				//propose a requirement to manager agent
				try{
					ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
					cfp.addReceiver(managerAgent);
					String reqString = String.valueOf(req);
					cfp.setContent(reqString);
					myAgent.send(cfp);
					System.out.println(getLocalName()+" propose requirement: " + req);
					// Prepare the template to get proposals
					mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				step = 1;
				break;
			case 1: //receive the decision from the manager
				ACLMessage receiveMsg = myAgent.receive(mt);
				String decision = "";
				if(receiveMsg!=null){
					decision = receiveMsg.getContent();
					if(decision.equals("accept")){
						step = 2;
						System.out.println(getLocalName()+" the requirement: " + req + " has been accepted");
					}
					else if(decision.equals("refuse")){
						step = 0;
						System.out.println(getLocalName()+" the requirement: " + req + " has been rejected");
					}
				}
				else{
					block();
				}
				break;		
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if (step == 2) {
				System.out.println("proposing requirement done");
			}
			return (step == 2);
		}
		
	}
	
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Customer-agent "+getAID().getName()+ " terminating."); 

	}

}

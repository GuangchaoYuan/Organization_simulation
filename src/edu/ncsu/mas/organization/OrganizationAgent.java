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

public class OrganizationAgent extends Agent {
	//attributes: states, traits, constraints
	private AID managerAgent;
	private OrganizationAgentMessage organizationMessage;
	private int state = 0;
	private int trait = 0;
	
	//Global gb = Global.getInstance();
	protected void setup(){
		// organization agent set up
		System.out.println(getLocalName()+ "setting up");
		Object[] args = getArguments();
		if(args != null)
		{
			doDelete();
		}
		else{
			try {
				// organization agent register service
				ServiceDescription sd = new ServiceDescription();
			    sd.setType("OrganizationAgent");
			    sd.setName("OrganizationAgentDescription");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				dfd.addServices(sd);
				DFService.register(this, dfd);
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void takeDown(){
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		System.out.println("Organization agent: " + getAID().getName() + " terminating");
	}
	
	//initialize parameters
	private class Initialize extends Behaviour{
		int step = 0;
		private MessageTemplate mt;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			switch(step){
			case 0:
				Random generator = new Random();
				state = generator.nextInt(Global.organStateRange);
				trait = generator.nextInt(Global.organTraitRange);
				step = 1;
				break;
			case 1:
				DFAgentDescription template = new DFAgentDescription();
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
					organizationMessage = new OrganizationAgentMessage();
					organizationMessage.setState(state);
					organizationMessage.setTrait(trait);
					organizationMessage.setResource(Global.numResource);
					cfp.setContentObject(organizationMessage);
					myAgent.send(cfp);
					
					// Prepare the template to get proposals
					mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				step = 2;
				break;
			
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if (step == 2) {
				System.out.println("Initialize method done");
			}
			return (step == 2);
		}
		
	}

}

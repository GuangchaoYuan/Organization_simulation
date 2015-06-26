package edu.ncsu.mas.organization;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class TimerAgent extends Agent{
	
	Global gt = Global.getInstance();
	
	
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
				//timer agent register service
				ServiceDescription sd = new ServiceDescription();
			    sd.setType("TimerAgent");
			    sd.setName("TimerAgentDescription");
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				dfd.addServices(sd);
				DFService.register(this, dfd);
				
				
				//add proposing requirement behavior
				addBehaviour(new Timer());
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class Timer extends CyclicBehaviour{
		//GlobalTimer gb = Global.getInstance();
		@Override
		public void action() {
			// TODO Auto-generated method stub
		    gt.increaseCounter();
			//gb.increaseCounter();
		}
	
	}
}

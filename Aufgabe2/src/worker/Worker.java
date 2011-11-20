package worker;

import static akka.actor.Actors.remote;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import javax.swing.JOptionPane;

public class Worker extends UntypedActor {
	private static int idGenerator = 0;
	private int Rho = 0;
	private long CPU = 0;
	private int actorId;
	private Calculate calculate;
	private ActorRef master;
	

	public Worker() {
		// Important: If the ID is not set, it will always use the same
		// instance of the actuator for remote calls from a client!
		getContext().setId(idGenerator + "");
		actorId = idGenerator;
		idGenerator++;
		System.out.println("Worker " + this.actorId + " has been started");
	}

	
	// message handler
	public void onReceive(Object message) {		
		if (message instanceof CalculateMessage) {
			//Create a thread to find a prime factor of N
			this.master = getContext().getSender().get();			
			CalculateMessage calculateMessage = (CalculateMessage) message;
			calculate = new Calculate(calculateMessage.getN(),calculateMessage.getCalculID(),master);
			Thread thread = new Thread(calculate);
			thread.start();
		} else if (message instanceof GetStatsMessage){
			//Return the calculation stats
			this.master = getContext().getSender().get();
			GetStatsMessage stats = new GetStatsMessage(this.CPU,this.Rho);
			master.tell(stats,getContext());
			this.CPU = 0l;
			this.Rho = 0;		
		} else if (message instanceof TestConnectionMessage) {
			//Acknowledge the connection try from the master without throwing an exception
		} else if (message instanceof TerminateMessage){
			//Send a stop request to the calculation thread if it is running and get the computation stats back
			if (calculate != null){
				calculate.requestStop();
				this.CPU+=calculate.getCPU();
				this.Rho+=calculate.getRho();
			}			
		} else {
			throw new IllegalArgumentException("Unknown message [" + message
					+ "]");
		}
	}

	@Override
	/**
	 * Stop the worker
	 */
	public void postStop() {
		System.out.println("Worker " + this.actorId + " has been terminated");
		super.postStop();
	}

	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(JOptionPane.showInputDialog(null,
				"Choose Port :", "Launcher StartUp",
				JOptionPane.QUESTION_MESSAGE));
		remote().start("localhost", port);

	}
}
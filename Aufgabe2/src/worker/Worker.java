package worker;

import static akka.actor.Actors.remote;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import java.security.SecureRandom;
//import static akka.actor.Actors.poisonPill;
import java.math.BigInteger;


import javax.swing.JOptionPane;

public class Worker extends UntypedActor {
	private static int idGenerator = 0;
	private int actorId;

	public Worker() {
		//Important: If the ID is not set, it will always use the same
		// instance of the actuator for remote calls from a client!
		getContext().setId(idGenerator + "");
		actorId = idGenerator;
		idGenerator++;
		System.out.println("Actor is now running");
	}

	private ActorRef master;

	// message handler
	public void onReceive(Object message) {
		if (message instanceof CalculateMessage) {
			// The sender is determined at the first call
			this.master = getContext().getSender().get();
			CalculateMessage calculateMessage = (CalculateMessage) message;
			BigInteger result = calculate(calculateMessage.getN());
			ResultMessage resultMessage = new ResultMessage(result);
			// Send the result to the master
			master.tell(resultMessage);
			// Through this.getContext().tell([Nachricht]) the actor can
			// send a message to itself. In this case it will be a 
			// poisonPill. When the actor receive this poisonPill it
			// terminates and postStop() is called
		}
		else {
			throw new IllegalArgumentException("Unknown message [" + message
					+ "]");
		}
	}
	
	/**
	 * Takes 2 BigIntegers and return the biggest common divisor.
	 * @param d : BigInteger
	 * @param e : BigInteger
	 * @return BigInteger
	 */

	private BigInteger calculate(BigInteger N){
		SecureRandom rand = new SecureRandom();
		BigInteger a = new BigInteger( N.bitLength(), rand);
			
		BigInteger x;
		do {
			x = new BigInteger( N.bitLength(), rand);
		} while (x.compareTo(N) >= 0);

		BigInteger y = x;
		BigInteger p = BigInteger.ONE;
		BigInteger d;
		
		do{
			x=((x.pow(2)).add(a)).mod(N);
			y=((y.pow(2)).add(a)).mod(N);
			y=((y.pow(2)).add(a)).mod(N);
			d=(y.subtract(x)).mod(N);
			p=d.gcd(N);
		}while(p.compareTo(BigInteger.ONE) == 0);
		
		if(!p.isProbablePrime(20)){
			p=calculate(p);
		}
			
		return p;
}


	@Override
	/**
	 * Stop the worker
	 */
	public void postStop() {
		System.out.println("Aktor wurde beendet: " + this.actorId);
		super.postStop();
	}
	
	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(JOptionPane.showInputDialog(null, "Choose Port :", "Launcher StartUp", JOptionPane.QUESTION_MESSAGE));
		remote().start("localhost", port);
		
	}
}
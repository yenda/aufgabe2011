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
	private int Rho = 0;
	private int actorId;
	private boolean found = false;
	

	public Worker() {
		// Important: If the ID is not set, it will always use the same
		// instance of the actuator for remote calls from a client!
		getContext().setId(idGenerator + "");
		actorId = idGenerator;
		idGenerator++;		
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
		} else if (message instanceof GetStatsMessage){
			
		} else if (message instanceof TestConnectionMessage) {
		} else {
			throw new IllegalArgumentException("Unknown message [" + message
					+ "]");
		}
	}

	/**
	 * Takes 2 BigIntegers and return the biggest common divisor.
	 * 
	 * @param d
	 *            : BigInteger
	 * @param e
	 *            : BigInteger
	 * @return BigInteger
	 */

	private BigInteger calculate(BigInteger N) {
		// if 2 is a factor of N it's immediately returned
		if(N.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0) return new BigInteger("2");
			
		SecureRandom rand;
		BigInteger a;
		BigInteger x;
		BigInteger y;
		BigInteger d;
		BigInteger p = N;
		do {
			rand = new SecureRandom();
			a = new BigInteger(N.bitLength(), rand);
			do {
				x = new BigInteger(N.bitLength(), rand);
			} while (x.compareTo(N) >= 0);

			y = x;

			do {
				Rho++;
				x = ((x.pow(2)).add(a)).mod(N);
				y = ((y.pow(2)).add(a)).mod(N);
				y = ((y.pow(2)).add(a)).mod(N);
				d = (y.subtract(x)).mod(N);
				p = d.gcd(N);
			} while (p.compareTo(BigInteger.ONE) == 0);
		} while (!p.isProbablePrime(20));
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
		int port = Integer.parseInt(JOptionPane.showInputDialog(null,
				"Choose Port :", "Launcher StartUp",
				JOptionPane.QUESTION_MESSAGE));
		remote().start("localhost", port);

	}
}
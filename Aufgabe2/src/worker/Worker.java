package worker;

import static akka.actor.Actors.remote;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import java.math.BigInteger;
import java.util.Random;

public class Worker extends UntypedActor {
	private static int idGenerator = 0;
	private int actorId;

	public Worker() {
		//Important: If the ID is not set, it will always use the same
		// instance of the actuator for remote calls from a client!
		getContext().setId(idGenerator + "");
		actorId = idGenerator;
		System.out.println("Aktor wurde erstellt: " + idGenerator);
		idGenerator++;
	}

	private ActorRef master;
	
	/**
	 * Takes 2 BigIntegers and return the biggest common divisor.
	 * @param d : BigInteger
	 * @param e : BigInteger
	 * @return BigInteger
	 */
	private BigInteger ggt(BigInteger d, BigInteger e ){
		if(d.compareTo(BigInteger.ZERO) != 0){
			while(e.compareTo(BigInteger.ZERO) != 0){
				if(d.compareTo(e) > 0)
					d=d.subtract(e);
				else
					e=e.subtract(d);
			}
			return d;
		}else{
			return e;
		}
	}
	
	private BigInteger calc(BigInteger N, BigInteger a){
		Random rand = new Random();
			
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
			d=d.abs();
			p=ggt(d,N);
		}while(p.compareTo(BigInteger.ONE) == 0);
			
		return p;
}

	private BigInteger calculate(BigInteger N) {
		return N;
	}

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
		System.out.println("Aktor wurde beendet: " + this.actorId);
		super.postStop();
	}

	public static void main(String[] args) throws Exception {
		remote().start("localhost", 2552);
	}
}
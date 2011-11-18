package launcher;

import java.io.Serializable;

import akka.actor.ActorRef;

public class AddWorkerMessage implements Serializable {
	private static final long serialVersionUID = -6137301410742220321L;
	private int port;
	private String address;
	private ActorRef workerRef;
	
	public AddWorkerMessage(String address, int port){
		this.port = port;
		this.address = address;
		this.workerRef = null;
	}
	
	public AddWorkerMessage(ActorRef workerRef, String address, int port){
		this.port = port;
		this.address = address;
		this.workerRef = workerRef;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * @return the workerRef
	 */
	public ActorRef getWorkerRef() {
		return workerRef;
	}
}
package master;

import akka.actor.ActorRef;

public class WorkerData {
	private ActorRef workerRef;
	private String workerAddress;
	private int workerPort;
	
	
	public WorkerData(ActorRef workerRef, String workerAdresse, int workerPort) {
		super();
		this.workerRef = workerRef;
		this.workerAddress = workerAdresse;
		this.workerPort = workerPort;
	}
	
	/**
	 * @return the workerRef
	 */
	public ActorRef getWorkerRef() {
		return workerRef;
	}

	/**
	 * @return the workerAdresse
	 */
	public String getWorkerAddress() {
		return workerAddress;
	}
	/**
	 * @return the workerPort
	 */
	public int getWorkerPort() {
		return workerPort;
	}
}

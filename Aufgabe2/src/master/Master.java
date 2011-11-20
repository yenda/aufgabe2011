package master;

import java.awt.EventQueue;
import static akka.actor.Actors.poisonPill;
import static akka.actor.Actors.remote;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.remoteinterface.RemoteServerModule;
import worker.*;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Master extends UntypedActor {
	static RemoteServerModule remoteSupport;
	private static ArrayList<WorkerData> workerList = new ArrayList<WorkerData>();
	private static ArrayList<BigInteger> factorList = new ArrayList<BigInteger>();
	private static BigInteger N;
	private static int statsMessage;
	private static int calculID;
	private static Long time;
	private static Long CPUTime;
	private static Integer cycles;
	private static MasterGUI masterGUI;
	private static ActorRef master;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ResultMessage) {
			//Receive a result from a worker
			ResultMessage resultMessage = (ResultMessage) message;
			treatResultMessage(resultMessage.getCalculID(),resultMessage.getResult());
		} else if (message instanceof GetStatsMessage){
			//Receive the stats for the workers computations
			GetStatsMessage stats = ((GetStatsMessage) message);
			Master.treatStatsMessage(stats.getCPUTime(),stats.getCycles());			
		} else {
			throw new IllegalArgumentException("Unknown message [" + message
					+ "]");
		}
	}
	
	/**
	 * Treat the stats received, is synchronized because they can arrive simultaneously and create errors
	 * @param CPUTime
	 * @param cycles
	 */
	private synchronized static void treatStatsMessage(long CPUTime, int cycles){
		Master.CPUTime+=CPUTime;
		Master.cycles+=cycles;
		Master.statsMessage++;
		Master.checkConnectionsWorkers();
		if (statsMessage == Master.workerList.size()){
			Master.time = System.currentTimeMillis() - Master.time;
			masterGUI.changeStateButton(true);
			masterGUI.displayResults(Master.factorList, Master.CPUTime, Master.time, Master.cycles);				
		}
	}
	
	/**
	 * Treat the results received, is synchronized because they can arrive simultaneously and create errors
	 * Only the first result is used, the others are ignored
	 */
	private synchronized static void treatResultMessage(int calculID, BigInteger result){
		//Check if a result hasn't been received yet for this calculation
		if (calculID==Master.calculID){
			Master.calculID++;
			factorList.add(result);
			if ((Master.N.compareTo(result) != 0)) {
				Master.N = Master.N.divide(result);					
				CalculateMessage calculate = new CalculateMessage(Master.N,Master.calculID);
				Master.checkConnectionsWorkers();
				for (WorkerData worker : workerList) {
					worker.getWorkerRef().tell(new TerminateMessage());
					worker.getWorkerRef().tell(calculate, master);
				}
			} else {
				//When the calculation is finished, ask for the worker's stats
				GetStatsMessage stats = new GetStatsMessage();
				for (WorkerData worker : workerList) {
					worker.getWorkerRef().tell(stats, master);
				}				
			}
		}
	}

	/**
	 * Add a worker to the list of worker
	 * @param address
	 * @param port
	 */
	public static void addWorker(String address, int port) {
		ActorRef worker = remote().actorFor(Worker.class.getName(), address,
				port);
		//Check if the connection is working
		try {
			TestConnectionMessage testConnexion = new TestConnectionMessage();
			worker.tell(testConnexion, master);
			workerList.add(new WorkerData(worker, address, port));
			masterGUI.refreshTextPaneWorkerList(workerList);
		} catch (Exception e) {
			//Shut down the connection
			remote().shutdownClientConnection(new InetSocketAddress(address, port));
			JOptionPane.showMessageDialog(null,
					"Connexion to the worker failed", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Remove a worker from the list used by the master for the calculation
	 * @param ID
	 */
	public static void removeWorker(int ID) {
		try {
			// Terminates the worker
			workerList.get(ID).getWorkerRef().tell(new TerminateMessage());
			workerList.get(ID).getWorkerRef().tell(poisonPill());
			// Removes it from the list of workers
			workerList.remove(ID);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"The ID doesn't match any Worker", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		Master.checkConnectionsWorkers();
	}

	/**
	 * Initiate the calculation of the prime factor of N
	 * @param N
	 */
	public static void calculateMessage(BigInteger N) {
		// Initialization of the variables
		masterGUI.changeStateButton(false);
		Master.time = System.currentTimeMillis();
		Master.CPUTime = 0l;
		Master.cycles = 0;
		Master.statsMessage = 0;
		Master.calculID = 1;
		Master.N = N;
		factorList = new ArrayList<BigInteger>();
		// if N = 1 result is returned immediately
		if (Master.N.compareTo(BigInteger.ONE) == 0) {
			ResultMessage result = new ResultMessage(N,calculID);
			master.tell(result, master);
		} else {
			CalculateMessage calculate = new CalculateMessage(Master.N,calculID);
			Master.checkConnectionsWorkers();
			for (WorkerData worker : workerList) {
				worker.getWorkerRef().tell(calculate, master);
			}
		}
	}
	
	/**
	 * Stop all the current calculations on the workers
	 */
	public static void terminate() {
		TerminateMessage terminate = new TerminateMessage();
		for (WorkerData worker : workerList) {
			worker.getWorkerRef().tell(terminate, master);
		}
		Master.factorList = new ArrayList<BigInteger>();
		masterGUI.cleanResults();		
		masterGUI.changeStateButton(true);
	}
	
	/**
	 * Check the connection off all the worker and delete the dead ones from the workersList
	 */
	private static void checkConnectionsWorkers() {
		for (WorkerData worker : workerList) {
			try {
				TestConnectionMessage testConnexion = new TestConnectionMessage();
				worker.getWorkerRef().tell(testConnexion, master);				
			} catch (Exception e) {
				//Shut down the connection
				remote().shutdownClientConnection(new InetSocketAddress(worker.getWorkerAddress(), worker.getWorkerPort()));
			}
		}
		masterGUI.refreshTextPaneWorkerList(workerList);
	}

	public static void main(String[] args) {
		// The Client must also be started as a remote actuator
		// to be able to receive messages from the worker later
		remoteSupport = remote().start("localhost", 2552);
		master = remote().actorFor(Master.class.getName(), "localhost", 2552);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					masterGUI = new MasterGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

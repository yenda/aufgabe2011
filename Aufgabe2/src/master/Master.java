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
	private static MasterGUI masterGUI;
	private static ActorRef master;

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ResultMessage) {
			// stop all the workers
			BigInteger result = ((ResultMessage) message).getResult();
			factorList.add(result);
			if ((Master.N.compareTo(result) != 0)) {
				Master.N = Master.N.divide(result);
				Master.calculateMessage(Master.N);
			} else {
				masterGUI.changeStateButton(true);
				masterGUI.displayResults(factorList, 0l, 0l, 0);
				factorList = new ArrayList<BigInteger>();
			}
		} else {
			throw new IllegalArgumentException("Unknown message [" + message
					+ "]");
		}
	}

	public static void addWorker(String address, int port) {
		ActorRef worker = remote().actorFor(Worker.class.getName(), address,
				port);
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

	public static void removeWorker(int ID) {
		try {
			// Terminates the worker
			workerList.get(ID).getWorkerRef().tell(poisonPill());
			// Removes it from the list of workers
			workerList.remove(ID);
			masterGUI.refreshTextPaneWorkerList(workerList);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"The ID doesn't match any Worker", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void calculateMessage(BigInteger N) {
		masterGUI.changeStateButton(false);
		Master.N = N;
		if (Master.N.compareTo(BigInteger.ONE) == 0) {
			ResultMessage result = new ResultMessage(N);
			master.tell(result, master);
		} else {
			CalculateMessage calculate = new CalculateMessage(Master.N);
			for (WorkerData worker : workerList) {
				worker.getWorkerRef().tell(calculate, master);
			}
		}
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

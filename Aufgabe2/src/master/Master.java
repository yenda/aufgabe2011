package master;

import java.awt.EventQueue;
import static akka.actor.Actors.poisonPill;
import static akka.actor.Actors.remote;
import akka.actor.ActorRef; import akka.actor.UntypedActor;
import akka.remoteinterface.RemoteServerModule;
import worker.*;
import launcher.*;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Master extends UntypedActor {
	static RemoteServerModule remoteSupport;
	private static ArrayList<WorkerData> workerList = new ArrayList<WorkerData>();
	private static MasterGUI masterGUI;
	private static ActorRef master;
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof CalculateMessage) {
			CalculateMessage calculate = (CalculateMessage) message;
			for(WorkerData worker : workerList){
				worker.getWorkerRef().tell(calculate, master);
			}
		} 
		else if (message instanceof ResultMessage) {
			System.out.println(((ResultMessage) message).getResult());
		} 
		else if (message instanceof AddWorkerMessage) {
			AddWorkerMessage addWorkerMessage = (AddWorkerMessage) message;
			workerList.add(new WorkerData(addWorkerMessage.getWorkerRef(), addWorkerMessage.getAddress(), addWorkerMessage.getPort()));
			masterGUI.refreshTextPaneWorkerList(workerList);
		}
		else {
			throw new IllegalArgumentException("Unknown message [" + message + "]");
		}
	}
	
	public static void addWorker (String address, int port){
		ActorRef launcher = remote().actorFor(Launcher.class.getName(),address, 2552);
		AddWorkerMessage addWorker = new AddWorkerMessage(address,port);
		launcher.tell(addWorker, master);
	}
	
	public static void removeWorker (int ID){
		try{
			//Terminates the worker
			workerList.get(ID).getWorkerRef().tell(poisonPill());
			//Removes it from the list of workers
			workerList.remove(ID);
			masterGUI.refreshTextPaneWorkerList(workerList);
		} 
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "The ID doesn't match any Worker", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void calculateMessage (BigInteger N){
		CalculateMessage calculate = new CalculateMessage(N);
		for(WorkerData worker : workerList){
			worker.getWorkerRef().tell(calculate);
		}
		
	}
	public static void main(String[] args) {
		// The Client must also be started as a remote actuator 
		// to be able to receive messages from the worker later
		remoteSupport = remote().start("localhost", 2553); 
		master = remote().actorFor(Master.class.getName(),"localhost", 2553);
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

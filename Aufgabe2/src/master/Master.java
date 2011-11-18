package master;

import java.awt.EventQueue;
import static akka.actor.Actors.poisonPill;
import static akka.actor.Actors.remote;
import akka.actor.ActorRef; 
import akka.actor.UntypedActor;
import akka.remoteinterface.RemoteServerModule;
import worker.*;

import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Master extends UntypedActor {
	static RemoteServerModule remoteSupport;
	private static ArrayList<WorkerData> workerList = new ArrayList<WorkerData>();
	private static ArrayList<BigInteger> factorList = new ArrayList<BigInteger>();
	private static BigInteger N;
	private static BigInteger current;
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
			//stop all the workers
			BigInteger result = ((ResultMessage) message).getResult();
			factorList.add(result);
			Master.current = Master.current.divide(result);
			if (Master.current.isProbablePrime(20)){
				//solution found	
			}
			CalculateMessage calculate = new CalculateMessage(Master.current);
			for(WorkerData worker : workerList){
				worker.getWorkerRef().tell(calculate,master);
			}
			
		} 
		else {
			throw new IllegalArgumentException("Unknown message [" + message + "]");
		}
	}
	
	public static void addWorker (String address, int port){
		ActorRef worker = remote().actorFor(Worker.class.getName(),address, port);
		workerList.add(new WorkerData(worker, address, port));
		masterGUI.refreshTextPaneWorkerList(workerList);
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
		Master.N = N;
		Master.current = N;
		CalculateMessage calculate = new CalculateMessage(N);
		for(WorkerData worker : workerList){
			worker.getWorkerRef().tell(calculate,master);
		}
		
		
	}
	public static void main(String[] args) {
		// The Client must also be started as a remote actuator 
		// to be able to receive messages from the worker later
		remoteSupport = remote().start("localhost", 2552); 
		master = remote().actorFor(Master.class.getName(),"localhost", 2552);
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

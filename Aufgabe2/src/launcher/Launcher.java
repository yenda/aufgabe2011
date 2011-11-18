package launcher;

import static akka.actor.Actors.remote;
import akka.actor.ActorRef; import akka.actor.UntypedActor;
import akka.remoteinterface.RemoteServerModule;
import worker.*;

public class Launcher extends UntypedActor {
	static RemoteServerModule remoteSupport;
	ActorRef master;
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof AddWorkerMessage) {
			this.master = getContext().getSender().get();
			// Create workers on the local host
			AddWorkerMessage addWorkerMessage = (AddWorkerMessage) message;
			ActorRef worker = remote().actorFor(Worker.class.getName(),"localhost", addWorkerMessage.getPort());
			AddWorkerMessage addWorker = new AddWorkerMessage(worker,addWorkerMessage.getAddress(),addWorkerMessage.getPort());
			// send back the WorkerRef
			master.tell(addWorker);
		}
		else {
			throw new IllegalArgumentException("Unknown message [" + message + "]");
		}
	}
	
	public static void main(String[] args) {
		// The Launcher must also be started as a Remote-Actor 
		// to be able to receive messages from the master
		remoteSupport = remote().start("localhost", 2553); 
		remote().actorFor(Launcher.class.getName(),"localhost", 2552);
	}
}

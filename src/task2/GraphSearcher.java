package task2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Will show that implementations of GraphSearcher are remote objects to be
 * accessed by RMI
 * 
 * @author Peter Swantek
 *
 */
public interface GraphSearcher extends Remote {

    Map<SerializableNode, Map<SerializableNode, Integer>> searchBenchmark(int howMany, SerializableNode[] nodes) throws RemoteException;

}

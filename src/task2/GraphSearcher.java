package task2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface GraphSearcher extends Remote {

    Map<SerializableNode, Map<SerializableNode, Integer>> searchBenchmark(int howMany, SerializableNode[] nodes) throws RemoteException;

}

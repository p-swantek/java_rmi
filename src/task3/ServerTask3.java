package task3;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Peter Swantek
 *
 */
public class ServerTask3 extends UnicastRemoteObject implements GetSearcher {

    private static final long serialVersionUID = 1L;

    public ServerTask3() throws RemoteException {
        super();
    }

    @Override
    public GraphSearcher getSearcher() throws RemoteException {
        return new GraphSearcherImpl();
    }

    public static void main(String[] args) {

        if (System.getSecurityManager() == null) {

            System.setSecurityManager(new SecurityManager());
        }

        String name = "GetSearcher";
        System.out.println("Server started, attempting to create registry and bind the GetSearcher...");
        try {
            GetSearcher searcher = new ServerTask3();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind(name, searcher);
            System.out.println("GetSearcher sucessfully bound!");
        }

        catch (RemoteException e) {
            System.out.println("Got a RemoteException");
            e.printStackTrace();
        }

    }

}

class GraphSearcherImpl implements GraphSearcher {

    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final Searcher searcher = new SearcherImpl();

    @Override
    public Map<Node, Map<Node, Integer>> searchBenchmark(int howMany, Node[] nodes) {
        // Display measurement header.
        System.out.printf("%7s %8s %13s %13s\n", "Attempt", "Distance", "Time", "TTime");

        //map of nodes to a mapping of nodes that are connected to the given node as well as the distances for those pairs
        Map<Node, Map<Node, Integer>> result = new HashMap<>();

        for (int i = 0; i < howMany; i++) {

            //Map<Node, Integer> map = new HashMap<>();

            // Select two random nodes.
            final int idxFrom = random.nextInt(nodes.length);
            final int idxTo = random.nextInt(nodes.length);

            // Calculate distance, measure operation time
            final long startTimeNs = System.nanoTime();
            final int distance = searcher.getDistance(nodes[idxFrom], nodes[idxTo]);
            final long durationNs = System.nanoTime() - startTimeNs;

            // Calculate transitive distance, measure operation time
            final long startTimeTransitiveNs = System.nanoTime();
            final int transitiveDistance = searcher.getTransitiveDistance(4, nodes[idxFrom], nodes[idxTo]);
            final long transitiveDurationNs = System.nanoTime() - startTimeTransitiveNs;

            if (distance != transitiveDistance) {
                System.out.printf("Standard and transitive algorithms inconsistent (%d != %d)\n", distance, transitiveDistance);
            }

            else {
                // Print the measurement result.
                System.out.printf("%7d %8d %13d %13d\n", i, distance, durationNs / 1000, transitiveDurationNs / 1000);
                if (result.containsKey(nodes[idxFrom])) {
                    Map<Node, Integer> currMap = result.get(nodes[idxFrom]);
                    currMap.put(nodes[idxTo], distance);
                    result.put(nodes[idxFrom], currMap);
                }

                else {
                    Map<Node, Integer> map = new HashMap<>();
                    map.put(nodes[idxTo], distance);
                    result.put(nodes[idxFrom], map);
                }

            }
        }

        return result;
    }

}

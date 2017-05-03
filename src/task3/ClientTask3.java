package task3;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Random;

/**
 * Client that will use RMI to obtain a remote reference to an object that will
 * then send over a GraphSearcher object. The client will then use this
 * GraphSearcher object to run the search benchmark method locally.
 * 
 * @author Peter Swantek
 *
 */
public class ClientTask3 {

    // How many nodes and how many edges to create.
    private static final int GRAPH_NODES = 10;
    private static final int GRAPH_EDGES = 40;
    private static final int NUM_SEARCHES = 50;
    private static final Random random = new Random();

    private static Node[] nodes; //graph, the nodes are not serializable

    /**
     * Creates nodes of a graph.
     * 
     * @param howMany
     */
    private static void createNodes(int howMany) {
        nodes = new Node[howMany];

        for (int i = 0; i < howMany; i++) {
            nodes[i] = new NodeImpl(i);
        }
    }

    /**
     * Creates a fully connected graph.
     */
    @SuppressWarnings("unused")
    private void connectAllNodes() {
        for (int idxFrom = 0; idxFrom < nodes.length; idxFrom++) {
            for (int idxTo = idxFrom + 1; idxTo < nodes.length; idxTo++) {
                nodes[idxFrom].addNeighbor(nodes[idxTo]);
                nodes[idxTo].addNeighbor(nodes[idxFrom]);
            }
        }
    }

    /**
     * Creates a randomly connected graph.
     * 
     * @param howMany
     */
    private static void connectSomeNodes(int howMany) {
        for (int i = 0; i < howMany; i++) {
            final int idxFrom = random.nextInt(nodes.length);
            final int idxTo = random.nextInt(nodes.length);

            nodes[idxFrom].addNeighbor(nodes[idxTo]);
        }
    }

    /**
     * Print out the map of results that was received from the server
     * 
     * @param map the result from the server after it finished doing the search
     */
    private static void printMapping(Map<Node, Map<Node, Integer>> map) {

        for (Node key : map.keySet()) {

            System.out.println(key + " has the mapping:");

            for (Map.Entry<Node, Integer> val : map.get(key).entrySet()) {

                System.out.println("\t" + val.getKey() + " -> " + val.getValue());

            }

            System.out.println();

        }

    }

    public static void main(String[] args) {

        //make sure a host name is supplied
        if (args.length != 1) {
            System.out.println("Usage: $java Client <hostname>");
            System.exit(1);
        }

        createNodes(GRAPH_NODES); //create the graph
        connectSomeNodes(GRAPH_EDGES); //connect the graph

        //set up the security manager
        if (System.getSecurityManager() == null) {

            System.setSecurityManager(new SecurityManager());

        }

        try {
            String name = "GetSearcher";
            Registry registry = LocateRegistry.getRegistry(args[0]); //get the registry at the given hostname

            GetSearcher getSearcher = (GetSearcher) registry.lookup(name); //get a remote reference to a GetSearcher object

            //use the GetSearcher object to obtain a GraphSearcher object, then use this GraphSearcher to execute the search benchmark method
            Map<Node, Map<Node, Integer>> result = getSearcher.getSearcher().searchBenchmark(NUM_SEARCHES, nodes);

            printMapping(result); //print the results out

        }

        catch (RemoteException e) {
            System.out.println("Got a RemoteException");
            e.printStackTrace();
        }

        catch (NotBoundException e) {
            System.err.println("Got an error when performing the registry lookup");
            e.printStackTrace();
        }

    }
}

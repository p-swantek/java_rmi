package task2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Random;

public class ClientTask2 {

    // How many nodes and how many edges to create.
    private static final int GRAPH_NODES = 10;
    private static final int GRAPH_EDGES = 40;
    private static final int NUM_SEARCHES = 50;
    private static final Random random = new Random();

    private static Node[] nodes;

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

        if (args.length != 1) {
            System.out.println("Usage: $java Client <hostname>");
            System.exit(1);
        }

        createNodes(GRAPH_NODES);
        connectSomeNodes(GRAPH_EDGES);

        //if (System.getSecurityManager() == null) {
        //   System.setSecurityManager(new SecurityManager());
        //}

        try {
            String name = "GraphSearcher";
            Registry registry = LocateRegistry.getRegistry(args[0]);

            GraphSearcher searcher = (GraphSearcher) registry.lookup(name);

            Map<Node, Map<Node, Integer>> result = searcher.searchBenchmark(NUM_SEARCHES, nodes);

            printMapping(result);

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

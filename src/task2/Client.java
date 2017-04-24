package task2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;

public class Client {

    // How many nodes and how many edges to create.
    private static final int GRAPH_NODES = 10;
    private static final int GRAPH_EDGES = 30;
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

        if (args.length != 2) {
            System.out.println("Usage: $java Client <hostname> <port number>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        createNodes(GRAPH_NODES);
        connectSomeNodes(GRAPH_EDGES);

        try (Socket sock = new Socket(host, port);
                ObjectOutputStream objOut = new ObjectOutputStream(sock.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(sock.getInputStream());) {

            objOut.writeObject(nodes);
            objOut.flush();

            Map<Node, Map<Node, Integer>> result = (Map<Node, Map<Node, Integer>>) objIn.readObject();

            printMapping(result);

        }

        catch (UnknownHostException e) {
            System.out.println("Error creating socket, host is unknown.");
            System.exit(1);

        }

        catch (IOException e) {
            System.out.println("Error creating socket, IO error occurred.");
            System.exit(1);
        }

        catch (ClassNotFoundException e) {
            System.out.println("Error when deserializing the result.");
            System.exit(1);
        }

    }

}

package task1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;

public class Client {

    // How many nodes and how many edges to create.
    private static final int GRAPH_NODES = 10;
    private static final int GRAPH_EDGES = 20;
    private static final Random random = new Random();

    private static Node[] nodes;

    /**
     * Creates nodes of a graph.
     * 
     * @param howMany
     */
    public static void createNodes(int howMany) {
        nodes = new Node[howMany];

        for (int i = 0; i < howMany; i++) {
            nodes[i] = new NodeImpl(i);
        }
    }

    /**
     * Creates a fully connected graph.
     */
    public void connectAllNodes() {
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
    public static void connectSomeNodes(int howMany) {
        for (int i = 0; i < howMany; i++) {
            final int idxFrom = random.nextInt(nodes.length);
            final int idxTo = random.nextInt(nodes.length);

            nodes[idxFrom].addNeighbor(nodes[idxTo]);
        }
    }

    private static void printMapping(Map<Node, Map<Node, Integer>> map) {

        for (Node key : map.keySet()) {

            System.out.print("Distances from " + key);

            for (Map.Entry<Node, Integer> val : map.get(key).entrySet()) {

                System.out.println("\t" + val.getKey() + " " + val.getValue());

            }

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
                InputStream is = sock.getInputStream();
                OutputStream os = sock.getOutputStream();
                ObjectOutputStream objOut = new ObjectOutputStream(os);
                ObjectInputStream objIn = new ObjectInputStream(is);) {

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

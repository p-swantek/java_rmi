package task1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;

/**
 * Client that will connect to a server using TCP. Once connected, the client
 * will send over a serialized array of Nodes that represents a graph. The
 * server will receive the graph, run the search benchmark on this graph, and
 * set back a serialized HashMap of results back to the client. The client will
 * then print out this mapping of accumulated results
 * 
 * @author Peter Swantek
 *
 */
public class ClientTask1 {

    // How many nodes and how many edges to create.
    private static final int GRAPH_NODES = 10;
    private static final int GRAPH_EDGES = 30;
    private static final Random random = new Random();

    private static SerializableNode[] nodes;

    /**
     * Creates nodes of a graph.
     * 
     * @param howMany
     */
    private static void createNodes(int howMany) {
        nodes = new SerializableNode[howMany];

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
    private static void printMapping(Map<SerializableNode, Map<SerializableNode, Integer>> map) {

        for (SerializableNode key : map.keySet()) {

            System.out.println(key + " has the mapping:");

            for (Map.Entry<SerializableNode, Integer> val : map.get(key).entrySet()) {

                System.out.println("\t" + val.getKey() + " -> " + val.getValue());

            }

            System.out.println();

        }

    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        //make sure the host and port number to connect to are given as command line args
        if (args.length != 2) {
            System.out.println("Usage: $java Client <hostname> <port number>");
            System.exit(1);
        }

        String host = args[0]; //get the host name of the server
        int port = Integer.parseInt(args[1]); //get the port the server is listening on

        createNodes(GRAPH_NODES); //create the nodes of the graph
        connectSomeNodes(GRAPH_EDGES); //establish connections between nodes of the graph

        //establish a socket connection to the server, get the object input/output streams for this socket then
        try (Socket sock = new Socket(host, port);
                ObjectOutputStream objOut = new ObjectOutputStream(sock.getOutputStream());
                ObjectInputStream objIn = new ObjectInputStream(sock.getInputStream());) {

            objOut.writeObject(nodes); //send the array of nodes through the socket
            objOut.flush();

            Map<SerializableNode, Map<SerializableNode, Integer>> result = (Map<SerializableNode, Map<SerializableNode, Integer>>) objIn.readObject(); //get back the results from the server

            printMapping(result); //print out the results contained in the hashmap

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

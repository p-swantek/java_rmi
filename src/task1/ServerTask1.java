package task1;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Server that will wait for clients to connect using TCP. When a client
 * connects, the client will send an array of Nodes to the server which
 * represent a graph. The server will then run the searchBenchMark method on
 * this graph, and send the result back to the client.
 * 
 * @author Peter Swantek
 *
 */
public class ServerTask1 {

    public static void main(String[] args) throws IOException {

        //make sure a port number is supplied
        if (args.length != 1) {
            System.out.println("Usage: $java Server <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]); //get the port the server should listen on

        ServerSocket sock = new ServerSocket(port); //create a socket for that port

        System.out.println(String.format("Server started...listening on port: %d", port));

        //loop forever waiting for clients to connect
        while (true) {

            Socket clientConn = sock.accept(); //block until a client connects

            System.out.println("Incoming client connection accepted! Handling the connection...");

            Thread t = new Thread(new ConnectionHandler(clientConn)); //create a thread to handle the connection

            t.start();

        }

    }
}

/**
 * Handles any client that connects to the server. Will obtain the graph of
 * nodes from the client and perform the search. After the search is done, will
 * send the result back to the client
 * 
 * @author Peter Swantek
 *
 */
class ConnectionHandler implements Runnable {

    private static final int NUM_SEARCHES = 50; // How many searches to perform

    private final Random random;
    private final Searcher searcher;
    private final Socket sock;

    /**
     * Creates a new connection handler to deal with a connection on a socket
     * 
     * @param s the socket thru which the client connected
     */
    public ConnectionHandler(Socket s) {
        random = new Random();
        searcher = new SearcherImpl();
        sock = s;
    }

    @Override
    public void run() {

        processRequest(); //process the client connection

    }

    /**
     * Process the client connection. will establish input/output streams from
     * the clients socket. Will read in the graph from the client, then perform
     * the search benchmark. Finally, the hashmap of results will be shipped
     * back to the client.
     * 
     */
    private void processRequest() {

        // Get a reference to the socket's input and output streams.
        //inputstream from the socket, transmits data from the socket
        //outputstream that will transmit data to the socket

        try (InputStream is = sock.getInputStream();
                OutputStream os = sock.getOutputStream();
                ObjectInputStream objIn = new ObjectInputStream(is);
                ObjectOutputStream objOut = new ObjectOutputStream(os);) {

            SerializableNode[] graph = (SerializableNode[]) objIn.readObject(); //obtain the graph from the client

            Map<SerializableNode, Map<SerializableNode, Integer>> result = searchBenchmark(NUM_SEARCHES, graph); //run search and accumulate results

            objOut.writeObject(result); //send the result back to the client
            objOut.flush();
        }

        catch (IOException e) {
            System.out.println("IO error occured while processing a client connection...");
            e.printStackTrace();
        }

        catch (ClassNotFoundException e) {
            System.out.println("IO error occured while processing a client connection...");
            e.printStackTrace();
        }

        finally {

            try {

                sock.close(); //once done, close the connection to the client
            }

            catch (IOException e) {
                System.out.println("IO error occured while closing the client connection...");
                e.printStackTrace();
            }
        }

    }

    /**
     * Runs a quick measurement on the graph.
     * 
     * @param howMany the amount of searches to do
     * @param nodes an array of nodes that was sent from the client, run search
     *            on these nodes
     * @return a mapping of each node to a mapping of the nodes this node was
     *         connected to and the distance between them.
     */
    private Map<SerializableNode, Map<SerializableNode, Integer>> searchBenchmark(int howMany, SerializableNode[] nodes) {
        // Display measurement header.
        System.out.printf("%7s %8s %13s %13s\n", "Attempt", "Distance", "Time", "TTime");

        //map of nodes to a mapping of nodes that are connected to the given node as well as the distances for those pairs
        Map<SerializableNode, Map<SerializableNode, Integer>> result = new HashMap<>();

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

                //update the map of results
                if (result.containsKey(nodes[idxFrom])) {
                    Map<SerializableNode, Integer> currMap = result.get(nodes[idxFrom]);
                    currMap.put(nodes[idxTo], distance);
                    result.put(nodes[idxFrom], currMap);
                }

                else {
                    Map<SerializableNode, Integer> map = new HashMap<>();
                    map.put(nodes[idxTo], distance);
                    result.put(nodes[idxFrom], map);
                }

            }
        }

        return result;
    }

}

package task1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a node in the graph
 * 
 *
 */
public class NodeImpl implements SerializableNode {

    private static final long serialVersionUID = 1L;
    private final Set<SerializableNode> nodes = new HashSet<SerializableNode>();
    private int nodeId; //add an id number for each node

    /**
     * Create a new Node, give the node an id number for identification
     * 
     * @param id integer representing the id of this particular node
     */
    public NodeImpl(int id) {
        nodeId = id;
    }

    /**
     * String representation of a Node, will include the Node's id
     * 
     */
    @Override
    public String toString() {
        return String.format("n%d", nodeId);
    }

    @Override
    public Set<SerializableNode> getNeighbors() {
        return nodes;
    }

    @Override
    public Map<SerializableNode, Integer> getTransitiveNeighbors(int distance) {
        if (distance <= 0)
            throw new IllegalArgumentException("Argument distance must be positive");

        Map<SerializableNode, Integer> nodeToDistance = new HashMap<SerializableNode, Integer>();
        Set<SerializableNode> currentLayer = new HashSet<SerializableNode>();

        // Initial node at zero-distance
        currentLayer.add(this);

        // Closure  for each level of i-distant nodes
        for (int i = 0; i < distance; ++i) {
            Set<SerializableNode> nextLayer = new HashSet<SerializableNode>();

            // Use nodes which are in the current level 
            for (SerializableNode node : currentLayer) {
                if (!nodeToDistance.containsKey(node)) {
                    nodeToDistance.put(node, i);
                    nextLayer.addAll(node.getNeighbors());
                }
            }

            // Move to the next layer
            currentLayer = nextLayer;
        }

        // Handle the last layer
        for (SerializableNode node : currentLayer) {
            if (!nodeToDistance.containsKey(node))
                nodeToDistance.put(node, distance);
        }

        return nodeToDistance;
    }

    @Override
    public void addNeighbor(SerializableNode neighbor) {
        nodes.add(neighbor);
    }
}

package task1;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Updated interface for Nodes of the graph, will make sure that the nodes are
 * serializable in order to be sent over the network.
 * 
 *
 */
public interface SerializableNode extends Serializable {
    Set<SerializableNode> getNeighbors();

    Map<SerializableNode, Integer> getTransitiveNeighbors(int distance);

    void addNeighbor(SerializableNode neighbor);
}

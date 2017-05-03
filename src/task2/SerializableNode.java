package task2;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface SerializableNode extends Serializable {
    Set<SerializableNode> getNeighbors();

    Map<SerializableNode, Integer> getTransitiveNeighbors(int distance);

    void addNeighbor(SerializableNode neighbor);
}

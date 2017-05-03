package task1;

/**
 * Behaviors needed to perform searches on a graph
 * 
 */
public interface Searcher {
    public static final int DISTANCE_INFINITE = -1;

    public int getDistance(SerializableNode from, SerializableNode to);

    public int getTransitiveDistance(int distance, SerializableNode from, SerializableNode to);
}

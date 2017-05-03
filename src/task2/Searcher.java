package task2;

public interface Searcher {
    public static final int DISTANCE_INFINITE = -1;

    public int getDistance(SerializableNode from, SerializableNode to);

    public int getTransitiveDistance(int distance, SerializableNode from, SerializableNode to);
}

package task3;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for the object that will actually perform the search benchmark
 * method. Will be serializable so that it can be sent over the network to the
 * client
 * 
 * @author Peter Swantek
 *
 */
public interface GraphSearcher extends Serializable {

    Map<Node, Map<Node, Integer>> searchBenchmark(int howMany, Node[] nodes);

}

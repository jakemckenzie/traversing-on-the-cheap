import java.util.ArrayList;
import java.util.List;
/**
 * @author Jake McKenzie
 * @version August 12, 2018
 */
public class Node {
    /**
     * @param node
     */
    int node;
    /**
     * @param arc
     */
    List<Edge> arc;
    /**
     * @param visited
     */
    boolean visited;
    /**
     * @param cost
     */
    int cost;
    /**
     * @param interior
     */
    Node interior;
    /**
     * @param node
     */
    public Node(int node) {
        visited = false;
        cost = 0x7FFFFFFF;
        this.node = node;
        arc = new ArrayList<Edge>();
        interior = null;
    }
    /**
     * @param destination
     * @param weight
     */
    public void edgeLabeling(Node destination, int weight) {
        if (!this.equals(destination)) {
            Edge e = new Edge(this, destination, weight);
            arc.add(e);
        }
    } 
}
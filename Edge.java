/**
 * @author Jake McKenzie
 * @version August 12, 2018
 */
public class Edge {
    /**
     * @param weight
     */
    int weight;
    /**
     * @param source
     */
    Node source;
    /**
     * @param destination
     */
    Node destination;
    /**
     * @param source
     * @param destination
     * @param weight
     */
    public Edge(Node source, Node destination, int weight){
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
    /**
     * Edge represented by it's interior and frontier node.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.source);
        sb.append(", ");
        sb.append(this.destination);
        sb.append(")");
        return sb.toString();
    }
}
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * @author Jake McKenzie
 * @version August 12, 2018
 */
public class Graph {
    /**
     * @param V A list of vertices represented by their nodes
     */
    List<Node> V;
    /**
     * @param E a list of edges represnted by edges
     */
    List<Edge> E;
    /**
     * A simple constructor that initializes the graph.
     * The array should never increase in size if my calculations are
     * correct saving a lot of time.
     */
    public Graph(int numberOfVertices){
        V = new ArrayList<Node>(numberOfVertices);
        E = new ArrayList<Edge>();
    }
    /**
     * @param n
     */
    public void addNode(Node n){
        V.add(n);
    }
    /**
     * @param test
     */
    
    public void drawGraph(int[][] test){
        int frontier = test.length;
        int i = 0;
        int j = 0;
        for(i = 0; i < frontier; i++) V.add(new Node(i));
        for(Node n: V){
            for(j = 0; j < frontier; j++){
                if(test[V.indexOf(n)][j] != 0){
                    n.edgeLabeling(V.get(j), test[V.indexOf(n)][j]);
                }
            }
        }

    }
    /**
     * A simple print function for seeing:
     * 1) the cost at each node
     * 2) the interior nodes
     * 3) the adjacency list
     * 4) the adjacency list cost
     */
    public void print(){
        StringBuilder sb = new StringBuilder();
        for(Node nodes: V){	
            sb.append("Node: " + nodes.node + "\nCost: " + nodes.cost);
            if (nodes.interior != null) sb.append("\nInterior: "+ nodes.interior.node);
            sb.append("\nAdjacency List: ");
            for (Edge e : nodes.arc) {
                sb.append("(" + e.source.node + ", " + e.destination.node + ") \nCost: "+ e.weight + "\n");
            }
            System.out.print(sb.toString());
        }
    }
    /**
     * The running time of this is O((V-1)!) ~ V^V = 2^(VlogV) because there are (V-1)! ways of paths to traverse in a graph like this.
     * I came to this reasoning from watching this video below. As I am dealing with permutatios instead of 
     * partitions. I have (V-1)! instead of 2^V.
     * https://www.coursera.org/lecture/advanced-algorithms-and-complexity/brute-force-search-x60TX
     * 
     * The space complexity is even worse as there are V^(V-1) spanning trees in a complete
     * graph. I do not have a complete graph here but it is not sparse either so the space
     * complexity is very bad.
     */
    
    public List<Dipath> naiveBruteForce(){
        List<Dipath> dipaths = new ArrayList<Dipath>();
        Dipath adjPath = null;
        List<Edge> edges = new ArrayList<Edge>();
        for(Node nodes: V){
            for(Edge e: nodes.arc){
                edges.add(e);
            }
        }
        adjPath = new Dipath();
        adjPath.edgeLabeling(edges.get(0));
        dipaths.add(adjPath);
        for(Edge e: edges){	
            List<Dipath> paths = new ArrayList<Dipath>(dipaths);			
            for(Dipath di: paths){
                adjPath = new Dipath();
                adjPath.path.addAll(di.path);
                adjPath.cost = di.cost;
                if(!adjPath.flagger(e)){
                    adjPath = new Dipath();
                    adjPath.edgeLabeling(e);
                }else adjPath.edgeLabeling(e);
                if(!dipaths.contains(adjPath)) dipaths.add(adjPath);
            }
        }
        
        List<Dipath> temp = new ArrayList<Dipath>(dipaths);
        for(Dipath di: temp){
            if(!(di.path.get(0).source.node == 0 && V.size() - 1 == di.path.get(di.path.size() - 1).destination.node)){
                dipaths.remove(di);
            }
        }
        return dipaths;
    }
    /**
     * Returns the minimum dipath for the naive approach.
     */
    
    public Dipath minimumNaiveBruteForce(){
        Dipath min = null;
        List<Dipath> completePaths = this.naiveBruteForce();
        for(Dipath path: completePaths){
            min = (min != null) ? min.cost > path.cost ? path : min  : path;
        }
        return min;
    }
    /**
     * Given an interior node and destination returns back the edge.
     * @param source interior node
     * @param desination frontior node
     */
    public Edge getEdge(Node source, Node destination){
        Edge temp = null;
        for(Node node : V){
            for(Edge edges : node.arc){
                if(edges.source.node == source.node && edges.destination.node == destination.node){
                    temp = edges;
                }
            }
        }
        return temp;
    }
    /**
     * Bredth First Search using decrease and conquer. 
     * http://www.cs.cmu.edu/afs/cs/academic/class/15210-f12/www/lectures/lecture10.pdf
     * https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-046j-design-and-analysis-of-algorithms-spring-2012/lecture-notes/MIT6_046JS12_lec06.pdf
     * https://www.geeksforgeeks.org/decrease-and-conquer/
     */
    
    public Dipath BreadthFirstSearch(){
        Node source = V.get(0);
        Node destination = V.get(V.size() - 1);
        source.cost = 0;
        BFS(source, destination);
        return recoverPath();

    }
    
    /**
     * Here we build a list of new "unvisited" routes, where the structure is a tuple of cost, then a 
     * list of paths taken is to get that cost from the start.
     * @param source
     * @param destination 
     */
    public void BFS(Node source, Node destination){
        for(Edge edges: source.arc){
            if(source.cost + edges.weight < edges.destination.cost){
                edges.destination.cost = source.cost + edges.weight;
                if(!source.arc.isEmpty() && !edges.destination.visited){
                    BFS(edges.destination, destination);
                    edges.destination.interior = source;
                }
            }
        }		

    }
    /**
     * http://www.csl.mtu.edu/cs4321/www/Lectures/Lecture%2010%20-%20Decrease%20and%20Conquer%20Sorts%20and%20Graph%20Searches.htm
     * https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-046j-design-and-analysis-of-algorithms-spring-2012/lecture-notes/MIT6_046JS12_lec06.pdf
     */
    
    public Dipath Dijkstra(){
        V.get(0).cost = 0;
        for(Node node: V){
                for(Edge edges: node.arc){
                    if(!edges.destination.visited){
                        if(edges.destination.cost > (node.cost+ edges.weight)){
                            edges.destination.cost = node.cost + edges.weight;
                            edges.destination.interior = node;
                        }
                    }

                }
                node.visited = true;
        }
        return recoverPath();
    }

    /**
     * Recovers the path for both BFS and Dijkstra's algorithm
     */
    public Dipath recoverPath(){
        Dipath dipath = new Dipath();
        List<Node> vertices = new ArrayList<Node>(V);
        Collections.reverse(vertices);
        for(Node nodes: vertices){
            if(nodes.interior != null){
                Edge temp = getEdge(nodes.interior, nodes);
                if(temp != null){
                    dipath.edgeLabeling(temp);
                }
            }
        }
        return dipath;
    }

}
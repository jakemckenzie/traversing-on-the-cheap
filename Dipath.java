import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * @author Jake McKenzie
 * @version August 12, 2018
 */
public class Dipath {
    /**
     * @param path
     */
    List<Edge> path;
    /**
     * @paramcost
     */
    int cost;
    /**
     * constructor for the directed path
     */
    public Dipath(){
        path = new ArrayList<Edge>();
        cost = 0;
    }
    /**
     * @param e an edge sent for labeling
     */
    public void edgeLabeling(Edge e){
        if(!e.source.equals(e.destination)){
            int size = path.size();
            if(path.size() < 1){
                path.add(e);
                cost+=e.weight;
            }else if(path.get(0).source.node == e.destination.node){
                path.add(0, e);
                cost+=e.weight;
            } else if(path.get(size - 1).destination.node == e.source.node){
                if(!path.contains(e)){
                    path.add(e);
                    cost+=e.weight;	
                }  
            }
        }
    }
    /**
     * A simple tostring method for printing.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        if (this.path.size() < 0) return sb.toString(); 
        sb.append("(");
        for (Edge e : path){
            sb.append(e.source.node);
            sb.append(", ");
        }
        sb.append(path.get(path.size() - 1).destination.node);
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o){
        Dipath p = (Dipath) o;
        return (this.cost == p.cost) ? this.path.equals(p.path) ? true : false : false;
    }

    public boolean flagger(Edge e){
        int size = path.size();
        return (path.size() < 1) ? true : (path.get(size - 1).destination.node == e.source.node) ? true : false;
    }
}
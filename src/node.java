import java.util.ArrayList;
import java.util.List;

public class node
{
	private int id,clusterId;		//id is overall id(from all nodes), clusterId is the Id w.r.t. the cluster
	private int clusterNum;			//the number of cluster to which it belongs
	private boolean isBdryPt = false;		//whether the node is a bdry point or not
	private List<edge> edges = new ArrayList<>();		//list of edges originating from the no
	private double tempTime;		//temp variable used to store the time values while applying dijsktra's
	private List<Integer> neighbours = new ArrayList<>();
	
	private node parent;
//	
//	node(){
//		
//	}
	
	public node getParent() {
		return parent;
	}

	public void setParent(node parent) {
		this.parent = parent;
	}
	
	public boolean isBdryPt() {
		return isBdryPt;
	}

	public void setBdryPt(boolean isBdryPt) {
		this.isBdryPt = isBdryPt;
	}

	public double getTempTime() {
		return Math.round(tempTime * 10000.0) / 10000.0;
//		return tempTime;
	}

	public void setTempTime(double tempTime) {
		this.tempTime = tempTime;
	}

	public List<edge> getEdges() {
		return edges;
	}

	public void setEdges(List<edge> edges) {
		this.edges = edges;
	}
	
	public void addEdge(edge e) {
		edges.add(e);
		neighbours.add(e.getEnd());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public int getClusterNum() {
		return clusterNum;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}
	
	public String getStringId() {
		String s=Integer.toString(id+1)+"("+Integer.toString(clusterId+1)+")";
		return s;
	}

	public boolean isNeighbour(int i) {
		// TODO Auto-generated method stub
		return neighbours.contains(i);
	}
}

import java.util.List;

public class node
{
	private int id,clusterId;		//id is overall id(from all nodes), clusterId is the Id w.r.t. the cluster
	private int clusterNum;			//the number of cluster to which it belongs
	private boolean isBdryPt;		//whether the node is a bdry point or not
	private List<edge> edges;		//list of edges originating from the no
	private double tempTime;		//temp variable used to store the time values while applying dijsktra's
	
	public boolean isBdryPt() {
		return isBdryPt;
	}

	public void setBdryPt(boolean isBdryPt) {
		this.isBdryPt = isBdryPt;
	}

	public double getTempTime() {
		return tempTime;
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

	boolean isBdryPoint;

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

	public boolean isBdryPoint() {
		return isBdryPoint;
	}

	public void setBdryPoint(boolean isBdryPoint) {
		this.isBdryPoint = isBdryPoint;
	}
	
}

import java.util.List;

public class node
{
	private int id,clusterId;
	private int clusterNum;
	private List<edge> edges;
	private double temp_dist;
	
	public double getTemp_dist() {
		return temp_dist;
	}

	public void setTemp_dist(double positiveInfinity) {
		this.temp_dist = positiveInfinity;
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

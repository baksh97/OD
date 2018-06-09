import java.util.List;

public class main {
	
	int clusterNums[];
	List<cluster> clusters;
	
	int timeWindow = 10;
	
	
	double findTimeBetweenNodeAtTime(node n1,node n2, double currentTime) {
		
		cluster cluster1 = getCluster(n1);
		cluster cluster2 = getCluster(n2);
		
		List<node> bdryPointsCluster1 = cluster1.getBdry_points();
		List<node> bdryPointsCluster2 = cluster2.getBdry_points();
		
		for(node bdryPt: bdryPointsCluster1) {
			
			int timeIndex1 = (int) (currentTime%timeWindow);
			
			double t1 = cluster1.getTimeFromNodeToBdry().get(timeIndex1).get(n1.getClusterId()).get(bdryPt.getClusterId());
			
			int timeIndex2 = (int) ((currentTime+t1)%timeWindow);
			
			for(node otherBdryPt: bdryPointsCluster2) {
				double t2 = cluster1.getTimeBtwBdry().get(timeIndex2).get(bdryPt.getClusterId()).get(otherBdryPt.getClusterId());				
			}
		}
		
		return 0;
	}
	
	cluster getCluster(node n) {
		int id = n.getId();
		return clusters.get(clusterNums[id]);
	}
	
	public static void main(String args[]) {
		System.out.println((int)3.444%2);
	}
	
}

class data{
	private List<node> nodes;
	private int num_vehicles;
}

class edge{
	private int start,end;
	private double[][] speed;
}

class node{
	private int id,clusterId;
	private int clusterNum;
	private List<edge> edges;
	
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

class cluster{
	
	private List<node> bdryPoints;
	private List<List<List<Double>>> timeFromNodeToBdry;
	private List<List<List<Double>>> timeBtwBdry;

	public List<List<List<Double>>> getTimeFromNodeToBdry() {
		return timeFromNodeToBdry;
	}

	public void setTimeFromNodeToBdry(List<List<List<Double>>> timeFromNodeToBdry) {
		this.timeFromNodeToBdry = timeFromNodeToBdry;
	}

	public List<List<List<Double>>> getTimeBtwBdry() {
		return timeBtwBdry;
	}

	public void setTimeBtwBdry(List<List<List<Double>>> timeBtwBdry) {
		this.timeBtwBdry = timeBtwBdry;
	}

	List<node> getBdry_points(){
		return bdryPoints;
	}
	
	void setBdry_points(List<node> bdryPoints) {
		this.bdryPoints = bdryPoints;
	}
}
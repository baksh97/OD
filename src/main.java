import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class main {
	
	int clusterNums[];
	static List<cluster> clusters;
	
	static double timeWindow = 10, endTime=100;
	static int num_clusters;
	
	double findTimeBetweenNodesAtTime(node n1,node n2, double currentTime) {
		
		cluster cluster1 = getCluster(n1);
		cluster cluster2 = getCluster(n2);
		
		List<node> bdryPointsCluster1 = cluster1.getBdry_points();
		List<node> bdryPointsCluster2 = cluster2.getBdry_points();
		
		double minTime = Double.POSITIVE_INFINITY;
		for(node bdryPt: bdryPointsCluster1) {
			
			double t1 = cluster1.getWeightedTime(currentTime,n1.getClusterId(), -1 ,bdryPt.getClusterId(), cluster1.getTimeIntraCluster());
				
			for(node otherBdryPt: bdryPointsCluster2) {
				double t2 = cluster1.getWeightedTime(currentTime+t1, bdryPt.getClusterId(),cluster2.getNum() ,otherBdryPt.getClusterId(), cluster1.getTimeBtwBdry()); //this is wrong as number of bdry pts can be different for each cluster
				
				double t3 = cluster2.getWeightedTime(currentTime+t1+t2, otherBdryPt.getClusterId(), -1, n2.getClusterId(), cluster2.getTimeIntraCluster());
				
				if(minTime > currentTime + t1 + t2 + t3) {
					minTime = currentTime + t1  +t2 + t3;
				}
			}
		}
		
		return minTime;
	}
	
	
	
	cluster getCluster(node n) {
		int id = n.getId();
		return clusters.get(clusterNums[id]);
	}
	
	public static void prepareData(data d) {
		cluster[] clustersArray = new cluster[d.getNum_clusters()];
		
		System.out.println("num clusters: "+d.getNum_clusters());
		
		int count=0;
		for(int i=0;i<d.getNum_clusters();i++) {
			clustersArray[i] = new cluster();
			clustersArray[i].setNum(count++);
		}
		
//		for(cluster c: clustersArray) {
//			System.out.println(c.getNum());
//		}
		
		for(node n: d.getNodes()) {
			int clusterNum = n.getClusterNum();
			cluster c1 = clustersArray[clusterNum];

			for(edge e: n.getEdges()) {				
				if(d.getNodes().get(e.getOtherEnd(n.getId())).getClusterNum() != clusterNum) {
					
					n.setBdryPt(true);
					c1.addBdryNode(n);
					break;
				}
			}
			if(!n.isBdryPt()) {
				c1.addInnerNode(n);
			}
		}
		
		for(cluster c: clustersArray) {
			c.setInnerNodes();
		}
		
		clusters = Arrays.asList(clustersArray);
		
		
		for(cluster c: clusters) {
			System.out.println(c.toString());
			
			c.setTimeIntraCluster(d.getNodes());
		}
		
		for(cluster c: clusters)c.setTimeBtwBdry(clusters, d.getNodes());
		
	}
	
}
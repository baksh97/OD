import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class main {
	
	int clusterNums[];
	List<cluster> clusters;
	
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
				double t2 = cluster1.getWeightedTime(currentTime+t1, bdryPt.getClusterId(),cluster2.getNum() ,otherBdryPt.getClusterId(), cluster1.getTimeBtwBdry());
				
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
	
	public static void main(String args[]) {
		System.out.println(3.444/2);
//		PriorityQueue<dijsktra_data> queue = new PriorityQueue<>(dist_comparator);
////		queue
//		node[] nodes = new node[6];
//		int[] dists = {3,5,6,4,2,3};
//		int i=0;
//		for(node n: nodes){
////			i++;
//			dijsktra_data d = new dijsktra_data(n, dists[i++]);
//			queue.add(d);
//		}
//		
//		for(dijsktra_data d: queue)
//			System.out.println(d.toString());
	}
	
}
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class main {
	
	int clusterNums[];
	List<cluster> clusters;
	
	static int timeWindow = 10;
	
	
	double findTimeBetweenNodeAtTime(node n1,node n2, double currentTime) {
		
		cluster cluster1 = getCluster(n1);
		cluster cluster2 = getCluster(n2);
		
		List<node> bdryPointsCluster1 = cluster1.getBdry_points();
		List<node> bdryPointsCluster2 = cluster2.getBdry_points();
		
		for(node bdryPt: bdryPointsCluster1) {
			
//			int timeIndex1 = (int) (currentTime%timeWindow);
			
			double t1 = cluster1.getTimeFromNodeToBdry(currentTime,n1.getClusterId(),bdryPt.getClusterId());
			
//			int timeIndex2 = (int) ((currentTime+t1)%timeWindow);
			
			for(node otherBdryPt: bdryPointsCluster2) {
				double t2 = cluster1.getTimeBtwBdry().get(timeIndex2).get(bdryPt.getClusterId()).get(otherBdryPt.getClusterId());				
			}
		}
		
		return 0;
	}
	
	public void calcDistance_from_node_within_cluster(node n,List<node> nodes){
		int clusterNum = n.getClusterNum();
		
		PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
		boolean visited[] = new boolean[]
		node current = new node();
		current.setTemp_dist(0);
		node min_data = null;
		
		for(node n1: nodes){
			if(n1.getId() != n.getId() && n1.getClusterNum()==clusterNum){
				node temp = new node();
				temp.setTemp_dist(Double.POSITIVE_INFINITY);
				unvisited.add(temp);
			}
		}
		
		while(unvisited.size()!=0){
			for(edge e: current.getN().getEdges()){
				int otherEnd = e.getOtherEnd(current.getN().getId());
				node otherNode = nodes.get(otherEnd);
				if(otherNode.getClusterNum()==clusterNum){
					
				}
			}
		}
	}
	
	public static Comparator<node> dist_comparator = new Comparator<node>() {
		
		@Override
		public int compare(node o1, node o2) {
			double dist1 = o1.getTemp_dist(),dist2 = o2.getTemp_dist();
			if(dist1==dist2)
				return 0;
			return (o1.getTemp_dist() < o2.getTemp_dist()) ? -1:1;
		}
	};
	
	cluster getCluster(node n) {
		int id = n.getId();
		return clusters.get(clusterNums[id]);
	}
	
	public static void main(String args[]) {
		System.out.println((int)3.444%2);
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
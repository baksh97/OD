import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class main {
	
	static List<cluster> clusters;
	
	static double timeWindow, endTime, startTime,storeTimeDiff;
	static int num_clusters;
	
	static double findTimeBetweenNodesAtTime(node n1,node n2, double currentTime) {
		
		cluster cluster1 = clusters.get(n1.getClusterNum());
		cluster cluster2 = clusters.get(n2.getClusterNum());
		
		if(cluster1.getNum()==cluster2.getNum()) {
			return cluster1.getWeightedTime(currentTime, n1.getClusterId(), -1, n2.getClusterId(), cluster1.getTimeIntraCluster());
		}
		else {

			List<node> bdryPointsCluster1 = cluster1.getBdry_points();
			List<node> bdryPointsCluster2 = cluster2.getBdry_points();
			
			if(n1.isBdryPt()) {
				if(n2.isBdryPt()) {
					return cluster1.getWeightedTime(currentTime, n1.getClusterId(), n2.getClusterNum(), n2.getClusterId(), cluster1.getTimeBtwBdry());
				}
				else {
					double min_time = Double.POSITIVE_INFINITY;
					for(node n: bdryPointsCluster2) {
						double timeFromN1ToBdry2 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), n.getClusterNum(), n.getClusterId(), cluster1.getTimeBtwBdry());
						double timeFromBdry2ToN2 = cluster2.getWeightedTime(currentTime+timeFromN1ToBdry2, n.getClusterId(), -1, n2.getClusterId(), cluster2.getTimeIntraCluster());
						
						if(min_time > timeFromN1ToBdry2 + timeFromBdry2ToN2) {
							min_time = timeFromN1ToBdry2 + timeFromBdry2ToN2;
						}
					}
					return min_time;
				}
			}
			else {
				if(n2.isBdryPt()) {
					double min_time = Double.POSITIVE_INFINITY;
					for(node n: bdryPointsCluster1) {
						double timeFromN1ToBdry1 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), -1, n.getClusterId(), cluster1.getTimeIntraCluster());
						double timeBdry1ToN2 = cluster1.getWeightedTime(currentTime + timeFromN1ToBdry1, n.getClusterId(), n2.getClusterNum(), n2.getClusterId(), cluster1.getTimeBtwBdry());
						
						if(min_time > timeFromN1ToBdry1 + timeBdry1ToN2) {
							min_time = timeFromN1ToBdry1  +timeBdry1ToN2;
						}
					}
					return min_time;
				}
				else {
					double min_time = Double.POSITIVE_INFINITY;
					for(node n: bdryPointsCluster1) {
						double timeFromN1ToBdry1 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), -1, n.getClusterId(), cluster1.getTimeIntraCluster());
						
						double min_time2= Double.POSITIVE_INFINITY;
						for(node m: bdryPointsCluster2) {
							double timeFromBdry1ToBdry2 = cluster1.getWeightedTime(currentTime + timeFromN1ToBdry1, n.getClusterId(), m.getClusterNum(), m.getClusterId(), cluster1.getTimeBtwBdry());
							double timeFromBdry2ToN2 = cluster2.getWeightedTime(currentTime + timeFromN1ToBdry1 + timeFromBdry1ToBdry2, m.getClusterId(), -1, n2.getClusterId(), cluster2.getTimeIntraCluster());
							
							if(min_time2  > timeFromBdry1ToBdry2 + timeFromBdry2ToN2)
								min_time2 = timeFromBdry1ToBdry2 + timeFromBdry2ToN2;
						}
						
						if(min_time > timeFromN1ToBdry1 + min_time2)
							min_time = timeFromN1ToBdry1 + min_time2;
					}
					return min_time;
				}
			}
		}
	}
	
	public static void prepareData(data d) {
		endTime = d.getEndTime();
		startTime = d.getStartTime();
		timeWindow = (int)((endTime-startTime)/d.getNum_intervals());
		num_clusters = d.getNum_clusters();
		
		cluster[] clustersArray = new cluster[num_clusters];
		
		
		int count=0;
		for(int i=0;i<num_clusters;i++) {
			clustersArray[i] = new cluster();
			clustersArray[i].setNum(count++);
		}
		
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
			c.setTimeIntraCluster(d.getNodes());
		}
		
		for(cluster c: clusters)c.setTimeBtwBdry( d.getNodes());
		
		
		for(cluster c: clusters) {
			System.out.println(c.toString());
		}
		
	}
	
}
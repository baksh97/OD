import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class main {
	
	static List<cluster> clusters;
	
	static double timeWindow, endTime, startTime,storeTimeDiff;
	static int num_clusters;
	
	
	static double getTimeForRoute(List<node> nodes , double currentTime){
		double time = 0;
		node n = nodes.get(0);
		if(nodes.size()==1){
			return time;
		}
		else{
			double neighTime= 0;
			for(edge e: n.getEdges()){
				if(e.getEnd() == nodes.get(1).getId()){
					neighTime = e.getTime_from_speed(currentTime);
				}
			}
			
			if(neighTime == 0){
				System.err.println("not neigbours");
				return -1;
			}
			
			nodes.remove(0);
			return neighTime + getTimeForRoute(nodes , currentTime + neighTime);
		}
		
//		return time;
	}
	
	
	private static List<Integer> getRouteIntraCluster(node n1, node n2, double currentTime) {
		
//		System.out.println("route intracluster called for nodes: "+n1.getStringId() + ", "+n2.getStringId() + " at time: "+currentTime);
		
		if(n1.getId() == n2.getId())
			return null;
		
		int clusterNum = n1.getClusterNum();
		List<List<List<node>>> routesIntraCluster = clusters.get(clusterNum).getRouteIntraCluster();
		List<Integer> route = new ArrayList<>();
		
		double time = currentTime;
		int index = (int) (time/storeTimeDiff);
		
//		System.out.println("index : "+ index);
		
		node earlierParent = (routesIntraCluster.get(index).get(n1.getClusterId()).get(n2.getClusterId()));
		
		List<Integer> otherRoute = getRouteIntraCluster(n1, earlierParent, currentTime);
		
		route.add(earlierParent.getId()+1);
		
		if(otherRoute!=null) {
			route.addAll(0,otherRoute);
		}
		
		return route;
	}
	
//	double time = currentTime;
//	if(clusterNum!=-1) {
//		List<List<List<List<Double>>>> times = (List<List<List<List<Double>>>>) obj;
//		
//		double timeDiff = main.storeTimeDiff;
//		int index = (int) (time/timeDiff);
//		
//		double earlierTime = (times.get(index).get(id1).get(clusterNum).get(id2));
//		
//		double weight1 = (timeDiff - time%timeDiff)/timeDiff,
//				weight2 = (time%timeDiff)/timeDiff;
//		if(index==times.size()-1)
//			return earlierTime;
//		else {
//			double laterTime = (times.get(index+1).get(id1).get(clusterNum).get(id2));
//			return earlierTime*weight1 + laterTime*weight2;
//		}
//	}
//	else {
//		List<List<List<Double>>> times = (List<List<List<Double>>>) obj;
//		double timeDiff = main.storeTimeDiff;
//		int index = (int) (time/timeDiff);
//		double earlierTime = (times.get(index).get(id1).get(id2));
//		
//		double weight1 = (timeDiff - time%timeDiff)/timeDiff,
//				weight2 = (time%timeDiff)/timeDiff;
//		if(index==times.size()-1)
//			return earlierTime;
//		else {
//			double laterTime = (times.get(index+1).get(id1).get(id2));
//			return earlierTime*weight1 + laterTime*weight2;
//		}
//	}
	
	private static List<Integer> getRouteInterCluster(node n1 , node n2, double currentTime){
		
//		System.out.println("route intercluster called for nodes: "+n1.getStringId() + ", "+n2.getStringId()  + " at time: "+currentTime);

		
		if(n1.getId() == n2.getId())
			return null;
		
		int clusterNum = n1.getClusterNum();
		List<List<List<List<node>>>> routesInterCluster = clusters.get(clusterNum).getRouteBtwBdry();
		List<Integer> route = new ArrayList<>();
		
		double time = currentTime;
		double timeDiff = main.storeTimeDiff;
		int index = (int) (time/timeDiff);
		
		node earlierParent = (routesInterCluster.get(index).get(n1.getClusterId()).get(n2.getClusterNum()).get(n2.getClusterId()));
		
		List<Integer> otherRoute = getRouteInterCluster(n1, earlierParent, currentTime);
		
		route.add(earlierParent.getId()+1);
		
		if(otherRoute!=null) {
			route.addAll(0,otherRoute);
		}
		
		return route;
	}
	
	static List<Integer> getRoutesBetweenNodesAtTime(node n1 , node n2, double currentTime) {
		
//		System.out.println("getRouteBtwNodesAtTime called for nodes: "+n1.getStringId() + ", "+n2.getStringId()  + " at time: "+currentTime);

		List<Integer> overallRoute = new ArrayList<>();
		
		cluster cluster1 = clusters.get(n1.getClusterNum());
		cluster cluster2 = clusters.get(n2.getClusterNum());
		
		if(cluster1.getNum()==cluster2.getNum()) {			//belong to same cluster
			
			System.out.println(n1.getStringId() + " and " + n2.getStringId() +" belong to same cluster");
			
			overallRoute = getRouteIntraCluster(n1,n2,currentTime);
		}
		else {

			List<node> bdryPointsCluster1 = cluster1.getBdry_points();
			List<node> bdryPointsCluster2 = cluster2.getBdry_points();
			
			if(n1.isBdryPt()) {
				if(n2.isBdryPt()) {			//both are bdry points
//					return cluster1.getWeightedTime(currentTime, n1.getClusterId(), n2.getClusterNum(), n2.getClusterId(), cluster1.getTimeBtwBdry());
					System.out.println(n1.getStringId() + " is bdry pt and " + n2.getStringId() +" is bdry pt");
//					return getRouteInterCluster(n1, n2, currentTime);
					overallRoute = getRouteInterCluster(n1, n2, currentTime);
				}
				else {	//n1 bdry but n2 not
//					System.out.println(n1.getStringId() + " is bdry pt and " + n2.getStringId() +" is not");
					node min_node = null;
					double min_time = Double.POSITIVE_INFINITY, min_time_N1ToBdry2 = Double.POSITIVE_INFINITY;
					for(node n: bdryPointsCluster2) {
						double timeFromN1ToBdry2 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), n.getClusterNum(), n.getClusterId(), cluster1.getTimeBtwBdry());
						double timeFromBdry2ToN2 = cluster2.getWeightedTime(currentTime+timeFromN1ToBdry2, n.getClusterId(), -1, n2.getClusterId(), cluster2.getTimeIntraCluster());
						
						if(min_time > timeFromN1ToBdry2 + timeFromBdry2ToN2) {
							min_time = timeFromN1ToBdry2 + timeFromBdry2ToN2;
							min_node = n;
							min_time_N1ToBdry2 = timeFromN1ToBdry2;
						}
					}
					
					if(min_node==null) {
						return null;
					}
					overallRoute.addAll(getRouteInterCluster(n1, min_node, currentTime));
					overallRoute.addAll(getRouteIntraCluster(min_node, n2, currentTime + min_time_N1ToBdry2));
////					overallRoute.add(n2.getId()+1);
//					return overallRoute;
				}
			}
			else {
				if(n2.isBdryPt()) {		//n2 is bdry but n1 not
//					System.out.println(n1.getStringId() + " is not bdry pt and " + n2.getStringId() +" is bdry pt");
//					List<Integer> overallRoute = new ArrayList<>();
					double min_time = Double.POSITIVE_INFINITY, min_time_N1ToBdry1 = Double.POSITIVE_INFINITY;
					node min_node = null;

					for(node n: bdryPointsCluster1) {
						double timeFromN1ToBdry1 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), -1, n.getClusterId(), cluster1.getTimeIntraCluster());
						double timeBdry1ToN2 = cluster1.getWeightedTime(currentTime + timeFromN1ToBdry1, n.getClusterId(), n2.getClusterNum(), n2.getClusterId(), cluster1.getTimeBtwBdry());
						
						if(min_time > timeFromN1ToBdry1 + timeBdry1ToN2) {
							min_time = timeFromN1ToBdry1  +timeBdry1ToN2;
							min_node = n;
							min_time_N1ToBdry1 = timeFromN1ToBdry1;
						}
					}
					if(min_node==null) {
						return null;
					}
					overallRoute.addAll(getRouteIntraCluster(n1, min_node, currentTime));
					overallRoute.addAll(getRouteInterCluster(min_node, n2, currentTime + min_time_N1ToBdry1));
//					return overallRoute;
				}
				else {			//neither is a bdry points
//					System.out.println(n1.getStringId() + " and " + n2.getStringId() +" are not bdry pts");
					double min_time = Double.POSITIVE_INFINITY, min_time_N1ToBdry1 = Double.POSITIVE_INFINITY, min_time_Bdry1ToBdry2 = Double.POSITIVE_INFINITY, min_time_Bdry1ToBdry2_temp = Double.POSITIVE_INFINITY;
					node min_node_cluster1 = null, min_node_cluster2 = null, min_node_cluster2_temp = null;
//					List<Integer> overallRoute = new ArrayList<>();

					for(node n: bdryPointsCluster1) {
						double timeFromN1ToBdry1 = cluster1.getWeightedTime(currentTime, n1.getClusterId(), -1, n.getClusterId(), cluster1.getTimeIntraCluster());
						
						double min_time2= Double.POSITIVE_INFINITY;
						for(node m: bdryPointsCluster2) {
							double timeFromBdry1ToBdry2 = cluster1.getWeightedTime(currentTime + timeFromN1ToBdry1, n.getClusterId(), m.getClusterNum(), m.getClusterId(), cluster1.getTimeBtwBdry());
							double timeFromBdry2ToN2 = cluster2.getWeightedTime(currentTime + timeFromN1ToBdry1 + timeFromBdry1ToBdry2, m.getClusterId(), -1, n2.getClusterId(), cluster2.getTimeIntraCluster());
							
							if(min_time2  > timeFromBdry1ToBdry2 + timeFromBdry2ToN2) {
								min_time2 = timeFromBdry1ToBdry2 + timeFromBdry2ToN2;
								min_node_cluster2_temp = m;
								min_time_Bdry1ToBdry2_temp = timeFromBdry1ToBdry2;
							}
						}
						
						if(min_time > timeFromN1ToBdry1 + min_time2) {
							min_time = timeFromN1ToBdry1 + min_time2;
							min_node_cluster1 = n;
							min_node_cluster2 = min_node_cluster2_temp;
							min_time_N1ToBdry1 = timeFromN1ToBdry1;
							min_time_Bdry1ToBdry2 = min_time_Bdry1ToBdry2_temp;
						}
					}
					if(min_node_cluster1==null || min_node_cluster2 == null) {
						return null;
					}
//					System.out.println("calling route intracluster called for nodes: "+n1.getStringId() + ", "+min_node_cluster1.getStringId());
					overallRoute.addAll(getRouteIntraCluster(n1, min_node_cluster1, currentTime));
//					System.out.println("calling route intercluster called for nodes: "+min_node_cluster1.getStringId() + ", "+min_node_cluster2.getStringId());
					overallRoute.addAll(getRouteInterCluster(min_node_cluster1, min_node_cluster2, currentTime + min_time_N1ToBdry1));
//					System.out.println("calling route intracluster called for nodes: "+min_node_cluster2.getStringId() + ", "+n2.getStringId());
					overallRoute.addAll(getRouteIntraCluster(min_node_cluster2, n2, currentTime + min_time_N1ToBdry1 + min_time_Bdry1ToBdry2));
//					return overallRoute;
				}
			}
		}
		
		overallRoute.add(n2.getId()+1);
		return overallRoute;
	}

	
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
		
		try {
			FileWriter writer = new FileWriter("clustersDescrption.txt");
			for(cluster c: clusters) {
				writer.write(c.toString());
			}
			writer.close();
//			BufferedWriter br = new BufferedWriter(new FileWriter("clustersDescription.csv"));
//			StringBuilder sb = new StringBuilder();
//			for (String element : array) {
//			 sb.append(element);
//			 sb.append(",");
//			}
//
//			br.write(sb.toString);
//			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
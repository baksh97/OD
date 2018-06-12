import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class cluster{
	private int num;
	private int num_nodes;
	private List<node> bdryPoints;
	private List<node> innerNodes;
	private List<List<List<Double>>> timeIntraCluster;
	private List<List<List<List<Double>>>> timeBtwBdry;

	public int getNum() {
		return num;
	}
	
	public void setNUm(int num) {
		this.num = num;
	}
	
	public List<node> getInnerNodes() {
		return innerNodes;
	}

	public void setInnerNodes(List<node> innerNodes) {
		this.innerNodes = innerNodes;
	}
//	public List<List<List<Double>>> getTimeFromBdryToNode() {
//		return timeFromBdryToNode;
//	}
	
	public int getNum_nodes() {
		return num_nodes;
	}

	public void setNum_nodes(int num_nodes) {
		this.num_nodes = num_nodes;
	}

	public double getWeightedTime(double currentTime,int id1, int clusterNum ,int id2, Object obj) {
		
		if(clusterNum!=-1) {
//			id2 += clusterNum*main.num_clusters;
			List<List<List<List<Double>>>> times = (List<List<List<List<Double>>>>) obj;
			
			double timeWindow = main.timeWindow;
			int index = (int) (currentTime/timeWindow);
			
			double earlierTime = (times.get(index).get(id1).get(clusterNum).get(id2));
			
			double weight1 = (timeWindow - currentTime%timeWindow)/timeWindow,
					weight2 = (currentTime%timeWindow)/timeWindow;
			if(index==times.size()-1)
				return earlierTime;
			else {
				double laterTime = (times.get(index+1).get(id1).get(clusterNum).get(id2));
				return earlierTime*weight1 + laterTime*weight2;
			}
		}
		else {
			List<List<List<Double>>> times = (List<List<List<Double>>>) obj;
			double timeWindow = main.timeWindow;
			int index = (int) (currentTime/timeWindow);
			
			double earlierTime = (times.get(index).get(id1).get(id2));
			
			double weight1 = (timeWindow - currentTime%timeWindow)/timeWindow,
					weight2 = (currentTime%timeWindow)/timeWindow;
			if(index==times.size()-1)
				return earlierTime;
			else {
				double laterTime = (times.get(index+1).get(id1).get(id2));
				return earlierTime*weight1 + laterTime*weight2;
			}
		}

	}

	public void setTimeBtwBdry(List<cluster> clusters, List<node> nodes) {			//inter cluster bdry
		timeBtwBdry = new ArrayList<>();
		
		PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
		
		List<Boolean[]> visited = new ArrayList<>();
		
		node currentNode;		
		
		for(double currentTime=0;currentTime<main.endTime;currentTime+= main.timeWindow) {
			List<List<List<Double>>> timeForCurrentTime = new ArrayList<>();
			for(node n: bdryPoints){			//setting times for this node in the cluster
			
				for(cluster c : clusters) {
					boolean cluster_visited[] = new boolean[c.bdryPoints.size()];
					for(node n1: c.bdryPoints) {
						n1.setTempTime(Double.POSITIVE_INFINITY);
						unvisited.add(n1);
						cluster_visited[n1.getClusterId()]=false;
					}
				}
				
//				for(int i=0;i<visited.length;i++) {
//					visited[i] = false;
//				}
				
				currentNode = n;
				n.setTempTime(0);
			
				while(!unvisited.isEmpty()) {
					currentNode = unvisited.peek();
					for(edge e: n.getEdges()) {									//for direct edges from the current node
						node n1 = nodes.get(e.getOtherEnd(n.getId()));
						if(n1.getClusterNum()!= n.getClusterNum() && !visited.get(n1.getClusterNum())[n1.getClusterId()]){		//check only if node belongs to other cluster; otherwise if it is inner node no need to check and if it is a bdry pt belonging to same cluster, it will be checked below
							double timeForEdge = e.getTime_from_speed(currentTime);
							if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
								n1.setTempTime(currentNode.getTempTime()+timeForEdge);
							}
						}
					}
					
					for(node n1: bdryPoints) {									//for indirect routes from the currentNode to the other bdry points belonging to same cluster
						if(!visited.get(n1.getClusterNum())[n1.getClusterId()]) {	//why waste time if already visited
							int timeIndex = (int)((currentTime+n.getTempTime())/main.timeWindow);
							double timeForEdge = getWeightedTime(currentTime+n.getTempTime(), n.getClusterId(), -1, n1.getClusterId(), timeIntraCluster);
							if(n.getTempTime() +  timeForEdge< n1.getTempTime()) {
								n1.setTempTime(n.getTempTime() + timeForEdge);
							}
						}
					}
					
					visited.get(currentNode.getClusterNum())[currentNode.getClusterId()] = true;
				}
				

				List<List<Double>> timeForCurrentNode = new ArrayList<>();				
				for(cluster c: clusters) {
					List<Double> timeForCluster = new ArrayList<>();
					for(node n1: c.bdryPoints) {
						timeForCluster.add(n1.getTempTime());
					}
					timeForCurrentNode.add(timeForCluster);
				}
				timeForCurrentTime.add(timeForCurrentNode);
			}
			timeBtwBdry.add(timeForCurrentTime);
		}
	}
	
	public void setTimeIntraCluster(List<node> nodes) {
		
		timeIntraCluster = new ArrayList<>();
		
		PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
		
		boolean visited[] = new boolean[num_nodes];
		
		
		node currentNode;
		List<node> allNodes = new ArrayList<node>(innerNodes);
		allNodes.addAll(bdryPoints);
		
		
		for(double currentTime=0;currentTime<main.endTime;currentTime+= main.timeWindow) {
			List<List<Double>> timeForCurrentTime = new ArrayList<>();
			for(node n: allNodes){			//setting times for this node in the cluster
			
				for(node n1 :allNodes) {
					n1.setTempTime(Double.POSITIVE_INFINITY);
					unvisited.add(n1);
				}
				
				for(int i=0;i<visited.length;i++) {
					visited[i] = false;
				}
				
				currentNode = n;
				n.setTempTime(0);
			
				while(!unvisited.isEmpty()) {
					currentNode = unvisited.peek();
					for(edge e: n.getEdges()) {
						node n1 = nodes.get(e.getOtherEnd(n.getId()));
						if(n1.getClusterNum() == num && !visited[n1.getClusterId()]){
							double timeForEdge = e.getTime_from_speed(currentTime);
							if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
								n1.setTempTime(currentNode.getTempTime()+timeForEdge);
							}
						}
					}
					visited[currentNode.getClusterId()] = true;
//					current
				}
				
				List<Double> timeForCurrentNode = new ArrayList<>();
				for(node n1: allNodes) {
					timeForCurrentNode.add(n1.getTempTime());
//					n1.setTempTime(0);
				}
				timeForCurrentTime.add(timeForCurrentNode);
			}
			timeIntraCluster.add(timeForCurrentTime);
		}
	}

	public List<List<List<List<Double>>>> getTimeBtwBdry() {
		return timeBtwBdry;
	}
	
	public List<List<List<Double>>> getTimeIntraCluster() {
		return timeIntraCluster;
	}
	
	List<node> getBdry_points(){
		return bdryPoints;
	}
	
	void setBdry_points(List<node> bdryPoints) {
		this.bdryPoints = bdryPoints;
	}
		
	public static Comparator<node> dist_comparator = new Comparator<node>() {
		
		@Override
		public int compare(node o1, node o2) {
			double dist1 = o1.getTempTime(),dist2 = o2.getTempTime();
			if(dist1==dist2)
				return 0;
			return (o1.getTempTime() < o2.getTempTime()) ? -1:1;
		}
	};
}
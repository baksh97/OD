import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class cluster{
	private int num;
	private int num_nodes=0;
	private List<node> bdryPoints = new ArrayList<>();
	private List<node> innerNodes = new ArrayList<>();
	private List<List<List<Double>>> timeIntraCluster;
	private List<List<List<List<Double>>>> timeBtwBdry;

	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public List<node> getInnerNodes() {
		return innerNodes;
	}

	public void setInnerNodes() {				//adding the number of bdry points to the ids of each inner point
		if(innerNodes.isEmpty())return;
		
//		System.out.println("number of bdry pts: "+bdryPoints.size() + " and number of inner pts: "+innerNodes.size());
		
		int num_bdry = bdryPoints.size();
		
		for(node n: innerNodes) {
			n.setClusterId(n.getClusterId() + num_bdry);
		}
		
	}
//	public List<List<List<Double>>> getTimeFromBdryToNode() {
//		return timeFromBdryToNode;
//	}
	
	public void addInnerNode(node n) {
		innerNodes.add(n);
		n.setClusterId(innerNodes.size()-1);
		num_nodes++;
	}
	
	public void addBdryNode(node n) {
		bdryPoints.add(n);
		n.setClusterId(bdryPoints.size()-1);
		num_nodes++;
	}
	
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
		
		
		List<boolean[]> visited = new ArrayList<>();
//		for(boolean[] b: visited) b = new boolean[]
		node currentNode;		
		
		for(double currentTime=0;currentTime<main.endTime;currentTime+= main.timeWindow) {
			List<List<List<Double>>> timeForCurrentTime = new ArrayList<>();
			for(node n: bdryPoints){			//setting times for this node in the cluster

				PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
			
				for(cluster c : clusters) {
					boolean cluster_visited[] = new boolean[c.bdryPoints.size()];
					for(node n1: c.bdryPoints) {
						if(n1!=n)
							n1.setTempTime(Double.POSITIVE_INFINITY);
						else
							n1.setTempTime(0);
						unvisited.add(n1);
						
						cluster_visited[n1.getClusterId()]=false;
						visited.add(cluster_visited);
					}
				}
				
//				for(int i=0;i<visited.length;i++) {
//					visited[i] = false;
//				}
				
				currentNode = n;
//				n.setTempTime(0);
				
				System.out.println("n: "+currentNode.getStringId());
				System.out.println();

				while(!unvisited.isEmpty()) {
					currentNode = unvisited.peek();
					System.out.println("currentNode: "+currentNode.getStringId());
					for(edge e: currentNode.getEdges()) {									//for direct edges from the current node
						node n1 = nodes.get(e.getOtherEnd(n.getId()));
						if(n1.getClusterNum()!= n.getClusterNum()) {		//check only if node belongs to other cluster; otherwise if it is inner node no need to check and if it is a bdry pt belonging to same cluster, it will be checked below
							if(!visited.get(n1.getClusterNum())[n1.getClusterId()]){
								double timeForEdge = e.getTime_from_speed(currentTime+currentNode.getTempTime());
								if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
									n1.setTempTime(currentNode.getTempTime()+timeForEdge);
								}
							}
						}
					}
					
					for(node n1: bdryPoints) {									//for indirect routes from the currentNode to the other bdry points belonging to same cluster
						if(!visited.get(n1.getClusterNum())[n1.getClusterId()]) {	//why waste time if already visited
//							int timeIndex = (int)((currentTime+currentNode.getTempTime())/main.timeWindow);
							double timeForEdge = getWeightedTime(currentTime+currentNode.getTempTime(), n.getClusterId(), -1, n1.getClusterId(), timeIntraCluster);
							if(n.getTempTime() +  timeForEdge< n1.getTempTime()) {
								n1.setTempTime(currentNode.getTempTime() + timeForEdge);
							}
						}
					}
					
					visited.get(currentNode.getClusterNum())[currentNode.getClusterId()] = true;
					unvisited.remove();
				}
				

				List<List<Double>> timeForCurrentNode = new ArrayList<>();				
				for(cluster c: clusters) {
					List<Double> timeForCluster = new ArrayList<>();
					for(node n1: c.bdryPoints) {
						timeForCluster.add(currentTime+n1.getTempTime());
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
		
		
		boolean visited[] = new boolean[num_nodes];
		
		
		node currentNode;
		List<node> allNodes = new ArrayList<node>(bdryPoints);		//all nodes is the list of all nodes present in the cluster
		allNodes.addAll(innerNodes);
		
		if(allNodes.size()!=num_nodes) {
			System.err.println("Some error in combining inner and bdry nodes");
		}
		
		for(double currentTime=0;currentTime<main.endTime;currentTime+= main.timeWindow) {
//			System.out.println("intra for time: "+currentTime+"==================");
			List<List<Double>> timeForCurrentTime = new ArrayList<>();
			for(node n: allNodes){			//setting times for this node in the cluster

				PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
		
				for(node n1 :allNodes) {
					if(n1!=n)
						n1.setTempTime(Double.POSITIVE_INFINITY);
					else
						n1.setTempTime(0);
					unvisited.add(n1);
				}
				

				for(int i=0;i<visited.length;i++) {
					visited[i] = false;
				}
				
				currentNode = n;
				currentNode.setTempTime(0);

				
//				System.out.println("error checking: printing unvisited:");
//				for(node unv: unvisited)System.out.print(unv.getStringId()+":"+unv.getTempTime()+", ");
//				System.out.println();
				
//				System.out.println("\ndoing for node: "+n.getStringId()+" with temp time: "+n.getTempTime());

				if(unvisited.isEmpty()) {
					System.err.println("unvisited is empty at the starting itself");
				}
			
				while(!unvisited.isEmpty()) {
					currentNode = unvisited.peek();
					
					if(currentNode.getTempTime()==Double.POSITIVE_INFINITY) {
//						System.out.println(currentNode.getStringId()+" is not reachable from "+n.getStringId());
						break;
					}
//					System.out.println("current node: "+currentNode.getStringId()+ " with temp time: "+currentNode.getTempTime());
					
					for(edge e: currentNode.getEdges()) {
						
						node n1 = nodes.get(e.getOtherEnd(currentNode.getId()));
						
//						System.out.println("node n1: "+n1.getStringId());
//						if(visited[n1.getClusterId()])System.out.println("already visited it");
						
						if(n1.getClusterNum() == num && !visited[n1.getClusterId()]){
							
							double timeForEdge = e.getTime_from_speed(currentTime);
							
//							System.out.println("time to travel edge b/w "+(currentNode.getId()+1) + " and "+(n1.getId()+1)+" is: "+timeForEdge);
							
							if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
//								System.out.print("updatig temp time of: "+n1.getStringId()+" from "+n1.getTempTime()+" to: ");
								n1.setTempTime(currentNode.getTempTime()+timeForEdge);
//								System.out.println(n1.getTempTime());
							}
						}
					}
					visited[currentNode.getClusterId()] = true;
					unvisited.remove();
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
	
	public String toString() {
		String s= "";
		s+= "CLUSTER "+Integer.toString(num)+" ==================================\n";
		s += "number of inner nodes: "+Integer.toString(innerNodes.size())+"\n";
		s += "number of bdry nodes: "+Integer.toString(bdryPoints.size())+"\n";
		s+= "Bdry nodes: ";
		for(node n: bdryPoints) s+= Integer.toString(n.getId()+1) + " ";
		s+= "\nInner nodes: ";
		for(node n: innerNodes) s+= Integer.toString(n.getId()+1) + " ";
		
		s+= "\n";
		
		if(!timeIntraCluster.isEmpty()) {
			for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.timeWindow) {
				
				List<List<Double>> timeForCurrentTime = timeIntraCluster.get((int)(currentTime/main.timeWindow));
				s += "Time: "+Double.toString(currentTime)+"\n";
				for(node n: bdryPoints) s+= "\t"+Integer.toString(n.getId()+1)+"("+Integer.toString(n.getClusterId()+1)+")";
				for(node n: innerNodes) s+= "\t"+Integer.toString(n.getId()+1)+"("+Integer.toString(n.getClusterId()+1)+")";
				s+="\n";
				
				boolean writeInnerNow = false;
				for(int i=0,j=0;i<timeForCurrentTime.size();i++,j++) {
					if(j==bdryPoints.size() && i==j) {
						writeInnerNow = true;
						j=0;
					}
					
					if(writeInnerNow)
						s += Integer.toString(innerNodes.get(j).getId()+1) + "(" + Integer.toString(innerNodes.get(j).getClusterId()+1)+"):" + "\t";
					else
						s += Integer.toString(bdryPoints.get(j).getId()+1) + "(" + Integer.toString(bdryPoints.get(j).getClusterId()+1)+")" + "\t";
					
					for(double time: timeForCurrentTime.get(i))
						s += Double.toString(time) + "\t";
					s+="\n";
				}
			}
		}
		
		if(!timeBtwBdry.isEmpty()) {
			for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.timeWindow) {
				
				List<List<List<Double>>> timeForCurrentTime = timeBtwBdry.get((int)(currentTime/main.timeWindow));
				s += "Time: "+Double.toString(currentTime)+"\n";
				
				List<cluster> c = main.clusters;
				
				for(cluster c1: c) {
					if(c1.getNum()!= num) {
						s+= "\tCluster: "+c1.getNum()+"\n";
						s+="\t\t";					
						for(node n2: c1.getBdry_points()) {
							s+=n2.getStringId()+"\t";
						}
						s+="\n";
						
						for(node n: bdryPoints) {
							s+="\t"+n.getStringId();
							for(node n2: c1.getBdry_points()) {
								s+="\t"+timeForCurrentTime.get(n.getClusterId()).get(c1.getNum()).get(n2.getClusterId());
							}
							s += "\n";
						}
						s+="\n";
					}
				}
				
				
				s+="\n";
				
//				boolean writeInnerNow = false;
//				for(int i=0,j=0;i<timeForCurrentTime.size();i++,j++) {
//					if(j==bdryPoints.size() && i==j) {
//						writeInnerNow = true;
//						j=0;
//					}
//					
//					if(writeInnerNow)
//						s += Integer.toString(innerNodes.get(j).getId()+1) + "(" + Integer.toString(innerNodes.get(j).getClusterId()+1)+"):" + "\t";
//					else
//						s += Integer.toString(bdryPoints.get(j).getId()+1) + "(" + Integer.toString(bdryPoints.get(j).getClusterId()+1)+")" + "\t";
//					
//					for(double time: timeForCurrentTime.get(i))
//						s += Double.toString(time) + "\t";
//					s+="\n";
//				}
			}
		}
		return s;
	}
}
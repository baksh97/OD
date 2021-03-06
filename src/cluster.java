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
	private List<List<List<node>>> routeIntraCluster;
	private List<List<List<List<Double>>>> timeBtwBdry;
	private List<List<List<List<node>>>> routeBtwBdry;

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
				
		int num_bdry = bdryPoints.size();
		
		for(node n: innerNodes) {
			n.setClusterId(n.getClusterId() + num_bdry);
		}
	}
	
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
		double time = currentTime;	//time should not be greater than or equal to endtime
		if(clusterNum!=-1) {
			List<List<List<List<Double>>>> times = (List<List<List<List<Double>>>>) obj;
			
			double timeDiff = main.storeTimeDiff;
			int index = (int) (time/timeDiff);
			
			double earlierTime = (times.get(index).get(id1).get(clusterNum).get(id2));
			
			double weight1 = (timeDiff - time%timeDiff)/timeDiff,
					weight2 = 1- weight1;
			if(index==times.size()-1)
				return earlierTime;
			else {
				double laterTime = (times.get(index+1).get(id1).get(clusterNum).get(id2));
				return earlierTime*weight1 + laterTime*weight2;
			}
		}
		else {
			List<List<List<Double>>> times = (List<List<List<Double>>>) obj;
			double timeDiff = main.storeTimeDiff;
			int index = (int) (time/timeDiff);
			double earlierTime = (times.get(index).get(id1).get(id2));
			
			double weight1 = (timeDiff - time%timeDiff)/timeDiff,
					weight2 = (time%timeDiff)/timeDiff;
			if(index==times.size()-1)
				return earlierTime;
			else {
				double laterTime = (times.get(index+1).get(id1).get(id2));
				return earlierTime*weight1 + laterTime*weight2;
			}
		}

	}

	public void setTimeBtwBdry(List<node> nodes) {			//inter cluster bdry
		timeBtwBdry = new ArrayList<>();
		routeBtwBdry = new ArrayList<>();
		
		List<cluster> clusters = main.clusters;
		node currentNode;		
		
		for(double currentTime=main.startTime; currentTime<main.endTime; currentTime+= main.storeTimeDiff) {
			List<List<List<Double>>> timeForCurrentTime = new ArrayList<>();
			List<List<List<node>>> routeForCurrentTime = new ArrayList<>();
			
			for(node n: bdryPoints){			//setting times for this node in the cluster
			
				PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
				int total_visited = 0;
				List<boolean[]> visited = new ArrayList<>();
	
				for(cluster c : clusters) {
					boolean cluster_visited[] = new boolean[c.getBdry_points().size()];
					for(node n1: c.bdryPoints) {
						n1.setParent(null);
						if(n1!=n)
							n1.setTempTime(Double.POSITIVE_INFINITY);
						else
							n1.setTempTime(0);
						unvisited.add(n1);
						
						cluster_visited[n1.getClusterId()]=false;
						visited.add(c.getNum(), cluster_visited);
					}
				}

				while(!unvisited.isEmpty()) {
					currentNode = unvisited.remove();
					if(currentNode.getTempTime()==Double.POSITIVE_INFINITY) {
						System.out.println(currentNode.getStringId()+" is not reachable from "+n.getStringId());
						break;
					}
					else if(currentTime + currentNode.getTempTime() > main.endTime){
//						System.err.println("End time reached.."+ Integer.toString(nodes.size() - total_visited)+" nodes not reached from "+Integer.toString(n.getId()));
						break;
					}
					
					for(edge e: currentNode.getEdges()) {									//for direct edges from the current node
						
						node n1 = nodes.get(e.getEnd());
						
						
						if(n1.getClusterNum()!= currentNode.getClusterNum()) {		//check only if node belongs to other cluster; otherwise if it is inner node no need to check and if it is a bdry pt belonging to same cluster, it will be checked below
							if(!visited.get(n1.getClusterNum())[n1.getClusterId()]){
								double timeForEdge = e.getTime_from_speed(currentTime+currentNode.getTempTime());
								if(timeForEdge + currentTime + currentNode.getTempTime() < main.endTime){
									if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
										n1.setTempTime(currentNode.getTempTime()+timeForEdge);
										
										//updating parent
										n1.setParent(currentNode);
										unvisited.remove(n1);
										unvisited.add(n1);
									}
								}
							}
						}
					}
					
					for(node n1: clusters.get(currentNode.getClusterNum()).getBdry_points()) {									//for indirect routes from the currentNode to the other bdry points belonging to same cluster
						if(!visited.get(n1.getClusterNum())[n1.getClusterId()]) {	//why waste time if already visited
							double timeForEdge = getWeightedTime(currentTime+currentNode.getTempTime(), currentNode.getClusterId(), -1, n1.getClusterId(), clusters.get(currentNode.getClusterNum()).getTimeIntraCluster());
							
							if(currentTime + currentNode.getTempTime() + timeForEdge < main.endTime){
								if(currentNode.getTempTime() +  timeForEdge< n1.getTempTime()) {
									n1.setTempTime(currentNode.getTempTime() + timeForEdge);
									
									n1.setParent(currentNode);
									unvisited.remove(n1);
									unvisited.add(n1);
								}
							}
						}
					}					
					visited.get(currentNode.getClusterNum())[currentNode.getClusterId()] = true;
					total_visited++;
				}
				

				List<List<Double>> timeForCurrentNode = new ArrayList<>();				
				List<List<node>> routeForCurrentNode = new ArrayList<>();
				
				for(cluster c: clusters) {
					List<Double> timeForCluster = new ArrayList<>();
					List<node> routeForCluster = new ArrayList<>();
					
					for(node n1: c.bdryPoints) {
						timeForCluster.add(n1.getTempTime());
						routeForCluster.add(n1.getParent());
					}
					timeForCurrentNode.add(timeForCluster);
					routeForCurrentNode.add(routeForCluster);
				}
				timeForCurrentTime.add(timeForCurrentNode);
				routeForCurrentTime.add(routeForCurrentNode);
			}
			timeBtwBdry.add(timeForCurrentTime);
			routeBtwBdry.add(routeForCurrentTime);
		}
	}
	
	public void setTimeIntraCluster(List<node> nodes) {
		
		timeIntraCluster = new ArrayList<>();
		routeIntraCluster = new ArrayList<>();
		
		boolean visited[] = new boolean[num_nodes];
		
		
		node currentNode;
		List<node> allNodes = new ArrayList<node>(bdryPoints);		//all nodes is the list of all nodes present in the cluster
		allNodes.addAll(innerNodes);
		
		if(allNodes.size()!=num_nodes) {
			System.err.println("Some error in combining inner and bdry nodes");
		}
		
		for(double currentTime=main.startTime; currentTime<main.endTime; currentTime+= main.storeTimeDiff) {
			
//			System.out.println("Current Time: "+currentTime);
			
//			System.out.println("intra for time: "+currentTime+"==================");
			List<List<Double>> timeForCurrentTime = new ArrayList<>();
			List<List<node>> routeForCurrentTime = new ArrayList<>();
			
			for(node n: allNodes){			//setting times for this node in the cluster

				PriorityQueue<node> unvisited = new PriorityQueue<>(dist_comparator);
				int total_visited = 0;
				for(node n1 :allNodes) {
					n1.setParent(null);
					if(n1!=n)
						n1.setTempTime(Double.POSITIVE_INFINITY);
					else
						n1.setTempTime(0);
					unvisited.add(n1);
				}
				

				for(int i=0;i<visited.length;i++) {
					visited[i] = false;
				}
				
//				currentNode = n;
//				currentNode.setTempTime(0);

				
//				System.out.println("error checking: printing unvisited:");
//				for(node unv: unvisited)System.out.print(unv.getStringId()+":"+unv.getTempTime()+", ");
//				System.out.println();
				
//				System.out.println("\ndoing for node: "+n.getStringId()+" with temp time: "+n.getTempTime());

				if(unvisited.isEmpty()) {
					System.err.println("unvisited is empty at the starting itself");
				}
				
//				System.out.println("\n\nDoing for : "+n.getStringId());
				
				while(!unvisited.isEmpty()) {
					currentNode = unvisited.peek();
//					System.out.println("Current Node: "+currentNode.getStringId());
					if(currentNode.getTempTime()==Double.POSITIVE_INFINITY) {
						System.out.println(currentNode.getStringId()+" is not reachable from "+n.getStringId());
						break;
					}
					else if(currentTime + currentNode.getTempTime() > main.endTime){
						System.err.println("End time reached.."+ Integer.toString(allNodes.size() - total_visited)+" nodes not reached from "+(n.getStringId()));
						break;
					}
//					System.out.println("current node: "+currentNode.getStringId()+ " with temp time: "+currentNode.getTempTime());
					
					for(edge e: currentNode.getEdges()) {
						
						node n1 = nodes.get(e.getEnd());
						
//						System.out.println("other end of edge n1: "+n1.getStringId());
//						if(visited[n1.getClusterId()])System.out.println("already visited it");
						
						if(n1.getClusterNum() == num) {
							if(!visited[n1.getClusterId()]){
								double timeForEdge = e.getTime_from_speed(currentTime + currentNode.getTempTime());
								
//								System.out.println("time to travel edge b/w "+currentNode.getStringId() + " and "+n1.getStringId()+" at "+ (currentTime + currentNode.getTempTime())+ "is: "+timeForEdge);
								if(timeForEdge + currentTime + currentNode.getTempTime() < main.endTime){
									if(currentNode.getTempTime() + timeForEdge < n1.getTempTime()) {
		//									System.out.print("updatig temp time of: "+n1.getStringId()+" from "+n1.getTempTime()+" to: ");
										n1.setTempTime(currentNode.getTempTime()+timeForEdge);
										
										n1.setParent(currentNode);
									}
									unvisited.remove(n1);
									unvisited.add(n1);
//									System.out.println(n1.getTempTime());
								}	
							}
						}
					}
					
//					System.out.println(currentNode.getStringId()+" is currently visited with total visited: "+Integer.toString(total_visited));
					
					visited[currentNode.getClusterId()] = true;
					unvisited.remove();
					total_visited++;
//					current
				}
				
				List<Double> timeForCurrentNode = new ArrayList<>();
				List<node> routeForCurrentNode = new ArrayList<>();
				
				for(node n1: allNodes) {
					timeForCurrentNode.add(n1.getTempTime());
					routeForCurrentNode.add(n1.getParent());
//					n1.setTempTime(0);
				}
				timeForCurrentTime.add(timeForCurrentNode);
				routeForCurrentTime.add(routeForCurrentNode);
			}
			timeIntraCluster.add(timeForCurrentTime);
			routeIntraCluster.add(routeForCurrentTime);
		}
	}

	public List<List<List<node>>> getRouteIntraCluster() {
		return routeIntraCluster;
	}

	public List<List<List<List<node>>>> getRouteBtwBdry() {
		return routeBtwBdry;
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
		
		s+= "\n\n";
		s+= "Time for Intra Cluster Nodes:\n";
		if(timeIntraCluster!=null) {
			if(!timeIntraCluster.isEmpty()) {
				for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.storeTimeDiff) {
					
					List<List<Double>> timeForCurrentTime = timeIntraCluster.get((int)(currentTime/main.storeTimeDiff));
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
		}
		
		s+= "\n\n";
		s+= "Time b/w Bdry Points:\n";

		if(timeBtwBdry!=null) {
			if(!timeBtwBdry.isEmpty()) {
				for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.storeTimeDiff) {
					
					List<List<List<Double>>> timeForCurrentTime = timeBtwBdry.get((int)(currentTime/main.storeTimeDiff));
					s += "Time: "+Double.toString(currentTime)+"\n";
					
					List<cluster> c = main.clusters;
					StringBuilder sb= new StringBuilder();
					for(cluster c1: c) {
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
					s+="\n";
				}
			}
		}
		return s;
	}
	
	
//	public StringBuilder getStringBuilder() {
//		String s= "";
//		s+= "CLUSTER "+Integer.toString(num)+" ==================================\n";
//		s += "number of inner nodes: "+Integer.toString(innerNodes.size())+"\n";
//		s += "number of bdry nodes: "+Integer.toString(bdryPoints.size())+"\n";
//		s+= "Bdry nodes: ";
//		for(node n: bdryPoints) s+= Integer.toString(n.getId()+1) + " ";
//		s+= "\nInner nodes: ";
//		for(node n: innerNodes) s+= Integer.toString(n.getId()+1) + " ";
//		
//		s+= "\n\n";
//		s+= "Time for Intra Cluster Nodes:\n";
//		if(timeIntraCluster!=null) {
//			if(!timeIntraCluster.isEmpty()) {
//				for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.storeTimeDiff) {
//					
//					List<List<Double>> timeForCurrentTime = timeIntraCluster.get((int)(currentTime/main.storeTimeDiff));
//					s += "Time: "+Double.toString(currentTime)+"\n";
//					for(node n: bdryPoints) s+= "\t"+Integer.toString(n.getId()+1)+"("+Integer.toString(n.getClusterId()+1)+")";
//					for(node n: innerNodes) s+= "\t"+Integer.toString(n.getId()+1)+"("+Integer.toString(n.getClusterId()+1)+")";
//					s+="\n";
//					
//					boolean writeInnerNow = false;
//					for(int i=0,j=0;i<timeForCurrentTime.size();i++,j++) {
//						if(j==bdryPoints.size() && i==j) {
//							writeInnerNow = true;
//							j=0;
//						}
//						
//						if(writeInnerNow)
//							s += Integer.toString(innerNodes.get(j).getId()+1) + "(" + Integer.toString(innerNodes.get(j).getClusterId()+1)+"):" + "\t";
//						else
//							s += Integer.toString(bdryPoints.get(j).getId()+1) + "(" + Integer.toString(bdryPoints.get(j).getClusterId()+1)+")" + "\t";
//						
//						for(double time: timeForCurrentTime.get(i))
//							s += Double.toString(time) + "\t";
//						s+="\n";
//					}
//				}
//			}
//		}
//		
//		s+= "\n\n";
//		s+= "Time b/w Bdry Points:\n";
//
//		if(timeBtwBdry!=null) {
//			if(!timeBtwBdry.isEmpty()) {
//				for(double currentTime = main.startTime;currentTime<main.endTime;currentTime += main.storeTimeDiff) {
//					
//					List<List<List<Double>>> timeForCurrentTime = timeBtwBdry.get((int)(currentTime/main.storeTimeDiff));
//					s += "Time: "+Double.toString(currentTime)+"\n";
//					
//					List<cluster> c = main.clusters;
//					StringBuilder sb= new StringBuilder();
//					for(cluster c1: c) {
//						s+= "\tCluster: "+c1.getNum()+"\n";
//						s+="\t\t";					
//						for(node n2: c1.getBdry_points()) {
//							s+=n2.getStringId()+"\t";
//						}
//						s+="\n";
//						
//						for(node n: bdryPoints) {
//							s+="\t"+n.getStringId();
//							for(node n2: c1.getBdry_points()) {
//								s+="\t"+timeForCurrentTime.get(n.getClusterId()).get(c1.getNum()).get(n2.getClusterId());
//							}
//							s += "\n";
//						}
//						s+="\n";
//					}
//					
//					
//					s+="\n";
//				}
//			}
//		}
//		return s;
//	}
}
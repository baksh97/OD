//import java.awt.List;
import java.util.List;
import java.util.PriorityQueue;

public class actual_time {
	public static double findTime(node n1, node n2, double currentTime, List<node> nodes) {
		
		PriorityQueue<node> unvisited = new PriorityQueue<>(cluster.dist_comparator);
		boolean visited[] = new boolean[nodes.size()];
		for(int i=0;i<nodes.size();i++)visited[i] = false;
		
		for(node n: nodes) {
			if(n.getId()!=n1.getId())
				n.setTempTime(Double.POSITIVE_INFINITY);
			else
				n.setTempTime(0);
			unvisited.add(n);
		}
		
		while(!unvisited.isEmpty()) {
			node currentNode = unvisited.remove();
			
			if(currentNode.getId()==n2.getId()) {
				return currentNode.getTempTime();
			}
			
			for(edge e: currentNode.getEdges()) {
				node otherNode = nodes.get(e.getEnd());
				
				if(!visited[otherNode.getId()]) {
					double timeForEdge = e.getTime_from_speed(currentTime + currentNode.getTempTime());
					
					if(currentNode.getTempTime() + timeForEdge < otherNode.getTempTime()) {
						otherNode.setTempTime(currentNode.getTempTime() + timeForEdge);
						unvisited.remove(otherNode);
						unvisited.add(otherNode);
					}
				}
			}
		}
		
		return -1;
	}
}

import java.util.List;

public class data{
	private int num_nodes,num_edges;
	public int getNum_edges() {
		return num_edges;
	}

	public void setNum_edges(int num_edges) {
		this.num_edges = num_edges;
	}

	public void setNum_nodes(int num_nodes) {
		this.num_nodes = num_nodes;
	}

	private List<node> nodes;
	private int num_vehicles;
	private int num_intervals;
	private double endTime,startTime=0;
	
	public int getNum_nodes() {
		return nodes.size();
	}
	
	public int getNum_intervals() {
		return num_intervals;
	}
	public void setNum_intervals(int num_intervals) {
		this.num_intervals = num_intervals;
	}
	public double getEndTime() {
		return endTime;
	}
	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}
	public double getStartTime() {
		return startTime;
	}
	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}
	//	private List<node> allBdryPts;
	public List<node> getNodes() {
		return nodes;
	}
	public void setNodes(List<node> nodes) {
		this.nodes = nodes;
	}
	public int getNum_vehicles() {
		return num_vehicles;
	}
	public void setNum_vehicles(int num_vehicles) {
		this.num_vehicles = num_vehicles;
	}
	
	public void addNode(node n) {
		nodes.add(n);
	}
	
	
	public String toString() {
		String s="Number of nodes: ";
		
		s += Integer.toString(nodes.size());
		s+="\nC.Num\tNeighbours\n";
		for(node n: nodes) {
			s+= Integer.toString(n.getClusterNum())+"\t";
			for(edge e:n.getEdges()) {
				s+= Integer.toString(e.getEnd()) + " ";				
			}
			
//			s+="number of neighbours: "+Integer.toString(n.getEdges().size())+"\n";
			s+="\n";
		}
		
		return s;
	}
	
}

public class dijsktra_data{
	private node n;
	private double dist;
	
	public dijsktra_data(node n, double dist) {
		this.n = n;
		this.dist = dist;
	}

	public node getN() {
		return n;
	}

	public double getDist() {
		return dist;
	}
	
	public String toString(){
		return Integer.toString(n.getId())+Double.toString(dist);
	}
}
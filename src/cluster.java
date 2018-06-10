import java.util.List;

public class cluster{
	
	private int num_nodes;
	private List<node> bdryPoints;
	private List<List<List<Double>>> timeFromNodeToBdry;
	private List<List<List<Double>>> timeBtwBdry;

	public int getNum_nodes() {
		return num_nodes;
	}

	public void setNum_nodes(int num_nodes) {
		this.num_nodes = num_nodes;
	}

	public double getTimeFromNodeToBdry(double currentTime,int nodeID, int bdryPtID) {
		
		return timeFromNodeToBdry;
	}

	public void setTimeFromNodeToBdry(List<List<List<Double>>> timeFromNodeToBdry) {
		this.timeFromNodeToBdry = timeFromNodeToBdry;
	}

	public List<List<List<Double>>> getTimeBtwBdry() {
		return timeBtwBdry;
	}

	public void setTimeBtwBdry(List<List<List<Double>>> timeBtwBdry) {
		this.timeBtwBdry = timeBtwBdry;
	}

	List<node> getBdry_points(){
		return bdryPoints;
	}
	
	void setBdry_points(List<node> bdryPoints) {
		this.bdryPoints = bdryPoints;
	}
}

public class edge {
	private int start,end;
	private double[][] speed;
	
	public double getTime_from_speed(double currentTime){
		int index = (int) (currentTime% main.timeWindow);
		return speed[index][0];/////////////correct this
	}
	
	public int getOtherEnd(int end1){
		return (start==end1)? end:start;
	}
}
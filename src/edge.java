
public class edge {
	private int start,end;
	private double[][] speed;
	private double dist;
	
	public double getTime_from_speed(double currentTime){
		int k = (int) (currentTime/ main.timeWindow);
//		return speed[index][0];/////////////correct this
		double t = currentTime;
		double d = dist;
//		int k = findIndex(t, speed, 0, speed[0].length - 1);
		System.out.println("index: "+k);
		double t_;
		try {
			t_ = t + d / speed[1][k];			
		}
		catch(Exception e) {
			System.err.println("Time input is smaller than the starting time.");
			return -1;
		}

		while(k != speed[0].length-1) {
			if(t_ > speed[0][k+1]) {
				d-= speed[1][k] * (speed[0][k+1] - t);
				t = speed[0][k+1];
				t_ = t + d/speed[1][k+1];
				k++;
			}
			else {
				break;
			}
		}
		System.out.println("Final time: "+t_);
		return t_;
	}
	
	public int getOtherEnd(int end1){
		return (start==end1)? end:start;
	}
}
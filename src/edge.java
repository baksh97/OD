
public class edge {
	private int start,end;
	private double[] speed;
	private double dist;
	
	public double getTime_from_speed(double currentTime){
//		if(currentTime > main.endTime)
//			return -1;
		int k = (int) ((currentTime-main.startTime)/ main.timeWindow);
		
		if(k >= speed.length){
			k=speed.length-1;
		}
//		System.out.println("k:" +k);
		double timeWindow = main.timeWindow;
//		return speed[index][0];/////////////correct this
		double t = currentTime;
		double d = dist;
//		int k = findIndex(t, speed, 0, speed[0].length - 1);
//		System.out.println("index: "+k);
		double t_ = t + d / speed[k];
//		try {
					
//		}
//		catch(Exception e) {
//			e.printStackTrace();
////			return -1;
//		}

		while(k != speed.length-1) {
			if(k!=speed.length-1){
				if(t_ > timeWindow*(k+1)) {
					d-= speed[k] * (timeWindow*(k+1) - t);
					t = timeWindow*(k+1);
					t_ = t + d/speed[k+1];
					k++;
				}
				else {
					break;
				}
			}
		}
//		System.out.println("Final time: "+t_);
		return t_-currentTime;
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public double[] getSpeed() {
		return speed;
	}

	public void setSpeed(double[] speed) {
		this.speed = speed;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public int getOtherEnd(int end1){
		return (start==end1)? end:start;
	}
}
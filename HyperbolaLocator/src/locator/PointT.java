package locator;

public class PointT extends Point {
	private long time;
	
	public PointT(double x, double y, long time) {
		super(x, y);
		this.setTime(time);
	}
	
	public PointT(Point pt, long time) {
		super(pt.x, pt.y);
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}

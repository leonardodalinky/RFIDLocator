package data;

import locator.Point;

public class MathUtils {
	public static final double PI2 = 6.283185307179586476925286766559;
	
	public static double phaseAdd(double n1, double n2) {
		double ret = n1 + n2;
		if (ret > PI2) {
			ret = ret - (int)(ret / PI2) * PI2;
		}
		else if (ret < 0) {
			ret = ret + ((int)(-ret / PI2) + 1) * PI2;
		}
		return ret;
	}
	
	public static double phaseSub(double n1, double n2) {
		if (Math.abs(n2 - n1) > 5.8) {
			if (n1 > n2) {
				return n1 - PI2 - n2;
			}
			else {
				return n1 + PI2 - n2;
			}
		}
		else {
			return n1 - n2;
		}
	}
	
	public static double distance(Point pt1, Point pt2) {
		double t1 = pt1.getX() - pt2.getX();
		double t2 = pt1.getY() - pt2.getY();
		return Math.sqrt((t1 * t1) + (t2 * t2));
	}
}

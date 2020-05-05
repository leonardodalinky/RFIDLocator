package locator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import csvparser.CsvParser;
import csvreader.CsvReaderException;
import data.AlignTagDatas;
import data.MathUtils;

public class HyperbolaLocator {
	// x轴上天线间距离
	public static final double D = 0.256;
	// y轴上天线组间距离
	public static final double L = 1.941;
	// speed of light
	public static final double C = 300000000;
	// frequency of channel
	public static final double CHANNEL = 920.625;
	
	public HyperbolaLocator() {
		// TODO Auto-generated constructor stub
	}
	
	public static PointT[] locateFromFile(String filePath) throws CsvReaderException, IOException {
		ArrayList<AlignTagDatas> datas = CsvParser.getAlignTagDatas(filePath);
		if (datas.size() <= 1) return null;
		PointT[][] pointss = new PointT[datas.size()][];
		for (int i = 0;i < datas.size();++i) {
			AlignTagDatas data = datas.get(i);
			Point[] ps = locate(data.phases[0], data.phases[1], data.phases[2], data.phases[3], CHANNEL);
			PointT[] pts = new PointT[ps.length];
			for (int j = 0;j < ps.length;++j) {
				pts[j] = new PointT(ps[j], data.timeAligned);
			}
			pointss[i] = pts;
		}
		
		// choose the most possible points
		PointT[] ans = new PointT[pointss.length - 1];
		for (int i = 0;i < ans.length;++i) {
			int minIndex1 = 0;
			double d = Double.MAX_VALUE;
			for (int j = 0;j < pointss[i].length;++j) {
				for (int k = 0;k < pointss[i + 1].length;++k) {
					double dis = MathUtils.distance(pointss[i][j], pointss[i + 1][k]);
					if (dis < d) {
						d = dis;
						minIndex1 = j;
					}
				}
			}
			ans[i] = new PointT(pointss[i][minIndex1], pointss[i][minIndex1].getTime());
		}
		
		return ans;
	}
	
    private static Point[] locate(double phase1OfGroup1, double phase2OfGroup1, double phase1OfGroup2, double phase2OfGroup2, 
    		double channel) {
        // 计算两个双曲线可能的距离差
        double[] dSet1 = getDeltaDSet(phase1OfGroup1, phase2OfGroup1, D, channel);
        double[] dSet2 = getDeltaDSet(phase1OfGroup2, phase2OfGroup2, D, channel);

        // 遍历所有可能的距离差，得到可能的交点集
        ArrayList<Point> points = new ArrayList<Point>();
        for (double d1 : dSet1) {
            for (double d2 : dSet2) {
                Point p = getIntersectionPoint(d1, d2, D, L);
                if (p != null) {
                    points.add(p);
                }
            }
        }
        Point[] results = new Point[points.size()];
        int i = 0;
        for (Point result : points) {
            results[i] = result;
            i++;
        }
        return results;
    }

    // 根据相邻天线的相位差导出可能的距离差，D相邻天线的距离
    private static double[] getDeltaDSet(double phase1, double phase2, double D, double channel) {
        TreeSet<Double> deltaDSet = new TreeSet<>();

        double deltaPhase = phase2 - phase1;
        double rate = (C / (1000000) / channel) / 4 / Math.PI;
        for (int k = -10; k < 10; k++) {
            // delta D 大于0的情况
            double temp = rate * (deltaPhase + k * 2 * Math.PI);
            if (temp >= -D && temp <= D) {
                deltaDSet.add(temp);
            }
        }
        double[] results = new double[deltaDSet.size()];
        int i = 0;
        for (Double deltaD : deltaDSet) {
            results[i] = deltaD;
            i++;
        }
        return results;
    }

    public static Point getIntersectionPoint(double d1, double d2, double D, double L) {
        if (d1 * d2 < 0) {
            return null;
        }

        double a1 = Math.abs(d1) / 2;
        double a2 = Math.abs(d2) / 2;

        double b1 = Math.sqrt(D * D / 4 - a1 * a1);
        double b2 = Math.sqrt(D * D / 4 - a2 * a2);
        if (Double.isNaN(b1) || Double.isNaN(b2)) {
            return null;
        }

        double A = (a1 * a1 / a2 / a2 / b1 / b1 - 1 / b2 / b2);
        double B = 2 * L / b2 / b2;
        double C = a1 * a1 / a2 / a2 - L * L / b2 / b2 - 1;
        double Delta = Math.sqrt(B * B - 4 * A * C);
        if (Double.isNaN(Delta)) {
            return null;
        }
        double y1 = (-B + Delta) / 2 / A;
        double y2 = (-B - Delta) / 2 / A;

        double y = -1;
        int cnt = 0;
        if (0 < y1 && y1 < L) {
            y = y1;
            cnt++;
        }
        if (0 < y2 && y2 < L) {
            y = y2;
            cnt++;
        }
        assert cnt != 2;
        if (y == -1) {
            return null;
        }

        if (d1 < 0 && d2 < 0) {
            double x = Math.sqrt(a1 * a1 * (1 + y * y / b1 / b1));
            return new Point(x, y);
        }

        if (d1 > 0 && d2 > 0) {
            double x = -Math.sqrt(a1 * a1 * (1 + y * y / b1 / b1));
            return new Point(x, y);
        }
        return null;
    }
    
    public static void main(String[] args) {
    	try {
			// write the enhanced data
//    		ArrayList<AlignTagDatas> eDatas = CsvParser.getAlignTagDatas("2020-04-11-14-48-57.csv");
//			File file = new File("data_enhanced.csv");
//			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
//			outputStream.write(new String("id,time,antenna,rssi,phase\n").getBytes());
//			int id = 0;
//			StringBuilder sb = new StringBuilder();
//			for (AlignTagDatas d : eDatas) {
//				for (int i = 0;i < CsvParser.ANTENNA_SIZE;++i) {
//					sb.append(id);sb.append(',');
//					sb.append(d.timeAligned);sb.append(',');
//					sb.append(i);sb.append(',');
//					sb.append(d.rssis[i]);sb.append(',');
//					sb.append(d.phases[i]);sb.append('\n');
//				}
//				++id;
//			}
//			outputStream.write(sb.toString().getBytes());
//			outputStream.close();
			// write the data into csv
			PointT[] ans = locateFromFile("2020-04-11-14-48-57.csv");
			File file = new File("HyperbolaData.csv");
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
			for (PointT pt : ans) {
				StringBuilder sb = new StringBuilder();
				sb.append(pt.getTime());
				sb.append(',');
				sb.append(pt.getX());
				sb.append(',');
				sb.append(pt.getY());
				sb.append('\n');
				outputStream.write(sb.toString().getBytes());
			}
			outputStream.close();
			
			System.out.println(ans.length);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
}

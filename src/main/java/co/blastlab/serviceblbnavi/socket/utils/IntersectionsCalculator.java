package co.blastlab.serviceblbnavi.socket.utils;

import co.blastlab.serviceblbnavi.domain.Anchor;
import co.blastlab.serviceblbnavi.dto.floor.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class IntersectionsCalculator {
	static List<Point> getIntersections(Anchor firstAnchor, double firstAnchorDistance, Anchor secondAnchor, double secondAnchorDistance)
	{
		List<Point> res = new ArrayList<>();
		double dx = secondAnchor.getX() - firstAnchor.getX();
		double dy = secondAnchor.getY() - firstAnchor.getY();
		double L2 = dx * dx + dy * dy;
		double rsum = (firstAnchorDistance + secondAnchorDistance);
		double rdiff = (firstAnchorDistance - secondAnchorDistance); // kolejnosc jest istotna
		double x, y;
		double lenI = 1 / Math.sqrt(L2); // dx*lenI to wersor kierunku
		// rozlaczne wewnetrznie
		if (L2 <= rdiff*rdiff)
		{
			// rozlaczne wewnetrznie
			x = (firstAnchor.getX() + secondAnchor.getX() - dx * lenI * rsum) / 2;
			y = (firstAnchor.getY() + secondAnchor.getY() - dy * lenI * rsum) / 2;
			res.add(new Point((int)x, (int)y));
			res.add(new Point((int)x, (int)y));
		}
		else if(rsum*rsum < L2)
		{

			// rozlaczne zewnetrznie
			x = (firstAnchor.getX() + secondAnchor.getX() + dx * lenI * rdiff) / 2;
			y = (firstAnchor.getY() + secondAnchor.getY() + dy * lenI * rdiff) / 2;
			res.add(new Point((int)x, (int)y));
			res.add(new Point((int)x, (int)y));
		}
		// gdy odleglosci sie przecinaja z pewnym zapasem
		else
		{
			double kk = (rsum*rsum - L2) * (L2 - rdiff*rdiff);
			double K = Math.sqrt(kk) / 4; // pole trojkata
			x = (int)(firstAnchor.getX() + secondAnchor.getX() + (secondAnchor.getX()-firstAnchor.getX())*(firstAnchorDistance * firstAnchorDistance - secondAnchorDistance * secondAnchorDistance) /L2) / 2;
			y = (int)(firstAnchor.getY() + secondAnchor.getY() + (secondAnchor.getY()-firstAnchor.getY())*(firstAnchorDistance * firstAnchorDistance - secondAnchorDistance * secondAnchorDistance) /L2) / 2;

			res.add(new Point(
				(int)(x + 2 * (secondAnchor.getY() - firstAnchor.getY()) * K / L2),
				(int)(y - 2 * (secondAnchor.getX() - firstAnchor.getX()) * K / L2))
			);
			res.add(new Point(
				(int)(x - 2 * (secondAnchor.getY() - firstAnchor.getY()) * K / L2),
				(int)(y + 2 * (secondAnchor.getX() - firstAnchor.getX()) * K / L2))
			);
		}
		return res;
	}

	static List<Double> calculateSumDistanceBetweenIntersectionPoints(List<Point> points) {
		Double[] IPdistance = new Double[points.size()];
		Arrays.fill(IPdistance, 0.0);
		for (int indn = 0; indn < points.size(); ++indn)
			for (Point point : points) {
				double dx = points.get(indn).getX() - point.getX();
				double dy = points.get(indn).getY() - point.getY();
				IPdistance[indn] += Math.sqrt(dx * dx + dy * dy);
			}
		return Arrays.asList(IPdistance);
	}

}

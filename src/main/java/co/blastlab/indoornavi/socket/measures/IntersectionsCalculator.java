package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.dto.Point;
import javafx.util.Pair;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class IntersectionsCalculator {
	public static List<Point> getIntersections(Anchor firstAnchor, double firstAnchorDistance, Anchor secondAnchor, double secondAnchorDistance) {
		List<Point> res = new ArrayList<>();
		double dx = secondAnchor.getX() - firstAnchor.getX();
		double dy = secondAnchor.getY() - firstAnchor.getY();
		double L2 = dx * dx + dy * dy;
		double rsum = (firstAnchorDistance + secondAnchorDistance);
		double rdiff = (firstAnchorDistance - secondAnchorDistance); // kolejnosc jest istotna
		double x, y;
		double lenI = 1 / Math.sqrt(L2); // dx*lenI to wersor kierunku
		// rozlaczne wewnetrznie
		if (L2 <= rdiff * rdiff) {
			// rozlaczne wewnetrznie
			x = (firstAnchor.getX() + secondAnchor.getX() - dx * lenI * rsum) / 2;
			y = (firstAnchor.getY() + secondAnchor.getY() - dy * lenI * rsum) / 2;
			res.add(new Point((int) x, (int) y));
		} else if (rsum * rsum < L2) {

			// rozlaczne zewnetrznie
			x = (firstAnchor.getX() + secondAnchor.getX() + dx * lenI * rdiff) / 2;
			y = (firstAnchor.getY() + secondAnchor.getY() + dy * lenI * rdiff) / 2;
			res.add(new Point((int) x, (int) y));
		}
		// gdy odleglosci sie przecinaja z pewnym zapasem
		else {
			double kk = (rsum * rsum - L2) * (L2 - rdiff * rdiff);
			double K = Math.sqrt(kk) / 4; // pole trojkata
			x = (int) (firstAnchor.getX() + secondAnchor.getX() + (secondAnchor.getX() - firstAnchor.getX()) * (firstAnchorDistance * firstAnchorDistance - secondAnchorDistance * secondAnchorDistance) / L2) / 2;
			y = (int) (firstAnchor.getY() + secondAnchor.getY() + (secondAnchor.getY() - firstAnchor.getY()) * (firstAnchorDistance * firstAnchorDistance - secondAnchorDistance * secondAnchorDistance) / L2) / 2;

			res.add(new Point(
				(int) (x + 2 * (secondAnchor.getY() - firstAnchor.getY()) * K / L2),
				(int) (y - 2 * (secondAnchor.getX() - firstAnchor.getX()) * K / L2))
			);
			res.add(new Point(
				(int) (x - 2 * (secondAnchor.getY() - firstAnchor.getY()) * K / L2),
				(int) (y + 2 * (secondAnchor.getX() - firstAnchor.getX()) * K / L2))
			);
		}
		return res;
	}

	public static List<Double> calculateSumDistanceBetweenIntersectionPoints(List<Point> points) {
		Double[] IPdistance = new Double[points.size()];
		Arrays.fill(IPdistance, 0.0);
		for (int indn = 0; indn < points.size(); ++indn) {
			for (Point point : points) {
				double dx = points.get(indn).getX() - point.getX();
				double dy = points.get(indn).getY() - point.getY();
				IPdistance[indn] += Math.sqrt(dx * dx + dy * dy);
			}
		}
		return Arrays.asList(IPdistance);
	}

	static boolean doSpheresIntersect(double distanceBetweenOneAndOtherAnchorCenter, double firstAnchorDistanceToTag, double secondAnchorDistanceToTag) {
		return !(distanceBetweenOneAndOtherAnchorCenter > firstAnchorDistanceToTag + secondAnchorDistanceToTag)
			&& !(distanceBetweenOneAndOtherAnchorCenter < Math.abs(firstAnchorDistanceToTag - secondAnchorDistanceToTag));
	}

	static SimpleMatrix calculateTwoSpheresPlanarMiddle(SimpleMatrix firstAnchorPosition,
	                                                    double firstAnchorDistanceToTag,
	                                                    SimpleMatrix secondAnchorPosition,
	                                                    double secondAnchorDistanceToTag) {
		SimpleMatrix radiusInFirstAnchorDirection;
		SimpleMatrix radiusInSecondAnchorDirection;

		SimpleMatrix versor = (secondAnchorPosition.minus(firstAnchorPosition));
		versor = versor.divide(versor.normF());
		double distanceBetweenOneAndOtherAnchorCenter = (secondAnchorPosition.minus(firstAnchorPosition)).normF();
		if (distanceBetweenOneAndOtherAnchorCenter > Math.max(firstAnchorDistanceToTag, secondAnchorDistanceToTag)) {
			radiusInFirstAnchorDirection = firstAnchorPosition.plus(versor.scale(firstAnchorDistanceToTag));
			radiusInSecondAnchorDirection = secondAnchorPosition.minus(versor.scale(secondAnchorDistanceToTag));
		} else {
			radiusInFirstAnchorDirection = firstAnchorPosition.plus(versor.scale(firstAnchorDistanceToTag));
			radiusInSecondAnchorDirection = secondAnchorPosition.plus(versor.scale(secondAnchorDistanceToTag));
		}
		return radiusInFirstAnchorDirection.scale(0.5).plus(radiusInSecondAnchorDirection.scale(0.5));
	}

	static List<Pair<SimpleMatrix, Float>> calculateTwoSpheresPlanarIntersections(SimpleMatrix firstAnchorPosition,
	                                                                              double firstAnchorDistanceToTag,
	                                                                              SimpleMatrix secondAnchorPosition,
	                                                                              double secondAnchorDistanceToTag,
	                                                                              SimpleMatrix thirdAnchorPosition) {
		List<Pair<SimpleMatrix, Float>> pairs = new ArrayList<>();
		double distanceBetweenOneAndOtherAnchorCenter = (firstAnchorPosition.minus(secondAnchorPosition)).normF();

		if (IntersectionsCalculator.doSpheresIntersect(distanceBetweenOneAndOtherAnchorCenter, firstAnchorDistanceToTag, secondAnchorDistanceToTag)) {
			pairs.add(
				new Pair<>(
					IntersectionsCalculator.calculateTwoSpheresPlanarMiddle(firstAnchorPosition, firstAnchorDistanceToTag, secondAnchorPosition, secondAnchorDistanceToTag),
					2f
				)
			);
		} else {
			// wzór Herona
			double p = 0.5 * (firstAnchorDistanceToTag + secondAnchorDistanceToTag + distanceBetweenOneAndOtherAnchorCenter);
			double P = Math.sqrt(p * (p - firstAnchorDistanceToTag) * (p - secondAnchorDistanceToTag) * (p - distanceBetweenOneAndOtherAnchorCenter));
			// end of wzór Herona

			double h = 2 * P / distanceBetweenOneAndOtherAnchorCenter;
			double distanceToMiddle = Math.sqrt(firstAnchorDistanceToTag * firstAnchorDistanceToTag - h * h);
			SimpleMatrix versor = (secondAnchorPosition.minus(firstAnchorPosition));
			versor = versor.divide(versor.normF());

			SimpleMatrix middlePoint = firstAnchorPosition.plus(versor.scale(distanceToMiddle));
			SimpleMatrix planeDirection = IntersectionsCalculator.crossProduct(firstAnchorPosition.minus(secondAnchorPosition), firstAnchorPosition.minus(thirdAnchorPosition));
			SimpleMatrix vortho = IntersectionsCalculator.crossProduct(planeDirection, firstAnchorPosition.minus(secondAnchorPosition));
			vortho = vortho.scale(h / vortho.normF());
			pairs.add(
				new Pair<>(
					middlePoint.plus(vortho),
					1f
				)
			);
			pairs.add(new Pair<>(
				middlePoint.minus(vortho),
				1f
			));
		}
		return pairs;
	}

	static List<Pair<SimpleMatrix, Float>> calculateThreeSpheresPlanarIntersections(SimpleMatrix firstAnchorPosition,
	                                                                              double firstAnchorDistanceToTag,
	                                                                              SimpleMatrix secondAnchorPosition,
	                                                                              double secondAnchorDistanceToTag,
	                                                                              SimpleMatrix thirdAnchorPosition,
	                                                                              double thirdAnchorDistanceToTag) {
		List<Pair<SimpleMatrix, Float>> pairs = new ArrayList<>();
		SimpleMatrix ex = secondAnchorPosition.minus(firstAnchorPosition);
		ex = ex.scale(1 / ex.normF());
		Double i = ex.dot(thirdAnchorPosition.minus(firstAnchorPosition));
		SimpleMatrix ey = thirdAnchorPosition.minus(firstAnchorPosition).minus(ex.scale(i));
		ey = ey.scale(1 / ey.normF());
		SimpleMatrix ez = IntersectionsCalculator.crossProduct(ex, ey);
		Double d = (secondAnchorPosition.minus(firstAnchorPosition)).normF();
		Double j = ey.dot(thirdAnchorPosition.minus(firstAnchorPosition));

		Double x = (firstAnchorDistanceToTag * firstAnchorDistanceToTag - secondAnchorDistanceToTag * secondAnchorDistanceToTag + d * d) / (2 * d);
		Double y = (firstAnchorDistanceToTag * firstAnchorDistanceToTag - thirdAnchorDistanceToTag * thirdAnchorDistanceToTag + i * i + j * j) / (2 * j) - i / j * x;
		double zz = firstAnchorDistanceToTag * firstAnchorDistanceToTag - x * x - y * y;
		if (zz < 0) {
			zz = 0d;
		}
		double z = Math.sqrt(zz);
		SimpleMatrix p = firstAnchorPosition.plus(ex.scale(x).plus(ey.scale(y)));
		pairs.add(
			new Pair<>(
				p.plus(ez.scale(z)),
				1f
			)
		);
		pairs.add(new Pair<>(
			p.minus(ez.scale(z)),
			1f
		));
		return pairs;
	}

	static List<Pair<SimpleMatrix, Float>> getIntersections3d(Anchor firstAnchor,
	                                                          double firstAnchorDistanceToTag,
	                                                          Anchor secondAnchor,
	                                                          double secondAnchorDistanceToTag,
	                                                          Anchor thirdAnchor,
	                                                          double thirdAnchorDistanceToTag) {
		List<Pair<SimpleMatrix, Float>> pairs = new ArrayList<>();
		SimpleMatrix firstAnchorPosition = IntersectionsCalculator.buildAnchorPositionMatrix(firstAnchor.getX(), firstAnchor.getY(), firstAnchor.getZ());
		SimpleMatrix secondAnchorPosition = IntersectionsCalculator.buildAnchorPositionMatrix(secondAnchor.getX(), secondAnchor.getY(), secondAnchor.getZ());
		SimpleMatrix thirdAnchorPosition = IntersectionsCalculator.buildAnchorPositionMatrix(thirdAnchor.getX(), thirdAnchor.getY(), thirdAnchor.getZ());
		double distanceBetweenFirstAndSecondAnchor = (firstAnchorPosition.minus(secondAnchorPosition)).normF();
		double distanceBetweenFirstAndThirdAnchor = (firstAnchorPosition.minus(thirdAnchorPosition)).normF();
		double distanceBetweenSecondAndThirdAnchor = (secondAnchorPosition.minus(thirdAnchorPosition)).normF();

		// gdy 1. i 2. sfera sa rozlaczne
		// wtedy wszystkie punkty przeciec sa rozlozone planarnie
		if (!IntersectionsCalculator.doSpheresIntersect(distanceBetweenFirstAndSecondAnchor, firstAnchorDistanceToTag, secondAnchorDistanceToTag) ||
			!IntersectionsCalculator.doSpheresIntersect(distanceBetweenFirstAndThirdAnchor, firstAnchorDistanceToTag, thirdAnchorDistanceToTag) ||
			!IntersectionsCalculator.doSpheresIntersect(distanceBetweenSecondAndThirdAnchor, secondAnchorDistanceToTag, thirdAnchorDistanceToTag))
		{
			pairs.addAll(IntersectionsCalculator.calculateTwoSpheresPlanarIntersections(
				firstAnchorPosition, firstAnchorDistanceToTag, secondAnchorPosition, secondAnchorDistanceToTag, thirdAnchorPosition)
			);
			pairs.addAll(IntersectionsCalculator.calculateTwoSpheresPlanarIntersections(
				firstAnchorPosition, firstAnchorDistanceToTag, thirdAnchorPosition, thirdAnchorDistanceToTag, secondAnchorPosition)
			);
			pairs.addAll(IntersectionsCalculator.calculateTwoSpheresPlanarIntersections(
				secondAnchorPosition, secondAnchorDistanceToTag, thirdAnchorPosition, thirdAnchorDistanceToTag, firstAnchorPosition)
			);
		}
		else
		{
			pairs.addAll(IntersectionsCalculator.calculateThreeSpheresPlanarIntersections(
				firstAnchorPosition, firstAnchorDistanceToTag, secondAnchorPosition, secondAnchorDistanceToTag, thirdAnchorPosition, thirdAnchorDistanceToTag)
			);
		}
		return pairs;
	}

	private static SimpleMatrix crossProduct(SimpleMatrix left, SimpleMatrix right) {
		SimpleMatrix result = new SimpleMatrix(3, 1);
		result.set(0, left.get(1) * right.get(2) - left.get(2) * right.get(1));
		result.set(1, -left.get(0) * right.get(2) + left.get(2) * right.get(0));
		result.set(2, left.get(0) * right.get(1) - left.get(1) * right.get(0));
		return result;
	}

	private static SimpleMatrix buildAnchorPositionMatrix(Integer x, Integer y, Integer z) {
		SimpleMatrix result = new SimpleMatrix(3, 1);
		result.set(0, x);
		result.set(1, y);
		result.set(2, z);
		return result;
	}
}

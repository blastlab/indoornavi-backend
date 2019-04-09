package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.domain.Anchor;
import co.blastlab.indoornavi.domain.Floor;
import org.apache.commons.lang3.tuple.Pair;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class IntersectionsCalculatorTest {

	@Test
	public void crossProduct() {
		// given
		SimpleMatrix one = new SimpleMatrix(new double[][]{
			new double[] { 1, 2, 3 }
		}).transpose();
		SimpleMatrix two = new SimpleMatrix(new double[][]{
			new double[] { 1, 5, 7 }
		}).transpose();

		// when
		SimpleMatrix result = IntersectionsCalculator.crossProduct(one, two);

		// then
		assertThat(result.get(0), is(-1d));
		assertThat(result.get(1), is(-4d));
		assertThat(result.get(2), is(3d));
	}

	@Test
	public void buildAnchorPositionMatrix() {
		// given

		// when
		SimpleMatrix result = IntersectionsCalculator.buildAnchorPositionMatrix(10d, 15d, 0d);

		// then
		assertThat(result.get(0), is(10d));
		assertThat(result.get(1), is(15d));
		assertThat(result.get(2), is(0d));
	}

	@Test
	public void getIntersections3d() {
		// given
		Anchor firstAnchor = new Anchor(-10, 20, -30, new Floor(), 32995);
		double firstAnchorDistance = 40;
		Anchor secondAnchor = new Anchor(15, 40, -10, new Floor(), 32996);
		double secondAnchorDistance = 10;
		Anchor thirdAnchor = new Anchor(0, 0, 0, new Floor(), 32997);
		double thirdAnchorDistance = 15;

		// when
		List<Pair<SimpleMatrix, Float>> intersections3d = IntersectionsCalculator.getIntersections3d(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);
		List<SimpleMatrix> points = intersections3d.stream().map(Pair::getKey).collect(Collectors.toList());

		// then
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(16.24d, 33.14d, -2.82d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(15.06d, 47.9d, -16.12d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(9.04d, 11.25d, 4.07d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(-9.22d, -10.9d, -4.6d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(8.35d, 22.28d, -5.57d)) < 1);
	}

	@Test
	public void getIntersections3d_two() {
		// given
		Anchor firstAnchor = new Anchor(-10, 20, -30, new Floor(), 32995);
		double firstAnchorDistance = 40;
		Anchor secondAnchor = new Anchor(15, 40, -10, new Floor(), 32996);
		double secondAnchorDistance = 25;
		Anchor thirdAnchor = new Anchor(0, 0, 0, new Floor(), 32997);
		double thirdAnchorDistance = 30;

		// when
		List<Pair<SimpleMatrix, Float>> intersections3d = IntersectionsCalculator.getIntersections3d(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);
		List<SimpleMatrix> points = intersections3d.stream().map(Pair::getKey).collect(Collectors.toList());

		// then
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(23.53d, 16.55d, -8.47d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(0.81d, 29.05d, 7.43d)) < 1);
	}

	@Test
	public void getIntersections3d_three() {
		// given
		Anchor firstAnchor = new Anchor(-10, 20, -30, new Floor(), 32995);
		double firstAnchorDistance = 40;
		Anchor secondAnchor = new Anchor(15, 40, -10, new Floor(), 32996);
		double secondAnchorDistance = 25;
		Anchor thirdAnchor = new Anchor(0, 0, 0, new Floor(), 32997);
		double thirdAnchorDistance = 20;

		// when
		List<Pair<SimpleMatrix, Float>> intersections3d = IntersectionsCalculator.getIntersections3d(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);
		List<SimpleMatrix> points = intersections3d.stream().map(Pair::getKey).collect(Collectors.toList());

		// then
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(12.59d, 17.57d, 4.18d)) < 1);
	}

	@Test
	public void calculateTwoSpheresPlanarMiddle() {
		// given
		SimpleMatrix firstAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-10d, 20d, -30d);
		double firstAnchorDistance = 60;
		SimpleMatrix secondAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(15d, 40d, -10d);
		double secondAnchorDistance = 10;
		SimpleMatrix thirdAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-0d, 0d, 0d);
		double thirdAnchorDistance = 15;

		// when
		SimpleMatrix result_1_2 = IntersectionsCalculator.calculateTwoSpheresPlanarMiddle(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance);
		SimpleMatrix result_1_3 = IntersectionsCalculator.calculateTwoSpheresPlanarMiddle(firstAnchor, firstAnchorDistance, thirdAnchor, thirdAnchorDistance);
		SimpleMatrix result_2_3 = IntersectionsCalculator.calculateTwoSpheresPlanarMiddle(secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);

		// then
		assertTrue(result_1_2.minus(IntersectionsCalculator.buildAnchorPositionMatrix(25.67d, 48.54d, -1.45d)).normF() < 1d);
		assertTrue(result_1_3.minus(IntersectionsCalculator.buildAnchorPositionMatrix(5.02d, -10.04d, 15.06d)).normF() < 1d);
		assertTrue(result_2_3.minus(IntersectionsCalculator.buildAnchorPositionMatrix(8.35d, 22.27d, -5.56d)).normF() < 1d);
	}

	private double calculateClosestDistance(List<SimpleMatrix> points, SimpleMatrix pointToCheck) {
		double minDistance = Double.POSITIVE_INFINITY;
		for (SimpleMatrix point : points) {
			double distance = point.minus(pointToCheck).normF();
			minDistance = Math.min(minDistance, distance);
		}
		return minDistance;
	}

	@Test
	public void calculateThreeSpheresPlanarIntersections() {
		// given
		SimpleMatrix firstAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-10d, 20d, -30d);
		double firstAnchorDistance = 40;
		SimpleMatrix secondAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(15d, 40d, -10d);
		double secondAnchorDistance = 25;
		SimpleMatrix thirdAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-0d, 0d, 0d);
		double thirdAnchorDistance = 20;

		// when
		List<Pair<SimpleMatrix, Float>> pairs = IntersectionsCalculator.calculateThreeSpheresPlanarIntersections(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);
		List<SimpleMatrix> points = pairs.stream().map(Pair::getKey).collect(Collectors.toList());

		// then
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(12.59d, 17.57d, 4.18d)) < 1);
	}

	@Test
	public void calculateThreeSpheresPlanarIntersectionsAllIntersects() {
		// given
		SimpleMatrix firstAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-10d, 20d, -30d);
		double firstAnchorDistance = 40;
		SimpleMatrix secondAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(15d, 40d, -10d);
		double secondAnchorDistance = 25;
		SimpleMatrix thirdAnchor = IntersectionsCalculator.buildAnchorPositionMatrix(-0d, 0d, 0d);
		double thirdAnchorDistance = 30;

		// when
		List<Pair<SimpleMatrix, Float>> pairs = IntersectionsCalculator.calculateThreeSpheresPlanarIntersections(firstAnchor, firstAnchorDistance, secondAnchor, secondAnchorDistance, thirdAnchor, thirdAnchorDistance);
		List<SimpleMatrix> points = pairs.stream().map(Pair::getKey).collect(Collectors.toList());

		// then
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(23.53d, 16.55d, -8.47d)) < 1);
		assertTrue(calculateClosestDistance(points, IntersectionsCalculator.buildAnchorPositionMatrix(0.81d, 29.05d, 7.43d)) < 1);
	}

}

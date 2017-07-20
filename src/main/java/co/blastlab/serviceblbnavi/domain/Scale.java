package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.dto.floor.Measure;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Scale extends TrackedEntity {

	private int startX;
	private int startY;
	private int stopX;
	private int stopY;

	@Enumerated(EnumType.STRING)
	private Measure measure;

	/**
	 * Measure should be always saved in db as centimeters.
	 */
	private int realDistanceInCentimeters;

//	/**
//	 * Don't use this. Use builder instead.
//	 */
//	private Scale(int startX, int startY,
//	              int stopX, int stopY,
//	              Measure measure, int realDistanceInCentimeters) {
//		this
//	}

	/*
	    this method calculate distance between start point and stop point
    */
	public double getDistance() {
		return Math.sqrt(Math.pow(Math.abs(startX - stopX), 2) + Math.pow(Math.abs(startY - stopY), 2));
	}

	public static MeasureBuilder scale(@Nullable Scale scaleEntity) {
		return measure -> distance -> startX -> startY -> stopX -> stopY -> {
			int realDistanceInCentimeters = measure.equals(Measure.METERS) ? distance * 100 : distance;
			Scale scale = new Scale(
				startX, startY,
				stopX, stopY,
				measure, realDistanceInCentimeters
			);
			if (scaleEntity != null) {
				scale.setId(scaleEntity.getId());
			}
			return scale;
		};
	}

	public interface MeasureBuilder {

		DistanceBuilder measure(Measure measure);
	}

	/**
	 * Distance will be converted to centimeters if needed
	 */
	public interface DistanceBuilder {

		StartXBuilder distance(int distance);
	}

	public interface StartXBuilder {

		StartYBuilder startX(int x);
	}

	public interface StartYBuilder {

		StopXBuilder startY(int y);
	}

	public interface StopXBuilder {

		StopYBuilder stopX(int x);
	}

	public interface StopYBuilder {

		Scale stopY(int y);
	}
}

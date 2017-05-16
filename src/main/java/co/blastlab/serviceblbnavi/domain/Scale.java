package co.blastlab.serviceblbnavi.domain;

import co.blastlab.serviceblbnavi.dto.floor.Measure;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class Scale extends TrackedEntity {
	private int startX;
	private int startY;
	private int stopX;
	private int stopY;

	@Enumerated(EnumType.STRING)
	private Measure measure;

	/*
		measure should be always centimeters
	*/
	private int realDistanceInCentimeters;

	/*
	    this method calculate distance between start point and stop point
    */
	public double getDistance() {
		return Math.sqrt(Math.pow(Math.abs(startX - stopX), 2) + Math.pow(Math.abs(startY - stopY), 2));
	}
}

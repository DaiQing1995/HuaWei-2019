package com.huawei.pojo;

import com.huawei.entity.Road;

/**
 * the distance can this car move
 * 
 * @author DaiQing
 *
 */
public class CarDistance {

	// can not pass the cross
	public final static int NO_CROSS_REACH = -1;
	// the car have finished its journey
	public final static int FINISHED_JOURNEY = -2;

	public int remainDistance;

	public int nextRoadDistance;

	public int nextRoadPane;

	public Road nextRoad;

	public boolean isFrontCarHinderTheWay;

	/**
	 * 
	 * @param remainDistance
	 *            remain distance of current road
	 * @param nextRoadDistance
	 *            next road distance, if the car can not run to next road: -1
	 * @param nextRoadPane
	 *            if the car does not enter next road, nextRoadPane equal to -1
	 * @param nextRoad
	 *            if the car does not enter next road, nextRoad is null
	 * @param isFrontCarHinderTheWay
	 *            if the front car have not moved, which cause this car can not move
	 *            or can not move as quickly as possible, this value is true
	 */
	public CarDistance(int remainDistance, int nextRoadDistance, int nextRoadPane, Road nextRoad,
			boolean isNextRoadCarHinderTheWay) {
		super();
		this.remainDistance = remainDistance;
		this.nextRoadDistance = nextRoadDistance;
		this.nextRoadPane = nextRoadPane;
		this.nextRoad = nextRoad;
		this.isFrontCarHinderTheWay = isNextRoadCarHinderTheWay;
	}

}

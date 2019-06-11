package com.huawei.util;

import java.util.List;

import com.huawei.Main;
import com.huawei.WeightMatrix;
import com.huawei.entity.Cross;
import com.huawei.entity.Road;
import com.huawei.entity.Vehicle;
import com.huawei.pojo.CarDistance;

/**
 * Tools for Vehicle
 * 
 * @author DaiQing
 *
 */
public class VehicleUtil {

	/**
	 * ONLY FOR THE USE OF UN START CARS although this method will change the values
	 * of fields of the car and also the values of fields of the road.
	 * 
	 * @param car
	 *            the car is going to run
	 * @return car Distance which indicate the pane, position of the car in the
	 *         road(also included in CarDistance)
	 */
	public static CarDistance startOnRoadDistance(Vehicle car) {

		// 1. get the plan time and next Road
		int nextCrossPId = car.getNextCrossOfPlan();
		Road nextRoad = findNextRoad(car.startPoint, Cross.crossDic.get(nextCrossPId));
		// [return 1] the road have no car, update the speed of car and also the road.
		// [return 2] the road have cars, update the speed of car and also the road.
		// [return 3] the road have have cars, and no empty space exists.
		List<Vehicle[]> RoadStatus;
		boolean direction;
		List<Integer> nextRoadSpeedLimit;
		if (nextRoad.startPointPId == car.startPoint.programId) {
			RoadStatus = nextRoad.positiveRoadStatus;
			direction = true;
			nextRoadSpeedLimit = nextRoad.positiveRoadSpeedLimit;
		} else {
			RoadStatus = nextRoad.negativeRoadStatus;
			direction = false;
			nextRoadSpeedLimit = nextRoad.negativeRoadSpeedLimit;
		}
		// 2. get a free pane of nextRoad
		int nextRoadPane;
		for (nextRoadPane = 0; nextRoadPane < nextRoad.roadSum; ++nextRoadPane) {
			if (RoadStatus.get(nextRoadPane)[0] == null)
				break;
		}
		// [return 1/3] the road have had cars, and no empty space exists. Normally
		// this situation does not exist because Dijkstra won't allow it only if the
		// road are all full.
		if (nextRoadPane == nextRoad.roadSum) {
			// [debug point]
			return new CarDistance(0, CarDistance.NO_CROSS_REACH, -1, null, true);
		}

		// 3. get free space of the road.
		int freeSpace;
		for (freeSpace = 0; freeSpace < nextRoad.length
				&& RoadStatus.get(nextRoadPane)[freeSpace] == null; freeSpace++) {
		}

		// 4. check the speed and free space, here we think the speed can not larger
		// than
		// length of a road.
		if (freeSpace == nextRoad.length) {
			// [return 2/3] the road have cars, update the speed of car and also the road.
			int speedOfCurrentPane = GetTheRightSpeed(nextRoad.maxSpeed, car.maxSpeed);
			car.changeRoadAndPosition(nextRoad, direction, speedOfCurrentPane, nextRoadPane, speedOfCurrentPane);
			return new CarDistance(CarDistance.NO_CROSS_REACH, speedOfCurrentPane, nextRoadPane, nextRoad, false);
		} else {
			int speedOfCurrentPane = GetTheRightSpeed(nextRoadSpeedLimit.get(nextRoadPane), car.maxSpeed);
			// Maybe the car has not reach the car in the front of it, so it should move as
			// fast as possible.
			int firstSpeed = GetTheRightSpeed(nextRoad.maxSpeed, car.maxSpeed);
			int nextDis;
			if (firstSpeed > freeSpace) {
				nextDis = freeSpace;
			} else {
				nextDis = firstSpeed;
			}
			car.changeRoadAndPosition(nextRoad, direction, nextDis, nextRoadPane, speedOfCurrentPane);
			// [return 3/3] the road have cars, update the speed of car and also the road.
			return new CarDistance(CarDistance.NO_CROSS_REACH, nextDis, nextRoadPane, nextRoad, false);
		}
	}

	/**
	 * ONLY FOR THE USE OF ON ROAD CARS.
	 * 
	 * If the car at the cross, pos equals to 0; If the car is going to pass the
	 * cross, this part give the remain distance of the current road and next road,
	 * also along with whether the next road car hinder the way or not. Just as Car
	 * distance described.
	 * 
	 * @param road
	 *            current road
	 * @param pos
	 *            current pos of the car on the road
	 * @param direction
	 *            true if the direction from the road's start to road's end
	 * @param pane
	 *            the pane of the car
	 * @param turnDirect
	 *            from PassCrossDirection Interface
	 * @return the distance of current car can move on the current road and next
	 *         road if possible. Also along with the finish journey flag.
	 */
	public static CarDistance moveRoadDistance(Road road, Vehicle car, int pos, int pane, boolean direction,
			int turnDirect) {
		// the current road
		Vehicle[] carsOnTheRoad;
		// the end of current road
		Cross startCross;
		// the start of current road (for the weight of dijkstra)
		Cross tabuCross;

		// 1. get the pane on which the car runs
		if (direction) {
			carsOnTheRoad = road.positiveRoadStatus.get(pane);
			startCross = Cross.crossDic.get(road.endPointPId);
			tabuCross = Cross.crossDic.get(road.startPointPId);
		} else {
			carsOnTheRoad = road.negativeRoadStatus.get(pane);
			startCross = Cross.crossDic.get(road.startPointPId);
			tabuCross = Cross.crossDic.get(road.endPointPId);
		}

		// 2.1 check the remain distance of the car can run
		int spaceFree = 0;
		int currentIndex = pos + 1;
		while (true) {
			if (currentIndex < carsOnTheRoad.length && carsOnTheRoad[currentIndex] == null) {
				currentIndex++;
				spaceFree++;
			} else
				break;
		}

		// 2.2 check if front have unmoved cars
		if (currentIndex < carsOnTheRoad.length && carsOnTheRoad[currentIndex] != null
				&& carsOnTheRoad[currentIndex].checkTime < Main.times) {
			// [return1/6] the car is going to finish its journey
			return new CarDistance(spaceFree, CarDistance.NO_CROSS_REACH, -1, null, true);
		}

		// 3. check the speed and the remain road distance, update the speed
		if (car.currentSpeed < car.maxSpeed)
			car.currentSpeed = car.maxSpeed < road.maxSpeed ? car.maxSpeed : road.maxSpeed;

		// 4.1 the cars in the front of current car exist, and the car is not going to
		// pass the road
		if (currentIndex < carsOnTheRoad.length && carsOnTheRoad[currentIndex] != null) {
			// [return2/6] the car is going to move on current road.
			if (spaceFree > car.currentSpeed)
				return new CarDistance(car.currentSpeed, CarDistance.NO_CROSS_REACH, -1, null, false);
			else
				return new CarDistance(spaceFree, CarDistance.NO_CROSS_REACH, -1, null, false);
		}

		// 4.2 the car is going to pass the road
		if (spaceFree < car.currentSpeed) {

			// [return2/5] the car is going to finish its journey
			if (startCross == car.endPoint) {
				if (currentIndex == carsOnTheRoad.length)
					return new CarDistance(spaceFree, CarDistance.FINISHED_JOURNEY, -1, null, false);
				else
					return new CarDistance(spaceFree, CarDistance.NO_CROSS_REACH, -1, null, false);
			}

			// A. 4.2.1 get the next road
			int nextCrossPId = 0;
			if (car.planTime != Main.times) {
				int fromCrossPid = startCross.programId;
				nextCrossPId = WeightMatrix.Dijkstra(startCross.programId, tabuCross.programId, car.endPoint.programId);
				if (fromCrossPid == nextCrossPId) {
					nextCrossPId = car.endPoint.programId;
				}
				car.afterItsPlan(Main.times, nextCrossPId);
			} else {
				nextCrossPId = car.planValue;
			}
			Road nextRoad = findNextRoad(startCross, Cross.crossDic.get(nextCrossPId));
			// if (nextRoad == null) {// TODO: debug point
			// System.out.println("err, next road is null. VehicleUtil Line 191");
			// }
			boolean nextRoadDirection;
			if (nextRoad.startPointPId == startCross.programId) {
				nextRoadDirection = true;
			} else {
				nextRoadDirection = false;
			}

			// A. 4.2.2 get the next road and speed limit of each pane
			List<Vehicle[]> nextRoadPane;
			List<Integer> nextRoadSpeedLimit;
			if (nextRoadDirection) {
				nextRoadPane = nextRoad.positiveRoadStatus;
				nextRoadSpeedLimit = nextRoad.positiveRoadSpeedLimit;
			} else {
				nextRoadPane = nextRoad.negativeRoadStatus;
				nextRoadSpeedLimit = nextRoad.negativeRoadSpeedLimit;
			}
			// A. 4.2.3 traverse the nextRoad to find a pane to go
			for (int i = 0; i < nextRoad.roadSum; ++i) {
				int j;
				for (j = 0; j < nextRoad.length; ++j) {
					/**
					 * 1. if next Road has car 1.1 the front cars have not move at this point of
					 * time
					 * 
					 * 1.1.1 current car is forced to move, then to [1.2.1]
					 * 
					 * 1.1.2 current car can wait until next time, [return]
					 * 
					 * 1.2 the front cars have moved at this point of time
					 * 
					 * 1.2.1 the front car does not hinder the way, [return]
					 * 
					 * 1.2.2 the front car hinder the way, the car can not move, and the current
					 * pane is not the last, [break 1.3]
					 * 
					 * 2. if next Road doesn't have a car: move as possible as it can move
					 * 
					 * if the nextRoad's car has not move and hinders the current car return
					 * object's hinder is true;
					 * 
					 * else if the car hinders current car but the current car can move at the speed
					 * of its max or the maxspeed of the road, return object's hinder is false;
					 */
					// situation 1
					if (nextRoadPane.get(i)[j] != null && nextRoadPane.get(i)[j].checkTime < Main.times
							&& car.forceMove != Vehicle.FORCEMOVE) {
						// [return3/5] next road's cars have not moved, so the speed of next road can
						// not specify. So return
						return new CarDistance(spaceFree, 0, -1, null, true);
					} else if (nextRoadPane.get(i)[j] != null
							&& (nextRoadPane.get(i)[j].checkTime == Main.times || car.forceMove == Vehicle.FORCEMOVE)) {
						// this judge is to ensure the next pane of the nextRoad is available
						if (nextRoadPane.get(i)[j] != null && j == 0 && i != nextRoad.roadSum - 1) {
							break;
						}
						// original speed, the highest speed that the car can move.
						car.currentSpeed = GetTheRightSpeed(nextRoad.maxSpeed, car.maxSpeed);
						// nextRoad distance is restricted by the remain distance of current road
						int nextRoadDis = car.currentSpeed - spaceFree > 0 ? car.currentSpeed - spaceFree : 0;
						// get the value of distance on the next road.
						CarDistance ret;
						if (nextRoadDis > j) {
							// if the car's next road distance is too fast, here to control that the car can
							// not crash the next car, the largest distance should be j.
							ret = new CarDistance(spaceFree, j, i, nextRoad, false);
						} else {
							ret = new CarDistance(spaceFree, nextRoadDis, i, nextRoad, false);
						}
						/**
						 * if all cars have moved already, this car's speed should only be down(limited
						 * to the road speed limit or its own speed which is lower than current road
						 * speed)
						 */
						car.currentSpeed = car.maxSpeed > nextRoadSpeedLimit.get(i) ? nextRoadSpeedLimit.get(i)
								: car.maxSpeed;
						// [return2/5] no hindered car exist
						return ret;
					}
				}
				// situation 2
				if (j == nextRoad.length) {
					// Until here this method does not return and j does not equal to 0 represents
					// the next road is free, no cars on the nextRoad found.
					car.currentSpeed = GetTheRightSpeed(nextRoadSpeedLimit.get(i), car.maxSpeed);
					int nextRoadDis;
					nextRoadDis = car.currentSpeed - spaceFree > 0 ? car.currentSpeed - spaceFree : 0;
					// [return4/5] return current space and next space,no hindered car exist
					return new CarDistance(spaceFree, nextRoadDis, i, nextRoad, false);
				}
			}
		} else if (spaceFree >= car.currentSpeed) {
			// [return5/5] B. the space is big enough for current car to move
			return new CarDistance(car.currentSpeed, CarDistance.NO_CROSS_REACH, -1, null, false);
		}
		// error if return null
//		System.err.println("error, doesn't return car distance VehicleUtil");
		return null;
	}

	/**
	 * get the proper temporary speed when come to nextRoad
	 * 
	 * @param roadLimit
	 * @param carMaxSpeed
	 * @return
	 */
	private static int GetTheRightSpeed(Integer roadLimit, int carMaxSpeed) {
		if (roadLimit < carMaxSpeed)
			return roadLimit;
		else {
			return carMaxSpeed;
		}
	}

	/**
	 * find the road between two cross
	 * 
	 * @param curCross
	 *            current cross
	 * @param nextCross
	 *            next cross
	 * @return
	 */
	private static Road findNextRoad(Cross curCross, Cross nextCross) {
		for (int i = 0; i < 4; ++i) {
			if (curCross.road[i] == null)
				continue;
			if (curCross.road[i].startPoint == curCross.id && curCross.road[i].endPoint == nextCross.id) {
				return curCross.road[i];
			}
			if (curCross.road[i].startPoint == nextCross.id && curCross.road[i].endPoint == curCross.id) {
				if (!curCross.road[i].isMutual)
					System.err.println("Dijkstra error, the road is not mutual");
				return curCross.road[i];
			}
		}
		return null;
	}
}
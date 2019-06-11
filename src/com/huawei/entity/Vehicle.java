package com.huawei.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.huawei.Main;
import com.huawei.WeightMatrix;

/**
 * Vehicle info
 * 
 * @author DaiQing
 *
 */
public class Vehicle {
	/**
	 * 0.0.0 overall data (4 elements)
	 */
	public static Map<Integer, Vehicle> vehicleDic = new HashMap<>();

	// ordered id from small to big
	public static LinkedList<Integer> orderedQueue = new LinkedList<>();

	// ordered id from small to big
	public static LinkedList<Integer> unstartCarQueue = new LinkedList<>();

	public static int countOfCar = 0;

	/**
	 * 0.0.1 cars have finished its journey, generated data. (1)
	 */
	public static Map<Integer, Vehicle> finishedCarDic = new HashMap<>();

	/**
	 * 0.1. original data (5 elements)
	 */
	public int id;
	public Cross startPoint;
	public Cross endPoint;
	public int maxSpeed;
	public int startTime;

	/**
	 * 1.1. generated dynamic data (7 elements)
	 */
	// 1.1.1started or not
	public boolean isStart;
	// 1.1.1started or not
	public boolean isFinish;
	// 1.1.2current spped
	public int currentSpeed;
	// 1.1.3current road
	public Road currentRoad;
	public boolean direction; // true if on the positive road, false if on the negative road
	public int curPosOfRoad;
	public int curPaneOfRoad;
	// 1.1.4 passed time, To synchronize with the system time
	public int checkTime;

	/**
	 * 1.2. generated data for print(2 elements)
	 */
	// the real running time of the car
	public int realStartTime;
	// passed crosses
	public List<Road> passedRoads = new ArrayList<>();

	/**
	 * 1.3.generated data for validation (2 elements)
	 */
	public int planTime;
	public int planValue;
	// to avoid deadlock, force move flag
	public int forceMove;
	public static final int WAIT_FOR_MOVE = 0;
	public static final int FIRST_WAIT_FOR_FRONT_CAR = 1;
	public static final int FORCEMOVE = 4;

	public Vehicle(int id, Cross startPoint, Cross endPoint, int maxSpeed, int startTime) {
		// 0.original data (5 elements)
		this.id = id;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.maxSpeed = maxSpeed;
		this.startTime = startTime;
		// 1.1 (4)
		currentSpeed = 0;
		currentRoad = null;
		isStart = false;
		isFinish = false;
		checkTime = 0;

		// 1.3 (3)
		planTime = -1;
		planValue = -1;
		
		forceMove = WAIT_FOR_MOVE;

		// 0.0.0 (4 elements)
		vehicleDic.put(id, this);
		orderedQueue.add(id);
		unstartCarQueue.add(id);
		countOfCar++;
		
	}

	/**
	 * record the value of the Dijkstra to ensure the car do not plan twice which
	 * may result in order error.
	 * 
	 * @param timeStamp
	 * @param planValue
	 */
	public void afterItsPlan(int timeStamp, int planValue) {
		this.planTime = timeStamp;
		this.planValue = planValue;
	}

	/**
	 * get next cross the car is going to through
	 * 
	 * @return next cross program id.
	 */
	public int getNextCrossOfPlan() {
		int nextCrossPId;
		if (planTime == -1) { // the car has never planed its road.
			int fromCrossId = startPoint.programId;
			nextCrossPId = WeightMatrix.Dijkstra(startPoint.programId, WeightMatrix.TABU_NOTEXIST, endPoint.programId);
			if (fromCrossId == nextCrossPId) {
				nextCrossPId = endPoint.programId;
			}
			// mark the time and the plan value
			afterItsPlan(Main.times, nextCrossPId);
		} else { // the car had planned its road already.
			nextCrossPId = planValue;
		}
		return nextCrossPId;
	}

	/**
	 * Change to another road, stayed to start is also included
	 * refresh the car info and corresponding road info
	 * @param passedCross
	 * @param curRoad
	 * @param direction
	 * @param positionOfRoad
	 * @param curPaneOfRoad
	 * @param currentSpeed
	 */
	public void changeRoadAndPosition(Road curRoad, boolean direction, int positionOfRoad,
			int curPaneOfRoad, int currentSpeed) {
		List<Vehicle[]> roadStatus;
		passedRoads.add(curRoad);
		// A. the car has not Started yet
		if (!isStart) {
			isStart = true;
			realStartTime = Main.times;

			this.currentRoad = curRoad;
			this.direction = direction;
			this.curPosOfRoad = positionOfRoad;
			this.curPaneOfRoad = curPaneOfRoad;
			this.currentSpeed = currentSpeed;
			if (this.direction) {
				roadStatus = this.currentRoad.positiveRoadStatus;
			} else {
				roadStatus = this.currentRoad.negativeRoadStatus;
			}
			
			if (roadStatus.get(this.curPaneOfRoad)[this.curPosOfRoad - 1] != null) {
				System.err.println("error debug point");
			}
			roadStatus.get(this.curPaneOfRoad)[this.curPosOfRoad - 1] = this;

			// refresh road speed
			if (currentSpeed < curRoad.positiveRoadSpeedLimit.get(curPaneOfRoad)) {
				curRoad.positiveRoadSpeedLimit.set(curPaneOfRoad, currentSpeed);
				curRoad.positiveSpeedLimit = currentSpeed;
			}
		} else {
			// B. the car has started
			// clear the old information
			if (this.direction) {
				roadStatus = this.currentRoad.positiveRoadStatus;
			} else {
				roadStatus = this.currentRoad.negativeRoadStatus;
			}
			roadStatus.get(this.curPaneOfRoad)[this.curPosOfRoad - 1] = null;
			// set the sum info
			this.currentRoad.currentCarSum --;
			
			// set the new information
			this.currentRoad = curRoad;
			this.direction = direction;
			if (this.direction) {
				roadStatus = this.currentRoad.positiveRoadStatus;
			} else {
				roadStatus = this.currentRoad.negativeRoadStatus;
			}
			this.curPosOfRoad = positionOfRoad;
			this.curPaneOfRoad = curPaneOfRoad;
			this.currentSpeed = currentSpeed;

//			if (roadStatus.get(this.curPaneOfRoad)[this.curPosOfRoad - 1] != null) {// TODO: debug point
//				System.err.println("error debug point Vehicle Line 205");
//			}
			roadStatus.get(curPaneOfRoad)[this.curPosOfRoad - 1] = this;
			
			// refresh the speed of road
			if (currentSpeed < curRoad.negativeRoadSpeedLimit.get(curPaneOfRoad)) {
				curRoad.negativeRoadSpeedLimit.set(curPaneOfRoad, currentSpeed);
				curRoad.negativeSpeedLimit = currentSpeed;
			}
		}
		forceMove = WAIT_FOR_MOVE;
		this.currentRoad.currentCarSum ++;
	}

	/**
	 * change the position of the car on the current road
	 * 
	 * @param curPosOfRoad
	 */
	public void changePosition(int curPosOfRoad) {
		List<Vehicle[]> roadStatus;
		if (direction) {
			roadStatus = this.currentRoad.positiveRoadStatus;
		} else {
			roadStatus = this.currentRoad.negativeRoadStatus;
		}
		roadStatus.get(curPaneOfRoad)[this.curPosOfRoad - 1] = null;
		this.curPosOfRoad = curPosOfRoad;

		if (roadStatus.get(this.curPaneOfRoad)[this.curPosOfRoad - 1] != null) {
			System.err.println("error debug point");
		}
		roadStatus.get(curPaneOfRoad)[this.curPosOfRoad - 1] = this;
		forceMove = WAIT_FOR_MOVE;
	}

	/**
	 * for the answer printing.
	 * @return
	 */
	public String getPath() {
		StringBuilder str = new StringBuilder();
		str.append("(" + this.id + ", " + this.realStartTime + ", ");
		for (int j = 0; j < passedRoads.size() - 1; ++j) {
			str.append(passedRoads.get(j).id + ", ");
		}
		str.append(passedRoads.get(passedRoads.size() - 1).id + ")");
		return str.toString();
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", startPoint=" + startPoint + ", endPoint=" + endPoint + ", maxSpeed=" + maxSpeed
				+ ", startTime=" + startTime + ", currentSpeed=" + currentSpeed + "]";
	}

}
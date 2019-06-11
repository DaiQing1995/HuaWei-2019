package com.huawei.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Road info
 * @author DaiQing
 */
public class Road {
	
	// programId -> Road
	public static Map<Integer, Road> roadDic = new HashMap<>();
	// Id -> ProgramId
	public static Map<Integer, Integer> idDic = new HashMap<>();
	
	private final Integer MAX_SPEED_LIMIT = Integer.MAX_VALUE / 2 - 10;
	
	public static int ROAD_COUNT = 0;
	
	public static int ROAD_FREE_SPACE = 0;
	
	// generated data
	public int programId;
	
	/**
	 * 0.original data 
	 */
	public int id;
	public int length;
	public int maxSpeed;
	public int roadSum;
	public boolean isMutual;
	public int startPoint;
	public int endPoint;
	
	/** 
	 * 1. generated data
	 */
	
	// 1.status
	public List<Vehicle[]> positiveRoadStatus;
	public List<Vehicle[]> negativeRoadStatus;
	// 2.each pane's speed limit, 
	public List<Integer> positiveRoadSpeedLimit;
	public List<Integer> negativeRoadSpeedLimit;
	// 3. as the weight of the road
	public Integer positiveSpeedLimit;
	public Integer negativeSpeedLimit;
	// 4. the programId indicator of cross
	public int startPointPId;
	public int endPointPId;
	// 5. current car sum
	public int currentCarSum;

	/**
	 * refresh a speed of specific pane
	 * @param pane
	 * @param direction
	 */
	public void refreshPanezSpeedInfo(int pane, boolean direction) {
		int minSpeed = maxSpeed;
		Vehicle[] vehicles;
		// 1. get pane's vehicles
		if (direction) {
			vehicles = positiveRoadStatus.get(pane);
		}else {
			vehicles = negativeRoadStatus.get(pane);
		}
		// 2. get pane's minimal speed
		for (int j = 0; j < vehicles.length; ++ j) {
			if (vehicles[j] != null && vehicles[j].currentSpeed < minSpeed) {
				minSpeed = vehicles[j].currentSpeed;
			}
		}
		// 3. refresh pane's speed and total speed.
		if (direction) {
			positiveRoadSpeedLimit.set(pane, minSpeed);
			if (minSpeed <  positiveSpeedLimit)
				positiveSpeedLimit = minSpeed;
		}else {
			negativeRoadSpeedLimit.set(pane, minSpeed);
			if (minSpeed <  negativeSpeedLimit)
				negativeSpeedLimit = minSpeed;
		}
	}
	
	/**
	 * refresh each pane's speed limit by traversing cars 
	 */
	public void refreshSpeedLimit() {
		for (int i = 0;i < positiveRoadStatus.size(); ++ i) {
			Vehicle[] vehicles = positiveRoadStatus.get(i);
			int minSpeed = Integer.MAX_VALUE / 2;
			for (int j = 0; j < vehicles.length; ++ j) {
				if (vehicles[j] != null && vehicles[j].currentSpeed < minSpeed) {
					minSpeed = vehicles[j].currentSpeed;
				}
			}
			positiveRoadSpeedLimit.set(i, minSpeed);
			if (positiveSpeedLimit > minSpeed)
				positiveSpeedLimit = minSpeed; 
		}
		for (int i = 0;i < negativeRoadStatus.size(); ++ i) {
			Vehicle[] vehicles = negativeRoadStatus.get(i);
			int minSpeed = Integer.MAX_VALUE / 2;
			for (int j = 0; j < vehicles.length; ++ j) {
				if (vehicles[j] != null && vehicles[j].currentSpeed < minSpeed) {
					minSpeed = vehicles[j].currentSpeed;
				}
			}
			negativeRoadSpeedLimit.set(i, minSpeed);
			if (negativeSpeedLimit > minSpeed)
				negativeSpeedLimit = minSpeed;
		}
	}
	
	/**
	 * Constructor
	 * @param id		road's id
	 * @param length	road's length
	 * @param maxSpeed	road's limit speed
	 * @param roadSum	road's panes
	 * @param startPoint road's start cross	
	 * @param endPoint	road's end cross
	 * @param isMutual	is road a binary way or two direction
	 */
	public Road(int id, int length, int maxSpeed, int roadSum, int startPoint, int endPoint, boolean isMutual) {
		// 0.
		this.programId = ROAD_COUNT ++;
		this.id = id;
		this.length = length;
		this.maxSpeed = maxSpeed;
		this.roadSum = roadSum;
		this.isMutual = isMutual;
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		//1.
		positiveRoadStatus = new LinkedList<Vehicle[]>();
		for (int i = 0; i < roadSum; ++ i) positiveRoadStatus.add(new Vehicle[length]);
		negativeRoadStatus = new LinkedList<Vehicle[]>();
		for (int i = 0; i < roadSum; ++ i) negativeRoadStatus.add(new Vehicle[length]);
		
		//2.
		positiveRoadSpeedLimit = new ArrayList<>();
		negativeRoadSpeedLimit = new ArrayList<>();
		for (int i = 0;i < roadSum; ++ i) {
			positiveRoadSpeedLimit.add(maxSpeed);
			if (isMutual)
				negativeRoadSpeedLimit.add(maxSpeed);
			else
				negativeRoadSpeedLimit.add(MAX_SPEED_LIMIT);
		}
		//3.
		positiveSpeedLimit = maxSpeed;
		negativeSpeedLimit = isMutual ? maxSpeed : MAX_SPEED_LIMIT;
		
		roadDic.put(programId, this);
		idDic.put(this.id, this.programId);
		
		if (isMutual)
			ROAD_FREE_SPACE += roadSum * length * 2;
		else
			ROAD_FREE_SPACE += roadSum * length;
		
		currentCarSum = 0;
	}
	
	/**
	 * after cross data input, this function executed
	 */
	public static void refreshCrossData() {
		for (int i = 0;i < ROAD_COUNT; ++ i) {
			Road road = roadDic.get(i);
			road.startPointPId = Cross.idDic.get(road.startPoint);
			road.endPointPId = Cross.idDic.get(road.endPoint);
		}
	}

	@Override
	public String toString() {
		return "Road [programId=" + programId + ", id=" + id + ", length=" + length + ", maxSpeed=" + maxSpeed
				+ ", roadSum=" + roadSum + ", isMutual=" + isMutual + ", startPoint=" + startPoint + ", endPoint="
				+ endPoint + "]";
	}
	
}
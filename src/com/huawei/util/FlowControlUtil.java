package com.huawei.util;

import com.huawei.entity.Road;
import com.huawei.entity.Vehicle;

/**
 * restrict the sum of cars on road
 * 
 * @author DaiQing
 *
 */
public class FlowControlUtil {

	public static int carSUM = 0;
	
	public static int lastTimeFinishedCar = 0;
	
//	public static LinkedList<Integer> finishedCarsCount = new LinkedList<>();
	
	public static int GetRestriction(int roadSum) {
//		if (carSUM < 20) {
//			carSUM ++;
//			return (int) ((double) Road.ROAD_FREE_SPACE / 8);
//		}
		int weight = 0;
		if (Vehicle.finishedCarDic.size() - lastTimeFinishedCar < roadSum / 4)
			weight = roadSum * 2;
		else if (Vehicle.finishedCarDic.size() - lastTimeFinishedCar > roadSum / 2)
			weight = -roadSum * 1;
//		System.err.println("last time finished cars: " + (Vehicle.finishedCarDic.size() - lastTimeFinishedCar));
//		finishedCarsCount.add(Vehicle.finishedCarDic.size() - lastTimeFinishedCar);
		lastTimeFinishedCar = Vehicle.finishedCarDic.size();
		return (int) (((double) Road.ROAD_FREE_SPACE / 13) - weight);
	}

}

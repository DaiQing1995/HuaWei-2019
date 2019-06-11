package com.huawei;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.huawei.entity.Cross;
import com.huawei.entity.Road;
import com.huawei.entity.Vehicle;
import com.huawei.pojo.CarDistance;
import com.huawei.pojo.PassCrossDirection;
import com.huawei.util.FileInputUtil;
import com.huawei.util.FlowControlUtil;
import com.huawei.util.VehicleUtil;

/**
 * the main process
 * 
 * @author DaiQing
 */
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class);

	// the car already on the road
	public static LinkedList<Integer> onRoadCarsQueue = new LinkedList<>();

	// the time line
	public static int times;

	public static void main(String[] args) {
		BasicConfigurator.configure();
		if (args.length != 4) {
			logger.error("please input args: inputFilePath, resultFilePath");
			return;
		}

		logger.info("Start...");

		String carPath = args[0];
		String roadPath = args[1];
		String crossPath = args[2];
		String answerPath = args[3];

		logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath
				+ " and answerPath = " + answerPath);

		// 1. read input files
		logger.info("start read input files");
		FileInputUtil.ReadDataWithSort(roadPath, FileInputUtil.FileType.Road);
		FileInputUtil.ReadDataWithSort(crossPath, FileInputUtil.FileType.Cross);
		FileInputUtil.ReadDataWithSort(carPath, FileInputUtil.FileType.Car);
		// init the weight metrix
		WeightMatrix.InitWeightMatrix();

		long startMillis = System.currentTimeMillis();
		// calc
		times = 0;
		// 1. start loop
		while (Vehicle.finishedCarDic.size() < Vehicle.countOfCar) {
			long curMillis = System.currentTimeMillis();
			if (Main.times > 5000)//TODO:check
				return;
			if((curMillis - startMillis) / 1000 > 500)//TODO: check
				return;
			// 1. refresh the weight of the road. @deprecated, every time do the dijkstra
			// will do the refresh, so this refresh is not needed anymore.
			// WeightMetrix.refreshMatrix();
			// 2. push time forward
			times++;
			logger.info("[check alive] current time: " + times + ", onroad cars: " + onRoadCarsQueue.size()
					+ ", finishedCars: " + Vehicle.finishedCarDic.size());
			// 3. move cars on road
			MoveAlreadyStartCars();
			// 4. move cars have not depart
			MoveCarsNotDep();
		}

		// write answer.txt get data from vehicle part
		logger.info("Start write output file");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(new File(answerPath)));
			for (int i = 0; i < Vehicle.countOfCar; ++i) {
				Integer carId = Vehicle.orderedQueue.poll();
				Vehicle vehicle = Vehicle.vehicleDic.get(carId);
				pw.println(vehicle.getPath());
			}

			// pw.close();
			// pw = new PrintWriter(new FileWriter(new File("F:\\1.haha\\tmp.txt")));
			// // need to delete part which is used for parameters settings.
			// for (int i = 0; i < FlowControlUtil.finishedCarsCount.size(); ++i) {
			// pw.println(FlowControlUtil.finishedCarsCount.get(i));
			// }
			pw.close();
		} catch (IOException e) {
			System.err.println("print answer error");
			e.printStackTrace();
		}

		logger.info("End...");
	}

	/**
	 * A. move cars which are not departed
	 * 
	 * Using the LinkedList to ensure the lower number is always in the front
	 * sequence of handling
	 */
	private static void MoveCarsNotDep() {
		LinkedList<Integer> unstartQueue = Vehicle.unstartCarQueue;
		LinkedList<Integer> newUnstartQueue = new LinkedList<>();
		int restriction = FlowControlUtil.GetRestriction(Road.ROAD_COUNT);
		int unstartSize = unstartQueue.size();
		for (int i = 0; i < unstartSize; ++i) {
			Integer carId = unstartQueue.poll();
			// restrict the flow size
			if (restriction <= onRoadCarsQueue.size()) {
				newUnstartQueue.add(carId);
				// car.checkTime = times;
				// continue;
				break;
			}
			Vehicle car = Vehicle.vehicleDic.get(carId);
			if (car.startTime <= Main.times) {
				// 2. move the car
				CarDistance carDis = VehicleUtil.startOnRoadDistance(car);
				if (carDis.nextRoadDistance == CarDistance.NO_CROSS_REACH) {
					// [result 1/2] the road have had cars, and no empty space exists.
					// unstartQueue.add(carId);
					newUnstartQueue.add(carId);
					car.checkTime = times;
				} else {
					// [result 2/2] no matter the road have cars or not, this car can move
					// update the speed limit of the road.
					car.realStartTime = Main.times;
					car.checkTime = times;
					carDis.nextRoad.refreshSpeedLimit();
					onRoadCarsQueue.add(car.id);
				}
			} else {
				// back to queue
				// unstartQueue.add(carId);
				newUnstartQueue.add(carId);
				car.checkTime = times;
			}
		}
		newUnstartQueue.addAll(unstartQueue);
		Vehicle.unstartCarQueue = newUnstartQueue;
	}

	/**
	 * B. move cars which are on the road
	 */
	private static void MoveAlreadyStartCars() {
		int unMovedCar = onRoadCarsQueue.size();
		Set<Integer> movedCarSet = new HashSet<>();
		while (unMovedCar > 0 && !onRoadCarsQueue.isEmpty()) {
			// 1. car in & out the queue
			Integer popCarId = onRoadCarsQueue.pop();
			Vehicle curCar = Vehicle.vehicleDic.get(popCarId);
			// 1.1 check if the car already moved
			// if (Vehicle.finishedCarDic.containsKey(popCarId)) {
			if (curCar.isFinish) {
				movedCarSet.add(popCarId);
				unMovedCar--;
				continue;
			} else
				onRoadCarsQueue.add(popCarId);
			// 2. check if the car has moved
			// if (curCar.id == 38516) {// 20778 debug point
			// System.err.println(curCar.forceMove);
			// System.err.println("38516");
			// }
			if (curCar.checkTime == times) {
				if (!movedCarSet.contains(popCarId)) {
					unMovedCar--;
					movedCarSet.add(popCarId);
				}
				curCar.forceMove = Vehicle.WAIT_FOR_MOVE;
				continue;
			}
			curCar.forceMove++; // avoid dead lock
			// 3. get the pane of current car
			Road curRoad = curCar.currentRoad;
			int toCrossPId;
			if (curCar.direction) {
				toCrossPId = curRoad.endPointPId;
			} else {
				toCrossPId = curRoad.startPointPId;
			}
			// 4. move cars around the cross which is the on coming cross of current car to
			// satisfy the rule of crossing of the game.
			MoveCarsOnTheCross(Cross.crossDic.get(toCrossPId));
		}
	}

	/**
	 * B.1 move cars about the cross
	 * 
	 * @param cross
	 */
	private static void MoveCarsOnTheCross(Cross cross) {
		// 1. begin to execute the cars on each road.
		Road curRoad = null;
		for (int turnDirect = 0; turnDirect < PassCrossDirection.DIRECT.length; ++turnDirect) {
			for (int indexOfRoad = 0; indexOfRoad < 4; ++indexOfRoad) {
				// loop to visit 4 roads
				indexOfRoad = indexOfRoad % 4;
				curRoad = cross.road[indexOfRoad];
				// 1. check if all cars in curRoad have moved or the road does not exist.
				if (curRoad == null || curRoad.currentCarSum == 0 || AreRdCarsToCosAllMoved(cross, curRoad)) {
					continue;
				}
				// 2. get the pane which direction is to the cross
				List<Vehicle[]> paneDirection = null;
				// List<Integer> roadSpeedLimit;
				boolean directionFlag;
				if (curRoad.startPointPId == cross.programId) {
					paneDirection = curRoad.negativeRoadStatus;
					// roadSpeedLimit = curRoad.negativeRoadSpeedLimit;
					directionFlag = false;
				} else {
					paneDirection = curRoad.positiveRoadStatus;
					// roadSpeedLimit = curRoad.positiveRoadSpeedLimit;
					directionFlag = true;
				}
				/**
				 * 3. loop for moving cars on curRoad, only execute the ${turnAround} direct or
				 * cars do not pass the cross.
				 */
				// wait flag is made to ensure the order of direction, also is to ensure the
				// access of front car
				boolean[] waitFlagOfPane = new boolean[curRoad.roadSum];
				for (int i = 0; i < waitFlagOfPane.length; ++i) {
					waitFlagOfPane[i] = false;
				}
				// begin loop
				CarDistance carDis = null;
				for (int i = curRoad.length - 1; i >= 0; --i) { // rule: position from front to tail
					for (int j = 0; j < curRoad.roadSum; ++j) { // rule: pane from low to high
						if (waitFlagOfPane[j])
							continue;
						if (paneDirection.get(j)[i] != null) { // pane j, i-th unmoved car
							Vehicle curCar = paneDirection.get(j)[i];
							if (curCar.checkTime == Main.times)// has already moved
								continue;
//							else if (curCar.checkTime < Main.times - 1) {// TODO: debug point
//								System.err.println("error, last time doesn't go");
//							}

							// get the distance of the current car can move.
							carDis = VehicleUtil.moveRoadDistance(curRoad, curCar, i, j, directionFlag, turnDirect);

							// if (curCar.id == 38516 && !carDis.isFrontCarHinderTheWay) {// 20362 car debug
							// point
							// System.err.println(curCar.forceMove);
							// System.out.println(Main.times);
							// System.err.println("what!!!");
							// }

							if (carDis.isFrontCarHinderTheWay) {
								// 3. if the front car has not go away or the front road's car has not go away,
								// just continue. In method of revoker, the queue will help finish all the cars
								// on other roads.
								waitFlagOfPane[j] = true;
							} else if (carDis.nextRoadDistance == CarDistance.FINISHED_JOURNEY) {
								// 1. if the car finished its journey
								Vehicle.finishedCarDic.put(curCar.id, curCar);
								curCar.isFinish = true;
								paneDirection.get(j)[i] = null;
								curRoad.refreshPanezSpeedInfo(j, directionFlag);
								curRoad.currentCarSum--;
								// update this car's time status
								curCar.checkTime = times;
							} else if (carDis.nextRoadDistance == CarDistance.NO_CROSS_REACH) {
								// 2. if the car is not going to pass
								// set the car's position of the road and passed time
								curCar.changePosition(carDis.remainDistance + (i + 1));
								// update this car's time status
								curCar.checkTime = times;
							} else {
								// 4. if the car is going to pass and cars do not hinder its way
								Road nextRoad = carDis.nextRoad;
								// check if the car nextRoadDistance gt 0 to set its position
								if (carDis.nextRoadDistance == 0) {
									curCar.changePosition(carDis.remainDistance + (i + 1));
								} else {
									// current state doesn't handle this direction
									if (GetDirection(cross, curRoad, nextRoad) != turnDirect)
										continue;
									if (Cross.crossDic.get(carDis.nextRoad.startPointPId) == cross) {
										curCar.changeRoadAndPosition(nextRoad, true, carDis.nextRoadDistance,
												carDis.nextRoadPane, curCar.currentSpeed);
										nextRoad.refreshPanezSpeedInfo(carDis.nextRoadPane, true);
									} else {
										curCar.changeRoadAndPosition(nextRoad, false, carDis.nextRoadDistance,
												carDis.nextRoadPane, curCar.currentSpeed);
										nextRoad.refreshPanezSpeedInfo(carDis.nextRoadPane, false);
									}
									curRoad.refreshPanezSpeedInfo(j, directionFlag);
								}
								// update this car's time status
								curCar.checkTime = times;
							}
						}
					}
				}
			} // end for (4 road)
		} // end for (3 direction)
	}

	/**
	 * B.1.1 check if all the car forwarding to the cross have moved.
	 * 
	 * @param cross
	 * @return
	 */
	// private static boolean AreCarsToCosAllMoved(Cross cross) {
	// Road[] road = cross.road;
	// for (int i = 0; i < road.length; ++i) {
	// if (road[i] != null && !AreRdCarsToCosAllMoved(cross, road[i])) {
	// return false;
	// }
	// }
	// return true;
	// }

	/**
	 * B.1.2 check if all the car from a specific road forwarding to the cross have
	 * moved.
	 * 
	 * @param cross
	 * @return
	 */
	private static boolean AreRdCarsToCosAllMoved(Cross cross, Road road) {
		List<Vehicle[]> checkRoad;
		if (Cross.crossDic.get(road.startPointPId) == cross) {
			checkRoad = road.negativeRoadStatus;
		} else {
			checkRoad = road.positiveRoadStatus;
		}
		for (int i = 0; i < checkRoad.size(); ++i) {
			for (int j = 0; j < road.length; ++j) {
				Vehicle car = checkRoad.get(i)[j];
				if (car != null && car.checkTime != Main.times)
					return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param cross
	 * @param from
	 * @param to
	 * @return
	 */
	private static int GetDirection(Cross cross, Road from, Road to) {
		int indexFrom;
		for (indexFrom = 0; cross.road[indexFrom] != from; ++indexFrom)
			;
		if (cross.road[(indexFrom + 2) % 4] == to)
			return PassCrossDirection.STRIGHT;
		else if (cross.road[(indexFrom + 3) % 4] == to)
			return PassCrossDirection.RIGHT;
		else if (cross.road[(indexFrom + 1) % 4] == to)
			return PassCrossDirection.LEFT;
		System.out.println("error Get Direction, Line:346");
		return indexFrom;
	}

}
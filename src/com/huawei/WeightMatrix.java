package com.huawei;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.huawei.entity.Cross;
import com.huawei.entity.Road;

/**
 * The dynamic status of weight metrix
 * 
 * @author DaiQing
 */
public class WeightMatrix {
	// Tabu point of dijksta.
	public final static int TABU_NOTEXIST = -2;

	// MAX Distance value
	private final static int NON_REACH = -10;

	// non visited pre
	private final static int NON_VISITED = -1;

	private static int[][] matrix;

	// Use program id to indicate each road
	private static List<Integer> roadIds;
	// Use program id to indicate each cross
	private static List<Integer> crossIds;
	
	private final static int PUNISH_WEIGHT = 2;

	/**
	 * refresh the weight metrix of the road
	 * @throws Exception 
	 */
	public static void refreshMatrix() throws Exception {
		// 1. reset the value
		for (int i = 0; i < matrix.length; ++i) {
			for (int j = 0; j < matrix.length; ++j) 
					matrix[i][j] = NON_REACH;
		}
		for (int j = 0;j < matrix.length; ++ j)
			matrix[j][j] = 0;
		// 2. reset the road weight
		for (int i = 0, length = roadIds.size(); i < length; ++i) {
			Road road = Road.roadDic.get(roadIds.get(i));
			int st = road.startPointPId;
			int ed = road.endPointPId;

			int weight = GetFreeWeight(road, true);
			if (weight == 0) {
				weight = -500;
			}
			// set value
			matrix[st][ed] = 5000 - road.positiveSpeedLimit * weight;
//			if (matrix[st][ed] < 0) {// TODO: debug point
//				System.err.println(" matrix negative value WeightMatrix Line 64");
//				throw new Exception();
//			}
			if (road.isMutual) {
				matrix[ed][st] = 5000 - road.positiveSpeedLimit * weight;
//				if (matrix[st][ed] < 0) {// TODO: debug point
//					System.err.println(" matrix negative value WeightMatrix Line 70");
//					throw new Exception();
//				}
			} else {
				matrix[ed][st] = NON_REACH;
			}
		}
	}

	/**
	 * Initialize the weight matrix.
	 */
	public static void InitWeightMatrix() {
		Iterator<Integer> iterator = Road.roadDic.keySet().iterator();
		roadIds = new ArrayList<>();
		while (iterator.hasNext()) {
			roadIds.add(iterator.next());
		}

		iterator = Cross.crossDic.keySet().iterator();
		crossIds = new ArrayList<>();
		while (iterator.hasNext()) {
			crossIds.add(iterator.next());
		}

		matrix = new int[Cross.CROSS_COUNT][Cross.CROSS_COUNT];

		try {
			refreshMatrix();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * programId of Cross
	 * 
	 * @param start
	 *            start Id
	 * @param end
	 *            end Id
	 * @return next Cross programId
	 */
	public static int Dijkstra(int start, int tabu, int end) {
		// 1. init U,S set
		Set<Integer> U = new HashSet<>();
		Set<Integer> S = new HashSet<>();
		U.add(start);
		for (int i = 0; i < crossIds.size(); ++i) {
			if (crossIds.get(i) != start)
				S.add(crossIds.get(i));
		}

		// 2. init weight metrix
		try {
			refreshMatrix();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (tabu != TABU_NOTEXIST)
			matrix[start][tabu] = NON_REACH;

		// 3. init the last node
		int[] last = new int[crossIds.size()];
		for (int i = 0; i < last.length; ++i) {
			last[i] = (i == start) ? i : NON_VISITED;
		}

		// 4. init distance between start and others
		int[] distance = new int[crossIds.size()];
		for (int i = 0; i < distance.length; ++i) {
			distance[i] = (i == start) ? 0 : Integer.MAX_VALUE / 2;
			if (matrix[start][i] < distance[i] && matrix[start][i] != NON_REACH) {
				distance[i] = matrix[start][i];
				last[i] = start;
			}
		}
		S.remove(crossIds.get(start));

		// Start Calculate
		while (!S.isEmpty()) {
			// 1. find new node
			int newNode = 0;
			int curMinDis = Integer.MAX_VALUE / 2 + 100; // current minimal distance
			Iterator<Integer> candidates = S.iterator();
			while (candidates.hasNext()) {
				Integer candidate = candidates.next();
				if (distance[candidate] < curMinDis) {
					curMinDis = distance[candidate];
					newNode = candidate;
				}
			}
			U.add(newNode);
			S.remove(newNode);
			// 2. refresh distance
			for (int i = 0; i < distance.length; ++i) {
				if (matrix[newNode][i] != NON_REACH && matrix[newNode][i] + distance[newNode] < distance[i]) {
					distance[i] = matrix[newNode][i] + distance[newNode];
					last[i] = newNode;
				}
			}
		}

		int curPoint = last[end];
		while (last[curPoint] != start) {
			curPoint = last[curPoint];
		}
		return curPoint;
	}

	/**
	 * the weight value set method
	 * more bigger, more better. 
	 * @param road
	 * @param direction
	 * @return
	 */
	private static int GetFreeWeight(Road road, boolean direction) {
//		List<Vehicle[]> roadStatus;
//		// 1. consider current road.
//		if (direction) {
//			roadStatus = road.positiveRoadStatus;
//		} else {
//			roadStatus = road.negativeRoadStatus;
//		}
		int roadspace = road.roadSum * road.length;
		if (road.isMutual)
			roadspace *= 2;
		int carsCount = road.currentCarSum;
		if (carsCount < 0 || carsCount > roadspace) {//debug point for weight WeightMatrix: Line177
			System.err.println("road cars count info error, WeightMatrix: Line177");
		}
		// 2. consider arounded roads
		
		Set<Road> roadSet = new HashSet<>();
		Cross startCross = Cross.crossDic.get(road.startPointPId);
		Cross endCross = Cross.crossDic.get(road.endPointPId);
		for (int i = 0;i < 4; ++ i) {
			if (startCross.road[i] != null && startCross.road[i] != road)
				roadSet.add(startCross.road[i]);
			if (endCross.road[i] != null && endCross.road[i] != road)
				roadSet.add(endCross.road[i]);
		}
		Road[] otherRoads = new Road[roadSet.size()]; 
		roadSet.toArray(otherRoads);

		int otherRoadCarsCount = 0;
		int otherRoadFreeSpace = 0;
		for (int i = 0; i < otherRoads.length; ++ i) {
			Road curRoad = otherRoads[i];
			otherRoadFreeSpace += curRoad.length * curRoad.roadSum;
			if (curRoad.isMutual)
				otherRoadFreeSpace += curRoad.length * curRoad.roadSum * 2;
			else
				otherRoadFreeSpace += curRoad.length * curRoad.roadSum;
			otherRoadCarsCount += curRoad.currentCarSum;
		}
		int punishVal = (int) (PUNISH_WEIGHT * (double) otherRoadCarsCount / (double )otherRoadFreeSpace);
		double baseVal = 1.0 - ((double) carsCount / (double) roadspace);
		// set 
		if (baseVal < 0.1)
			return 0;
		int ret = (int) (50 * baseVal - punishVal) + 1;
		return ret;
	}

	public static void main(String[] args) {
		int carsCount = 103;
		int roadspace = 103;
		int value = (int) (100 * (1.0 - ((double) carsCount / (double) roadspace))) + 1;
		System.out.println(value);
	}

}
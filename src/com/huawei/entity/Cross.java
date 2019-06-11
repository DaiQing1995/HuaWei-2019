package com.huawei.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Cross
 * 
 * @author DaiQing
 *
 */
public class Cross {
	// ProgramId -> Cross
	public static Map<Integer, Cross> crossDic = new HashMap<>();
	// Id -> ProgramId
	public static Map<Integer, Integer> idDic = new HashMap<>();

	public static int CROSS_COUNT = 0;

	// the id in the program, as for the easy use of matrix (0 ~ n-1)
	public int programId;

	// the corss id in the input file
	public int id;
	// four directions roads: north, east, south, west
	public Road[] road;

	public Cross(int id, Road north, Road east, Road south, Road west) {
		programId = CROSS_COUNT++;

		this.id = id;
		road = new Road[4];

		int i = 0;
		road[i++] = north;
		road[i++] = east;
		road[i++] = south;
		road[i++] = west;

		crossDic.put(programId, this);
		idDic.put(this.id, this.programId);
	}

	@Override
	public String toString() {
		return "Cross [programId=" + programId + ", id=" + id + ", road=" + Arrays.toString(road) + "]";
	}

}

package com.huawei.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class DjkstraTest {

	@Test
	public void DjTest() {
		int M = Integer.MAX_VALUE / 2 - 10;

		// 1. init U,S set
		int start = 0;
		Set<Integer> U = new HashSet<>();
		Set<Integer> S = new HashSet<>();
		U.add(start);
		for (int i = 0; i < 5; ++i) {
			if (i != 0)
				S.add(i);
		}

		// 2. init weight metrix
		int[][] matrix = { { 0, 4, M, 2, M }, { 4, 0, 4, 1, M }, { M, 4, 0, 1, 3 }, { 2, 1, 1, 0, 7 },
				{ M, M, 3, 7, 0 } };

		// 4. init the last node
		int[] last = new int[5];
		for (int i = 0; i < last.length; ++i) {
			last[i] = (i == start) ? i : -1;
		}

		// 3. init distance between start and others
		int[] distance = new int[5];
		for (int i = 0; i < distance.length; ++i) {
			distance[i] = (i == start) ? 0 : M;
			if (matrix[start][i] < distance[i]) {
				distance[i] = matrix[start][i];
				last[i] = start;
			}
		}

		// Start Calculate
		while (!S.isEmpty()) {
			// 1. find new node
			int newNode = 0;
			int curMinDis = M; // current minimal distance
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
				if (matrix[newNode][i] + distance[newNode] < distance[i]) {
					distance[i] = matrix[newNode][i] + distance[newNode];
					last[i] = newNode;
				}
			}
		}
		for (int i = 0; i < distance.length; ++i)
			System.out.print(distance[i] + " ");
		System.out.println();

		int curPoint = last[4];
		while (last[curPoint] != 0) {
			System.out.print(curPoint + " ");
			curPoint = last[curPoint];
		}
		System.out.println("next node: " + curPoint);
	}

}

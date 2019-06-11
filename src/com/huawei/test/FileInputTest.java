package com.huawei.test;


import org.junit.Test;

import com.huawei.entity.Vehicle;
import com.huawei.util.FileInputUtil;

public class FileInputTest {

	@Test
	public void threeFileTest() {
		FileInputUtil.ReadDataWithSort("F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-1\\road.txt", FileInputUtil.FileType.Road);
		FileInputUtil.ReadDataWithSort("F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-1\\cross.txt", FileInputUtil.FileType.Cross);
		FileInputUtil.ReadDataWithSort("F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-1\\car.txt", FileInputUtil.FileType.Car);

//		for (int i = 0; i < Road.ROAD_COUNT; ++i)
//			System.out.println(Road.roadDic.get(i));
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//		
//		for (int i = 0; i < Cross.CROSS_COUNT; ++i)
//			System.out.println(Cross.crossDic.get(i));
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
//		Iterator<Integer> iterator = Vehicle.vehicleDic.keySet().iterator();
//		while (iterator.hasNext()) {
//			Integer next = iterator.next();
//			Vehicle vehicle = Vehicle.vehicleDic.get(next);
//			System.out.println(vehicle.id + " " + vehicle.startTime);
//		}
		for (int i = 0;i < Vehicle.unstartCarQueue.size(); ++ i) {
			Vehicle vehicle = Vehicle.vehicleDic.get(Vehicle.unstartCarQueue.poll());
			System.out.println(vehicle.id + " " + vehicle.startTime);
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

}

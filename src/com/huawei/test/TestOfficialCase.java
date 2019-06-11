package com.huawei.test;

import org.junit.Ignore;
import org.junit.Test;

import com.huawei.Main;
import com.huawei.entity.Road;
import com.huawei.entity.Vehicle;
import com.huawei.util.FileInputUtil;
import com.huawei.util.FlowControlUtil;

public class TestOfficialCase {

//	@Test
	@Ignore
	public void test() {
		String officialTestBase = "I:\\huawei2019\\SDK\\SDK_java\\bin\\config_";
		for (int i = 1; i <= 10; ++ i) {
			System.out.println("test case: " + i);
			FileInputUtil.flag = 0b000;
			FilePath.carPath = officialTestBase + i + "\\car.txt";
			FilePath.roadPath = officialTestBase + i + "\\road.txt";
			FilePath.crossPath = officialTestBase + i + "\\cross.txt";
			FilePath.answerPath = officialTestBase + i + "\\answer.txt";
			String[] args = new String[] {FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath};
			Main.main(args);	
		}
	}
	
//	@Test
	@Ignore
	public void test2() {
//		String officialTestBase = "I:\\huawei2019\\SDK\\SDK_java\\bin\\config_";
		String officialTestBase = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config_";
		long startTimeMillis = System.currentTimeMillis();
		int i = 8;
		FileInputUtil.flag = 0b000;
		FilePath.carPath = officialTestBase + i + "\\car.txt";
		FilePath.roadPath = officialTestBase + i + "\\road.txt";
		FilePath.crossPath = officialTestBase + i + "\\cross.txt";
		FilePath.answerPath = officialTestBase + i + "\\answer.txt";
		String[] args = new String[] { FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath };
		Main.main(args);
		long endTimeMillis = System.currentTimeMillis();
		System.out.println("elapsed time: " + (endTimeMillis - startTimeMillis) + ", finished cars: " + Vehicle.finishedCarDic.size());
		System.out.println("restriction: " + FlowControlUtil.GetRestriction(Road.ROAD_COUNT));
	}
	
	@Test
//	@Ignore
	public void exam() {
//		String officialTestBase = "I:\\huawei2019\\SDK\\SDK_java\\bin\\config_";
		String officialTestBase = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-";
		long startTimeMillis = System.currentTimeMillis();
		int i = 1;
		FileInputUtil.flag = 0b000;
		FilePath.carPath = officialTestBase + i + "\\car.txt";
		FilePath.roadPath = officialTestBase + i + "\\road.txt";
		FilePath.crossPath = officialTestBase + i + "\\cross.txt";
		FilePath.answerPath = officialTestBase + i + "\\answer.txt";
		String[] args = new String[] { FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath };
		Main.main(args);
		long endTimeMillis = System.currentTimeMillis();
		System.out.println("elapsed time: " + (endTimeMillis - startTimeMillis) + ", finished cars: " + Vehicle.finishedCarDic.size());
		System.out.println("restriction: " + FlowControlUtil.GetRestriction(Road.ROAD_COUNT));
	}
	
	@Ignore
	public void test3() {
		String officialTestBase = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config";
		FileInputUtil.flag = 0b000;
		FilePath.carPath = officialTestBase + "\\car.txt";
		FilePath.roadPath = officialTestBase + "\\road.txt";
		FilePath.crossPath = officialTestBase + "\\cross.txt";
		FilePath.answerPath = officialTestBase + "\\answer.txt";
		String[] args = new String[] { FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath };
		Main.main(args);
	}
	
	public static void main(String[] args) {
		String officialTestBase = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-";
		long startTimeMillis = System.currentTimeMillis();
		int i = 1;
		FileInputUtil.flag = 0b000;
		FilePath.carPath = officialTestBase + i + "\\car.txt";
		FilePath.roadPath = officialTestBase + i + "\\road.txt";
		FilePath.crossPath = officialTestBase + i + "\\cross.txt";
		FilePath.answerPath = officialTestBase + i + "\\answer.txt";
		String[] add = new String[] { FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath };
		Main.main(add);
		long endTimeMillis = System.currentTimeMillis();
		System.out.println("elapsed time: " + (endTimeMillis - startTimeMillis) + ", finished cars: " + Vehicle.finishedCarDic.size());
		System.out.println("restriction: " + FlowControlUtil.GetRestriction(Road.ROAD_COUNT));
	}
	
}

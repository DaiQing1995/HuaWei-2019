package com.huawei.test;

import org.junit.Ignore;
import org.junit.Test;

import com.huawei.Main;

/**
 * test1
 * @author DaiQing
 *
 */
public class TestOneRoad {

	@Test
	public void testOneRoad() {
		FilePath.carPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test1\\tcar.txt";
		FilePath.roadPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test1\\troad.txt";
		FilePath.crossPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test1\\tcross.txt";
		FilePath.answerPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test1\\answer.txt";
		String[] args = new String[] {FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath};
		Main.main(args);
	}
	
	@Ignore
	public void testOneRoadTwoChannel() {
		FilePath.carPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test2\\tcar.txt";
		FilePath.roadPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test2\\troad.txt";
		FilePath.crossPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test2\\tcross.txt";
		FilePath.answerPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test2\\answer.txt";
		String[] args = new String[] {FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath};
		Main.main(args);
	}

}

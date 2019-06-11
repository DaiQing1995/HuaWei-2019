package com.huawei.test;

import org.junit.Test;

import com.huawei.Main;

public class TestThreeRoadsConnection {

	@Test
	public void test() {
		FilePath.carPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test3\\tcar.txt";
		FilePath.roadPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test3\\troad.txt";
		FilePath.crossPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test3\\tcross.txt";
		FilePath.answerPath = "F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\config\\test3\\answer.txt";
		String[] args = new String[] {FilePath.carPath, FilePath.roadPath, FilePath.crossPath, FilePath.answerPath};
		Main.main(args);
	}

}

package com.huawei.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehicleDataInputTmp implements Comparable<VehicleDataInputTmp> {
	/**
	 * original data (5 elements)
	 */
	public int id;
	public Cross startPoint;
	public Cross endPoint;
	public int maxSpeed;
	public int startTime;

	public VehicleDataInputTmp(int id, Cross startPoint, Cross endPoint, int maxSpeed, int startTime) {
		super();
		this.id = id;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.maxSpeed = maxSpeed;
		this.startTime = startTime;
	}

	@Override
	public int compareTo(VehicleDataInputTmp o) {
		if (this.id == o.id)
			return 0;
		else if (this.id > o.id)
			return 1;
		else
			return -1;
	}

	public static void main(String[] args) throws IOException {
		List<VehicleDataInputTmp> inputTmps = new ArrayList<>();

		File file = new File("F:\\1.haha\\huawei2019\\SDK\\SDK_java\\bin\\1-map-exam-1\\car.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.readLine();
		String str;
		while ((str = reader.readLine()) != null) {
			str = str.substring(1, str.length() - 1);
			String[] split = str.split(",");
			for (int i = 0; i < split.length; ++i)
				split[i] = split[i].replaceAll(" ", "");

			inputTmps.add(new VehicleDataInputTmp(Integer.parseInt(split[0]),
					Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[1]))),
					Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[2]))), Integer.parseInt(split[3]),
					Integer.parseInt(split[4])));
		}
		Collections.sort(inputTmps);
		for (int i = 0;i < inputTmps.size(); ++ i)
			System.out.println(inputTmps.get(i).id);
		reader.close();
	}
}

package com.huawei.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.huawei.entity.Cross;
import com.huawei.entity.Road;
import com.huawei.entity.Vehicle;
import com.huawei.entity.VehicleDataInputTmp;

/**
 * for the input of several files
 * 
 * @author DaiQing
 *
 */
public class FileInputUtil {

	public static int flag = 0b000;

	public static enum FileType {
		Road, Cross, Car
	}

	/**
	 * read $type data from $filePath 
	 * @param filePath
	 * @param type
	 */
	public static void ReadData(String filePath, FileType type) {
		File file = new File(filePath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
//			reader.readLine();
			String str;
			while ((str = reader.readLine()) != null) {
				if (str.contains("#"))
					continue;
				str = str.substring(1, str.length() - 1);
				String[] split = str.split(",");
				for (int i = 0;i < split.length; ++ i)	split[i] = split[i].replaceAll(" ", "");
				if (type.equals(FileType.Road)) {
					new Road(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]),
							Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]),
							Integer.parseInt(split[6]) == 1 ? true : false);
				} else if (type.equals(FileType.Cross)) {
					new Cross(Integer.parseInt(split[0]),
							Integer.parseInt(split[1]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[1]))),
							Integer.parseInt(split[2]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[2]))),
							Integer.parseInt(split[3]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[3]))),
							Integer.parseInt(split[4]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[4]))));
				} else if (type.equals(FileType.Car)) {
					new Vehicle(Integer.parseInt(split[0]), Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[1]))),
							Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[2]))), Integer.parseInt(split[3]),
							Integer.parseInt(split[4]));
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + filePath + "not found.");
		} catch (IOException e) {
			System.err.println("buffered reader read error.");
		}
		
		
		switch (type) {
		case Car:
			if (flag != 0b011) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b111;
			break;
		case Cross:
			if (flag != 0b001) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b011;
			Road.refreshCrossData();
			break;
		case Road:
			if (flag != 0b000) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b001;
			break;
		}
	}
	
	

	/**
	 * read $type data from $filePath 
	 * @param filePath
	 * @param type
	 */
	public static void ReadDataWithSort(String filePath, FileType type) {
		List<VehicleDataInputTmp> inputTmps = new ArrayList<>();
		File file = new File(filePath);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			reader.readLine();
			String str;
			while ((str = reader.readLine()) != null) {
				str = str.substring(1, str.length() - 1);
				String[] split = str.split(",");
				for (int i = 0;i < split.length; ++ i)	split[i] = split[i].replaceAll(" ", "");
				if (type.equals(FileType.Road)) {
					new Road(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]),
							Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]),
							Integer.parseInt(split[6]) == 1 ? true : false);
				} else if (type.equals(FileType.Cross)) {
					new Cross(Integer.parseInt(split[0]),
							Integer.parseInt(split[1]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[1]))),
							Integer.parseInt(split[2]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[2]))),
							Integer.parseInt(split[3]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[3]))),
							Integer.parseInt(split[4]) == -1 ? null : Road.roadDic.get(Road.idDic.get(Integer.parseInt(split[4]))));
				} else if (type.equals(FileType.Car)) {
					inputTmps.add(new VehicleDataInputTmp(Integer.parseInt(split[0]), Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[1]))),
							Cross.crossDic.get(Cross.idDic.get(Integer.parseInt(split[2]))), Integer.parseInt(split[3]),
							Integer.parseInt(split[4])));
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File " + filePath + "not found.");
		} catch (IOException e) {
			System.err.println("buffered reader read error.");
		}
		switch (type) {
		case Car:
			if (flag != 0b011) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b111;
			Collections.sort(inputTmps);
			Collections.sort(inputTmps, new Comparator<VehicleDataInputTmp>() {
				@Override
				public int compare(VehicleDataInputTmp o1, VehicleDataInputTmp o2) {
					if (o1.startTime > o2.startTime)
						return 1;
					else if (o1.startTime == o2.startTime)
						return 0;
					else
						return -1;
				}
			});
			for (int i = 0;i < inputTmps.size(); ++ i) {
				new Vehicle(inputTmps.get(i).id, inputTmps.get(i).startPoint,
						inputTmps.get(i).endPoint, inputTmps.get(i).maxSpeed,
						inputTmps.get(i).startTime);	
			}
			break;
		case Cross:
			if (flag != 0b001) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b011;
			Road.refreshCrossData();
			break;
		case Road:
			if (flag != 0b000) {
				System.err.println("File input sequence: road, cross, car");
			}
			flag = 0b001;
			break;
		}
	}
}

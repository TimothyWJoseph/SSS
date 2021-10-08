package SSSCompress.SSSCompress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BuildSoil {
	private final static int base = 256;
	private final static int setMultiplier = 25;
	private final static int setLimit = 100;
	private final static int increment = 3;
	private final static File initialFolder = new File("D:\\SOIL5");
	private final static File coreFolder = new File(initialFolder + "\\Core");
	private final static File coreFile = new File(coreFolder + "\\Core.sss");

	private static BitSet core = new BitSet(setLimit);
	private static BitSet bottomFours = new BitSet(setLimit);
	private static BitSet bottomFives = new BitSet(setLimit);
	private static BitSet bottomEights = new BitSet(setLimit);
	private static BitSet bottomNines = new BitSet(setLimit);
	private static Vector<Integer> coreSoilVector = new Vector<>();
	private static Vector<Integer> coreFours = new Vector<>();
	private static Vector<Integer> coreFives = new Vector<>();
	private static Vector<Integer> coreEights = new Vector<>();
	private static Vector<Integer> coreNines = new Vector<>();
	private static long bitSetTotal = 0;
	private static long holdoverCount = 0;
	private static int continueCount = 0;

	static void soilBuilder(int power) {
		if (!initialFolder.exists()) {
			initialFolder.mkdir();
			coreFolder.mkdir();
		}

		if (!coreFile.exists()) {
			core = coreBuild(setLimit, increment);
			generateCoreFile(coreFile.getPath());
		} else {
			core = getBaseSoilNumbers();
		}

		System.out.println("Core:");
		System.out.print(4 + ";");
		coreSoilVector.add(4);

		long currentValue = 0;
		for (int i = 0; i < setLimit; i++) {
			if (core.get(i) == false) {
				currentValue = ((i + 1) * 3) + increment + ((i / 5) * 8);
				coreSoilVector.add((int) currentValue);
				System.out.print(currentValue + ";");
			}
		}

		setCoreVectors();

		System.out.println();
		createLibrary(power);
	}

	private static void createLibrary(int power) {
		File stageFolder = null;
		for (int stage = 1; stage <= power; stage++) {
			long soilCountMax = (long) Math.pow(base, stage + 1) - holdoverCount;
			long indexTotal = 0;

			System.out.println("Stage: " + stage);
			stageFolder = new File(initialFolder + "\\Stage" + stage);
			if (!stageFolder.exists()) {
				stageFolder.mkdir();
			}

			for (int outerStage = 1; outerStage < 10000; outerStage++) {
				String folderIndex = "";
				File outerFolder = new File(initialFolder + "\\Stage" + stage + "\\" + outerStage);

				if (!outerFolder.exists()) {
					outerFolder.mkdir();
				}

				for (int innerStage = 1; innerStage <= 10000; innerStage++) {
					long soilCountCurrent = 0;
					File currentFile = new File(outerFolder + "\\" + innerStage + ".sss");
					if (!currentFile.exists()) {
						soilCountCurrent = populateFiles(10000, currentFile.getPath());
						indexTotal += soilCountCurrent;
						folderIndex += soilCountCurrent + ";";

						if (indexTotal >= soilCountMax) {
							break;
						}

						if (continueCount == 99) {
							folderIndex = folderIndex.substring(0, folderIndex.length() - 1);
							File folderIndexFile = new File(outerFolder + "\\FolderIndex.sss");

							FileWriter out;
							try {
								out = new FileWriter(folderIndexFile);
								out.write(folderIndex);
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							System.exit(1);
						} else {
							continueCount++;
						}
					} else {
						bitSetTotal += 10000;
						File folderIndexFile = new File(outerFolder + "\\FolderIndex.sss");
						if (folderIndexFile.exists()) {
							soilCountCurrent = getFileTotal(innerStage, folderIndexFile);
							indexTotal += soilCountCurrent;
							folderIndex += soilCountCurrent + ";";

							if (indexTotal >= soilCountMax) {
								break;
							}
						} else {
							System.out.println("Error: " + folderIndexFile.getPath());
						}
					}
				}

				if (outerStage > 0) {
					folderIndex = folderIndex.substring(0, folderIndex.length() - 1);
					File folderIndexFile = new File(outerFolder + "\\FolderIndex.sss");

					FileWriter out;
					try {
						out = new FileWriter(folderIndexFile);
						out.write(folderIndex);
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (indexTotal >= soilCountMax) {
					break;
				}
			}

			File stageTotal = new File(stageFolder + "\\StageTotal.sss");

			if (!stageTotal.exists()) {
				FileWriter out;
				try {
					out = new FileWriter(stageTotal);
					out.write(String.valueOf(indexTotal));
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			holdoverCount = indexTotal - soilCountMax;

			System.out.println(indexTotal);
			System.out.println(bitSetTotal);

			System.out.println("Stage: " + stage + " Completed!");
		}
	}

	private static long getFileTotal(int innerStage, File folderIndexFile) {
		int count = 0;
		long total = 0;
		String test = "";
		Scanner sc = null;
		try {
			sc = new Scanner(folderIndexFile);
			sc.useDelimiter(";");

			while (sc.hasNext()) {
				test = sc.next();
				count++;
				if (count == innerStage) {
					total = Long.valueOf(test);
					break;
				} else if (count > innerStage) {
					System.out.println("Error: " + folderIndexFile.getPath());
				}
			}
		} catch (IOException exp) {
			exp.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}

		return total;
	}

	private static void setCoreVectors() {
		for (Integer currentSoil : coreSoilVector) {
			int remainder = currentSoil % 10;
			switch (remainder) {
			case 4: {
				coreFours.add(currentSoil);
				break;
			}
			case 5: {
				coreFives.add(currentSoil);
				break;
			}
			case 8: {
				coreEights.add(currentSoil);
				break;
			}
			case 9: {
				coreNines.add(currentSoil);
				break;
			}
			}
		}
	}

	private static long populateFiles(int endCount, String filename) {
		String fileIndex = "";
		long soilTotal = 0;
		long bottomEnd = 0;
		long firstEnd = 0;
		long topEnd = 0;
		int fileCount = 0;
		byte[] file = new byte[0];
		byte[] current = new byte[0];

		long startTime = System.nanoTime();
		for (fileCount = 1; fileCount <= endCount; fileCount++) {
			// long threadStart = System.nanoTime();
			bitSetTotal++;
			BitSet currentSoil = new BitSet(setLimit);

			bottomFours.clear();
			bottomFives.clear();
			bottomEights.clear();
			bottomNines.clear();

			BitSet firstFours = new BitSet(setLimit);
			BitSet firstFives = new BitSet(setLimit);
			BitSet firstEights = new BitSet(setLimit);
			BitSet firstNines = new BitSet(setLimit);
			
			BitSet topFours = new BitSet(setLimit);
			BitSet topFives = new BitSet(setLimit);
			BitSet topEights = new BitSet(setLimit);
			BitSet topNines = new BitSet(setLimit);

			topEnd = Math.round(Math.sqrt((bitSetTotal + 1) * setMultiplier));
			bottomEnd = topEnd / 3;
			if (bottomEnd < 10) {
				bottomEnd = 10;
			}
			firstEnd = bottomEnd * 2;
		
			int coreCount = Runtime.getRuntime().availableProcessors();
			ExecutorService es = Executors.newFixedThreadPool(coreCount);
			es.execute(new BottomFoursThread(bottomEnd));
			es.execute(new BottomFivesThread(bottomEnd));
			es.execute(new BottomEightsThread(bottomEnd));
			es.execute(new BottomNinesThread(bottomEnd));
			bottomEnd -= 10;
			try {

				firstFours = es.submit(new OtherThreads(bottomEnd, firstEnd, 4)).get();
				firstFives = es.submit(new OtherThreads(bottomEnd, firstEnd, 5)).get();
				firstEights = es.submit(new OtherThreads(bottomEnd, firstEnd, 8)).get();
				firstNines = es.submit(new OtherThreads(bottomEnd, firstEnd, 9)).get();
				firstEnd -= 10;
				
				topFours = es.submit(new OtherThreads(firstEnd, topEnd, 5)).get();
				topFives = es.submit(new OtherThreads(firstEnd, topEnd, 8)).get();
				topEights = es.submit(new OtherThreads(firstEnd, topEnd, 4)).get();
				topNines = es.submit(new OtherThreads(firstEnd, topEnd, 9)).get();

			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			es.shutdown();
			try {
				es.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			currentSoil.or(bottomFives);
			currentSoil.or(bottomEights);
			currentSoil.or(bottomFours);
			currentSoil.or(bottomNines);

			currentSoil.or(firstFours);
			currentSoil.or(firstFives);
			currentSoil.or(firstEights);
			currentSoil.or(firstNines);
			
			currentSoil.or(topFours);
			currentSoil.or(topFives);
			currentSoil.or(topEights);
			currentSoil.or(topNines);

			int setTotal = 100 - currentSoil.cardinality();
			soilTotal += setTotal;
			fileIndex += setTotal + ";";
			file = currentSoil.toByteArray();
			byte[] temp = new byte[current.length + file.length];
			System.arraycopy(current, 0, temp, 0, current.length);
			System.arraycopy(file, 0, temp, current.length, file.length);
			current = temp;
		}

		long arrayEnd = System.nanoTime();
		long arrayDuration = (arrayEnd - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("Array Duration: " + arrayDuration);

		SevenZ.zipByteArray(filename, current);

		fileIndex = fileIndex.substring(0, fileIndex.length() - 1);

		FileWriter out;
		try {
			out = new FileWriter(new File(filename.substring(0, filename.length() - 4) + "Index.sss"));
			out.write(fileIndex);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("Total Duration: " + duration);

		return soilTotal;
	}

	private static class BottomFoursThread implements Runnable {
		long currentMax = 0;
		long lastSoil = 0;
		boolean done = false;

		public BottomFoursThread(long setEnd) {
			currentMax = setEnd;
		}

		public void run() {
			for (Integer currentSoil : coreFours) {
				if (currentSoil > currentMax) {
					done = true;
					break;
				}
				bottomFours = generateCurrentSoil(bottomFours, currentSoil, bitSetTotal);
				lastSoil = currentSoil;
			}
			while (!done) {
				lastSoil += 10;
				if (lastSoil > currentMax) {
					done = true;
					break;
				}
				bottomFours = generateCurrentSoil(bottomFours, lastSoil, bitSetTotal);
			}
		}
	}

	private static class BottomFivesThread implements Runnable {
		long currentMax = 0;
		long lastSoil = 0;
		boolean done = false;

		public BottomFivesThread(long setEnd) {
			currentMax = setEnd;
		}

		public void run() {
			for (Integer currentSoil : coreFives) {
				if (currentSoil > currentMax) {
					done = true;
					break;
				}
				bottomFives = generateCurrentSoil(bottomFives, currentSoil, bitSetTotal);
				lastSoil = currentSoil;
			}
			while (!done) {
				lastSoil += 10;
				if (lastSoil > currentMax) {
					done = true;
					break;
				}
				bottomFives = generateCurrentSoil(bottomFives, lastSoil, bitSetTotal);
			}
		}
	}

	private static class BottomEightsThread implements Runnable {
		long currentMax = 0;
		long lastSoil = 0;
		boolean done = false;

		public BottomEightsThread(long setEnd) {
			currentMax = setEnd;
		}

		public void run() {
			for (Integer currentSoil : coreEights) {
				if (currentSoil > currentMax) {
					done = true;
					break;
				}
				bottomEights = generateCurrentSoil(bottomEights, currentSoil, bitSetTotal);
				lastSoil = currentSoil;
			}
			while (!done) {
				lastSoil += 10;
				if (lastSoil > currentMax) {
					done = true;
					break;
				}
				bottomEights = generateCurrentSoil(bottomEights, lastSoil, bitSetTotal);
			}
		}
	}

	private static class BottomNinesThread implements Runnable {
		long currentMax = 0;
		long lastSoil = 0;
		boolean done = false;

		public BottomNinesThread(long setEnd) {
			currentMax = setEnd;
		}

		public void run() {
			for (Integer currentSoil : coreNines) {
				if (currentSoil > currentMax) {
					done = true;
					break;
				}
				bottomNines = generateCurrentSoil(bottomNines, currentSoil, bitSetTotal);
				lastSoil = currentSoil;
			}
			while (!done) {
				lastSoil += 10;
				if (lastSoil > currentMax) {
					done = true;
					break;
				}
				bottomNines = generateCurrentSoil(bottomNines, lastSoil, bitSetTotal);
			}
		}
	}

	private static class OtherThreads implements Callable<BitSet> {
		long currentEnd = 0;
		long currentMid = 0;
		int remainder = 0;
		BitSet currentSet = new BitSet(setLimit);

		public OtherThreads(long setMid, long setEnd, int digit) {
			currentEnd = setEnd;
			remainder = (int) (setMid % 10);
			if (remainder == digit) {
				currentMid = setMid;
			} else {
				currentMid = setMid + (digit - remainder);
			}
		}

		public BitSet call() {
			while (currentMid <= currentEnd) {
				currentMid += 10;
				currentSet = generateCurrentSoil(currentSet, currentMid, bitSetTotal);
			}
			return currentSet;
		}
	}

	private static BitSet generateCurrentSoil(BitSet currentSoil2, long currentSoil, long stage) {
		long offset = -1;

		if (currentSoil != 3) {
			offset = (((currentSoil - ((((currentSoil - increment) / 10) * 2) + increment)) / 2) - 1);
		}

		stage = stage * setLimit;

		int remainder = (int) (currentSoil % 10);
		long[] currentSequence = new long[4];

		switch (remainder) {
		case 4: {
			currentSequence[0] = 1;
			currentSequence[1] = 2;
			currentSequence[2] = 3;
			currentSequence[3] = 4;
			break;
		}
		case 5: {
			currentSequence[0] = 5;
			currentSequence[1] = 3;
			currentSequence[2] = 3;
			currentSequence[3] = 3;
			break;
		}
		case 8: {
			currentSequence[0] = 4;
			currentSequence[1] = 12;
			currentSequence[2] = 8;
			currentSequence[3] = 7;
			break;
		}
		case 9: {
			currentSequence[0] = 3;
			currentSequence[1] = 25;
			currentSequence[2] = 3;
			currentSequence[3] = 3;
			break;
		}
		}

		long sequenceMultiplier = currentSoil / 10;

		currentSequence[0] = currentSequence[0] + (2 * sequenceMultiplier);
		currentSequence[1] = currentSequence[1] + (15 * sequenceMultiplier);
		currentSequence[2] = currentSequence[2] + (8 * sequenceMultiplier);
		currentSequence[3] = currentSequence[3] + (3 * sequenceMultiplier);

		long loopSize = Arrays.stream(currentSequence).sum();
		long index = ((stage / loopSize) * loopSize) - stage + offset;
		int looper = 0;

		BitSet tempSoil = new BitSet(setLimit);
		while (index < setLimit) {
			if (index > -1) {
				tempSoil.set((int) index);
			}
			if (looper > 3) {
				looper = 0;
			}
			index += currentSequence[looper];
			looper++;
		}
		currentSoil2.or(tempSoil);

		return currentSoil2;
	}

	private static void generateCoreFile(String initialFile) {
		SevenZ.zipByteArray(initialFile, core.toByteArray());
	}

	static void generateBaseSoilFiles() {
		BitSet currentSoilSet = new BitSet(setLimit);
		for (Integer currentSoil : coreSoilVector) {
			int offset = -1;
			if (currentSoil != 3) {
				offset = (((currentSoil - ((((currentSoil - increment) / 10) * 2) + increment)) / 2) - 1);
			}

			int remainder = currentSoil % 10;
			int[] currentSequence = new int[4];

			switch (remainder) {
			case 4: {
				currentSequence[0] = 1;
				currentSequence[1] = 2;
				currentSequence[2] = 3;
				currentSequence[3] = 4;
				break;
			}
			case 5: {
				currentSequence[0] = 5;
				currentSequence[1] = 3;
				currentSequence[2] = 3;
				currentSequence[3] = 3;
				break;
			}
			case 8: {
				currentSequence[0] = 4;
				currentSequence[1] = 12;
				currentSequence[2] = 8;
				currentSequence[3] = 7;
				break;
			}
			case 9: {
				currentSequence[0] = 3;
				currentSequence[1] = 25;
				currentSequence[2] = 3;
				currentSequence[3] = 3;
				break;
			}
			}

			int sequenceMultiplier = currentSoil / 10;

			currentSequence[0] = currentSequence[0] + (2 * sequenceMultiplier);
			currentSequence[1] = currentSequence[1] + (15 * sequenceMultiplier);
			currentSequence[2] = currentSequence[2] + (8 * sequenceMultiplier);
			currentSequence[3] = currentSequence[3] + (3 * sequenceMultiplier);

			int loopSize = Arrays.stream(currentSequence).sum();
			int index = ((setLimit / loopSize) * loopSize) - 100 + offset;
			ArrayList<byte[]> soilSets = new ArrayList<byte[]>();
			byte[] current = core.toByteArray();
			int looper = 0;
			while (soilSets.size() < 1) {
				currentSoilSet.clear();
				if (index < -100) {
					currentSoilSet.flip(0, setLimit);
					index += 100;
				} else {
					while (index < setLimit) {
						if (index > -1) {
							currentSoilSet.set(index);
						}
						if (looper > 3) {
							looper = 0;
						}
						index += currentSequence[looper];
						looper++;
					}
				}

				current = currentSoilSet.toByteArray();

				if (current.length < 13) {
					byte[] original = current;
					current = new byte[13];
					System.arraycopy(original, 0, current, 0, original.length);
				}

				soilSets.add(current);

				index -= setLimit;
			}

			// System.out.println();

			byte[] currentSoilByte = new byte[0];
			byte[] previous = new byte[0];

			for (byte[] b : soilSets) {
				currentSoilByte = new byte[previous.length + b.length];
				System.arraycopy(previous, 0, currentSoilByte, 0, previous.length);
				System.arraycopy(b, 0, currentSoilByte, previous.length, b.length);
				previous = currentSoilByte;
			}

			String filename = "Soil\\CoreFiles\\Base" + currentSoil + "s.sss";
			SevenZ.zipByteArray(filename, currentSoilByte);
		}
	}

	private static BitSet getBaseSoilNumbers() {
		BitSet currentSoil = null;
		currentSoil = SevenZ.unzipBase(coreFile.getPath());
		return currentSoil;
	}

	private static BitSet getCurrentSoil(File currentFile, int stage, int folderCounter, int fileCounter,
			int bitSetCounter) {
		BitSet currentSoil = null;
		currentSoil = SevenZ.unzipBitSet(currentFile.getPath(), 1);

		if (currentSoil.isEmpty()) {
			if (stage == 1) {
				currentSoil = coreBuild(setLimit, increment);
			} else {
				currentSoil = complexBuild(stage, folderCounter, fileCounter);
			}

			try {
				OutputStream outputStream = new FileOutputStream(currentFile);
				outputStream.write(currentSoil.toByteArray());
				outputStream.close();
				String temp = currentFile.getPath();
				SevenZ.zip(temp, temp.substring(0, temp.length() - 3) + "sss");
				currentFile.delete();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return currentSoil;
	}

	// This method finds all Soil smaller than 'limit'.
	//It also stores found Soil in vector soil[]
	static BitSet coreBuild(int limit, int interval) {
		int currentValue = 3;
		int remainder = 0;
		int multiplier = 3;
		int originalInterval = interval;
		int tracker = 0;
		int tempInterval = 0;

		BitSet mark = new BitSet(limit);

		tracker = (currentValue * multiplier);
		tempInterval = (((tracker - increment) / 10) * 2) + increment;
		tracker = ((tracker - tempInterval) / 2) - 1;

		multiplier = 7;

		while (tracker < limit) {
			mark.set(tracker);
			tracker = (currentValue * multiplier);
			tempInterval = (((tracker - increment) / 10) * 2) + increment;
			tracker = ((tracker - tempInterval) / 2) - 1;
			multiplier += 2;
			remainder = (multiplier) % 5;
			if (remainder == 0) {
				multiplier += 2;
			}
		}

		for (int p = 0; (currentValue = ((p + 1) * 2) + interval) < limit; p++) {
			multiplier = 6;

			if (p != 0) {
				remainder = (p) % 4;
				if (remainder == 0) {
					interval += 2;
					currentValue += 2;
				}
				if (currentValue >= limit) {
					continue;
				}
			}

			tracker = (currentValue * multiplier);
			tempInterval = (((tracker - increment) / 10) * 2) + increment;
			tracker = ((tracker - tempInterval) / 2) - 1;

			// If p is not changed, then it is a soil
			if (mark.get(p) == false) {
				// Update all multiples of p
				while (tracker < limit) {
					mark.set(tracker);
					tracker = (currentValue * multiplier);
					tempInterval = (((tracker - increment) / 10) * 2) + increment;
					tracker = ((tracker - tempInterval) / 2) - 1;

					multiplier += 2;
					remainder = (multiplier) % 5;
					if (remainder == 0) {
						multiplier += 2;
					}
				}
			}
		}

		interval = originalInterval;

		System.out.println();
		// Print all soil numbers and store them in soil
		for (int p = 0; p < limit; p++) {
			if (p != 0) {
				remainder = (p) % 4;
				if (remainder == 0) {
					interval += 2;
				}
			}

			if (mark.get(p) == false) {
				currentValue = ((p + 1) * 2) + interval;
			}
		}
		return mark;
	}

	// This method finds all Soil smaller than 'limit'
	// It also stores found Soil in vector soil[]
	static BitSet complexBuild(int stage, int folderCounter, int fileCounter) {
		int currentValue = (fileCounter * base) + 1;
		int remainder = 0;
		int multiplier = 0;
		int currentInterval = increment;
		int originalInterval = currentInterval;
		int tracker = 0;
		int tempInterval = 0;
		int max = (fileCounter + 1) * base;
		BitSet currentSoil = new BitSet(setLimit);

		currentSoil = getCurrentSoil(coreFile, 3, 3, 3, 3);
		int incrementBy = increment;
		// Print all soil numbers and store them in soil
		for (int i = 0; i < setLimit; i++) {
			if (i != 0) {
				remainder = (i) % 4;
				if (remainder == 0) {
					incrementBy += 2;
				}
			}

			if (currentSoil.get(i) == false) {
				currentValue = ((i + 1) * 2) + incrementBy;
				coreSoilVector.add(i);
				System.out.print(currentValue + ";");
			}
		}
		
		BitSet mark = new BitSet(setLimit);

		tracker = (currentValue * multiplier);
		tempInterval = (((tracker - increment) / 10) * 2) + increment;
		tracker = ((tracker - tempInterval) / 2) - 1;

		multiplier = 6;

		// Update all multiples of p
		while (tracker < max) {
			mark.set(tracker);
			tracker = (currentValue * multiplier);
			tempInterval = (((tracker - increment) / 10) * 2) + increment;
			tracker = ((tracker - tempInterval) / 2) - 1;
			multiplier += 2;
			remainder = (multiplier) % 5;
			if (remainder == 0) {
				multiplier += 2;
			}
		}

		for (int p = 0; (currentValue = ((p + 1) * 2) + currentInterval) < max; p++) {
			multiplier = 6;

			if (p != 0) {
				remainder = (p) % 4;
				if (remainder == 0) {
					currentInterval += 2;
					currentValue += 2;
				}
				if (currentValue >= max) {
					continue;
				}
			}

			tracker = (currentValue * multiplier);
			tempInterval = (((tracker - increment) / 10) * 2) + increment;
			tracker = ((tracker - tempInterval) / 2) - 1;

			if (mark.get(p) == false) {
				// Update all multiples of p
				while (tracker < max) {
					mark.set(tracker);
					tracker = (currentValue * multiplier);
					tempInterval = (((tracker - increment) / 10) * 2) + increment;
					tracker = ((tracker - tempInterval) / 2) - 1;

					multiplier += 2;
					remainder = (multiplier) % 5;
					if (remainder == 0) {
						multiplier += 2;
					}
				}
			}
		}

		currentInterval = originalInterval;

		// Print all soil numbers and store them in soil
		for (int p = 0; p < max; p++) {
			if (p != 0) {
				remainder = (p) % 4;
				if (remainder == 0) {
					currentInterval += 2;
				}
			}

			if (mark.get(p) == false) {
				currentValue = ((p + 1) * 2) + currentInterval;
				System.out.print(currentValue + ";");
			}
		}
		return mark;
	}
}

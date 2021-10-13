package SSS.SSS;

import java.util.Arrays;
import java.util.BitSet;

public class SomeOpperation {
	// Code example
	static void doThisThing(long[] realCompareArray,long[] realOnesSequence, long[] realTwosSequence,long[] realThreesSequence,long[] realFoursSequence, long realTopEnd) {
		long[] exampleCompareArray = new long[] {1L,2L,3L,4L,1001L,1002L,1003L,1004L,1102L,12345671L,12345672L,12345673L,12345674L,22345671L,1234567890123451L,1234567890123452L,1234567890123453L,1234567890123454L};
		
		long startTime = System.nanoTime();
		for (int setCount = 1; setCount <= 10000; setCount++) {
			long loopStart = System.nanoTime();
			long topEnd = 1234567890123453L; //=realTopEnd
			BitSet currentBitSet = new BitSet(100);

			for (long l : exampleCompareArray) { //: realCompareArray) {				
				if (l > topEnd) {
					break;
				}

				int remainder = (int) (l % 10);
				long[] currentSequence = null;

				switch (remainder) {
				case 1: {
					currentSequence = new long[] { 2, 2, 2, 2 }; // =realOnesSequence;
					break;
				}
				case 2: {
					currentSequence = new long[] { 3, 6, 9, 12 }; // =realTwosSequence;
					break;
				}
				case 3: {
					currentSequence = new long[] { 5, 6, 7, 8 }; // =realThreesSequence;
					break;
				}
				case 4: {
					currentSequence = new long[] { 2, 14, 2, 2 }; // =realFoursSequence;
					break;
				}
				}

				currentBitSet.or(generateNewBitSet(l, currentSequence, setCount));
			}

			System.out.println(currentBitSet);
			
			long loopEnd = System.nanoTime();
			long loopDuration = (loopEnd - loopStart); // divide by 1000000 to get milliseconds.
			System.out.println("Loop Duration: " + loopDuration);
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime); // divide by 1000000 to get milliseconds.
		System.out.println("Total Duration: " + duration);
	}

	public static BitSet generateNewBitSet(long currentNumber, long[] sequence, long currentSet) {
		BitSet tempSet = new BitSet(100);
		long offset = -1;
    	
    	if (currentNumber !=3)
		{
			offset = (((currentNumber - ((((currentNumber-5)/10 )*2)+9))/2)-1);
		}
    	
		currentSet = currentSet * 100;
		long sequenceMultiplier = currentNumber / 10;

		//Example sequence
		sequence[0] = sequence[0] + (5 * sequenceMultiplier);
		sequence[1] = sequence[1] + (10 * sequenceMultiplier);
		sequence[2] = sequence[2] + (5 * sequenceMultiplier);
		sequence[3] = sequence[3] + (5 * sequenceMultiplier);

		long loopSize = Arrays.stream(sequence).sum();
		long index = ((currentSet / loopSize) * loopSize) - currentSet + offset;
		int looper = 0;

		while (index < 100) {
			if (index > -1) {
				tempSet.set((int) index);
			}
			if (looper > 3) {
				looper = 0;
			}
			index += sequence[looper];
			looper++;
		}

		return tempSet;
	}
}

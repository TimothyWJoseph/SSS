package SSS.SSS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

public class SevenZ {

	private static final int BUFFER = 13;

	public static void zip(String string, String zipFileName) {
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];

			for (int i = 0; i < 1; i++) {
				FileInputStream fi = new FileInputStream(string);
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(string.substring(string.lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unzip2billion(String in, File destination) {
		SevenZFile sevenZFile;
		try {
			sevenZFile = new SevenZFile(new File(in));

			SevenZArchiveEntry entry;
			while ((entry = sevenZFile.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}

				FileOutputStream out = new FileOutputStream(destination);
				byte[] content = new byte[(int) entry.getSize()];
				sevenZFile.read(content, 0, content.length);
				out.write(content);
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sevenZipBytesArrays(String inFile, String outFile, String zipFile, byte[] input) {
		try {
			ZipEntry entry = new ZipEntry(inFile);
			FileOutputStream fos = new FileOutputStream(inFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);

			zos.putNextEntry(entry);
			zos.write(input);
			zos.closeEntry();
			zos.finish();
			zos.flush();
			zos.close();

			File fileIn = new File(inFile);
			File fileOut = new File(outFile);
			SevenZOutputFile out = new SevenZOutputFile(fileOut);
			SevenZArchiveEntry sevenZEntry = out.createArchiveEntry(fileIn, zipFile);
			out.putArchiveEntry(sevenZEntry);

			FileInputStream in = new FileInputStream(fileIn);
			byte[] b = new byte[100000000];
			int count = 0;
			while ((count = in.read(b)) > 0) {
				out.write(b, 0, count);
			}
			in.close();
			out.closeArchiveEntry();
			out.close();
			fileIn.delete();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		System.out.println(outFile + " generated");
	}

	public static void zipBytesToFull(String inFile, String outFile, byte[] input) {
		try {
			ZipEntry entry = new ZipEntry(inFile);
			FileOutputStream fos = new FileOutputStream(inFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);

			zos.putNextEntry(entry);
			zos.write(input);
			zos.closeEntry();
			zos.finish();
			zos.flush();
			zos.close();

			File fileIn = new File(inFile);
			File fileOut = new File(outFile);
			SevenZOutputFile out = new SevenZOutputFile(fileOut);
			SevenZArchiveEntry sevenZEntry = out.createArchiveEntry(fileIn, "FullPrimesZip.sss");
			out.putArchiveEntry(sevenZEntry);

			FileInputStream in = new FileInputStream(fileIn);
			byte[] b = new byte[100000000];
			int count = 0;
			while ((count = in.read(b)) > 0) {
				out.write(b, 0, count);
			}
			in.close();
			out.closeArchiveEntry();
			out.close();
			fileIn.delete();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		System.out.println(outFile + " generated");
	}

	public static byte[] un7ZipBytes(String fileToGet) {
		byte[] toReturn = null;
		File fileIn = new File(fileToGet);
		String zipName = null;
		try {
			SevenZFile sevenZFile = new SevenZFile(fileIn);
			SevenZArchiveEntry entry;
			File curfile = null;
			while ((entry = sevenZFile.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}

				zipName = fileIn.getParent() + "\\" + entry.getName();
				curfile = new File(zipName);
				FileOutputStream out = new FileOutputStream(curfile);
				byte[] content = new byte[(int) entry.getSize()];
				sevenZFile.read(content, 0, content.length);
				out.write(content);
				out.close();
			}

			toReturn = unzipByteArray(zipName);
			sevenZFile.close();
			System.gc();
			curfile.delete();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return toReturn;
	}

	public static void zipByteArray(String fileToGenerate, byte[] input) {
		try {
			ZipEntry entry = new ZipEntry(fileToGenerate);
			FileOutputStream fos = new FileOutputStream(fileToGenerate);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);

			zos.putNextEntry(entry);
			zos.write(input);
			zos.closeEntry();
			zos.finish();
			zos.flush();
			zos.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println();
		// System.out.println(fileToGenerate + " generated");
	}

	public static byte[] unzipByteArray(String zipFile) {
		byte[] toReturn = null;

		try {
			ZipFile zf = new ZipFile(zipFile);
			Enumeration<?> e = zf.entries();
			ZipEntry entry2 = (ZipEntry) e.nextElement();
			int bytesize = (int) entry2.getSize();
			toReturn = new byte[bytesize];
			byte[] temp = new byte[0];
			InputStream is2 = zf.getInputStream(entry2);
			int buffer = bytesize;
			int count = 0;
			int length = 0;

			while ((count = (is2.read(toReturn, 0, buffer))) != -1) {
				if (count < bytesize) {
					if (length == 0) {
						temp = Arrays.copyOfRange(toReturn, 0, count);
						length = temp.length;
					} else {
						byte[] temp2 = new byte[temp.length + count];
						System.arraycopy(temp, 0, temp2, 0, temp.length);
						System.arraycopy(toReturn, 0, temp2, temp.length, count);
						temp = temp2;
						length = temp.length;
					}

					if (length == bytesize) {
						toReturn = temp;
						break;
					} else {
						buffer = bytesize - length;
					}
				} else if (count == bytesize) {
					break;
				}
			}
			is2.close();
			is2 = null;
			zf.close();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return toReturn;
	}

	public static BitSet unzipBitSet(String zipFile, int bitSet) {
		BitSet currentBitSet = null;

		try {
			ZipFile zf = new ZipFile(zipFile);
			Enumeration<?> e = zf.entries();
			ZipEntry entry2 = (ZipEntry) e.nextElement();
			long bytesize = entry2.getSize();
			byte[] d1 = new byte[BUFFER];
			byte[] temp = new byte[0];
			InputStream is2 = zf.getInputStream(entry2);
			int buffer = BUFFER;
			long count1 = 0;
			int count2 = 0;
			is2.skipNBytes(bitSet * BUFFER);

			List<byte[]> listOfByteArrays = new ArrayList<byte[]>();
			byte[] currentArray = { 41, -108, 66, 41, -108, 66, 41, -108, 66, 41, -108, 66, 9 };
			listOfByteArrays.add(currentArray);
			currentArray = new byte[] { 66, 41, -108, 66, 41, -108, 66, 41, -108, 66, 41, -108, 2 };
			listOfByteArrays.add(currentArray);
			currentArray = new byte[] { -108, 66, 41, -108, 66, 41, -108, 66, 41, -108, 66, 41, 4 };
			listOfByteArrays.add(currentArray);

			while ((count2 = (is2.read(d1, 0, buffer))) != -1) {
				count1 += count2;
				if (count1 == bytesize) {
					break;
				}
				// System.out.println(count1);

				if (count1 + buffer > bytesize) {
					buffer = (int) (bytesize - count1);
				}

				if (count2 < BUFFER) {
					if (temp.length == 0) {
						temp = Arrays.copyOfRange(d1, 0, count2);
					} else {
						int tempCount = temp.length + count2;
						if (tempCount > BUFFER) {
							count2 = BUFFER - temp.length;
							tempCount = temp.length + count2;
						}
						byte[] temp2 = new byte[tempCount];
						System.arraycopy(temp, 0, temp2, 0, temp.length);
						System.arraycopy(d1, 0, temp2, temp.length, count2);
						temp = temp2;
					}

					if (temp.length == BUFFER) {
						d1 = temp;
						temp = new byte[0];
						buffer = BUFFER;
						/**/
						currentBitSet = BitSet.valueOf(d1);
						break;

						/*
						 * boolean foundIt = false; for (byte[] arr : listOfByteArrays) { if
						 * (Arrays.equals(arr, d1 )) { foundIt = true; } }
						 * 
						 * if (!foundIt) { System.out.println(Arrays.toString(d1)); } /
						 **/
					} else {
						buffer = BUFFER - temp.length;
					}
				} else if (count2 == BUFFER || temp.length == BUFFER) {
					/**/
					currentBitSet = BitSet.valueOf(d1);
					break;

					/*
					 * boolean foundIt = false; for (byte[] arr : listOfByteArrays) { if
					 * (Arrays.equals(arr, d1 )) { foundIt = true; } }
					 * 
					 * if (!foundIt) { System.out.println(Arrays.toString(d1)); } /
					 **/
				}
			}
			// System.out.println(count1);
			is2.close();
			zf.close();

			/*
			 * if (count > 12640) { //System.out.println("Test");
			 * System.out.println(Arrays.toString(d1) + " " + count);
			 * //System.out.println(BitSet.valueOf(d1)); //System.out.println(count); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

		return currentBitSet;
	}

	public static BitSet unzipBase(String zipFile) {
		final int BUFFER = 100;
		BitSet currentBitSet = null;

		try {
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			while ((zis.getNextEntry()) != null) {
				byte[] data = new byte[BUFFER];
				if ((zis.read(data, 0, BUFFER)) != -1) {
					currentBitSet = BitSet.valueOf(data);
				}
			}
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentBitSet;
	}

	public static void testZip(String filename) {
		try {
			ZipFile zf = new ZipFile(filename);
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				String name = ze.getName();
				long uncompressedSize = ze.getSize();
				long compressedSize = ze.getCompressedSize();
				System.out.println();
				System.out.println(name);
				System.out.println(uncompressedSize);
				System.out.println(compressedSize);

			}
			zf.close();
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public static long unzipFileSize(String zipFile, int bitSet) {
		long fileSize = 0;

		try {
			ZipFile zf = new ZipFile(zipFile);
			Enumeration<?> e = zf.entries();
			ZipEntry entry2 = (ZipEntry) e.nextElement();
			fileSize = entry2.getSize();
			zf.close();
		} catch (IOException ex) {
			System.err.println(ex);
		}
		return fileSize;
	}
}
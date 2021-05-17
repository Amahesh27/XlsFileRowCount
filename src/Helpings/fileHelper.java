package Helpings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class fileHelper {

	public static File getTheNewestFile(String folderPath) {
		File theNewestFile = null;
		File dir = new File(folderPath);
		if (!dir.exists())
			return theNewestFile;
		FileFilter fileFilter = new WildcardFileFilter("*.*");
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];

		}

		return theNewestFile;
	}

	public static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] bu = new byte[1024];
			int l;
			while ((l = is.read(bu)) > 0) {
				os.write(bu, 0, l);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static String removeFileExtension(File inputFile) {
		String filewithoutExtension = FilenameUtils.removeExtension(inputFile.getName());
		return filewithoutExtension;
	}

//	public static void main(String[] args) {
//
//		System.out.println(fileHelper.getTheNewestFile("D:\\Demo"));
//
//	}

}

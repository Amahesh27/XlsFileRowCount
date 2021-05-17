package Excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.crestech.opkey.plugin.KeywordLibrary;
import com.crestech.opkey.plugin.communication.contracts.functionresult.FunctionResult;
import com.crestech.opkey.plugin.communication.contracts.functionresult.Result;

import Helpings.ExcelExtractor;
import Helpings.List;
import Helpings.fileHelper;


public class ReadExcelFile implements KeywordLibrary {
	
	public static FunctionResult getRowsCount(String filePath) throws IOException {
	
	//public static void getRowsCount(String filePath) throws IOException {

		String outFilePath = "";
		List list = new List();
		try {
			Workbook w1 = new Workbook(filePath);

			File inputFile = new File(filePath);
			String inputFileName = fileHelper.removeFileExtension(inputFile);
			String inputFilePath = inputFile.getParent();
			outFilePath = inputFilePath + "\\" + inputFileName + ".xlsx";
			w1.save(outFilePath, SaveFormat.XLSX);
			w1.dispose();
			ExcelExtractor gen = new ExcelExtractor(outFilePath, "Sheet1");
			list = gen.getTable();
			System.out.println(list.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			File file = new File(outFilePath);
			file.delete();
		}
		return Result.PASS().setOutput(list.size()).make();

	}
	
//	public static void main(String args[]) {
//		try {
//			getRowsCount("E:\\CountOfXlsRow\\auditreport.xls");
//		} catch (IOException e) {
//			System.out.println("In catch block");
//			e.printStackTrace();
//			
//		}
//	}
}


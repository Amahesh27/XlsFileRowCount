package Helpings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ExceptionHandling.InvalidArgumentsException;

public class ExcelExtractor {

	private String tempFilePath = "";
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private FileInputStream fis = null;

	private Logger logger = Logger.getGlobal();

	boolean logsFlag = false;
	boolean debug = false;

	// String dir = Context.session().getCustomLibraryFolderPath();
	String dir = System.getProperty("java.io.tmpdir");;

	public ExcelExtractor() {

	}

	public ExcelExtractor(String ExcelFilePath) throws IOException {
		if (!(ExcelFilePath.endsWith(".xls") || ExcelFilePath.endsWith(".xlsx")) || ExcelFilePath.isEmpty())
			throw new InvalidArgumentsException(
					"File Format Invalid or FilePath is empty ! Supported formats: .xls, .xlsx");
		loadXLFile(ExcelFilePath);
		System.out.println("Initializing Excel Extractor ...");
	}

	@Deprecated
	public ExcelExtractor(String ExcelFilePath, String Sheet, String treeItem) throws IOException {
		System.out.println("Initializing Excel Data Extractor ...");

		if (!(ExcelFilePath.endsWith(".xls") || ExcelFilePath.endsWith(".xlsx")) || ExcelFilePath.isEmpty())
			throw new InvalidArgumentsException(
					"File Format Invalid or FilePath is empty ! Supported formats: .xls, .xlsx");
		loadXLFile(ExcelFilePath, Sheet);
		System.out.println("Initializing Excel Data Extractor ...");
	}

	public ExcelExtractor(String ExcelFilePath, String Sheet) throws IOException {
		if (!(ExcelFilePath.endsWith(".xlsx")) || ExcelFilePath.isEmpty())
			throw new InvalidArgumentsException(
					"File Format Invalid or FilePath is empty ! Supported formats: .xls, .xlsx");
		loadXLFile(ExcelFilePath, Sheet);
		System.out.println("Initializing Excel Extractor ...");
	}

	private void loadXLFile(String Path) throws IOException {
		File xlfile = new File(Path);
		if (xlfile.exists()) {
			logger.info("Excel File found!");
			System.out.println("Excel File found on given path: " + Path);
			openExcelFile(Path);
			if (debug)
				System.out.println("Opening Excel File ...");
			logger.info("File fetched succesfully !");
		} else {
			throw new FileNotFoundException("Excel File not found at given path! Path: " + Path);
		}
	}

	private void loadXLFile(String Path, String Sheet) throws IOException {
		File xlfile = new File(Path);
		if (xlfile.exists()) {
			logger.info("Excel File found!");
			System.out.println("Excel File found on given path: " + Path);
			openExcelFile(Path, Sheet);
			if (debug)
				System.out.println("Opening Excel File ...");
			logger.info("File fetched succesfully !");
		} else {
			throw new FileNotFoundException("Excel File not found at given path! Path: " + Path);
		}
	}


	public List getTable() throws IOException {

		List list = new List();
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> row = new ArrayList<String>();

		int lastRow = sheet.getLastRowNum();

		boolean headersAdded = false;

		rowitr: for (int i = 0; i <= lastRow; i++) {

			XSSFRow fetchedRow = sheet.getRow(i);

			if (fetchedRow == null)
				continue;

			row = new ArrayList<String>();

			int columnSize = fetchedRow.getLastCellNum();
			if (headersAdded)
				columnSize = headers.size();

			colitr: for (int j = 1; j < columnSize; j++) {
				XSSFCell fetchedCell = fetchedRow.getCell(j);

				if (fetchedCell == null)
					break colitr;

				String cell = getCellValue(fetchedCell);
				// cell = cell.replace("\n", "").trim();

				if (!headersAdded) {

					if (!cell.toString().trim().isEmpty())
						headers.add(cell);
					continue colitr;

				}
				if (j == 0 && cell.isEmpty())
					break rowitr;

				row.add(cell);

			}

			if (!headers.isEmpty()) {
				headersAdded = true;
			}

			if (!row.isEmpty()) {
				list.put(row);
			}

		}

		workbook.close();

		return list;
	}

	public List getRoleTable() throws IOException {

		List list = new List();
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<String> row = new ArrayList<String>();

		int lastRow = sheet.getLastRowNum();

		boolean headersAdded = false;

		rowitr: for (int i = 0; i <= lastRow; i++) {

			XSSFRow fetchedRow = sheet.getRow(i);

			if (fetchedRow == null)
				continue;

			row = new ArrayList<String>();

			int columnSize = fetchedRow.getLastCellNum();
			if (headersAdded)
				columnSize = headers.size();

			colitr: for (int j = 0; j < columnSize; j++) {
				XSSFCell fetchedCell = fetchedRow.getCell(j);

				if (fetchedCell == null)
					break colitr;

				String cell = getCellValue(fetchedCell);
				// cell = cell.replace("\n", "").trim();

				if (!headersAdded) {

					// if (!cell.toString().trim().isEmpty())
					headers.add(cell);
					continue colitr;

				}
				if (j == 0 && cell.isEmpty())
					break rowitr;

				row.add(cell);

			}

			if (!headers.isEmpty() && !headersAdded) {
				headersAdded = true;
				list.put(headers);
			}

			if (!row.isEmpty()) {
				list.put(row);
			}

		}

		workbook.close();

		return list;
	}

	private boolean openExcelFile(String filePath) throws IOException {
		if (debug)
			System.out.println("To FIND-- XLPath: " + filePath);
		File orig_file = new File(filePath);
		// SessionPath=Context.session().getCustomLibraryFolderPath();
		tempFilePath = dir + "\\tempXL.xlsx";

		fileHelper.copyFileUsingStream(orig_file, new File(tempFilePath));
		fis = new FileInputStream(new File(tempFilePath));
		workbook = new XSSFWorkbook(fis);

		return true;
	}

	public boolean openSheet(String sheetID) throws IOException {
		if (debug)
			System.out.println("Sheet:" + sheetID);

		int sheetNum = -1;
		try {
			sheetNum = Integer.parseInt(sheetID);
			sheet = workbook.getSheetAt(sheetNum);
		} catch (NumberFormatException e) {
			sheet = workbook.getSheet(sheetID);
		}
		if (sheet == null)
			throw new InvalidArgumentsException("Sheet ID='" + sheetID + "' not found on found Excel File ! ");
		if (debug)
			System.out.println("Excel sheet:'" + sheetID + "' fetched. Last Row Number:" + sheet.getLastRowNum() + 1);
		return true;
	}

	private boolean openExcelFile(String filePath, String sheetID) throws IOException {
		if (debug)
			System.out.println("To FIND-- XLPath: " + filePath + " || Sheet:" + sheetID);
		fis = new FileInputStream(new File(filePath));
		workbook = new XSSFWorkbook(fis);

		int sheetNum = -1;
		try {
			sheetNum = Integer.parseInt(sheetID);
			sheet = workbook.getSheetAt(sheetNum);
		} catch (NumberFormatException e) {
			sheet = workbook.getSheet(sheetID);
		}
		if (sheet == null)
			throw new InvalidArgumentsException("Sheet ID='" + sheetID + "' not found on found Excel File ! ");
		if (debug)
			System.out.println("Excel sheet:'" + sheetID + "' fetched. Last Row Number:" + sheet.getLastRowNum() + 1);
		return true;
	}

	private boolean loadSheet(String sheetID) {
		// Get Sheet
		int sheetNum = -1;
		try {
			sheetNum = Integer.parseInt(sheetID);
			sheet = workbook.getSheetAt(sheetNum);
		} catch (NumberFormatException e) {
			sheet = workbook.getSheet(sheetID);
		}
		if (sheet == null)
			throw new InvalidArgumentsException("Sheet ID='" + sheetID + "' not found on found Excel File ! ");
		if (debug)
			System.out.println("Excel sheet:'" + sheetID + "' fetched. Last Row Number:" + sheet.getLastRowNum() + 1);
		return true;

	}

	private boolean loadSheet(int sheetIndex) {
		// Get Sheet
		sheet = workbook.getSheetAt(sheetIndex);

		if (sheet == null)
			throw new InvalidArgumentsException("Sheet index ='" + sheetIndex + "' not found on found Excel File ! ");
		if (debug)
			System.out
					.println("Excel sheet:'" + sheetIndex + "' fetched. Last Row Number:" + sheet.getLastRowNum() + 1);
		return true;

	}

//	private String getCellString(XSSFCell cell) {
//		if (cell == null) {
//			// [Blank] Null Cell found.
//			return "";
//		}
//
//		String cellValue = "";
//		switch (cell.getCellType()) {
//		case Cell.CELL_TYPE_STRING:
//			cellValue = cell.getStringCellValue();
//			break;
//		case Cell.CELL_TYPE_BOOLEAN:
//			cellValue = String.valueOf(cell.getBooleanCellValue());
//			break;
//		case Cell.CELL_TYPE_NUMERIC:
//			cellValue = String.valueOf(cell.getNumericCellValue());
//			break;
//		case Cell.CELL_TYPE_BLANK:
//			// cellValue = null;
//			// [Blank] Blank_type Cell found.
//		}
//		cellValue = cellValue.replaceAll("\r", "");
//		cellValue = cellValue.replaceAll("\n", "");
//		cellValue = cellValue.trim();
//
//		return cellValue;
//	}
	
	private String getCellString(XSSFCell cell) {
		if (cell == null) {
			// [Blank] Null Cell found.
			return "";
		}

		String cellValue = "";
		switch (cell.getCellType()) {
		case STRING:
			cellValue = cell.getStringCellValue();
			break;
		case BOOLEAN:
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case NUMERIC:
			cellValue = String.valueOf(cell.getNumericCellValue());
			break;
		case BLANK:
			// cellValue = null;
			// [Blank] Blank_type Cell found.
		}
		cellValue = cellValue.replaceAll("\r", "");
		cellValue = cellValue.replaceAll("\n", "");
		cellValue = cellValue.trim();

		return cellValue;
	}

//	private String getCellStringDataFieldsDef(XSSFCell cell) {
//		if (cell == null) {
//			// [Blank] Null Cell found.
//			return "";
//		}
//
//		String cellValue = "";
//		switch (cell.getCellType()) {
//		case Cell.CELL_TYPE_STRING:
//			cellValue = cell.getStringCellValue();
//			break;
//		case Cell.CELL_TYPE_BOOLEAN:
//			cellValue = String.valueOf(cell.getBooleanCellValue());
//			break;
//		case Cell.CELL_TYPE_NUMERIC:
//
//			int value = (int) Math.round(cell.getNumericCellValue());
//			cellValue = String.valueOf(value);
//
//			break;
//		case Cell.CELL_TYPE_BLANK:
//			// cellValue = null;
//			// [Blank] Blank_type Cell found.
//		}
//
//		cellValue = cellValue.replaceAll("\r", "");
//		// cellValue = cellValue.replaceAll("\n", "");
//		cellValue = cellValue.trim();
//
//		return cellValue;
//	}

	
	private String getCellStringDataFieldsDef(XSSFCell cell) {
	if (cell == null) {
		// [Blank] Null Cell found.
		return "";
	}

	String cellValue = "";
	switch (cell.getCellType()) {
	case STRING:
		cellValue = cell.getStringCellValue();
		break;
	case BOOLEAN:
		cellValue = String.valueOf(cell.getBooleanCellValue());
		break;
	case NUMERIC:

		int value = (int) Math.round(cell.getNumericCellValue());
		cellValue = String.valueOf(value);

		break;
	case BLANK:
		// cellValue = null;
		// [Blank] Blank_type Cell found.
	}

	cellValue = cellValue.replaceAll("\r", "");
	// cellValue = cellValue.replaceAll("\n", "");
	cellValue = cellValue.trim();

	return cellValue;
}
	

	public boolean isRowEmpty(Row row) {

		if (row != null) {

			for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {

				Cell cell = row.getCell(i);
				if (cell != null) {
					String str = cell.toString().trim();
					if (!str.isEmpty())
						return false;
				}

			}

		}

		return true;
	}

	// get All Sheet Name
	public ArrayList<String> getAllSheetName() {
		ArrayList<String> sheetNames = new ArrayList<String>();
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheetNames.add(workbook.getSheetName(i));
		}
		return sheetNames;

	}

	public int getHeaderRowIndex(int rowIndex) {

		int lastRow = sheet.getLastRowNum();
		rowitr: for (int i = rowIndex; i <= lastRow; i++) {
			boolean emptyRowFound = false;
			XSSFRow row = null;

			// Fetch Row
			try {
				row = sheet.getRow(i);
				if (row == null)
					emptyRowFound = true;
			} catch (NullPointerException e) {
				emptyRowFound = true;
			}

			if (emptyRowFound)
				continue rowitr;

			colitr: for (int j = 0; j < 1; j++) {
				XSSFCell cell = row.getCell(j);
				String cellvalue = cell.getStringCellValue();
				if (cellvalue.isEmpty())
					continue;
				if ((workbook.getFontAt(cell.getCellStyle().getFontIndex()).getBold()
						&& cell.getCellStyle().getFillForegroundColor() != 64)) {
					System.out.println("Header Row Found");
					return i;
				}

			}
		}
		return -1;
	}

//	public String getCellValue(XSSFCell cell) {
//		int type = cell.getCellType();
//		String str = "";
//
//		if (type == XSSFCell.CELL_TYPE_STRING) {
//
//			str = cell.getStringCellValue();
//
//		} else if (type == XSSFCell.CELL_TYPE_NUMERIC) {
//
//			// DataFormatter dataFormatter = new DataFormatter();
//			// String cellStringValue = dataFormatter.formatCellValue(cell);
//			// System.out.println ("Is shows data as show in Excel file" + cellStringValue);
//
//			double ad = cell.getNumericCellValue();
//			str = NumberToTextConverter.toText(cell.getNumericCellValue());
//
//		} else if (type == XSSFCell.CELL_TYPE_BOOLEAN) {
//
//		} else if (type == XSSFCell.CELL_TYPE_ERROR) {
//			str = "#N/A";
//		} else if (type == XSSFCell.CELL_TYPE_FORMULA) {
//
//			type = cell.getCachedFormulaResultType();
//
//			if (type == XSSFCell.CELL_TYPE_STRING) {
//
//				str = cell.getStringCellValue();
//
//			} else if (type == XSSFCell.CELL_TYPE_ERROR) {
//				str = "#N/A";
//			} else if (type == XSSFCell.CELL_TYPE_NUMERIC) {
//
//				str = NumberToTextConverter.toText(cell.getNumericCellValue());
//			}
//		} else {
//
//		}
//
//		return str;
//	}
	
	public String getCellValue(XSSFCell cell) {
		CellType type = cell.getCellType();
		String str = "";

		if (type == type.STRING) {
			str = cell.getStringCellValue();

		} else if (type == type.NUMERIC) {
			str = NumberToTextConverter.toText(cell.getNumericCellValue());

		} else if (type == type.BOOLEAN) {
			//TODO
			str=Boolean.toString(cell.getBooleanCellValue());
			
		} else if (type == type.ERROR) {
			str = "#N/A";
			
		} else if (type == type.FORMULA) {
			//type = cell.getCachedFormulaResultType();
			//str=NumberToTextConverter.toText(cell.getCachedFormulaResultType());
			str  =String.valueOf(cell.getCachedFormulaResultType());
			if (type == type.STRING) {
				str = cell.getStringCellValue();

			} else if (type == type.ERROR) {
				str = "#N/A";
			} else if (type == type.NUMERIC) {
				str = NumberToTextConverter.toText(cell.getNumericCellValue());
			}
		} else {

		}

		return str;
	}

//	public static void main(String[] args) throws IOException {
//
//		ExcelExtractor extractor = new ExcelExtractor("C:\\Users\\rishabh.kumar\\Downloads\\AllColumns.xls",
//				"Exported");
//
//	}

}

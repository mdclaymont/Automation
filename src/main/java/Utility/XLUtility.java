package Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Reporter;

import CommonClass.BaseClass;

public class XLUtility extends BaseClass {
	public static FileInputStream fi;
	public static FileOutputStream fo;
	public static XSSFWorkbook wb;
	private static XSSFSheet sheet;
	private static XSSFRow row;
	private static XSSFCell cell;
	
	public XLUtility(String expPath, String expSheetName) {
		xls_Reader(expPath,expSheetName);
	}
	
	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	xls_Reader
	 * Function Arg: 	String expPath,String expSheetName
	 * expPath==> 		Expected Path where we can get data or default download file path with current directory
	 * expSheetName==>	Expected Sheet Name if user provide and if does not provide default will be first sheet
	 * FunctionOutPut: It will Return xls_Reader
	 * ***************************************************************************************************************/
	public static void xls_Reader(String expPath, String expSheetName) {
		String actualSheetName;
		try {
			if (expPath.isEmpty() || expPath.length() < 2) {
				expPath = ".\\TestData\\Controller.xlsx";
			}
			fi = new FileInputStream(expPath);
			wb = new XSSFWorkbook(fi);
			if (!expSheetName.isEmpty() || expSheetName.trim().length() > 1) { /// Check if user provide sheet name if not then
																		/// default sheet will be first one
				int TotalSheet = wb.getNumberOfSheets(); /// Total Sheet Number
				for (int i = 0; i <= TotalSheet; i++) {
					actualSheetName = wb.getSheetName(i);
					if (expSheetName.replaceAll(" ", "").equalsIgnoreCase(actualSheetName.replaceAll(" ", ""))) {
						sheet = wb.getSheetAt(i);
						break;
					}
				}
			} else {
				sheet = wb.getSheetAt(0);
			}
			fi.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	getRowCount
	 * Function Arg:	
	 * FunctionOutPut: 	It will Return User Total Row Count
	 * ***************************************************************************************************************/

	public static int getRowCount() throws IOException
	{
		//xls_Reader(expPath,expSheetName);
		int rowcount=sheet.getLastRowNum();
		return rowcount;
	}
	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	getColCount
	 * Function Arg:	
	 * FunctionOutPut: 	It will Return User Total Column Count
	 * ***************************************************************************************************************/

	public static int getColCount(String expPath,String expSheetName) throws IOException{
		int totalColNum =row.getLastCellNum();
		return totalColNum;
	}
	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	getHeader
	 * Function Arg: 	int rowNum,int totalColNum
	 * 					rowNum==> 		Expected Row that need data
	 * 					totalColNum==>	Expected Total Column Number
	 * FunctionOutPut: 	It will Return User Header Name
	 * ***************************************************************************************************************/

	public static List<String> getHeader(int rowNum) {
		List<String> excelHeader=new ArrayList<String>();
		int totalColNum =row.getLastCellNum();
		for(int i=0;i<totalColNum;i++) {
			try {
				String headerName = null;
				Cell CkCell =sheet.getRow(rowNum).getCell(i);
				if(CkCell!=null) {
					headerName=sheet.getRow(rowNum).getCell(i).getStringCellValue();
				}	
				excelHeader.add(headerName);
			}catch(Exception e) {
			}
		}
		return excelHeader;
	}
	
	public LinkedHashMap<String,String> getCellValue(int rowNum,int ColStart,List<String> expHeader) {
		String cellValue="";
		LinkedHashMap<String,String> eachRowData=new LinkedHashMap<>();
		int totalColNum =row.getLastCellNum();
		for (int j=ColStart; j < totalColNum; j++){
			Cell CkCell = sheet.getRow(j).getCell(j);
			if (CkCell != null) // Validate if cell value is empty
			{
				switch (CkCell.getCellType()){
				case BOOLEAN:
					Boolean BCell = CkCell.getBooleanCellValue();
					cellValue= Boolean.toString(BCell);
					break;
				case STRING:
					cellValue= CkCell.getRichStringCellValue().getString();
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(CkCell)){
						Date DCell = CkCell.getDateCellValue();
						cellValue= DCell.toString();
					}
					else{
						cellValue = NumberToTextConverter.toText(CkCell.getNumericCellValue());
					}
					break;
				case FORMULA:
					cellValue= CkCell.getCellFormula().toString();
					break;
				case BLANK:
					System.out.print("");
					break;
				default:
					System.out.print("There is no value");
					cellValue= " ";
				}
			}
			else{
				cellValue= " ";
			}
			eachRowData.put(eachRowData.get(j),cellValue);
		}
		
		return eachRowData;
	}
	public LinkedHashMap<String,String> getRowValue(int rowNum,int ColStart,int totalColNum,List<String> expHeader) {
		String cellValue="";
		LinkedHashMap<String,String> eachRowData=new LinkedHashMap<>();
		int count=0;
		for (int j=ColStart; j<totalColNum; j++){
			Cell CkCell = sheet.getRow(rowNum).getCell(j);
			if (CkCell != null) // Validate if cell value is empty
			{
				switch (CkCell.getCellType()){
				case BOOLEAN:
					Boolean BCell = CkCell.getBooleanCellValue();
					cellValue= Boolean.toString(BCell).trim();
					break;
				case STRING:
					cellValue= CkCell.getRichStringCellValue().getString().trim();
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(CkCell)){
						Date DCell = CkCell.getDateCellValue();
						SimpleDateFormat objformat=new SimpleDateFormat("MM/dd/yyyy");
						cellValue=objformat.format(DCell);
					}
					else{
						cellValue = NumberToTextConverter.toText(CkCell.getNumericCellValue());
					}
					break;
				case FORMULA:
					cellValue= CkCell.getRichStringCellValue().toString().trim();
					break;
				case BLANK:
					cellValue="";
					count++;
					break;
				default:
					System.out.print("There is no value");
					cellValue= " ";
				}
			}
			else{
				cellValue= " ";
			}
			eachRowData.put(eachRowData.get(j),cellValue);
		}
		
		return eachRowData;
	}
	
	public static void getTcData(String expPath,String SheetName,String expData) {
		int rStart=0,totalColNum=0;
		String expTc=null;
		List<String> excelHeaderName=null;
		
		
	}
	/****************************************************************************************************************
	 * Author: Md Rezaul Karim 
	 * Function Name: getCellData
	 * Function Arg: String expPath,String expSheetName,int rowNum,int colNum
	 * expPath==> 		Expected Path where we can get data or default download file path with current directory
	 * expSheetName==>	Expected Sheet Name if user provide and if does not provide default will be first sheet
	 * FunctionOutPut: It will Return User Total Row Count
	 * @throws IOException 
	 * ***************************************************************************************************************/
	
	
	public static String getCellData(String expPath,String expSheetName,int rowNum,int colNum) throws IOException
	{
		if (expPath.isEmpty())						 // Check If File Path Is Empty
		{
			expPath= currentDirectory +"/src/main/java/Data/controller.xlsx";// Get System Dir
		}
		fi=new FileInputStream(expPath);
		wb=new XSSFWorkbook(fi);
		int TotalSheet = wb.getNumberOfSheets();				/// Total Sheet Number
		for (int i = 0; i <= TotalSheet; i++){
			String ActualSheetName = wb.getSheetName(i);
			if (expSheetName.isEmpty())					///Check if user provide sheet name if not then default sheet will be first one
			{
				expSheetName= ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expSheetName)){
				expSheetName = ActualSheetName;
				break;
			}
		}
		sheet=wb.getSheet(expSheetName);
		row=sheet.getRow(rowNum);
		cell=row.getCell(colNum);
		String data;
		try {
			DataFormatter formatter=new DataFormatter();
			String cellData=formatter.formatCellValue(cell);
			return cellData;
		}
		catch(Exception e)
		{
			data="";
		}
		wb.close();
		fi.close();
		return data;
	}
	
	public static void setCellData(String expPath,String expSheetName,int rowNum,int colNum,String data) throws IOException
	{
		if (expPath.isEmpty())						 // Check If File Path Is Empty
		{
			expPath= currentDirectory +"/src/main/java/Data/controller.xlsx";// Get System Dir
		}
		fi=new FileInputStream(expPath);
		wb=new XSSFWorkbook(fi);
		int TotalSheet = wb.getNumberOfSheets();				/// Total Sheet Number
		for (int i = 0; i <= TotalSheet; i++){
			String ActualSheetName = wb.getSheetName(i);
			if (expSheetName.isEmpty())					///Check if user provide sheet name if not then default sheet will be first one
			{
				expSheetName= ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expSheetName)){
				expSheetName = ActualSheetName;
				break;
			}
		}
		sheet=wb.getSheet(expSheetName);
		row=sheet.getRow(rowNum);
		cell=row.getCell(colNum);
		cell.setCellValue(data);
		fo=new FileOutputStream(expPath);
		wb.write(fo);
		wb.close();
		fi.close();
		fo.close();
	}
	
	public static Object[][] getExcelData(String expectedSheetName, String excellFilePath, String expectedTestCaseData)
		throws IOException {
		Reporter.log("******************************************Get Data From Excel Started******************************************");
		System.out.println("******************************************Get Data From Excel Started****************************************");
		int RowStart = 1,r = 0,ColStart =2,c = 0,TotalrowNum = 0, TotalcolNum = 0, FinalFlag = 0, AllTC = 0,AcTCID = 0; 
		int	ExTCFlagColStart = 0, RowArraySize = 0, ColArraySize = 0;
		List<String>excelHeaderName=new ArrayList<String>();
		double ExTCId = 0;
		String AcTCFlag = "", ExColData = "";
		Object[][] CellValue;								//Store Value to an two dimetion array
		String[] ExpTestCase = null; 						 // Declare Array Variable
		if (excellFilePath.isEmpty())						 // Check If File Path Is Empty
		{
			String CurrentPath = System.getProperty("user.dir");
			//	excellFilePath = "/" + CurrentPath + "/src/DataFile/controller.xlsx";						// Get System Dir
			excellFilePath =CurrentPath +"/src/main/java/TestData/controller.xlsx";
		}
		FileInputStream objFile = new FileInputStream(excellFilePath);
		XSSFWorkbook WorkBook = new XSSFWorkbook(objFile);
		int TotalSheet = WorkBook.getNumberOfSheets();				/// Total Sheet Number
		for (int i = 0; i <= TotalSheet; i++){
			String ActualSheetName = WorkBook.getSheetName(i);
			if (expectedSheetName.isEmpty())					///Check if user provide sheet name if not then default sheet will be first one
			{
				expectedSheetName = ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expectedSheetName)){
				expectedSheetName = ActualSheetName;
				break;
			}
		}
		XSSFSheet objsheet = WorkBook.getSheet(expectedSheetName);
		XSSFRow row = objsheet.getRow(0);
		TotalrowNum = objsheet.getLastRowNum() + 1;				//Get total Row number
		TotalcolNum = row.getLastCellNum();						//Get total column number
		if (!(expectedTestCaseData.isEmpty()))					//Check user provide Expected Test Case Data
		{
			ExpTestCase = expectedTestCaseData.split(",");
			if (ExpTestCase.length < 2) // If User Provide Number Of Test Case from first that User Want Data
			{
				RowArraySize = Integer.parseInt(ExpTestCase[0]);/// Define The Array Size
				FinalFlag = 1;
				TotalrowNum=RowArraySize+RowStart;
			}
			else if (ExpTestCase.length == 2){
				for (int i = 0; i < TotalcolNum; i++){
					ExColData = objsheet.getRow(0).getCell(i).getStringCellValue();        // Find the Expected Column Test
					if (ExpTestCase[0].toLowerCase().contains(ExColData.toLowerCase())){
						if (ExColData.toLowerCase().contains(("tcid"))){
							ExTCId = Integer.parseInt(ExpTestCase[1]);
							AllTC = 1;
							RowStart = 1;
							ExTCFlagColStart = i;
							RowArraySize = 1; /// Define The Array Size
							break;
						}
						else{
							AllTC = 2;
							ExTCFlagColStart = i;
							for (int j = 0; j < TotalrowNum; j++){
								ExColData = objsheet.getRow(j).getCell(ExTCFlagColStart).getStringCellValue();
								if (ExpTestCase[1].toLowerCase().contains(ExColData.toLowerCase())){
									RowArraySize = RowArraySize + 1;
								}
							}
							break;
						}
					}
				}
			}
		} 
		else{
			FinalFlag = 1;
			RowArraySize = TotalrowNum - RowStart;
		}
		ColArraySize = TotalcolNum - ColStart;
		CellValue = new Object[RowArraySize][ColArraySize];
		for (int j = RowStart; j < TotalrowNum; j++){
			Cell CkCellEmpty = objsheet.getRow(j).getCell(ExTCFlagColStart);
			if (AllTC == 1 && CkCellEmpty != null) // check if user TCID provide then
			{
				AcTCID = (int) CkCellEmpty.getNumericCellValue();
				if (AcTCID == ExTCId){
					FinalFlag = 1;// If Flag value 1 then get data
				}
			}
			else if (AllTC == 2 && CkCellEmpty != null){
				AcTCFlag = CkCellEmpty.getStringCellValue();
				if (AcTCFlag.toLowerCase().contains(ExpTestCase[1].toLowerCase())){
					FinalFlag = 1;
				}
			}
			if (FinalFlag == 1){
				c = 0;
				for (int k = ColStart; k < TotalcolNum; k++){
					Cell CkCell = objsheet.getRow(j).getCell(k);
					if (CkCell != null) // Validate if cell value is empty
					{
						switch (CkCell.getCellType()){
						case BOOLEAN:
							Boolean BCell = CkCell.getBooleanCellValue();
							CellValue[r][c] = Boolean.toString(BCell);
							break;
						case STRING:
							CellValue[r][c] = CkCell.getRichStringCellValue().getString();
							break;
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(CkCell)){
								Date DCell = CkCell.getDateCellValue();
								CellValue[r][c] = DCell.toString();
							}
							else{
								CellValue[r][c] = NumberToTextConverter.toText(CkCell.getNumericCellValue());
							}
							break;
						case FORMULA:
							CellValue[r][c] = CkCell.getCellFormula().toString();
							break;
						case BLANK:
							System.out.print("");
							break;
						default:
							System.out.print("There is no value");
							CellValue[r][c] = " ";
						}
					}
					else{
						CellValue[r][c] = " ";
					}
					if ((c + ColStart) != TotalcolNum) // if col does not start from begaining
					{
						c = c + 1;   		////Increase column
					}
				}
			}
			if (FinalFlag == 1 && (r + RowStart) != TotalrowNum) // if row does not strat from begaining
			{
				r = r + 1;			////Increase row
			}
			if (AllTC == 1 && FinalFlag == 1)		//if only one row data need then break loop
			{
				break;
			}
			if (AllTC == 2)
			{
				FinalFlag = 0;
			}
		}
		Reporter.log("******************************************Get Data From Excel Ended******************************************");
		System.out.println("******************************************Get Data From Excel Ended****************************************");
		WorkBook.close();
		return CellValue;
	}	
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: getExcelDataForTestNG
	*  Function Arg: String expectedSheetName, String excellFilePath,String expectedTestCaseData(User Can Get Number of Data Or Using TCID Or TCFlag)
	*  FunctionOutPut: It will Return User Defind Data From Excell Or All Data From Excell
	* 
	* ***************************************************************************************************************/
	
	public static Object[] getExcelDataForTestNG(String expectedSheetName, String excellFilePath,String expectedTestCaseData) throws IOException {
		Reporter.log("******************************************Get Data From Excel Started******************************************");
		System.out.println("******************************************Get Data From Excel Started****************************************");
		int TotalrowNum=0,TotalcolNum=0,FinalFlag=0,AllTC=0,ColStrat=2,ExRowStart=0,ExTCFlagColStart=0,AcTCID=0,AllCellDataLenth=0,AllCellDataArrayLenth=0 ;
		double ExTCId=0;
		String AcTCFlag="",AllRowValue="",ExColData="";
		Object CellValue="";
		String [] ExpTestCase = null;	//Declar Array Variable
		Object[]AllCellData;
		if (excellFilePath.isEmpty())																		//Check If File Path Is Empty 
		{
			String CurrentPath=System.getProperty("user.dir");
			//excellFilePath = "/"+ CurrentPath +"/src/DataFile/controller.xlsx";//Get System Dir
			excellFilePath = CurrentPath +"/src/main/java/TestData/controller.xlsx";
		}
		FileInputStream objFile = new FileInputStream(excellFilePath);
		XSSFWorkbook WorkBook = new XSSFWorkbook(objFile);
		/// Find Sheet Name
		int TotalSheet = WorkBook.getNumberOfSheets();
		for (int i = 0; i <= TotalSheet; i++)
		{
			String ActualSheetName =WorkBook.getSheetName(i); 
			if (expectedSheetName.isEmpty())
			{
				expectedSheetName=ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expectedSheetName))
			{
				expectedSheetName=ActualSheetName;
				break;
			}
		}
		XSSFSheet objsheet = WorkBook.getSheet(expectedSheetName);
		XSSFRow row=objsheet.getRow(0);
		TotalrowNum = objsheet.getLastRowNum();
		TotalcolNum = row.getLastCellNum();
		if(!(expectedTestCaseData.isEmpty()))
		{
			ExpTestCase=expectedTestCaseData.split(",");
			
			if (ExpTestCase.length<2)																		//If User Provide Number Of Test Case from first  that User  Want Data
			{
				TotalrowNum=Integer.parseInt(ExpTestCase[0]);
				AllCellDataLenth=TotalrowNum;///Define The Array Size
				FinalFlag=1;
			}
			else if (ExpTestCase.length==2)
			{
				for(int i=0;i<TotalcolNum;i++)
				{
					ExColData=objsheet.getRow(0).getCell(i).getStringCellValue();
					if(ExpTestCase[0].toLowerCase().contains(ExColData.toLowerCase()))
					{
						if(ExColData.toLowerCase().contains(("tcid")))
						{
							ExTCId=Integer.parseInt(ExpTestCase[1]);
							AllTC=1;
							ExTCFlagColStart=i;
							ExRowStart=1;
							AllCellDataLenth=1; ///Define The Array Size
							break;
						}
						else
						{
							AllTC=2;
							ExTCFlagColStart=i;
							for(int j=0;j<TotalrowNum;j++)
							{	
								ExColData=objsheet.getRow(j).getCell(ExTCFlagColStart).getStringCellValue();
								if(ExpTestCase[1].toLowerCase().contains(ExColData.toLowerCase()))
								{
									AllCellDataLenth=AllCellDataLenth+1;
								}
							}
							break;
						}	
					}
				}
			}
		}
		else
		{
			FinalFlag=1;
			AllCellDataLenth=TotalrowNum+1;
		}
		AllCellData = new Object[AllCellDataLenth];
		for (int j =ExRowStart; j <=TotalrowNum-1; j++)
		{
			CellValue="";
			AllRowValue="";
			Cell CkCellEmpty=objsheet.getRow(j).getCell(ExTCFlagColStart);
			if(AllTC==1 && CkCellEmpty!=null)
			{
				AcTCID=(int) CkCellEmpty.getNumericCellValue();
				if(AcTCID==ExTCId)
				{
					FinalFlag=1;//If Flag value 1 then get data
				}
			}
			else if(AllTC==2 && CkCellEmpty!=null)
			{
				AcTCFlag=CkCellEmpty.getStringCellValue();
				
				if(AcTCFlag.toLowerCase().contains(ExpTestCase[1].toLowerCase()))
				{
					FinalFlag=1;
				}
			}	
			if(FinalFlag==1)
			{	
				for (int k =ColStrat; k<=TotalcolNum-1; k++)
				{
					Cell CkCell=objsheet.getRow(j).getCell(k);
					if(CkCell!=null)														//Validate if cell value is empty
					{	
						switch (CkCell.getCellType())
						{
							case BOOLEAN:
								CellValue=CkCell.getBooleanCellValue();
								break;
							case STRING:
								CellValue=CkCell.getRichStringCellValue();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(CkCell))
								{
									CellValue=CkCell.getDateCellValue();
								} 
								else 
								{
									CellValue=CkCell.getNumericCellValue();
								}
								break;
							case FORMULA:
								CellValue=CkCell.getCellFormula();
								break;
							case BLANK:
								System.out.print("");
								break;
							default:
								System.out.print("There is no value");
								CellValue=" ";
						}
					}
					else
					{
						CellValue=" ,";
					}
					AllRowValue=AllRowValue+CellValue+",";
				}
			}
			if(FinalFlag==1)											///Check if only one data retrieve or multiple 
			{
				AllRowValue=AllRowValue.substring(0,AllRowValue.length()-1);   //Remove last comma
				AllCellData[AllCellDataArrayLenth]=AllRowValue;
				System.out.println(AllCellData[AllCellDataArrayLenth]);
				AllCellDataArrayLenth=AllCellDataArrayLenth+1;
			}
			if(AllTC==1 && FinalFlag==1 )
			{
				break;
			}
			if(AllTC==2)
			{
				FinalFlag=0;
			}
		}
		Reporter.log("******************************************Get Data From Excel Ended******************************************");
		System.out.println("******************************************Get Data From Excel Ended****************************************");
		return AllCellData;
	}
	public static Object[][] getExcelDatabACK(String excellFilePath,String expectedSheetName,String expectedTestCaseData)
			throws IOException {
			Reporter.log("******************************************Get Data From Excel Started******************************************");
			System.out.println("******************************************Get Data From Excel Started****************************************");
			int RowStart = 1,r = 0,ColStart =2,c = 0,TotalrowNum = 0, TotalcolNum = 0, FinalFlag = 0, AllTC = 0,AcTCID = 0; 
			int	ExTCFlagColStart = 0, RowArraySize = 0, ColArraySize = 0;
			double ExTCId = 0;
			String AcTCFlag = "", ExColData = "";
			Object[][] CellValue;								//Store Value to an two dimetion array
			String[] ExpTestCase = null; 						 // Declare Array Variable
			if (excellFilePath.isEmpty())						 // Check If File Path Is Empty
			{
				String CurrentPath = System.getProperty("user.dir");
				//	excellFilePath = "/" + CurrentPath + "/src/DataFile/controller.xlsx";						// Get System Dir
				excellFilePath =CurrentPath +"/src/main/java/TestData/controller.xlsx";
			}
			FileInputStream objFile = new FileInputStream(excellFilePath);
			XSSFWorkbook WorkBook = new XSSFWorkbook(objFile);
			int TotalSheet = WorkBook.getNumberOfSheets();				/// Total Sheet Number
			for (int i = 0; i <= TotalSheet; i++){
				String ActualSheetName = WorkBook.getSheetName(i);
				if (expectedSheetName.isEmpty())					///Check if user provide sheet name if not then default sheet will be first one
				{
					expectedSheetName = ActualSheetName;
					break;
				}
				else if (ActualSheetName.equalsIgnoreCase(expectedSheetName)){
					expectedSheetName = ActualSheetName;
					break;
				}
			}
			XSSFSheet objsheet = WorkBook.getSheet(expectedSheetName);
			XSSFRow row = objsheet.getRow(0);
			TotalrowNum = objsheet.getLastRowNum() + 1;				//Get total Row number
			TotalcolNum = row.getLastCellNum();						//Get total column number
			if (!(expectedTestCaseData.isEmpty()))					//Check user provide Expected Test Case Data
			{
				ExpTestCase = expectedTestCaseData.split(",");
				if (ExpTestCase.length < 2) // If User Provide Number Of Test Case from first that User Want Data
				{
					RowArraySize = Integer.parseInt(ExpTestCase[0]);/// Define The Array Size
					FinalFlag = 1;
					TotalrowNum=RowArraySize+RowStart;
				}
				else if (ExpTestCase.length == 2){
					for (int i = 0; i < TotalcolNum; i++){
						ExColData = objsheet.getRow(0).getCell(i).getStringCellValue();        // Find the Expected Column Test
						if (ExpTestCase[0].toLowerCase().contains(ExColData.toLowerCase())){
							if (ExColData.toLowerCase().contains(("tcid"))){
								ExTCId = Integer.parseInt(ExpTestCase[1]);
								AllTC = 1;
								RowStart = 1;
								ExTCFlagColStart = i;
								RowArraySize = 1; /// Define The Array Size
								break;
							}
							else{
								AllTC = 2;
								ExTCFlagColStart = i;
								for (int j = 0; j < TotalrowNum; j++){
									ExColData = objsheet.getRow(j).getCell(ExTCFlagColStart).getStringCellValue();
									if (ExpTestCase[1].toLowerCase().contains(ExColData.toLowerCase())){
										RowArraySize = RowArraySize + 1;
									}
								}
								break;
							}
						}
					}
				}
			} 
			else{
				FinalFlag = 1;
				RowArraySize = TotalrowNum - RowStart;
			}
			ColArraySize = TotalcolNum - ColStart;
			CellValue = new Object[RowArraySize][ColArraySize];
			for (int j = RowStart; j < TotalrowNum; j++){
				Cell CkCellEmpty = objsheet.getRow(j).getCell(ExTCFlagColStart);
				if (AllTC == 1 && CkCellEmpty != null) // check if user TCID provide then
				{
					AcTCID = (int) CkCellEmpty.getNumericCellValue();
					if (AcTCID == ExTCId){
						FinalFlag = 1;// If Flag value 1 then get data
					}
				}
				else if (AllTC == 2 && CkCellEmpty != null){
					AcTCFlag = CkCellEmpty.getStringCellValue();
					if (AcTCFlag.toLowerCase().contains(ExpTestCase[1].toLowerCase())){
						FinalFlag = 1;
					}
				}
				if (FinalFlag == 1){
					c = 0;
					for (int k = ColStart; k < TotalcolNum; k++){
						Cell CkCell = objsheet.getRow(j).getCell(k);
						if (CkCell != null) // Validate if cell value is empty
						{
							switch (CkCell.getCellType()){
							case BOOLEAN:
								Boolean BCell = CkCell.getBooleanCellValue();
								CellValue[r][c] = Boolean.toString(BCell);
								break;
							case STRING:
								CellValue[r][c] = CkCell.getRichStringCellValue().getString();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(CkCell)){
									Date DCell = CkCell.getDateCellValue();
									CellValue[r][c] = DCell.toString();
								}
								else{
									CellValue[r][c] = NumberToTextConverter.toText(CkCell.getNumericCellValue());
								}
								break;
							case FORMULA:
								CellValue[r][c] = CkCell.getCellFormula().toString();
								break;
							case BLANK:
								System.out.print("");
								break;
							default:
								System.out.print("There is no value");
								CellValue[r][c] = " ";
							}
						}
						else{
							CellValue[r][c] = " ";
						}
						if ((c + ColStart) != TotalcolNum) // if col does not start from begaining
						{
							c = c + 1;   		////Increase column
						}
					}
				}
				if (FinalFlag == 1 && (r + RowStart) != TotalrowNum) // if row does not strat from begaining
				{
					r = r + 1;			////Increase row
				}
				if (AllTC == 1 && FinalFlag == 1)		//if only one row data need then break loop
				{
					break;
				}
				if (AllTC == 2)
				{
					FinalFlag = 0;
				}
			}
			Reporter.log("******************************************Get Data From Excel Ended******************************************");
			System.out.println("******************************************Get Data From Excel Ended****************************************");
			WorkBook.close();
			return CellValue;
		}	
	
	
	
	

}

package CommonClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Properties;
//import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.Status;

import Pages.HomePage;
import Pages.LogInPage;
import Utility.TestListener;

public class BaseClass {
	
	public static String systemUserDir = System.getProperty("user.dir");
	public static String downloadPath = System.getProperty("user.dir") + File.separator + "downloads";
	public static String configFilePath = ".\\Configuration\\config.properties";
	public static Properties objProp;
	public static int PAGE_LOAD_TIME;
	public static int IMPLICITLY_TIME;
	public static int EXPLICITLY_TIME;
	public static String browserName;
	public static LogInPage lp;
	public static HomePage hp;
	public static Logger logger=LogManager.getLogger(BaseClass.class);
	public static WebDriver driver;
	public static SoftAssert soft=new SoftAssert();							// Declare Soft Assert
	
	
	//	Initialize Properties For Data 
	public BaseClass() {
		readProperties("");
		
		PAGE_LOAD_TIME=Integer.parseInt(objProp.getProperty("pageLoadTime"));
		IMPLICITLY_TIME=Integer.parseInt(objProp.getProperty("implicitlyWait"));
		EXPLICITLY_TIME=Integer.parseInt(objProp.getProperty("explicitlyWait"));
	}

	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name:	readProperties
	 * Function Arg: 	expPropFilePath
	 * FunctionOutPut: 	It will Return objProp Properties
	 **************************************************************************************************************/

	public static void readProperties(String expPropFilePath) {
		try {
			if (expPropFilePath.trim().isEmpty() || expPropFilePath.trim().length() < 1) {
				expPropFilePath = configFilePath;
			}
			objProp = new Properties();
			FileInputStream objFile = new FileInputStream(configFilePath);

			objProp.load(objFile);
			objFile.close();
		} catch (Exception e) {
			System.out.println("Not Able to load File >> " + e.getMessage());
			e.printStackTrace();
		}
	}

	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	getConfigData
	 * Function Arg: 	expKeyToSearch
	 * FunctionOutPut:	It will return String
	 ***************************************************************************************************************/

	public static String getConfigData(String expKeyToSearch) {
		return objProp.getProperty(expKeyToSearch);
	}
	
	//	Assert 
	
	

	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim
	 * Function Name: 	initializeDriver
	 * Function Arg:	exptBrowser
	 * FunctionOutPut: 	It will initialize Driver and Return Driver
	 ***************************************************************************************************************/

	public static WebDriver initializeDriver(String exptBrowser) {
		Reporter.log("****************************************	initilize Driver readProperties	*******************");
		String expBrowser;
		String mavenBrowserName = System.getProperty("Browser");// check if maven send any browser

		if (mavenBrowserName != null) {
			expBrowser = mavenBrowserName;
		} else if (exptBrowser.trim().isEmpty() || exptBrowser.trim().length() < 1) {

			expBrowser = objProp.getProperty("browserName");
			System.out.println(expBrowser);
		} else {
			expBrowser = exptBrowser;
		}

		if (expBrowser.replaceAll(" ", "").toLowerCase().contains("firefox") || expBrowser.contains("ff")) {
			driver = new FirefoxDriver();
		} else if (expBrowser.replaceAll(" ", "").toLowerCase().contains("internetexplorer")
				|| expBrowser.contains("ie")) {
			driver = new InternetExplorerDriver();
		} else if (expBrowser.replaceAll(" ", "").toLowerCase().contains("edge") || expBrowser.contains("ed")) {
			driver = new EdgeDriver();
		} else if (expBrowser.replaceAll(" ", "").toLowerCase().contains("safary") || expBrowser.contains("sf")) {
			driver = new SafariDriver();
		}
		// if user want headless browser then you can use it
		else if (expBrowser.contains("chromeheadless") || expBrowser.contains("headless")) {

			System.setProperty("webdriver.chrome.silentOutput", "true");// it will remove unnessary log
			HashMap<String, Object> ohp = new HashMap<String, Object>();
			ohp.put("profile.defult_content_settings.popups", 0);
			ohp.put("download.defult_directory", downloadPath); // if download any file it will save to current user dir
			ChromeOptions objoption = new ChromeOptions();
			objoption.addArguments("headless");
			objoption.setExperimentalOption("useAutomationExtension", false);
			driver = new ChromeDriver(objoption);
			expBrowser = "chrome";
		}

		else {
			driver = new ChromeDriver();
		}
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		System.out.println(PAGE_LOAD_TIME);
		System.out.println(IMPLICITLY_TIME);
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_LOAD_TIME));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICITLY_TIME));
		logger.info("Expected Browser Driver "+expBrowser+" Opened");
		browserName=expBrowser;
		 return driver;
	}
	
	//					URL
	
	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	OpenUrl 
	 * Function Arg: 	expectedUrl ==>Which Url Or Domain You want work for 
	 * FunctionOutPut: 	It will open Url That you want Automated
	 * 
	 ***************************************************************************************************************/

	public static void openUrl(String expectedUrl) {
		try {
			Reporter.log("******************************************Url Open Strated******************************************");
			System.out.println("******************************** Expected Browser Open Started		******************************");
			driver.get(expectedUrl);
			String curUrl = driver.getCurrentUrl();
			 Reporter.log("\t Expected Url ' "+expectedUrl+" ' Opend Or Lunch");
			 TestListener.test.log(Status.PASS,"\t Expected Url ' "+expectedUrl+" ' Opend Or Lunch");
			 logger.info("\t Expected Url ' "+expectedUrl+" ' Opend Or Lunch");
		} catch (Exception e) {
			 Reporter.log("\t Expected Url ' "+expectedUrl+" ' Did Not Opend Or Lunch");
			 TestListener.test.log(Status.FAIL,"\t Expected Url ' "+expectedUrl+" ' Did Not  Opend Or Lunch");
			 logger.info("\t Expected Url ' "+expectedUrl+" ' Did Not  Opend Or Lunch");
		}
		Reporter.log("******************************** Expected Browser Open Ended		******************************");
	}

	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim
	 * Function Name: 	closeBrowser
	 * Function Arg:
	 * FunctionOutPut: It will close only Current Instance browser or current browser
	 ***************************************************************************************************************/

	public static void closeBrowser() {

		Reporter.log("****************************************** Expected Browser Close Started ******************************************");
		driver.close();
		driver = null;
		Reporter.log("******************************************Expected Browser Closed ******************************************");
	}

	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim
	 * Function Name: 	quitBrowser
	 * Function Arg:
	 * FunctionOutPut: 	It will close all browser or current browser
	 ***************************************************************************************************************/

	public void quitBrowser() {
		Reporter.log("****************************************** Expected Browser Close Started ******************************************");
		driver.quit();
		driver = null;
		Reporter.log("******************************************Expected Browser Closed ******************************************");
	}
	
	/****************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name:	ValidateUrl
	*  Function Arg:	ExpectedUrl(Which Url You want work for)
	*  FunctionOutPut:	It will open Url That you want Automated
	****************************************************************************************************************/
		
	public static void validateUrl(String expURL) throws IOException{
	
		Reporter.log("******************************************Validate Url Strated******************************************");
		System.out.println("******************************************Validate Url Strated***********************************************");
		String actualUrl = driver.getCurrentUrl();
		int uIndex=expURL.indexOf("www");
		int aIndex=actualUrl.indexOf("www");
		if(uIndex>0 && aIndex<0 )
			{
			expURL=expURL.replace("www.","").trim();	
			}
		else if(uIndex<0 && aIndex>0)
			{
				actualUrl=actualUrl.replace("www.","").trim();
			} 
		int eplength = expURL.length();
		int aclength = actualUrl.length();
		int lengthDiffrent =eplength-aclength;
		if (actualUrl.equals(expURL))
			{
				System.out.println("Expected Url ****** " + expURL + " ******* Found And Validation of Url Successfully Passed");
				soft.assertTrue(true,"Expected Url ****** " + expURL + " ******* Found And Validation of Url Successfully Passed");
				logger.info("Expected Url ****** ==> " + expURL + " <== ******* Found And Validation of Url Successfully Passed");
			}
		else if (actualUrl.equalsIgnoreCase(expURL))
			{
				System.out.println("Expected Url ****** " + expURL+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
				soft.assertTrue(true, "Expected Url ****** " + expURL+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
				logger.warn("Expected Url ****** " + expURL+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
			}
		else if (actualUrl.contains(expURL))
			{	
				System.out.println("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
				soft.assertTrue(true,"Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
				logger.warn("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
			} 
		else if (actualUrl.contains(expURL.toLowerCase()))
			{
				System.out.println("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				soft.assertTrue(true, "Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				logger.warn("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
			}
		else if(lengthDiffrent>0 && lengthDiffrent<5)
			{
				if(expURL.toLowerCase().contains(actualUrl.toLowerCase()))
				{
					System.out.println("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
					soft.assertTrue(true, "Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
					logger.warn("Expected Url ****** " +expURL+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				}
			}
		else 
			{
			takeScreenShorts("","ValidateUrl");
				soft.assertFalse(false, "Expected Url ***** " + expURL+ " ***** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
				System.out.println("Expected Url ***** " + expURL+ " ***** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
				logger.error("Expected Url ****** ==> " + expURL+ " <== ****** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
			}
		Reporter.log("******************************************Validate Url Ended******************************************");
		System.out.println("******************************************Validate Url Ended*************************************************");
	}
	
	
	//**********			Input			*******
	
	public static String takeScreenShorts(String expFilePath, String expFileName) {
		File objFtc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		if (expFilePath.trim().isBlank() || expFilePath.trim().isEmpty()) {
			expFilePath = systemUserDir + "\\ScreenShorts";
		}
		if (expFileName.trim().isBlank() || expFileName.trim().isEmpty()) {
			long RNum = getRandomNumber(5);
			expFileName = "Test_" + RNum + ".png";
		}
		String curFolder = createFolder(expFilePath);
		String curFolderNFilePath = curFolder + "\\" + expFileName;
		// String expTcFilePath =createFileFolder(expFilePath,expFileName);;
		try {
			FileUtils.copyFile(objFtc, new File(curFolderNFilePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return curFolderNFilePath;
	}

	public String createFile(String expFileName) {
		if (expFileName.trim().isBlank() || expFileName.trim().isEmpty()) {
			long RNum = getRandomNumber(5);
			expFileName = "Test" + RNum;
		}

		return expFileName;

	}

	public static String createFolder(String expFolderNameAndPath) {
		// String folderPath = null;
		if (expFolderNameAndPath.trim().isBlank() || expFolderNameAndPath.trim().isEmpty()) {
			expFolderNameAndPath = systemUserDir + "\\TestFolder";
		}

		File objFc = new File(expFolderNameAndPath);
		if (!objFc.exists()) {
			objFc.mkdirs();
		}

		return expFolderNameAndPath;
	}

	public static String createFileFolder(String expFileFolderPath, String expFileName,String expFileExtention) {

		if (expFileName.trim().isBlank() || expFileName.trim().isEmpty()) {
			long RNum = getRandomNumber(5);
			expFileName = "Test_" + RNum ;
		}
		
		if (expFileExtention.isEmpty()|| expFileExtention.length()<1) {
			expFileExtention=".png";
		} 
		String curFolder = createFolder(expFileFolderPath);
		String curFolderNFilePath = curFolder + "\\" + expFileName +"."+expFileExtention;
		File objFc = new File(curFolderNFilePath);
		try {
			boolean isFileCreated = objFc.createNewFile();
			if (isFileCreated) {
				System.out.println("File Created");
			} else {
				System.out.println("File Already Created or Exist");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return curFolderNFilePath;
	}

	public static long getRandomNumber(int expDigit) {
		LocalDateTime now = LocalDateTime.now();
		String curStr = Integer.toString(now.getNano());
		if (expDigit > 8) {
			curStr = curStr + curStr;
		}
		return Long.parseLong(curStr.substring(0, expDigit));

	}
	
	//				Validate 


	
}

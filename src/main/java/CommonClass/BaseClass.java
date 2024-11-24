package CommonClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.Status;

import Pages.HomePage;
import Pages.LogInPage;
import Utility.TestListener;

public class BaseClass {
	
	public static String currentDirectory = System.getProperty("user.dir");
	public static String downloadPath = System.getProperty("user.dir") + File.separator + "downloads";
	public static String configFilePath = ".\\Configuration\\config.properties";
	public static Properties objProp;
	public static int PAGE_LOAD_TIME;
	public static int IMPLICITLY_TIME;
	public static int EXPLICITLY_TIME;
	public static String browserName;
	public static LogInPage lp;
	public static HomePage hp;
	public static WebDriver driver;
	public static JavascriptExecutor jse;
	public static FileInputStream objFile;
	public static SoftAssert soft = new SoftAssert(); // Declare Soft Assert
	public static WebDriverWait wait;
	public static Logger logger = LogManager.getLogger(BaseClass.class);
	public static Alert objAlert;
	public static Actions objAction;

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
	
	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	getConfigData
	 * Function Arg: 	expKeyToSearch
	 * FunctionOutPut:	It will return String
	 ***************************************************************************************************************/
	public static WebDriverWait expicit() {
		wait = new WebDriverWait(driver, Duration.ofSeconds(EXPLICITLY_TIME)); // Explicit Wait
		return wait;
	}
	
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
			Reporter.log(
					"******************************************Url Open Strated******************************************");
			System.out.println(
					"******************************** Expected Browser Open Started		******************************");
			driver.get(expectedUrl);
		
			Reporter.log("\t Expected Url ' " + expectedUrl + " ' Opend Or Lunch");
			TestListener.test.log(Status.PASS, "\t Expected Url ' " + expectedUrl + " ' Opend Or Lunch");
			logger.info("\t Expected Url ' " + expectedUrl + " ' Opend Or Lunch");
		} catch (Exception e) {
			Reporter.log("\t Expected Url ' " + expectedUrl + " ' Did Not Opend Or Lunch");
			TestListener.test.log(Status.FAIL, "\t Expected Url ' " + expectedUrl + " ' Did Not  Opend Or Lunch");
			logger.info("\t Expected Url ' " + expectedUrl + " ' Did Not  Opend Or Lunch");
		}
		Reporter.log(
				"******************************** Expected Browser Open Ended		******************************");
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
	
	
	//**********			Screen Shorts			*******
	
	public static String takeScreenShorts(String expFilePath, String expFileName) {
		File objFtc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		if (expFilePath.trim().isBlank() || expFilePath.trim().isEmpty()) {
			expFilePath = currentDirectory + "\\ScreenShorts";
		}
		if (expFileName.trim().isBlank() || expFileName.trim().isEmpty()) {
			long RNum = randomNumber(5);
			expFileName = "Test_" + RNum + ".png";
		}
		String curFolder = createFolder(expFilePath);
		String curFolderNFilePath = curFolder + "\\" + expFileName;
		try {
			FileUtils.copyFile(objFtc, new File(curFolderNFilePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curFolderNFilePath;
	}
	
		/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	windowScroll
	 * Function Arg:	WebElement expElement	expScroll
	 * FunctionOutPut: 	It will Scroll exptect
	 * ***************************************************************************************************************/

	public static void windowScroll(WebElement expElement, String expScroll) {
		jse = (JavascriptExecutor) driver;
		if (expScroll.toLowerCase().contains("up")) {
			jse.executeScript("window.scrollTo(0,-document.body.scrollHeight);");
		} else if (expScroll.toLowerCase().contains("down")) {
			jse.executeScript("window.scrollTo(0,document.body.scrollHeight);");
		} else if (expScroll.toLowerCase().contains("horigintaldown")) {
			jse.executeScript("window.scrollTo(0,document.body.scrollHeight);");
		} else {
			jse.executeScript("arguments[0].scrollIntoView();", expElement);
		}
	}
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	fileInStream
	 * Function Arg: 	String FileInStreamPath
	 * FunctionOutPut: 	It will create a Object For File Input Stream
	 ****************************************************************************************************************/
	public static FileInputStream fileInStream(String FileInStreamPath ) throws FileNotFoundException
	{
		objFile=new FileInputStream(FileInStreamPath);
		return objFile;
	}

	
	
	//					 User Action Start
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	click
	 * Function Arg: 	WebElement expElement
	 * FunctionOutPut: 	It will Click An Element
	 ****************************************************************************************************************/

	public static void click(WebElement expElement) {
		String expClickElement = null;
		try {
			wait.until(ExpectedConditions.visibilityOf(expElement));
			expClickElement = expElement.getText().trim();
			expElement.click();
			TestListener.test.log(Status.PASS, "\t Expected ' " + expClickElement + " ' Element Clicked");
			logger.info("\t Expected ' " + expClickElement + " ' Element Clicked");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected ' " + expClickElement + " ' Not Found or Able To Clicked");
			logger.info("\t Expected ' " + expClickElement + " ' Not Found or Able To Clicked");
		}
	}
	
	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	clickByJs
	 *  Function Arg: 	ExpElement element locator
	 *  FunctionOutPut: It will create a object for Action class
	 ***************************************************************************************************************/
	
	public static void clickByJs(WebElement expElement){
		try {
			jse=(JavascriptExecutor)driver;
			jse.executeScript("arguments[0].click();",expElement);
			TestListener.test.log(Status.PASS,"\t Expected Element Clicked");
			logger.info("\t Expected Element Clicked");
		}catch(Exception e) {
			TestListener.test.log(Status.FAIL,"\t Expected Not Found or Able To Clicked");
			logger.info("\t Expected Not Found or Able To Clicked");
		}
	}
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	setText
	 * Function Arg: 	WebElement expElement, String expText
	 * FunctionOutPut: 	It will Write Text On Edit Filed 
	 ****************************************************************************************************************/
	
	public static void setText(WebElement expElement, String expText) {
		try {
			wait.until(ExpectedConditions.visibilityOf(expElement));
			expElement.sendKeys(expText);
			TestListener.test.log(Status.PASS, "\t Expected ' " + expText + " ' Element Set Input/Edit Field");
			logger.info("\t Expected ' " + expText + " ' Element Set Input/Edit Field");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected ' " + expText + " ' Element Does Not Set Input/Edit Field");
			logger.info("\t Expected ' " + expText + " ' Element Does Not Set Input/Edit Field");
		}
	}
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	setTextByJs
	 * Function Arg: 	String WebElement expElement, String expText
	 * FunctionOutPut: 	It will Write Text On Edit Filed
	 ****************************************************************************************************************/
	
	public static void setTextByJs(WebElement expElement, String expText) {
		try {
			jse = (JavascriptExecutor) driver;
			jse.executeScript("arguments[0].setAttribute('value','" + expText + "');", expElement);
			Thread.sleep(1000);
			String setValue = expElement.getAttribute("value").trim();
			TestListener.test.log(Status.PASS, "\t Expected ' " + setValue + " ' Element Set Input/Edit Field");
			logger.info("\t Expected ' " + setValue + " ' Element Set Input/Edit Field");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected ' " + expText + " ' Element Does Not Set Input/Edit Field");
			logger.info("\t Expected ' " + expText + " ' Element Does Not Set Input/Edit Field");
		}
	}
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	clearFiled
	 * Function Arg: 	WebElement expElement
	 * FunctionOutPut: 	It will create a Object For File Input Stream
	 ****************************************************************************************************************/
	
	public static void clearFiled(WebElement expElement) {
		try {
			wait.until(ExpectedConditions.visibilityOf(expElement));
			expElement.clear();
			TestListener.test.log(Status.PASS, "\t Expected Input/Edit Field Cleared");
			logger.info("\t Expected Input/Edit Field Cleared");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected Input/Edit Field Did Not Cleared");
			logger.info("\t Expected Input/Edit Field Did Not Cleared");
		}
	}
	
	/****************************************************************************************************************
	 * Author:			Md Rezaul Karim 
	 * Function Name: 	getText
	 * Function Arg: 	WebElement expElement
	 * FunctionOutPut: 	It will Get Inner Text From Element
	 ****************************************************************************************************************/
	
	public static String getText(WebElement expElement) {
		String expReadElement = null;
		wait.until(ExpectedConditions.visibilityOf(expElement));
		expReadElement = expElement.getText().trim();
		return expReadElement;
	}

	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	selectObject
	 *  Function Arg: 	ExpSelect Expected Select Element Locator 
	 *  FunctionOutPut: It will create a object for select class
	 * 
	 * ***************************************************************************************************************/
		
	public static Select selectObject(WebElement ExpSelect) {

		Select obs = new Select(ExpSelect);
		return obs;
	}
		
	/****************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name: 	selectDropDown
	*  Function Arg: 	expText,List<WebElement> expElement 
	*  FunctionOutPut: 	It will Select value from drop down when drop down is not able to select by select tag
	****************************************************************************************************************/
	public static void selectDropDown(String expText, List<WebElement> expElement) {
		Reporter.log("******************************************Auto Suggest Drop Down Started******************************************");
		System.out.println("******************************************Auto Suggest Drop Down Started*************************************");
		String actualValue = "";
		int totalEl = expElement.size();
		if (totalEl > 0) {
			for (int i = 0; i < totalEl; i++) {
				actualValue = expElement.get(i).getText().trim();
				if (actualValue.equalsIgnoreCase(expText)) {
					try {
						expElement.get(i).click();
						TestListener.test.log(Status.PASS, "\t Expected Drop Down Selected");
						logger.info("\t Expected Drop Down Selected");
						break;
					} catch (Exception e) {
						TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
						logger.info("\t Expected Drop Down Did not Selected");
					}
					
				}
			}
		}
		else {
			TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
			logger.info("\t Expected Drop Down Did not Selected");
		}

		Reporter.log("******************************************Auto Suggest Drop Down Ended******************************************");
		System.out.println("******************************************Auto Suggest Drop Down Ended****************************************");
	}
	
	/****************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name: 	selectDropDown
	*  Function Arg: 	expText,WebElement expElement
	*  FunctionOutPut: 	It will Select value from drop down By Text Value
	****************************************************************************************************************/
	public static void selectDropDown(String expText, WebElement expElement) {
		Select objS = new Select(expElement);
		try {
			objS.selectByVisibleText(expText);
			TestListener.test.log(Status.PASS, "\t Expected Drop Down Selected");
			logger.info("\t Expected Drop Down Selected");

		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
			logger.info("\t Expected Drop Down Did not Selected");
		}
	}
	
	/****************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name: 	selectDropDown
	*  Function Arg: 	expIndex,WebElement expElement
	*  FunctionOutPut: 	It will Select value from drop down By Text Value
	****************************************************************************************************************/
	public static void selectDropDown(int expIndex, WebElement expElement) {
		Select objS = new Select(expElement);
		try {
			objS.selectByIndex(expIndex);
			TestListener.test.log(Status.PASS, "\t Expected Drop Down Selected");
			logger.info("\t Expected Drop Down Selected");

		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
			logger.info("\t Expected Drop Down Did not Selected");
		}
	}
	
	/****************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name: 	selectDropDownByValue
	*  Function Arg: 	expIndex,WebElement expElement
	*  FunctionOutPut: 	It will Select value from drop down By Text Value
	****************************************************************************************************************/
	public static void selectDropDownByValue(String expText, WebElement expElement) {
		Select objS = new Select(expElement);
		try {
			objS.selectByValue(expText);
			TestListener.test.log(Status.PASS, "\t Expected Drop Down Selected");
			logger.info("\t Expected Drop Down Selected");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
			logger.info("\t Expected Drop Down Did not Selected");
		}
	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	selectByJs
	*  Function Arg: 	WebElement expElement,String ExpValue
	*  FunctionOutPut: 	It will Select value from drop down when drop down is not able to select by select tag
	* 
	* ***************************************************************************************************************/
	
	public static void selectByJs(WebElement expElement, String ExpValue) {
		try {
			jse.executeScript("arguments[0].setAttribute('value','" + ExpValue + "');", expElement);
			TestListener.test.log(Status.PASS, "\t Expected Drop Down Selected");
			logger.info("\t Expected Drop Down Selected");
		} catch (Exception e) {
			TestListener.test.log(Status.FAIL, "\t Expected Drop Down Did not Selected");
			logger.info("\t Expected Drop Down Did not Selected");
		}
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: SetClander
	*  Function Arg: String dateLocator, String monthLocator, String yearLocator, String nextLocator,String expectedDate
	*  FunctionOutPut: It will Select value from drop down when drop down is not able to select by select tag
	* ***************************************************************************************************************/
	
	public static void SetClander(List<WebElement> expDateElm, WebElement expMonthElm, WebElement expYearElm,
			WebElement expNextLocator, String expDate) throws InterruptedException {

		Reporter.log("*******************************Set Clander Started******************************************");
		System.out.println("**********************Set Clander Started*******************************************");

		String expDat[] = expDate.split("/");
		String Month = expDat[0];
		String day = expDat[1];
		String years = expDat[2];
		if (years.length() < 3) {
			years = ("20" + years);
		}
		for (int i = 0; i < 11; i++) {
			String month = expMonthElm.getText();
			String year = expYearElm.getText();
			if (month.toLowerCase().contains(Month.toLowerCase())) {
				if (year.toLowerCase().contains(years.toLowerCase())) {
					break;
				}
			} else {
				expNextLocator.click();
			}
		}
		int totalDate = expDateElm.size();
		for (int i = 0; i < totalDate; i++) {
			String actualDate = expDateElm.get(i).getText();
			String reActualDate = actualDate.trim();
			if (reActualDate.contains(day)) {
				if (expDateElm.get(i).isEnabled())// check if date is enable
				{
					expDateElm.get(i).click();
					break;
				} else {
					System.out.println("The Date you want select is Disable");
				}
			}
		}
		Reporter.log("*********************************Set Clander Ended******************************************");
		System.out.println("***********************Set Clander Ended************************************************");
	}

	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	refreshByJs
	 *  Function Arg: 	ExpElement element locator
	 *  FunctionOutPut: It will create a object for Action class
	 * **************************************************************************************************************/
		
	public static void refreshByJs() {
		jse.executeScript("history.go(0)");
	}
		
	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	refreshByJs
	 *  Function Arg: 	ExpElement element locator
	 *  FunctionOutPut: It will create a object for Action class
	 * **************************************************************************************************************/
	public static void refreshPage() {
		driver.navigate().refresh();
	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	setBorder
	*  Function Arg: 	expElement which element want make border 
	*  FunctionOutPut: 	It will make border which element you want 
	* 
	***************************************************************************************************************/
		
	public static void setBorder(WebElement expElement) throws IOException {
		jse.executeScript("arguments[0].style.border='3px solid red'", expElement);
	}
	
	//*******************************************          Close                 *****************************************************//
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	closeExpectedWindow
	*  Function Arg: 	expWindowTabClose  ==> it will take number of child window tab that user want close
	*  FunctionOutPut: 	close child window
	* **************************************************************************************************************/
	
	public static void closeExpectedWindow(String expWindowTabClose) {

		String[] ExTab = expWindowTabClose.split(",");
		int totalTab = ExTab.length;
		String PareantWindow = driver.getWindowHandle();
		System.out.println("No. of tabs: " + PareantWindow);
		Set<String> objWhandles = driver.getWindowHandles();
		ArrayList<String> objTab = new ArrayList<String>(objWhandles);
		int TotalWindow = objWhandles.size();
		for (int i = 0; i < totalTab; i++) {
			driver.switchTo().window(objTab.get(Integer.parseInt(ExTab[i]))).close();
			System.out.println("No. of tabs: " + TotalWindow);
		}
		driver.switchTo().window(PareantWindow);
	}

	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: CloseAllChildWindow
	*  Function Arg: it will close all  child window tab that user open
	*  FunctionOutPut: close all child window
	* ***************************************************************************************************************/
		
	public static void CloseAllChildWindow() {

		String PareantWindow = driver.getWindowHandle();
		System.out.println("No. of tabs: " + PareantWindow);
		Set<String> objWhandles = driver.getWindowHandles();
		int TotalWindow = objWhandles.size();
		for (String child : objWhandles) {
			if (!PareantWindow.equalsIgnoreCase(child)) {
				driver.switchTo().window(child).close();
				System.out.println("No. of tabs: " + TotalWindow);
			}
		}
		driver.switchTo().window(PareantWindow);
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: objAlert
	*  Function Arg:  
	*  FunctionOutPut: It will handle Alert acuction
	 * @return 
	* 
	* **************************************************************************************************************/
	public static Alert objAlert() {
		objAlert = driver.switchTo().alert();
		return objAlert;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: objAlert
	*  Function Arg:  
	*  FunctionOutPut: It will handle Alert acuction
	 * @return 
	* 
	* **************************************************************************************************************/
	public static Actions objAction() {
		objAction = new Actions(driver);
		return objAction;
	}
		
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: setAlert
	*  Function Arg:  
	*  FunctionOutPut: It will handle Alert acuction
	 * @return 
	* 
	* **************************************************************************************************************/
	
	public static   boolean isAlertPresent(){
		try {
				driver.switchTo().alert();
				return true;
			}
			catch(Exception e)
			{
				return false;
			}
	}
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	setFrame
	*  Function Arg:  	expFrame it can be index or webelement or value 
	*  FunctionOutPut: 	It will handle frame auction
	* 
	* 
	**************************************************************************************************************/
	
	public static   WebDriver setFrame(WebElement expFrame){
		 WebDriver frame = driver.switchTo().frame(expFrame);
		return frame;
	}
	

	////******************************   Validation Part   ******************************************************88
	
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
    *  Function Name: 	verifyDownload
	*  Function Arg: 	expFileName
	*  FunctionOutPut: 	It will Validate Expected Text And Actual Text
	 * @return 
	* ***************************************************************************************************************/
	public static boolean verifyDownload(String expFileName) {
		boolean verifyDownload = false;
		File objDir = new File(downloadPath + "//");
		File[] objFiles = objDir.listFiles();
		File lastModifiedFile = objFiles[0];
		for (int i = 0; i < objFiles.length; i++) {
			if (lastModifiedFile.lastModified() < objFiles[i].lastModified()) {
				lastModifiedFile = objFiles[i];
			}
		}
		if (lastModifiedFile.getName().toString().startsWith(expFileName)) {
			System.out.println("Download Sucessfull");
			TestListener.test.log(Status.FAIL,"\t Download Sucessfull");
			logger.info("\t Download Sucessfull");
			verifyDownload = true;
		}else {
			TestListener.test.log(Status.FAIL,"\t Download Does not Sucessfull");
			logger.info("\t Download Does not Sucessfull");
		}
		return verifyDownload;
	}
		
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
    *  Function Name: 	validateText
	*  Function Arg: 	expectedText And actualText
	*  FunctionOutPut: 	It will Validate Expected Text And Actual Text
	 * @throws IOException 
	* 
	* ***************************************************************************************************************/
		
	public static void validateText(String expectedText, String actualText) throws IOException {

		Reporter.log("******************************************Validate Text Started******************************************");
		System.out.println("******************************************Validate Text Started**********************************************");
		String exText;
		String acText;
		int expLength, actLength;
		String[] expText = expectedText.split(",");
		String[] actText = actualText.split(",");
		expLength = expText.length;
		actLength = actText.length;
		if (expLength > actLength) {
			expText = Arrays.copyOf(expText, (actLength));
		}
		if (expLength < actLength) {
			actText = Arrays.copyOf(actText, (expLength));
		}
		expLength = expText.length;
		actLength = actText.length;
		int j = 0;
		for (int i = 0; i < expLength; i++) {
			while (j < actLength) {
				exText = expText[i].trim();
				acText = actText[j].trim();
				if (exText.equals(acText)) {
					System.out.println("Expected Text Element  ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed");
					soft.assertTrue(true, "Expected Text Element  ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed");
					logger.info("Expected Text Element  ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed");
				} else if (exText.equalsIgnoreCase(acText)) {
					System.out.println("Expected Text Element ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "
							+ acText + " ***");
					soft.assertTrue(true, "Expected Text Element ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "
							+ acText + " ***");
					logger.warn("Expected Text Element ****** " + exText
							+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "
							+ acText + " ***");
				} else if (exText.contains(acText)) {
					System.out.println("Expected Text Element  ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "
							+ acText + " ***");
					soft.assertTrue(true, "Expected Text Element  ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "
							+ acText + " ***");
					logger.warn("Expected Text Element  ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "
							+ acText + " ***");
				} else if (exText.contains(acText.toLowerCase())) {
					System.out.println("Expected Text Element ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text SuccessfullyThe Actual Text Was *** "
							+ acText + " ***");
					soft.assertTrue(true, "Expected Text Element ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text Successfully The Actual Text Was *** "
							+ acText + " ***");
					logger.warn("Expected Text Element ****** " + exText
							+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text SuccessfullyThe Actual Text Was *** "
							+ acText + " ***");
				} else {
					takeScreenShorts("","ValidateText");
					soft.assertFalse(false,
							"Expected Text Element  ***** " + exText
									+ " *****  Not Found And Validation of Text element  Are Failed "
									+ "The Actual Text Was *** " + acText + " ***");
					System.out.println("Expected Text Element  ***** " + exText
							+ " *****  Not Found And Validation of Text element  Are Failed "
							+ "The Actual Text Was *** " + acText + " ***");
					logger.error("Expected Text Element  ***** " + exText
							+ " *****  Not Found And Validation of Text element  Are Failed "
							+ "The Actual Text Was *** " + acText + " ***");
				}
				j++;
				break;
			}
		}
		Reporter.log("******************************************Validate Text Ended******************************************");
		System.out.println(	"******************************************Validate Text Ended************************************************");
	}
	
	/***************************************************************************************************************
	*  Author:			Md Rezaul Karim 
	*  Function Name: 	validateTitle
	*  Function Arg: 	expectedTitle
	*  FunctionOutPut: 	It will Validate Expected Title
	* ***************************************************************************************************************/
		
	public static void validateTitle(String expectedTitle) throws IOException {

		Reporter.log("****************************Validate Title Header Started**************************************");
		System.out.println("**************************Validate Title Header Started**********************************");
		String actualTitle = driver.getTitle();
		if (actualTitle.contains(expectedTitle)) {
			TestListener.test.log(Status.PASS,
					"The Expected Title Header Is *** ==> " + actualTitle + " <==*** Found Test Case Pass Succefully");
			soft.assertTrue(true,
					"The Expected Title Header Is *** ==> " + actualTitle + " <==*** Found Test Case Pass Succefully");
			System.out.println(
					"The Expected Title Header Is *** ==> " + actualTitle + " <==*** Found Test Case Pass Succefully");
		} else {
			TestListener.test.log(Status.FAIL, "The Expected *** ==> " + expectedTitle
					+ " <==*** Not Found Test Case Failed. The Actual Title was ***==> " + actualTitle + "<==*** ");
			soft.assertTrue(false, "The Expected *** ==> " + expectedTitle
					+ " <==*** Not Found Test Case Failed. The Actual Title was ***==> " + actualTitle + "<==*** ");
			System.out.println("The Expected *** ==> " + expectedTitle
					+ " <==*** Not Found Test Case Failed. The Actual Title was ***==> " + actualTitle + "<==*** ");
		}
		Reporter.log("****************************Validate Title Header Ended**************************************");
		System.out.println("**************************Validate Title Header Ended**********************************");
	}
	
	/****************************************************************************************************************
    *  Author: 			Md Rezaul Karim 
	*  Function Name: 	ValidateClick
	*  Function Arg: 	expectedClick ==>Its Element sent from method,TextElement==>Text Element Name That Clicked
	*  FunctionOutPut: 	It will Validate Expected Element Clicked Or Not
	* ***************************************************************************************************************/
		
	public static void validateClick(WebElement expElement,String TextElement) throws IOException{
		
		Reporter.log("******************************************Validate Clicked Started******************************************");
		System.out.println("******************************************Validate Clicked Started*******************************************");
		if(! expElement.isDisplayed())
			{
				System.out.println("The Expected Element *** "+TextElement+" is Clicked Successfully");
				soft.assertTrue(true,"The Expected Element *** "+TextElement+" is Clicked Successfully");
				logger.info("The Expected Element *** "+TextElement+" is Clicked Successfully");
			}
		else
			{
				setBorder(expElement);	
				//takeScreenShot("ValidateClick");	
				System.out.println("The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
				soft.assertTrue(false,"The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
				logger.error("The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
			}
		Reporter.log("******************************************Validate Clicked Ended******************************************");
		System.out.println("******************************************Validate Clicked Ended*********************************************");
	}
		
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	validateInputValue
	*  Function Arg: expectedEditElement Its Element sent from method,actualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	* 
	* ***************************************************************************************************************/
	
	public static void validateInputValue(WebElement expElement, String actualEditValue) {

		Reporter.log("***********************************Validate Input Value Started******************************************");
		System.out.println("*****************************Validate Input Value Started***************************************");
		String runValue = expElement.getAttribute("value");
		if ((runValue.trim()).equals(actualEditValue)) {
			System.out.println(
					"The Expected Input Value *** " + actualEditValue + " *** is Successfully Set on Input Box");
			soft.assertTrue(true,
					"The Expected Input Value *** " + actualEditValue + " *** is Successfully Set on Input Box");
			logger.info("The Expected Input Value *** " + actualEditValue + " *** is Successfully Set on Input Box");
			TestListener.test.log(Status.PASS,
					"The Expected Input Value *** " + actualEditValue + " *** is Successfully Set on Input Box");
		} else {
			System.out.println("The Expected Input Value *** " + actualEditValue
					+ " *** Does not Set on Input Box Actual Input Value Was  *** " + runValue + " ***");
			soft.assertTrue(false, "The Expected Input Value *** " + actualEditValue
					+ " *** Does not Set on Input Box Actual Input Value Was  *** " + runValue + " ***");
			logger.error("The Expected Input Value *** " + actualEditValue
					+ " *** Does not Set on Input Box Actual Input Value Was  *** " + runValue + " ***");
			TestListener.test.log(Status.FAIL, "The Expected Input Value *** " + actualEditValue
					+ " *** Does not Set on Input Box Actual Input Value Was  *** " + runValue + " ***");
		}
		Reporter.log("***********************************Validate Input Value Ended******************************************");
		System.out.println("******************************Validate Input Value Ended*****************************************");
	}

	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	validateDropValue
	*  Function Arg: 	expectedDropElement ==> Its Element sent from method, ActualSelectedValue==>The Value That Will Set on Input Field
	*  FunctionOutPut: 	It will Validate Expected Input Value Set On Input Filed or Not
	*
	**************************************************************************************************************/
	
	public static void validateDropValue(WebElement expectedDropElement, String ActualSelectedValue)
			throws InterruptedException {

		Reporter.log("******************************** Validate Drop Value Started ******************************************");
		System.out.println(	"*******************************Validate Drop Value Started ***************************************");

		String SelectedValue = expectedDropElement.getAttribute("value");
		if ((SelectedValue.trim()).equals(ActualSelectedValue.trim())) {
			System.out.println("The Expected Selected Input Value *** " + SelectedValue	+ " *** is Successfully Set on Drop Down List");
			soft.assertTrue(true, "The Expected Selected Input Value *** " + SelectedValue+ " *** is Successfully Set on Drop Down List");
			logger.info("The Expected Selected Input Value *** " + SelectedValue + " *** is Successfully Set on Drop Down List");
			TestListener.test.log(Status.PASS, "The Expected Selected Input Value *** " + SelectedValue	+ " *** is Successfully Set on Drop Down List");
		} else {
			System.out.println("The Expected Selected Input Value *** " + ActualSelectedValue+ " *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** " + SelectedValue
					+ " ***");
			soft.assertTrue(false,"The Expected Selected Input Value *** " + ActualSelectedValue+ " *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** "
							+ SelectedValue + " ***");
			logger.error("The Expected Selected Input Value *** " + ActualSelectedValue	+ " *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** " + SelectedValue
					+ " ***");
			TestListener.test.log(Status.FAIL, "The Expected Selected Input Value *** " + ActualSelectedValue+ " *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** " + SelectedValue
					+ " ***");
		}
		Reporter.log("************************************Validate Drop Value Ended******************************************");
		System.out.println("*******************************Validate Drop Value Ended******************************************");
	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	ValidateStringShort
	*  Function Arg: 	expectedEditElement Its Element sent from method,ActualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: 	It will Validate Expected Input Value Set On Input Filed or Not
	* ***************************************************************************************************************/
	
	public static void validateStringShort(List<WebElement> expElementList, String Locator) {
		
		Reporter.log("******************************************Validate String Short Started******************************************");
		System.out.println("******************************************Validate String Short Started**************************************");
		ArrayList<String> originalList = new ArrayList<String>();
		for (int i = 0; i < expElementList.size(); i++)
			{
				originalList.add(expElementList.get(i).getText());
			}
		ArrayList<String> copyList = new ArrayList<String>();
		for (int i = 0; i < originalList.size(); i++)
			{
				copyList.add(originalList.get(i));
			}
		Collections.sort(copyList);
		soft.assertTrue(originalList.equals(copyList), "Expected Value Are Shorted:");
		TestListener.test.log(Status.PASS, "Expected Value Are Shorted:");
		Reporter.log("******************************************Validate String Short Ended******************************************");
		System.out.println("******************************************Validate String Short Ended****************************************");
	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	validateBrookenLink
	*  Function Arg: expectedEditElement Its Element sent from method,ActualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	* 
	* ***************************************************************************************************************/

	public static void validateBrookenLink(String Locator, int TotalLink) throws InterruptedException {

	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	ValidateStringShort
	*  Function Arg: 	expectedEditElement Its Element sent from method,ActualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: 	It will Validate Expected Input Value Set On Input Filed or Not
	* ***************************************************************************************************************/

	public static void validateColor(String expColor,WebElement expColorEl) throws IOException {
		//boolean colorResult=null;
		String actualColor=expColorEl.getCssValue("color");
		validateText(expColor,actualColor);
	}
	
////******************************   All Input And Random Data Function  ***********************************************************
/*	
	public static List<LinkedHashMap<String,String>> fetchTable(String expColumn){
		List<LinkedHashMap<String,String>> allData=new ArrayList<LinkedHashMap<String,String>>();
		boolean checkifMorePage;
		List<String> allHeaderName=new ArrayList<String>();
		if(expColumn.isEmpty() || expColumn.length()<1) {
			List<WebElement> allHeaderEl=TableHeaderValue();
			for(WebElement header : allHeaderEl ) {
				String headerName=header.getText().replaceAll("\r\n","").replaceAll("ui-btn","").trim();
				allHeaderName.add(headerName);
			}
		}
		else {
			String[] expColumnSplit=expColumn.split(",");
			allHeaderName=Arrays.asList(expColumnSplit);
		}
		int totalColumn=allHeaderName.size();
		try {
			Thread.sleep(1000);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		do {
			int totalRowList=cf.TableRowValueList().Size();
			for(int i=0;i<totalRowList;i++) {
				LinkedHashMap<String,String> eachRowData=new LinkedHashMap<>();
				int k=0;
				for(int j=0;j<totalColumn;j++) {
					String cellValue=cf.TableRowValuelist().get(i+k).getText();
					k=k+1;
					eachRowData.put(allHeaderName.get(j), cellValue)
				}
				i=i+k-1;
				allData.add(eachRowData);
			}
			checkifMorePage=cf.RightArrow().get
		}
		return allData;
	}
	
	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	uniqueName
	*  Function Arg:  	expFileStart it can be start With File Name  
	*  FunctionOutPut: 	It will Create A Unique File Name 
	**************************************************************************************************************/
	
	public static String uniqueName(String expFileStart) {
		if(expFileStart==null || expFileStart.length()<1) {
			expFileStart="Test";
		}
		Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        String formattedDate = formatter.format(date).trim();
        System.out.println(formattedDate);
		String expName=expFileStart+"_"+ formattedDate.replace(":","_").replace(" ","_").replace("/","_");
		return expName;
	}
	
//	File And Folder

	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: 	createFolder
	*  Function Arg: 	String expFolderNameAndPath
	*  FunctionOutPut: 	It will take Create a folder with current date
	***************************************************************************************************************/
	
	public static String createFolder(String expFolderNameAndPath) {
		if (expFolderNameAndPath.trim().isBlank() || expFolderNameAndPath.trim().isEmpty()) {
			expFolderNameAndPath = currentDirectory + "\\TestFolder";
		}
		File objFc = new File(expFolderNameAndPath);
		if (!objFc.exists()) {
			objFc.mkdirs();
		}
		return expFolderNameAndPath;
	}

	/****************************************************************************************************************
	*  Author: 			Md Rezaul Karim 
	*  Function Name: createFolder
	*  Function Arg: String expFolderName
	*  FunctionOutPut: It will take Create a folder with current date
	* 
	***************************************************************************************************************/
	
	public static String createFileFolder(String expFileFolderPath, String expFileName, String expFileExtention) {

		if (expFileName.trim().isBlank() || expFileName.trim().isEmpty()) {
			long RNum = randomNumber(5);
			expFileName = "Test_" + RNum;
		}

		if (expFileExtention.isEmpty() || expFileExtention.length() < 1) {
			expFileExtention = ".png";
		}
		String curFolder = createFolder(expFileFolderPath);
		String curFolderNFilePath = curFolder + "\\" + expFileName + "." + expFileExtention;
		File objFc = new File(curFolderNFilePath);
		try {
			boolean isFileCreated = objFc.createNewFile();
			if (isFileCreated) {
				System.out.println("File Created");
			} else {
				System.out.println("File Already Created or Exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curFolderNFilePath;
	}

	/****************************************************************************************************************
	 * Author: 			Md Rezaul Karim 
	 * Function Name: 	encodedStr
	 * Function Arg: 	String expStr
	 * FunctionOutPut: 	It will encoded String
	 * ***************************************************************************************************************/

	public static String encodedStr(String expStr) {
		String encodedResult= Base64.getEncoder().encodeToString(expStr.getBytes()); 
		System.out.println(encodedResult);
		return encodedResult;
	}
	
	/****************************************************************************************************************
	 * Author: Md Rezaul Karim 
	 * Function Name: dencodedStr
	 * Function Arg: String expStr
	 * FunctionOutPut: It will dencoded String
	 * ***************************************************************************************************************/

	public static String dencodedStr(String expStr) {
		byte[] actualByte= Base64.getDecoder().decode(expStr); 
		String actualString= new String(actualByte); 
		return actualString;
	}
		
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name: getInput
	 *  Function Arg: No Arguments
	 *  FunctionOutPut: It will get input from keyboard
	 * 
	 * ***************************************************************************************************************/
	public static void  getInput() {
		Scanner objInputValue = new Scanner(System.in);
		String inputValue = "";
		while (!(inputValue.equalsIgnoreCase("Exit"))) {
			System.out.println(" Please Enter Your Value:(For Exit Please Enter Exit:)=>");
			inputValue = objInputValue.nextLine();
			if(!inputValue.equalsIgnoreCase("exit"))
				{
					System.out.println("You Enter :" + inputValue );
				}
		}
		System.out.println("Exit from input taker ");
		objInputValue.close();
	}
	
	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	randomNumber
	 *  Function Arg:	expNumberOfDigit 
	 *  Function OutPut:It will Random numeric Number
	 ****************************************************************************************************************/
	
	public static long randomNumber(int expNumberOfDigit) {
		
		String AlphaNumericString ="0123456789"; 
		StringBuilder objString = new StringBuilder(expNumberOfDigit);
		for(int i=0;i<expNumberOfDigit;i++)
		{
			int index=(int) (AlphaNumericString.length()*Math.random());// generate a random number between 0 to AlphaNumericString variable length
			objString.append(AlphaNumericString.charAt(index));
		}
		long randomNumeric=Long.parseLong(objString.toString());
		return randomNumeric;
	}
	
	/****************************************************************************************************************
	 *  Author:			Md Rezaul Karim 
	 *  Function Name:	randomFloatNumber 
	 *  Function Arg: expNumberOfDigit => how many digit do you want Number,expDecimalDigit => how many digit do you want Decimal
	 *  FunctionOutPut: It will return Random Floating Number
	 * @return 
	 * ***************************************************************************************************************/
	
	public static float randomFloatNumber  (int expNumberOfDigit,float expDecimalDigit) {
		String AlphaNumericString = "0123456789";
		
		Random rnd=new Random();
		StringBuilder objString = new StringBuilder(expNumberOfDigit);
		for(int i=0;i<expNumberOfDigit;i++)
		{
			int index=(int) (AlphaNumericString.length()*Math.random());// generate a random number between 0 to AlphaNumericString variable length
			objString.append(AlphaNumericString.charAt(index));
		}
		float curBeforeNum=Float.parseFloat(objString.toString());
		System.out.println(curBeforeNum);
		StringBuilder objh = new StringBuilder();
		for(int i=1;i<=expDecimalDigit;i++) {
			objh.append("#");
		}
		String totalDigit=objh.toString();
		float curDecNum=rnd.nextFloat(expDecimalDigit);
		System.out.println(curDecNum);
		float t=curBeforeNum+curDecNum;
		System.out.println(t+" print");
		DecimalFormat decimalFormat = new DecimalFormat("#."+totalDigit);
		float twoDigitsF = Float.valueOf(decimalFormat.format(t));
		return twoDigitsF;
	}
	
	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	randomAlphaNumeric
	 *  Function Arg: 	StringSize how many digit do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha numeric String
	 *************************************************************************************************************** */
	
	public static String randomAlphaNumeric(int expStrSize) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"+ "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(expStrSize);
		for(int i=0;i<expStrSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomAlphaNumeric=objString.toString();
		return randomAlphaNumeric;
	}

	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	randomUpperLowerString
	 *  Function Arg: 	expStrSize how many Character do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha  character upper and lower case String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomUpperLowerString(int expStrSize) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(expStrSize);
		for(int i=0;i<expStrSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length 
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomUpperLowerString=objString.toString();
		return randomUpperLowerString;
	}

	/****************************************************************************************************************
	 *  Author: 		Md Rezaul Karim 
	 *  Function Name:	randomAlphaNumericSpeceal
	 *  Function Arg: StringSize how many digit do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha numeric and special character String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomAlphaNumericSpeceal(int expStrSize) {
		
		Reporter.log("******************************************Create Random Numeric Speceal String Started******************************************");
		System.out.println("******************************************Create Random Numeric Speceal String Started***********************");
		
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"+"!@#$%^&*()_+<>?"+ "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(expStrSize);
		for(int i=0;i<expStrSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomAlphaNumericSpeceal=objString.toString();
		return randomAlphaNumericSpeceal;
	}
	
	
	//				Validate 


	
}

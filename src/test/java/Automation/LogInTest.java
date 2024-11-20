package Automation;


import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import CommonClass.BaseClass;
import Pages.LogInPage;

public class LogInTest extends BaseClass {
	public LogInTest() {
		super();
	}
	
	@BeforeMethod
	public void setUp() {
		initializeDriver("");
		openUrl(objProp.getProperty("AutoUrl"));
		lp=new LogInPage();
		
	}
	
	@Test(priority = 1)
	public void validateLogInPageLogo() {
		boolean lRes=lp.verifyLogo();
		Assert.assertTrue(lRes, "Test Result");
	}
	
	@Test(priority = 2)
	public void validateTitle() {
		String curTitle=lp.validateLogingPageTitle();
		System.out.println(curTitle);
		Assert.assertEquals(curTitle,objProp.getProperty("AutoTitle"));
	}
	
	@Test(priority = 3)
	
	public void logIn() {
		hp=lp.logInApp();
	}
	
	@AfterMethod
	public void tearDown() {
			closeBrowser();
			System.out.println("Test");
	}

}

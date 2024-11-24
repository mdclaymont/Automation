package AutomationPractice;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import CommonClass.BaseClass;
import Pages.LogInPage;

public class HomePageTest extends BaseClass {
	public HomePageTest() {
		super();
	}

	@BeforeMethod
	public void setUp() {
		initializeDriver("");
		openUrl(objProp.getProperty("AutoUrl"));
		lp = new LogInPage();
		hp = lp.logInApp();

	}

	@Test
	public void products() {
		hp.productClick();
		System.out.println("Test Products");
		Assert.assertTrue(true);
	}

	@AfterMethod
	public void tearDown() {
		// closeBrowser();
	}

}

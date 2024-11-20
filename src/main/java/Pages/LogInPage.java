package Pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import CommonClass.BaseClass;

public class LogInPage extends BaseClass {
	
	public LogInPage() {
		PageFactory.initElements(driver,this);
	}
	
	
	@FindBy(xpath="//*[@alt='Website for automation practice']") WebElement logo;
	@FindBy(name="email") WebElement userId;
	@FindBy(name="password") WebElement userPassword;
	@FindBy(xpath="//*[text()='Login']") WebElement logInBtn;
	
	public boolean verifyLogo() {
		return logo.isDisplayed();
	}
	
	public String validateLogingPageTitle() {
		return driver.getTitle();
	}
	
	public HomePage logInApp() {
		userId.sendKeys(objProp.getProperty("AutoUserId"));
		userPassword.sendKeys(objProp.getProperty("AutoPassword"));
		logInBtn.click();
		return new  HomePage();
	}
	

}

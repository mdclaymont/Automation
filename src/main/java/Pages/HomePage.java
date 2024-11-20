package Pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import CommonClass.BaseClass;

public class HomePage extends BaseClass {
	
	public HomePage() {
		PageFactory.initElements(driver,this);
	}
	@FindBy(xpath="//*[contains(text(),'Logout')]") WebElement logOutLink;
	@FindBy(xpath="//a[contains(text(),'Products')]") WebElement products; 
	@FindBy(id="search_product") WebElement search;
	
	public void productClick() {
		products.click();
	}
	
	public void logOutClick() {
		logOutLink.click();
	}
	
	public WebElement searchField() {
		return search;
	}
}

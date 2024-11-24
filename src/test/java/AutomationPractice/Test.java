package AutomationPractice;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import Utility.XLUtility;


public class Test {

	public static void main(String[] args) throws IOException {
		XLUtility xl=new XLUtility("","");
		
		System.out.println(xl.getRowCount());
		
	}

}

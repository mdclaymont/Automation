package Utility;

import java.io.IOException;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import CommonClass.BaseClass;



public class ExtentManager extends BaseClass {

	public static ExtentReports extent;
	public static ExtentSparkReporter spark;

	public static ExtentReports createInstance() {
		String fileName = null;
		fileName = createFileFolder(System.getProperty("user.dir") + "/Reports/", "extentReport", ".html");
		spark = new ExtentSparkReporter(fileName);
		spark.config().setDocumentTitle("Automation Test Report");
		spark.config().setEncoding("utf-8");
		spark.config().setReportName(fileName);
		spark.config().setTheme(Theme.STANDARD);
		
		extent = new ExtentReports();
		extent.setSystemInfo("Browser Name", browserName);
		extent.setSystemInfo("OS Name", System.getProperty("os.name"));
		extent.setSystemInfo("machen ", System.getProperty("user.name"));
		extent.attachReporter(spark);

		return extent;
	}
}
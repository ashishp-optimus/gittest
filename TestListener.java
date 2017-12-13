package com.blue.utils;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.blue.tests.testcases.BaseTest;

/**
 * Defines the Listener methods
 * @author optimus
 */
public class TestListener implements ITestListener
{

  Logger logger = Logger.getLogger(TestListener.class);

  private static String ERROR_SYMBOL = " *********";
  private static String TEST_CASE_FAILURE = "***** Error test has failed : ";
  private static String TEST_START = "Starting test case : ";
  private static String TEST_SUCCESS = "Test Passed : ";
  private static String TEST_SKIPPED = "Test SKIPPED ";

  static int screenshotCounter = 0;
  static ArrayList<String> screenShotPaths = new ArrayList<String>();
  WebDriver driver;

  /**
   * Captures Screenshot on test failure
   */
  @Override
  public void onTestFailure(ITestResult result)
  {
    logger.info(TEST_CASE_FAILURE + result.getName() + ERROR_SYMBOL);
    System.out.println("In OnTestFailure custom");
    Object currentClass = result.getInstance();
    WebDriver webDriver = ((BaseTest) currentClass).getDriver();
    String className = result.getTestClass().getRealClass().getSimpleName();
    String methodName = result.getMethod().getMethodName();
    Utilities.takeScreenshot(webDriver, className, methodName);   
  }

  /**
   * Logging on Test Start
   */
  @Override
  public void onTestStart(ITestResult result)
  {
    logger.info(TEST_START + result.getName());
  }

  /**
   * Logging on Test Success
   */
  @Override
  public void onTestSuccess(ITestResult result)
  {
    logger.info(TEST_SUCCESS + result.getName());

  }

  /**
   * Logging on Test Skip
   */
  @Override
  public void onTestSkipped(ITestResult result)
  {
    logger.info(TEST_SKIPPED + result.getName());

  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onStart(ITestContext context)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void onFinish(ITestContext context)
  {
    // TODO Auto-generated method stub

  }

}

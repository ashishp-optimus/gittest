package com.blue.utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import com.blue.interfaces.Action;
import com.blue.interfaces.Constants;

/**
 * Define methods to extend WebDriver properties
 * @author optimus
 */

public class WebDriverUtilities
{

  protected static Logger log = Logger.getLogger(WebDriverUtilities.class);;

  /**
   * Upload file without opening window's dialogue prompt
   * @param driver : Accepts WebDriver property
   * @param ele : WebElement of drop area
   * @param filePath : path of file to be uploaded
   * @param uploadArea : UploadElement name to be displayed in log message
   */
  public static void uploadFile(WebDriver driver, WebElement ele, String filePath, String uploadArea)
  {
    log.info(String.format("Uploading image in %s ", uploadArea));
    final String JS_DROP_FILE = "var tgt=arguments[0],e=document.createElement('input');e.type='" + "file';e.addEventListener('change',function(event){var dataTrans"
        + "fer={dropEffect:'',effectAllowed:'all',files:e.files,items:{},t" + "ypes:[],setData:function(format,data){},getData:function(format"
        + "){}};var emit=function(event,target){var evt=document.createEve" + "nt('Event');evt.initEvent(event,true,false);evt.dataTransfer=da"
        + "taTransfer;target.dispatchEvent(evt);};emit('dragenter',tgt);em" + "it('dragover',tgt);emit('drop',tgt);document.body.removeChild(e" + ");},false);document.body.appendChild(e);return e;";

    JavascriptExecutor js = (JavascriptExecutor) driver;
    ArrayList<WebElement> obj = new ArrayList<WebElement>();
    obj.add(ele);
    ((WebElement) js.executeScript(JS_DROP_FILE, obj.get(0))).sendKeys(filePath);
  }

  /**
   * Wait until the code written in lambda executes successfully
   * @param element : Accepts WebElement
   * @param action : Execute code inside lambda
   */
  private static void waitUntilElementActionCompletes(WebElement element, Action action)
  {
    new FluentWait<>(element).withTimeout(Constants.CONFIG_EXPLICIT_WAIT, TimeUnit.SECONDS).pollingEvery(Constants.CONFIG_POLLING_PERIOD, TimeUnit.MILLISECONDS).ignoring(WebDriverException.class)
        .until(new Function<WebElement, Boolean>()
        {
          @Override
          public Boolean apply(WebElement element)
          {
            try
            {
              action.apply();
              return true;
            }
            catch (Exception exception)
            {
              //This is just to wait for element action complete, just continue
            }
            return false;
          }
        });
  }

  /**
   * Wait until the code written in lambda returns true
   * @param element : Accepts WebElement
   * @param pred : Accepts lambda expression which returns boolean
   */
  private static void waitUntilBooleanReturned(WebElement element, Predicate<WebElement> pred)
  {
    new FluentWait<>(element).withTimeout(Constants.CONFIG_EXPLICIT_WAIT, TimeUnit.SECONDS).pollingEvery(Constants.CONFIG_POLLING_PERIOD, TimeUnit.MILLISECONDS).ignoring(WebDriverException.class)
        .until(new Function<WebElement, Boolean>()
        {
          @Override
          public Boolean apply(WebElement element)
          {
            try
            {
              if (pred.test(element))
                return true;
            }
            catch (Exception exception)
            {
              //This is just to wait for the boolean returned, just continue

            }
            return false;
          }
        });
  }

  /**
   * Wait until the code written in lambda executes successfully
   * @param driver : Accepts WebDriver property
   * @param action : Accepts lambda expression
   */
  private static void waitUntilWebDriverActionCompletes(WebDriver driver, Action action)
  {
    new FluentWait<>(driver).withTimeout(Constants.CONFIG_EXPLICIT_WAIT, TimeUnit.SECONDS).pollingEvery(Constants.CONFIG_POLLING_PERIOD, TimeUnit.MILLISECONDS).ignoring(WebDriverException.class)
        .until(new Function<WebDriver, Boolean>()
        {
          @Override
          public Boolean apply(WebDriver element)
          {
            try
            {
              action.apply();
              return true;
            }
            catch (Exception exception)
            {
              //This is just to wait for WebDriver action complete, just continue
            }
            return false;
          }
        });
  }

  /**
   * Make a click on the button
   * @param element : WebElement of button
   */
  public static void clickButtonFluently(WebElement element)
  {
    try
    {
      log.info(String.format("Click on the Webelement"));
      waitUntilElementActionCompletes(element, () -> {
        element.click();
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to Click the Weblement", exception);
    }
  }

  /**
   * Make a decision
   * @param element : WebElement of element
   * @param pred : Predicate WebElement
   * @return true/false
   */
  public static boolean makeDecision(WebElement element, Predicate<WebElement> pred)
  {
    try
    {
      if (pred.test(element))
        return true;
    }
    catch (Exception exception)
    {
      // This is just to verify predicate of WebElement, just continue
    }
    return false;
  }

  /** 
  * Perform drag and drop operation, need source and destination elements
  * @param driver : Accepts WebDriver property
  * @param source : Source WebElement
  * @param dest : Destination WebElement
  */
  public static void dragAndDrop(WebDriver driver, WebElement source, WebElement dest)
  {
    try
    {
      log.info("Performing drag and drop operation");
      waitUntilElementActionCompletes(source, () -> {
        new Actions(driver).dragAndDrop(source, dest).build().perform();
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to drag and drop the Weblement", exception);
    }
  }

  /**
   * Perform move mouse and click operation
   * @param driver : Accepts WebDriver property
   * @param element : WebElement of button
   */
  public static void moveMouseAndClick(WebDriver driver, WebElement element)
  {
    try
    {
      log.info(String.format("Performing move mouse and click operation on %s button"));
      waitUntilElementActionCompletes(element, () -> {
        new Actions(driver).moveToElement(element).click().build().perform();
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to move the mouse and click the Weblement", exception);
    }

  }

  /**
   * Clear the text box and send the text provided in 'value' argument
   * @param element : Webelement of text box
   * @param value : text to send in text box
   */
  public static void clearTextboxAndFillValue(WebElement element, String value)
  {
    try
    {
      log.info(String.format("Entering %s into the textbox", value));
      waitUntilElementActionCompletes(element, () -> {
        element.clear();
        element.sendKeys(value);
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to clear textbox and fill the value", exception);
    }

  }

  /**
   * Click the text box and send the text provided in 'value' argument
   * @param element : WebElement of text box
   * @param value : text to send in text box
   */
  public static void clickAndEnterKeywords(WebElement element, String value)
  {
    try
    {
      log.info(String.format("Entering %s into the textbox", value));
      waitUntilElementActionCompletes(element, () -> {
        element.clear();
        element.click();
        element.sendKeys(value);
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to click and enter keyword in the textbox", exception);
    }
  }

  /**
   * Send the text provided in 'value' argument
   * @param element : Webelement of text box
   * @param value : text to send in text box
   */
  public static void fluentEnterKeyword(WebElement element, String value)
  {
    try
    {
      log.info(String.format("Entering %s into the textbox", value));
      waitUntilElementActionCompletes(element, () -> {
        element.sendKeys(value);
      });
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Unable to enter the keyword in the textbox", exception);
    }
  }

  /**
   * Fluent wait to check visibility of element
   * @param element : Wait until the visibility of the WebElement
   */
  public static void fluentWaitToCheckVisible(WebElement element)
  {
    try
    {
      log.info(String.format("wait until the visibility of of the WebElement"));
      waitUntilBooleanReturned(element, (ele) -> ele.isDisplayed());
    }
    catch (Exception exception)
    {
      Assert.fail("Assertion Failed: Element is not visible", exception);
    }
  }

  /**
   * Get the browser name, OS type and Application build number
   * @param driver : WebDriver Reference
   * @return environmentDetails : array of environment details
   * @throws Exception : If an exception occurs
   */
  public static String[] getBrowserOsAppVersion(WebDriver driver) throws Exception
  {
    Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
    String[] environmentDetails = new String[3];
    environmentDetails[0] = Constants.PLATFORM_NAME.toUpperCase();
    environmentDetails[1] = cap.getBrowserName().toUpperCase();
    environmentDetails[2] = Utilities.getApiData(Constants.APPLICATION_BUILD_NUMBER_API).toUpperCase();
    return environmentDetails;
  }

  /**
   * Get the required platform
   * @param browserName : name of the browser
   * @return platform   
   */
  public static Platform getPlatform(String browserName)
  {
    switch (browserName)
    {
    case "win10":
      return Platform.WIN10;
    case "linux":
      return Platform.LINUX;
    case "mac":
      return Platform.MAC;
    case "mavericks":
      return Platform.MAVERICKS;
    case "mountain_lion":
      return Platform.MOUNTAIN_LION;
    case "sierra":
      return Platform.SIERRA;
    case "snow_leopard":
      return Platform.SNOW_LEOPARD;
    case "unix":
      return Platform.UNIX;
    case "vista":
      return Platform.VISTA;
    case "win8":
      return Platform.WIN8;
    case "win8.1":
      return Platform.WIN8_1;
    case "windows":
      return Platform.WINDOWS;
    case "xp":
      return Platform.XP;
    case "yosemite":
      return Platform.YOSEMITE;
    default:
      return Platform.ANY;
    }
  }

}

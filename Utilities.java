package com.blue.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.blue.interfaces.Constants;
import io.qameta.allure.Allure;

/**
 * Define methods to extend common functionalities of framework
 * @author optimus
 *
 */
public class Utilities
{

  static Logger log = Logger.getLogger(Utilities.class);

  /**
   * Get current time stamp based on the pattern passed in method argument
   * @param pattern : Accepts DateTimeFormatter pattern
   * @return Current time stamp
   */
  public static String getCurrentTimeStamp(String pattern)
  {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
  }

  /**
   * It captures screenshot and returns the path of the newly saved screenshot
   * @param webDriver : WebDriver reference
   * @param className : Class name of the test that is in execution
   * @param methodName : Method name of the test that is execution
   */
  public static void takeScreenshot(WebDriver webDriver, String className, String methodName)
  {
    String path = Paths.get(Constants.PATH_SCREENSHOT_FOLDER, className, String.format("%s_%s.jpg", methodName, Utilities.getCurrentTimeStamp(Constants.PATTERN_FILE))).toString();
    if (webDriver != null)
    {
      try
      {
        File f = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(f, new File(path));
        Path content = Paths.get(path);
        try (InputStream is = Files.newInputStream(content))
        {
          Allure.addAttachment(className + " - " + methodName, is);
        }
      }
      catch (IOException e)
      {
        log.error("Error occured while taking screenshots");
        log.error(e.getMessage());
      }
    }

  }

  /**
   * Write JSONObject to the file whose path is provided in 'filePath' argument
   * @param jsonObject : Accepts JSONArray
   * @param filePath : Accepts String for filePath
   * @throws Exception : If an exception occurs
   */
  public static void writeJsonFromObject(JSONObject jsonObject, String filePath) throws Exception
  {
    try (BufferedWriter bf = new BufferedWriter(new FileWriter(filePath)))
    {
      jsonObject.write(bf, 1, 1);
    }
    catch (IOException exception)
    {
      log.error("IO Exception occured while taking screenshots");
      log.error(exception.getMessage());
      throw exception;
    }
    catch (Exception exception)
    {
      log.error("Error occured while writring JSON");
      log.error(exception.getMessage());
      throw exception;
    }
  }

  /**
   * Write JSONArray to the file whose path is provided in 'filePath' argument
   * @param jsonArray : Accepts JSONArray
   * @param filePath : Accepts String for filePath
   * @throws Exception : If an exception occurs 
   */
  public static void writeJsonFromArray(JSONArray jsonArray, String filePath) throws Exception
  {
    try (BufferedWriter bf = new BufferedWriter(new FileWriter(filePath)))
    {
      jsonArray.write(bf, 1, 1);
    }
    catch (IOException exception)
    {
      log.error("IO Exception occured while taking screenshots");
      log.error(exception.getMessage());
      throw exception;
    }
    catch (Exception exception)
    {
      log.error("Error occured while writring JSON");
      log.error(exception.getMessage());
      throw exception;
    }
  }

  /**
   * Read key from JSONObject
   * @param filePath : path of the file
   * @param key : Key to read
   * @return value of the 'Key' found in JSONObject
   */
  public static Object readJSONFromObj(String filePath, String... key)
  {
    JSONObject jsonObj = getJSONFromCSV(filePath);
    for (int i = 0; i < key.length - 1; i++)
    {
      jsonObj = (JSONObject) jsonObj.get(key[i]);
    }
    return jsonObj.get(key[key.length - 1]);
  }

  /**
   * Read key from JSONArray
   * @param filePath : Path of the file
   * @param key : Key to read
   * @return Returns value of the 'Key' found in JSONArray
   */
  public static Object readJSONFromArray(String filePath, String... key)
  {
    JSONObject jsonObj = getJSONFromCSV(filePath);
    return jsonObj.getJSONArray(key[0]).getJSONObject(0).get(key[1]);
  }

  /**
   * Get JSONObject from file 
   * @param filePath : path of file containing JSON
   * @return JSONObject : Read from file
   */
  private static JSONObject getJSONFromCSV(String filePath)
  {
    JSONObject jsonObj = null;
    try (FileReader fr = new FileReader(filePath))
    {
      JSONTokener tokener = new JSONTokener(fr);
      jsonObj = new JSONObject(tokener);
    }
    catch (IOException exception)
    {
      log.error("IO Exception occured while taking screenshots");
      log.error(exception.getMessage());
      // No need to throw the exception, this method is used in Constants file
    }
    catch (Exception exception)
    {
      log.error("Error occured while fetching JSON from file");
      log.error(exception.getMessage());
      // No need to throw the exception, this method is used in Constants file
    }
    return jsonObj;
  }

  /**
   * Executes batch and shell scripts
   * @param path : Accepts path of batch and shell scripts
   * @param args : Arguments to be passed into the batch and shell scripts
   * @throws Exception : If an exception occurs
   */
  public static void executeScript(String path, String... args) throws Exception
  {
    try
    {
      StringBuilder sb = new StringBuilder(10);
      for (String s : args)
      {
        sb.append(String.format("\"%s\" ", s));
      }
      String cmd = "cmd /c start /wait call " + "\"" + path + "\" " + sb.toString().trim() + "";
      Process process = Runtime.getRuntime().exec(cmd);
      System.out.println("Executing batch file...");
      process.waitFor();
      System.out.println("Executed Successfully");
    }
    catch (IOException exception)
    {
      log.error("IO Exception occured while taking screenshots");
      log.error(exception.getMessage());
      throw exception;
    }
    catch (Exception exception)
    {
      log.error("Error occured while executing shell file");
      log.error(exception.getMessage());
      throw exception;
    }
  }

  /**
   * It reads CSV file and returns the data inside the file as a String
   * @param filePath : Path of CSV file
   * @return builder.toString().trim() : Read from CSV file
   * @throws Exception : If an exception occurs
   */
  public static String readCSV(String filePath) throws Exception
  {
    String line = "";
    StringBuilder builder = new StringBuilder(100);
    try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
    {
      while ((line = br.readLine()) != null)
      {
        builder.append(line).append("\n");
      }
    }
    catch (IOException exception)
    {
      log.error("IO Exception occured while taking screenshots");
      log.error(exception.getMessage());
      throw exception;
    }
    catch (Exception exception)
    {
      log.error("Error occured while reading CSV file");
      log.error(exception.getMessage());
      throw exception;
    }
    return builder.toString().trim();
  }

  /**
   *  Get the files inside a directory
   * @param path : Path of the file in directory
   * @return test_files : Files after iterating through each instance
   */
  public static String[] getFilesInsideADirectory(String path)
  {
    Collection<?> files = FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    String[] test_files = new String[files.size()];
    Iterator<?> iterator = files.iterator();
    int count = 0;
    while (iterator.hasNext())
    {
      test_files[count++] = iterator.next().toString();
    }
    return test_files;
  }

  /**
  * Write the environment details in a property file
  * @param environmentDetails : Details related to the environment in form of Array
  * @throws IOException : If an Input or Output exception occurs
  */
  public static void writePropertiesFile(String[] environmentDetails) throws IOException
  {
    try
    {
      Properties properties = new Properties();
      properties.setProperty("OS Type", environmentDetails[0]);
      properties.setProperty("Browser", environmentDetails[1]);
      properties.setProperty("Build Version", environmentDetails[2]);

      FileOutputStream fileOut = new FileOutputStream(Constants.PATH_ALLURE_ENVIRONMENT_FILE);
      properties.store(fileOut, null);
      fileOut.close();
    }
    catch (FileNotFoundException exception)
    {
      log.error(exception.getMessage());
      throw exception;
    }
    catch (IOException exception)
    {
      log.error(exception.getMessage());
      throw exception;
    }
  }

  /**
  * Get the data for the API URL
  * @param url : URL of the API
  * @return data : Return data From API
  * @throws Exception : If an Input or Output exception occurs
  */
  public static String getApiData(String url) throws Exception
  {

    String data = null;
    try
    {
      HttpClient client = HttpClientBuilder.create().build();
      HttpGet request = new HttpGet(url);
      request.addHeader("User-Agent", "Mozilla/5.0");
      HttpResponse response = client.execute(request);

      BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

      StringBuffer result = new StringBuffer();
      String line = "";
      while ((line = rd.readLine()) != null)
      {
        result.append(line + "\n");
      }

      data = result.toString();
    }
    catch (Exception exception)
    {
      log.error(exception.getMessage());
      throw exception;
    }
    return data;
  }

  /**
   * Get the required platform
   * @param osName : Name of the operating system
   * @return platform : Returns name of the operating system  
   */
  public static Platform getPlatform(String osName)
  {
    switch (osName)
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

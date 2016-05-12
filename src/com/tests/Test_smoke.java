package com.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test_smoke {
	
	private final Logger logger = Logger.getLogger(getClass().getSimpleName().getClass().getName());
	private static final String SCREENSHOT_PATH = "screenshots";
	private final String RESULTS_BASE_PATH = "logs";
	private String resultsPath = new File(RESULTS_BASE_PATH).getAbsolutePath();
	private String screenshotsResultsPath = new File(RESULTS_BASE_PATH + File.separator + SCREENSHOT_PATH).getAbsolutePath();
	protected Handler fileHandler = null;
    protected java.util.logging.Formatter formatter = null;
	protected WebDriver driver;
    protected Properties properties;
    protected String baseUrl;
    protected String currentDate;
    protected String browserType;
    protected String dataDir;
    protected StringBuffer verificationErrors = new StringBuffer();
    boolean assertion;
	private String xpath;
    
    public Test_smoke(){
    	if (!new File(resultsPath).exists()) {
			new File(resultsPath).mkdirs();
		}
		if (!new File(screenshotsResultsPath).exists()) {
			new File(screenshotsResultsPath).mkdirs();
		}
    }
	
    @Before
    	public void setUp() throws Exception {
    	String root = new File("").getAbsolutePath();
    	currentDate = getCurrentDateinCustomFormat(); 
    	dataDir = root+File.separator+"data";
    	fileHandler = new FileHandler("logs"+File.separator + currentDate + ".log");
    	formatter = new SimpleFormatter();
    	fileHandler.setLevel(Level.FINEST);
    	fileHandler.setFormatter(formatter);
    	logger.addHandler(fileHandler);
    	properties = new Properties();
    	String propFileName = dataDir+File.separator + "properties"+File.separator+"testdata.properties";
    	InputStream is = new FileInputStream(propFileName);
    try{
		properties.load(is);
   	    baseUrl = properties.getProperty("baseUrl");
		logger.info("loaded from properties - baseUrl: "+baseUrl);
		browserType = properties.getProperty("browser");
		logger.info("loaded from properties - browserType: "+browserType);
		driver = getBrowser(browserType);
        driver.get(baseUrl);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
  	   }
	catch (Exception e){
    		e.printStackTrace();  
    		};
    	logger.info("SetUp method for "+getClass().toString()+" is completed...");
	
   	}
    
    @Test
    public void test_execution_1() throws Exception{
    	try{
    	    	
    	//verify that there are a minimum of 10 artists on the starting page ************
    	xpath = ".//*[@id='artists-mix-filter']/span[text()='Alle']";
    	waitForElementPresence(By.xpath(xpath));
    	
    	xpath = ".//div[@class='mask']";
    	waitForElementPresence(By.xpath(xpath));
    	List<WebElement> picturesList = getDisplayedPcsList(xpath);
    	int listSize = picturesList.size();
		logger.info("there "+listSize+" displayed images have been found in the grid.");
    	    	
    	assertTrue("Expected: minimum of 10 artists on the starting page", picturesList.size()>=10);
    	}
    	catch (Exception ex) {
    		System.err.println("Exception message: " + ex.getMessage());
    	}

    }
    
    @Test
    public void test_execution_2() throws Exception{
    	Actions builder = new Actions(driver);    
    	
    	//verify that the "Maifeld Derby" is visible, if the filter is selected ***********
    	String expectedText = "https://love-your-artist.com/de/maifeld-derby/";
    	
    	//Select English language
    	xpath = ".//div[child::span[text()='Language']]";
    	waitAnElement_andClick(By.xpath(xpath));
    	xpath = "//a[contains(@href,'https://love-your-artist.com/en/')]";
    	waitAnElement_andClick(By.xpath(xpath));
    	
    	//Select Festivals, then Maifeld Derby
    	xpath = "//span[@class='filter'][text()='Festivals']";
    	waitAnElement_andClick(By.xpath(xpath));
    	xpath = ".//div[@id='main']//div[contains(@style,'display: inline-block;')]/a[@href]";
    	waitForElementPresence(By.xpath(xpath));
    	
    	List<String> actualList = getImgsHrefList(xpath); 
    	assertion = actualList.contains(expectedText);
    	assertTrue("Expected: 'Maifeld Derby' is visible", assertion == true);
    	
    	//'buy' an available merch and verify the shipping costs to germany in the "shipping" step. ***********
    	//Click on the Maifeld Derby img
    	getElementFromTheList(xpath, expectedText).click();
    
    	//Select an available item
    	xpath = "//div[@class='info']//descendant::li[not(span[@class='sold_out'])][1]//form/a";
    	clickUsingScrolling(xpath);
    	xpath = "//div[@class='cart-btn']//input";
      	clickUsingExecutor_forXpath(builder, xpath);
    	
    	//Go to checkout
    	xpath = "//div[@class='single_product_in_list row']//input[@class='ccm-core-commerce-cart-buttons-checkout'][@value='Bestellung aufgeben']";
       	clickUsingExecutor_forXpath(builder, xpath);
    	
    	//Press Next
    	String id_name = "submit_next";
       	clickUsingExecutor_forId(builder, id_name);
    	 
    	//Fill the dialog fields
    	xpath = "//label[text()='Vorname']//following-sibling::input";
    	waitForElementPresence(By.xpath(xpath));
    	driver.findElement(By.xpath(xpath)).sendKeys("First Name");
    	xpath = "//label[text()='Name']//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("Name");
       	xpath = "//label[contains(text(),'Email-Adresse')]//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("myEmail@techDetails.com");
       	xpath = "//table[not(contains(@style,'display: none;'))]//label[contains(text(),'Stra')]//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("Rasen str.");
       	xpath = "//label[text()='Hausnummer']//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("18");
       	xpath = "//label[text()='PLZ']//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("12345");
       	xpath = "//label[text()='Ort']//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("Bremen");
       	xpath = "//table[not(contains(@style,'display: none;'))]//select[contains(@id,'country')]";
        WebElement we = driver.findElement(By.xpath(xpath));
        new Select(we).selectByVisibleText("Deutschland");
        xpath = "//label[contains(text(),'Ich habe die Allgemeinen ')]//following-sibling::div/label[child::input]";
      	clickUsingExecutor_forXpath(builder, xpath);
    	
        //Press Next
        id_name = "submit_next";
        clickUsingExecutor_forId(builder, id_name);
    	
        //verify the shipping costs to germany
    	xpath = ".//div[child::span[contains(text(),'zzgl. versicherter Versand nach Deutschland.')]]/span[contains(text(),'4,99')]";
    	waitForElementPresence(By.xpath(xpath));
    	assertTrue("Expected: shipment cost to Germany 4,99Eur", isElementPresent(By.xpath(xpath)));
    	//***********************
  
    	//go back
    	xpath =".//input[@name='submit_previous']";
    	clickUsingExecutor_forXpath(builder, xpath);
    	//Attention: there is no the obvious way to add a new item to the cart from this or previous step - usability bug!!!
    	
    	//open the cart
    	xpath = "//a[@id='warenkorb_link']";
    	clickUsingExecutor_forXpath(builder, xpath);
    	    	
    	//Delete the item from the cart
    	xpath = "//div[@id='warenkorbModal']//span[contains(@class,'-commerce-cart-remove update-cart')]/a";
    	clickUsingExecutor_forXpath(builder, xpath);
    	
    	//verify that the cart is empty
    	xpath = "//h1[text()='Ihr Warenkorb ist leer']";
    	waitForElementPresence(By.xpath(xpath));
    	assertTrue("Expected: the cart should be empty on this step!", isElementPresent(By.xpath(xpath))==true);
    	
    	//Close the cart dialog 
    	xpath = "//div[@id='header']//descendant::div[contains(@class,'-commerce-cart-buttons')][1]/input";
       	waitAnElement_andClick(By.xpath(xpath));
    	
    	//On this step page should not contain the item that has been added to the cart because the cart is empty - bug?!!!
    	//Remove the item again
    	xpath = "//span[contains(@class,'-commerce-cart-remove update-cart')]/a";
    	clickUsingExecutor_forXpath(builder, xpath);
    	//verify that the cart is empty again
    	xpath = "//h1[text()='Ihr Warenkorb ist leer']";
    	assertTrue("Expected: the cart should be empty on this step!", isElementPresent(By.xpath(xpath))==true);
    	
    	//Go back to the main page
    	xpath = "//div[@id='content']//descendant::div[contains(@class,'-commerce-cart-buttons')][1]/input";
    	clickUsingExecutor_forXpath(builder, xpath);
    	
    	//Why at this step English language that has been set before now is changed to German? - bug!
    	//Select English language
    	xpath = ".//div[child::span[text()='Language']]";
    	clickUsingExecutor_forXpath(builder, xpath);
    	xpath = "//a[contains(@href,'https://love-your-artist.com/index.php/en/') and not(contains(@class,'-multilingual-'))]"; //Attention: the link txt differs from the original 'https://love-your-artist.com/en/' (see beginning of the test script)
    	clickUsingExecutor_forXpath(builder, xpath);
    	
    	//Click on Festival
    	xpath = "//span[@class='filter'][text()='Festivals']";
    	clickUsingExecutor_forXpath(builder, xpath);
      	
    	//'buy' an available merch and verify the shipping costs to germany in the "shipping" step.
    	driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
    	xpath = ".//div[@id='main']//div[contains(@style,'display: inline-block;')]/a[contains(@href,'/de/maifeld-derby/')]";
    	clickUsingExecutor_forXpath(builder, xpath);
    	   
    	//buy additional 2 tickets
    	xpath = "//descendant::div[@class='about-product'][1]//span[text()='TICKETS KAUFEN']";
    	clickUsingExecutor_forXpath(builder, xpath);
      	xpath = "//select[@name='quantity']";
        we = driver.findElement(By.xpath(xpath));
        new Select(we).selectByVisibleText("2");
        xpath = "//a[@class='buythis']";
        clickUsingExecutor_forXpath(builder, xpath);
        xpath = "//button[text()='Direkt zum Checkout']";
        clickUsingExecutor_forXpath(builder, xpath);
        
        
        //verify that now there is no shipping step needed.
        //First need to re-enter the Vorname/Email-Adresse/Name - seems like this is a bug?
    	xpath = "//label[text()='Vorname']//following-sibling::input";
    	waitForElementPresence(By.xpath(xpath));
    	driver.findElement(By.xpath(xpath)).sendKeys("First Name");
    	xpath = "//label[text()='Name']//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("Name");
       	xpath = "//label[contains(text(),'Email-Adresse')]//following-sibling::input";
       	driver.findElement(By.xpath(xpath)).sendKeys("myEmail@techDetails.com");
        
        //Press Next
        id_name = "submit_next";
        clickUsingExecutor_forId(builder, id_name);
        
        //wait for image logo for payment and tick the payment method
        xpath = "//img[@class='payment_logo']";
        waitForElementPresence(By.xpath(xpath));
        xpath = "//label[child::img[@class='payment_logo']]";
        clickUsingExecutor_forXpath(builder, xpath);
        
        //verify that  there is no shipping info present
        xpath = ".//div[child::span[contains(text(),'zzgl. versicherter Versand nach Deutschland.')]]/span[contains(text(),'4,99')]";
    	assertFalse("Expected: shipment cost to Germany 4,99Eur should not be present", isElementPresent(By.xpath(xpath)));
    	 xpath = "//*[contains(text(),'Shipping')]";
     	assertFalse("Expected: shipment info should not be present", isElementPresent(By.xpath(xpath)));
        
        //Press Next
        id_name = "submit_next";
        waitForElementPresence(By.id(id_name));
        clickUsingExecutor_forId(builder, id_name);
        
        //wait for the text Thanks for your purchase
        xpath = "//p[contains(text(),'Danke ')][contains(text(),'Ihren Einkauf')]";
        waitForElementPresence(By.xpath(xpath));
        
        //verify that  there is no shipping info present
        xpath = ".//div[child::span[contains(text(),'zzgl. versicherter Versand nach Deutschland.')]]/span[contains(text(),'4,99')]";
    	assertFalse("Expected: shipment cost to Germany 4,99Eur should not be present", isElementPresent(By.xpath(xpath)));
        xpath = "//*[contains(text(),'Shipping')]";
    	assertFalse("Expected: shipment info should not be present", isElementPresent(By.xpath(xpath)));
   
    }

	private void clickUsingExecutor_forXpath(Actions builder, String xpath) throws InterruptedException {
		waitForElementPresence(By.xpath(xpath));
		builder.moveToElement(driver.findElement(By.xpath(xpath))).build().perform();
		JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(xpath)));
        driver.manage().timeouts().implicitlyWait(6,TimeUnit.SECONDS);
       }

	private void clickUsingExecutor_forId(Actions builder, String idName) throws InterruptedException {
		waitForElementPresence(By.id(idName));
		builder.moveToElement(driver.findElement(By.id(idName))).build().perform();
		JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", driver.findElement(By.id(idName)));
	}  
        
	private void clickUsingExecutor(Actions builder, WebElement elem) throws InterruptedException {
		waitForElementPresence(By.xpath(xpath));
		builder.moveToElement(elem).build().perform();
		JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", elem);
      }     
        
         
    private List<WebElement> getDisplayedPcsList(String xpath){
    	List<WebElement> pcsList = driver.findElements(By.xpath(xpath));
    	WebElement ele = null;
        Iterator<WebElement> iter = pcsList.iterator();
        while (iter.hasNext()) {
            ele = iter.next();
            if (ele.isDisplayed()) {
            	pcsList.add(ele);
             }
         }
        System.out.println("size of displayed pics in the grid with xpath: "+xpath+" is "+pcsList.size());
        return pcsList;
    }
        
 	public List<String> getImgsHrefList(String xpath) {
 		List<WebElement> elements = driver.findElements(By.xpath(xpath));
 		List<String> imgsTexts = new ArrayList<String>();
 		WebElement elem = null;
 		for (int i = 0; i<elements.size(); i++){
 			 elem = elements.get(i);
 			 String name; 
 			 	 name = elem.getAttribute("href");
 	    	     imgsTexts.add(name);
 	    			}	
 		for(String imgText : imgsTexts){  
 					System.out.println("img href text: "+imgText); 
 				}		
 			return imgsTexts;
 	}
 	
 	public void clickUsingScrolling(String xpath) throws Exception
 	{
 		WebElement elem = getWebElement(xpath);
 		if(elem.isDisplayed()==false){
 			System.out.println("trying to scrolling to element with locator: "+xpath+" to be displayed");
 			clickElem_viaScrolling(xpath);
 		}
 		else if(!(elem.isDisplayed()==false)){
 			System.out.println("Element with locator: "+xpath+" is displayed");
 			driver.findElement(By.xpath(xpath)).click();
 		}
 	}
 	
 	public void clickElem_viaScrolling(String xpath) throws Exception
 	{	
 		Locatable mouseDownItem  = (Locatable) driver.findElement(By.xpath(xpath));
 		Mouse mouse = ((HasInputDevices) driver).getMouse();
 		mouse.mouseDown(mouseDownItem.getCoordinates());
 		//driver.findElement(By.xpath(xpath)).click();
 		Actions builder = new Actions(driver);    
		builder.moveToElement(driver.findElement(By.xpath(xpath))).build().perform();
		 JavascriptExecutor executor = (JavascriptExecutor) driver;
         executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(xpath)));
         Thread.sleep(4500);
         System.out.println("Clicking on item ...");
	}

 	public WebElement getWebElement(String xpath) 
 	{
 			List<WebElement> elems = driver.findElements(By.xpath(xpath));
 			if (!(elems.size() == 0)){
 				System.out.println("The element with xpath="+xpath+" is found...");
 				return elems.get(0);
 				}
 			else {System.out.println("The element with xpath="+xpath+" is not found!");
 			return null;}
 	}
  
 	 private WebElement getElementFromTheList(String xpath, String expectedText) {
 		List<WebElement> elements = driver.findElements(By.xpath(xpath));
  		List<String> imgsTexts = new ArrayList<String>();
  		WebElement elem = null;
  		for (int i = 0; i<elements.size(); i++){
  			 elem = elements.get(i);
  			 String name; 
  			 	 name = elem.getAttribute("href");
  	    	     imgsTexts.add(name);
  			 	 if(name.equals(expectedText)){
  			 		return elem;
  			 	 }
  	    	}	
   		return null;
 	}
 	 	
    private String getCurrentDateinCustomFormat(){
		Date dateNow = new Date();	
		SimpleDateFormat dateformatyyyyMMdd = new SimpleDateFormat("MM_dd_yyyy");
		String date_to_string = dateformatyyyyMMdd.format(dateNow);
		System.out.println("date into MMddyyyy format: " + date_to_string);
		return date_to_string;
		}
    
    protected WebDriver getBrowser(String browserType) {
    	if(driver == null) {
		 		if(browserType.equals("Firefox")) {
		 			driver = new FirefoxDriver();
		 	}
		 		if(browserType.equals("Chrome")) {
		 			driver = new ChromeDriver();
			}
		 		if(browserType.equals("IE")) {
		 			driver = new InternetExplorerDriver();
		    } 
		}
		return driver;
    }
    
    public void waitAnElement_andClick(By Locator) {
        Wait<WebDriver> wait = new WebDriverWait(driver, 25);
        wait.until(ExpectedConditions.presenceOfElementLocated(Locator));
        driver.findElement(Locator).click();
        logger.info("after waitAnElement_andClick method with locator: " + Locator.toString());
    }
    
    public boolean isElementPresent(By locator){
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		List<WebElement> list =driver.findElements(locator);
		driver.manage().timeouts().implicitlyWait(8,TimeUnit.SECONDS);
		if (list.size()==0){
			 logger.info("element with locator: "+locator+" is not found...");
			return false;
		}
		else{
			 logger.info("element with locator: "+locator+" is found...");
			return list.get(0).isDisplayed();
			}
	}
    
    public void waitForElementPresence(By Locator) {
        Wait<WebDriver> wait = new WebDriverWait(driver, 25);
        wait.until(ExpectedConditions.presenceOfElementLocated(Locator));
        System.out.println("after waitForElementPresence method with locator: " + Locator.toString());
        logger.info("after waitForElementPresence method with locator: " + Locator.toString());
    }
    
    @After
    public void tearDown() throws Exception {
        String currentDate = getCurrentDateinCustomFormat();
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	    FileUtils.copyFile(scrFile, new File(screenshotsResultsPath+File.separator+"screenshot_" + getClass().getSimpleName() + "_" + currentDate + ".png"));
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

}

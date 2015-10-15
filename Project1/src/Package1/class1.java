package Package1;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class class1 {
	//Defined the Test Data parameters
	String vExecute, vTDID, vURL, vPrice, vDown, vTradeIn, vRate, vMonthlyPayment, vTotalPayment;
	String xPrice, xDown, xTradeIn, xRate, xCalc, xMonthly, xTotal;
	WebDriver driver;
	int xRows, xCols;
	String[][] xData;
	String xlPath, xlSheet, xlPathRes;
	
	@BeforeMethod
	public void myBefore() throws Exception{
		// Get TestData from the Excel
		xlPath = "Q://Documents/Project4-DDF.xls";
		xlPathRes = "Q://Documents/Project4-DDF-Res.xls";
		xlSheet = "TestData";
		xData = readXL(xlPath, xlSheet);
		
		// Create the WebDriver
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		// Define the xPaths that are needed
		xPrice = "//input[@name='param[principal]']";
		xDown = "//input[@name='param[down_payment]']";
		xTradeIn = "//input[@name='param[trade_in_value]']";
		xRate = "//input[@name='param[interest_rate]']";
		xCalc = "//input[@value='Calculate']";
		xMonthly = "//h3";
		xTotal = "(//h3)[2]";
		}
	
	@Test
	public void driverDFTest() {

		for (int i = 1; i<xRows; i++) {
			// Initialize the Test Data.
			vExecute = xData[i][0];
			vTDID = xData[i][1];
			vURL = xData[i][2];
			vPrice = xData[i][3];
			vDown = xData[i][4];
			vTradeIn = xData[i][5];
			vRate = xData[i][6];
			
			if (vExecute.equals("Y")) {
				System.out.println("Now executing Test Data : " + vTDID);
				
				//TC001	1	Go to home
				driver.get(vURL);
				
				//TC001	2	Enter Purchase Price
				driver.findElement(By.xpath(xPrice)).clear();
				driver.findElement(By.xpath(xPrice)).sendKeys(vPrice);
				
				//TC001	3	Enter Down Payment
				driver.findElement(By.xpath(xDown)).clear();
				driver.findElement(By.xpath(xDown)).sendKeys(vDown);
				
				//TC001	4	Enter Trade-in value
				driver.findElement(By.xpath(xTradeIn)).clear();
				driver.findElement(By.xpath(xTradeIn)).sendKeys(vTradeIn);
				
				//TC001	5	Enter Interest Rate
				driver.findElement(By.xpath(xRate)).clear();
				driver.findElement(By.xpath(xRate)).sendKeys(vRate);
				
				//TC001	6	Click Calculate
				driver.findElement(By.xpath(xCalc)).click();
					
				//TC001	7	Get the monthly premium
				vMonthlyPayment = driver.findElement(By.xpath(xMonthly)).getText();
				System.out.println("Monthly payment is : " + vMonthlyPayment);
				xData[i][7] = vMonthlyPayment;
				
				//TC001	8	Total payments
				vTotalPayment = driver.findElement(By.xpath(xTotal)).getText();
				System.out.println("Total payment is : " + vTotalPayment);
				xData[i][8] = vTotalPayment;			
			}
		}
	}
	
	@AfterMethod
	public void myEndCode() throws Exception{
		//TC001	9	Close the browser
		driver.close();
		writeXL(xlPathRes, xlSheet, xData);		
	}
	
	// Exercise : Create a method that calculates the monthly payment using a formula
	/*
	 * EMI = ( P × r × (1+r)n ) / ((1+r)n − 1)  
	 * Where    EMI = Equated Monthly Installment    
	 *  P = Loan Amount - Down payment     
	 *  r = Annual Interest rate / 1200     
	 *  n = Term (Period or no.of year or months for loan re-payment.)
	 * 
	 */
	
	// Method to write into an XL
		public void writeXL(String sPath, String iSheet, String[][] xData) throws Exception{

		    	File outFile = new File(sPath);
		        HSSFWorkbook wb = new HSSFWorkbook();
		        HSSFSheet osheet = wb.createSheet(iSheet);
		        int xR_TS = xData.length;
		        int xC_TS = xData[0].length;
		    	for (int myrow = 0; myrow < xR_TS; myrow++) {
			        HSSFRow row = osheet.createRow(myrow);
			        for (int mycol = 0; mycol < xC_TS; mycol++) {
			        	HSSFCell cell = row.createCell(mycol);
			        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			        	cell.setCellValue(xData[myrow][mycol]);
			        }
			        FileOutputStream fOut = new FileOutputStream(outFile);
			        wb.write(fOut);
			        fOut.flush();
			        fOut.close();
		    	}
			}
		
		// Method to read XL
			public String[][] readXL(String sPath, String iSheet) throws Exception{
					String[][] xData;   

					File myxl = new File(sPath);                                
					FileInputStream myStream = new FileInputStream(myxl);                                
					HSSFWorkbook myWB = new HSSFWorkbook(myStream);                                
					HSSFSheet mySheet = myWB.getSheet(iSheet);                                 
					xRows = mySheet.getLastRowNum()+1;                                
					xCols = mySheet.getRow(0).getLastCellNum();                                
					xData = new String[xRows][xCols];        
					for (int i = 0; i < xRows; i++) {                           
							HSSFRow row = mySheet.getRow(i);
							for (int j = 0; j < xCols; j++) {                               
								HSSFCell cell = row.getCell(j);
								String value = "-";
								if (cell!=null){
									value = cellToString(cell);
								}
								xData[i][j] = value;      
								//System.out.println(value);
								//System.out.print("--");
								}        
							}                                      
					return xData;
			}
			
			//Change cell type
			public static String cellToString(HSSFCell cell) { 
				// This function will convert an object of type excel cell to a string value
				int type = cell.getCellType();                        
				Object result;                        
				switch (type) {                            
					case HSSFCell.CELL_TYPE_NUMERIC: //0                                
						result = cell.getNumericCellValue();                                
						break;                            
					case HSSFCell.CELL_TYPE_STRING: //1                                
						result = cell.getStringCellValue();                                
						break;                            
					case HSSFCell.CELL_TYPE_FORMULA: //2                                
						throw new RuntimeException("We can't evaluate formulas in Java");  
					case HSSFCell.CELL_TYPE_BLANK: //3                                
						result = "%";                                
						break;                            
					case HSSFCell.CELL_TYPE_BOOLEAN: //4     
						result = cell.getBooleanCellValue();       
						break;                            
					case HSSFCell.CELL_TYPE_ERROR: //5       
						throw new RuntimeException ("This cell has an error");    
					default:                  
						throw new RuntimeException("We don't support this cell type: " + type); 
						}                        
				return result.toString();      
				}

}

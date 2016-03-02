package com.qsa.jaf;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Uma Mahesh.G
 * Date: 18/11/13
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */

public class xlsReader {

	public  String path;
	public  FileInputStream fis = null;
	public  FileOutputStream fileOut =null;
	private HSSFWorkbook workbook = null;
	private HSSFSheet sheet = null;
	private HSSFRow row   =null;
	private HSSFCell cell = null;
	
	public xlsReader(String path) {
		
		this.path=path;
		try {
			fis = new FileInputStream(path);
			workbook = new HSSFWorkbook(fis);
			sheet = workbook.getSheetAt(0);
			fis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

    // returns the row count in a sheet
	public int getRowCount(String sheetName){
		int index = workbook.getSheetIndex(sheetName);
		if(index==-1)
			return 0;
		else{
		sheet = workbook.getSheetAt(index);
		int number=sheet.getLastRowNum()+1;
		return number;
		}
		
	}
    public List<String> getSheetNames(String path) {
        int sheetsCount = 0;
        String sheetName = null;
        List<String> sheetNameList = new ArrayList<String>();
        try {
            sheetsCount = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetsCount; i++) {
                sheetName = workbook.getSheetName(i);
                sheetNameList.add(sheetName);
            }

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheetNameList;
    }
	// returns the data from a cell
	public String getCellData(String sheetName,String colName,int rowNum){
		try{
			if(rowNum <=0)
				return "";

		int index = workbook.getSheetIndex(sheetName);
		int col_Num=-1;
		if(index==-1)
			return "";

		sheet = workbook.getSheetAt(index);
		row=sheet.getRow(0);
            //System.out.println(row.getLastCellNum());
		for(int i=0;i<row.getLastCellNum();i++){

			if(row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
				col_Num=i;
		}
		if(col_Num==-1)
			return "";

		sheet = workbook.getSheetAt(index);
		row = sheet.getRow(rowNum-1);
		if(row==null)
			return "";
		cell = row.getCell(col_Num);

		if(cell==null)
			return "";
		//System.out.println(cell.getCellType());
		if(cell.getCellType()==Cell.CELL_TYPE_STRING)
			  return cell.getStringCellValue();
		else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC || cell.getCellType()==Cell.CELL_TYPE_FORMULA ){

			  String cellText  = String.valueOf(cell.getNumericCellValue());
			  if (HSSFDateUtil.isCellDateFormatted(cell)) {
		           // format in form of M/D/YY
				  double d = cell.getNumericCellValue();

				  Calendar cal =Calendar.getInstance();
				  cal.setTime(HSSFDateUtil.getJavaDate(d));
		            cellText =
		             (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
		           cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" +
		                      cal.get(Calendar.MONTH)+1 + "/" +
		                      cellText;
		            }



			  return cellText;
		  }else if(cell.getCellType()==Cell.CELL_TYPE_BLANK)
		      return "";
		  else
			  return String.valueOf(cell.getBooleanCellValue());

		}
		catch(Exception e){

			e.printStackTrace();
			return "row "+rowNum+" or column "+colName +" does not exist in xls";
		}
	}
	
	// returns the data from a cell
	public String getCellData(String sheetName,int colNum,int rowNum){
		try{
			if(rowNum <=0)
				return "";

		int index = workbook.getSheetIndex(sheetName);

		if(index==-1)
			return "";


		sheet = workbook.getSheetAt(index);
		row = sheet.getRow(rowNum-1);
		if(row==null)
			return "";
		cell = row.getCell(colNum);
		if(cell==null)
			return "";

	  if(cell.getCellType()==Cell.CELL_TYPE_STRING)
		  return cell.getStringCellValue();
	  else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC || cell.getCellType()==Cell.CELL_TYPE_FORMULA ){

		  String cellText  = String.valueOf(cell.getNumericCellValue());
		  if (HSSFDateUtil.isCellDateFormatted(cell)) {
	           // format in form of M/D/YY
			  double d = cell.getNumericCellValue();

			  Calendar cal =Calendar.getInstance();
			  cal.setTime(HSSFDateUtil.getJavaDate(d));
	            cellText =
	             (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
	           cellText = cal.get(Calendar.MONTH)+1 + "/" +
	                      cal.get(Calendar.DAY_OF_MONTH) + "/" +
	                      cellText;

	          // System.out.println(cellText);

	         }



		  return cellText;
	  }else if(cell.getCellType()==Cell.CELL_TYPE_BLANK)
	      return "";
	  else
		  return String.valueOf(cell.getBooleanCellValue());
		}
		catch(Exception e){

			e.printStackTrace();
			return "row "+rowNum+" or column "+colNum +" does not exist  in xls";
		}
	}

  // find whether sheets exists	
	public boolean isSheetExist(String sheetName){
		int index = workbook.getSheetIndex(sheetName);
		if(index==-1){
			index=workbook.getSheetIndex(sheetName.toUpperCase());
				if(index==-1)
					return false;
				else
					return true;
		}
		else
			return true;
	}
	
	// returns number of columns in a sheet	
	public int getColumnCount(String sheetName){
		// check if sheet exists
		if(!isSheetExist(sheetName))
		 return -1;
		
		sheet = workbook.getSheet(sheetName);
		row = sheet.getRow(0);
		
		if(row==null)
			return -1;
		
		return row.getLastCellNum();
		
		
		
	}

    // returns cell row number from the sheet
	public int getCellRowNum(String sheetName,String colName,String cellValue){
		
		for(int i=2;i<=getRowCount(sheetName);i++){
	    	if(getCellData(sheetName,colName , i).equalsIgnoreCase(cellValue)){
	    		return i;
	    	}
	    }
		return -1;
		
	}



    // returns true if data is set successfully else false
    public boolean setCellData(String sheetName,String colName,int rowNum, String data,String url){
        //System.out.println("setCellData setCellData******************");
        try{
            fis = new FileInputStream(path);
            workbook = new HSSFWorkbook(fis);

            if(rowNum<=0)
                return false;

            int index = workbook.getSheetIndex(sheetName);
            int colNum=-1;
            if(index==-1)
                return false;


            sheet = workbook.getSheetAt(index);

            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){

                if(row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
                    colNum=i;
            }

            if(colNum==-1)
                return false;
            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum-1);
            if (row == null)
                row = sheet.createRow(rowNum-1);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            cell.setCellValue(data);
            HSSFCreationHelper createHelper = workbook.getCreationHelper();

            //cell style for hyperlinks
            //by default hypelrinks are blue and underlined
            CellStyle hlink_style = workbook.createCellStyle();
            HSSFFont hlink_font = workbook.createFont();
            hlink_font.setUnderline(HSSFFont.U_SINGLE);

            hlink_font.setColor(IndexedColors.BLUE.getIndex());
            hlink_style.setFont(hlink_font);

            HSSFHyperlink link = createHelper.createHyperlink(XSSFHyperlink.LINK_FILE);
            link.setAddress(url);
            cell.setHyperlink(link);
            cell.setCellStyle(hlink_style);

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);

            fileOut.close();

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    // returns true if data is set successfully else false
    public boolean setCellData(String sheetName,String colName,int rowNum, String data){
        try{
            fis = new FileInputStream(path);
            workbook = new HSSFWorkbook(fis);

            HSSFFont defaultFont= workbook.createFont();
            defaultFont.setFontHeightInPoints((short)10);
            defaultFont.setFontName("Cambria");
            defaultFont.setColor(IndexedColors.BLACK.getIndex());
            defaultFont.setBoldweight(defaultFont.BOLDWEIGHT_BOLD);
            defaultFont.setItalic(false);

            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.RED.index);
            style.setFont(defaultFont);

            if(rowNum<=0)
                return false;

            int index = workbook.getSheetIndex(sheetName);
            int colNum=-1;
            if(index==-1)
                return false;


            sheet = workbook.getSheetAt(index);


            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){

                if(row.getCell(i).getStringCellValue().trim().equals(colName))
                    colNum=i;
            }
            if(colNum==-1)
                return false;

            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum-1);
            if (row == null)
                row = sheet.createRow(rowNum-1);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);


            cell.setCellValue(data);

            fileOut = new FileOutputStream(path);

            workbook.write(fileOut);

            fileOut.close();

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // returns true if column is created successfully
    public boolean addColumn(String sheetName,String colName){
        //System.out.println("**************addColumn*********************");

        try{
            fis = new FileInputStream(path);
            workbook = new HSSFWorkbook(fis);
            int index = workbook.getSheetIndex(sheetName);
            if(index==-1)
                return false;

            HSSFFont defaultFont= workbook.createFont();
            defaultFont.setFontHeightInPoints((short)10);
            defaultFont.setFontName("Cambria");
            defaultFont.setColor(IndexedColors.BLACK.getIndex());
            defaultFont.setBoldweight(defaultFont.BOLDWEIGHT_BOLD);
            defaultFont.setItalic(false);

            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.RED.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            style.setFont(defaultFont);

            sheet=workbook.getSheetAt(index);

            row = sheet.getRow(0);
            if (row == null)
                row = sheet.createRow(0);

            //cell = row.getCell();
            //if (cell == null)
            //System.out.println(row.getLastCellNum());
            if(row.getLastCellNum() == -1)
                cell = row.createCell(0);
            else
                cell = row.createCell(row.getLastCellNum());

            cell.setCellValue(colName);
            cell.setCellStyle(style);

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;


    }

    public boolean addSheet(String  sheetname){

        FileOutputStream fileOut;
        try {
            workbook.createSheet(sheetname);
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

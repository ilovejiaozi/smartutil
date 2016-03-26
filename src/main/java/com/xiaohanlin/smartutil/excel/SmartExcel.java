package com.xiaohanlin.smartutil.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SmartExcel {

	List<SmartSheet> sheetList;

	public SmartExcel(String fileName) {
		org.apache.poi.ss.usermodel.Workbook workbook = null;
		try {
			workbook = fileName.endsWith(".xls") ? new HSSFWorkbook(new FileInputStream(fileName)) : new XSSFWorkbook(new FileInputStream(fileName));
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int index = 0; index < numberOfSheets; index++) {
				addSheet(new SmartSheet(workbook.getSheetAt(index)));
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
				}
				workbook = null;
			}
		}
	}

	final protected void addSheet(SmartSheet sheet) {
		this.sheetList.add(sheet);
	}

	final protected void addSheetList(Collection<SmartSheet> sheets) {
		this.sheetList.addAll(sheets);
	}

	final public List<SmartSheet> getSheetList() {
		return sheetList;
	}

	final public SmartSheet getSheet(String sheetName) {
		for (SmartSheet sheet : sheetList) {
			if (sheet.getName().equals(sheetName)) {
				return sheet;
			}
		}
		return null;
	}
}

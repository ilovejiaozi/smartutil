package com.xiaohanlin.smartutil.excel;

import java.io.File;
import java.io.IOException;

import com.xiaohanlin.smartutil.file.SmartFileUtil;

public class SmartExcelReader {

	public static SmartExcel openExcel(String filePath) throws IOException {
		SmartFileUtil.isFileExistThrowException(filePath);
		return new SmartExcel(new File(filePath).getCanonicalPath());
	}
	
	public static SmartExcel openExcel(File file) throws IOException {
		return new SmartExcel(file.getCanonicalPath());
	}

}

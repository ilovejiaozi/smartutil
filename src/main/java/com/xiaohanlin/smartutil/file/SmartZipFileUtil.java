package com.xiaohanlin.smartutil.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * zip文件
 * @author jiaozi
 *
 */
public class SmartZipFileUtil {
	/**
	 * 创建ZIP文件
	 * 
	 * @param sourcePath
	 *            文件或文件夹路径
	 * @param zipPath
	 *            生成的zip文件存在路径（包括文件名）
	 * @throws IOException
	 */
	public static void createZip(String sourcePath, String zipPath) throws IOException {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipPath);
			zos = new ZipOutputStream(fos);
			writeZip(new File(sourcePath), "", zos);
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	private static void writeZip(File file, String parentPath, ZipOutputStream zos) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {// 处理文件夹
				ZipEntry ze = new ZipEntry(parentPath + file.getName() + File.separator);
				zos.putNextEntry(ze);

				parentPath += file.getName() + File.separator;
				File[] files = file.listFiles();
				for (File f : files) {
					writeZip(f, parentPath, zos);
				}
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					ZipEntry ze = new ZipEntry(parentPath + file.getName());
					zos.putNextEntry(ze);
					byte[] content = new byte[1024 * 1024];
					int len;
					while ((len = fis.read(content)) != -1) {
						zos.write(content, 0, len);
						zos.flush();
					}

				} catch (FileNotFoundException e) {
					throw e;
				} catch (IOException e) {
					throw e;
				} finally {
					try {
						if (fis != null) {
							fis.close();
						}
					} catch (IOException e) {
						throw e;
					}
				}
			}
		}
	}
}

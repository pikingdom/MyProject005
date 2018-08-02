package com.nd.hilauncherdev.plugin.navigation.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 文件相关的工具类<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class FileUtil {

	private static final String TAG = "FileUtil";

	/**
	 * 往创建文件，并往文件中写内容
	 * 
	 * @param path
	 * @param content
	 * @param append
	 */
	public static void writeFile(String path, String content, boolean append) {
		try {
			File f = new File(path);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			if (!f.exists()) {
				f.createNewFile();
				f = new File(path); // 重新实例化
			}
			FileWriter fw = new FileWriter(f, append);
			if ((content != null) && !"".equals(content)) {
				fw.write(content);
				fw.flush();
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定路么下是否存在文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return boolean
	 */
	public static boolean isFileExits(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists())
				return true;
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * 从assets 目录读取文件内容
	 * @param context
	 * @param srcFileName
	 * @return
	 */
	public static String readFromAssetsFile(Context context,String srcFileName){
		
		try {
			InputStream is;
			is = context.getAssets().open(srcFileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String text = new String(buffer);
			
			return text;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 从assets目录复制文件到指定路径
	 * 
	 * @param context
	 * @param srcFileName
	 *            复制的文件名
	 * @param targetDir
	 *            目标目录
	 * @param targetFileName
	 *            目标文件名
	 * @return boolean
	 */
	public static boolean copyPluginAssetsFile(Context context, String srcFileName, String targetDir, String targetFileName) {
		AssetManager asm = null;
		FileOutputStream fos = null;
		DataInputStream dis = null;
		try {
			asm = context.getAssets();
			dis = new DataInputStream(asm.open(srcFileName));
			createDir(targetDir);
			File targetFile = new File(targetDir, targetFileName);
			if (targetFile.exists()) {
				targetFile.delete();
			}

			fos = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = dis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			return true;
		} catch (Exception e) {
			Log.w(TAG, "copy assets file failed:" + e.toString());
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (dis != null)
					dis.close();
			} catch (Exception e2) {
			}
		}

		return false;
	}// end copyAssetsFile

	/**
	 * 读取文件内容
	 * 
	 * @param path
	 * @return String
	 */
	public static String readFileContent(String path) {
		StringBuffer sb = new StringBuffer();
		if (!isFileExits(path)) {
			return sb.toString();
		}
		InputStream ins = null;
		try {
			ins = new FileInputStream(new File(path));
			BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 创建文件夹
	 * 
	 * @param dir
	 */
	public static void createDir(String dir) {
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}

	}

	public static boolean downloadFileByURL(String downloadUrl, String localPath) {
		File f = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			f = new File(localPath);
			if (!f.exists()) {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
				HttpGet httpget = new HttpGet(downloadUrl);
				HttpResponse resp = httpclient.execute(httpget);
				if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
					HttpEntity entity = resp.getEntity();
					is = entity.getContent();
					byte[] bs = new byte[2048];
					int len = -1;
					os = new FileOutputStream(f);
					while ((len = is.read(bs)) != -1) {
						os.write(bs, 0, len);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (f != null && f.exists()) {
				FileUtil.delFile(f.getAbsolutePath());
			}
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 删除某个文件
	 * 
	 * @param path
	 *            文件路径
	 */
	public static void delFile(String path) {
		try {
			File f = new File(path);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除path 下全部文件
	 * 
	 * @param path
	 * @return true删除成功
	 */
	public static boolean delAllFile(String path) {
		return delAllFile(path, null);
	}

	/**
	 * 删除文件夹内所有文件
	 * 
	 * @param path
	 * @param filenameFilter
	 *            过滤器 支持null
	 * @return boolean
	 */
	public static boolean delAllFile(String path, FilenameFilter filenameFilter) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return true;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		File[] tempList = file.listFiles(filenameFilter);
		int length = tempList.length;
		for (int i = 0; i < length; i++) {

			if (tempList[i].isFile()) {
				tempList[i].delete();
			}
			if (tempList[i].isDirectory()) {
				/**
				 * 删除内部文件
				 */
				delAllFile(tempList[i].getAbsolutePath(), filenameFilter);
				/**
				 * 删除空文件夹
				 */
				String[] ifEmptyDir = tempList[i].list();
				if (null == ifEmptyDir || ifEmptyDir.length <= 0) {
					tempList[i].delete();
				}
				flag = true;
			}
		}
		return flag;
	}
}

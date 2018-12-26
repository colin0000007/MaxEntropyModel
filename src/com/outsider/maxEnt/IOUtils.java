package com.outsider.maxEnt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
	public static String readTextWithLineCheckBreak(String path, String encoding) {
		return readText(path, encoding, "\n");
	}
	/**
	 * 读取文本文件，返回整个字符串，不包括换行符号
	 * @param path 文件路径
	 * @param encoding 编码，传入null或者空串使用默认编码
	 * @return
	 */
	public static String readText(String path, String encoding) {
		return readText(path, encoding, null);
	}
	/**
	 * 读取文本，指定每一行末尾符号
	 * @param path
	 * @param encoding
	 * @param lineEndStr
	 * @return
	 */
	public static String readText(String path, String encoding, String lineEndStr) {
		BufferedReader reader = null;
		try {
			if(lineEndStr == null) {
				lineEndStr = "";
			}
			if((!encoding.trim().equals(""))&&encoding!=null) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			}
			String s="";
			StringBuilder sb  = new StringBuilder();
			while((s=reader.readLine())!=null) {
				sb.append(s+lineEndStr);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * 读取文本文件，返回整个字符串，不包括换行符号
	 * @param path 文件路径
	 * @param encoding 编码，传入null或者空串使用默认编码
	 * @param addNewLine 是否加换行符
	 * @return
	 */
	public static List<String> readTextAndReturnLinesCheckLineBreak(String path, String encoding, boolean addNewLine) {
		BufferedReader reader = null;
		try {
			String lineBreak;
			if(addNewLine) {
				lineBreak = "\n";
			} else {
				lineBreak = "";
			}
			if((!encoding.trim().equals(""))&&encoding!=null) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			}
			String s="";
			List<String> list = new ArrayList<>();
			while((s=reader.readLine())!=null) {
				list.add(s+lineBreak);
			}
			reader.close();
			return list;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static List<String> readTextAndReturnLines(String path, String encoding){
		return readTextAndReturnLinesCheckLineBreak(path, encoding, false);
	}
	/**
	 * 读取文本的每一行
	 * 并且返回数组形式
	 * @param path
	 * @param encoding
	 * @return
	 */
	public static String[] readTextAndReturnLinesOfArray(String path, String encoding){
		List<String> lines = readTextAndReturnLines(path, encoding);
		String[] arr = new String[lines.size()];
		lines.toArray(arr);
		return arr;
	}
	/**
	 * 写入文本文件
	 * @param data
	 * @param path
	 * @param encoding
	 */
	public static void writeTextData2File(String data,String path,String encoding) {
		BufferedWriter writer = null;
		try {
			if((!encoding.trim().equals(""))&&encoding!=null) {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),encoding));
			} else {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
			}
			writer.write(data);
			writer.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

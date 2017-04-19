package cn.edu.bjut; 

import java.util.*;

import cn.edu.bjut.Dictionary; 

/*
 * 参�?�文献：
 * [1]	陈小�?, 2000. 现代汉语自动分析—Visual C++实现. 北京: 北京语言文化大学出版�?, 90-103. 
 * */

public class Segmentation {
	private Dictionary dict; 
	
	public Segmentation(Dictionary dict) {
		this.dict = dict; 
	}
	
	ArrayList<String> FMM(String str) {
		ArrayList<String> result = new ArrayList<String>(); 
		
		while (!str.equalsIgnoreCase("")) {
			int len = str.length(); 
			if (len > this.dict.getMaxWordLength()) {
				len = this.dict.getMaxWordLength(); 
			}
			
			String strTmp = str.substring(0, len); 
			while (len > 1 && !this.dict.containsKey(strTmp)) {
				strTmp = strTmp.substring(0, --len); 
			}
			
			result.add(strTmp); 
			str = str.substring(strTmp.length()); 
		}
		
		return result; 
	}
	
	ArrayList<String> BMM(String str) {
		ArrayList<String> result = new ArrayList<String>(); 
		
		while (!str.equalsIgnoreCase("")) {
			int len = str.length(); 
			if (len > this.dict.getMaxWordLength()) {
				len = this.dict.getMaxWordLength(); 
			}
			
			String strTmp = str.substring(str.length() - len); 
			while (len > 1 && !this.dict.containsKey(strTmp)) {
				strTmp = strTmp.substring(strTmp.length() - (--len)); 
			}
			
			result.add(strTmp); 
			str = str.substring(0, str.length() - strTmp.length()); 
		}
		
		
		
		return reserve(result); 
	}
	
	private ArrayList<String> reserve(ArrayList<String> list) {
		ArrayList<String> ret = new ArrayList<String>(list.size()); 
		
		for (int i = list.size() - 1; i >= 0; i--) {
			ret.add(list.get(i)); 
		}
		
		return ret; 
	}
	
	public static void main(String[] args) {
		Dictionary dict = new Dictionary(); 
		
		Segmentation seg = new Segmentation(dict); 
		
		String str1 = "智能可变气门正时系统"; 
		String str2 = "气门正时调控系统"; 
		
		ArrayList<String> strSeged1 = seg.FMM(str1); 
		System.out.print("\"" + str1 + "\"" + "前向�?大分词："); 
		for (int i = 0; i < strSeged1.size(); i++) {
			System.out.print(strSeged1.get(i) + " "); 
		}
		System.out.println(); 
		
		ArrayList<String> strSeged2 = seg.BMM(str2); 
		System.out.print("\"" + str2 + "\"" + "后向�?大分词："); 
		for (int i = 0; i < strSeged2.size(); i++) {
			System.out.print(strSeged2.get(i) + " "); 
		}
		System.out.println(); 
	}
}
package cn.edu.bjut; 

import java.util.*;  
import java.io.*; 

/*
 * 参�?�文献：
 * [1] 梅家�?, 竺一�?, 高蕴�?, 殷鸿�?, 1983. 《同义词词林�?. 上海: 上海辞书出版�?. 
 * [2] 哈工大信息检索研究室, 2009.  哈工大信息检索研究室同义词词林扩展版�?. Online Available: http://www.ir-lab.org/.
 * */
public class Dictionary {
	Map<String, List<String>> word2Code = new HashMap<String, List<String>>(); 
	int maxWordLength = 0; 
	String cilinPath = "resource/cilin.txt"; 
	
	public Dictionary() {
		loadDict(this.cilinPath);
		
	}
	
	public int getMaxWordLength() {
		return this.maxWordLength; 
	}
	
	private void loadDict(String cilinPath) {	
		try {
			FileReader file = new FileReader(cilinPath); 
			BufferedReader buff = new BufferedReader(file); 
			
			String line; 
			for (; (line = buff.readLine()) != null; ) {
				int pos = line.indexOf(" "); 
				String strCode = line.substring(0, pos); 
				String strTerms = line.substring(pos + 1); 

				for (StringTokenizer st = new StringTokenizer(strTerms, " "); st.hasMoreTokens(); ) {
					String strTerm = st.nextToken(); 
					
					if (strTerm.length() > this.maxWordLength) {
						this.maxWordLength = strTerm.length(); 
					}
					
					if (this.word2Code.containsKey(strTerm)) {
						List<String> tmpValue = this.word2Code.get(strTerm); 
						tmpValue.add(strCode); 
					} else {
						List<String> tmpValue = new ArrayList<String>();
						tmpValue.add(strCode); 
						this.word2Code.put(strTerm, tmpValue); 
					}
					
					//System.out.println(strTerm + ": " + this.word2Code.get(strTerm)); 
				}	
			}
			
			file.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printDict() {
		for (int i = 0; i < this.word2Code.size(); i++) {
			String strTerm = (String) this.word2Code.keySet().toArray()[i]; 
			System.out.print(strTerm + ": "); 
			
			List<String> Codes = this.word2Code.get(strTerm); 
			for (int j = 0; j < Codes.size(); j++) {
				System.out.print(Codes.get(j) + " "); 
			}
			System.out.println(); 	
		}
	}
	
	public List<String> getCodes(String strTerm) {
		return this.word2Code.get(strTerm); 
	}
	
	public boolean containsKey(String strTerm) {
		return this.word2Code.containsKey(strTerm); 
	}
	
	public static void main(String[] args) {
		Dictionary dict = new Dictionary(); 
		
		String str = "�?"; 
		System.out.println(str); 
		if (dict.containsKey(str)) {
			List<String> codes = dict.getCodes(str); 
			for (int i = 0; i < codes.size(); i++) {
				System.out.println(codes.get(i)); 
			}
		}
		
		str = "�?"; 
		System.out.println(str); 
		if (dict.containsKey(str)) {
			List<String> codes = dict.getCodes(str); 
			for (int i = 0; i < codes.size(); i++) {
				System.out.println(codes.get(i)); 
			}
		}
		
		str = "�?"; 
		System.out.println(str); 
		if (dict.containsKey(str)) {
			List<String> codes = dict.getCodes(str); 
			for (int i = 0; i < codes.size(); i++) {
				System.out.println(codes.get(i)); 
			}
		}
		
		//dict.printDict(); 
		
		System.out.println("The max length: " + dict.getMaxWordLength()); 
	}
}
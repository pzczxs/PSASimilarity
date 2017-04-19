package cn.edu.bjut; 

import java.util.*;

import cn.edu.bjut.Dictionary;
import cn.edu.bjut.Segmentation;

import java.lang.Math; 

/*
 * 参�?�文献：
 * [1]	徐硕, 朱礼�?, 乔晓�?, 薛春�?, 2010. 基于双序列比对的中文术语语义相似度计算的新方�?. 
 * 情报学报, Vol. 29, No. 4, pp. 701-708.
 * [2] Shuo Xu, Lijun Zhu, Xiaodong Qiao, and Chunxiang Xue, 2009. A Novel Approach 
 * for Measuring Chinese Terms Semantic Similarity based on Pairwise Sequence Alignment. 
 * Proceedings of the 5th International Conference on Semantics, Knowledge and Grid (SKG), 
 * pp. 92-98, Zhuhai, China.
 * */

public class Similarity {
	private Dictionary dict = null; 
	private Segmentation seg = null; 
	
	public Similarity() {
		this.dict = new Dictionary(); 
		this.seg = new Segmentation(this.dict); 
	}
	
	// 两个原子概念编码间的相似度计�?
	// 假设这两个编码满足格式要�?
	private double PrimitiveCodeSimilary(String strCode1, String strCode2){
		double dSpd = 0.0; // Superposed degree, 重合�?
		strCode1 = strCode1.trim();
		strCode2 = strCode2.trim(); 
		
		// 大类
		if(strCode1.substring(0, 1).equals(strCode2.substring(0, 1))){
			dSpd++; 
		}
		// 中类
		if(strCode1.substring(1, 2).equals(strCode2.substring(1, 2))){
			dSpd++; 
		}
		// 小类
		if(strCode1.substring(2, 4).equals(strCode2.substring(2, 4))){
			dSpd++; 
		}
		// 词群
		if(strCode1.substring(4, 5).equals(strCode2.substring(4, 5))){
			dSpd++; 
		}
		// 原子词群
		if(strCode1.substring(5, 7).equals(strCode2.substring(5, 7))){
			dSpd++; 
		}
		
		return (dSpd / 5.0); 
	}
	
	// 两个原子概念间的相似度计�?
	// 假设已有的概念空间中存在这两个原子概�?
	private double PrimitiveConceptSimilary(String strConcept1, String strConcept2) {
		List<String> Codes1, Codes2; 
		strConcept1 = strConcept1.trim(); 
		strConcept2 = strConcept2.trim(); 
		
		if (this.dict.containsKey(strConcept1) && this.dict.containsKey(strConcept2)) {
			Codes1 = this.dict.getCodes(strConcept1); 
			Codes2 = this.dict.getCodes(strConcept2); 
		} else if (strConcept1.equals(strConcept2)) {
			return 1.0; 			
		} else {
			return 0.0; 
		}
		
		double dSim = 0.0; 
		for (int i = 0; i < Codes1.size(); i++) {
			for (int j = 0; j < Codes2.size(); j++) {
				double tmp = PrimitiveCodeSimilary(Codes1.get(i), Codes2.get(j)); 
				
				if (tmp > dSim) {
					dSim = tmp; 
				}
			}
		}
		
		return dSim; 
	}
	
	public double TermSimilary(String strTerm1, String strTerm2) {
		ArrayList<String> Term1 = this.seg.BMM(strTerm1.trim()); 
		ArrayList<String> Term2 = this.seg.BMM(strTerm2.trim()); 
		double spacePenalty = -0.05; 
		
		// 初始�?
		double[][] scoreMatrix = new double [Term1.size() + 1][Term2.size() + 1]; 
		scoreMatrix[Term1.size()][Term2.size()] = 0.0; 
		for (int i = Term1.size() - 1; i >= 0; i--) {
			scoreMatrix[i][Term2.size()] = spacePenalty * (Term1.size() - i); 
		}
		for (int j = Term2.size() - 1; j >= 0; j--) {
			scoreMatrix[Term1.size()][j] = spacePenalty * (Term2.size() - j); 
		}
		
		// 填充打分矩阵
		double[][] simMatrix = new double [Term1.size()][Term2.size()]; 
		for (int i = Term1.size() - 1; i >= 0; i--) {
			for (int j = Term2.size() - 1; j >= 0; j--) {
				simMatrix[i][j] = PrimitiveConceptSimilary(Term1.get(i), Term2.get(j)); 
				
				double dChoice1 = scoreMatrix[i + 1][j + 1] + simMatrix[i][j]; // 对角�?
				double dChoice2 = scoreMatrix[i][j + 1] + spacePenalty; // 左方
				double dChoice3 = scoreMatrix[i + 1][j] + spacePenalty; // 上方
				
				// 取三个元素中的最大�??
				scoreMatrix[i][j] = Math.max(dChoice1, dChoice2); 
				scoreMatrix[i][j] = Math.max(scoreMatrix[i][j], dChoice3); 
			}
		}
		
		/*
		System.out.println("rows: " + (Term1.size() + 1) + "\ncols: " + (Term2.size() + 1)); 
		System.out.println("scoreMatrix = "); 
		for (int i = 0; i <= Term1.size(); i++) {
			for (int j = 0; j <= Term2.size(); j++) {
				System.out.print(scoreMatrix[i][j] + " "); 
			}
			System.out.println(); 
		}
		*/
		
		// 回溯，按照比对结果计算相似度
		double dPart1 = 0.0, dPart21 = 0.0, dPart22 = 0.0; 
		for (int i = 0, j = 0; i < Term1.size() && j < Term2.size(); ) {
			if (scoreMatrix[i][j] == (scoreMatrix[i + 1][j + 1] + simMatrix[i][j])) { // 对角�?
				dPart1 += simMatrix[i][j];
				dPart21 += simMatrix[i][j] * (i + 1);
				dPart22 += simMatrix[i][j] * (j + 1); 
				
				i++; 
				j++; 
			} else if (scoreMatrix[i][j] == (scoreMatrix[i][j + 1] + spacePenalty)) { // 左方
				j++; 
			} else { // 上方
				i++; 
			}
		}
		
		double termSimilary = 0.3 * dPart1 * (1.0/Term1.size() + 1.0/Term2.size()); 
		int denominator = 0; 
		for (int i = 0; i < Term1.size(); i++) {
			denominator += i + 1; 
		}
		dPart21 /= denominator; 
		
		denominator = 0; 
		for (int j = 0; j < Term2.size(); j++) {
			denominator += j + 1;  
		}
		dPart22 /= denominator;
		
		if (Term1.size() > Term2.size()) {
			termSimilary += 0.2 * (Term2.size() / Term1.size()) * (dPart21 + dPart22); 
		} else {
			termSimilary += 0.2 * (Term1.size() / Term2.size()) * (dPart21 + dPart22); 
		}
		
		return termSimilary; 
	}
	
	public static void main(String[] args) {
		Similarity sim = new Similarity(); 
		
		String strTerm1 = "智能可变气门正时系统"; 
		String strTerm2 = "可变气门正时调控系统"; 
		System.out.println("Sim(\"" + strTerm1 + "\", \"" + strTerm2 + "\") = " 
				+ sim.TermSimilary(strTerm1, strTerm2)); 
		
		strTerm1 = "燃气汽车"; 
		strTerm2 = "汽车燃气"; 
		System.out.println("Sim(\"" + strTerm1 + "\", \"" + strTerm2 + "\") = " 
				+ sim.TermSimilary(strTerm1, strTerm2));
		
		strTerm1 = "�?"; 
		strTerm2 = "汽车"; 
		System.out.println("Sim(\"" + strTerm1 + "\", \"" + strTerm2 + "\") = " 
				+ sim.TermSimilary(strTerm1, strTerm2));
		
		strTerm1 = "�?"; 
		strTerm2 = "�?"; 
		System.out.println("Sim(\"" + strTerm1 + "\", \"" + strTerm2 + "\") = " 
				+ sim.TermSimilary(strTerm1, strTerm2));
		
		strTerm1 = "�?"; 
		strTerm2 = "�?"; 
		System.out.println("Sim(\"" + strTerm1 + "\", \"" + strTerm2 + "\") = " 
				+ sim.TermSimilary(strTerm1, strTerm2));
	}
}


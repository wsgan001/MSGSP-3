package sequenceMining;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.sun.javafx.scene.control.skin.VirtualFlow.ArrayLinkedList;
import com.sun.org.apache.xml.internal.utils.SuballocatedByteVector;

/**
 * @author Aditi Mallavarapu
 * @date 07/06/2019
 *
 */
public class MSGSP {
	private ArrayList<ArrayList<ArrayList<String>>> allTransaction;
	private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> candidateList;
	private ArrayList<ArrayList<ArrayList<ArrayList<String>>>> frequentList;
	private LinkedHashMap<String, Double> minSupport ;
	private LinkedHashMap<ArrayList<String>, Double> support;
	private ArrayList<ArrayList<String>> omitTogether;
		
	public ArrayList<ArrayList<String>> getOmitTogether() {
		return omitTogether;
	}

	public void setOmitTogether() {
 		String[] biome = {"Desert","Jungle","Plains","Wetlands"};
		for(int index=0;index<biome.length;index++) {
			ArrayList<String> omitPair = new ArrayList<String>();
			omitPair.add("MP".concat(":"+biome[index]));
			omitPair.add("P".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("D".concat(":"+biome[index]));
			omitPair.add("MD".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MWI".concat(":"+biome[index]));
			omitPair.add("WI".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MWO".concat(":"+biome[index]));
			omitPair.add("WO".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MUI".concat(":"+biome[index]));
			omitPair.add("UI".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MUD".concat(":"+biome[index]));
			omitPair.add("UD".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MIM");
			omitPair.add("IM");
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MDM");
			omitPair.add("DM");
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MDI".concat(":"+biome[index]));
			omitPair.add("DI".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
			omitPair = new ArrayList<String>();
			omitPair.add("MII".concat(":"+biome[index]));
			omitPair.add("II".concat(":"+biome[index]));
			this.omitTogether.add(omitPair);
		}
	}

	public ArrayList<ArrayList<ArrayList<String>>> getTransaction() {
		return allTransaction;
	}

	public ArrayList<ArrayList<ArrayList<ArrayList<String>>>> getFrequentList() {
		return frequentList;
	}
	
	public LinkedHashMap<String, Double> getMinSupport() {
		return minSupport;
	}
	public LinkedHashMap<ArrayList<String>, Double> getSupport() {
		return support;
	}
	public void setSupport(LinkedHashMap<ArrayList<String>, Double> support) {
		this.support = support;
	}
	
	public void init(){
		this.omitTogether = new ArrayList<ArrayList<String>>();
		this.allTransaction = new ArrayList<ArrayList<ArrayList<String>>>();
		this.frequentList = new ArrayList<ArrayList<ArrayList<ArrayList<String>>>>();
		this.candidateList = new ArrayList<ArrayList<ArrayList<ArrayList<String>>>>();
		this.support = new LinkedHashMap<ArrayList<String>, Double>();
		this.setOmitTogether();
		this.minSupport = new LinkedHashMap<String, Double>();
		this.setMinSupport();
		this.minSupport = this.sortHashMapByValues();  // sorting MIS hash in ascending
	}
	
	/***
	 *reads the file with minsupports and populates into hashmap 
	 */
	public void setMinSupport(){
		//define multiple minsupport
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(".\\SequenceMining Files\\minsup.txt"));
			String next = new String();
			while((next=br.readLine())!=null) {
				String split[]= next.split("=");
				this.minSupport.put(split[0], Double.parseDouble(split[1]));
			}
			br.close();
			//sort lexicographically
			List<String> mapKeys = new ArrayList<String>(this.getMinSupport().keySet());
			Collections.sort(mapKeys);
			LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
			Iterator<String> keyIt = mapKeys.iterator();
			while (keyIt.hasNext()) {
				String key = keyIt.next();	
				sortedMap.put(key,this.minSupport.get(key));
			}
			this.minSupport.clear();
			this.minSupport = sortedMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	/***
	 * @param grade
	 * 1) import transactions to list, calculate supports for items
	 * and calculate support and call the algorithm
	 * 	 */
	
	public void populateTrasactions(String grade) {
		try {
			BufferedReader br1 = new BufferedReader(
					new FileReader(".\\"+grade+".txt"));
			String pathFilename = ".\\EDM\\"+grade+"\\events\\";
			String nextFileLine = new String();
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				String path = pathFilename+nextFile.split(".csv")[0]+"seq20.txt";
				this.init();
				BufferedReader br = new BufferedReader(
					new FileReader(path));
				String next = new String();
				while((next=br.readLine())!=null) {
					ArrayList<ArrayList<String>> transaction = new ArrayList<ArrayList<String>>();
					String split[] = next.split("->");
					for(int i = 0;i<split.length;i++) {
						ArrayList<String> itemset = new ArrayList<String>();
						String item[]  = split[i].split(",");
						for(int j=0;j<item.length;j++) {
							if(!item[j].isEmpty()) {
								itemset.add(item[j]);
							}
						}	
						if(itemset.size()>1) {
							transaction.add(itemset);
						}
					}
					this.allTransaction.add(transaction);	
				}
				//all transactions need to be sorted internally
				ArrayList<ArrayList<ArrayList<String>>> sortedTransactions = 
						new ArrayList<ArrayList<ArrayList<String>>>();
				for(ArrayList<ArrayList<String>> transaction: this.allTransaction) {
					ArrayList<ArrayList<String>> sortedItemsets = 
							new ArrayList<ArrayList<String>>();
					for(ArrayList<String> itemset:transaction) {
						Collections.sort(itemset);
						sortedItemsets.add(itemset);
					}
					sortedTransactions.add(sortedItemsets);
				}
				br.close();
				this.allTransaction.clear();
				this.allTransaction = sortedTransactions;
				this.calcSupport();
				this.algorithm(grade,nextFile);
			}
		br1.close();  //for all files do this
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * 
	 * @param sub_transactions
	 * @return unique elements in each transaction 
	 * required as each item (irrespective of multiple occurence) is counted once
	 */
	public Set<String> createUniqueSet(ArrayList<ArrayList<String>> sub_transactions) {
		Iterator<ArrayList<String>> middle = sub_transactions.iterator();
		Set<String> uniqueSubSet = new HashSet<String>();
		while(middle.hasNext()) {
			ArrayList<String> subsub_transactions = middle.next();
			Iterator<String> inner = subsub_transactions.iterator();
			while(inner.hasNext()) {
				uniqueSubSet.add(inner.next());
			}
		}
		return uniqueSubSet;
	}
	/***
	 * Reads the transaction list, and 
	 * calculates support of each single items into the hashmap support
	 */
	public void calcSupport() {
		double support = 0.0;
		Iterator<Entry<String, Double>> iterator = this.getMinSupport().entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pair = (Map.Entry) iterator.next();
			ArrayList<String> temp = new ArrayList<String>();
			temp.add((String) pair.getKey());
			this.support.put(temp, 0.0);
		}

		ArrayList<ArrayList<ArrayList<String>>> tempTransactions = this.getTransaction();
		Iterator<ArrayList<ArrayList<String>>> outer = tempTransactions.iterator();
		while (outer.hasNext()) {
			ArrayList<ArrayList<String>> sub_transactions = outer.next();
			// convert sub-transaction to a set remove duplicates as we count each item once in a sequence
			   Set<String> uniqueSubSet = this.createUniqueSet(sub_transactions);  
				Iterator<String> inner = uniqueSubSet.iterator();
				while (inner.hasNext()) {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(inner.next());
					this.support.computeIfPresent(temp, (k, v) -> v + 1);
				}
			}
		
		Iterator iteratorSupport = this.support.entrySet().iterator();
		while (iteratorSupport.hasNext()) {
			Map.Entry pair = (Map.Entry) iteratorSupport.next();
			ArrayList<String> key = (ArrayList<String>) pair.getKey();
			Double value = (Double) pair.getValue();
			this.support.put(key, value / this.allTransaction.size());
			//System.out.println(value/this.allTransaction.size()+ " "+ key+ " "+"count:"+value);
		}
	}
	/***
	 * 
	 * @return sorted hashmap of MIS values 
	 * called from the constructor
	 */
	public LinkedHashMap<String, Double> sortHashMapByValues() {
		List<Double> mapValues = new ArrayList<Double>(this.getMinSupport().values());
		List<String> mapKeys = new ArrayList<String>(this.getMinSupport().keySet());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);

		LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();

		Iterator<Double> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Double val = valueIt.next();
			Iterator<String> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) {
				String key = keyIt.next();
				Double comp1 = this.minSupport.get(key);
				Double comp2 = val;

				if (comp1.equals(comp2)) {
					keyIt.remove();
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}
	
	/***
	 * 
	 * @param LSatisfySupport
	 * @return level 1 frequent itemset
	 */
	public ArrayList<ArrayList<ArrayList<String>>> 
	setF1(ArrayList<String> LSatisfySupport) {
		/* function to generate level 1 frequent itemset */

		// array List to be returned
		ArrayList<ArrayList<ArrayList<String>>> F1 = 
				new ArrayList<ArrayList<ArrayList<String>>>();
		// support counts (computed already do not divide by n again)
		LinkedHashMap<ArrayList<String>, Double> localSupport = this.getSupport();
		// MIS values for each element in itemset
		LinkedHashMap<String, Double> localMIS = this.getMinSupport();
		Iterator<String> iteratorL = LSatisfySupport.iterator();
		// compare the MIS(i) to sup(i)
		while (iteratorL.hasNext()) {
			String keyL = iteratorL.next();    // the item
			double keyMIS = localMIS.get(keyL);  // the MIS of the key 
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(keyL);
			double sup = localSupport.get(temp);  // the supportcount of the key
			if (sup >= keyMIS) {
				ArrayList<ArrayList<String>> itemset = new ArrayList<ArrayList<String>>();
				ArrayList<String> item = new ArrayList<String>();
				item.add(keyL);
				itemset.add(item);
				F1.add(itemset);
			}
		}
		System.out.println("F1: " + F1);
		return F1;
	}

	/***
	 * 
	 * @return List of Single items that satisfy MIS
	 */
	public ArrayList<String> init_pass() {
		//sort min sup hash by minimum support already done on initialization 
		//of object transfer code here if needed
		// List of items satisfying respective minsup to be returned
		ArrayList<String> LSatisySupport = new ArrayList<String>();
		//compare sorted MIS and the supports and include all those that are >= MIS(i)
		Iterator<Entry<String, Double>> iteratorMIS = 
				this.getMinSupport().entrySet().iterator();
		Iterator<Entry<ArrayList<String>, Double>> iteratorSupports =
				this.getSupport().entrySet().iterator();
		double mis_min = 0.0;  // stores the MIS(i) to compare to subsequent j
		
		while(iteratorMIS.hasNext()) {
			Map.Entry currentMISpair = (Map.Entry) iteratorMIS.next();
			iteratorSupports.next();  // progress MIS hash and Supports hash together
			Double currentMISValue = (Double)currentMISpair.getValue();
			mis_min = currentMISValue;
			ArrayList<String> temp = new ArrayList<String>();
			temp.add((String) currentMISpair.getKey());
			double currentSupport = this.getSupport().get(temp);
			if(currentSupport >= currentMISValue) {
				LSatisySupport.add((String)currentMISpair.getKey());
				break;
			}
		}		
		
		while(iteratorSupports.hasNext()) {
			Map.Entry currentSupportpair = (Map.Entry) iteratorSupports.next();
			ArrayList<String> currentSupportKey = (ArrayList<String>) currentSupportpair.getKey();
			double currentSupportValue = (Double) currentSupportpair.getValue();
			if (currentSupportValue >= mis_min) {
				// support is greater than minimum support include it
				LSatisySupport.add(currentSupportKey.get(0));
			}
		}
		System.out.println(LSatisySupport);
		return LSatisySupport;
	}
	
/***
 * 
 * @param LSatisfySupport
 * @return level2 candidate list
 */
	public ArrayList<ArrayList<ArrayList<String>>> genLevel2(ArrayList<String> LSatisfySupport) {
		//list to return 
		System.out.println("In C2");
		ArrayList<ArrayList<ArrayList<String>>> c2 = new ArrayList<ArrayList<ArrayList<String>>>();
		LinkedHashMap<ArrayList<String>, Double> localSupport = this.getSupport();
		LinkedHashMap<String, Double> localMIS = this.getMinSupport();
		Iterator<String> iteratorL = LSatisfySupport.iterator();
		for (int i = 0; i < LSatisfySupport.size(); i++) {
			String key = LSatisfySupport.get(i);
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(key);
			ArrayList<ArrayList<String>> itemset = new ArrayList<ArrayList<String>>();
			ArrayList<String> itemCandidate = new ArrayList<String>();
			//{{x}{x}}  // do we need to include {{y}{y}} as well?
			itemCandidate.add(key);
			itemset.add(itemCandidate);
			itemCandidate = new ArrayList<String>();
			itemCandidate.add(key);
			itemset.add(itemCandidate);
			c2.add(itemset);
			if (localSupport.get(temp) >= localMIS.get(key)) {
				for (int j = i + 1; j < LSatisfySupport.size(); j++) {
					String sub_key = LSatisfySupport.get(j);
					//double diff = Math.abs(localSupport.get(sub_key) - localSupport.get(key));
					ArrayList<String> tempSatisfy = new ArrayList<String>();
					tempSatisfy.add(sub_key);
					if (localSupport.get(tempSatisfy) >= localMIS.get(key)){
						// && diff <= local_sdc) {
						//modify with join options 
						itemset = new ArrayList<ArrayList<String>>();
						itemCandidate = new ArrayList<String>();
						//adds {{x}{y}}
						itemCandidate.add(key);
						itemset.add(itemCandidate);
						itemCandidate = new ArrayList<String>();
						itemCandidate.add(sub_key);
						itemset.add(itemCandidate);
						c2.add(itemset);
						//adds {{y}{x}}
						itemset = new ArrayList<ArrayList<String>>();
						itemCandidate = new ArrayList<String>();
						itemCandidate.add(sub_key);
						itemset.add(itemCandidate);
						itemCandidate = new ArrayList<String>();
						itemCandidate.add(key);
						itemset.add(itemCandidate);
						c2.add(itemset);
						//adds {x,y}
						itemset = new ArrayList<ArrayList<String>>();
						itemCandidate = new ArrayList<String>();
						itemCandidate.add(key);
						itemCandidate.add(sub_key);
						itemset.add(itemCandidate);	
						c2.add(itemset);
					}
				}
			}
		}
		//prune: remove unacceptable or impossible sequences
		for(ArrayList<ArrayList<String>> itemsetc2: c2) {
			if(this.containsCandidate(itemsetc2,this.omitTogether)) {
				c2.remove(new ArrayList<ArrayList<String>>(itemsetc2));
			}
		}
		System.out.println("This is the candidate:"+c2);
		return c2;
	}
	/***
	 * the MS-GSP algorithm
	 */
	public void algorithm(String grade, String filename) {
		ArrayList<String> LSatisfySupport = this.init_pass();
		ArrayList<ArrayList<ArrayList<String>>> F1 = this.setF1(LSatisfySupport);
		//stores supports for candidates
		LinkedHashMap<ArrayList<ArrayList<String>>, Double> candidateSupport = 
				new LinkedHashMap<ArrayList<ArrayList<String>>, Double>();
		this.frequentList.add(F1);
		//current is used to know F(k-1) for Ck
		ArrayList<ArrayList<ArrayList<String>>> currentFrequentList = 
				new ArrayList<ArrayList<ArrayList<String>>>();
		currentFrequentList = F1;
		int iteration = 2; // count the number of Frequent lists generated
		while (iteration >= 2) {
			if (currentFrequentList.isEmpty()) {
				break;
			} else {
				// present is used to know ck for fk
				ArrayList<ArrayList<ArrayList<String>>> presentCandidateList =
						new ArrayList<ArrayList<ArrayList<String>>>();
				if (iteration == 2) {
					/*
					 * Level2 generation function
					 */
					System.out.println("Now working on C2");
					ArrayList<ArrayList<ArrayList<String>>> c2 = this.genLevel2(LSatisfySupport);
					presentCandidateList = c2;
					System.out.println("C"+iteration+":"+c2);
					this.candidateList.add(c2);
					
				} 
				else {
					System.out.println("Now working on C"+iteration);
					ArrayList<ArrayList<ArrayList<String>>> ck = 
							this.MSCandidateGen(currentFrequentList);
					presentCandidateList = ck;
					this.candidateList.add(ck);
					System.out.println("C"+iteration+":"+ck);
				}
				//initialize counters for new candidates
				for (ArrayList<ArrayList<String>> candidate : presentCandidateList) {
					candidateSupport.put(
							(ArrayList<ArrayList<String>>)candidate, 0.0);
					}
				
				//get transactions and compute supports for candidates if found
				ArrayList<ArrayList<ArrayList<String>>> localTransactions = this.getTransaction();
				for (ArrayList<ArrayList<String>> transaction : localTransactions) {
					for (ArrayList<ArrayList<String>> candidate : presentCandidateList) {
							if(containsCandidate(transaction, candidate)) {
								candidateSupport.computeIfPresent(candidate, (k,v)->v+1);
							}
							//line 13 and 14 go here omitting for now for rules
						}
					}
				ArrayList<ArrayList<ArrayList<String>>> subFrequentList =  // this is Fk
						new ArrayList<ArrayList<ArrayList<String>>>();
				for (ArrayList<ArrayList<String>> candidate : presentCandidateList) {
					double candidateMIS = this.retMinMIS(candidate);
					if((candidateSupport.get(candidate)/this.allTransaction.size()) >= candidateMIS) {
						subFrequentList.add(candidate);
					}
				}
				
				System.out.println("F"+iteration+":"+subFrequentList);
				if(subFrequentList.size()>=1) {
					this.frequentList.add(subFrequentList);
				}
				currentFrequentList = subFrequentList; // marks f(k-1) for ck
				iteration++;
				//add the supports for future reference
				Iterator iteratorSupport = candidateSupport.entrySet().iterator();
				while (iteratorSupport.hasNext()) {
					Map.Entry pair = (Map.Entry) iteratorSupport.next();
					ArrayList<String> key =  (ArrayList<String>) pair.getKey();
					Double value = (Double) pair.getValue();
					this.support.put(key, value / this.allTransaction.size());
				//	System.out.println(value/this.allTransaction.size()+ " "+ key+"count:"+value);
				}
			}
		}
		this.writeFrequentList(grade, filename);
	}
	
	public void writeFrequentList(String grade, String filename) {
		//write into respective filenames
		try {
			String pathWrite = ".\\SequenceMining files\\"+grade+"\\"+filename.split(".csv")[0]+"sequences.txt";
			FileWriter fw = new FileWriter(pathWrite);
			BufferedWriter bw = new BufferedWriter(fw);
			Iterator iteratorF = this.frequentList.iterator();
			while(iteratorF.hasNext())   {
				ArrayList<ArrayList<ArrayList<String>>> itemsets = 
						(ArrayList<ArrayList<ArrayList<String>>>) iteratorF.next();
				Iterator iteratorItemSet = itemsets.iterator();
				while(iteratorItemSet.hasNext()) {
					ArrayList<String> key = (ArrayList<String>) iteratorItemSet.next();
					bw.write( key+"\n");
				}
			}
			bw.write("supports:\n");
			Iterator iteratorSupport = this.support.entrySet().iterator();
			while (iteratorSupport.hasNext()) {
				Map.Entry pair = (Map.Entry) iteratorSupport.next();
				ArrayList<String> key =  (ArrayList<String>) pair.getKey();
				Double value = (Double) pair.getValue();
				bw.write(key+":"+value+"\n");
			}
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * 
	 * @param candidate list
	 * @return the MIS of the list = min(MIS(i1), MIS(i2).....)
	 */
	public double retMinMIS(ArrayList<ArrayList<String>> candidate) {
		double min = Double.MAX_VALUE;
		Iterator iteratorCandidate = candidate.iterator();
		while(iteratorCandidate.hasNext()) {
			ArrayList<String> itemsetCandidate = (ArrayList<String>) iteratorCandidate.next();
			Iterator iteratorItemset = itemsetCandidate.iterator();
			while(iteratorItemset.hasNext()) {
				String current = (String) iteratorItemset.next();
				double currentMIS = this.retMIS(current);
				if(min > currentMIS ) {
					min = currentMIS;
				}
			}
		}
		return min;
	}
	/***
	 * 
	 * @param transaction
	 * @param candidate
	 * @return true or false if candidate is a found in transaction
	 */
	public boolean containsCandidate(ArrayList<ArrayList<String>> transaction, 
			ArrayList<ArrayList<String>> candidate) {
		/* function to check if a candidate is part of a given transaction */
		int index = 0;
		ArrayList<String> candidateElements = new ArrayList<String>();
		Iterator iteratorCandidate = candidate.iterator();
		while(iteratorCandidate.hasNext()) {
			ArrayList<String> innerSubc= (ArrayList<String>) iteratorCandidate.next();
			Iterator iteratorInnerSubc = innerSubc.iterator();
			while(iteratorInnerSubc.hasNext()) {
				candidateElements .add((String) iteratorInnerSubc.next());
			}
		}
		Iterator iteratorTransaction = transaction.iterator();
		ArrayList<ArrayList<String>> tempTransaction = new ArrayList<ArrayList<String>>();
		while(iteratorTransaction.hasNext()) { 
			ArrayList<String> itemsetTransaction = (ArrayList<String>) iteratorTransaction.next();
			Iterator iteratorItemset = itemsetTransaction.iterator();
			ArrayList<String> tempItemset = new ArrayList<String>();
			while(iteratorItemset.hasNext()) {
				String current = (String) iteratorItemset.next();
				if(candidateElements.contains(current)){
					tempItemset.add(current);
				}
			}
			if(tempItemset.size()>=1 && !tempTransaction.contains(tempItemset)) {
				tempTransaction.add(tempItemset);
			}
		}
		return (tempTransaction.equals(candidate)) ? true : false;
	}

	/***
	 * 
	 * @param currentFrequentList
	 * @return
	 */
	public ArrayList<ArrayList<ArrayList<String>>> 
			MSCandidateGen (ArrayList<ArrayList<ArrayList<String>>> currentFrequentList){
		System.out.println("Now in MSCandidate");
		ArrayList<ArrayList<ArrayList<String>>> ck =  new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<ArrayList<String>>> Fksorted = new ArrayList<ArrayList<ArrayList<String>>>(currentFrequentList);
		for (int j = 0; j < Fksorted.size(); j++) {
			for (int i = j + 1; i < Fksorted.size(); i++) {
				ArrayList<ArrayList<String>> s1 = Fksorted.get(j);
				ArrayList<ArrayList<String>> s2 = Fksorted.get(i);
				//System.out.println("s1"+s1);
				//System.out.println("s2"+s2);
			    //join
				if( this.retFirstItemLeast(s1)) {
					ArrayList<ArrayList<String>> noSecondTermS1 = this.removeSecondS1(s1);
					//System.out.println("no secondterm:" + noSecondTermS1);
					ArrayList<ArrayList<String>> noLastTermS2 = this.removeLastS2(s2);
					//System.out.println("no last term:"+ noLastTermS2);
					if(this.compareRemainingS1S2(noSecondTermS1,noLastTermS2) && 
							this.retMIS(this.retLastItemS2(s2)) >this.retMIS(this.retFirstItemS1(s1))) {
						//check if separate or merged
							if(s2.get(s2.size()-1).size() ==1) {
								//System.out.println("Join 1");
								//separate {x}{y}
								ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
								Iterator iteratorS1 = s1.iterator();
								while(iteratorS1.hasNext()) {
									newCandidate.add((ArrayList<String>) iteratorS1.next());
								}
								ArrayList<String> newSubCandidate = new ArrayList<String>();
								newSubCandidate.add(this.retLastItemS2(s2));
								newCandidate.add(newSubCandidate);
								//System.out.println("Adding to candidate:"+newCandidate);
								ck.add(newCandidate);
						
								if((this.retSize(s1) == 2 && this.retLength(s1) ==2) 
										&& (this.retLastItemS2(s2).compareTo(this.retLastItemS2(s1))==1)) {
									//special merge
									//System.out.println("Join 2");
									newCandidate = new ArrayList<ArrayList<String>>();
									for(int index =0;index<s1.size()-1;index++) {
										newCandidate.add(s1.get(index));
									}
									newSubCandidate = new ArrayList<String>();
									ArrayList<String> lastS1 = s1.get(s1.size()-1);
									for(int index =0;index<lastS1.size();index++) {
										newSubCandidate.add(lastS1.get(index));
									}
									newSubCandidate.add(this.retLastItemS2(s2));
									newCandidate.add(newSubCandidate);
									//System.out.println("Adding to candidate:"+newCandidate);
									ck.add(newCandidate);
								}
							}
							else if(((this.retSize(s1) ==1 && this.retLength(s1) ==2)  
									&& (this.retLastItemS2(s2).compareTo(this.retLastItemS2(s1))==-1)) || (this.retSize(s1)) >2) {
								//System.out.println("Join 3");
								ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
								for(int index =0;index<s1.size()-1;index++) {
									newCandidate.add(s1.get(index));
								}
								ArrayList<String> newSubCandidate = new ArrayList<String>();
								ArrayList<String> lastS1 = s1.get(s1.size()-1);
								for(int index =0;index<lastS1.size();index++) {
									newSubCandidate.add(lastS1.get(index));
								}
								newSubCandidate.add(this.retLastItemS2(s2));
								newCandidate.add(newSubCandidate);
								//System.out.println("Adding to candidate:"+newCandidate);
								ck.add(newCandidate);
							}
						}
					}
				else if(this.retLastItemLeast(s2) ) {
					ArrayList<ArrayList<String>> noFirstTermS1 = this.removeFirstS1(s1);
					ArrayList<ArrayList<String>> noSecondLastTermS2 = this.removeSecondLastS2(s2);
					if(this.compareRemainingS1S2(noFirstTermS1,noSecondLastTermS2) && 
						//check the second condition /**Checked with kush's file**/
						this.retMIS(this.retLastItemS2(s2)) > this.retMIS(this.retFirstItemS1(s1))) {
					//check if separate or merged
					 if(s1.get(0).size()==1) {
						//separate {x}{y}
						 	ArrayList<String> newSubCandidate = new ArrayList<String>();
						 	newSubCandidate.add(this.retFirstItemS1(s1));
							ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
							newCandidate.add(newSubCandidate);
							Iterator iteratorS2 = s2.iterator();
							while(iteratorS2.hasNext()) {
								newCandidate.add((ArrayList<String>) iteratorS2.next());
							}			
							//System.out.println("Adding to candidate:"+newCandidate);
							ck.add(newCandidate);
							
							//compare to returns 1 if a comes before b a.comparesTo("b")
							//check this second condition /**Checked with kush's file**/
							if((this.retSize(s2) == 2 && this.retLength(s2) ==2) 
									&& (this.retFirstItemS1(s2).compareTo(this.retFirstItemS1(s1))==-1)) {
								//special merge
								newSubCandidate = new ArrayList<String>();
								newCandidate = new ArrayList<ArrayList<String>>();
								newSubCandidate.add(this.retFirstItemS1(s1));
								ArrayList<String> firstS2 = s2.get(0);
								for(int index =1;index<firstS2.size();index++) {
									newSubCandidate.add(firstS2.get(index));
								}
								newCandidate.add(newSubCandidate);
								for(int index =1;index<s2.size();index++) {
										newCandidate.add(s2.get(index));
									}
								//System.out.println("Adding to candidate:"+newCandidate);
								ck.add(newCandidate);
							}
					    }
					 //check these conditions /**Checked with kush's file**/
						else if(((this.retSize(s2) ==1 && this.retLength(s2) ==2)  
								&& (this.retFirstItemS1(s2).compareTo(this.retFirstItemS1(s1))==-1)) || (this.retSize(s2)) >2) {
							ArrayList<String> newSubCandidate = new ArrayList<String>();
							ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
							newSubCandidate.add(this.retFirstItemS1(s1));
							ArrayList<String> firstS2 = s2.get(0);
							for(int index =1;index<firstS2.size();index++) {
								newSubCandidate.add(firstS2.get(index));
							}
							newCandidate.add(newSubCandidate);
							for(int index =1;index<s2.size();index++) {
									newCandidate.add(s2.get(index));
								}
							//System.out.println("Adding to candidate:"+newCandidate);
							ck.add(newCandidate);
							}
						}
					}
			else {
				//join from algorithm 2.13
				ArrayList<ArrayList<String>> noFirstTermS1 = this.removeFirstS1(s1);
				ArrayList<ArrayList<String>> noLastTermS2 = this.removeLastS2(s2);
				if(this.compareRemainingS1S2(noFirstTermS1,noLastTermS2) ) {
					if(s2.get(s2.size()-1).size() ==1) {  //separate
						ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
						Iterator iteratorS1 = s1.iterator();
						while(iteratorS1.hasNext()) {
							newCandidate.add((ArrayList<String>) iteratorS1.next());
						}
						ArrayList<String> newSubCandidate = new ArrayList<String>();
						newSubCandidate.add(this.retLastItemS2(s2));
						newCandidate.add(newSubCandidate);
						//System.out.println("Adding to candidate:"+newCandidate);
						ck.add(newCandidate);
						}
					else {
						ArrayList<ArrayList<String>> newCandidate = new ArrayList<ArrayList<String>>();
						for(int index =0;index<s1.size()-1;index++) {
								newCandidate.add(s1.get(index));
							}
						ArrayList<String> newSubCandidate = new ArrayList<String>();
						ArrayList<String> lastS1 = s1.get(s1.size()-1);
						for(int index =0;index<lastS1.size();index++) {
							newSubCandidate.add(lastS1.get(index));
							}
						newSubCandidate.add(this.retLastItemS2(s2));
						newCandidate.add(newSubCandidate);
						//System.out.println("Adding to candidate:"+newCandidate);
						ck.add(newCandidate);
						}
					}
				}
			}
		}
		//prune here
		ArrayList<ArrayList<ArrayList<String>>> ckCopy = 
				new ArrayList<ArrayList<ArrayList<String>>>(ck); 
		Iterator iteratorCk = ck.iterator();
		while(iteratorCk.hasNext()) {
			ArrayList<ArrayList<String>>current = (ArrayList<ArrayList<String>>) iteratorCk.next();
			if(!this.checkSubsequences(current ,	currentFrequentList)) {
				ckCopy.remove(current);
			}
		}
		ck.clear();
		ck = ckCopy;
		return ck;
	}
	/***
	 * 
	 * @param subc = a subset of Candiate ck
	 * @return generates subsequences for the pruning step
	 */
	public boolean checkSubsequences(
			ArrayList<ArrayList<String>> subc, 
			ArrayList<ArrayList<ArrayList<String>>> currentFrequentList){
		ArrayList<ArrayList<ArrayList<String>>> subsequences = new ArrayList<ArrayList<ArrayList<String>>>();
		//get the element and its position
		String[] mapIndex = new String[this.retLength(subc)];
		int index=0;
		Iterator iteratorSubc = subc.iterator();
		while(iteratorSubc.hasNext()) {
			ArrayList<String> innerSubc= (ArrayList<String>) iteratorSubc.next();
			Iterator iteratorInnerSubc = innerSubc.iterator();
			while(iteratorInnerSubc.hasNext()) {
				mapIndex[index] = (String) iteratorInnerSubc.next();
				index++;
			}
		}
		for(int i=0;i<this.retLength(subc);i++) {
			if(!currentFrequentList.contains(this.noNItemS(subc, mapIndex, i))){
				return false;
			}
		}
		return true;
	}
	/***
	 * 
	 * @param subc = subsequence of candidate Ck
	 * @param mapIndex = to know which item is at which position
	 * @param position = to keep track of which item is being dropped
	 * @return  subsequence with no item at the position location.
	 */
	public ArrayList<ArrayList<String>> noNItemS(
			ArrayList<ArrayList<String>> subc,
			String[] mapIndex, int position){
		ArrayList<ArrayList<String>> subsequence = new ArrayList<ArrayList<String>>();
		Iterator iteratorSubc = subc.iterator();
		//skipping the item at position
		for(int index=0;index<mapIndex.length;) {
			while(iteratorSubc.hasNext()) {
				ArrayList<String> innerSubsequence = new ArrayList<String>();
				ArrayList<String> innerSubc= (ArrayList<String>) iteratorSubc.next();
				Iterator<String> iteratorInnerSubc = innerSubc.iterator();
				while(iteratorInnerSubc.hasNext()) {
					if(position != index) {  // skipping position
						innerSubsequence.add(iteratorInnerSubc.next());
					}
					else {
						iteratorInnerSubc.next();//skipping
					}
					index++;
				}
				if(!innerSubsequence.isEmpty()) {
					subsequence.add(innerSubsequence);
				}
			}
		}
		return subsequence;
	}
	
	/***
	 * 
	 * @param s2= sequence
	 * @return if the last item is the least MIS
	 */
	public boolean retLastItemLeast(ArrayList<ArrayList<String>> s2) {
		boolean flag = false;
		double min = 1000.0;
		Iterator iteratorS2 = s2.iterator();
		while(iteratorS2.hasNext()) {
			ArrayList<String> itemsetS2 = (ArrayList<String>) iteratorS2.next();
			Iterator<String> iteratorItemsetS2 = itemsetS2.iterator();
			while(iteratorItemsetS2.hasNext()) {
				String key = iteratorItemsetS2.next();
				if(this.retMIS(key) < min ) {
					min = this.retMIS(key);
				}
			}
		}
		int count = 0;
		iteratorS2 = s2.iterator();
		while(iteratorS2.hasNext()) {
			ArrayList<String> itemsetS2 = (ArrayList<String>) iteratorS2.next();
			Iterator<String> iteratorItemsetS2 = itemsetS2.iterator();
			while(iteratorItemsetS2.hasNext()) {
				String key = iteratorItemsetS2.next();
				if(min == this.retMIS(key)) {
					count++;
				}
			}
		}
		if(min == this.retMIS(this.retLastItemS2(s2)) && count==1) {
			flag = true;
		}
		else {
			flag = false;
		}
		return flag;
	}
	/***
	 * 
	 * @param s1= sequence
	 * @return if the first item is the least MIS
	 */
	public boolean retFirstItemLeast(ArrayList<ArrayList<String>> s1) {
		boolean flag = false;
		double min = 1000.0;
		Iterator iteratorS1 = s1.iterator();
		while(iteratorS1.hasNext()) {
			ArrayList<String> itemsetS1 = (ArrayList<String>) iteratorS1.next();
			Iterator<String> iteratorItemsetS1 = itemsetS1.iterator();
			while(iteratorItemsetS1.hasNext()) {
				String key = iteratorItemsetS1.next();
				if(this.retMIS(key) < min ) {
					min = this.retMIS(key);
				}
			}
		}
		int count = 0;
		iteratorS1 = s1.iterator();
		while(iteratorS1.hasNext()) {
			ArrayList<String> itemsetS1 = (ArrayList<String>) iteratorS1.next();
			Iterator<String> iteratorItemsetS1 = itemsetS1.iterator();
			while(iteratorItemsetS1.hasNext()) {
				String key = iteratorItemsetS1.next();
				if(min == this.retMIS(key)) {
					count++;
				}
			}
		}
		if(min == this.retMIS(this.retFirstItemS1(s1)) && count == 1) {
			flag = true;
		}
		else {
			flag = false;
		}
		return flag;
	}
	/***
	 * 
	 * @param s2= sequence
	 * @return size of last itemset
	 */
	public int retLastItemsetS2Size(ArrayList<ArrayList<String>> s2) {
		ArrayList<String> lastItemset = s2.get(s2.size()-1);
		return lastItemset.size();
	}
	
	/***
	 * 
	 * @param s2 = sequence
	 * @return the last item of s2 of type string
	 */
	public String retLastItemS2(ArrayList<ArrayList<String>> s2) {
		String last = new String();
		ArrayList<String> lastItemset = s2.get(s2.size()-1);
		last = lastItemset.get(lastItemset.size()-1);
		return last;
	}
	/***
	 * 
	 * @param s1= sequence
	 * @return first item in the sequence
	 */
	public String retFirstItemS1(ArrayList<ArrayList<String>> s1) {
		String first = new String();
		Iterator iteratorS1 = s1.iterator();
		if(iteratorS1.hasNext()) {
			ArrayList<String> item = (ArrayList<String>)iteratorS1.next();
			if(item.size() >1) {
				first = item.get(0);
			}
		}
		return first;
	}
	/***
	 * 
	 * @param key= item of type String
	 * @return its MIS from the hash
	 */
	public double retMIS(String key) {
		LinkedHashMap<String, Double> localMIS = this.getMinSupport();
		double keyMIS = -100.0;
		if(!key.isEmpty()) {
			keyMIS = localMIS.get(key);
		}
		return keyMIS;
	}
	/***
	 * 	
	 * @param noSecondTermS1
	 * @param noLastTermS2
	 * @return compare them tell if same or not
	 */
	public boolean compareRemainingS1S2(ArrayList<ArrayList<String>> noSecondTermS1,
			ArrayList<ArrayList<String>> noLastTermS2) {
		boolean result = false;
		result = noLastTermS2.equals(noSecondTermS1);
		return result;
	}
	
	/***
	 * 
	 * @param s = sequence
	 * @returnsize of the sequence
	 */
	public int retSize(ArrayList<ArrayList<String>> s) {
		int size = 0;
		return s.size();
	}
	/***
	 * 
	 * @param s
	 * @return length of the sequence
	 */
	public int retLength(ArrayList<ArrayList<String>> s) {
		int length = 0;
		Iterator iteratorS = s.iterator();
		while(iteratorS.hasNext()) {
			ArrayList<String> itemset = (ArrayList<String>)iteratorS.next();
			Iterator iteratorItemset = itemset.iterator();
			while(iteratorItemset.hasNext()) {
				iteratorItemset.next();
				length++;
			}
		}
		return length;
	}
	/***
	 * 
	 * @param s2 second sequence
	 * @return s2-last item
	 */
	public ArrayList<ArrayList<String>> removeLastS2(ArrayList<ArrayList<String>> s2){
		ArrayList<ArrayList<String>> remainingS2 = new ArrayList<ArrayList<String>>();
		for(int index =0;index<s2.size()-1;index++) {
			remainingS2.add(s2.get(index));
		}
		ArrayList<String> lastItemset = s2.get(s2.size()-1);
		ArrayList<String> remainingS2Itemset = new ArrayList<String>();
		for(int index =0;index< lastItemset.size()-1;index++)
		{
			remainingS2Itemset.add(lastItemset.get(index));
		}
		if(!remainingS2Itemset.isEmpty()) {
			remainingS2.add(remainingS2Itemset);
		}
		return remainingS2;
	}
	/***
	 * 
	 * @param s1= sequence
	 * @return s1- second term
	 */
	public ArrayList<ArrayList<String>> removeSecondS1(ArrayList<ArrayList<String>> s1){
		ArrayList<ArrayList<String>> remainingS1 = new ArrayList<ArrayList<String>>();
		
		Iterator iteratorS1 = s1.iterator();
		int firstFlag = 0;
		while(iteratorS1.hasNext()) {  
			//used to store the items we want
			ArrayList<String> remainingS1Itemset = new ArrayList<String>();
			//used to check the items we read
			ArrayList<String> itemset = (ArrayList<String>)iteratorS1.next();
			Iterator iteratorItemset = itemset.iterator();
			if(itemset.size()>=2  && firstFlag ==0) {
				remainingS1Itemset.add((String)iteratorItemset.next()); // first item
				iteratorItemset.next();  // ignore the term
				while(iteratorItemset.hasNext()) {
					remainingS1Itemset.add((String)iteratorItemset.next());
				}
				firstFlag =1;
			}
			else if(itemset.size()<2 && firstFlag ==0) {
				remainingS1Itemset.add((String)iteratorItemset.next()); // first item
				if(iteratorS1.hasNext()) {
					itemset = (ArrayList<String>)iteratorS1.next(); 
					iteratorItemset = itemset.iterator();
					if(itemset.size()>=1) {
						iteratorItemset.next(); // ignore the term
						while(iteratorItemset.hasNext()) {
							remainingS1Itemset.add((String)iteratorItemset.next());
						}
					}
				}
				firstFlag =1;
			}
			else {
				while(iteratorItemset.hasNext()) {
					remainingS1Itemset.add((String)iteratorItemset.next());
				}
			}
			if(remainingS1Itemset.isEmpty()) {
				remainingS1.add(remainingS1Itemset);
			}
		}
		return remainingS1;
	}
	
	/***
	 * 
	 * @param s2 second sequence
	 * @return s2- second last item
	 */
	public ArrayList<ArrayList<String>> removeSecondLastS2(ArrayList<ArrayList<String>> s2){
		ArrayList<ArrayList<String>> remainingS2 = new ArrayList<ArrayList<String>>();
		if(s2.get(s2.size()-1).size() > 1) {
			for(int index =0;index<s2.size()-1;index++) {
				remainingS2.add(s2.get(index));
			}
			ArrayList<String> lastItemset = s2.get(s2.size()-1);
			ArrayList<String> remainingS2Itemset = new ArrayList<String>();
			for(int index =0;index< lastItemset.size()-2;index++)
			{
				remainingS2Itemset.add(lastItemset.get(index));
			}
			//skip 1 element here
			remainingS2Itemset.add(lastItemset.get(lastItemset.size()-1));
			remainingS2.add(remainingS2Itemset);
		}
		else {
			for(int index =0;index<s2.size()-2;index++) {
				remainingS2.add(s2.get(index));
			}
			ArrayList<String> secondlastItemset = s2.get(s2.size()-2);
			ArrayList<String> remainingS2Itemset = new ArrayList<String>();
			for(int index =0;index< secondlastItemset.size() -1;index++)
			{
				remainingS2Itemset.add(secondlastItemset.get(index));
			}
			//skipping 1
			remainingS2.add(remainingS2Itemset);
			ArrayList<String> lastItemset = s2.get(s2.size()-1);
			remainingS2.add(lastItemset);

		}
		return remainingS2;
	}
	/***
	 * 
	 * @param s1= sequence
	 * @return s1- first term
	 */
	public ArrayList<ArrayList<String>> removeFirstS1(ArrayList<ArrayList<String>> s1){
		ArrayList<ArrayList<String>> remainingS1 = new ArrayList<ArrayList<String>>();
		
		Iterator iteratorS1 = s1.iterator();
		int skip =1;
		while(iteratorS1.hasNext()) {
			ArrayList<String> remainingItemsetS1 = new ArrayList<String>();
			ArrayList<String> itemsetS1 = (ArrayList<String>) iteratorS1.next();
			Iterator iteratorItemsetS1 = itemsetS1.iterator();
			while(iteratorItemsetS1.hasNext()) {
				if(skip ==1) {
					iteratorItemsetS1.next();
					skip =0;
				}
				else {
					remainingItemsetS1.add((String) iteratorItemsetS1.next());
				}
			}
			if(!remainingItemsetS1.isEmpty()) {
				remainingS1.add(remainingItemsetS1);
			}
		}
		return remainingS1;
	}
		/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MSGSP m = new MSGSP();
		String grade_files[]= {"2-grade","3-grade","4-grade","5-grade","7-grade","8-grade"};
		for(String grade:grade_files) {
			m.populateTrasactions(grade);
		}
		
	}

}

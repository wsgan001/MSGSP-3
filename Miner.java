import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 
 */

/**
 * @author Aditi Mallavarapu
 * @date 06/11/2019
 *
 */
public class Miner {
	private LinkedHashMap<String, Integer> seqFreq;
	private LinkedHashMap<String, Double> frequent;
	private double minSupport ;
	
	Miner(){
		//this.seqFreq= new LinkedHashMap<String, Integer>();
		//this.frequent = new LinkedHashMap<String, Double>();
		this.minSupport = 0.1;
	}
		
	void addNewToHash(String seq) {
		if(!seq.isEmpty()) {
			this.seqFreq.put(seq, 0);
		}
	}
	
	void changeFreqInHash(String seq) {
		this.seqFreq.computeIfPresent(seq, (k, v) -> v + 1);
	}
	
	boolean checkHash(String seq) {
		if(this.seqFreq.containsKey(seq)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	void printHash(LinkedHashMap temp) {
		temp.entrySet().forEach(entry->{
		    System.out.println(((Entry<String, Integer>) entry).getKey() + " " + ((Entry<String, Integer>) entry).getValue());  
		 });
	}
	
	/***
	 *  @param grade
	 * 1) pick the fields to be included in the event sequence
	 * a) include all plantable biomes planting actions (cummulative not separated by levels), 
	 * b) death in each plantable biomes
	 * c) water inlet and outlets plantable biomes only, 
	 * d) users increase and decrease plantable only
	 * file is suffixed with "seq" 
	 */// mode and diversity not included 
	 
	void build_sequence(String grade) {
		try {
			BufferedReader br1 = new BufferedReader(
					new FileReader(".\\"+grade+".txt"));
			String nextFileLine = new String();
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				BufferedReader br = new BufferedReader(
					new FileReader(".\\EDM\\"+grade+"\\events\\"+nextFile));
				String path = ".\\EDM\\"+grade+"\\events\\"+nextFile.split(".csv")[0]+"seq.csv";
				FileWriter fw = new FileWriter(path);
				BufferedWriter bw = new BufferedWriter(fw);
				String next = new String();
				next = br.readLine();   // headings
				String seq = new String();
				int index[] = {21,22,23,24,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,60,61,62,63};
				System.out.println(nextFile);
				String heading[] = next.split(",");
				while((next=br.readLine())!=null) {
					seq="";
					String split[] = next.split(",");
					for(int i =0;i<index.length;i++) {
						if(index[i] < split.length) {
							if(!split[index[i]].isEmpty() ) {
								if(index[i] != 53) {
									seq=seq.concat(",");
									seq=seq.concat(split[index[i]].split(":")[0]);
									seq=seq.concat(":");
									String location = returnBiome(heading[index[i]]);
									seq = seq.concat(location);
								}
								else {
									seq=seq.concat(",");
									seq=seq.concat(split[index[i]].split(":")[0]);
								}
							}
						}
					}
					bw.write(split[1]+","+seq+"\n");  // removed a ","
				}
				br.close();
				bw.close();
			}
			br1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/***
	 * @param grade
	 * 1) combines every 20 seconds to be one sequence to a txt file
	 * 2) every 20 seconds to be included in one transaction (transaction is defined as set
	 * of events that can (have ability to) effect one another = t-20 to t)
	 * 
	 */
	void sec20Sequence(String grade) {
		try {
			BufferedReader br1 = new BufferedReader(
					new FileReader(".\\"+grade+".txt"));
			String nextFileLine = new String();
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				String path = ".\\EDM\\"+grade+"\\events\\"+nextFile.split(".csv")[0]+"seq.csv";
				BufferedReader br = new BufferedReader(
						new FileReader(path));
				String pathWrite = ".\\EDM\\"+grade+"\\events\\"+nextFile.split(".csv")[0]+"seq20.txt";
				FileWriter fw = new FileWriter(pathWrite);
				BufferedWriter bw = new BufferedWriter(fw);
				String next = new String();
				ArrayList<String> seq = new ArrayList<String>();
				while((next=br.readLine())!=null) {
					String temp = new String();
					String split[]=next.split(",");
					if(split.length > 1) {
						for(int u = 2;u <split.length;u++) {
							temp = temp.concat(split[u]);
							temp = temp.concat(",");
							
						}
						seq.add(temp);
					}
					else {
						seq.add("");
					}
				}
				this.calcSupportCounts(seq,grade);
				br.close();
				for(int i=0;i<21;i++) {
					String tempSeq = new String();
					for(int j=0;j<=i;j++) {
						tempSeq = tempSeq.concat(",");
						tempSeq = tempSeq.concat(seq.get(j));
						tempSeq = tempSeq.concat("->");
					}
					bw.write(tempSeq+"\n");
				}
				for(int i=21;i<seq.size();i++) {
					String tempSeq = new String();
					for(int j=i-20;j<=i;j++) {
						tempSeq = tempSeq.concat(",");
						tempSeq = tempSeq.concat(seq.get(j));
						tempSeq = tempSeq.concat("->");
					}	
					bw.write(tempSeq+"\n");
				}
				bw.close();
			}
		br1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	String returnBiome(String heading) {
		String biome[] = {"Desert","Plains","Jungle","Wetlands"};
		String add = new String();
		for (String string : biome) {
			if(heading.contains(string)) {
				add=string;
			}
		}
		return add;
	}
	
	void calcSupportCounts(ArrayList<String> temp, String grade){
		LinkedHashMap<String, Double> supportCounts = new LinkedHashMap<String, Double>();
		Iterator<String> iter = temp.iterator();
		while(iter.hasNext()) {
			String  key = iter.next();
			String split[] = key.split(",");
			for(String eachKey:split) {
				if(supportCounts.containsKey(eachKey)) {
					supportCounts.computeIfPresent(eachKey, (k, v) -> v + 1.0);
				}
				else {
					supportCounts.put(eachKey, 1.0);
				}
			}
		}
		
		try {
		File file1 = new File(".\\EDM\\"+grade+"\\events\\supports.csv");
		FileWriter fw1 = new FileWriter(file1,true);
		BufferedWriter bw1 = new BufferedWriter(fw1);
		ArrayList<String> headings = new ArrayList<String>();
		headings.add("MWI:Desert");
		headings.add("MWI:Plains");
		headings.add("MWI:Jungle");
		headings.add("MWI:Wetlands");
		headings.add("MWO:Desert");
		headings.add("MWO:Plains");
		headings.add("MWO:Jungle");
		headings.add("MWO:Wetlands");
		headings.add("MUI:Desert");
		headings.add("MUI:Plains");
		headings.add("MUI:Jungle");
		headings.add("MUI:Wetlands");
		headings.add("MDI:Desert");
		headings.add("MDI:Plains");
		headings.add("MDI:Jungle");
		headings.add("MDI:Wetlands");
		headings.add("WO:Desert");
		headings.add("WO:Plains");
		headings.add("WO:Jungle");
		headings.add("WO:Wetlands");
		headings.add("UD:Desert");
		headings.add("UD:Plains");
		headings.add("UD:Jungle");
		headings.add("UD:Wetlands");
		headings.add("MUD:Desert");
		headings.add("MUD:Plains");
		headings.add("MUD:Jungle");
		headings.add("MUD:Wetlands");
		headings.add("UI:Desert");
		headings.add("UI:Plains");
		headings.add("UI:Jungle");
		headings.add("UI:Wetlands");
		headings.add("MP:Desert");
		headings.add("MP:Plains");
		headings.add("MP:Jungle");
		headings.add("MP:Wetlands");
		headings.add("MD:Desert");
		headings.add("MD:Plains");
		headings.add("MD:Jungle");
		headings.add("MD:Wetlands");
		headings.add("MII:Desert");
		headings.add("MII:Plains");
		headings.add("MII:Jungle");
		headings.add("MII:Wetlands");
		headings.add("WI:Desert");
		headings.add("WI:Plains");
		headings.add("WI:Jungle");
		headings.add("WI:Wetlands");
		headings.add("DI:Desert");
		headings.add("DI:Plains");
		headings.add("DI:Jungle");
		headings.add("DI:Wetlands");
		headings.add("P:Desert");
		headings.add("P:Plains");
		headings.add("P:Jungle");
		headings.add("P:Wetlands");
		headings.add("D:Desert");
		headings.add("D:Plains");
		headings.add("D:Jungle");
		headings.add("D:Wetlands");
		headings.add("II:Desert");
		headings.add("II:Plains");
		headings.add("II:Jungle");
		headings.add("II:Wetlands");
		headings.add("MDI:Desert");
		headings.add("MDI:Plains");
		headings.add("MDI:Jungle");
		headings.add("MDI:Wetlands");
		headings.add("MIM");
		headings.add("MDM");
		headings.add("IM");
		headings.add("DM");
		bw1.write("\n");
		
		Iterator it = headings.iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			bw1.write(key+",");
			if(supportCounts.containsKey(key)) {
				bw1.write(supportCounts.get(key)+",");
				System.out.println(key+":"+supportCounts.get(key));
			}
			else {
				bw1.write(",");
			}
		}
		bw1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Miner m = new Miner();
		String grade_files[]= {"2-grade","3-grade","4-grade","5-grade","7-grade","8-grade"};
		for(String grade:grade_files) {
			m.build_sequence(grade);
			m.sec20Sequence(grade);
		}
		
	}

}

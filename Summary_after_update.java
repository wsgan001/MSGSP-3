import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.*;



public class Summary_after_update {
	Map<Integer, Integer> plantlevels = new HashMap<Integer, Integer>();
	void readPlantlevels() {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(".\\plantindex.txt"));
			String next = new String();
			next = br1.readLine();  //heading
			while((next = br1.readLine())!=null) {
				int id = Integer.parseInt(next.split(",")[0]);
				int level = Integer.parseInt(next.split(",")[1]);
				this.plantlevels.put(id, level);
			}
			br1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	String convert_to_minutes(int sec) {
		int hour = sec/3600;
		int min = (sec%3600)/60;
		int sec_rem = (sec%3600)%60;
		System.out.println(""+hour+"-"+min+"-"+sec_rem);
		return (""+hour+"-"+min+"-"+sec_rem);
	}
	
	void fill_gaps(String grade) {
		try {
			String readpath = "C:\\Users\\Aditi\\workspace\\ESSIL\\updated logs\\";
			BufferedReader br1 = new BufferedReader(new FileReader(".\\"+grade+".txt"));
			String path = ".\\EDM\\"+grade+"\\";
			String nextFileLine = new String();
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				System.out.println(nextFile);
				int visitors = Integer.parseInt(nextFileLine.split(",")[1]);
				File file = new File(path+nextFile);
				//System.out.println(nextFile);
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				BufferedReader br2 = new BufferedReader(new FileReader(readpath+nextFile));
				String next = br2.readLine(); //heading
				bw.write(next);
				next = br2.readLine();
				bw.write("\n"+next);
				String split_temp[] = next.split(",");
				int prev_time = Integer.parseInt(split_temp[0].split("-")[3].trim())*60*60+
						Integer.parseInt(split_temp[0].split("-")[4].trim())*60 
						+Integer.parseInt(split_temp[0].split("-")[5].trim());
				int next_time = 0;
				while((next=br2.readLine())!=null) {
					String split_next[] = next.split(",");
					next_time = Integer.parseInt(split_next[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split_next[0].split("-")[4].trim())*60 
							+Integer.parseInt(split_next[0].split("-")[5].trim());
					
					if(next_time-prev_time>1) {
						System.out.println("gap"+(next_time-prev_time));
						System.out.println(next_time);
						System.out.println(prev_time);
						for(int time=prev_time+1;time<next_time;time++) {
							String date= split_temp[0].split("-")[0]+"-"+split_temp[0].split("-")[1]+
									"-"+split_temp[0].split("-")[2];
							String prev_time_min = convert_to_minutes(time);
							String temp_write = "\n"+date+"-"+prev_time_min+",";
							for(int i=1;i<split_temp.length;i++) {
								temp_write+=split_temp[i]+",";
							}
							bw.write(temp_write);
						}
					}
					bw.write("\n"+next);
					split_temp = split_next;
					prev_time = next_time;
				}
				bw.close();
				br2.close();
			}
			br1.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	void mark_events(String grade) {
		try {
			String readpath = ".\\EDM\\"+grade+"\\sorted\\";
			BufferedReader br1 = new BufferedReader(
					new FileReader(".\\"+grade+".txt"));
			String path = ".\\EDM\\"+grade+"\\events\\";
			String nextFileLine = new String();
			File file1 = new File(path+"intensity.csv");
			FileWriter fw1 = new FileWriter(file1);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				int visitors = Integer.parseInt(nextFileLine.split(",")[1]);
				File file = new File(path+nextFile);
				System.out.println(nextFile);
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				BufferedReader br2 = new BufferedReader(new FileReader(readpath+nextFile));
				String next = br2.readLine(); //heading
				//add rain and clouds?
				bw.write("Timeremaining,seconds,session,game,TotalWater,"
				+ "Desertlv1Event,Desertlv2Event,Desertlv3Event,Desertlv4Event,"
				+ "Plainslv1Event,Plainslv2Event,Plainslv3Event,Plainslv4Event,"
				+ "Junglelv1Event,Junglelv2Event,Junglelv3Event,Junglelv4Event,"
				+ "Wetalndslv1Event,Wetlandslv2Event,Wetlandslv3Event,Wetlandslv4Event," 
				+"DesertPlantEvent,PlainsPlantEvent,JunglePlantEvent,WetlandsPlantEvent,"
				+"Desertdeadlv1Event,Desertdeadlv2Event,Desertdeadlv3Event,Desertdeadlv4Event,"
				+ "Plainsdeadlv1Event,Plainsdeadlv2Event,Plainsdeadlv3Event,Plainsdeadlv4Event,"
				+ "Jungledeadlv1Event,Jungledeadlv2Event,Jungledeadlv3Event,Jungledeadlv4Event,"
				+ "Wetalndsdeadlv1Event,Wetlandsdeadlv2Event,Wetlandsdeadlv3Event,Wetlandsdeadlv4Event," 
				+"DesertdeadPlantEvent,PlainsdeadPlantEvent,JungledeadPlantEvent,WetlandsdeadPlantEvent,"
				+"DesertWaterEvent,PlainsWaterEvent,JungleWaterEvent,WetlandsWaterEvent,"
				+"DesertIndexEvent,PlainsIndexEvent,JungleIndexEvent,WetlandsIndexEvent,ModeEvent,"
				+ "DesertUsersEvent,PlainsUsersEvent,JungleUserEvent,WetlandsUserEvent,MountainValleyUsersEvent,"
				+ "ReservoirUserEvent, DesertOverPlanting, PlainsOverPlanting, JungleOverPlanting, WetlandsOverPlanting");
				String prev = br2.readLine(); // first line
				ArrayList<Double> plants = new ArrayList<Double>();
				ArrayList<Double> deadPlants = new ArrayList<Double>();
				ArrayList<Double> water = new ArrayList<Double>();
				ArrayList<Double> diversity = new ArrayList<Double>();
				ArrayList<Double> mode = new ArrayList<Double>();
				ArrayList<Double> users = new ArrayList<Double>();
				String split_prev[] = prev.split(",");
				while((next=br2.readLine())!=null) {
					int thislength = 0;
					String split_next[] = next.split(",");
					bw.write("\n");
					for(int i=1;i<=5 ;i++) {
						bw.write(split_next[i]+",");
						thislength++;
					}	
					
					//plant events MP: Mass plantation, P: plantation
					for(int i=20;i<=39;i++) {
						double difference = 0.0;
						if((Double.parseDouble(split_prev[i].trim())- Double.parseDouble(split_prev[i+39].trim()))!=0.0) {
							difference =((Double.parseDouble(split_next[i].trim()) -
									Double.parseDouble(split_next[i+39].trim()))- 
									(Double.parseDouble(split_prev[i].trim())-
									Double.parseDouble(split_prev[i+39].trim()))) * 100 / 
									(Double.parseDouble(split_prev[i].trim())-Double.parseDouble(split_prev[i+39].trim()));
							if(i>=37 && i<=39 && difference >0) { // only cumulative plant numbers 
								plants.add(difference);
							}
						}
						//the calculated average for the percentage change 
						//was 18% so anything above that is considered overplanting
						if(difference > 18) {
							bw.write("MP:"+difference+",");
							thislength++;
						}
						else if(difference <= 18 && difference > 0.0) {
							bw.write("P:"+difference+",");
							thislength++;
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					//dead plant events MD: Mass death, D: Death
					for(int i = 59;i<=78;i++) {
						double difference = 0;
						if(Double.parseDouble(split_next[i-39].trim())!=0.0) {
								difference = (Double.parseDouble(split_next[i].trim())-Double.parseDouble(split_prev[i].trim())) 
										* 100 /Double.parseDouble(split_prev[i-39].trim());
								if(i>=76 && i<=79 && difference >0) {
									deadPlants.add(difference);
								}
						}
						if(difference > 12) {
							bw.write("MD:"+difference+",");
							thislength++;
						}
						else if(difference <= 12 && difference > 0.0) {
							bw.write("D:"+difference+",");
							thislength++;
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					//water MWI: Mass water intake, WI: Water Intake, 
					//MWO: Mass water Output, WO: Water Output
					for(int i=40;i<=43;i++) {
						double difference = 0;
						if(Double.parseDouble(split_prev[i].trim())!=0.0) {
							difference = (Double.parseDouble(split_next[i].trim()) - Double.parseDouble(split_prev[i].trim())) 
									* 100 /Double.parseDouble(split_prev[i].trim());
							water.add(Math.abs(difference));
						}
						if(difference > 0) {
							if(difference > 5) {
								bw.write("MWI:"+difference+",");
								thislength++;
							}
							else if(difference <= 5 && difference != 0.0) {
								bw.write("WI:"+difference+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else if(difference < 0) {
							if(difference < -5) {
								bw.write("MWO:"+Math.abs(difference)+",");
								thislength++;
							}
							else if(difference >= -5 && difference != 0.0) {
								bw.write("WO:"+Math.abs(difference)+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					//shannon diversity index 
					//MII: Mass increase in Index, II: Increase index
					//MDI: Mass decrease in index, DI: Decrease index
			    	for(int i=54;i<=57;i++) {
						double difference = 0;
						if(Double.parseDouble(split_prev[i].trim())!=0.0) {
							difference = (Double.parseDouble(split_next[i].trim()) - 
									Double.parseDouble(split_prev[i].trim())) 
									* 100 /Double.parseDouble(split_prev[i].trim());
							diversity.add(Math.abs(difference));
						}
						if(difference > 0.0) {
							if(difference > 1) {
								bw.write("MII:"+difference+",");
								thislength++;
							}
							else if(difference <= 1 && difference != 0.0) {
								bw.write("II:"+difference+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else if(difference < 0.0) {
							if(difference < -1) {
								bw.write("MDI:"+Math.abs(difference)+",");
								thislength++;
							}
							else if(difference >= -1 && difference != 0.0) {
								bw.write("DI:"+Math.abs(difference)+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					//mode 
			    	//MIM: Mass increase in mode, IM: Increase in mode
			    	//MDM: Mass decrease in mode, DM; decrease in mode
					double difference = 0;
					if(Double.parseDouble(split_prev[58].trim())!=0.0) {
						difference = (Double.parseDouble(split_next[58].trim()) - Double.parseDouble(split_prev[58].trim())) 
								* 100 /Double.parseDouble(split_prev[58].trim());
						mode.add(Math.abs(difference));
					}
					if(difference > 0.0) {
						if(difference > 3) {
							bw.write("MIM:"+difference+",");
							thislength++;
						}
						else if(difference <= 3 && difference != 0.0) {
							bw.write("IM:"+difference+",");
							thislength++;
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					else if(difference < 0.0) {
						if(difference < -3) {
							bw.write("MDM:"+Math.abs(difference)+",");
							thislength++;
						}
						else if(difference >= -3 && difference != 0.0) {
							bw.write("DM:"+Math.abs(difference)+",");
							thislength++;
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					else {
						bw.write(",");
						thislength++;
					}
					//users  
					//MIU: Mass increase in users, IU: Increase in Users
					//MDU: Mass decrease in users, DU: Decrease in Users
					for(int i=10;i<=15;i++) {
						difference = 0;
						System.out.println(i+","+split_prev[i].trim());
						if(Double.parseDouble(split_prev[i].trim())!=0.0) {
							difference = (Double.parseDouble(split_next[i].trim()) - Double.parseDouble(split_prev[i].trim())) 
									* 100 /Double.parseDouble(split_prev[i].trim());
							users.add(Math.abs(difference));
						}
						if(difference > 0) {
							if(difference > 16) {
								bw.write("MUI:"+difference+",");
								thislength++;
							}
							else if(difference <= 16 && difference != 0) {
								bw.write("UI:"+difference+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else if(difference < 0) {
							if(difference < -16) {
								bw.write("MUD:"+Math.abs(difference)+",");
								thislength++;
							}
							else if(difference >= -16 && difference != 0) {
								bw.write("UD:"+Math.abs(difference)+",");
								thislength++;
							}
							else {
								bw.write(",");
								thislength++;
							}
						}
						else {
							bw.write(",");
							thislength++;
						}
					}
					//overplanting :OP: Over Planting change in overplanting 
					//first term give change in water second term gives change in plants if it is 0 
					// need to calculate the water consumption 
					// water consumed = water available - current plant drink
					 //if positive then no event else event OP.
					for(int i=40;i<43;i++) {
						difference = 0.0;
						System.out.println(i+","+split_prev[i].trim());
						//(water available - plant drink)
						difference = (Double.parseDouble(split_next[i].trim())- Double.parseDouble(split_prev[i].trim())) - 
								(Double.parseDouble(split_next[i+4].trim())-Double.parseDouble(split_prev[i+4].trim())) ;
						if(difference >= 0.0) {
							bw.write(",");
							thislength++;
						}
						else if(difference < 0.0) {
							bw.write("OP:"+Math.abs(difference)+",");
							thislength++;
						}
					}
					
				
				split_prev = split_next;	
				}
				bw1.write("\n"+nextFile+","+mean(plants));	
				bw1.write(","+mean(deadPlants));	
				bw1.write(","+mean(water));
				bw1.write(","+mean(diversity));	
				bw1.write(","+mean(mode));	
				bw1.write(","+mean(users));	
				bw1.write(","+median(plants));	
				bw1.write(","+median(deadPlants));	
				bw1.write(","+median(water));
				bw1.write(","+median(diversity));	
				bw1.write(","+median(mode));	
				bw1.write(","+median(users));
				br2.close();
				bw.close();
			}
			br1.close();
			bw1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public double sum (ArrayList<Double> a){
        if (a.size() > 0) {
            double sum = 0;
 
            for (Double i : a) {
                sum += i;
            }
            return sum;
        }
        return 0;
    }
	
	public double mean (ArrayList<Double> a){
        double sum = sum(a);
        double mean = 0;
        mean = sum / (a.size() * 1.0);
        return mean;
    }
	
    public double median (ArrayList<Double> a){
        int middle = a.size()/2;
 
        if (a.size() % 2 == 1) {
            return a.get(middle);
        } else {
           return (a.get(middle-1) + a.get(middle)) / 2.0;
        }
    }
    
	void readfile(String grade) {
		try {
				BufferedReader br1 = new BufferedReader(new FileReader(".\\"+grade+".txt"));
				String path = ".\\EDM\\"+grade+"\\";
				String nextFileLine = new String();
				while((nextFileLine=br1.readLine())!=null) {
					String nextFile = nextFileLine.split(",")[0];
					System.out.println(nextFile);
					int visitors = Integer.parseInt(nextFileLine.split(",")[1]);
					File file = new File(path+"\\sorted\\"+nextFile);
					//System.out.println(nextFile);
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("Timestamp,Timeremaining,seconds,session,game,Total Water,"
				    		+ "Desert_WaterBins,Plains_WaterBins,Jungle_WaterBins,Wetlands_WaterBins,"
				    		+"DesertUsers,PlainsUsers,JungleUsers,WetlandsUsers,MountainValleyusers,ReservoirUsers,"
				    		+"ForesteryUsers,WaterManagementUsers,LogManagementUsers,BalanceMode,"
				    	    +"Dlv1,Dlv2,Dlv3,Dlv4,Plv1,Plv2,Plv3,Plv4,Jlv1,Jlv2,Jlv3,Jlv4,Wlv1,Wlv2,Wlv3,Wlv4,"
				    		+"DesertPlantsNos,PlainsPlantNos,JunglePlantNos,WetlandsPlantNos,"
				    		+ "DesertWaterAmount,PlainsWaterAmount,JungleWaterAmount,WetlandsWaterAmount,"		
				    		+ "DesertPlantsDrink,PlainsPlantDrink,JunglePlantDrink,WetlandsPlantDrink,"
				    		+ "Desertindex,Plainsindex,Jungleindex,Wetlandsindex,Mode,waterMode, "
				    		+"ShannonDesertIndex,ShannonPlainsIndex,ShannonJungleIndex,ShannonWetlandsindex, Mode,"
				    		+"Dlv1_dead,Dlv2_dead,Dlv3_dead,Dlv4_dead,Plv1_dead,Plv2_dead,Plv3_dead,Plv4_dead,Jlv1_dead,"
				    		+"Jlv2_dead,Jlv3_dead,Jlv4_dead,Wlv1_dead,Wlv2_dead,Wlv3_dead,Wlv4_dead,"
				    		+"DesertPlantsNos_dead,PlainsPlantNos_dead,JunglePlantNos_dead,WetlandsPlantNos_dead");
					//bw.write("\n"+nextFile+",");
					BufferedReader br2 = new BufferedReader(new FileReader(path+nextFile));
					String next = br2.readLine(); //heading
					String split_temp[] = nextFile.split("-");
					int start = Integer.parseInt(split_temp[3].trim())*60*60+
							Integer.parseInt(split_temp[4].trim())*60 
							+Integer.parseInt(split_temp[5].trim());	
					
					int prev = start;
					while((next=br2.readLine())!=null) {
						String split[] = next.split(",");
						String session = nextFile.split("-")[7];
						String timestamp = split[0];
						String timeremainin = split[1];
						String game = nextFile.split("-")[6];
						String tot_water = split[2];
						int present = Integer.parseInt(split[0].split("-")[3].trim())*60*60+
								Integer.parseInt(split[0].split("-")[4].trim())*60 
								+Integer.parseInt(split[0].split("-")[5].trim());
						if(present - start <= 720) {
							
						//find plants
						//81-90  alternate are creatures
						String plants[] = new String[4];
						int count=0;
						for(int i=81;i<=91;i+=2) {
							if(i!=83 && i!=89) {  //check for empty cells , and mountain and reservoir plants
							//	System.out.println("plants:"+i+","+ split[i]);
								if(!split[i].equals(" ")) {
									plants[count] = split[i];
								}
								else {
									plants[count]= " ";
								}
								count++;
							}
						}
						int users[] = new int[6];
						for(int i= 63;i<69;i++) {
							//users[i-63] = Double.parseDouble(split[i].trim())/visitors;
							users[i-63] = Integer.parseInt(split[i].trim());
							System.out.println("the users are:"+users[i-63]);
						}
						
						double forest = users[0]+users[2]+users[3]+users[5];
						double waterManage = 1-forest;
						double logs = waterManage- users[1] - users[4];
						//find drink amount per biome at that line, this should return arraylist of 4 doubles
						double biome_plants[] = this.drinkAmount(plants);
						int plantnos[] = this.calcPlantNos(plants);
						//find SDI per biome flag =1, find shannon index per biome flag =2
						double index[] = this.returnSDI(plants,1);	
						double shan[] = this.returnSDI(plants,2);
						//find modes for that line
						int mode= this.returnModes(index,0.6);  //SDI
						int smode = this.returnModes(shan, 1.5);
						//find water bins as is and as water available
						//69-80 alternate are flood water, 
						String water[] = new String[4];
						count=0;
						for(int i=69;i<80;i+=2) {
							if(i!=71 && i!=77) {
								water[count] = split[i];
								count++;
							}
						}
						double waterAmounts[] = this.returnwaterAvailable(water);
						int water_mode = this.returnwaterMode(biome_plants, waterAmounts);
						int balance_mode = this.returnBalanceMode(biome_plants, waterAmounts);
						bw.write("\n"+timestamp+","+timeremainin+","+(present-start)+","+session+","+game+","+tot_water+","+water[0]+","
								+water[1]+","+water[2]+","+water[3]+","+users[0]+","+users[2]+","+users[3]+","+users[5]+","+users[1]+","+
								users[4]+","+forest+","+waterManage+","+logs+","+balance_mode+","+split[27]+","+split[28]+","+split[29]+","+
								split[30]+","+split[31]+","+split[32]+","+split[33]+","+split[34]+","+split[35]+","+split[36]+","+split[37]+","+
								split[38]+","+split[39]+","+split[40]+","+split[41]+","+split[42]+","
								+plantnos[0]+","+plantnos[1]+","+plantnos[2]+","+plantnos[3]+
								","+waterAmounts[0]+","+waterAmounts[1]+","+waterAmounts[2]+","+
								waterAmounts[3]+","+biome_plants[0]+","+biome_plants[1]+","+biome_plants[2]+","
								+biome_plants[3]+","+index[0]+","+index[1]+","+index[2]+","+index[3]+","+mode+","+water_mode
								+","+shan[0]+","+shan[1]+","+shan[2]+","+shan[3]+","+smode+","+split[43]+","+split[44]+","+split[45]
								+","+split[46]+","+split[48]+","+split[49]+","+split[50]+","+split[51]+","+
								split[53]+","+split[54]+","+split[55]+","+split[56]+","+split[58]+","+split[59]+","+
								split[60]+","+split[61]+","+(Integer.parseInt(split[43].trim())+
								Integer.parseInt(split[44].trim())+Integer.parseInt(split[45].trim())+
								Integer.parseInt(split[46].trim()))+","+(Integer.parseInt(split[48].trim())+Integer.parseInt(split[49].trim())+Integer.parseInt(split[50].trim())+
								Integer.parseInt(split[51].trim()))+","+(Integer.parseInt(split[53].trim())+Integer.parseInt(split[54].trim())+Integer.parseInt(split[55].trim())+
								Integer.parseInt(split[56].trim()))+","+(Integer.parseInt(split[58].trim())+Integer.parseInt(split[59].trim())+Integer.parseInt(split[60].trim())+
								Integer.parseInt(split[61].trim()))
								);
						prev = present;
						}
					}	
					br2.close();
					bw.close();
				}
			br1.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int returnwaterMode(double plantswater[], double waterAmounts[]) {
		int waterMode = 0;
		for(int i =0;i<4;i++) {
			if(plantswater[i] !=0.0 && (waterAmounts[i] - plantswater[i]) >=1)
			{
				waterMode++;
			}
		}
		return waterMode;
	}
	
	public int returnBalanceMode(double plantswater[], double waterAmounts[]) {
		int balanceMode = 0;
		for(int i =0;i<4;i++) {
			if(Math.abs(plantswater[i]-waterAmounts[i]) <= 5 && plantswater[i]!=0.0)
			{
				balanceMode++;
			}
		}
		return balanceMode;
	}
	
	public void write_water_mode(String grade) {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(".\\"+grade+".txt"));
			String path = ".\\EDM\\"+grade+"\\sorted\\";
			String nextFileLine = new String();
			File file = new File(".\\EDM\\"+grade+"\\waterspans-"+grade.split("-")[0]+".csv");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Filename,Mode,Seconds,Sustainment");
			//mode calculated per biome
			//if water-plants and plants!= 0 then mode
			//
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				System.out.println(nextFile);
				BufferedReader br2 = new BufferedReader(new FileReader(path+nextFile));
				String next = br2.readLine(); // heading
				String prev = br2.readLine();
				String split_prev[] = prev.split(",");
				int startspan = 0;
				int start= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
						Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
						+Integer.parseInt(split_prev[0].split("-")[5].trim());
				int span=1;
				while((next=br2.readLine())!=null) {
					//fill gaps
					split_prev = prev.split(",");
					int prevSec= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
							+Integer.parseInt(split_prev[0].split("-")[5].trim());
					
					String split[] = next.split(",");
					
					int nextSec= Integer.parseInt(split[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split[0].split("-")[4].trim())*60 
							+Integer.parseInt(split[0].split("-")[5].trim());
					
					if(Integer.parseInt(split_prev[split_prev.length-6]) == 
							Integer.parseInt(split[split.length -6])) {
						span += nextSec - prevSec;
						prev = next;
					}
					else {
						bw.write("\n"+nextFile+","+split_prev[split_prev.length-6]+","+startspan+","+span);
						//endSpan = prevSec;
						startspan = nextSec-start;
						prev = next;
						span =1;
					}
				}
				bw.write("\n"+nextFile+","+split_prev[split_prev.length-6]+","+startspan+","+span);
				br2.close();
			}
			br1.close();
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write_balance_mode(String grade) {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(".\\"+grade+".txt"));
			String path = ".\\EDM\\"+grade+"\\sorted\\";
			String nextFileLine = new String();
			File file = new File(".\\EDM\\"+grade+"\\balancespans-"+grade.split("-")[0]+".csv");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Filename,Mode,Seconds,Sustainment");
			//mode calculated per biome
			//if water-plants and plants!= 0 then mode
			//
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				System.out.println(nextFile);
				BufferedReader br2 = new BufferedReader(new FileReader(path+nextFile));
				String next = br2.readLine(); // heading
				String prev = br2.readLine();
				String split_prev[] = prev.split(",");
				int startspan = 0;
				int start= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
						Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
						+Integer.parseInt(split_prev[0].split("-")[5].trim());
				int span=1;
				while((next=br2.readLine())!=null) {
					//fill gaps
					split_prev = prev.split(",");
					int prevSec= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
							+Integer.parseInt(split_prev[0].split("-")[5].trim());
					
					String split[] = next.split(",");
					
					int nextSec= Integer.parseInt(split[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split[0].split("-")[4].trim())*60 
							+Integer.parseInt(split[0].split("-")[5].trim());
					
					if(Integer.parseInt(split_prev[19]) == 
							Integer.parseInt(split[19])) {
						span += nextSec - prevSec;
						prev = next;
					}
					else {
						bw.write("\n"+nextFile+","+split_prev[19]+","+startspan+","+span);
						//endSpan = prevSec;
						startspan = nextSec-start;
						prev = next;
						span =1;
					}
				}
				bw.write("\n"+nextFile+","+split_prev[19]+","+startspan+","+span);
				br2.close();
			}
			br1.close();
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write_mode(String grade, int flag) {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(".\\"+grade+".txt"));
			String path = ".\\EDM\\"+grade+"\\sorted\\";
			String nextFileLine = new String();
			String modefile = " ";
			if(flag ==0) {
				modefile="SDI";
			}
			else {
				modefile="shannon";
			}
			File file = new File(".\\EDM\\"+grade+"\\withspans-"+modefile+".csv");
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("Filename,Mode,Seconds,Sustainment");
			int offset =0;
			if(flag==0) { //SDI
				offset=7;
			}
			else {//shannon
				offset =1;
			}
			while((nextFileLine=br1.readLine())!=null) {
				String nextFile = nextFileLine.split(",")[0];
				BufferedReader br2 = new BufferedReader(new FileReader(path+nextFile));
				String next = br2.readLine(); // heading
				String prev = br2.readLine();
				String split_prev[] = prev.split(",");
				int startspan = 0;
				int start= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
						Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
						+Integer.parseInt(split_prev[0].split("-")[5].trim());
				int span=1;
				while((next=br2.readLine())!=null) {
					//fill gaps
					split_prev = prev.split(",");
					int prevSec= Integer.parseInt(split_prev[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split_prev[0].split("-")[4].trim())*60 
							+Integer.parseInt(split_prev[0].split("-")[5].trim());
					
					String split[] = next.split(",");
					
					int nextSec= Integer.parseInt(split[0].split("-")[3].trim())*60*60+
							Integer.parseInt(split[0].split("-")[4].trim())*60 
							+Integer.parseInt(split[0].split("-")[5].trim());
					
					if(Integer.parseInt(split_prev[split_prev.length-offset]) == 
							Integer.parseInt(split[split.length -offset])) {
						span += nextSec - prevSec;
						prev = next;
					}
					else {
						bw.write("\n"+nextFile+","+split_prev[split_prev.length-offset]+","+startspan+","+span);
						//endSpan = prevSec;
						startspan = nextSec-start;
						prev = next;
						span =1;
					}
				}
				bw.write("\n"+nextFile+","+split_prev[split_prev.length-offset]+","+startspan+","+span);
				br2.close();
			}
			br1.close();
			bw.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public int returnModes(double index[], double threshhold) {
		int mode = 0;
		for(int i=0;i<4;i++) {
			if(index[i]>=threshhold) {
				mode++;
			}
		}
		return mode;
	}
	
	public double[] returnwaterAvailable(String water[]) {
		double waterAmounts[] = new double[4];
		for(int i =0;i<4;i++) {
			waterAmounts[i] = 0.0;
		}
		for(int i =0;i<4;i++) {
			String biome_water[] = water[i].split("-");
			for(int bin=0;bin<10;bin++) {
				waterAmounts[i] += Double.parseDouble(biome_water[bin]);
			}
			waterAmounts[i] = waterAmounts[i];
		}
		return waterAmounts;
	}
	public double[] returnSDI(String plants[], int flag) {
		double index [] = new double [4];
		for(int i=0;i<4;i++) {
			if(plants[i].equals(" ")) {
			index[i] = 0.0;
			}
			else {
				String biome[] = plants[i].split("-"); //T[NUM]Q[NUM]X[NUM]
				Integer[] plantsnos = new Integer[biome.length];
				for(int count=0;count<biome.length;count++){  	
					plantsnos[count]=Integer.parseInt((biome[count].split("Q")[1]).split("X")[0]);
				}
				
				if(flag ==1) {
					index[i] = this.calcSDI(plantsnos);
				}
				else {
					index[i] = this.shanon(plantsnos);
				}
			}
		}
		return index;
	}
	
	
	
	public double calcSDI(Integer[] plants)
	{
		// for each timestamp,calculate the simsons diversity index through the plant category,and live numbers
		int sum=0;
		int species_sum[] = new int[plants.length];
		for(int i =0;i<plants.length;i++){
			sum+=plants[i];       /*N*/
			species_sum[i]=plants[i]*(plants[i]-1);  /*n(n-1)*/
		}
		double numerator = species_sum[0];
		for(int i=1;i<species_sum.length;i++){
			numerator+=species_sum[i];
		}
		double index =0;
		if(sum!=0 && sum!=1){
			double dinominator = sum*(sum-1);
			index = 1- (numerator/ dinominator);
		}
		
		return index;
		
	}
	
	public int[] calcPlantNos(String plants[]) {
		int biome_plants[] =new int[4];
		for(int i=0;i<4;i++) {
			biome_plants[i] =0;
		}
		for(int i=0;i<4;i++) {
			//System.out.println(plants[i]);
			if(plants[i].equals(" ")) {
				biome_plants[i] += 0;
			}
			else {
					String biome[] = plants[i].split("-"); //T[NUM]Q[NUM]X[NUM]
					for(int count=0;count<biome.length;count++){ 
						biome_plants[i]+=Integer.parseInt((biome[count].split("Q")[1]).split("X")[0]);	
					}
			}
		}
		return biome_plants;
	}
	public double[] drinkAmount(String plants[]) {
		double biome_plants[] = new double[4];
		for(int i=0;i<4;i++) {
			//System.out.println(plants[i]);
			if(plants[i].equals(" ")) {
				biome_plants[i]= 0.0;
			}
			else {
				String biome[] = plants[i].split("-"); //T[NUM]Q[NUM]X[NUM]
				double levelplants = 0.0;
				for(int count=0;count<biome.length;count++){  	
				//	System.out.println(biome[count]);
					int id = Integer.parseInt(biome[count].split("Q")[0].split("T")[1]);
					//System.out.println(id);
					Integer level = this.plantlevels.get(id);
					double multiplier = 0.0; //check if we need to divide by 1000 or not
					if(level ==1) {
						multiplier = 0.2*0.279;
					}
					else if(level==2) {
						multiplier = 0.467*0.279;
					}
					else if(level ==3) {
						multiplier = 0.73* 0.279;
					}
					else if(level ==4) {
						multiplier = 1.5 *0.279;
					}
					levelplants+=Integer.parseInt((biome[count].split("Q")[1]).split("X")[0])*multiplier;
				}
				biome_plants[i] = levelplants;	
				}
		}
		return biome_plants;
	}
	
	public double getdeviation(ArrayList field,double avg)
	{
		double deviation=0;
		for(int i=0;i< field.size();i++){
			deviation=deviation+Math.pow(((int)field.get(i)-avg),2);
		}
		double ans=deviation/field.size();
		deviation = Math.sqrt(ans);
		return deviation;
	}
	
	public void latency_plants(String grade,int indexflag) {
		//go through the span files find start time of first mode 4
		//read one filename find mode 4 for that record filename latency 
		//and read frequency too
		try {
			
			String modefile = " ";
			if(indexflag==0) {
				modefile ="SDI";
			}
			else {
				modefile = "shannon";
			}
			BufferedReader br = new BufferedReader(new FileReader(".\\EDM\\"+grade+"\\withspans-"+modefile+".csv"));
			File file = new File(".\\EDM\\"+grade+"\\freq-lat-duration-avg-"+grade.split("-")[0]+"-"+modefile+".csv");
			String next = br.readLine();
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			int first = 0;
			int flag = 0;
			int latency = -1000;
			int duration = 0;
			int freq = 0;
			int avg= 0;
			next = br.readLine();
			String prev = next.split(",")[0];
			ArrayList spans = new ArrayList<Integer>();
			String split[] = next.split(",");
			if(Integer.parseInt(split[1]) == 4 ) {
				latency = Integer.parseInt(split[2]);
				duration = Integer.parseInt(split[3]);
				flag =1;
				avg += Integer.parseInt(split[3]);
				spans.add(Integer.parseInt(split[3]));
				freq++;
			}
			
			bw.write("Filename,Latency,Duration,Frequency,Average Duration,SD Duration");
			while((next=br.readLine())!=null) {
				split = next.split(",");
				String filename = split[0];
				if(!prev.equals(filename) && first != 0) {
					double to_write = 0.0;
					double sd =0;
					if(freq!=0) {
						to_write = avg/freq;
						sd = this.getdeviation(spans, avg);
					}
					bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
					spans= new ArrayList<Integer>();
					avg =0;
					freq=0;
					flag =0;
					latency=0;
					duration =0;
				}
				else if(!prev.equals(filename) && first ==0){
					double to_write = 0.0;
					double sd =0;
					if(freq!=0) {
						to_write = avg/freq;
						sd = this.getdeviation(spans, avg);
					}
					bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
					spans= new ArrayList<Integer>();
					avg =0;
					freq=0;
					flag =0;
					duration =0;
					latency=0;
					first=1;
				}
				if(flag == 0 &&  Integer.parseInt(split[1]) == 4 ) {
					latency = Integer.parseInt(split[2]);
					duration = Integer.parseInt(split[3]);
					flag =1;
				}
				if(Integer.parseInt(split[1])==4) {
					avg += Integer.parseInt(split[3]);
					spans.add(Integer.parseInt(split[3]));
					freq++;
				}
				prev = filename;
			}
			double to_write = 0.0;
			double sd =0;
			if(freq!=0) {
				to_write = avg/freq;
				sd = this.getdeviation(spans, avg);
			}
			bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
			bw.close();
			br.close();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void latency_water(String grade) {
		//go through the span files find the start time of first mode 4
		//go through the span files find start time of first mode 4
				//read one filename find mode 4 for that record filename latency 
				//and read frequency too
				try {
					BufferedReader br = new BufferedReader(new FileReader(".\\EDM\\"+grade+"\\waterspans-"+grade.split("-")[0]+".csv"));
					File file = new File(".\\EDM\\"+grade+"\\freq-lat-duration-avg-"+grade.split("-")[0]+"-water.csv");
					String next = br.readLine();  //heading
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					int first = 0;
					int flag = 0;
					int latency = -1000;
					int duration = 0;
					int freq = 0;
					int avg= 0;
					next = br.readLine();
					String prev = next.split(",")[0];  //filename
					ArrayList spans = new ArrayList<Integer>();
					String split[] = next.split(",");
					if(Integer.parseInt(split[1]) == 4 ) {
						latency = Integer.parseInt(split[2]);
						duration = Integer.parseInt(split[3]);
						flag =1;
						avg += Integer.parseInt(split[3]);
						spans.add(Integer.parseInt(split[3]));
						freq++;
					}
					
					bw.write("Filename,Latency,Duration,Frequency,Average Duration,SD Duration");
					while((next=br.readLine())!=null) {
						split = next.split(",");
						String filename = split[0];
						if(!prev.equals(filename) && first != 0) {
							double to_write = 0.0;
							double sd =0;
							if(freq!=0) {
								to_write = avg/freq;
								sd = this.getdeviation(spans, avg);
							}
							bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
							spans= new ArrayList<Integer>();
							avg =0;
							freq=0;
							flag =0;
							latency =0;
							duration =0;
						}
						else if(!prev.equals(filename) && first ==0){
							double to_write = 0.0;
							double sd =0;
							if(freq!=0) {
								to_write = avg/freq;
								sd = this.getdeviation(spans, avg);
							}
							bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
							spans= new ArrayList<Integer>();
							avg =0;
							freq=0;
							flag =0;
							first=1;
							latency =0;
							duration =0;
						}
						if(flag == 0 &&  Integer.parseInt(split[1]) == 4 ) {
							latency = Integer.parseInt(split[2]);
							duration = Integer.parseInt(split[3]);
							flag =1;
						}
						if(Integer.parseInt(split[1])==4) {
							avg += Integer.parseInt(split[3]);
							spans.add(Integer.parseInt(split[3]));
							freq++;
						}
						prev = filename;
					}
					double to_write = 0.0;
					double sd =0;
					if(freq!=0) {
						to_write = avg/freq;
						sd = this.getdeviation(spans, avg);
					}
					bw.write("\n"+prev+","+latency+","+duration+","+freq+","+to_write+","+sd);
					bw.close();
					br.close();
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
	}
	
	double shanon(Integer[] plants) {
		double sum=0.0;
		double species_sum = 0.0;
		for(int i =0;i<plants.length;i++) {
			species_sum += plants[i];
		}
		if(species_sum==0.0) {
			sum=0.0;
		}
		else {
			for(int i =0;i<plants.length;i++) {
				if(plants[i]/species_sum != 0.0) {
					sum += (plants[i]/species_sum) * (Math.log(plants[i]/species_sum));
				}
			}
		}
		return ((-1)*sum);
	}
	public static void main(String args[])
	{
		Summary_after_update s = new Summary_after_update();
		s.readPlantlevels();
		String grade_files[]= {"2-grade","3-grade","4-grade","5-grade","7-grade","8-grade"};
		for(String grade:grade_files) {
			//s.fill_gaps(grade);
			//s.readfile(grade);
			/*s.write_mode(grade,0);
			s.write_mode(grade,1);
			s.write_water_mode(grade);
			s.write_balance_mode(grade);
			s.latency_plants(grade,0);
			s.latency_plants(grade,1);
			s.latency_water(grade);
			s.latency_water(grade);*/
			System.out.println(grade);
			s.mark_events(grade);
			
		   }
		}
	}


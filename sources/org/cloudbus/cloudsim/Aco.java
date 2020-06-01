package org.cloudbus.cloudsim;

import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class Aco {
	private List<Ant> ants;
	private int antcount;
	private int Q = 100;
	private double[][] pheromone;
	private double[][] Delta;
	private int vmNum;
	private int cloudletNum;
	public position[] bestTour;
	private double bestLength;
	private List<? extends Cloudlet> cloudletList;
	private List<? extends Vm> vmList;
	public class position{
		int vm;
		int cloudlet;
		public position(int a, int b){
			vm = a;
			cloudlet = b;
		}
	}
	
	public void init(int antNum, List<? extends Cloudlet> list1, List<? extends Vm> list2){
		//cloudletList = new ArrayList<? extends Cloudlet>;
		cloudletList = list1;
		vmList = list2;
		antcount = antNum;
		ants = new ArrayList<Ant>(); 
		vmNum = vmList.size();
		cloudletNum = cloudletList.size();
		pheromone = new double[vmNum][cloudletNum];
		Delta = new double[vmNum][cloudletNum];
		bestLength = 1000000;
		
		for(int i=0; i<vmNum; i++){
			for(int j=0; j<cloudletNum; j++){
				pheromone[i][j] = 0.7 * vmList.get(i).getMips() / 100000 + 
						0.1 * vmList.get(i).getBw() / 10000 + 
						0.1 * vmList.get(i).getRam() +
						0.1 * vmList.get(i).getSize();
			}
		}
		
		bestTour = new position[cloudletNum];
		for(int i=0; i<cloudletNum; i++){
			bestTour[i] = new position(-1, -1);
		}
		//随机放置蚂蚁  
        for(int i=0; i<antcount; i++){  
            ants.add(new Ant());  
            ants.get(i).randomSelectVM(cloudletList, vmList);
        }  			
	}
	
	public void start(int maxgen){
		for(int runTime=0; runTime<maxgen; runTime++){
			for(int i=0; i<antcount; i++){
				for(int j=1; j<cloudletNum; j++){	
					ants.get(i).selectNextVM(pheromone);
				}
			}
			for(int i=0; i<antcount; i++){
				ants.get(i).calTourLength();
				
				if(ants.get(i).tourLength<bestLength){  
	                bestLength = ants.get(i).tourLength;
	                for(int j=0;j<cloudletNum;j++){  
	                	bestTour[j].vm = ants.get(i).tour.get(j).vm;
	                    bestTour[j].cloudlet = ants.get(i).tour.get(j).cloudlet;
	                } 
	                  
				}
				ants.get(i).calDelta();
			}
			updatePheromone();
			for(int k=0; k<vmNum; k++){
            	for(int j=0; j<cloudletNum; j++){
            		pheromone[k][j] = pheromone[k][j] + Q/bestLength;
            	}
            }
			for(int i=0;i<antcount;i++){  
				ants.get(i).randomSelectVM(cloudletList, vmList);  
		    }  	
		}
	}
	
	public void updatePheromone(){
		double rou=0.4;  
        for(int k=0; k<antcount; k++){
        	for(int i=0; i<vmNum; i++){
        		for(int j=0; j<cloudletNum; j++){
        			Delta[i][j] += ants.get(k).delta[i][j];
        		}
        	}
        }
        
        for(int i=0; i<vmNum; i++){
        	for(int j=0; j<cloudletNum; j++){
        		pheromone[i][j] = (1-rou)*pheromone[i][j] + Delta[i][j];
        	}
        }  
	}
	
    public void print(){  
        System.out.println("最优路径长度是"+bestLength);
        for(int j=0; j<cloudletNum; j++)
        {
        	System.out.println(bestTour[j].cloudlet+"分配给："+bestTour[j].vm);
        }
    }  	
    
    public List<Ant> getAnts(){
    	return this.ants;
    }
    
    public int getAntcount(){
    	return this.antcount;
    }
    
    public double[][] getPheromone(){
    	return this.pheromone;
    }
    
    public double[][] getDelta(){
    	return this.Delta;
    }
    
    public int getVmNum(){
    	return this.vmNum;
    }
    
    public int getCloudletNum(){
    	return this.cloudletNum;
    }
    
    public position[] getBestTour(){
    	return this.bestTour;
    }
    
    public double getBestLength(){
    	return this.bestLength;
    }
}
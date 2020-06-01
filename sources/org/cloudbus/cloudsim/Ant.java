package org.cloudbus.cloudsim;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

 public class Ant{
	public class position{
		public int vm;
		public int cloudlet;
		public position(int a, int b){
			vm = a;
			cloudlet = b;
		}
	} 
	public static final  double E = 2.71828;
	public double[][] delta;
	public int Q = 100;
	public List<position> tour;
	public double tourLength;
	public long[] TL_cloudlet;
	public List<Integer> tabu;
	private int VMs;
	private int cloudlets;
	private List<? extends Cloudlet> cloudletList;
	private List<? extends Vm> vmList;
	
	public void randomSelectVM(List<? extends Cloudlet> list1, List<? extends Vm> list2){
		cloudletList = list1;
		vmList = list2;
		VMs = vmList.size();
		cloudlets = cloudletList.size();
		delta = new double[VMs][cloudlets];
		TL_cloudlet = new long[VMs];
		for(int i=0; i<VMs; i++) TL_cloudlet[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		int firstVM = (int)(VMs*Math.random());
		int firstExecute = (int)(cloudlets*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(firstExecute);
		TL_cloudlet[firstVM] += cloudletList.get(firstExecute).getCloudletLength();
	}
	
	public double dij(int vm, int cloudlet){
		double d;
	    d = TL_cloudlet[vm]/(vmList.get(vm).getMips() * vmList.get(vm).getNumberOfPes()) + (double)cloudletList.get(cloudlet).getCloudletLength()/vmList.get(vm).getBw();
		return d;
	}
	
	public void selectNextVM(double[][] pheromone){
		double[][] p;
		p = new double[VMs][cloudlets];
		double alpha = 0.3;
		double beta = 1.0;
		double sum = 0;
		for(int i=0; i<VMs; i++){
			for(int j=0; j<cloudlets; j++){
				if(tabu.contains(j)) continue;
				sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/dij(i,j),beta);
			}
		}
		
		for(int i=0; i<VMs; i++){
			for(int j=0; j<cloudlets; j++){
				p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/dij(i,j),beta)/sum;
				if(tabu.contains(j))p[i][j] = 0;
			}
		}
		double selectp = Math.random();
        double sumselect = 0;
        int selectVM = -1;
        int selectcloudlet = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<cloudlets; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectcloudlet = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectcloudlet == -1) {
        	System.out.println("选择下一个虚拟机没有成功！");
        }
        tabu.add(selectcloudlet);
		tour.add(new position(selectVM, selectcloudlet));
		TL_cloudlet[selectVM] += cloudletList.get(selectcloudlet).getCloudletLength();  		
	  }
	  
	public void calTourLength(){
		double[] max;
		max = new double[VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += dij(tour.get(i).vm, tour.get(i).cloudlet); 
		}		
		tourLength = max[0];
		for(int i=0; i<VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
		}
		return;
	}
	
	public void calDelta() {
		for(int i = 0; i < VMs; ++i) {
			for(int j = 0; j < cloudlets; ++j) {
				if(i == tour.get(j).vm && tour.get(j).cloudlet == j) {
					delta[i][j] = Q/tourLength;
				}
				else delta[i][j] = 0;
			}
		}
	}
	
    public void calDelta(double bestLength, int T){
    	Random r = new Random();
    	double md = 0;
    	for(int i=0; i<VMs; i++){
    		for(int j=0; j<cloudlets; j++){
    			double dF = r.nextDouble();
    			if(i==tour.get(j).vm&&tour.get(j).cloudlet==j) {
    				for(int k = 0; k < cloudletList.size(); k++) {
    					if(tourLength > md)
    						md = tourLength;
    				}
    				if(md < bestLength) {
    					delta[i][j] = Q/tourLength;
    				}
    				else if(Math.pow(E, (bestLength - md)) / T > dF) {
    					delta[i][j] = Q/tourLength;
    				}
    				else
    					delta[i][j] = 0;
    			}
    			else delta[i][j] = 0;
    		}
    	}
    }
    
    public double[][] getDelta(){
    	return this.delta;
    }
    
    public List<position> getTour(){
    	return this.tour;
    }
    
    public double getTourLength(){
    	return this.tourLength;
    }
    
    public long[] getTL_cloudlet(){
    	return this.TL_cloudlet;
    }
    
    public List<Integer> getTabu(){
    	return this.tabu;
    }
}
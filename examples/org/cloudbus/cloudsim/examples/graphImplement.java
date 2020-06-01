/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */


package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterBrokerACO;
import org.cloudbus.cloudsim.DatacenterBrokerLBACO;
import org.cloudbus.cloudsim.DatacenterBrokerMinMin;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import org.cloudbus.cloudsim.examples.LineChart_AWT;
import org.jfree.data.category.DefaultCategoryDataset;

public class graphImplement {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	private static List<Cloudlet> cloudletList1;
	private static List<Cloudlet> cloudletList2;
	private static List<Cloudlet> cloudletList3;

	/** The vmlist. */
	private static List<Vm> vmlist;
	private static List<Vm> vmlist1;
	private static List<Vm> vmlist2;
	private static List<Vm> vmlist3;
	
	private static List<Vm> createVM(int userId, int vms) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; //image size (MB)
		int ram = 512; //vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name
		Random r = new Random(1);
		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i<vms;i++){
			mips = 500 + r.nextInt(500);
			vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			//for creating a VM with a space shared scheduling policy for cloudlets:
			//vm[i] = Vm(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

			list.add(vm[i]);
		}

		return list;
	}


	private static List<Cloudlet> createCloudlet(int userId, int cloudlets){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long length ;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		Random r1 = new Random(1);
		// Random r2 = new Random(2);
		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			length = 100 + r1.nextInt(900);
			// fileSize = 100 + r2.nextInt(200);
			cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}


	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample6...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events
			
			DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
			DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
			
			int i=20;	
			for (int k=1;k<=5;k++){
				int counts = 100*k;
				CloudSim.init(num_user, calendar, trace_flag);
				// Second step: Create Datacenters
				//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
				@SuppressWarnings("unused")
				Datacenter datacenter0 = createDatacenter("Datacenter_0",i);
				Datacenter datacenter1 = createDatacenter("Datacenter_1",i);
				Datacenter datacenter2 = createDatacenter("Datacenter_2",i);
				Datacenter datacenter3 = createDatacenter("Datacenter_2",i);
				
				//Third step: Create Broker
				DatacenterBroker broker = createBroker();
				DatacenterBrokerMinMin brokerMinMin = createBrokerMinMin();
				DatacenterBrokerACO brokerACO = createBrokerACO();
				DatacenterBrokerLBACO brokerLBACO = createBrokerLBACO();
				
				int brokerId = broker.getId();
				int brokerId1 = brokerMinMin.getId();
				int brokerId2 = brokerACO.getId();
				int brokerId3 = brokerLBACO.getId();
				
				vmlist = createVM(brokerId,i);
				vmlist1 = createVM(brokerId1,i);
				vmlist2 = createVM(brokerId2,i);
				vmlist3 = createVM(brokerId3,i);
				
				cloudletList = createCloudlet(brokerId,counts);
				cloudletList1 = createCloudlet(brokerId1,counts);
				cloudletList2 = createCloudlet(brokerId2,counts);
				cloudletList3 = createCloudlet(brokerId3,counts);
				Log.printLine(cloudletList.size());
				
				broker.submitVmList(vmlist);
				broker.submitCloudletList(cloudletList);
				
				brokerMinMin.submitVmList(vmlist1);
				brokerMinMin.submitCloudletList(cloudletList1);
				
				brokerACO.submitVmList(vmlist2);
				brokerACO.submitCloudletList(cloudletList2);
				brokerACO.bindCloudletsToVms(12, 34);
				
				brokerLBACO.submitVmList(vmlist3);
				brokerLBACO.submitCloudletList(cloudletList3);
				
				// Fifth step: Starts the simulation
				CloudSim.startSimulation();
				
				// Final step: Print results when simulation is over
				List<Cloudlet> newList = broker.getCloudletReceivedList();
				List<Cloudlet> newList1 = brokerMinMin.getCloudletReceivedList();
				List<Cloudlet> newList2 = brokerACO.getCloudletReceivedList();
				List<Cloudlet> newList3 = brokerLBACO.getCloudletReceivedList();
				
				double LB1 = broker.getLB();
				double LB2 = brokerMinMin.getLB();
				double LB3 = brokerACO.getLB();
				double LB4 = brokerLBACO.getLB();
				
				CloudSim.stopSimulation();
				
				double max = 0;
				for (int j=0;j<counts;j++){
					if(max < newList.get(j).getFinishTime())
						max = newList.get(j).getFinishTime();
				}
				double min = 10000000;
				for (int j=0;j<counts;j++){
					if(min > newList.get(j).getExecStartTime())
						min = newList.get(j).getExecStartTime();
				}
				
				double max1 = 0;
				for (int j=0;j<counts;j++){
					if(max1 < newList1.get(j).getFinishTime())
						max1 = newList1.get(j).getFinishTime();
				}
				double min1 = 10000000;
				for (int j=0;j<counts;j++){
					if(min1 > newList1.get(j).getExecStartTime())
						min1 = newList1.get(j).getExecStartTime();
				}
				
				double max2 = 0;
				for (int j=0;j<counts;j++){
					if(max2 < newList2.get(j).getFinishTime())
						max2 = newList2.get(j).getFinishTime();
				}
				double min2 = 10000000;
				for (int j=0;j<counts;j++){
					if(min2 > newList2.get(j).getExecStartTime())
						min2 = newList2.get(j).getExecStartTime();
				}
				
				double max3 = 0;
				for (int j=0;j<counts;j++){
					if(max3 < newList3.get(j).getFinishTime())
						max3 = newList3.get(j).getFinishTime();
				}
				double min3 = 10000000;
				for (int j=0;j<counts;j++){
					if(min3 > newList3.get(j).getExecStartTime())
						min3 = newList3.get(j).getExecStartTime();
				}
				
				dataset2.addValue((Number)(LB1),"RR Algo",String.valueOf(100*k));
				dataset2.addValue((Number)(LB2),"MinMin Algo",String.valueOf(100*k));
				dataset2.addValue((Number)(LB3),"ACO Algo",String.valueOf(100*k));
				dataset2.addValue((Number)(LB4),"MyACO Algo",String.valueOf(100*k));
				dataset1.addValue((Number)(max-min),"RR Algo",String.valueOf(100*k));
				dataset1.addValue((Number)(max1-min1),"MinMin Algo",String.valueOf(100*k));
				dataset1.addValue((Number)(max2-min2),"ACO Algo",String.valueOf(100*k));
				dataset1.addValue((Number)(max3-min3),"MyACO Algo",String.valueOf(100*k));
			}
			
			LineChart_AWT.implement("Load Balance Vs. Cloudlet","No. of cloudlets","Degree of Imbalance",dataset2);
			LineChart_AWT.implement("Finish Time Vs. Cloudlet","No. of cloudlets","Total makespan",dataset1);
			
			Log.printLine("CloudSimExample6 finished!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static double dFix(double a, double b) {
		return (a + b) / 2.9;
	}
	
	private static Datacenter createDatacenter(String name, int elements){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		//    Machines
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		//    create a list to store these PEs before creating
		//    a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into the list.
		//for a quad-core machine, a list of 4 PEs is required:
		for (int i=0;i<elements;i++){
			peList1.add(new Pe(i, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		}
		
		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 25600; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 50000;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList1,
    				new VmSchedulerSpaceShared(peList1)
    			)
    		); 


		//To create a host with a space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerSpaceShared(peList1)
    	//		)
    	//	);

		//To create a host with a oportunistic space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerOportunisticSpaceShared(peList1)
    	//		)
    	//	);


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBrokerACO createBrokerACO(){

		DatacenterBrokerACO broker = null;
		try {
			//int m, double Q, double alpha, double beta, double rho
			broker = new DatacenterBrokerACO("Broker2");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBrokerLBACO createBrokerLBACO(){

		DatacenterBrokerLBACO broker = null;
		try {
			//int m, double Q, double alpha, double beta, double gamma, double rho
			broker = new DatacenterBrokerLBACO("Broker2",37,1,3,2,8,0.01);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	private static DatacenterBrokerMinMin createBrokerMinMin(){

		DatacenterBrokerMinMin broker = null;
		try {
			broker = new DatacenterBrokerMinMin("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	// private static void printCloudletList(List<Cloudlet> list) {
	// 	int size = list.size();
	// 	Cloudlet cloudlet;

	// 	String indent = "    ";
	// 	Log.printLine();
	// 	Log.printLine("========== OUTPUT ==========");
	// 	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
	// 			"Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

	// 	DecimalFormat dft = new DecimalFormat("###.##");
	// 	for (int i = 0; i < size; i++) {
	// 		cloudlet = list.get(i);
	// 		Log.print(indent + cloudlet.getCloudletId() + indent + indent);

	// 		if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
	// 			Log.print("SUCCESS");

	// 			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
	// 					indent + indent + indent + dft.format(cloudlet.getActualCPUTime()) +
	// 					indent + indent + dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime()));
	// 		}
	// 	}

	// }
}
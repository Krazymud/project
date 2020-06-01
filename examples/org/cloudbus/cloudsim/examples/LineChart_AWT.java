package org.cloudbus.cloudsim.examples;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class LineChart_AWT extends ApplicationFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LineChart_AWT(String title, String category, String value, DefaultCategoryDataset dataset) {
		super("test");
		
//		JFreeChart lineChart = ChartFactory.createLineChart(
//		         title,
//		         category,value,
//		         dataset,
//		         PlotOrientation.VERTICAL,
//		         true,true,false);
		
		JFreeChart barChart = ChartFactory.createBarChart(
				title, 
				category, value,
				dataset);
		
		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
		
	}
	
	public static void implement(String title, String category, String value, DefaultCategoryDataset dataset) {
		LineChart_AWT chart = new LineChart_AWT(title, category, value, dataset);
		
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}
}
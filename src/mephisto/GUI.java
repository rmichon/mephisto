/*
 *  Copyright (C) 2013 GRAME, Romain Michon, CCRMA - Stanford University
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, in part or in full are permitted. 
 *  This sample code is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package mephisto;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * GUI implements the user interface of Mephisto 
 */
public class GUI {
	// global variables and objects of the class
	static String[] oscAddresses;
	static float[] oscMinVals;
	static float[] oscMaxVals;
	static float[] sensorsRangeMin;
	static float[] sensorsRangeMax;
	static int[] sensorN;
	static String networkName;
	static String networkPswd;
	static String networkHostAddress;
	static int updateRateVal;
	static JTextArea oscAddress;
	static JTextArea rangeMin;
	static JTextArea rangeMax;
	static JComboBox sensors;
	static JTextArea netName;
	static JTextArea netPswd;
	static JTextArea hostAddress;
	static JTextArea updateRate;

	/*
	 * getSensorRange configures the range of a sensor in function
	 * of its name.
	 */
	public void getSensorRange(String sensor, int i){
		if(sensor.equals("Knob")){ 
			sensorsRangeMin[i] = 0;
			sensorsRangeMax[i] = 1023;
		}
		else if(sensor.equals("Proximity")){ 
			sensorsRangeMin[i] = 0;
			sensorsRangeMax[i] = 660;
		}
		else if(sensor.equals("AccelerometerX")){ 
			sensorsRangeMin[i] = 150;
			sensorsRangeMax[i] = 500;
		}
		else if(sensor.equals("AccelerometerY")){ 
			sensorsRangeMin[i] = 150;
			sensorsRangeMax[i] = 550;
		}
		else if(sensor.equals("AccelerometerZ")){ 
			sensorsRangeMin[i] = 150;
			sensorsRangeMax[i] = 550;
		}
		else if(sensor.equals("Flex")){ 
			sensorsRangeMin[i] = 350;
			sensorsRangeMax[i] = 680;
		}
		else if(sensor.equals("Pressure")){ 
			sensorsRangeMin[i] = 0;
			sensorsRangeMax[i] = 1005;
		}
	}
	
	/*
	 * oscConfig creates the interface to configure one sensor input.
	 * i is the sensor input number and mainWin is the JPanel to which
	 * the created line will be added.
	 */
	public void oscCongig(JPanel mainWin, String oscAdd, float min, float max, 
			float sensMin, float sensMax, int sensIndex, final int i){
		// the line containing the UI elements for this param
		final JPanel win = new JPanel();
		String[] sensorList = {"Knob", "Proximity", "AccelerometerX", "AccelerometerY", "AccelerometerZ", "Flex", "Pressure"};
		
		// UI labels
		JLabel oscLbl = new JLabel("OSC Address " + i + ":");
		oscLbl.setToolTipText("The OSC address of jack " + i);
	    JLabel rangeMinLbl = new JLabel("Min:");
	    rangeMinLbl.setToolTipText("Minimum OSC value");
	    JLabel rangeMaxLbl = new JLabel("Max:");
	    rangeMaxLbl.setToolTipText("Maximum OSC value");
	    JLabel sensorLbl = new JLabel("Sensor:");
	    sensorLbl.setToolTipText("Sensor plugged to jack " + i);
	     
	    // Instantiating the UI elements 
	    oscAddress = new JTextArea(1,10);
	    rangeMin = new JTextArea(1,5);
	    rangeMax = new JTextArea(1,5);
	    sensors = new JComboBox(sensorList);
	        
	    // Default values of the UI elements
	    oscAddress.setText(oscAdd);
	    oscAddress.setToolTipText("The OSC address of jack " + i);
	    rangeMin.setText(String.valueOf(min));
	    rangeMin.setToolTipText("Minimum OSC value");
	    rangeMax.setText(String.valueOf(max));
	    rangeMax.setToolTipText("Maximum OSC value");
	    sensors.setSelectedIndex(sensIndex);
	    sensors.setToolTipText("Sensor plugged to jack " + i);
	     
	    // UI elements are added to the row
	    win.add(oscLbl);
	    win.add(oscAddress);
	    win.add(rangeMinLbl);
	    win.add(rangeMin);
	    win.add(rangeMaxLbl);
	    win.add(rangeMax);
	    win.add(sensorLbl);
	    win.add(sensors);
	     
	    // the row is added to the main window
	    mainWin.add(win);
	     
	    // listener for the sensor list drop down menu
	    sensors.addActionListener(new ActionListener(){
	    	public void actionPerformed(ActionEvent e) {
	    		JComboBox cb = (JComboBox)e.getSource();
	    		getSensorRange((String)cb.getSelectedItem(),i);
	    		sensorN[i] = cb.getSelectedIndex();
	    	}
	    }
	    );
	    
	    // listener for the OSC address name
	    oscAddress.getDocument().addDocumentListener(new DocumentListener() {
	    	@Override
	    	public void changedUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void insertUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void removeUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	private void printIt(DocumentEvent documentEvent) {
	    		Document document = (Document) documentEvent.getDocument();
	    		int length = document.getLength();
	    		try {
	    			oscAddresses[i] = document.getText(0,length);
	    		} catch (BadLocationException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    }
	    );
	     
	    // listener for the minimum OSC value
	    rangeMin.getDocument().addDocumentListener(new DocumentListener() {
	    	@Override
	    	public void changedUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void insertUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void removeUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	private void printIt(DocumentEvent documentEvent) {
	    		Document document = (Document) documentEvent.getDocument();
	    		int length = document.getLength();
	    		try {
	    			oscMinVals[i] = Float.valueOf(document.getText(0,length));
	    		} catch (BadLocationException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    }
	    );
	    
	    // listener for the maximum OSC value
	    rangeMax.getDocument().addDocumentListener(new DocumentListener() {
	    	@Override
	    	public void changedUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void insertUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	@Override
	    	public void removeUpdate(DocumentEvent documentEvent) {
	    		printIt(documentEvent);
	    	}
	    	private void printIt(DocumentEvent documentEvent) {
	    		Document document = (Document) documentEvent.getDocument();
	    		int length = document.getLength();
	    		try {
	    			oscMaxVals[i] = Float.valueOf(document.getText(0,length));
	    		} catch (BadLocationException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    }
	    );
	}
	
	/*
	 * networkConfig builds the UI for the network configuration
	 * and add it to the main window.
	 */
	public void networkConfig(JPanel mainWin){
		// one JPanel for each row
		final JPanel win1 = new JPanel();
		final JPanel win2 = new JPanel();
		// UI elements labels
		JLabel netNameLab = new JLabel("Network Name:");
		netNameLab.setToolTipText("The name of the WIFI network you wish mephisto to connect by default");
		JLabel netPswdLab = new JLabel("Network Password:");
		netPswdLab.setToolTipText("The password to connect to the network. Leave \"none\" if there are no password");
		JLabel hostAddressLab = new JLabel("Host Address:");
		hostAddressLab.setToolTipText("The IP address of the host.");
		JLabel updateRateLab = new JLabel("Update Rate:");
		updateRateLab.setToolTipText("The rate in ms at which OSC messages are sent");
		
		// Instantiate UI elements
		netName = new JTextArea(1,10);
		netPswd = new JTextArea(1,10);
		hostAddress = new JTextArea(1,10);
		updateRate = new JTextArea(1,4);
		
		// default values for UI elements
		netName.setText(networkName);
		netName.setToolTipText("The name of the WIFI network you wish mephisto to connect by default");
		netPswd.setText(networkPswd);
		netPswd.setToolTipText("The password to connect to the network. Leave \"none\" if there are no password");
		hostAddress.setText(networkHostAddress);
		hostAddress.setToolTipText("The IP address of the host.");
		updateRate.setText(String.valueOf(updateRateVal));
		updateRate.setToolTipText("The rate in ms at which OSC messages are sent");
		
		// adding to the rows
		win1.add(netNameLab);
		win1.add(netName);
		win1.add(netPswdLab);
		win1.add(netPswd);
		win2.add(hostAddressLab);
		win2.add(hostAddress);
		win2.add(updateRateLab);
		win2.add(updateRate);
		
		// adding the two rows to the main window
		mainWin.add(win1);
		mainWin.add(win2);
		
		// network name listener
		netName.getDocument().addDocumentListener(new DocumentListener() {
	    	 @Override
	    	 public void changedUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void insertUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void removeUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 private void printIt(DocumentEvent documentEvent) {
	    		 Document document = (Document) documentEvent.getDocument();
	    		 int length = document.getLength();
	    		 try {
	    			 networkName = document.getText(0,length);
	    		 } catch (BadLocationException e) {
	    			 // TODO Auto-generated catch block
	    			 e.printStackTrace();
	    		 }
	    	 }
	     }
		);
		
		// network password listener
		netPswd.getDocument().addDocumentListener(new DocumentListener() {
	    	 @Override
	    	 public void changedUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void insertUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void removeUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 private void printIt(DocumentEvent documentEvent) {
	    		 Document document = (Document) documentEvent.getDocument();
	    		 int length = document.getLength();
	    		 try {
	    			 networkPswd = document.getText(0,length);
	    		 } catch (BadLocationException e) {
	    			 // TODO Auto-generated catch block
	    			 e.printStackTrace();
	    		 }
	    	 }
	     }
	     );
		
		// host address listener
		hostAddress.getDocument().addDocumentListener(new DocumentListener() {
	    	 @Override
	    	 public void changedUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void insertUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void removeUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 private void printIt(DocumentEvent documentEvent) {
	    		 Document document = (Document) documentEvent.getDocument();
	    		 int length = document.getLength();
	    		 try {
	    			 networkHostAddress = document.getText(0,length);
	    		 } catch (BadLocationException e) {
	    			 // TODO Auto-generated catch block
	    			 e.printStackTrace();
	    		 }
	    	 }
	     }
	     );
		
		// update rate listener
		updateRate.getDocument().addDocumentListener(new DocumentListener() {
	    	 @Override
	    	 public void changedUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void insertUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 @Override
	    	 public void removeUpdate(DocumentEvent documentEvent) {
	    		 printIt(documentEvent);
	    	 }
	    	 private void printIt(DocumentEvent documentEvent) {
	    		 Document document = (Document) documentEvent.getDocument();
	    		 int length = document.getLength();
	    		 try {
	    			 updateRateVal = Integer.valueOf(document.getText(0,length));
	    		 } catch (BadLocationException e) {
	    			 // TODO Auto-generated catch block
	    			 e.printStackTrace();
	    		 }
	    	 }
	     }
	     );
	}
	
	/*
	 * createAppName add .png file of the program name to the main window
	 */
	public void createAppName(JPanel mainWin){
		String filePath = System.getProperty("user.dir")+"/utility/picts/name.png";
		JPanel win = new JPanel();
		win.setSize(500,49);
		ImageIcon theTitle = new ImageIcon(filePath); 
		JLabel label = new JLabel(); 
	    label.setIcon(theTitle); 
	    win.add(label);
		mainWin.add(win);
	}
	
	/*
	 * savaParams save the parameters in a text file for the next use 
	 */
	public void saveParams(){
		BufferedWriter writer = null;
		File outFile = new File(System.getProperty("user.dir") + "/utility/savedParams.txt");
		
		try{
			writer = new BufferedWriter(new FileWriter(outFile));
			
            for(int i=0; i<5; i++){ 
            	writer.write(oscAddresses[i] + "\n");
            	writer.write(oscMinVals[i] + "\n");
            	writer.write(oscMaxVals[i] + "\n");
            	writer.write(sensorN[i] + "\n");
            }
            writer.write(networkName + "\n");
            writer.write(networkPswd + "\n");
            writer.write(networkHostAddress + "\n");
            writer.write(updateRateVal + "\n");
            writer.close();
		}catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}
	
	/*
	 * retrieveParams retrieve the saved parameters from a previous use 
	 */
	public void retrieveParams(){
		BufferedReader reader = null;
		try{
			File inFile = new File(System.getProperty("user.dir") + "/utility/savedParams.txt");
			reader = new BufferedReader(new FileReader(inFile));
			for(int i=0; i<5; i++){
				oscAddresses[i] = reader.readLine();
				oscMinVals[i] = Float.valueOf(reader.readLine());
				oscMaxVals[i] = Float.valueOf(reader.readLine());
				sensorN[i] = Integer.valueOf(reader.readLine());
			}
			networkName = reader.readLine();
			networkPswd = reader.readLine();
			networkHostAddress = reader.readLine();
			updateRateVal = Integer.valueOf(reader.readLine());
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/*
	 * Compile, save and upload button
	 */
	public void createUploadButton(JPanel win){
		JButton uploadBut = new JButton("Upload!");
		uploadBut.setToolTipText("Generate, compile and upload the firmware to Mephisto.");
        uploadBut.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
            	WriteSource.write(oscAddresses, oscMinVals, oscMaxVals, sensorsRangeMin, sensorsRangeMax, networkName, networkPswd, networkHostAddress,updateRateVal);
            	saveParams();
            	CompileAndUpload.doIt();
            }
        });
        
        win.add(uploadBut);
	}
	
	public GUI()
    {  
    	JFrame guiFrame = new JFrame();
        
        // make sure the program exits when the frame closes
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Mephisto");
        guiFrame.setSize(700,350);
      
        // this will center the JFrame in the middle of the screen
        guiFrame.setLocationRelativeTo(null);
        
        // panel for the OSC parameters
        final JPanel oscPanel = new JPanel();
        oscPanel.setLayout(new BoxLayout(oscPanel, BoxLayout.PAGE_AXIS));
        
        // initializing the parameters
        oscAddresses = new String[5];
        oscMinVals = new float[5];
        oscMaxVals = new float[5];
        sensorsRangeMin = new float[5];
        sensorsRangeMax = new float[5];
        sensorN = new int[5];
        
    	// getting the saved parameters
        retrieveParams();
    	
        // Default values for the parameters
        for(int i=0;i<5;i++){
        	sensorsRangeMin[i] = 0;
			sensorsRangeMax[i] = 1023;
			// add a configuration row for each parameter input
        	oscCongig(oscPanel, oscAddresses[i], oscMinVals[i], oscMaxVals[i], sensorsRangeMin[i], sensorsRangeMax[i], sensorN[i], i);
        }
        
        // create the network configuration options
        networkConfig(oscPanel);
        
        // application name 
        final JPanel appName = new JPanel();
        createAppName(appName);
        
        final JPanel uploadOpt = new JPanel();
        
        createUploadButton(uploadOpt);
        
        // the different UI elements are added to the main window
        guiFrame.add(oscPanel, BorderLayout.NORTH);
        guiFrame.add(uploadOpt,BorderLayout.CENTER);
        guiFrame.add(appName,BorderLayout.SOUTH);
        
        //make sure the JFrame is visible
        guiFrame.setVisible(true);
    }
}
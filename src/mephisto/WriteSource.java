/*
 *  Copyright (C) 2013 GRAME, Romain Michon, CCRMA - Stanford University
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, in part or in full are permitted. 
 *  This sample code is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package mephisto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * Customize a template Arduino fimrware with the parameters provided by the
 * user in the UI and write it in a temporary file.
 */
public class WriteSource {
	// global variables for the class
	static String[] oscAddresses;
	static float[] oscMinVals;
	static float[] oscMaxVals;
	static float[] sensorsMinVals;
	static float[] sensorsMaxVals;
	
	// init the global variables
	public static void initParams(){
		oscAddresses = new String[5];
        oscMinVals = new float[5];
        oscMaxVals = new float[5];
        sensorsMinVals = new float[5];
        sensorsMaxVals = new float[5];
	}
	
	/*
	 * generate and write the source
	 */
	public static void write(String[] add, float[] oscMin, float[] oscMax, 
			float[] sensorsMin, float[] sensorsMax, String SSID, 
			String PSWD, String UDP, int updateRate)
	{
		// initialize the parameters
		initParams();
		
		// set the parameters to be added to the source code of the Arduino firmware
		oscAddresses = add;
		oscMinVals = oscMin;
		oscMaxVals = oscMax;
		sensorsMinVals = sensorsMin;
		sensorsMaxVals = sensorsMax;
		
		// create the buffers for input and output files
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		// turns the String UDP into an int UDP
		int[] udp = new int[4];
		for(int i=0;i<4;i++){
			if(i==3) udp[i] = Integer.valueOf(UDP);
			else{
				udp[i] = Integer.valueOf(UDP.substring(0, UDP.indexOf(".")));
				UDP = UDP.substring(UDP.indexOf(".")+1);
			}
		}
		
        try {      	
            // file readers and writers
        	File inFile = new File(System.getProperty("user.dir") + "/utility/mephisto.ino");
            File outFile = new File(System.getProperty("user.dir") + "/utility/mephisto/src/mephisto.ino");
            
            reader = new BufferedReader(new FileReader(inFile));
            writer = new BufferedWriter(new FileWriter(outFile));
            
            // get the number of lines of the input file
            int lines = 0, insertLine = 0;
            while(reader.readLine() != null) lines++;
            reader.close();
           
            // retrieve the parameters and associate them to the right firmware variables
            reader = new BufferedReader(new FileReader(inFile));
            String[] theLine = new String[lines+1];
            String[] oscParams = new String[32];
            
            oscParams[0] = "oscParams.address[0] = \"" + oscAddresses[0] + "\";";
            oscParams[1] = "oscParams.address[1] = \"" + oscAddresses[1] + "\";";
            oscParams[2] = "oscParams.address[2] = \"" + oscAddresses[2] + "\";";
            oscParams[3] = "oscParams.address[3] = \"" + oscAddresses[3] + "\";";
            oscParams[4] = "oscParams.address[4] = \"" + oscAddresses[4] + "\";";
            oscParams[5] = "oscParams.minVal[0] = " + oscMinVals[0] + ";";
        	oscParams[6] = "oscParams.minVal[1] = " + oscMinVals[1] + ";";
        	oscParams[7] = "oscParams.minVal[2] = " + oscMinVals[2] + ";";
        	oscParams[8] = "oscParams.minVal[3] = " + oscMinVals[3] + ";";
        	oscParams[9] = "oscParams.minVal[4] = " + oscMinVals[4] + ";";
        	oscParams[10] = "oscParams.maxVal[0] = " + oscMaxVals[0] + ";";
        	oscParams[11] = "oscParams.maxVal[1] = " + oscMaxVals[1] + ";";
        	oscParams[12] = "oscParams.maxVal[2] = " + oscMaxVals[2] + ";";
        	oscParams[13] = "oscParams.maxVal[3] = " + oscMaxVals[3] + ";";
        	oscParams[14] = "oscParams.maxVal[4] = " + oscMaxVals[4] + ";";
        	oscParams[15] = "oscParams.sensorsMinVal[0] = " + sensorsMinVals[0] + ";";
        	oscParams[16] = "oscParams.sensorsMinVal[1] = " + sensorsMinVals[1] + ";";
        	oscParams[17] = "oscParams.sensorsMinVal[2] = " + sensorsMinVals[2] + ";";
        	oscParams[18] = "oscParams.sensorsMinVal[3] = " + sensorsMinVals[3] + ";";
        	oscParams[19] = "oscParams.sensorsMinVal[4] = " + sensorsMinVals[4] + ";";
        	oscParams[20] = "oscParams.sensorsMaxVal[0] = " + sensorsMaxVals[0] + ";";
        	oscParams[21] = "oscParams.sensorsMaxVal[1] = " + sensorsMaxVals[1] + ";";
        	oscParams[22] = "oscParams.sensorsMaxVal[2] = " + sensorsMaxVals[2] + ";";
        	oscParams[23] = "oscParams.sensorsMaxVal[3] = " + sensorsMaxVals[3] + ";";
        	oscParams[24] = "oscParams.sensorsMaxVal[4] = " + sensorsMaxVals[4] + ";";
        	oscParams[25] = "oscParams.rate = " + updateRate + ";";
        	oscParams[26] = "oscParams.udp[0] = " + udp[0] + ";";
        	oscParams[27] = "oscParams.udp[1] = " + udp[1] + ";";
        	oscParams[28] = "oscParams.udp[2] = " + udp[2] + ";";
        	oscParams[29] = "oscParams.udp[3] = " + udp[3] + ";";
        	oscParams[30] = "oscParams.ssid = \"" + SSID + "\";";
        	oscParams[31] = "oscParams.pswd = \"" + PSWD + "\";";
        	
            // localize the line number in the input file where the parameters should be inserted
        	lines = 0;
            while((theLine[lines] = reader.readLine()) != null){ 
            	if(theLine[lines].equals("<<< params >>>")) insertLine = lines;
            	lines++;
            }
            reader.close();
            
            // generate and write the customized firmware
            lines = 0;
            while(theLine[lines] != null){
            	if(insertLine == lines){ 
            		for(int i=0; i<32; i++) writer.write(oscParams[i] + "\n");
            	}
            	else writer.write(theLine[lines] + "\n");
            	lines++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}
	
	public WriteSource(){}

}

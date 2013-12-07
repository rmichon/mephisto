/*
 *  Copyright (C) 2013 GRAME, Romain Michon, CCRMA - Stanford University
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, in part or in full are permitted. 
 *  This sample code is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package mephisto;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Compile the source generated source code and upload it to the Arduino.
 * This is all done by a bash script: uploadToMep
 */
public class CompileAndUpload {
	public static void messageWindow(String message){
		final JFrame guiFrame = new JFrame();
		guiFrame.setTitle("Warning");
		if(message == "Succes!") guiFrame.setSize(200,100);
		else guiFrame.setSize(500,100);
		guiFrame.setLocationRelativeTo(null);
		guiFrame.setVisible(true);
		
		JPanel messageArea = new JPanel();
		JLabel theMessage = new JLabel();
		theMessage.setText(message);
		messageArea.add(theMessage);
		
		JPanel buttonArea = new JPanel();
		JButton closeButton = new JButton("OK");
		
		closeButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
            	guiFrame.dispose();
            }
        });
		
		buttonArea.add(closeButton);
		guiFrame.add(messageArea,BorderLayout.NORTH);
		guiFrame.add(buttonArea,BorderLayout.SOUTH);
	}
	public static int execShellCmd(String cmd) {
		int returnValue = 0;
        try {  
            Runtime runtime = Runtime.getRuntime();  
            Process process = runtime.exec(new String[] { "/bin/bash", "-c", cmd });  
            
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));  
            String line = "";  
            
            while ((line = buf.readLine()) != null) {  
                System.out.println(line);
                if(line.equals("Guessing serial port ... FAILED")) returnValue = 1;
                else if(returnValue < 0) returnValue = 0;
                
            } 
            
        } catch (Exception e) {  
            System.out.println(e);  
        }  
        return returnValue;
    }
	
	public static void doIt(){
		int result;
		result = execShellCmd(System.getProperty("user.dir") + "/utility/uploadToMep");
		if(result == 0) messageWindow("Success!");
		else if(result == 1) messageWindow("Couldn't find a mephisto device connected to this computer.");
	}
}

/*
 *  Copyright (C) 2013 GRAME, Romain Michon, CCRMA - Stanford University
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, in part or in full are permitted. 
 *  This sample code is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package mephisto;

public class Mephisto {
    public static void main(String[] args) {
        new GUI();
        new WriteSource();
        new CompileAndUpload();
    }
}

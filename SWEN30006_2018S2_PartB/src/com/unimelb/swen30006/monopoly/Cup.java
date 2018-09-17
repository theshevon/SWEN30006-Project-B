package com.unimelb.swen30006.monopoly;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is created based on case study of Monopoly of "Applying UML and Patterns, 3rd edition by Craig Larman".
 * For demonstration on subject SWEN30006 at The University of Melbourne 
 * 
 * This class is proposed by Pure Fabrication, and holds a collection of many Die objects;
 * 
 * The behavior is coded based on Figure 25.8 and 25.9
 * 
 * New Feature:
 * 1. made a global variable and accessible to Property Squares
 * 
 * @author 	Yunzhe(Alvin) Jia
 * @version 2.0
 * @since 	2016-07-19
 *
 */
public class Cup {
	
	private static List<Die> dice;
	private static int total;
	
	private static FileWriter outStream = null;
	
	public static void initialize(int numOfDice) {
		dice = new ArrayList<Die>(numOfDice);
		for (int i = 0; i < numOfDice; i++ ){
			dice.add(new Die());
		}
		try {
			outStream = new FileWriter("monopolyRollsOut.txt");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * roll all the dice
	 */
	public static void roll(){
		try {
			total = 0;
			for (Die die:dice){
				die.roll();
				total += die.getFaceValue();
				outStream.write(Integer.toString(die.getFaceValue()));
				outStream.append('\n');
			}
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static int getTotal(){
		return total;
	}

}

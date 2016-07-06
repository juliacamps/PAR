/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author Jakie
 */
public final class Planner {
    /**
     * @param args the first param refers to the path of the settings file, and second one refers to the logfile path.
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println(df.format(dateobj));
        String filePath;// = "C:\\Users\\Jakie\\Desktop\\Dropbox\\MASTER\\PAR\\LAB\\PLANNER\\Tests\\test3.txt";
        String filelogPath;// = "C:\\Users\\Jakie\\Desktop\\Dropbox\\MASTER\\PAR\\LAB\\PLANNER\\Tests\\logfile1.txt";
        if(args.length != 2){
            Scanner scanner = new Scanner(System.in);
            System.out.print("For the data files, better use txt plain text files, in UTF-8, without special characters.\n\nEnter the settings file path  \n(remember to write the file name and extention within the path): ");
            System.out.flush();
            filePath = scanner.nextLine();
            System.out.print("Enter the logfile file path \n(if this file does not exist, it will be created): ");
            System.out.flush();
            filelogPath = scanner.nextLine();
        }
        else{
            filePath = args[0];
            filelogPath = args[1];
        }
        Path logfile = Paths.get(filelogPath);
        
        Files.write(logfile, ("\n\n\n"+df.format(dateobj)+" :: Starts Planner Code\n===================\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        String filename = filePath;
        Building environment = initialize(filename, logfile);
        String plan = environment.solvePlan(logfile);
        byte data[] = plan.getBytes();
        Files.write(logfile, data, StandardOpenOption.APPEND);
        System.out.print(plan);
    }
    public static Building initialize(String filename, Path logfile) throws FileNotFoundException, IOException {
        Files.write(logfile, "Start initialitzation\n-------------------\n".getBytes(), StandardOpenOption.APPEND);
        ArrayList<String> indexes = new ArrayList<>();
        String fullFile;
        try(BufferedReader br = new BufferedReader(new   FileReader(filename))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine().replaceAll("\\s","").replaceAll("\\t", "");

            while (line != null) {
                if(line.startsWith("Offices=")){
                        indexes.add("Offices=");
                    }
                else{
                    if(line.startsWith("Boxes=")){
                        indexes.add("Boxes=");
                    } 
                    else{
                        if(line.startsWith("InitialState=")){
                            indexes.add("InitialState=");
                        }
                        else{
                            if (line.startsWith("GoalState=")){
                                indexes.add("GoalState=");
                            }
                        }
                    }
                }
                
                sb.append(line);
                line = br.readLine();
            }
            fullFile = sb.toString();

        }
        
        String[] boxNames = null;
        String[] officeNames = null;
        String[] initialState = null;
        String[] finalState = null;
        
        
        for (int i=0;i<indexes.size();i++) {
            String indexed;
            String index = indexes.get(i);
            if (i == indexes.size()-1) indexed = fullFile.substring(fullFile.indexOf(index)).replace(index,"");
            else indexed = fullFile.substring(fullFile.indexOf(index),fullFile.indexOf(indexes.get(i+1))).replace(index,"");
            switch (index){
                case "Boxes=":
                    boxNames = indexed.split(",");
                    break;
                case "Offices=":
                    officeNames = indexed.split(",");
                    break;
                case "InitialState=":
                    initialState = indexed.split(";");
                    break;
                case "GoalState=":
                    finalState = indexed.split(";");
                    break;
            }
        }
        
        
        finalState = addCleanConditions(finalState, officeNames);
        
        int[][] adjMat = getAdjMat();
        Files.write(logfile, "Sucsesfully initialized\n-------------------\n".getBytes(), StandardOpenOption.APPEND);
        return Building.CreateEnvironment(officeNames, boxNames, initialState, finalState, adjMat);
    }
    
    public static String[] addCleanConditions(String[] conditions, String[] officeNames){
        String[] newConds = new String[officeNames.length + conditions.length];
        for(int i=0; i<newConds.length; i++) 
            if (i<conditions.length) newConds[i] = conditions[i];
            else newConds[i] = "Clean("+officeNames[i-conditions.length]+")";
        return newConds;
    }
    
    
    
    public static String[] getBuildingSettings(){   
        String [] matrix =  {"Clean-office(o)|PREC:robot-location(o);dirty(o);"+
                "empty(o)|Add:clean(o)|Delete:dirty(o)","Move(o1,o2)|PREC:"+
                "robot-location(o1);adjacent(o1,o2)|Add:robot-location(o2)|Delete:"+
                "robot-location(o1)","Push(b,o1,o2)|PREC:robot-location(o1);"+
                "adjacent(o1,o2);box-location(b,o1);empty(o2)|Add:robot-location(o1);"+
                "box-location(b,o2);empty(o1)|Delete:robot-location(o1);"+
                "box-location(o1);empty(o2)"};
        return matrix;
    }

    private static int[][] getAdjMat() {
        int[][] adjMat =  new int[][]{{0,1,2,1,2,3,2,3,4},
                                    {1,0,1,2,1,2,3,2,3},
                                    {2,1,0,3,2,1,4,3,2},
                                    {1,2,3,0,1,2,1,2,3},
                                    {2,1,2,1,0,1,2,1,2},
                                    {3,2,1,2,1,0,3,2,1},
                                    {2,3,4,1,2,3,0,1,2},
                                    {3,2,3,2,1,2,1,0,1},
                                    {4,3,2,3,2,1,2,1,0}};
        return adjMat;
    }
    
}

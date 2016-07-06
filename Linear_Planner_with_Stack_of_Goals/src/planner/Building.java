/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakie
 */
public class Building {
    
    private List<Office> offices;
    private List<Box> boxes;
    private Robot robot;
    private List<Condition> status;
    private List<Operator> operators;
    private int[][] distMat;
    private String[] finalstate;
    private Path logfile;
    
    public Building(List<Office> newOffices, List<Box> newBoxes, int[][] adjMat){
        offices = newOffices;
        boxes = newBoxes;
        robot = new Robot();
        distMat = adjMat;
        operators = Operator.createOperators();
    }
    
    private static List<Office> createOffices(String[] officeNames){
        List<Office> newOffices = new ArrayList<>();
        int i=0;
        for (String offName : officeNames) {
            i++;
            if ((i != 4) && (i != 6)) {
                newOffices.add(new Office(offName,i));
            }
            else {
                if (i == 4) newOffices.add(new Office(offName,6)); 
                else newOffices.add(new Office(offName,4));
            }
        }
        return newOffices;
    }
    
    private static List<Box> createBoxes(String[] boxNames){
        List<Box> newBoxes = new ArrayList<>();
        for (String boxName : boxNames) newBoxes.add(new Box(boxName));
        return newBoxes;
    }
    
    private static List<Condition> createConds(String[] states, Building newBuilding){
        List<Condition> newStatus = new ArrayList<>();
        for (String condName : states){
            newStatus.add(Condition.createCond(newBuilding, condName));
        }
        newStatus.addAll(newBuilding.getAjacent());
        return newStatus;
    }
    
    private static Building buildBuilding(String[] officeNames, String[] boxNames, 
             int[][] adjMat, String[] initialState){
        List<Office> newOffices = createOffices(officeNames);
        List<Box> newBoxes = createBoxes(boxNames);
        Building newBuilding = new Building(new ArrayList<>(newOffices), 
                new ArrayList<>(newBoxes), adjMat);
        List<Condition> newStatus = createConds(initialState, newBuilding);
        newBuilding.setStatus(newStatus);
        return newBuilding;
    }
    
    public static Building CreateEnvironment(String[] officeNames, String[] boxNames, 
             String[] initialState, String[] finalState, int[][] adjMat){
        
        boolean validSettings = false;
        Building newBuilding = buildBuilding(officeNames,boxNames,adjMat,initialState);
        try{
            validSettings = newBuilding.build("initial");
        }
        catch(Error e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        
        if (validSettings){
            Building auxBuilding = buildBuilding(officeNames,boxNames,adjMat,finalState);
            try{
                validSettings = auxBuilding.build("final");
            }
            catch(Error e){
                System.out.println(e.getMessage());
                System.exit(2);
            }

            if (validSettings) newBuilding.initialize(finalState);
        }

        for (Operator op : newBuilding.getOperators()) op.setRobot(newBuilding.getRobot());
        newBuilding.finalstate = finalState;
        return newBuilding;
    }
    
    private void initialize(String[] finalState){
        List<Condition> newGoals = createConds(finalState, this);
        getRobot().setFinalState(newGoals, this);
    }
    
    private boolean build(String part) throws Error{
        boolean validSettings = true;
        Iterator<Condition> iter = getStatus().iterator();
        Condition cond = null, auxCond = null;
        while (iter.hasNext() && validSettings) {
            auxCond = iter.next();
            if (auxCond != null){
                cond = auxCond;
                validSettings = !((!(cond.applyCond())) && (cond.getType().equals("Box-location") || 
                        cond.getType().equals("Robot-location") || cond.getType().equals("Adjacent")));
            }
        }
        if (!validSettings) {
            String msg;
            if (cond == null) msg = "empty "+part+" conditions list!";
            else msg = "condition: "+cond.toString()+" is not supported when reached, may"+
                    " be incompatible with previous conditions!";
            throw new Error("Incoherence in the "+part+" conditions "+msg);
        }
        return validSettings;
    }
    

    public String solvePlan(Path logfile){
        this.logfile = logfile;
        List<Operator> plan = null;
        try {
            plan = robot.solvePlan();
        } catch (IOException ex) {
            Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ret = "-----------------\n<<<<<<<     The solution plan is composed by "+plan.size()+" operators     >>>>>>>>>\nThe retreived plan is:\n";
        for(int i=0; i<plan.size(); i++) ret+="O"+(i+1)+": "+plan.get(i).toString()+"\n";
        ret +="----------------\nAnd the final state is:\n";
        int i=0;
        String strStatus = "";
        for(Condition cond:getStatus()) {
            i+=1;
            if(!cond.getType().equals("Adjacent")){
                strStatus+=cond.toString()+";";
                if (i>5){
                    i=0;
                    strStatus+="\n";
                }
            }
        }
        String[] matches = new String[finalstate.length];
        for (i=0; i<matches.length; i++) matches[i] = "0";
        String[] auxStrs = strStatus.split(";");
        for(i=0; i<finalstate.length; i++) for(String auxStr: auxStrs) if(finalstate[i].trim().equals(auxStr.trim())) matches[i] = "1"; 
        for (i=0;i<matches.length;i++) System.out.println(matches[i]+" : "+finalstate[i].trim());
        ret += strStatus;
        return ret;
    }
    
    public List<Condition> getAjacent(){
        List<Condition> ret = new ArrayList<>();
        for (int i=0;i<offices.size();i++){
            for (int j=0;j<offices.size();j++){
                if(getDistMat()[i][j] == 1) ret.add(Condition.createCond(this, "Adjacent("+offices.get(i)+","+offices.get(j)+")"));
            }
        }
        return ret;
    }
    
    public int[][] getDistMat(){
        return this.distMat;
    }
    
    public Office getOffice(String name){
        Office ret = null;
        for(Office off : getOffices()) if(off.getName().equals(name)) ret = off;
        return ret;
    }
    
    public Box getBox(String name){
        Box ret = null;
        for(Box b : getBoxes()) if(b.getName().equals(name)) ret = b;
        return ret;
    }
    
    public List<Operator> getAddOperators(Condition cond){
        //Handel moving operators for conditions, adjacent. Maybe changing the preconditions on the push and move operator,
        //having partial goals, like partial paths ... think about it and draw a valid codification
        List<Operator> ret = new ArrayList<>();
        List<Operator> auxOps;
        for(Operator op : getOperators()) {
            for(Condition c : op.getAdd()){
                if(c.getType().equals(cond.getType())){
                    Operator auxOp = op.fulfill(cond);
                    if (auxOp.isPartial()){
                        auxOps = getPossibleOperators(auxOp);
                        ret.addAll(auxOps);
                    }
                    else ret.add(auxOp);
                }
            }
        }
        return ret;
    }
    
    public boolean checkCondition(Condition cond){
        return status.contains(cond);
    }
    
    public boolean checkConditions(List<Condition> conds){
        boolean ret = true;
        for(Condition cond : conds) ret = (ret && checkCondition(cond));
        return ret;
    }
    
    public List<Condition> parseState2Conditions(List<String> states){
        List<Condition> conditions = new ArrayList<>();
        for (String state : states)
            conditions.add(Condition.createCond(this, state));
        return conditions;
    }
    
    public String[] parseStringCondFormat(String conditions){
        return conditions.split(";");
    }

    public List<Operator> getPossibleOperators(Operator operator) {
        List<Operator> ret = new ArrayList<>();
        switch(operator.getType()){
            case "Clean":
                ret = operator.completeOp(getOffices());
                break;
            case "Push":
                operator = (PushOperator) operator;
                ret = operator.completeOps(getBoxes(), getOffices());
                break;
            case "Move":
                ret = operator.completeOp(getOffices());
                break;
        }
        return ret;
    }


    public List<Office> getOffices() {
        return offices;
    }

    public void setOffices(List<Office> offices) {
        this.offices = offices;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public Robot getRobot() {
        return robot;
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public List<Condition> getStatus() {
        return status;
    }

    public void setStatus(List<Condition> status) {
        this.status = status;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    void pushOp(Box b, Office o1, Office o2) {
        getRobot().move(o1, o2);
        b.move(o1, o2);
    }

    void deleteConditions(List<Condition> delete) {
        getStatus().removeAll(delete);
    }

    void addConditions(List<Condition> add) {
        getStatus().addAll(add);
    }

    void cleanOp(Office o) {
        o.clean();
    }

    void moveOp(Office o1, Office o2) {
        getRobot().move(o1, o2);    
    }
    
    public int[] getDistArray(int i){return getDistMat()[i];}
    
    public Office getOffice(int Id){
        Office ret = null;
        for (Office off : getOffices()) if(off.getId() == Id) ret = off;
        return ret;
    }

    public Office nextStep(Office A, Office B) {
        Office ret = null;
        int[] dists = getDistArray(A.getId()-1);
        for (int i=0;i<dists.length;i++){
            if(dists[i]==1){
                if(ret == null) ret = getOffice(i+1);
                else if(getDistMat()[i][B.getId()-1] < getDistMat()[ret.getId()-1][B.getId()-1]) ret = getOffice(i+1);
                else if((getDistMat()[i][B.getId()-1] == getDistMat()[ret.getId()-1][B.getId()-1])&&
                        (!ret.isEmpty()) && getOffice(i+1).isEmpty()) ret = getOffice(i+1);
            }
        }
        return ret;
    }

    private String paintClean(Office o){
        String ret = " ";
        if(!o.isClean()) ret = "#";
        return ret;
    }
    
    private String paintBox(Office o){
        String ret = " ";
        if(!o.isEmpty()) ret = o.getBoxIn().getName();
        return ret;
    }
    
    private String paintRobot(Office o){
        String ret = " ";
        if(o.getRobot()!=null) ret = "@";
        return ret;
    }
    
    private String paintOffice(Office o){
        String ret = "";
        ret = paintClean(o)+paintRobot(o)+paintBox(o);
        return ret;
    }
    
    private String paintRow(int i){
        String ret = "";
        Office o1 = null,o2=null,o3=null;
        switch(i){
            case 1:
                o1 = getOffice("o1");
                o2 = getOffice("o2");
                o3 = getOffice("o3");
                break;   
            case 2:
                o1 = getOffice("o4");
                o2 = getOffice("o5");
                o3 = getOffice("o6");
                break;   
            case 3:
                o1 = getOffice("o7");
                o2 = getOffice("o8");
                o3 = getOffice("o9");
                break;   
        }
        return "| "+paintOffice(o1)+" | "+paintOffice(o2)+" | "+paintOffice(o3)+" |\n";
    }
    
    String paintLine(){return "+-----+-----+-----+\n";}
    
    public void paint() throws IOException {
        String visualize = "\n\n\n\n"+paintLine()+paintRow(1)+paintLine()+paintRow(2)+paintLine()+paintRow(3)+paintLine();
        System.out.println(visualize);
        Files.write(logfile, visualize.getBytes(), StandardOpenOption.APPEND);
    }
    
    
}

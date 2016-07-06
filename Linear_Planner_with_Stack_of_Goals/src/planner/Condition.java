/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.util.List;

/**
 *
 * @author Jakie
 */
public abstract class Condition {
    protected String type;
    protected int priority = 0;

    public int getPriority(){return priority;}
    
    public static Condition createCond(Building controller, String condStr) {   
        Condition newCond = null;
        condStr = condStr.replace("(", ";");
        condStr = condStr.replace(",", ";");
        condStr = condStr.replace(")", "");
        String[] parts = condStr.split(";");
        final String newType = parts[0].trim();
        String strOff = null;
        Office newOff = null;
        
        switch (newType){
            case "Clean": case "Empty": case "Dirty": case "Robot-location":
                strOff = parts[1];
                newOff = controller.getOffice(strOff);
                switch (newType){
                    case "Clean":
                        newCond = new CleanCondition(newType, newOff);
                        break;
                    case "Empty":
                        newCond = new EmptyCondition(newType, newOff);
                        break;
                    case "Dirty":
                       newCond = new DirtyCondition(newType, newOff);
                        break;
                    case "Robot-location":
                        newCond = new RobotCondition(newType, newOff, controller.getRobot());
                        break;
                }
                break;
            case "Box-location":
                String strBox = parts[1];
                strOff = parts[2];
                Box newBox = controller.getBox(strBox);
                newOff = controller.getOffice(strOff);
                newCond = new BoxCondition(newType, newBox, newOff);
                break;
            case "Adjacent":
                String strOff1 = parts[1];
                String strOff2 = parts[2];
                Office newOff1 = controller.getOffice(strOff1);
                Office newOff2 = controller.getOffice(strOff2);
                newCond = new AdjacentCondition(newType, newOff1, newOff2);
                break;
        }
        return newCond;
    }
    
    public abstract Office getOffice();

    
    @Override
    public boolean equals(Object o) {
        return ((o != null) && this.toString().equals(o.toString()));
    }

 
    @Override
    public abstract String toString();

    public String getType() {
        return type;
    }
    
    public abstract boolean isPartial();
    
    public abstract Object getPartial();
    
    public abstract Condition completePartial(Object newO);
    
    public abstract Object getActor(String actName);

    public abstract boolean applyCond();
 
}

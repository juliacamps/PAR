/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

/**
 *
 * @author Jakie
 */
public class RobotCondition extends Condition {
    
    private Office office;
    private Robot robot;
    
    public RobotCondition(String type, Office office, Robot robot){
        super.type = type;
        this.office = office;
        this.robot = robot;
        super.priority = 6;
    }
    
    public RobotCondition(Office office, Robot robot){
        super.type = "Robot-location";
        this.office = office;
        this.robot = robot;
        super.priority = 6;
    }

    @Override
    public String toString() {
        String oNam = "";
        if(office!=null)oNam = office.getName();
        return type+"("+ oNam +")";
    }

    @Override
    public boolean isPartial(){
        return false;
    }

    @Override
    public Object getActor(String actName){
        if (!actName.equals("robot")) return this.office;
        else return this.robot;
    }

    @Override
    public Object getPartial() {
        Office ret = null;
        if (this.office.isPartial()) ret = this.office;
        return ret;    
    }

    @Override
    public Condition completePartial(Object newO) {
        if (this.office.isPartial()) this.office = (Office) newO;
        return this;
    }

    @Override
    public boolean applyCond() {
        robot.setInOffice(office);
        office.putRobot(robot);
        return true;
    }

    @Override
    public Office getOffice() {
        return office;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jakie
 */
public class MoveOperator extends Operator{
    private Office o1 = null;
    private Office o2 = null;

    public MoveOperator(Office o1, Office o2){
        this.o1 = o1;
        this.o2 = o2;
        super.setPriority(2);
        super.setType("Move");
        List<Condition> newPre = new ArrayList<>();
        newPre.add(new RobotCondition(o1, super.getRobot()));
        setPreconditions(newPre);
        
        List<Condition> newAdd = new ArrayList<>();
        newAdd.add(new RobotCondition(o2,super.getRobot()));
        setAdd(newAdd);
        
        List<Condition> newDel = new ArrayList<>();
        newDel.add(new RobotCondition(o1,super.getRobot()));
        setDelete(newDel);
    }

    public Office getO1() {
        return o1;
    }

    public void setO1(Office o1) {
        this.o1 = o1;
    }

    public Office getO2() {
        return o2;
    }
    
    
    @Override
    public Operator fulfill(Condition cond){
        Operator ret = null;
        if (cond.getType().equals("Robot-location")) {
            ret = setO2(super.getRobot().getInOffice(),super.getRobot().NextOffice((Office) cond.getActor("o2")));
        }
        return ret;
    }
    
    @Override
    public String toString(){
        return getType()+"("+o1.getName()+","+o2.getName()+")";
    }
    
    private Operator setO2(Office o1,Office o2){
        Operator newOp = new MoveOperator(o1, o2);
        newOp.setId(super.getId());
        newOp.setName(super.getName());
        return newOp;
    }
    
    public List<String> getPartialMembers(){
        List<String> ret = new ArrayList<>();
        if(getO1()==null) ret.add("O1");
        if(getO2()==null) ret.add("O2");
        return ret;
    }
    
    @Override
    public String getPartial(){
        return "Office";
    }
    
    @Override
    public boolean isPartial(){
        return ((o1==null)||(o2==null));
    }
    
    @Override
    public List<Operator> completeOp(List<Office> l){
        List<Operator> ret = new ArrayList<>();
        List<String> partials = getPartialMembers();
        if (partials.size()==2){
            for(Office off1 : l){
                for(Office off2 : l){
                    if (! off1.equals(off2)) ret.add(new MoveOperator(off1, off2));
                }
            }
        }
        else{
            switch(partials.get(0)){
                case "O1":
                    for(Office off1 : l) ret.add(new MoveOperator(off1, getO2()));
                    break;
                case "O2":
                    for(Office off2 : l) ret.add(new MoveOperator(getO1(), off2));
                    break;
            }
        }  
        return ret;
    }
    
    @Override
    public void specificExecuteOperator(Building controller){
        controller.moveOp(o1,o2);
    }
    
    @Override
    public List<Operator> completeOps(List<Box> boxes, List<Office> offices){
        return new ArrayList<>();
    }
}

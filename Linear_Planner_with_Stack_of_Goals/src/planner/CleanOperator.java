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
public class CleanOperator extends Operator{
    private Office o;

    public CleanOperator(Office o){
        this.o = o;
        super.setPriority(1);
        super.setType("Clean");
        List<Condition> newPre = new ArrayList<>();
        newPre.add(new EmptyCondition(o));
        newPre.add(new DirtyCondition(o));
        newPre.add(new RobotCondition(o,super.getRobot()));
        setPreconditions(newPre);
        
        List<Condition> newAdd = new ArrayList<>();
        newAdd.add(new CleanCondition(o));
        setAdd(newAdd);
        
        List<Condition> newDel = new ArrayList<>();
        newDel.add(new DirtyCondition(o));
        setDelete(newDel);
    }
    
    @Override
    public Operator fulfill(Condition cond){
        Operator ret = null;
        if (cond.getType().equals("Clean")) ret = setO((Office) cond.getActor("o"));
        return ret;
    }
    
    @Override
    public String toString(){
        return getType()+"("+o.getName()+")";
    }
    
    private Operator setO(Office o){
        Operator newOp = new CleanOperator(o);
        newOp.setId(super.getId());
        newOp.setName(super.getName());
        return newOp;
    }
    
    @Override
    public String getPartial(){
        return "Office";
    }
    
    @Override
    public boolean isPartial(){
        return (o==null);
    }
    
    @Override
    public List<Operator> completeOp(List<Office> l){
        List<Operator> ret = new ArrayList<>();
        for(Office off : l) if(!off.getClean()) ret.add(new CleanOperator(off));
        return ret;
    }
    
    @Override
    public void specificExecuteOperator(Building controller){
        controller.cleanOp(o);
    }
    
    @Override
    public List<Operator> completeOps(List<Box> boxes, List<Office> offices){
        return new ArrayList<>();
    }
}
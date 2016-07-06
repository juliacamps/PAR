/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import com.sun.org.apache.xml.internal.serializer.ToHTMLSAXHandler;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jakie
 */
public class PushOperator extends Operator{
    private Box b;
    private Office o1;
    private Office o2;

    public PushOperator(Box b, Office o1, Office o2){
        this.b = b;
        this.o1 = o1;
        this.o2 = o2;
        super.setPriority(3);
        super.setType("Push");
        List<Condition> newPre = new ArrayList<>();
        newPre.add(new BoxCondition(b,o1));
        newPre.add(new RobotCondition(o1,super.getRobot()));
        newPre.add(new EmptyCondition(o2));
        newPre.add(new AdjacentCondition(o1,o2));
        setPreconditions(newPre);
        
        List<Condition> newAdd = new ArrayList<>();
        newAdd.add(new RobotCondition(o2,super.getRobot()));
        newAdd.add(new BoxCondition(b,o2));
        newAdd.add(new EmptyCondition(o1));
        setAdd(newAdd);
        
        List<Condition> newDel = new ArrayList<>();
        newDel.add(new BoxCondition(b,o1));
        newDel.add(new RobotCondition(o1,super.getRobot()));
        newDel.add(new EmptyCondition(o2));
        setDelete(newDel);
    }
    
    @Override
    public String toString(){
        return getType()+"("+b.getName()+","+o1.getName()+","+o2.getName()+")";
    }

    public Box getB() {
        return b;
    }

    public void setB(Box b) {
        this.b = b;
    }

    public Office getO1() {
        return o1;
    }

    public Office getO2() {
        return o2;
    }
    
    
    
    @Override
    public Operator fulfill(Condition cond){
        Operator ret = null;
        switch (cond.getType()){
            case "Empty":
                Office auxO = (Office) cond.getActor("o1");
                ret = setBO(auxO.getBoxIn(), auxO);
                break;
            case "Box-location":
                Box bo = (Box) cond.getActor("b");
                ret = setBO2(bo, bo.getInOffice(), super.getRobot().getController().nextStep(bo.getInOffice(), (Office) cond.getActor("o2")));
                break;
            case "Robot-location":
                ret = setBO2(super.getRobot().getInOffice().getBoxIn(), super.getRobot().getInOffice(),(Office) cond.getActor("o2"));
                break;
        } 
        return ret;
    }
    
    private Operator setBO(Box b, Office o1){
        Operator newOp = new PushOperator(b, o1, o2);
        newOp.setId(super.getId());
        newOp.setName(super.getName());
        return newOp;
    }
    
//    private Operator setO(Office o1, Office o2){
//        Operator newOp = new PushOperator(b, o1, o2);
//        newOp.setId(super.getId());
//        newOp.setName(super.getName());
//        return newOp;
//    }
    
    private Operator setBO2(Box b, Office o1, Office o2){
        Operator newOp = new PushOperator(b, o1, o2);
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
        return ((b==null)||(o1==null)||(o2==null));
    }
    
    @Override
    public List<Operator> completeOp(List<Office> l){
        List<Operator> ret = new ArrayList<>();
        List<String> partials = getPartialMembers();
        if (partials.size()==2){
            for(Office off1 : l){
                for(Office off2 : l){
                    if (!(off1.equals(off2))) ret.add(new PushOperator(getB(), off1, off2));
                }
            }
        }
        else{
            switch(partials.get(0)){
                case "O1":
                    for(Office off1 : l) ret.add(new PushOperator(getB(), off1, getO2()));
                    break;
                case "O2":
                    for(Office off2 : l) ret.add(new PushOperator(getB(), getO1(), off2));
                    break;
            }
        }  
        return ret;
    }
    
    @Override
    public List<Operator> completeOps(List<Box> boxes, List<Office> offices){
        List<Operator> ret = new ArrayList<>();
        if ((b == null) && (o1 != null) && (o2 != null)) {
            for(Box bo:boxes) if(checkParams(bo,o1,o2)) ret.add(new PushOperator(bo, o1, o2));
        }
        else if((b != null) && (o1 == null) && (o2 != null)){
            for(Office of:offices) if(checkParams(b,of,o2)) ret.add(new PushOperator(b, of, o2));
        }
        else if((b != null) && (o1 != null) && (o2 == null)){
            for(Office of:offices) if(checkParams(b,o1,of)) ret.add(new PushOperator(b, o1, of));
        }
        else if((b == null) && (o1 == null) && (o2 != null)){
            for(Office of:offices) for (Box bo:boxes) if(checkParams(bo,of,o2)) ret.add(new PushOperator(bo, of, o2));
        }
        else if((b == null) && (o1 != null) && (o2 == null)){
            for(Office of:offices) for (Box bo:boxes) if(checkParams(bo,o1,of)) ret.add(new PushOperator(bo, o1, of));
        }
        return ret;
    }
    
    private boolean checkParams(Box b, Office o1, Office o2){
        return (((b!=null)&&(o1!=null)&&(o2!=null))&&((b.getInOffice() == o1)&&(o1.getBoxIn()==b)&&(o1!=o2)&&(o1.isAdjacent(o2))));
    }
    
    
    @Override
    public void specificExecuteOperator(Building controller){
        controller.pushOp(b,o1,o2);
    }
}
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
public class EmptyCondition extends Condition {
    
    private Office office;
    
    public EmptyCondition(String type, Office office){
        super.type = type;
        this.office = office;
        super.priority = 5;
    }
    
    public EmptyCondition(Office office){
        super.type = "Empty";
        this.office = office;
        super.priority = 5;
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
        return this.office;
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
        return office.removeBox();
    }
    
    @Override
    public Office getOffice(){
        return office;
    }
    
}

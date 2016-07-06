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
public class AdjacentCondition extends Condition {
    private Office office1;
    private Office office2;  
    
    public AdjacentCondition(String type, Office o1, Office o2){
        super.type = type;
        this.office1 = o1;
        this.office2 = o2;
        super.priority = 4;
    }
    
    public AdjacentCondition(Office o1, Office o2){
        super.type = "Adjacent";
        this.office1 = o1;
        this.office2 = o2;
        super.priority = 4;
    }
    

    @Override
    public String toString() {
        String o1Nam = "";
        String o2Nam = "";
        if(office1!=null)o1Nam = office1.getName();
        if(office2!=null)o2Nam = office2.getName();
        return type+"("+ o1Nam +","+o2Nam+")";
    }

    @Override
    public boolean isPartial(){
        return ((this.office1.isPartial()) || (this.office1.isPartial()));
    }

    @Override
    public Object getPartial(){
        Office ret;
        if (this.office1.isPartial()) ret = this.office1;
        else ret = this.office2;
        return ret;
    }

    @Override
    public Condition completePartial(Object newO){
        Office newOff = (Office) newO;
        if (this.office1.isPartial()) this.office1 = newOff;
        else if (this.office2.isPartial()) this.office1 = newOff;
        return this;
    }

    @Override
    public Object getActor(String actName){
        Object actor = null;
        switch(actName){
            case "o1":
                actor = this.office1;
                break;
            case "o2":
                actor = this.office2;
                break;
        }
        return actor;
    }

    @Override
    public boolean applyCond() {
        return office1.addAdjacent(office2);
    }
    
    @Override
    public Office getOffice(){
        return office2;
    }
}

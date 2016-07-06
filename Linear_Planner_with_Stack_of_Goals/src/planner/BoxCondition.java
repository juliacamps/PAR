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
public class BoxCondition extends Condition {
    private Office office;
    private Box box;
    
    public BoxCondition(String type, Box box, Office office){
        super.type = type;
        this.box = box;
        this.office = office;
        super.priority = 3;
    }
    
     public BoxCondition(Box box, Office office){
        super.type = "Box-location";
        this.box = box;
        this.office = office;
        super.priority = 3;
    }
     

    @Override
    public String toString() {
        String bNam = "";
        String oNam = "";
        if(box!=null)bNam = box.getName();
        if(office!=null)oNam = office.getName();
        return type+"("+ bNam + "," + oNam + ")";
    }

    @Override
    public boolean isPartial(){
        return ((this.office.isPartial()) || (this.box.isPartial()));
    }

    @Override
    public Object getPartial(){
        Object ret = null;
        if (this.office.isPartial()) ret = this.office;
        else if (this.box.isPartial()) ret = this.box;
        return ret;
    }

    @Override
    public Condition completePartial(Object newO){
        if (this.office.isPartial()) this.office = (Office) newO;
        else if (this.box.isPartial()) this.box = (Box) newO;
        return this;
    }

    @Override
    public Object getActor(String actName){
        Object actor = null;
        switch(actName){
            case "o": case "o1": case "o2":
                actor = this.office;
                break;
            case "b":
                actor = this.box;
                break;
        }
        return actor;
    }

    @Override
    public boolean applyCond() {
        return office.putBox(box);
    }
    
    @Override
    public Office getOffice(){
        return office;
    }
}

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
public abstract class Operator {

    private List<Condition> Preconditions = new ArrayList<>();
    private List<Condition> Add = new ArrayList<>();
    private List<Condition> Delete = new ArrayList<>();
    private int priority;
    private String type = "";
    private String name = "";
    private int id = 0;
    private Robot robot;
    

    public static List<Operator> createOperators(){
        List<Operator> ret = new ArrayList<>();
        ret.add(new CleanOperator(null));
        ret.add(new MoveOperator(null,null));
        ret.add(new PushOperator(null,null,null));
        return ret;
    }
    
    public abstract Operator fulfill(Condition cond);
    
    public boolean inAdd(Condition cond){
        return getAdd().contains(cond);
    }
    
    public boolean inDelete(Condition cond){
        return getDelete().contains(cond);
    }
    
    public void setRobot(Robot robot){
        this.robot = robot;
    }
    
    public Robot getRobot(){
        return this.robot;
    }
    
    //Setters And Getters
    public List<Condition> getPreconditions() {
        return Preconditions;
    }

    public void setPreconditions(List<Condition> Preconditions) {
        this.Preconditions = Preconditions;
    }

    public List<Condition> getAdd() {
        return Add;
    }

    public void setAdd(List<Condition> Add) {
        this.Add = Add;
    }

    public List<Condition> getDelete() {
        return Delete;
    }

    public void setDelete(List<Condition> Delete) {
        this.Delete = Delete;
    }

    public boolean isempty() {
        return ((getPreconditions().isEmpty())&&(getAdd().isEmpty())&&(getDelete().isEmpty()));
    }

    public abstract String getPartial();

    public abstract List<Operator> completeOp(List<Office> l);
    
    public abstract List<Operator> completeOps(List<Box> boxes, List<Office> offices);
    
    
    
    public List<Condition> completeConds(List<Condition> partialConds, Object o){
        List<Condition> ret = new ArrayList<>();
        for (Condition cond : partialConds) ret.add(cond.completePartial(o));
        return ret;
    }

    protected void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean executeOperator(Building controller){
        boolean ret = !controller.getStatus().containsAll(getAdd());
        if(ret){
            controller.deleteConditions(getDelete());
            controller.addConditions(getAdd());
            specificExecuteOperator(controller);
        }
        return ret;
    }
    
    public abstract void specificExecuteOperator(Building controller);
    public abstract boolean isPartial();
}

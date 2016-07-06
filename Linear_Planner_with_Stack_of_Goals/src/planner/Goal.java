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
public class Goal {
    private int id;
    private int target;
    private List<Condition> conditions = null;
    private Condition condition = null;
    private Operator operator = null;
    private String type = null;
    
    public Goal(List<Condition> conditions, int id, int target){
        this.conditions = conditions;
        this.type = "conditions";
        this.id = id;
        this.target = target;
    }
    
    public Goal(Condition condition, int id, int target){
        this.condition = condition;
        this.type = "condition";
        this.id = id;
        this.target = target;
    }
    
    public Goal(Operator operator, int id, int target){
        this.operator = operator;
        this.type = "operator";
        this.id = id;
        this.target = target;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
    
    @Override
    public boolean equals(Object o) {
        return ((o != null) && this.toString().equals(o.toString()));
    }
    
    @Override
    public String toString(){
        String ret = Integer.toString(getId());
//        switch (type){
//            case "conditions":
//                for (Condition cond : getConditions()) ret += cond.toString()+";";
//                break;
//            case "condition":
//                ret = getCondition().toString();
//                break;
//            case "operator":
//                ret = getOperator().toString();
//                break;
//        }
        return ret;
    }
    
    public boolean conflicts(Condition cond){
        boolean ret = false;
        switch (type){
            case "conditions":
                for(Condition c : getConditions()) ret = (ret || c.equals(cond));
                break;
            case "condition":
                ret = getCondition().equals(cond);
                break;
        }
        return ret;
    }
        
    public boolean evaluateGoal(List<Condition> status){
        boolean ret = false;
        switch (type){
            case "conditions":
                ret = status.containsAll(getConditions());
                break;
            case "condition":
                Condition c = getCondition();
                ret = (status.contains(getCondition())) || getCondition().getType().equals("Dirty");
                break;
            case "operator":
                ret = (status.containsAll(getOperator().getPreconditions()) || 
                        status.containsAll(getOperator().getAdd()) || (checkAdds(status, getOperator())));
                break;
        }
        return ret;
    }
    private boolean checkAdds(List<Condition> status, Operator op){
        List<Condition> auxConds = new ArrayList<>();
        boolean ret = false;
        for(Condition cond : op.getAdd()) if(!(op.getType().equals("Push") && cond.getType().equals("Robot-location"))) auxConds.add(cond);
        if (status.containsAll(auxConds)) ret = true;
        return ret;
    }
    
    

}

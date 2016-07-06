/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Jakie
 */
public class Robot extends MobilObject{
    private List<Operator> Plan = new ArrayList<>();
    private HashMap<Integer, ArrayList<Goal>> ActiveObjectives;
    private HashMap<Integer, ArrayList<Goal>> PendingObjectives;
    private Stack<Goal> GoalsStack = new Stack<>();
    private Building controller;
    private int goalCounter = 0;
    private int inteligence = 0;
    private int iterations = 0;
    private List<Condition> finalState;
    
    public Robot(){
        this.PendingObjectives = new HashMap<>();
        this.ActiveObjectives = new HashMap<>();
    }
    
//    public void completeInfo(List<Condition> finalState, Building controller){
//        int finalId = setGoalId();
//        finalState = sortHeuristic(finalState);
//        this.GoalsStack.push(new Goal(finalState,finalId,finalId));
//        for(Condition cond : finalState)this.GoalsStack.push(new Goal(cond,setGoalId(),finalId));
//        this.controller = controller;
//    }
    
//    public Robot(){
//        this.PendingObjectives = new HashMap<>();
//        this.ActiveObjectives = new HashMap<>();
//    }
    
    public void setFinalState(List<Condition> finalState, Building controller){
        this.finalState = finalState;
        int finalId = setGoalId();
        finalState = sortHeuristic(finalState);
        this.GoalsStack.push(new Goal(finalState,finalId,finalId));
        for(Condition cond : finalState)this.GoalsStack.push(new Goal(cond,setGoalId(),finalId));
        this.controller = controller;
    }


    public List<Operator> solvePlan() throws IOException{
//        System.out.println("Hello Im robot");
        boolean evaluation;
        Goal nextGoal;
        while(! getGoalsStack().empty() && iterations < 100){
            inteligence++;
            if(inteligence > 2000){
                iterations++;
                inteligence = 0;
            }
            if (!getController().getStatus().containsAll(finalState)){
                if (!this.getInOffice().isClean() && this.getInOffice().isEmpty()){
                    int newId = setGoalId();
                    nextGoal = new Goal(new CleanOperator(this.getInOffice()),newId,newId);
                }
                else{
                    nextGoal = getGoalsStack().pop();
                }
                evaluation = nextGoal.evaluateGoal(controller.getStatus());
                if (! evaluation){
                    int newTarget = nextGoal.getId();
                    getGoalsStack().add(nextGoal);
                    switch(nextGoal.getType()){
                        case "conditions":
    //                        System.out.println("conds: "+nextGoal.getId());
                            for(Condition cond : sortHeuristic(nextGoal.getConditions())) getGoalsStack().push(new Goal(cond,setGoalId(),newTarget));
                            break;
                        case "condition":
    //                        if(nextGoal.getCondition().getType().equals("Adjacent")) System.out.println("Estancat en adjacent!");
    //                        System.out.println("cond: "+nextGoal.getCondition().getType());
                            Operator newOp = getOperator(nextGoal.getCondition());
                            getGoalsStack().push(new Goal(newOp,setGoalId(),newTarget));
                            break;
                        case "operator":
    //                        System.out.println("operator: "+nextGoal.getId());
                            for(Condition cond : nextGoal.getOperator().getPreconditions()) getGoalsStack().push(new Goal(cond,setGoalId(),newTarget));
                            break;
                    }
                }
                else{
                    acomplishedGoal(nextGoal);
    //                System.out.println("Oh Yeah! "+nextGoal.getType()+nextGoal.getId());

                    if(nextGoal.getType().equals("operator")){
                            executeOperator(nextGoal.getOperator());
                            controller.paint();
                            if(inteligence > 1000){
                                iterations++;
                                inteligence = 0;
                            }
                            setAddsAsActives(nextGoal);
                    }
                    else setAsActive(nextGoal);
                }

            }
            else {
                iterations = 100;
            }
        }
        
//        System.out.println("Next goal: ");
        return Plan;
    }
    
    private void acomplishedGoal(Goal g){
        getPendingObjectives().remove(g.getId());
        getActiveObjectives().remove(g.getId());
        
    }
    
    private void setAsActive(Goal g){
        if (g.getId() != g.getTarget()) acomplishedIsActive(g);
    }
    
    private void setAddsAsActives(Goal g){
        if (g.getId() != g.getTarget()){
            for(Goal go: createAddGoals(g.getOperator().getAdd(), g.getTarget())) acomplishedIsActive(go);
        }
    }
    
    private List<Goal> createAddGoals(List<Condition> conds, int target){
        List<Goal> ret = new ArrayList<>();
        for (Condition c:conds) ret.add(new Goal(c, setGoalId(), target));
        return ret;
    }
    
    private void acomplishedIsActive(Goal g){
        if (getPendingObjectives().containsKey(g.getTarget())) {
            if (getPendingObjectives().get(g.getTarget()).contains(g)) getPendingObjectives().get(g.getTarget()).remove(g);
        }
        else if (getActiveObjectives().containsKey(g.getTarget())) getActiveObjectives().get(g.getTarget()).add(g);
        else {
            getActiveObjectives().put(g.getTarget(), new ArrayList<Goal>());
            getActiveObjectives().get(g.getTarget()).add(g);
        }
    }
    
    public int setGoalId(){
        goalCounter++;
        return goalCounter;
    }
    
    @Override
    public boolean unregister(Office o) {
        setInOffice(null);
        return o.removeRobot();
    }

    @Override
    public boolean register(Office o) {
        setInOffice(o);
        return o.putRobot(this);
    }
    
    public static boolean moveBox(Box b, Office o1, Office o2){
        boolean ret = ((b != null) && (!(b.isPartial())) && (o1 != null) && 
                (!(o1.isPartial())) && (o2 != null) && (!(o2.isPartial())));
        if (ret) ret = b.move(o1, o2);
        return ret;
    }
    
    public static boolean moveRobot(Robot r, Office o1, Office o2){
        boolean ret = ((r != null) && (o1 != null) && (!(o1.isPartial())) && 
                (o2 != null) && (!(o2.isPartial())));
        if (ret) ret = r.move(o1, o2);
        return ret;
    }

    public List<Operator> getPlan() {
        return Plan;
    }

    public List<Operator> setPlan(List<Operator> Plan) {
        return this.Plan = Plan;
    }

    public HashMap<Integer, ArrayList<Goal>> getActiveObjectives() {
        return ActiveObjectives;
    }

    public void setActiveObjectives(HashMap ActiveObjectives) {
        this.ActiveObjectives = ActiveObjectives;
    }
    
    public HashMap<Integer, ArrayList<Goal>> getPendingObjectives() {
        return PendingObjectives;
    }
    
    public void setPendingObjectives(HashMap PendingObjectives){
        this.PendingObjectives = PendingObjectives;
    }
    
    public Stack<Goal> getGoalsStack() {
        return GoalsStack;
    }

    public void setGoalsStack(Stack<Goal> GoalsStack) {
        this.GoalsStack = GoalsStack;
    }
    
    public Building getController(){
        return this.controller;
    }


    private boolean checkConditions(List<Condition> conditions) {
        return controller.checkConditions(conditions);
    }
    private boolean checkCondition(Condition condition) {
        return controller.checkCondition(condition);
    }

    private boolean isBetter(Operator newOp, Operator oldOp) {
        boolean ret;
        if(newOp == null) ret = false;
        else if(oldOp == null) ret = true;
        else if(isConflictive(oldOp) && !isConflictive(newOp)) ret = true;
        else if(!isConflictive(newOp) && heuristicBetter(newOp,oldOp)) ret = true;
        else if(isConflictive(newOp) && isConflictive(oldOp) && heuristicBetter(newOp,oldOp)) ret = true;
        else if(doable(newOp) && !doable(oldOp)) ret = true;
        else if((!doable(newOp) && doable(oldOp)) || heuristicBetter(oldOp,newOp)) ret = false;
        else if(inteligence>1000) ret = (Math.random() < 0.5);
        else if((!doable(newOp) && doable(oldOp)) || !isConflictive(oldOp)) ret = false;
        else if(inteligence>500) ret = (Math.random() < 0.5);
        else ret = false;
        return ret;
    }
    
    private boolean doable(Operator op){
        List<Condition> auxConds = new ArrayList<>();
        boolean ret = false;
        for(Condition cond : op.getAdd()) if(!(op.getType().equals("Push") && cond.getType().equals("Robot-location"))) auxConds.add(cond);
        if (getController().getStatus().containsAll(auxConds)) ret = true;
        else {
            auxConds = new ArrayList<>();
            for(Condition cond : op.getPreconditions()) if(!(op.getType().equals("Push") && cond.getType().equals("Robot-location"))) auxConds.add(cond);
            if (getController().getStatus().containsAll(auxConds)) ret = true;
        }
        return ret;
    }
    
    private Operator getOperator(Condition cond){
        List<Operator> operators = getController().getAddOperators(cond);
        return operatorHeuristic(operators);
    }
    
    private Operator operatorHeuristic(List<Operator> operators){
        Operator ret = null;
        for(Operator op : operators) if(isBetter(op, ret)) ret = op;
        return ret;
    }

    private boolean conflictive(List<Condition> delete) {
        boolean ret = false;
        for( ArrayList<Goal> goals:getActiveObjectives().values()){
            for(Goal g:goals){
                for(Condition d:delete){
                    ret = ret || g.conflicts(d);
                }
            }
        }
        return ret;
    }
    
    private boolean isConflictive(Operator o) {
        return conflictive(o.getDelete());
    }
    
    private void executeOperator(Operator op){
        if (op.executeOperator(getController())){
            getPlan().add(op);
            manageConflicts(op.getDelete());
        }
    }
    
    private void manageConflicts(List<Condition> delete) {
        List<Goal> newPending = new ArrayList<>();
        for (ArrayList<Goal> goals : getActiveObjectives().values()) {
            for(Goal g:goals){
                for(Condition d:delete){
                    if (g.conflicts(d)){
                        newPending.add(g);
                    }
                }
            }
        }
        for(Goal g : newPending) addPending(g);
    }
    
    private void addPending(Goal g){
        removeObjective(g, getActiveObjectives());
        addObjective(g, getPendingObjectives());
    }
    
    private void addObjective(Goal g, HashMap<Integer, ArrayList<Goal>> objectives){
        if(objectives.containsKey(g.getTarget())) objectives.get(g.getTarget()).add(g);
    }
    
    private void removeObjective(Goal g, HashMap<Integer, ArrayList<Goal>> objectives){
        if(objectives.containsKey(g.getTarget())){
            if(objectives.get(g.getTarget()).contains(g)) objectives.get(g.getTarget()).remove(g);
        }
    }

    private boolean heuristicBetter(Operator newOp, Operator oldOp) {
        return ((newOp.getPriority() < oldOp.getPriority()));
    }
    
    private List<Condition> sortHeuristic(List<Condition> conds){
        List<Condition> newConds = new ArrayList<>();
        Collections.sort(conds, new CustomComparator());
        return conds;
    }

    public Office NextOffice(Office office) {
        Office ret = null;
        if (getInOffice().isAdjacent(office)) ret = office;
        else{
            ret = getController().nextStep(getInOffice(),office);
        }
        return ret;
    }

    

    public class CustomComparator implements Comparator<Condition> {
        public int compare(Condition o1, Condition o2) {
            int ret;
            if (o1.getPriority() != o2.getPriority()){ 
                Integer a = (o1.getPriority());
                ret = ((Integer) o2.getPriority()).compareTo((Integer) (o1.getPriority()));
            }
            else {
                ret = ((Integer) o2.getOffice().getPriority()).compareTo((Integer) o1.getPriority());
            }
            return ret;
        }
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package planner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jakie
 */
public class Office {
    private String name = "";
    private int id;
    private List<Office> adjacent = new ArrayList<>();
    private boolean clean = false;
    private Box boxIn = null;
    private boolean empty = true;
    private Robot robot = null;
    private int priority = 0;

    public Office(String newName, int p){
        name = newName;
        priority = p;
        if (p==4) id = 6;
        else if(p==6) id = 4;
        else id = p;
    }
    
     public Office(String newName){
        name = newName;
    }
    
    public Office(){}
    
    public Office(String newName, List<Office> newAdjacent, Boolean status) {
        name = newName;
        adjacent = newAdjacent;
        clean = status;
    }
    
    public Office(String newName, List<Office> newAdjacent, Boolean status, Box newBoxIn) {
        name = newName;
        adjacent = newAdjacent;
        clean = status;
        boxIn = newBoxIn;
        empty = false;
    }
    
    public int getId(){return id;}
    
    public int getPriority(){
        return priority;
    }
    
    public boolean isempty(){
        return (getEmpty() && (getBoxIn()==null));
    }

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    public boolean getEmpty() {
        return empty;
    }
    
    public void setRobot(Robot robot) {
        this.robot = robot;
        if (robot != null) robot.setInOffice(this);
    }
    
    public Robot getRobot() {
        return robot;
    }
    
    public boolean isPartial(){
        return getName().isEmpty();
    }

    public String getName() {
        return name;
    }

    public List<Office> getAdjacent() {
        return adjacent;
    }

    public void setAdjacent(List<Office> adjacent) {
        this.adjacent = adjacent;
    }

    public Boolean getClean() {
        return clean;
    }

    public void setClean(Boolean clean) {
        this.clean = clean;
    }

    public Box getBoxIn() {
        return boxIn;
    }

    public void setBoxIn(Box boxIn) {
        this.boxIn = boxIn;
        if (boxIn != null) boxIn.setInOffice(this);
    }
 
    //Methods
    public Boolean isAdjacent(Office oB){
        return getAdjacent().contains(oB);
    }
    
    @Override
    public boolean equals(Object o) {
        return ((o != null) && (this.getClass().getName().equals(o.getClass().getName())) &&
                (this.toString().equals(o.toString())));
    }
    
    @Override
    public String toString() {
        return getName();
    }

    public boolean clean() {
        boolean ret = !(getClean());
        setClean(true);
        return ret;
    }
    
    public boolean dirty() {
        setClean(false);
        return true;
    }

    public boolean addAdjacent(Office o2) {
        boolean ret = !(this.equals(o2));
        if (ret) getAdjacent().add(o2);
        return ret;
    }
    
    public boolean putBox(Box b){
        boolean ret = ((isEmpty()) || (getBoxIn().isPartial()));
        setBoxIn(b); 
        setEmpty(false);
        return ret;
    }
    
    public boolean removeBox(){
        boolean ret = ((!(isEmpty())) && (!(getBoxIn().isPartial())));
        setBoxIn(null);
        setEmpty(true);
        return ret;
    }
    
    public boolean putRobot(Robot r){
        boolean ret = (getRobot() == null);
        setRobot(r);
        return ret;
    }
    
    public boolean removeRobot(){
        boolean ret = (getRobot() != null);
        setRobot(null);
        return ret;
    }
    
}

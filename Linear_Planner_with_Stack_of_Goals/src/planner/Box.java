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
public class Box extends MobilObject{
    private String name = "";
    private Office inOffice = null;
    
    public Box(String newName, Office newOffice) {
        name = newName;
        inOffice = newOffice;
    }
    
    public Box(String newName) {
        name = newName;
        inOffice = new Office();
    }
    
    public Box(){}

    public String getName() {
        return name;
    }

    public Office getInOffice() {
        return inOffice;
    }

    public Office setInOffice(Office inOffice) {
        return this.inOffice = inOffice;
    }
    
    public boolean isPartial(){
        return getName().isEmpty();
    }

    @Override
    public boolean unregister(Office o) {
        setInOffice(null);
        return o.removeBox();
    }

    @Override
    public boolean register(Office o) {
        setInOffice(o);
        return o.putBox(this);
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
    
}

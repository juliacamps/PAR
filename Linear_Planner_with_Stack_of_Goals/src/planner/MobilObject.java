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
public abstract class MobilObject {
    private Office inOffice = null;
    
    public boolean move(Office oA, Office oB){
        boolean ret;
        if (ret = (oA.isAdjacent(oB))){
            unregister(oA);
            register(oB);
        }
        return ret;
    }
    
    public Office getInOffice() {
        return inOffice;
    }

    public Office setInOffice(Office inOffice) {
        return this.inOffice = inOffice;
    }
    
    public abstract boolean unregister(Office o);
    public abstract boolean register(Office o);
}

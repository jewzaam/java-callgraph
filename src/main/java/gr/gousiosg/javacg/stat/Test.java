/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.gousiosg.javacg.stat;

/**
 *
 * @author nmalik
 */
public class Test {
    public void target() {
        
    }
    
    public void caller() {
        target();
    }
    
    public void root() {
        caller();
    }
}

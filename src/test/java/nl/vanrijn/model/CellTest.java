/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import org.junit.Test;

/**
 *
 * @author marek
 */
public class CellTest {

    Cell c = null;

    @Test
    public void checkCell() {
        c = new Cell(1, 1, 1, 1, 1, new ArrayList<Segment>());
        System.out.println("CellTest: created a cell = " + c.toString());
    }
}

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
        ArrayList<DendriteSegment> seg = new ArrayList<>();
        Column col = new Column(1, 1, 2);

        c = new Cell(col, 1, 1, 1, seg);
        System.out.println("CellTest: created a cell = " + c.toString());
    }
}

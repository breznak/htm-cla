/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

import htm.model.input.Input;

/**
 *
 * @author marek
 */
public class SpatialPooler {

    int columns;
    Column[] cols;

    public SpatialPooler(int outputs, Input in) {
        for (int i = 0; i < columns; i++) {
            cols[i] = new Column(in);
        }
    }
}

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
public class SpatialPooler extends LayerAbstract {

    int columns;
    Column[] cols;

    public SpatialPooler(int outputs, Input in) {
        super(null, null, 0, 1);
        for (int i = 0; i < columns; i++) {
            cols[i] = new Column(in, this, 1);
        }
    }
}

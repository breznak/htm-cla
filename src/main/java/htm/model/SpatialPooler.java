/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package htm.model;

/**
 *
 * @author marek
 */
public class SpatialPooler {

    int columns;
    int syncPC;
    byte synapse[][];

    public SpatialPooler(int outputs, int synapsesPerColumn) {
        columns = outputs;
        syncPC = synapsesPerColumn;
        for (int i = 0; i < columns; i++) {
            synapse[i] = rnd.nextInt(syncPC);
        }
    }
}

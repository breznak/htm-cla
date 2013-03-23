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
    int synapsesPerColumn;
    int synapse[][] = new int[columns][synapsesPerColumn];

    public SpatialPooler(int outputs, int synapsesPerColumn) {
        columns = outputs;
        this.synapsesPerColumn = synapsesPerColumn;
        for (int i = 0; i < columns; i++) {
            synapse[i] = Column.initSynapses(); //TODO init not totaly in specs, see page 34
        }
    }
}

/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

/**
 * synapse A data structure representing a synapse - contains a permanence value
 * and the source input index.
 *
 * @author vanrijn
 */
public abstract class SynapseAbstract {

    /**
     * similar like weight in NN ; range 0..1
     */
    private double permanance;

    public SynapseAbstract(double initPermanance) {
        this.permanance = initPermanance;
    }

    /**
     * synapse is considered connected if its permanance is bigger than
     * connectedPermanance
     *
     * @param connectedPermanance
     * @return
     */
    public boolean isConnected(double connectedPermanance) {
        // logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
        // "input="+sourceInput);
        return this.permanance >= connectedPermanance;
    }

    /**
     * permanance is like weight in NN
     *
     * @return
     */
    public double getPermanance() {
        return this.permanance;
    }

    /**
     * set permanence value to d, ensure range is between 0..1
     *
     * @param d
     */
    public void setPermanance(double d) {
        this.permanance = Math.min(Math.max(d, 0), 1);
    }
}

package eu.warble.voice.util.aStarSearch;

import com.indoorway.android.common.sdk.model.IndoorwayNode;

/**
 * This class represents an AbstractNode. It has all the appropriate fields as well
 * as getter and setter to be used by the A* algorithm.
 * <p>
 * <p>
 * An <code>AbstractNode</code> has x- and y-coordinates and can be walkable or not.
 * A previous AbstractNode may be set, as well as the
 * <code>fCosts</code>, <code>gCosts</code> and <code>hCosts</code>.
 * <p>
 * <p>
 * <code>fCosts</code>: <code>gCosts</code> + <code>hCosts</code>
 * <p>
 * <code>gCosts</code>: calculated costs from start AbstractNode to this AbstractNode
 * <p>
 * <code>hCosts</code>: estimated costs to get from this AbstractNode to end AbstractNode
 * <p>
 * <p>
 * A subclass has to override the heuristic function
 * <p>
 * <code>sethCosts(AbstractNode endAbstractNode)</code>
 * <p>
 * <p>
 */
public abstract class AbstractNode {

    IndoorwayNode indoorwayNode;

    /** costs to move sideways from one square to another. */
    /** costs to move diagonally from one square to another. */

    /** the previous AbstractNode of this one on the currently calculated path. */
    private AbstractNode previous;


    /** calculated costs from start AbstractNode to this AbstractNode. */
    private int gCosts;

    /** estimated costs to get from this AbstractNode to end AbstractNode. */
    private double hCosts;

    /**
     * constructs a walkable AbstractNode with given coordinates.
     *
     */
    public AbstractNode(IndoorwayNode indoorwayNode) {
        this.indoorwayNode = indoorwayNode;
    }

    public IndoorwayNode getIndoorwayNode() {
        return indoorwayNode;
    }

    /**
     * @return the xPosition
     */
    public double getxPosition() {
        return indoorwayNode.getCoordinates().component1();
    }

    /**
     * @return the yPosition
     */
    public double getyPosition() {
        return indoorwayNode.getCoordinates().component2();
    }


    /**
     * returns the node set as previous node on the current path.
     *
     * @return the previous
     */
    public AbstractNode getPrevious() {
        return previous;
    }

    /**
     * @param previous the previous to set
     */
    public void setPrevious(AbstractNode previous) {
        this.previous = previous;
    }


    /**
     * returns <code>gCosts</code> + <code>hCosts</code>.
     * <p>
     *
     *
     * @return the fCosts
     */
    public double getfCosts() {
        return gCosts + hCosts;
    }

    /**
     * returns the calculated costs from start AbstractNode to this AbstractNode.
     *
     * @return the gCosts
     */
    public int getgCosts() {
        return gCosts;
    }

    /**
     * sets gCosts to <code>gCosts</code> plus <code>movementPanelty</code>
     * for this AbstractNode.
     *
     * @param gCosts the gCosts to set
     */
    private void setgCosts(int gCosts) {
        this.gCosts = gCosts;
    }


    /**
     * sets gCosts to <code>gCosts</code> plus <code>movementPanelty</code>
     * for this AbstractNode given the previous AbstractNode.
     * <p>
     * It will assume <code>BASICMOVEMENTCOST</code> as the cost from
     * <code>previousAbstractNode</code> to itself if the movement is not diagonally,
     * otherwise it will assume <code>DIAGONALMOVEMENTCOST</code>.
     * Weather or not it is diagonally is set in the Map class method which
     * finds the adjacent AbstractNodes.
     *
     * @param previousAbstractNode
     */
    public void setgCosts(AbstractNode previousAbstractNode) {
            setgCosts(previousAbstractNode.getgCosts());

    }

    /**
     * calculates - but does not set - g costs.
     * <p>
     * It will assume <code>BASICMOVEMENTCOST</code> as the cost from
     * <code>previousAbstractNode</code> to itself if the movement is not diagonally,
     * otherwise it will assume <code>DIAGONALMOVEMENTCOST</code>.
     * Weather or not it is diagonally is set in the Map class method which
     * finds the adjacent AbstractNodes.
     *
     * @param previousAbstractNode
     * @return gCosts
     */
    public double calculategCosts(AbstractNode previousAbstractNode) {
            return (previousAbstractNode.getgCosts());

    }

    /**
     * calculates - but does not set - g costs, adding a movementPanelty.
     *
     * @param previousAbstractNode
     * @param movementCost costs from previous AbstractNode to this AbstractNode.
     * @return gCosts
     */
    public double calculategCosts(AbstractNode previousAbstractNode, int movementCost) {
        return (previousAbstractNode.getgCosts() + movementCost);
    }

    /**
     * returns estimated costs to get from this AbstractNode to end AbstractNode.
     *
     * @return the hCosts
     */
    public double gethCosts() {
        return hCosts;
    }

    /**
     * sets hCosts.
     *
     * @param hCosts the hCosts to set
     */
    protected void sethCosts(double hCosts) {
        this.hCosts = hCosts;
    }

    /**
     * calculates hCosts for this AbstractNode to a given end AbstractNode.
     * Uses Manhatten method.
     *
     * @param endAbstractNode
     */
    public abstract void sethCosts(IndoorwayNode endAbstractNode);



    /**
     * returns a String containing the coordinates, as well as h, f and g
     * costs.
     *
     * @return
     */


    @Override
    public String toString() {
        return "(" + getxPosition() + ", " + getyPosition() + "): h: "
                + gethCosts() + " g: " + getgCosts() + " f: " + getfCosts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractNode that = (AbstractNode) o;

        if (gCosts != that.gCosts) return false;
        if (Double.compare(that.hCosts, hCosts) != 0) return false;
        if (indoorwayNode != null ? !indoorwayNode.equals(that.indoorwayNode) : that.indoorwayNode != null)
            return false;
        return previous != null ? previous.equals(that.previous) : that.previous == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = indoorwayNode != null ? indoorwayNode.hashCode() : 0;
        result = 31 * result + (previous != null ? previous.hashCode() : 0);
        result = 31 * result + gCosts;
        temp = Double.doubleToLongBits(hCosts);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

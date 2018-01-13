package eu.warble.voice.util.aStarSearch;

/**
 * Created by GaskinPC on 13.01.2018.
 */
import com.indoorway.android.common.sdk.model.IndoorwayNode;

public class AStarNodeWrapper extends AbstractNode {

    public AStarNodeWrapper(IndoorwayNode indoorwayNode) {
        super(indoorwayNode);
    }


    public void sethCosts(IndoorwayNode endNode) {
        this.sethCosts((absolute(super.getIndoorwayNode().getCoordinates().component1()-endNode.getCoordinates().component1())
                + absolute(super.getIndoorwayNode().getCoordinates().component2()-endNode.getCoordinates().component2())));
    }

    private double absolute(double a) {
        return a > 0 ? a : -a;
    }



}
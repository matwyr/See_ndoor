package eu.warble.voice.util.aStarSearch;

import com.indoorway.android.common.sdk.model.IndoorwayNode;

import java.util.*;

/**
 * The AStarSearch class, along with the AStarNode class,
 * implements a generic A* search algorithm. The AStarNode
 * class should be subclassed to provide searching capability.
 */
public class AStarSearch {

    List<IndoorwayNode> nodeList;

    public final List<IndoorwayNode> findPath(IndoorwayNode startNode, IndoorwayNode endNode) {

        AStarNodeWrapper startWrapper = new AStarNodeWrapper(startNode);
        List<AStarNodeWrapper> openList = new LinkedList<>();
        List<AStarNodeWrapper> closedList = new LinkedList<>();
        openList.add(new AStarNodeWrapper(startNode)); // add starting node to open list

        Boolean done = false;
        AStarNodeWrapper current;
        while (!done) {
            current = lowestFInOpen(openList); // get node with lowest fCosts from openList
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if ((current.getIndoorwayNode().equals(endNode))) { // found goal
                return calcPath(startWrapper, current);
            }


            // for all adjacent nodes:
            List<AStarNodeWrapper> adjacentNodes = getAdjacent(current);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                AStarNodeWrapper currentAdj = adjacentNodes.get(i);
                if (!openList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCosts(endNode); // set h costs of this node (estimated costs to goal)
                    currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.getgCosts() > currentAdj.calculategCosts(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

            if (openList.isEmpty()) { // no path exists
                return new LinkedList<IndoorwayNode>(); // return empty list
            }
        }
        return null; // unreachable
    }

    private List<AStarNodeWrapper> getAdjacent(AStarNodeWrapper current) {
        List<AStarNodeWrapper> result = new LinkedList<>();

        Collection<Long> neighbours = current.getIndoorwayNode().getNeighbours();
        for (Long id :
                neighbours) {
            for (IndoorwayNode node :
                    nodeList) {
                if (node.getId() == id) {
                    result.add(new AStarNodeWrapper(node));
                    break;
                }
            }
        }
        return result;
    }

    private AStarNodeWrapper lowestFInOpen(List<AStarNodeWrapper> openList) {
        // TODO currently, this is done by going through the whole openList!
        AStarNodeWrapper cheapest = openList.get(0);
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getfCosts() < cheapest.getfCosts()) {
                cheapest = openList.get(i);
            }
        }
        return cheapest;
    }

    private List<IndoorwayNode> calcPath(AStarNodeWrapper start, AStarNodeWrapper goal) {
        // TODO if invalid nodes are given (eg cannot find from
        // goal to start, this method will result in an infinite loop!)
        LinkedList<AStarNodeWrapper> path = new LinkedList<>();

        AStarNodeWrapper curr = goal;
        boolean done = false;
        while (!done) {
            path.addFirst(curr);
            curr = (AStarNodeWrapper) curr.getPrevious();

            if (curr.equals(start)) {
                done = true;
            }
        }
        List<IndoorwayNode> indoorwayNodes = new LinkedList<>();
        for (AStarNodeWrapper node :
                path) {
            indoorwayNodes.add(node.getIndoorwayNode());
        }
        return indoorwayNodes;
    }
}


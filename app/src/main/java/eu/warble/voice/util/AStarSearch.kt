package eu.warble.voice.util

import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.data.model.Node
import java.util.*
import kotlin.collections.HashSet


class AStarSearch(list: List<IndoorwayNode>) {
    private var nodeList: LinkedList<Node> = LinkedList()

    init {
        list.forEach {
            nodeList.add(Node(it))
        }
    }

    fun findPath(from: IndoorwayNode, to: IndoorwayNode): List<IndoorwayNode>{
        val fromNode = getNodeWithId(from.id)
        val toNode = getNodeWithId(to.id)
        val explored = HashSet<Node?>()

        val queue: PriorityQueue<Node?> = PriorityQueue(500, { node1: Node?, node2: Node? ->
            when {
                node1 != null && node2 != null -> when {
                    node1.f_scores > node2.f_scores -> return@PriorityQueue 1
                    node1.f_scores < node2.f_scores -> return@PriorityQueue -1
                    else -> return@PriorityQueue 0
                }
                else -> return@PriorityQueue 0
            }
        })

        fromNode?.g_scores = 0.0
        queue.add(fromNode)
        while (!queue.isEmpty()){
            val current = queue.poll()
            explored.add(current)

            if(current!= null && current == toNode) {
                return makePath(toNode)
            }

            if (current != null) {
                for (nodeId in current.indoorwayNode.neighbours){
                    val child = getNodeWithId(nodeId)
                    if (child != null){
                        val cost = child.indoorwayNode.coordinates.getDistanceTo(current.indoorwayNode.coordinates)
                        val temp_g_scores = current.g_scores + cost
                        val temp_f_scores = temp_g_scores + child.h_scores;
                        if (explored.contains(child) && (temp_f_scores >= child.f_scores)){
                            continue
                        }else if(!queue.contains(child) || temp_f_scores < child.f_scores){
                            child.parent = current
                            child.g_scores = temp_g_scores
                            child.f_scores = temp_f_scores
                            if (queue.contains(child)) {
                                queue.remove(child)
                            }
                            queue.add(child)
                        }
                    }
                }
            }
        }
        return emptyList()
    }

    private fun makePath(toNode: Node): List<IndoorwayNode> {
        val result = LinkedList<IndoorwayNode>()

        var node: Node? = toNode
        while (node != null) {
            result.add(node.indoorwayNode)
            node = node.parent
        }
        Collections.reverse(result)
        return result
    }

    private fun getNodeWithId(id: Long): Node? {
        return nodeList.find {
            it.indoorwayNode.id == id
        }
    }
}
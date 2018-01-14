package eu.warble.voice.util

import android.util.Log
import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.data.model.Direction
import eu.warble.voice.data.model.NodeInfo

object PathTranslator {

    fun translate(nodeList: List<IndoorwayNode>): LinkedHashMap<IndoorwayNode, NodeInfo> {
        val hmap = LinkedHashMap<IndoorwayNode, NodeInfo>()
        try {
            var prevNode: IndoorwayNode
            var currentNode: IndoorwayNode
            var milestoneNode: IndoorwayNode
            var prevAngle: Float
            var currentAngle: Float
            var relativeAngle: Float
            var milestoneDistance = 0.toDouble()
            var prevDistance: Double
            val iterator = nodeList.listIterator()
            prevNode = nodeList[0]
            currentNode = nodeList[1]
            prevAngle = prevNode.coordinates.getAngleTo(currentNode.coordinates)
            milestoneNode = prevNode
            var direction = Direction.LEFT

            while (iterator.hasNext()) {
                currentNode = iterator.next()
                prevDistance = prevNode.coordinates.getDistanceTo(currentNode.coordinates)
                currentAngle = prevNode.coordinates.getAngleTo(currentNode.coordinates)
                relativeAngle = currentAngle - prevAngle

                if (relativeAngle > -20 && relativeAngle < 20) {
                    milestoneDistance += prevDistance
                } else {
                    if (relativeAngle > -150 && relativeAngle < -20) direction=Direction.LEFT
                    else if (relativeAngle > 20 && relativeAngle < 150) direction=Direction.RIGHT
                    milestoneDistance += prevDistance
                    if (milestoneDistance > 2) {
                        hmap.put(milestoneNode, NodeInfo(direction, milestoneDistance))
                    }
                    Log.e("whatever", relativeAngle.toString() + direction + milestoneDistance)
                    milestoneNode = currentNode
                    milestoneDistance = 0.0
                }
                prevNode = currentNode
                prevAngle = currentAngle
            }
            hmap.put(nodeList.last(), NodeInfo(Direction.FINISH, milestoneDistance))
        } catch (e: Exception) {
            Log.e(javaClass.name, e.message)
        }
        return hmap
    }
}

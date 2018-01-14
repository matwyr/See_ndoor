package eu.warble.voice.util

import android.util.Log
import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.data.model.Direction
import eu.warble.voice.data.model.NodeInfo



//                      ******        PathTranslator.translate(nodelist)           *******
object PathTranslator {

        fun translate(nodelist: List<IndoorwayNode>): LinkedHashMap<IndoorwayNode, NodeInfo> {
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

                val iterator = nodelist.listIterator()

                prevNode = nodelist[0]
                currentNode = nodelist[1]
                prevAngle = prevNode.coordinates.getAngleTo(currentNode.coordinates)

                milestoneNode = prevNode

                var direction = Direction.STRAIGHT

                while (iterator.hasNext()) {
                    currentNode = iterator.next()

                    prevDistance = prevNode.coordinates.getDistanceTo(currentNode.coordinates)
                    currentAngle = prevNode.coordinates.getAngleTo(currentNode.coordinates)

                    relativeAngle = currentAngle - prevAngle

                    if (relativeAngle > -20 && relativeAngle < 20) {

                        milestoneDistance += prevDistance

                    } else{
                        if (relativeAngle > -150 && relativeAngle < -20) direction=Direction.LEFT
                        else if (relativeAngle > 20 && relativeAngle < 150) direction=Direction.RIGHT

                            milestoneDistance += prevDistance

                            if (milestoneDistance > 2) {
                                hmap.put(milestoneNode, NodeInfo(direction, milestoneDistance))
                            }
                            Log.e("whatever", relativeAngle.toString() + direction + milestoneDistance)

                            milestoneNode = currentNode

                            milestoneDistance = 0.toDouble()


                    }

                    prevNode = currentNode
                    prevAngle = currentAngle

                }

                hmap.put(nodelist.last(), NodeInfo(Direction.FINISH, milestoneDistance))

                //hmap.put()

            } catch (e: Exception) {
                error(e.printStackTrace())
            }

            var ret = ""

            for ((_, value) in hmap) {
                ret += "$value" + "\n"
            }

            Log.e("whatever", ret)

            return hmap
        }
}

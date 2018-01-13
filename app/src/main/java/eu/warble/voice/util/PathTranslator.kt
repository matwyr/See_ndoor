package eu.warble.voice.util

import android.util.Log
import android.util.Log.wtf
import com.indoorway.android.common.sdk.model.IndoorwayNode
import eu.warble.voice.data.model.Direction
import eu.warble.voice.data.model.NodeInfo



//                      ******        PathTranslator.translate(nodelist: List<IndoorwayNode>)           *******
class PathTranslator {

    companion object Translator {

        fun translate(nodelist: List<IndoorwayNode>):HashMap<IndoorwayNode,NodeInfo> {
            val hmap= HashMap<IndoorwayNode,NodeInfo>()
            try{



            var prevNode:IndoorwayNode
            var currentNode:IndoorwayNode
            var milestoneNode:IndoorwayNode

            var prevAngle:Float
            var currentAngle:Float
            var relativeAngle:Float

            var milestoneDistance = 0.toDouble()
            var prevDistance:Double


            //var dist = tnode.coordinates.getDistanceTo(tnode2.coordinates)
            //var angle = tnode.coordinates.getAngleTo(tnode2.coordinates)
            //var ninfo= NodeInfo(direction = Direction.STRAIGHT, distanceToNext = dist)


            //fangle=fnode.coordinates.getAngleTo(snode.coordinates)

            val literator=nodelist.listIterator()
            prevNode=nodelist[0]
            currentNode=nodelist[1]
            prevAngle=prevNode.coordinates.getAngleTo(currentNode.coordinates)

            milestoneNode=prevNode

            while(literator.hasNext()){
                currentNode=literator.next()

                prevDistance=prevNode.coordinates.getDistanceTo(currentNode.coordinates)
                currentAngle=prevNode.coordinates.getAngleTo(currentNode.coordinates)

                relativeAngle=currentAngle-prevAngle

                if(relativeAngle>-20&&relativeAngle<20){

                        milestoneDistance += prevDistance

                }
                else if(relativeAngle>-150&&relativeAngle<-20){

                    milestoneDistance += prevDistance

                    hmap.put(milestoneNode, NodeInfo(Direction.LEFT, 0.toDouble()))

                    milestoneNode=currentNode

                    milestoneDistance=0.toDouble()

                }
                else if(relativeAngle>20&&relativeAngle<150){

                    milestoneDistance += prevDistance

                    hmap.put(milestoneNode, NodeInfo(Direction.RIGHT, 0.toDouble()))

                    milestoneNode=currentNode

                    milestoneDistance=0.toDouble()

                }

                prevNode=currentNode
                prevAngle=currentAngle

            }



            //hmap.put()

            } catch (e: Exception) {
                error(e.printStackTrace())
            }

            Log.e("whatever",hmap.toString())
            return hmap
        }
    }
}
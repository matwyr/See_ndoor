package eu.warble.voice.data.model

import com.indoorway.android.common.sdk.model.IndoorwayNode

data class Node(val indoorwayNode: IndoorwayNode, var g_scores: Double = 0.0, var h_scores: Double = 0.0,
                var f_scores: Double = 0.0, var parent: Node? = null){
    override fun equals(other: Any?): Boolean {
        if (this.hashCode() == other?.hashCode()) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Node

        if (java.lang.Double.compare(other.g_scores, g_scores) != 0) return false
        if (java.lang.Double.compare(other.h_scores, h_scores) != 0) return false
        if (java.lang.Double.compare(other.f_scores, f_scores) != 0) return false

        if (indoorwayNode != other.indoorwayNode) return false
        if (parent != null && parent == other.parent) return true
        return false
    }

    override fun hashCode(): Int {
        var result: Int = indoorwayNode.hashCode()
        var temp: Long = java.lang.Double.doubleToLongBits(g_scores)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(h_scores)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(f_scores)
        result = 31 * result + (temp xor temp.ushr(32)).toInt()
        result = 31 * result + if (parent != null) parent!!.hashCode() else 0
        return result
    }
}
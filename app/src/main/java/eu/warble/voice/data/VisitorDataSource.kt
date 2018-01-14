package eu.warble.voice.data

import com.indoorway.android.common.sdk.IndoorwaySdk
import com.indoorway.android.common.sdk.listeners.generic.Action1
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.common.sdk.model.RegisteredVisitor
import com.indoorway.android.common.sdk.model.VisitorLocation
import com.indoorway.android.common.sdk.task.IndoorwayTask

object VisitorDataSource {

    fun findPerson(name: String, onPersonFindListener: OnPersonFindListener) {
        getAllVisitors(
                onVisitorFound = Action1{
                    val visitor = it.find { it.name == name }
                    if (visitor == null){
                        onPersonFindListener.notFound()
                        return@Action1
                    }
                    getLocationFor(visitor,
                            onLocationFound = Action1 {
                                onPersonFindListener.found(it?.position)
                            }, onFailedListener = Action1 { onPersonFindListener.notFound() })
                }, onFailedListener = Action1 { onPersonFindListener.notFound() })
    }

    private fun getAllVisitors(onVisitorFound: Action1<List<RegisteredVisitor>>,
                               onFailedListener: Action1<IndoorwayTask.ProcessingException>){
        IndoorwaySdk.instance()
                .visitors()
                .list()
                .setOnCompletedListener(onVisitorFound)
                .setOnFailedListener(onFailedListener)
                .execute()
    }

    private fun getLocationFor(visitor: RegisteredVisitor?, onLocationFound: Action1<VisitorLocation?>,
                               onFailedListener: Action1<IndoorwayTask.ProcessingException>){
        IndoorwaySdk.instance()
                .visitors()
                .locations()
                .setOnCompletedListener(Action1 {
                    val location = it.find {
                        it.visitorUuid == visitor?.uuid
                    }
                    onLocationFound.onAction(location)
                })
                .setOnFailedListener(onFailedListener)
                .execute()
    }

    interface OnPersonFindListener{
        fun found(position: IndoorwayPosition?)
        fun notFound()
    }
}
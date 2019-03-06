package com.cleverapp.ui.use_cases.images_list

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.cleverapp.repository.data.Image
import com.cleverapp.ui.viewmodels.BaseViewModel

private const val PREFERENCE_KEY_SPAN_COUNT = "PREFERENCE_KEY_SPAN_COUNT"

class ImagesViewModel(app: Application): BaseViewModel(app) {

    private val imagesChangedObserver: Observer<Boolean> = Observer {
        if (it == true)
            request.value = true
    }
    private val request = MutableLiveData<Boolean>()
    private val images = Transformations.switchMap(request) {
        repository.getImages()
    }
    private val viewMode = MutableLiveData<HistoryViewMode>().also { it.value = getCurrentViewMode() }

    init {
        repository.getImagesChangedLiveData().observeForever(imagesChangedObserver)
        request.value = true
    }

    override fun onCleared() {
        super.onCleared()
        repository.getImagesChangedLiveData().removeObserver(imagesChangedObserver)
    }

    fun getImagesLiveData(): LiveData<List<Image>> = images
    fun getViewModeLiveData(): LiveData<HistoryViewMode> = viewMode

    fun removeImage(image: Image) {
        repository.removeImage(image)
    }

    fun changeGrid() {
        val current = getCurrentViewMode()
        val toApply =
                if (current == HistoryViewMode.SingleColumn) HistoryViewMode.MultiColumn
                else HistoryViewMode.SingleColumn
        preferences.edit().putInt(
                PREFERENCE_KEY_SPAN_COUNT,
                toApply.spanCount)
                .apply()
        viewMode.value = toApply
    }

    fun updateImageOrdering(currentUiOrder: List<Image>) {
        val changedIndices = ArrayList<Image>(currentUiOrder.size)
        val reverse = currentUiOrder.asReversed()
        for (i in reverse.indices) {
            val image = reverse[i]
            if (image.ordinalNum != i)
                changedIndices.add(image.apply { this.ordinalNum = i })
        }
        repository.updateImages(changedIndices)
    }

    fun addImage(imageUriList: List<Uri>){
        repository.saveImages(imageUriList)
    }

    fun removeImages(images: List<Image>) {
        repository.removeImages(images)
    }

    private fun getCurrentViewMode(): HistoryViewMode {
        val currentIsSingleColumn =
                preferences.getInt(
                        PREFERENCE_KEY_SPAN_COUNT,
                        HistoryViewMode.MultiColumn.spanCount) == HistoryViewMode.SingleColumn.spanCount
        return if (currentIsSingleColumn) HistoryViewMode.SingleColumn else HistoryViewMode.MultiColumn
    }
}

enum class HistoryViewMode(val spanCount: Int) {
    SingleColumn(1),
    MultiColumn(3)
}

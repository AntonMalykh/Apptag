package com.cleverapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.cleverapp.repository.data.TaggedImage

private const val PREFERENCE_KEY_SPAN_COUNT = "PREFERENCE_KEY_SPAN_COUNT"

class ImagesViewModel(app: Application): BaseViewModel(app) {

    var imagesChangedObserver: Observer<Boolean> = Observer {
        if (it == true)
            updateHistory()
    }

    private val request = MutableLiveData<Boolean>()

    private val images = Transformations.switchMap(request) {
        repository.getSavedTaggedImages()
    }
    private val viewMode = MutableLiveData<HistoryViewMode>().also { it.value = getCurrentViewMode() }

    init {
        repository.getTaggedImagesChangedLiveData().observeForever(imagesChangedObserver)
    }

    fun getImagesLiveData(): LiveData<List<TaggedImage>> = images
    fun getViewModeLiveData(): LiveData<HistoryViewMode> = viewMode

    fun updateHistory(){
        request.value = true
    }

    fun onRemoveClicked(image: TaggedImage) {
        repository.deleteSavedTaggedImage(image)
        updateHistory()
    }

    fun onGridMenuClicked() {
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

    private fun getCurrentViewMode(): HistoryViewMode {
        val currentIsSingleColumn =
                preferences.getInt(
                        PREFERENCE_KEY_SPAN_COUNT,
                        HistoryViewMode.MultiColumn.spanCount) == HistoryViewMode.SingleColumn.spanCount
        return if (currentIsSingleColumn) HistoryViewMode.SingleColumn else HistoryViewMode.MultiColumn
    }

    override fun onCleared() {
        super.onCleared()
        repository.getTaggedImagesChangedLiveData().removeObserver(imagesChangedObserver)
    }

    fun updateImageOrdering(currentUiOrder: List<TaggedImage>) {
        val changedIndices = ArrayList<TaggedImage>(currentUiOrder.size)
        val reverse = currentUiOrder.asReversed()
        for (i in reverse.indices) {
            val image = reverse[i]
            if (image.ordinalNum != i)
                changedIndices.add(image.apply { this.ordinalNum = i })
        }
        repository.updateTaggedImages(changedIndices)
    }
}

enum class HistoryViewMode(val spanCount: Int) {
    SingleColumn(1),
    MultiColumn(3)
}

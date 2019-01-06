package com.cleverapp.repository.tagservice.clarifaitagservice

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import clarifai2.api.ClarifaiBuilder
import clarifai2.api.ClarifaiClient
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import com.cleverapp.repository.data.ImageTagResult
import com.cleverapp.repository.tagservice.TagService

internal class ClarifaiTagService : TagService {

    private val CLARIFAI_API_KEY = "7838cc2605b54bb2a64956e469ca9099"

    private var client: ClarifaiClient

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    init{
        client = ClarifaiBuilder(CLARIFAI_API_KEY)
                .buildSync()
    }

    override fun getImageTags(imageBytes: ByteArray, resultHandler: Observer<ImageTagResult>) {
        client.defaultModels.generalModel().predict()
                .withInputs(ClarifaiInput.forImage(imageBytes))
                .executeAsync {
                    mainHandler.post {
                        resultHandler.onChanged(ImageTagResult.success(responseToTagList(it)))
                    }
                }
    }

    private fun responseToTagList(responseResult: List<ClarifaiOutput<Concept>>): List<String> {
        val data = responseResult[0].data()
        return data.fold(ArrayList(data.size)){
            acc, concept ->
                acc.add(concept.name()!!)
                acc
        }
    }
}

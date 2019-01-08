package com.cleverapp.repository.tagservice.clarifaitagservice

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import clarifai2.api.ClarifaiBuilder
import clarifai2.api.ClarifaiClient
import clarifai2.api.request.ClarifaiRequest
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import com.cleverapp.repository.data.ImageTagResult
import com.cleverapp.repository.tagservice.TagService

internal class ClarifaiTagService : TagService {

    private val CLARIFAI_API_KEY = ""

    private var client: ClarifaiClient

    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    init{
        client = ClarifaiBuilder(CLARIFAI_API_KEY)
                .buildSync()
    }

    @Suppress("RedundantSamConstructor")
    override fun getImageTags(imageBytes: ByteArray, consumer: Observer<ImageTagResult>) {
        client.defaultModels.generalModel().predict()
                .withInputs(ClarifaiInput.forImage(imageBytes))
                .executeAsync(
                        ClarifaiRequest.OnSuccess {
                            mainHandler.post {
                                consumer.onChanged(ImageTagResult.success(responseToTagList(it)))
                            }
                        },
                        ClarifaiRequest.OnFailure {
                            mainHandler.post {
                                consumer.onChanged(ImageTagResult.error(errorCodeToMessage(it)))
                            }
                        },
                        ClarifaiRequest.OnNetworkError {
                            mainHandler.post {
                                consumer.onChanged(ImageTagResult.error(it.localizedMessage))
                            }
                        })
    }

    private fun errorCodeToMessage(clarifaiErrorCode: Int): String {
        return "Unable to process image"
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

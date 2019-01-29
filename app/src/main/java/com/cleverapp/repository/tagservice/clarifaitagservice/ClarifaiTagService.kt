package com.cleverapp.repository.tagservice.clarifaitagservice

import androidx.lifecycle.Observer
import clarifai2.api.ClarifaiBuilder
import clarifai2.api.ClarifaiClient
import clarifai2.api.request.ClarifaiRequest
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import com.cleverapp.repository.Language
import com.cleverapp.repository.tagservice.TagService
import com.cleverapp.repository.tagservice.GetImageTagResponse

internal class ClarifaiTagService : TagService {

    private val CLARIFAI_API_KEY = ""

    private var client: ClarifaiClient


    init{
        client = ClarifaiBuilder(CLARIFAI_API_KEY)
                .buildSync()
    }

    @Suppress("RedundantSamConstructor")
    override fun getImageTags(imageBytes: ByteArray,
                              tagsLanguage: Language,
                              tagsCount: Int,
                              consumer: Observer<GetImageTagResponse>) {

        client.defaultModels.generalModel().predict()
                .withInputs(ClarifaiInput.forImage(imageBytes))
                .withLanguage(tagsLanguage.code)
                .withMaxConcepts(tagsCount)
                .executeAsync(
                        ClarifaiRequest.OnSuccess {
                            consumer.onChanged(GetImageTagResponse.success(imageBytes, responseToTagList(it)))
                        },
                        ClarifaiRequest.OnFailure {
                            consumer.onChanged(GetImageTagResponse.error(imageBytes, errorCodeToMessage(it)))
                        },
                        ClarifaiRequest.OnNetworkError {
                            consumer.onChanged(GetImageTagResponse.error(imageBytes, it.localizedMessage))
                        })
    }

    private fun errorCodeToMessage(clarifaiErrorCode: Int): String {
        return "Unable to process image"
    }

    private fun responseToTagList(responseResult: List<ClarifaiOutput<Concept>>): List<String> {
        val data = responseResult[0].data()
        return data.fold(ArrayList(data.size)){
            acc, concept ->
                concept.name()?.let { acc.add(it)}
                acc
        }
    }
}

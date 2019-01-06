package com.cleverapp.repository.tagservice.clarifaitagservice

import android.net.Uri
import clarifai2.api.ClarifaiBuilder
import clarifai2.api.ClarifaiClient
import clarifai2.api.ClarifaiResponse
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import com.cleverapp.repository.tagservice.TagService
import java.io.File

internal class ClarifaiTagService : TagService {

    private val CLARIFAI_API_KEY = "7838cc2605b54bb2a64956e469ca9099"

    private var client: ClarifaiClient

    init{
        client = ClarifaiBuilder(CLARIFAI_API_KEY)
                .buildSync()
    }

    override fun getTags(fileUri: Uri): ClarifaiResponse<MutableList<ClarifaiOutput<Concept>>> {
        return client.defaultModels.generalModel().predict()
                .withInputs(ClarifaiInput.forImage(File(fileUri.path)))
                .executeSync();
    }
}
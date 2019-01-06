package com.cleverapp.repository.tagservice

import android.net.Uri
import clarifai2.api.ClarifaiResponse
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept

interface TagService {
    fun getTags(fileUri: Uri): ClarifaiResponse<MutableList<ClarifaiOutput<Concept>>>
}

package com.example.tbtb.ui.proposal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tbtb.data.model.Proposal
import com.example.tbtb.data.repository.ProposalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProposalViewModel(application: Application) : AndroidViewModel(application) {

    private val proposalRepository = ProposalRepository()

    private val _proposals = MutableStateFlow<List<Proposal>>(emptyList())
    val proposals: StateFlow<List<Proposal>> get() = _proposals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _serverError = MutableStateFlow<String?>(null)
    val serverError: StateFlow<String?> get() = _serverError

    private val _operationResult = MutableStateFlow<Boolean?>(null)
    val operationResult: StateFlow<Boolean?> get() = _operationResult

    fun createProposal(
        token: String,
        projectId: String,
        title: String,
        description: String,
        pdfFile: MultipartBody.Part,
        onComplete: (Boolean) -> Unit
    ) {
        _isLoading.value = true
        _error.value = null
        _serverError.value = null
        viewModelScope.launch {
            try {
                val response = proposalRepository.createProposal("Bearer $token", projectId, title, description, pdfFile)
                if (response.isSuccessful) {
                    _operationResult.value = true
                    if (response.code() == 400) {
                        _serverError.value = response.message()
                    }
                    onComplete(true)
                } else {
                    _serverError.value = response.message()
                    _operationResult.value = true
                    onComplete(true)
                }
            } catch (e: Exception) {
                _error.value = e.message
                _operationResult.value = false
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setServerError(message: String?) {
        _serverError.value = message
    }
}

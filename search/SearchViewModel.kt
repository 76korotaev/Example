package com.smarttransport.its.monitoringums.search

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel



class SearchViewModel: ViewModel() {

    var searchText = ObservableField<String>()
    var isLoading = ObservableField<Boolean>(false)
    var isError = ObservableField<Boolean>(false)

}
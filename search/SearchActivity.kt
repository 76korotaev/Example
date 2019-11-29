package com.smarttransport.its.monitoringums.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttransport.its.monitoringums.R
import com.smarttransport.its.monitoringums.utils.ActivityUtils

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var searchFragment = supportFragmentManager.findFragmentById(R.id.a_search_fl) as SearchFragment?

        if (searchFragment == null) {
            searchFragment = SearchFragment.newInstance()
            ActivityUtils().addFragmentToActivity(supportFragmentManager, searchFragment, R.id.a_search_fl)
        }
    }
}

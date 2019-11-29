package com.smarttransport.its.monitoringums.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smarttransport.its.monitoringums.data.model.Crosswalk
import com.smarttransport.its.monitoringums.custom.ViewHolderHeader
import com.smarttransport.its.monitoringums.databinding.CrosswalkItemBinding
import com.smarttransport.its.monitoringums.databinding.HeaderItemBinding


class CrosswalkAdapter(private val clickListener: (Crosswalk) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list : List<Crosswalk>? = null
    var filteredList: List<Crosswalk>? = list

    fun setData(l: List<Crosswalk>){
        this.list = l.filter { it.lat != null }.sortedBy { it.roadName }
        this.filteredList = list
        prepareItems()
    }

    fun filtered (parameters: FilterParameters){
        filteredList = list?.filter {
            t ->

            (t.name.contains(parameters.textSearch, true) || t.roadName.contains(parameters.textSearch, true))
                    && (t.regionId == (if (parameters.regionId != null) parameters.regionId else {t.regionId}) ) &&
                    (t.roadId == (if (parameters.roadId != null) parameters.roadId else {t.roadId}) )
        }
        prepareItems()
    }

    private fun prepareItems(){
        val tmpList = ArrayList<Crosswalk>()
        var roadName = ""

        filteredList?.forEach {
            if (roadName != it.roadName){
                roadName = it.roadName
                tmpList.add(Crosswalk(0, "", 0.0, "", null, null, roadName, -1, null))
            }
            tmpList.add(it)
        }

        filteredList = tmpList
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(viewGoup: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGoup.context)

        return if (type == 0){
            val binding = HeaderItemBinding.inflate(inflater, viewGoup, false)
            ViewHolderHeader(binding)

//                    ViewHolderHeader( LayoutInflater.from(viewGoup.context)
//                    .inflate(R.layout.header_item, viewGoup, false))
        } else {
            val binding = CrosswalkItemBinding.inflate(inflater, viewGoup, false)
            ViewHolderCrosswalk(binding)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val item = filteredList?.get(position)
        if (item?.getId() == 0){
            return 0
        } else {
            return 1
        }
    }

    override fun getItemCount(): Int {
        return if (filteredList == null){
            0
        } else {
            filteredList!!.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredList?.get(position)
        if (holder is ViewHolderHeader){
            if (filteredList!=null) {
                holder.setText(item?.localName)
            }
        }
        if (holder is ViewHolderCrosswalk) {
            holder.binding.tvCrosswalkName.text = item?.searchName
            if (item != null) {
                holder.binding.root.setOnClickListener { clickListener(item) }
            }
        }
    }

    class ViewHolderCrosswalk(val binding: CrosswalkItemBinding) : RecyclerView.ViewHolder(binding.root)


}
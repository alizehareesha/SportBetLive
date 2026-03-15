package com.sportbetlive.sgdsn.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sportbetlive.sgdsn.R
import com.sportbetlive.sgdsn.util.Country

class CountryAdapter(
    private var countries: List<Country>,
    private val onCountrySelected: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flagText: TextView = itemView.findViewById(R.id.countryFlagText)
        val nameText: TextView = itemView.findViewById(R.id.countryNameText)
        val codeText: TextView = itemView.findViewById(R.id.countryCodeTextItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        holder.flagText.text = country.flagEmoji
        holder.nameText.text = country.name
        holder.codeText.text = country.phoneCode
        
        holder.itemView.setOnClickListener {
            onCountrySelected(country)
        }
    }

    override fun getItemCount(): Int = countries.size

    fun updateList(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }
}

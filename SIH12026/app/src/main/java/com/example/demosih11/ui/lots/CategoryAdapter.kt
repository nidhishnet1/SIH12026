package com.example.demosih11.ui.lots

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.demosih11.R

data class EwasteCategory(val name: String, val iconRes: Int)

class CategoryAdapter(context: Context, categories: List<EwasteCategory>) :
    ArrayAdapter<EwasteCategory>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_ewaste_category, parent, false
        )
        val category = getItem(position)
        val icon = view.findViewById<ImageView>(R.id.imgCategoryIcon)
        val name = view.findViewById<TextView>(R.id.textCategoryName)

        category?.let {
            icon.setImageResource(it.iconRes)
            name.text = it.name
        }
        return view
    }
}
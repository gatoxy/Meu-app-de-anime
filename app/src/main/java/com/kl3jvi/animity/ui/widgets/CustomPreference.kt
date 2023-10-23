package com.kl3jvi.animity.ui.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder

class CustomPreference(context: Context, attrs: AttributeSet) :
    Preference(context, attrs) {
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val summaryView = holder.findViewById(android.R.id.summary) as TextView?
        summaryView?.setTextColor(
            ColorStateList.valueOf(
                Color.parseColor("#A0AAB5"),
            ),
        ) // Set your desired color
    }
}

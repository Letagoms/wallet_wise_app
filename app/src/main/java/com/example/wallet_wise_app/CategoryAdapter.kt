package com.example.wallet_wise_app

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_wise_app.models.Category

class CategoryAdapter(private var categories: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var spentMap: Map<Int, Double> = emptyMap()

    fun setSpentData(map: Map<Int, Double>) {
        spentMap = map
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconBackground: LinearLayout = itemView.findViewById(R.id.iconBackground)
        val categoryIcon: ImageView      = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView       = itemView.findViewById(R.id.categoryName)
        val categoryStatus: TextView     = itemView.findViewById(R.id.categoryStatus)
        val categorySpent: TextView      = itemView.findViewById(R.id.categorySpent)
        val categoryGoal: TextView       = itemView.findViewById(R.id.categoryGoal)
        val categoryProgress: ProgressBar = itemView.findViewById(R.id.categoryProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        val context  = holder.itemView.context
        val spent    = spentMap[category.id] ?: 0.0

        holder.categoryIcon.setImageResource(category.iconResId)
        holder.iconBackground.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, category.colorResId))
        holder.categoryName.text = category.name
        holder.categoryGoal.text = "of R%.2f–R%.2f".format(category.minGoal, category.maxGoal)

        val isOverBudget = spent > category.maxGoal
        holder.categorySpent.text = "R%.2f".format(spent)

        if (isOverBudget) {
            val overBy = spent - category.maxGoal
            holder.categoryStatus.text    = "Over budget by R%.2f".format(overBy)
            holder.categoryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            holder.categorySpent.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            holder.categoryProgress.progressDrawable =
                ContextCompat.getDrawable(context, R.drawable.progress_bar_red)
            holder.categoryProgress.progress = 100
        } else {
            val remaining = category.maxGoal - spent
            holder.categoryStatus.text    = "R%.2f remaining".format(remaining)
            holder.categoryStatus.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            holder.categorySpent.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            holder.categoryProgress.progressDrawable =
                ContextCompat.getDrawable(context, R.drawable.progress_bar_blue)

            val progress = if (category.maxGoal > 0)
                ((spent / category.maxGoal) * 100).toInt().coerceIn(0, 100)
            else 0
            holder.categoryProgress.progress = progress
        }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
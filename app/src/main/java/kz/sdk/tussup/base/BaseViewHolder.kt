package kz.sdk.tussup.base
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kz.sdk.tussup.models.Order

abstract class BaseViewHolder<VB : ViewBinding, T>(protected open val binding: VB) :
    RecyclerView.ViewHolder(binding.root) {
    abstract fun bindView(item: T)
}

abstract class BaseOrderViewHolder<VB : ViewBinding>(override val binding: VB) :
    BaseViewHolder<VB, Order>(binding)

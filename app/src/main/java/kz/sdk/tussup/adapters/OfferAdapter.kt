package kz.sdk.tussup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseOrderViewHolder
import kz.sdk.tussup.databinding.ItemOfferBinding
import kz.sdk.tussup.models.Order
import coil.load

class OfferAdapter:ListAdapter<Order, BaseOrderViewHolder<*>>(OfferDiffUtils()) {


    var itemClick:((Order) ->Unit)? = null
    class OfferDiffUtils:DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseOrderViewHolder<*> {
        return OfferViewHolder(
            ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseOrderViewHolder<*>, position: Int) {
        holder.bindView(getItem(position))
    }
    inner class OfferViewHolder(binding:ItemOfferBinding):BaseOrderViewHolder<ItemOfferBinding>(binding) {
        override fun bindView(item: Order) {
            with(binding){
                backgroundImg.load(item.imageUrl){
                    placeholder(R.drawable.placeholder)
                }
                price.text = item.price?.toInt().toString()+" ₸"
                amountText.text = "${item.amount} left"
                offerTitle.text  = item.title
                pickUpTime.text = "pick up today at ${item.pickUpTimeFrom}"
            }
            itemView.setOnClickListener {
                itemClick?.invoke(item)
            }
        }
    }
}
//package com.example.myapplication;
//
//import java.util.List;
//
//public class CustomRecyclerAdapter (private val values:List<String>):
//        RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
//
//        override fun getItemCount() = values.size
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.recyclerview_item, parent, false)
//        return MyViewHolder(itemView)
//        }
//
//        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.largeTextView?.text = values[position]
//        holder.smallTextView?.text = "кот"
//        }
//
//class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var largeTextView: TextView? = null
//        var smallTextView: TextView? = null
//
//        init {
//        largeTextView = itemView?.findViewById(R.id.textViewLarge)
//        smallTextView = itemView?.findViewById(R.id.textViewSmall)
//        }
//        }
//        }
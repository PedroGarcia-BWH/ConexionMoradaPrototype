package com.example.conexionmorada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.forocardview.view.*

class ForoAdapter(val chatClick: (Foro) -> Unit): RecyclerView.Adapter<ForoAdapter.ForoViewHolder>() {

    private var messages: List<Foro> = emptyList()

        fun setData(list:   List<Foro> ){
            messages = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForoViewHolder {
            return ForoViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.forocardview,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ForoViewHolder, position: Int) {
            /*messages.forEach{
                holder.itemView.temaTexto.text = it.tema
            }*/
            holder.itemView.temaTexto.text = messages[position].tema

            //position++;;
           // holder.itemView.temaTexto.text = messages[position].tema
            holder.itemView.setOnClickListener {
                chatClick(messages[position])
            }
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        class ForoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    }
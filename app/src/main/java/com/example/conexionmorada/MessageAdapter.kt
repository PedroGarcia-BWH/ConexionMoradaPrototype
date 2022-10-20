package com.example.conexionmorada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.mensajecardview.view.*

class MessageAdapter(): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Mensaje> = emptyList()

    fun setData(list: List<Mensaje>){
        messages = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.mensajecardview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
       val message = messages[position]
            holder.itemView.nickname.text = message.nickname
            holder.itemView.cuerpoMensaje.text = message.cuerpoMensaje
            holder.itemView.horaMensaje.text = message.hora
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}
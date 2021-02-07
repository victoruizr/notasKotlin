package com.example.recyclerviewkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Modelo.Nota
import com.example.firebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_nota.view.*


class RecyclerAdapter(
    val context: Context,
    val listaNotas: List<Nota>,
    private val itemClickListener: ValueEventListener,
    val id: Int,
    val firebaseAuth: FirebaseAuth,
    val db: DatabaseReference
) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return NotasViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_nota,
                parent,
                false
            )
        )
    }

    interface OnNotaClickListener {

    }


    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is NotasViewHolder)
            holder.bind(listaNotas[position], position)
        else
            throw IllegalArgumentException("Error viewHolder errorneo")
    }

    inner class NotasViewHolder(itemView: View) : BaseViewHolder<Nota>(itemView) {
        override fun bind(item: Nota, position: Int) {
            itemView.contenido.text = item.contenido.toString()

            itemView.contenido.setOnClickListener {

                if (id==1) {
                    confirmar(item)

                } else {
                    val bundle = Bundle()
                    bundle.putSerializable("nota", item)
                    bundle.putInt("posicion", position)
                    Navigation.findNavController(itemView)
                        .navigate(R.id.action_nav_home_to_info_Mensaje, bundle)
                }
            }
        }
    }

    private fun confirmar(item: Nota) {
        val dialogo1: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogo1.setTitle("Borrar")
        dialogo1.setMessage("¿ Seguro que desea eliminar de manera permanente la nota ?")
        dialogo1.setCancelable(false)
        dialogo1.setPositiveButton("Confirmar",
            DialogInterface.OnClickListener {
                    dialogo1, id -> aceptar(item) })
        dialogo1.setNegativeButton("Cancelar",
            DialogInterface.OnClickListener {dialogo1, id -> Toast.makeText(context,"No se borro",Toast.LENGTH_SHORT).show() })
        dialogo1.show()
    }

    private fun aceptar(item: Nota) {
        db.child("Usuarios").child(firebaseAuth.currentUser?.uid!!).child("listaEliminados")
            .child(item.posicion.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.ref.removeValue()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    override fun getItemCount(): Int = listaNotas.size

}
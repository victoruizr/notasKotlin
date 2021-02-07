package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Modelo.Nota
import com.example.recyclerviewkotlin.RecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GalleryFragment : Fragment() {
    private lateinit var nota: Nota
    private lateinit var db: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        recyclerView= root.findViewById(R.id.recylerEliminados)

        cargarNotas(root,this.id)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference()
    }


    private fun cargarNotas(root: View, id: Int) {

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        firebaseAuth.currentUser?.getUid()?.let {
            db.child("Usuarios").child(it).child("listaEliminados")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listaNotas: java.util.ArrayList<Nota> = java.util.ArrayList<Nota>()
                        listaNotas.clear()
                        for (xNota in snapshot.children) {
                            nota = xNota.getValue(Nota::class.java)!!
                            listaNotas.add(nota)
                        }
                        recyclerView.adapter =
                            RecyclerAdapter( root.context, listaNotas, this,1,firebaseAuth,db )


                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

}
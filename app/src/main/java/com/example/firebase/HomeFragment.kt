package com.example.firebase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Modelo.Nota
import com.example.firebase.Modelo.Usuarios
import com.example.recyclerviewkotlin.RecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), RecyclerAdapter.OnNotaClickListener {
    private lateinit var nota: Nota
    private lateinit var imagen: String
    private lateinit var photoURl: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    var activityX: MainActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val addNote: ImageView = root.findViewById(R.id.addNote)
        recyclerView= root.findViewById(R.id.recycler)

        usuario();
        cargarNotas(root)

        addNote.setOnClickListener {
            this.findNavController().navigate(R.id.action_nav_home_to_anadirNota)
        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference()



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityX = context as MainActivity
    }


    private fun cargarNotas(root: View) {

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
              context,
             DividerItemDecoration.VERTICAL
        )
        )
        firebaseAuth.currentUser?.getUid()?.let {
            db.child("Usuarios").child(it).child("listaNotas")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listaNotas: java.util.ArrayList<Nota> = java.util.ArrayList<Nota>()
                        listaNotas.clear()
                        for (xNota in snapshot.children) {
                            nota = xNota.getValue(Nota::class.java)!!
                            listaNotas.add(nota)
                        }
                       recyclerView.adapter =
                            RecyclerAdapter(root.context, listaNotas, this, 0, firebaseAuth, db)


                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }


    private fun usuario() {
        db.child("Usuarios").child(firebaseAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val email = snapshot.child("email").value.toString()
                        photoURl = snapshot.child("imagen").value.toString()
                        activityX?.changeNavHeaderData(email, photoURl)
                    } else {
                        crearUsuario()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun crearUsuario() {
        if (firebaseAuth.currentUser!!.photoUrl == null) {
            imagen =
                "https://firebasestorage.googleapis.com/v0/b/fir-kotlin-52c50.appspot.com/o/perfil.png?alt=media&token=32cf6ebe-e333-4538-b9b8-1946f9ba328d"
        } else {
            imagen = firebaseAuth.currentUser!!.photoUrl.toString()
        }


        val listaNotas: MutableList<Nota> = ArrayList<Nota>()
        val listaNotasBorradas: MutableList<Nota> = ArrayList<Nota>()
        val nota = Nota()
        listaNotas.add(nota)
        listaNotasBorradas.add(nota)
        val usu = firebaseAuth.currentUser!!.email?.let {
            Usuarios(
                it,
                imagen,
                listaNotas as ArrayList<Nota>,
                listaNotasBorradas as ArrayList<Nota>
            )
        }

        db.child("Usuarios").child(firebaseAuth.currentUser!!.uid).setValue(usu)
    }
}
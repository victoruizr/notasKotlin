package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.firebase.Modelo.Nota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList


class Info_Mensaje : Fragment() {

    private lateinit var nota: Nota
    private var myInt: Int = 0
    private lateinit var n: Nota
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            myInt = bundle.getInt("posicion")

            n = bundle.getSerializable("nota") as Nota
        }

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_info__mensaje, container, false)
        val texto: TextView = root.findViewById(R.id.textMensaje)
        val eliminar: Button = root.findViewById(R.id.delete)
        texto.text = n.contenido.toString()

        eliminar.setOnClickListener {
            eliminarNota(root);
            escribirNotas()
            root.findNavController().navigate(R.id.action_info_Mensaje_to_nav_home)
        }


        return root
    }

    private fun escribirNotas() {
        firebaseAuth.currentUser?.getUid()?.let {
            db.child("Usuarios").child(it).child("listaEliminados")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listaNotas: ArrayList<Nota> = ArrayList<Nota>()
                        listaNotas.clear()
                        for (xNota in snapshot.children) {
                            nota = xNota.getValue(Nota::class.java)!!
                            listaNotas.add(nota)
                        }

                        var notas = Nota(n.contenido, listaNotas.size)

                        db.child("Usuarios").child(firebaseAuth.currentUser!!.getUid())
                                .child("listaEliminados")
                                .child(listaNotas.size.toString()).setValue(notas)



                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }


    private fun eliminarNota(root: View) {
        db.child("Usuarios").child(firebaseAuth.currentUser?.uid!!).child("listaNotas")
            .child(n.posicion.toString() ).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.ref.removeValue()
                }

                override fun onCancelled(error: DatabaseError) {}
            })


    }
}

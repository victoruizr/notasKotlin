package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.firebase.Modelo.Nota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_anadir_nota.*
import java.util.*


class AnadirNota : Fragment() {


    private var resultado: Int = 0
    private lateinit var not: Nota
    private var numero: String = ""
    private lateinit var nota: Nota
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_anadir_nota, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        db = FirebaseDatabase.getInstance().getReference()

        val addNotes: ImageView = root.findViewById(R.id.imageButton3)

        addNotes.setOnClickListener {
            escribirNotas(root)
        }

        return root
    }

    private fun escribirNotas(root: View) {
        firebaseAuth.currentUser?.getUid()?.let {
            db.child("Usuarios").child(it).child("listaNotas")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val listaNotas: ArrayList<Nota> = ArrayList<Nota>()
                        listaNotas.clear()
                        for (xNota in snapshot.children) {
                            nota = xNota.getValue(Nota::class.java)!!
                            val add = listaNotas.add(nota)
                            numero = xNota.key.toString()
                        }
                        if (!textoNota.text.isEmpty()) {

                            if (numero.equals("")) {
                                not = Nota(textoNota.text.toString(), 0)
                            } else {

                                var n = numero.toInt()

                                resultado = n + 1


                                not = Nota(textoNota.text.toString(), resultado)
                            }


                            db.child("Usuarios").child(firebaseAuth.currentUser!!.getUid())
                                .child("listaNotas")
                                .child(resultado.toString()).setValue(not)
                            numero += 1
                            root.findNavController().navigate(R.id.action_anadirNota_to_nav_home)

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    }
}

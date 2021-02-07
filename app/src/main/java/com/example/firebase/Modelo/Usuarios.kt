package com.example.firebase.Modelo

import com.google.firebase.auth.FirebaseAuth
import java.sql.Array

data class Usuarios(var email: String,val imagen: String,var notas: ArrayList<Nota>,var notasBorradas:ArrayList<Nota>) {

}
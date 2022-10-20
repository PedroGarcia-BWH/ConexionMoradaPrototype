package com.example.adictless1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegisterActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)

        val rechazar =findViewById<Button>(R.id.decline)
        rechazar.setOnClickListener {
            val intento1 = Intent(this, MainActivity::class.java)
            startActivity(intento1)
        }
        val aceptar =findViewById<Button>(R.id.accept)
        aceptar.setOnClickListener {
            val intento2 = Intent(this, RegisterActivity2::class.java)
            startActivity(intento2)
        }
    }
}
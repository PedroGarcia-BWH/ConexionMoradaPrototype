package com.example.conexionmorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conditions)

        val rechazar =findViewById<Button>(R.id.decline)
        rechazar.setOnClickListener {
            val intento1 = Intent(this, MainActivity::class.java)
            startActivity(intento1)
            finish()
        }
        val aceptar =findViewById<Button>(R.id.accept)
        aceptar.setOnClickListener {
            val intento2 = Intent(this, RegisterActivity::class.java)
            startActivity(intento2)
            finish()
        }
    }
}
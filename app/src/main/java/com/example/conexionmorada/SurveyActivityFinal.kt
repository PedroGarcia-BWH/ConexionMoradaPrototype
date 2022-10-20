package com.example.conexionmorada

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

class SurveyActivityFinal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_final)
        var RDactivate = false
        val redesLayout = findViewById<LinearLayout>(R.id.RedesLayout)
        redesLayout.visibility = View.GONE

        val redesActivate = findViewById<RadioButton>(R.id.p3Yes)
        val redesDeactivate = findViewById<RadioButton>(R.id.p3No)

        redesActivate.setOnClickListener{
            redesLayout.visibility = View.VISIBLE
            RDactivate = true
        }
        redesDeactivate.setOnClickListener{
            redesLayout.visibility = View.GONE
            RDactivate = false
        }

        val apuestasLayout = findViewById<LinearLayout>(R.id.layoutApuesta)
        apuestasLayout.visibility = View.GONE

        val apuestasActivate = findViewById<RadioButton>(R.id.p10Yes)
        val apuestasDeactivate = findViewById<RadioButton>(R.id.p10No)

        apuestasActivate.setOnClickListener{
            apuestasLayout.visibility = View.VISIBLE
        }
        apuestasDeactivate.setOnClickListener{
            apuestasLayout.visibility = View.GONE
        }
        val juegosLayout = findViewById<LinearLayout>(R.id.layoutJuegos)
        juegosLayout.visibility = View.GONE

        val juegosActivate = findViewById<RadioButton>(R.id.p15Yes)
        val juegosDeactivate = findViewById<RadioButton>(R.id.p15No)

        juegosActivate.setOnClickListener{
            juegosLayout.visibility = View.VISIBLE
        }
        juegosDeactivate.setOnClickListener{
            juegosLayout.visibility = View.GONE
        }

        var contRedesSociales=0

        var contApuesta=0

        var contGames=0


        val p6RS = findViewById<RadioButton>(R.id.p6No)
        p6RS.setOnClickListener{
            contRedesSociales++
        }

        val p7RS = findViewById<RadioButton>(R.id.p7Yes)
        p7RS.setOnClickListener{
            contRedesSociales++
        }

        val p8RS = findViewById<RadioButton>(R.id.p8Yes)
        p8RS.setOnClickListener{
            contRedesSociales++
        }
        val p13A = findViewById<RadioButton>(R.id.p13Yes)
        p13A.setOnClickListener{

            contApuesta++
        }

        val p12A = findViewById<RadioButton>(R.id.p12Yes)
        p12A.setOnClickListener{
            contApuesta++
        }

        val p11A = findViewById<RadioButton>(R.id.p11Yes)
        p11A.setOnClickListener{
            contApuesta++
        }

        val p14A = findViewById<RadioButton>(R.id.p14Yes)
        p14A.setOnClickListener{
            contApuesta++
        }

        val p16J = findViewById<RadioButton>(R.id.p16Online)
        p16J.setOnClickListener{
            contGames++
        }

        val p18J = findViewById<RadioButton>(R.id.p18Yes)
        p18J.setOnClickListener{
            contGames++
        }

        val p19J = findViewById<RadioButton>(R.id.p19Yes)
        p19J.setOnClickListener{
            contGames++
        }

        val p29J = findViewById<RadioButton>(R.id.p29Yes)
        p29J.setOnClickListener{
            contGames++
        }

        val continuar =findViewById<Button>(R.id.bottonSurvey)
        continuar.setOnClickListener {

          if (RDactivate == true) {
              var RD = findViewById<EditText>(R.id.nRedesSociales)
              var nRD = RD.getText().toString()

              contRedesSociales += Integer.parseInt(nRD)
          }
            val p21J = findViewById<RadioButton>(R.id.p21Yes)
            p21J.setOnClickListener{
                if(contGames!= 0) contGames += 100
                if(contApuesta != 0) contApuesta += 100
                if(contRedesSociales != 0) contRedesSociales +=100
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("RESULTADO")
            if (contRedesSociales >=5 && contApuesta >=2 && contGames >=2){
                builder.setMessage("Puede necesitar ayuda sobre Redes Sociales, Apuestas y Videojuegos")
            }
            else if(contRedesSociales >=5 && contApuesta >=2) builder.setMessage("Puede necesitar ayuda sobre Redes Sociales y Apuestas")
            else if(contApuesta >=2 && contGames >=2) builder.setMessage("Puede necesitar ayuda sobre Apuestas y VideoJuegos")
            else if(contRedesSociales >=2 && contGames >=2) builder.setMessage("Puede necesitar ayuda sobre Redes Sociales y Videojuegos")
            else if(contRedesSociales >=5) builder.setMessage("Puede necesitar ayuda sobre Redes Sociales")
            else if(contApuesta >=2) builder.setMessage("Puede necesitar ayuda sobre Apuestas")
            else if(contGames >=2) builder.setMessage("Puede necesitar ayuda sobre Videojuegos")
            else builder.setMessage("Puede que no necesite ayuda")

            builder.setCancelable(true)

            builder.setPositiveButton("Continuar", DialogInterface.OnClickListener{ dialog, which ->
                val continuarAct = Intent(this, RegisterActivity2::class.java)
                startActivity(continuarAct)
            })

            val alertDialog = builder.create()
            alertDialog.show();
        }
    }
}
package es.uca.conexionmorada

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val newId = getIntent().getExtras()?.getInt("parametro")
        var titulo = findViewById<TextView>(R.id.tituloArticulo)
        val subTitulo= findViewById<TextView>(R.id.subTituloArticulo)
        val cuerpoArticulo = findViewById<TextView>(R.id.cuerpoArticulo)
        val foto = findViewById<ImageView>(R.id.imagenArticulo)
        val context = this


        when(newId)
        {
            1-> {
                titulo.text = getString(R.string.tituloArticulo1)
                subTitulo.text = getString(R.string.subTituloArticulo1)
                cuerpoArticulo.text = getString(R.string.cuerpoArticulo1)
                foto.setImageResource(R.drawable.adiccion)
            }
            2->
            {
                titulo.text = getString(R.string.tituloArticulo2)
                subTitulo.text = getString(R.string.subTituloArticulo2)
                cuerpoArticulo.text = getString(R.string.cuerpoArticulo2)
                foto.setImageResource(R.drawable.img_2)
                (titulo.layoutParams as ConstraintLayout.LayoutParams).apply {
                    // individually set text view any side margin
                    topMargin = 0.dpToPixels(context)
                }
            }

            3-> {
                titulo.text = getString(R.string.tituloArticulo3)
                subTitulo.text = getString(R.string.subTituloArticulo3)
                cuerpoArticulo.text = getString(R.string.cuerpoArticulo3)
                foto.setImageResource(R.drawable.article3)
            }
            4-> {
                titulo.text = getString(R.string.tituloArticulo4)
                subTitulo.text = getString(R.string.subTituloArticulo4)
                cuerpoArticulo.text = getString(R.string.cuerpoArticulo4)
                foto.setImageResource(R.drawable.article4)
            }
            else->{}
        }
    }
}

fun Int.dpToPixels(context: Context):Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
).toInt()
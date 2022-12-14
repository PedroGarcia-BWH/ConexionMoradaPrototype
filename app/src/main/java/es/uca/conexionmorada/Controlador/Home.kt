package es.uca.conexionmorada.Controlador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import es.uca.conexionmorada.NewsActivity
import es.uca.conexionmorada.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {

    val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    companion object{
        private var TAG = "DocSnippets"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        val user = auth.currentUser
        val doc_ref = user?.let { db.collection("users").document(it.uid) }
            doc_ref!!.get()
                .addOnSuccessListener { document ->
                    if (document.data != null) {
                        Log.d(TAG, "Datos Recibidos desde la Base de Datos")
                        val data_user= document.data
                        val username = data_user?.get("username")
                        val login_usuario = view?.findViewById<TextView>(R.id.alias)
                        login_usuario?.text = "Bienvenido\n" + username

                        val level_db = data_user?.get("level").toString().toFloat()
                        val exp = (level_db % 1).toBigDecimal()

                        val level = level_db.toInt().toBigDecimal()
                        val level_usuario = view?.findViewById<TextView>(R.id.LvlTextView)
                        level_usuario?.text = "Nivel " + level

                        val multiplier = 100

                        val exp_total = level.multiply(BigDecimal(multiplier))
                        val exp_actual = exp.multiply(BigDecimal(multiplier)).multiply(level).setScale(0, BigDecimal.ROUND_HALF_UP)

                        val progress_bar = view?.findViewById<ProgressBar>(R.id.progressBar)
                        progress_bar?.max = exp_total.toInt()
                        progress_bar?.progress = exp_actual.toInt()

                    } else {
                        Log.d(TAG, "No existe dicho documento en la Base de Datos")
                        val usuario = "Invitado"

                        if(usuario == "Invitado")
                        {
                            val login_usuario = view?.findViewById<TextView>(R.id.alias)
                            login_usuario?.text = "Bienvenido\n" + usuario
                            val settings = view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
                            settings!!.visibility = View.GONE
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

        val an1CardView = view?.findViewById<CardView>(R.id.announce1)
        Log.d("View", an1CardView.toString())
        an1CardView?.setOnClickListener {
            val an1Ac = Intent(activity, NewsActivity::class.java)
            an1Ac.putExtra("parametro", 1);

            activity?.startActivity(an1Ac)
        }


        val an2CardView = view?.findViewById<CardView>(R.id.announce2)
        an2CardView?.setOnClickListener {
            val an2Ac = Intent(activity, NewsActivity::class.java)
            an2Ac.putExtra("parametro", 2);

            activity?.startActivity(an2Ac)
        }

        val an3CardView = view?.findViewById<CardView>(R.id.announce3)
        an3CardView?.setOnClickListener {
            val an3Ac = Intent(activity, NewsActivity::class.java)
            an3Ac.putExtra("parametro", 3);

            activity?.startActivity(an3Ac)
        }

        val an4CardView = view?.findViewById<CardView>(R.id.announce6)
        an4CardView?.setOnClickListener {
            val an4Ac = Intent(activity, NewsActivity::class.java)
            an4Ac.putExtra("parametro", 4);

            activity?.startActivity(an4Ac)
        }

        val settings =  view?.findViewById<FloatingActionButton>(R.id.ruedaSettings)
        settings?.setOnClickListener {
            val settingsAct = Intent(activity, SettingsActivity::class.java)
            activity?.startActivity(settingsAct)
        }

    }

}
package es.uca.conexionmorada.Controlador

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conexionmorada.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_forum.*

/**
 * A simple [Fragment] subclass.
 * Use the [Forum.newInstance] factory method to
 * create an instance of this fragment.
 */
class Forum : Fragment() {

    private lateinit var auth: FirebaseAuth
    var db = Firebase.firestore

    companion object {
        var TAG = "DocSnippets"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forum, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth
        rvForo.layoutManager = LinearLayoutManager(activity)
        rvForo.adapter  =
            ForoAdapter { chat ->
                foroSelected(chat)
            }

        val user = auth.currentUser
        val doc_ref = user?.let { db.collection("users").document(it.uid) }

        doc_ref!!.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(Forum.TAG, "Datos Recibidos desde la Base de Datos")
                    val textoSesion = view?.findViewById<TextView>(R.id.SOON)
                    textoSesion!!.visibility = View.GONE

                    val candado = view?.findViewById<ImageView>(R.id.Candado)
                    candado!!.visibility = View.GONE
                } else {
                    Log.d(Forum.TAG, "No existe dicho documento en la Base de Datos")
                    val usuario = "Invitado"

                    val compForo = view?.findViewById<Group>(R.id.foro)
                    compForo!!.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.d(Forum.TAG, "get failed with ", exception)
            }
        //----------------FORO REDES SOCIALES----------------
        val chatRD = view?.findViewById<CardView>(R.id.fRedesSociales)
        chatRD?.setOnClickListener() {
            val chatIntent = Intent(activity, ChatActivity::class.java)
            chatIntent.putExtra("nombreTema", FGeneralRD.text);
            activity?.startActivity(chatIntent)
        }
        //----------------FORO VIDEOJUEGOS----------------
        val chatV = view?.findViewById<CardView>(R.id.fVideojuegos)
        chatV?.setOnClickListener() {
            val chatIntent = Intent(activity, ChatActivity::class.java)
            chatIntent.putExtra("nombreTema", FGVideoJuegos.text);
            activity?.startActivity(chatIntent)
        }
        //----------------FORO APUESTAS----------------
        val chatA = view?.findViewById<CardView>(R.id.fApuestas)
        chatA?.setOnClickListener() {
            val chatIntent = Intent(activity, ChatActivity::class.java)
            chatIntent.putExtra("nombreTema", FGeneralAp.text);
            activity?.startActivity(chatIntent)
        }


        val adding = view?.findViewById<FloatingActionButton>(R.id.addTag)
        adding?.setOnClickListener() {
            val intent = Intent(activity,newTemaActivity::class.java)
            activity?.startActivity(intent)
        }

        db.collection("foro").get()
            .addOnSuccessListener { messages ->
                var listForo : List<Foro> = emptyList()
                var cont = 0
                for (document in messages) {
                    if (cont > 2) {
                        listForo += (Foro(document.id))
                    }
                    cont++;
                    //(rvForo.adapter as ForoAdapter).setData(listForo)
                }
                (rvForo.adapter as ForoAdapter).setData(listForo)
            }

        db.collection("foro")
            .addSnapshotListener { messages, error ->
                if (error == null) {
                    messages?.let {
                        var listForo: List<Foro> = emptyList()
                        var cont = 0
                        var aux = messages.documents.forEach()
                        {
                            //if (it.) { //agregar que si es a == 0 entonces no lo  muestre
                                listForo += Foro(it.id.toString())
                            //}
                            //cont++;
                        }

                        listForo += Foro(it.toString())
                        (rvForo.adapter as ForoAdapter).setData(listForo)
                    }
                }
            }
    }

    fun foroSelected(foro: Foro) {
        val intent = Intent(activity,ChatActivity::class.java)
        intent.putExtra("nombreTema", foro.tema);
        activity?.startActivity(intent)
    }
}






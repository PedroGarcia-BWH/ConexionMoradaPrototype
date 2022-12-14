package es.uca.conexionmorada

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import es.uca.conexionmorada.Controlador.Forum
import es.uca.conexionmorada.Controlador.Home
import es.uca.conexionmorada.Controlador.PageController
import es.uca.conexionmorada.Controlador.Progress
import com.google.android.material.tabs.TabLayout




class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpTabs()
    }



    private fun setUpTabs(){

        val adapter = PageController(supportFragmentManager)
        adapter.addFragment(Forum(), "Foro")
        adapter.addFragment(Home(), "Inicio")
        adapter.addFragment(Progress(), "Progreso")
        val viewpager = findViewById<ViewPager>(R.id.viewpager)
        val tabs = findViewById<TabLayout>(R.id.tabs)
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)

        tabs.getTabAt(1)!!.setIcon(R.drawable.home_icon)
        tabs.getTabAt(0)!!.setIcon(R.drawable.forum_icon)
        tabs.getTabAt(2)!!.setIcon(R.drawable.progress_icon)
        /* Quitar titulo de las pestañas
        tabs.getTabAt(0)!!.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
        tabs.getTabAt(1)!!.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
        tabs.getTabAt(2)!!.setTabLabelVisibility(TabLayout.TAB_LABEL_VISIBILITY_UNLABELED);
        */

        viewpager.setCurrentItem(1)
    }

    fun recreateFragment(fragment : Fragment){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            supportFragmentManager.beginTransaction().detach(fragment).commitNow()
            supportFragmentManager.beginTransaction().attach(fragment).commitNow()
        }else{
            supportFragmentManager.beginTransaction().detach(fragment).attach(fragment).commitNow()
        }
    }
}
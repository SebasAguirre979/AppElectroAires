package com.tg.electroaires.ui

import android.content.ClipData.Item
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.tg.electroaires.R
import com.tg.electroaires.model.Servicio
import com.tg.electroaires.ui.adapters.ServicioAdapter
import com.tg.electroaires.ui.fragment.AddServicioFragment
import com.tg.electroaires.ui.fragment.BuscarVehiculoFragment
import com.tg.electroaires.ui.fragment.InfoServicioFragment
import com.tg.electroaires.ui.fragment.ServicioFragment
import com.tg.electroaires.ui.fragment.UsuarioFragment
import com.tg.electroaires.ui.fragment.ValoracionesFragment

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //llamando las funcionalidades correspondientes para agregar el menu de navegacion
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Obtener el usuario desde la VariableGlobal
        val singleton = VariableGlobal.getInstance()
        val nombre_usuario = singleton.nombreUsuario

        //Mandar nombre al nav_header
        val headerView = navigationView.getHeaderView(0)
        val textView = headerView.findViewById<TextView>(R.id.nombreUsuario)
        textView.text = "Hola: $nombre_usuario"


        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ServicioFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    //Boton del menu de navegacion para las rutas
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ServicioFragment()).commit()
            R.id.nav_addservicio -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddServicioFragment()).commit()
            R.id.nav_searchvehiculo -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BuscarVehiculoFragment()).commit()
            R.id.nav_valoraciones -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ValoracionesFragment()).commit()
            R.id.nav_user -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsuarioFragment()).commit()
            R.id.nav_logout -> finalizarActivity()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //Opcion para cuando se de al boton de atras cierra navegacion o en su caso toda la actividad
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    //inflando el menu para agregar el toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.buscar)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                if (fragment is ServicioFragment) {
                    fragment.filterServicesByClient(newText)
                }
                return true
            }
        })
        return true
    }

    //Finalizar actividad al dar logout desde el menu de navegacion
    fun finalizarActivity(){
        Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
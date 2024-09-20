package kz.sdk.tussup

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.databinding.ActivityMainBinding
import kz.sdk.tussup.utils.BottomNavigationViewListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomNavigationViewListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBusinessMode(false)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

    }
    override fun showBottomNavigationView(show: Boolean) {
        if (show) {
            binding.bottomNavigation.visibility = View.VISIBLE
        } else {
            binding.bottomNavigation.visibility = View.GONE
        }
    }
    fun setBusinessMode(isBusiness: Boolean) {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.menu.clear()

        if (isBusiness) {
            bottomNavigationView.inflateMenu(R.menu.bottom_menu_business)
            navController.setGraph(R.navigation.nav_graph_business)
        } else {
            bottomNavigationView.inflateMenu(R.menu.bottom_menu)
            navController.setGraph(R.navigation.nav_graph)
        }
    }
}
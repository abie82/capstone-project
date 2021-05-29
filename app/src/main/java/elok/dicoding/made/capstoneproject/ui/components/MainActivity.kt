 package elok.dicoding.made.capstoneproject.ui.components

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import elok.dicoding.made.capstoneproject.MyApplication
import elok.dicoding.made.capstoneproject.R
import elok.dicoding.made.capstoneproject.databinding.ActivityMainBinding
import elok.dicoding.made.core.ui.base.BaseActivity
import elok.dicoding.made.core.utils.ext.gone
import elok.dicoding.made.core.utils.ext.visible
import kotlinx.coroutines.ExperimentalCoroutinesApi

 class MainActivity : BaseActivity<ActivityMainBinding>({ ActivityMainBinding.inflate(it) }) {

     private lateinit var navController: NavController

     @ExperimentalCoroutinesApi
     override fun ActivityMainBinding.onCreate(savedInstanceState: Bundle?) {
         (application as MyApplication).appComponent.inject(this@MainActivity)
         val navHostFragment =
             supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController
         binding.navView.setupWithNavController(navController)

         navController.addOnDestinationChangedListener { _, destination, _ ->
             if (destination.id == R.id.movieDetailFragment) {
                 binding.navView.gone()
             } else {
                 binding.navView.visible()
             }
         }
     }

     override fun observeViewModel() {}
}
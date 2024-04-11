package com.demo.movingtarget.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.demo.movingtarget.R
import com.demo.movingtarget.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentHomeBinding
    private var currentView: View? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.fragment_home, container, false)
            binding = FragmentHomeBinding.bind(currentView!!)
            init()
            setOnClickListener()
        }
        return currentView!!
    }

    private fun init() {




    }

    private fun setOnClickListener() {

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.m1->{
                navController.navigate(R.id.m1Fragment)
            }
            R.id.m2->{
                navController.navigate(R.id.m2Fragment)
            }
            R.id.m3->{
                navController.navigate(R.id.m3Fragment)
            }
            R.id.m4->{
                navController.navigate(R.id.m4Fragment)
            }
            R.id.quick->{
                navController.navigate(R.id.quick)
            }
        }
    }
}
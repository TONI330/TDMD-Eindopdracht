package com.example.sanic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.sanic.databinding.FragmentMainBinding


/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        updateSettings()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        updateSettings()
        super.onResume()
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        updateSettings()
        super.onOptionsMenuClosed(menu)
    }

    private fun updateSettings() {
        //update instructionsVisible
        if(this.activity != null) {
            if(KeyValueStorage.getBoolean(this.activity!!.applicationContext, "showInstructions") == true) {
                this.binding?.instructionsText?.visibility = View.VISIBLE
                return
            }
            this.binding?.instructionsText?.setTextColor(resources.getColor(R.color.theme))
        }


    }
}
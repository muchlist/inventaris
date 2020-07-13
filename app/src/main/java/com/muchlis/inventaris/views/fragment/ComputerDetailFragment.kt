package com.muchlis.inventaris.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.muchlis.inventaris.data.response.SelectOptionResponse
import com.muchlis.inventaris.databinding.FragmentComputerDetailBinding
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.OptionsMarshaller
import com.muchlis.inventaris.views.view_model.ComputerDetailViewModel
import es.dmoral.toasty.Toasty


class ComputerDetailFragment : Fragment() {

    private var _binding: FragmentComputerDetailBinding? = null
    private val bd get() = _binding!!

    private lateinit var viewModel: ComputerDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComputerDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ComputerDetailViewModel::class.java)

        val options = OptionsMarshaller().getOption()
        if (options == null){
            Toasty.error(requireActivity(),"Error Saat Mendapatkan Option",Toasty.LENGTH_LONG).show()
        }

        val locations = options!!.locations

        var locationString = ""
        for (location in locations){
            locationString = locationString + "$location\n"
        }

        bd.fragmentId.text = locationString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.muchlis.inventaris.views.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muchlis.inventaris.databinding.FragmentComputerDetailBinding


class ComputerDetailFragment : Fragment() {

    private var _binding: FragmentComputerDetailBinding? = null
    private val bd get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComputerDetailBinding.inflate(inflater, container, false)
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //HERE PROGRAMM
        bd.fragmentId.text = "TODO"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
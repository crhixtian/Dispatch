package com.gestion.despacho.presentation.validatedPicking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gestion.despacho.MainActivity
import com.gestion.despacho.R
import com.gestion.despacho.databinding.FragmentPickingSuccesBinding
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.SessionManager

class PickingSuccesFragment : Fragment(R.layout.fragment_picking_succes) {

    private lateinit var binding: FragmentPickingSuccesBinding
    private val viewmodel: PickingSuccesViewModel by viewModels()
    lateinit var picking: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        events()
        setupView()
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() = with(binding) {
        viewmodel.picking.observe(viewLifecycleOwner) {
            tvNbrPicking.text = it.nbrpicking
            tvRazSocial.text = it.petitioner
            var controller = ""
            if (it.pickingDet.size == 1) {
                it.pickingDet.forEach { pickingDet ->
                    controller += pickingDet.dispatcher
                }
            } else {
                it.pickingDet.forEach { pickingDet ->
                    controller += getString(R.string.barra)+ pickingDet.dispatcher
                }
            }

            tvController.text = controller
            tvLicense.text = SessionManager().getUser()
            tvDateVerify.text =
                "${SessionManager().getHourVerify()} del ${SessionManager().getDateVerify()}"
            lySuccesPicking.visibility = View.VISIBLE
        }
    }

    private fun init() {
        val bundleReception = requireActivity().intent.extras

        bundleReception?.apply {
            picking = this.getString(Constants.RESPONSE).toString()
        }
        viewmodel.getInfoPicking(picking)
    }

    private fun events() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }
}
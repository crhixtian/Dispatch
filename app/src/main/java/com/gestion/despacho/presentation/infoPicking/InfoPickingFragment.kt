package com.gestion.despacho.presentation.infoPicking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.gestion.despacho.model.ClsPicking
import com.gestion.despacho.MainActivity
import com.gestion.despacho.R
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.DialogManager
import com.gestion.despacho.utils.SessionManager
import com.gestion.despacho.utils.Toast.Toast
import com.gestion.despacho.databinding.DialogLogoutBinding
import com.gestion.despacho.databinding.FragmentInfoPickingBinding

class InfoPickingFragment : Fragment(R.layout.fragment_info_picking) {

    private lateinit var binding : FragmentInfoPickingBinding
    private lateinit var globalView: View
    private val viewModel: InfoPickingViewModel by viewModels()
    private lateinit var id: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInfoPickingBinding.bind(view)
        globalView = view

        arguments?.let {
            id = requireArguments().getString(Constants.PICKING_FRAGMENT).toString()
            loadData()
            observers()
            events()
            setupIconValidate()
        }
    }

    private fun setupIconValidate() = with(binding){
        println(SessionManager().getStatus())
        when(SessionManager().getStatus()){
            1 -> {
                lyValidate.setBackgroundResource(R.drawable.ic_check)
            }
            3 -> {
                lyValidate.setBackgroundResource(R.drawable.ic_warning)
            }
        }
    }
    private fun events() = with(binding){
        includeHeader.imgHome.setOnClickListener {
            logOut(Constants.HOME).show()
        }
        includeHeader.btnLogout.setOnClickListener {
            logOut(Constants.LOG_OUT).show()
        }
        swipeInfo.setOnRefreshListener {
            println(id)
            viewModel.loadData(id)
            swipeInfo.isRefreshing = false
        }

        val callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                logOut(Constants.HOME).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun logOut(origin: String): AlertDialog {
        val bindingAlert = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        val builder =  AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        when(origin) {
            Constants.LOG_OUT -> {
                bindingAlert.btnSiLogOut.setOnClickListener {
                    SessionManager().saveStatuSession(status = false)
                    requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                    alertDialog.dismiss()
                }
            }

            Constants.HOME -> {
                bindingAlert.tvTitleDialog.text = getString(R.string.do_you_want_to_exit_picking)
                bindingAlert.btnSiLogOut.setOnClickListener {
                    requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                    alertDialog.dismiss()
                }
            }
        }
        bindingAlert.btnNoLogout.setOnClickListener {
            alertDialog.dismiss()
        }
        return alertDialog
    }

    private fun loadData(){
        viewModel.getInfoPicking(id = id)
    }

    private fun observers() {
        viewModel.infoPicking?.observe(viewLifecycleOwner){picking ->
            picking?.let {
                setupLyPicking(picking = picking)
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {e ->
            context?.Toast(Mensaje = e)
        }
        viewModel.loader.observe(viewLifecycleOwner) {flag ->
            if (flag) DialogManager.showProgress(requireContext())
            else DialogManager.hideProgress()
        }
        return viewModel.message.observe(viewLifecycleOwner) { m ->
            if( m == "SUCCESS"){
                setupIconValidate()
            }
            context?.Toast(m)
        }
    }

    private fun setupLyPicking(picking: ClsPicking) = with(binding){
        picking.let{
            tvNbrPicking.text = it.Picking
            tvRazSocial.text = it.petitioner
            tvPlate.text = it.plate
            tvDriver.text = it.driver
            tvLicense.text = it.license
            tvDate.text = it.date_deliv
            tvHour.text = it.Hour
            if(it.observation.toString().isNotEmpty()){
                tvObs.text = it.observation
            }
        }
    }
}
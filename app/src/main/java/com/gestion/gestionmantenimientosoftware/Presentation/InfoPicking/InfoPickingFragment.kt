package com.gestion.gestionmantenimientosoftware.Presentation.InfoPicking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.gestion.gestionmantenimientosoftware.Model.ClsPicking
import com.gestion.gestionmantenimientosoftware.MainActivity
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.DialogManager
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import com.gestion.gestionmantenimientosoftware.Utils.Toast.Toast
import com.gestion.gestionmantenimientosoftware.databinding.DialogLogoutBinding
import com.gestion.gestionmantenimientosoftware.databinding.FragmentInfoPickingBinding

class InfoPickingFragment : Fragment(R.layout.fragment_info_picking) {

    private lateinit var binding : FragmentInfoPickingBinding
    private lateinit var globalView: View
    private val viewModel: InfoPickingViewModel by viewModels()
    private lateinit var id: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentInfoPickingBinding.bind(view)
        globalView = view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments != null){
            id = arguments!!.getString(Constants.PICKING_FRAGMENT).toString()

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
            2 ->{
                //requireContext().Toast("Picking por atender")
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
                bindingAlert.tvTitleDialog.text = "¿Desea salir del picking?"
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

    private fun observers() = with(binding){
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
        viewModel.message.observe(viewLifecycleOwner) {m ->
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
            tvObs.text = it.observation
        }
    }
}
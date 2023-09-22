package com.gestion.gestionmantenimientosoftware.Presentation.ConsultPicking

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.gestion.gestionmantenimientosoftware.Model.User
import com.gestion.gestionmantenimientosoftware.Presentation.Picking.PickingActivity
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.DialogManager
import com.gestion.gestionmantenimientosoftware.Utils.PermissionsAwareActivity.getPackageManager
import com.gestion.gestionmantenimientosoftware.Utils.PermissionsAwareActivity.getSystemService
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import com.gestion.gestionmantenimientosoftware.Utils.Toast.Toast
import com.gestion.gestionmantenimientosoftware.databinding.DialogLogoutBinding
import com.gestion.gestionmantenimientosoftware.databinding.FragmentConsultPickingBinding
import com.google.zxing.integration.android.IntentIntegrator
import java.time.LocalTime


class ConsultPickingFragment : Fragment(R.layout.fragment_consult_picking) {

    lateinit var binding: FragmentConsultPickingBinding
    lateinit var globalView: View
    lateinit var globalUser: User
    private val viewModel: ConsultPickingViewModel by viewModels()
    lateinit var nrPicking: String
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashOn = false
    val fragment: Fragment = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentConsultPickingBinding.bind(view)
        globalView = view

        binding.includeHeader.imgHome.visibility = View.GONE

        setupUser()
        events()
        observers()
    }

    private fun setupUser() = with(binding) {
        arguments?.let{
            globalUser = ConsultPickingFragmentArgs.fromBundle(it).objUser

            tvNameUser.text = globalUser.FullName
            tvRol.text = globalUser.RolName
        }
    }

    private fun events() = with(binding) {
        btnGetPicking.setOnClickListener {
            if (edtNbrPicking.text.isNotEmpty()) {
                if (edtNbrPicking.text.length < 10) {
                    viewModel.getPicking(idPicking = edtNbrPicking.text.toString().padStart(10, '0'))
                    //context?.Toast("El número de entrega debe tener 10 dígitos")
                } else {
                    viewModel.getPicking(idPicking = edtNbrPicking.text.toString())
                }
            } else {
                context?.Toast(Mensaje = Constants.ERROR_FIELDS)
            }
        }
        imgScan.setOnClickListener {
            initScanner()
        }
        includeHeader.btnLogout.setOnClickListener {
            logOut().show()
        }
        val callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                exit().show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun observers() = with(binding) {
        viewModel.loader.observe(viewLifecycleOwner) { loader ->
            if (loader) DialogManager.showProgress(requireContext())
            else DialogManager.hideProgress()
        }

        viewModel.nbrPicking.observe(viewLifecycleOwner) { nro ->
            nrPicking = nro
        }

        viewModel.pass.observe(viewLifecycleOwner) { flag ->
            if (flag) {
                goToActivity(picking = nrPicking, user = globalUser)
                edtNbrPicking.setText("")
            }
        }

        viewModel.canceled.observe(viewLifecycleOwner) { canceled ->
            if (canceled) canceledDialog().show()
        }

        viewModel.error.observe(viewLifecycleOwner) { e ->
            context?.Toast(Mensaje = e)
        }

        viewModel.message.observe(viewLifecycleOwner) { m ->
            context?.Toast(Mensaje = m)
        }
    }

    private fun exit(): AlertDialog {
        val bindingAlert = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        bindingAlert.tvTitleDialog.text = "¿Desea salir de la aplicación?"
        bindingAlert.btnSiLogOut.setOnClickListener {
            requireActivity().finishAffinity()
            alertDialog.dismiss()
        }
        bindingAlert.btnNoLogout.setOnClickListener {
            alertDialog.dismiss()
        }
        return alertDialog
    }

    private fun logOut(): AlertDialog {
        val bindingAlert = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        bindingAlert.btnSiLogOut.setOnClickListener {
            SessionManager().saveStatuSession(status = false)
            Navigation.findNavController(globalView).navigate(R.id.action_consultPickingFragment_to_loginFragment)
            alertDialog.dismiss()
        }
        bindingAlert.btnNoLogout.setOnClickListener {
            alertDialog.dismiss()
        }
        return alertDialog
    }

    private fun canceledDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(Constants.CANCELED_PICKING)
        builder.setMessage(Constants.CANCELED_PICKING_DESC)

        return builder.create()
    }

    private fun goToActivity(picking: String, user: User) {

        val bundle = Bundle().apply {
            putString(Constants.PICKING, picking)
            putSerializable(Constants.USER_LOGIN, user)
        }
        val intent = Intent(requireContext(), PickingActivity::class.java)
        intent.apply { putExtras(bundle) }

        startActivity(intent)
    }

    private fun initScanner() {
        val integrator = IntentIntegrator.forSupportFragment(fragment)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Escanea un número de picking")
        integrator.setBeepEnabled(true)
        val hourIni = LocalTime.of(18, 0)
        val hourFin = LocalTime.of(6, 0)
        if(LocalTime.now().isAfter(hourIni) || LocalTime.now().isBefore(hourFin)){
            integrator.setTorchEnabled(true)
        }else{
            integrator.setTorchEnabled(false)
        }
        integrator.initiateScan()
        //IntentIntegrator.forSupportFragment(fragment).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                context?.Toast("Cancelado")
            } else {
                binding.edtNbrPicking.setText("")
                binding.edtNbrPicking.setText(result.contents.padStart(10, '0'))
            }
        } else {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
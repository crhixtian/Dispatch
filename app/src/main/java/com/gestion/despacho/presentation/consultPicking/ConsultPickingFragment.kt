package com.gestion.despacho.presentation.consultPicking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.gestion.despacho.model.User
import com.gestion.despacho.presentation.picking.PickingActivity
import com.gestion.despacho.R
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.DialogManager
import com.gestion.despacho.utils.SessionManager
import com.gestion.despacho.utils.Toast.Toast
import com.gestion.despacho.databinding.DialogLogoutBinding
import com.gestion.despacho.databinding.FragmentConsultPickingBinding
import com.google.zxing.integration.android.IntentIntegrator
import java.time.LocalTime


class ConsultPickingFragment : Fragment(R.layout.fragment_consult_picking) {

    private lateinit var binding: FragmentConsultPickingBinding
    private lateinit var globalView: View
    private lateinit var globalUser: User
    private val viewModel: ConsultPickingViewModel by viewModels()
    private lateinit var nrPicking: String

    private val scanLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            val scanResult = IntentIntegrator.parseActivityResult(result.resultCode, data)
            if (scanResult != null) {
                if (scanResult.contents == null) {
                    context?.Toast(getString(R.string.cancelled))
                } else {
                    binding.edtNbrPicking.setText(scanResult.contents.padStart(10, '0'))
                    viewModel.getPicking(scanResult.contents.padStart(10, '0'))
                }
            }
        }

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
        arguments?.let {
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
        val callback = object : OnBackPressedCallback(true) {
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
                edtNbrPicking.setText(getString(R.string.empty))
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

        bindingAlert.tvTitleDialog.text = getString(R.string.do_you_want_to_exit_the_application)
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
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt(getString(R.string.scan_picking_number))
        integrator.setBeepEnabled(true)
        val hourIni = LocalTime.of(18, 0)
        val hourFin = LocalTime.of(6, 0)
        if (LocalTime.now().isAfter(hourIni) || LocalTime.now().isBefore(hourFin)) {
            integrator.setTorchEnabled(true)
        } else {
            integrator.setTorchEnabled(false)
        }
        scanLauncher.launch(integrator.createScanIntent())
    }
}


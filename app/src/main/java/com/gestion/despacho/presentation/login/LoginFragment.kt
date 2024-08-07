package com.gestion.despacho.presentation.login

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.gestion.despacho.model.User
import com.gestion.despacho.R
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.DialogManager
import com.gestion.despacho.utils.SessionManager
import com.gestion.despacho.utils.Toast.Toast
import com.gestion.despacho.databinding.DialogApiBinding
import com.gestion.despacho.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var globalView: View
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        globalView = view

        //validateSession()
        dummySession()

        init()
        events()
        observers()
    }

    private fun dummySession() {
        val user = User(
            User = "crhixtian",
            FullName = "Crhistian Villantoy",
            RolId = 1,
            RolCode = "ROL",
            RolName = "Verificador"
        )
        val direction =
            LoginFragmentDirections.actionLoginFragmentToConsultPickingFragment(user)
        Navigation.findNavController(globalView).navigate(direction)

    }

    private fun init() = with(binding) {
        tvVersion.text = Constants.APP_VERSION
    }

    private fun events() = with(binding) {
        btnLogin.setOnClickListener {
            if (edtUser.editText?.text!!.isNotEmpty() && edtPass.editText?.text!!.isNotEmpty()) {
                viewModel.authenticate(
                    edtUser.editText?.text.toString(),
                    edtPass.editText?.text.toString()
                )
            } else {
                requireContext().Toast(Constants.ERROR_FIELDS)
            }
        }
        imgLogo.setOnLongClickListener {
            getDialogApi().show()
            true
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }

    private fun observers() {
        viewModel.error.observe(viewLifecycleOwner) {
            requireContext().Toast(it)
        }
        viewModel.loader.observe(viewLifecycleOwner) {
            if (it) DialogManager.showProgress(requireContext())
            else DialogManager.hideProgress()
        }
        viewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                SessionManager().saveObjUser(it)
                val direction =
                    LoginFragmentDirections.actionLoginFragmentToConsultPickingFragment(it)
                Navigation.findNavController(globalView).navigate(direction)
            }
        }
    }

    private fun validateSession() {
        if (SessionManager().getStatusSession() == true) {
            val user = User(
                SessionManager().getUsuario()!!,
                SessionManager().getNameUser()!!,
                SessionManager().getRolIdLogin()!!,
                SessionManager().getRolCode()!!,
                SessionManager().getPerfil()!!
            )
            val direction =
                LoginFragmentDirections.actionLoginFragmentToConsultPickingFragment(user)
            Navigation.findNavController(globalView).navigate(direction)
        }
    }

    private fun getDialogApi(): AlertDialog {
        val bindingAlert = DialogApiBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        bindingAlert.edtApiRest.setText(SessionManager().getBaseUrl())
        bindingAlert.btnAceptarApi.setOnClickListener {
            SessionManager().saveUrl(baseUrl = bindingAlert.edtApiRest.text.toString())
            alertDialog.dismiss()
        }
        bindingAlert.btnCancelarApi.setOnClickListener {
            alertDialog.dismiss()
        }
        return alertDialog
    }
}
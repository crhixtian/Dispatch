package com.android.dispatch.presentation.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.android.dispatch.R
import com.android.dispatch.databinding.DialogApiBinding
import com.android.dispatch.databinding.FragmentLoginBinding
import com.android.dispatch.model.User
import com.android.dispatch.utils.Constants
import com.android.dispatch.utils.DialogManager
import com.android.dispatch.utils.SessionManager
import com.android.dispatch.utils.Toast.Toast

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var globalView: View
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        globalView = view

        validateSession()
        init()
        events()
        observers()
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
package com.gestion.gestionmantenimientosoftware.Presentation.Stevedores

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.gestion.gestionmantenimientosoftware.Model.ClsStevedores
import com.gestion.gestionmantenimientosoftware.MainActivity
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.BaseAdapter
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.DialogManager
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import com.gestion.gestionmantenimientosoftware.Utils.Toast.Toast
import com.gestion.gestionmantenimientosoftware.databinding.*

class StevedoresFragment : Fragment(R.layout.fragment_stevedores) {

    private lateinit var binding : FragmentStevedoresBinding
    private lateinit var globalView: View
    private val viewModel: StevedoresViewModel by viewModels()
    private lateinit var id: String

    private val adapter: BaseAdapter<ClsStevedores> =
        object : BaseAdapter<ClsStevedores>(emptyList()) {
            override fun getViewHolder(parent: ViewGroup): BaseViewHolder<ClsStevedores> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_stevedor, parent, false)
                return object : BaseViewHolder<ClsStevedores>(view) {
                    private val binding: ItemStevedorBinding = ItemStevedorBinding.bind(view)
                    override fun bind(entity: ClsStevedores) = with(binding) {
                        tvNameStevedor.text = entity.nombre
                        tvDocStevedor.text = entity.dni
                        tvMaterialStevedor.text = entity.material

                        when(SessionManager().getRolId()){
                            //Verificador torre de control
                            4, 7 -> {
                                lyDelete.visibility = View.GONE
                                lyEdit.visibility = View.GONE
                            }
                        }
                        lyEdit.setOnClickListener {
                            editStevedor(entity).show()
                        }
                        lyDelete.setOnClickListener {
                            sureDelete(entity).show()
                        }

                        if(SessionManager().getStatus() == 1){
                            lyDelete.visibility = View.GONE
                            lyEdit.visibility = View.GONE
                        }
                    }
                }
            }

        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentStevedoresBinding.bind(view)
        globalView = view

        setupAdapter()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments != null){
            id = arguments!!.getString(Constants.PICKING_FRAGMENT).toString()
            binding.txtNbrPicking.text = id

            loadData()
            observers()
            events()

        }
    }

    private fun events() = with(binding){
        includeHeader.imgHome.setOnClickListener {
            logOut(Constants.HOME).show()
        }
        includeHeader.btnLogout.setOnClickListener {
            logOut(Constants.LOG_OUT).show()
        }
        swipeStevedores.setOnRefreshListener {
            viewModel.loadData(id)
            swipeStevedores.isRefreshing = false
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

        when(origin)
        {
            Constants.LOG_OUT ->{
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
        //viewModel.getStevedores(id)
    }

    private fun observers(){
        viewModel.error.observe(viewLifecycleOwner){e ->
            context?.Toast(e)
        }
        viewModel.loader.observe(viewLifecycleOwner){l ->
            if(l) DialogManager.showProgress(requireContext())
            else DialogManager.hideProgress()
        }
        viewModel.message.observe(viewLifecycleOwner){
            context?.Toast(it)
        }
        /*viewModel.stevedores.observe(viewLifecycleOwner){
            it.observe(viewLifecycleOwner){ list ->
                adapter.updateList(list)
            }
        }*/
        viewModel.stevedores?.observe(viewLifecycleOwner){
            adapter.updateList(it)
        }
    }

    private fun setupAdapter() = with(binding){
        rcvStevedores.adapter = adapter
    }

    private fun sureDelete(entity: ClsStevedores): AlertDialog {
        val bindingAlert = DialogDeleteBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        bindingAlert.tvNameStevedor.text = entity.nombre

        bindingAlert.btnAgree.setOnClickListener {
            viewModel.delete(entity)
            alertDialog.dismiss()
        }
        bindingAlert.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        return alertDialog
    }

    private fun editStevedor(entity: ClsStevedores): AlertDialog {
        val bindingAlert = DialogEditBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        bindingAlert.edtName.setText(entity.nombre)
        bindingAlert.edtDni.setText(entity.dni)

        bindingAlert.btnUpdate.setOnClickListener {
            entity.nombre = bindingAlert.edtName.text.toString()
            entity.dni = bindingAlert.edtDni.text.toString()
            viewModel.edit(entity)

            alertDialog.dismiss()
        }

        return alertDialog
    }
}
package com.gestion.gestionmantenimientosoftware.Presentation.MaterialsPicking

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.gestion.gestionmantenimientosoftware.Model.ClsPickingDetail
import com.gestion.gestionmantenimientosoftware.MainActivity
import com.gestion.gestionmantenimientosoftware.Presentation.ValidatedPicking.ValidatedPickingActivity
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.BaseAdapter
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.Utils.DialogManager
import com.gestion.gestionmantenimientosoftware.Utils.SessionManager
import com.gestion.gestionmantenimientosoftware.Utils.Toast.Toast
import com.gestion.gestionmantenimientosoftware.databinding.*

class MaterialsPickingFragment : Fragment(R.layout.fragment_materials_picking) {

    private lateinit var binding: FragmentMaterialsPickingBinding
    private val viewModel: MaterialPickingViewModel by viewModels()
    private lateinit var globalView: View
    private lateinit var id: String

    private val adapter: BaseAdapter<ClsPickingDetail> =
        object : BaseAdapter<ClsPickingDetail>(emptyList()) {
            override fun getViewHolder(parent: ViewGroup): BaseViewHolder<ClsPickingDetail> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_material, parent, false)
                return object : BaseViewHolder<ClsPickingDetail>(view) {
                    private val binding: ItemMaterialBinding = ItemMaterialBinding.bind(view)

                    @RequiresApi(Build.VERSION_CODES.O)
                    @SuppressLint("SetTextI18n")
                    override fun bind(entity: ClsPickingDetail) = with(binding) {
                        tvMaterial.text = entity.material
                        tvQuantity.text = entity.quantity.toString()
                        tvTon.text = entity.ton.toString()
                        tvWeight.text = entity.weight.toString()
                        tvTypeLoad.text = entity.type_load
                        tvStart.text = entity.startDate
                        tvEnd.text = entity.endDate
                        tvObservation.text = entity.observation

                        if (SessionManager().getStatus() == 1) {
                            btnAddStvedor.visibility = View.GONE
                            btnObservation.visibility = View.GONE
                        } else {
                            root.setOnClickListener {
                                if (SessionManager().getRolId() == 6) {
                                    if (entity.startDate == "") {
                                        starLoad(entity).show()
                                    } else if (entity.endDate!!.isNotEmpty()) {
                                        context?.Toast("Ya se finalizó la carga")
                                    } else {
                                        checkStevedores(entity)
                                    }
                                }
                            }
                        }

                        when (SessionManager().getRolId()) {
                            //Verificador torre de control - Supervisor solo consulta
                            4, 7 -> {
                                btnAddStvedor.visibility = View.GONE
                                btnObservation.visibility = View.GONE
                            }
                        }

                        btnAddStvedor.setOnClickListener { addStevedor(entity).show() }
                        btnObservation.setOnClickListener{
                            if(entity.startDate == ""){
                                context?.Toast("Primero debe iniciar la carga del material")
                            }else{
                                logOut(Constants.OBSERVE_MATERIAL, entity).show()
                            }
                        }
                    }
                }
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMaterialsPickingBinding.bind(view)
        globalView = view

        setupView()
        setupAdapter()
        //binding.btnPdf.visibility = View.VISIBLE
        binding.btnPdf.setOnClickListener {
            sendMail()
        }
    }

    private fun setupView() {
        when (SessionManager().getRolId()) {
            //Despachador responsable - Supervisor solo consulta
            6, 7 -> {
                binding.linearLayoutButton.visibility = View.GONE
                binding.btnValidate.visibility = View.GONE
                binding.btnObserve.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null) {
            id = arguments!!.getString(Constants.PICKING_FRAGMENT).toString()
            binding.tvPicking.text = id

            status()
            observers()
            events()
        }
    }

    private fun status() = with(binding) {
        if (SessionManager().getStatus() == 1) {
            linearLayoutButton.visibility = View.GONE
            btnValidate.visibility = View.GONE
            btnObserve.visibility = View.GONE
            btnPdf.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observers() = with(binding) {
        viewModel.pickingDet?.observe(viewLifecycleOwner) { pickingDet ->
            adapter.updateList(nData = pickingDet)
        }
        viewModel.error.observe(viewLifecycleOwner) { e ->
            context?.Toast(Mensaje = e)
        }
        viewModel.loader.observe(viewLifecycleOwner) { l ->
            if (l) DialogManager.showProgress(requireContext())
            else DialogManager.hideProgress()
        }
        viewModel.message.observe(viewLifecycleOwner) { m ->
            context?.Toast(Mensaje = m)
        }
        viewModel.picking.observe(viewLifecycleOwner) { pickingDet ->
            endLoad(entity = pickingDet).show()
        }
        viewModel.pdf.observe(viewLifecycleOwner) {
            //if(it) sendMail()
        }
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response) sendMail()
        }
        viewModel.success.observe(viewLifecycleOwner) { picking ->
            getSuccess(picking.nbrpicking)
        }
        viewModel.observe.observe(viewLifecycleOwner) {
            if(it) observePicking(id = id).show()
        }
        viewModel.getObserve.observe(viewLifecycleOwner){
            if (it) getObserve()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun sendMail() {
        viewModel.sendMail(id, requireContext())
    }

    private fun getSuccess(picking: String) {
        val bundle = Bundle().apply {
            putString(Constants.RESPONSE, picking)
        }
        val intent = Intent(requireActivity(), ValidatedPickingActivity::class.java)
        intent.apply { putExtras(bundle) }
        requireActivity().startActivity(intent)
    }

    private fun getObserve() {
        requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun events() {
        binding.btnValidate.setOnClickListener {
            logOut(origin = Constants.VALIDATE, null).show()
        }
        binding.btnObserve.setOnClickListener {
            logOut(origin = Constants.OBSERVE, null).show()
            //observePicking(id = id).show()
        }
        binding.includeHeader.imgHome.setOnClickListener {
            logOut(origin = Constants.HOME, null).show()
        }
        binding.includeHeader.btnLogout.setOnClickListener {
            logOut(origin = Constants.LOG_OUT, null).show()
        }
        binding.swipeMaterials.setOnRefreshListener {
            viewModel.loadData(id)
            binding.swipeMaterials.isRefreshing = false
        }
        val callback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                logOut(Constants.HOME, null).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun logOut(origin: String, material: ClsPickingDetail?): AlertDialog {
        val bindingAlert = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        val builder =  AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        when(origin){

            Constants.LOG_OUT -> {

                bindingAlert.tvTitleDialog.text = "¿Seguro que desea cerrar sesión?"

                bindingAlert.btnSiLogOut.setOnClickListener {
                    SessionManager().saveStatuSession(status = false)
                    requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                    alertDialog.dismiss()
                }
            }

            Constants.VALIDATE -> {
                bindingAlert.tvTitleDialog.text = "¿Seguro que desea validar el picking?"

                bindingAlert.btnSiLogOut.setOnClickListener {
                    viewModel.checkPicking(id = id, Constants.VALIDATE)
                    alertDialog.dismiss()
                }
            }

            Constants.OBSERVE -> {

                bindingAlert.tvTitleDialog.text = "¿Seguro que desea observar el picking?"

                bindingAlert.btnSiLogOut.setOnClickListener {
                    viewModel.checkPicking(id = id, Constants.OBSERVE)
                    alertDialog.dismiss()
                }

            }

            Constants.OBSERVE_MATERIAL -> {
                bindingAlert.tvTitleDialog.text = "¿Seguro que desea observar el material?"

                bindingAlert.btnSiLogOut.setOnClickListener {
                    alertDialog.dismiss()
                    observeMaterial(material).show()
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

    private fun setupAdapter() = with(binding) {
        rcvMaterials.adapter = adapter
    }

    private fun checkStevedores(entity: ClsPickingDetail) {
        viewModel.checkStevedores(entity)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observePicking(id: String): AlertDialog {
        val bindingAlert = DialogObservationBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvNroPicking.text = id
        bindingAlert.btnStartLoad.setOnClickListener {
            if (bindingAlert.edtNbrLot.text.toString() == "") {
                context?.Toast("Debe ingresar un motivo")
            } else {
                viewModel.observePicking(id, bindingAlert.edtNbrLot.text.toString())
                alertDialog.dismiss()
            }
        }
        return alertDialog
    }

    private fun observeMaterial(material: ClsPickingDetail?): AlertDialog {
        val bindingAlert = DialogObservationBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvTitle.text = "Material: "
        bindingAlert.tvNroPicking.text = material?.material
        bindingAlert.btnStartLoad.setOnClickListener {
            if (bindingAlert.edtNbrLot.text.toString() == "") {
                context?.Toast("Debe ingresar un motivo")
            } else {
                viewModel.observeMaterial(material, bindingAlert.edtNbrLot.text.toString())
                alertDialog.dismiss()
            }
        }
        return alertDialog
    }

    private fun starLoad(entity: ClsPickingDetail): AlertDialog {
        val bindingAlert = DialogStartloadBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvNameProduct.text = entity.material
        bindingAlert.btnStartLoad.setOnClickListener {
            if(bindingAlert.edtNbrLot.text.isNotEmpty()){
                viewModel.startLoad(entity, bindingAlert.edtNbrLot.text.toString())
                alertDialog.dismiss()
            }else{
                context?.Toast("Debe ingresar un número de lote")
            }
        }
        return alertDialog
    }

    private fun endLoad(entity: ClsPickingDetail): AlertDialog {
        val bindingAlert = DialogEndloadBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvNameProduct.text = entity.material
        bindingAlert.btnEndLoad.setOnClickListener {
            viewModel.endLoad(entity)
            alertDialog.dismiss()
        }
        return alertDialog
    }

    private fun addStevedor(entity: ClsPickingDetail): AlertDialog {
        val bindingAlert = DialogAddStevedorBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvDialogMaterial.text = entity.material
        bindingAlert.btnAddStevedor.setOnClickListener {
            if (bindingAlert.edtDialogName.text.isNotEmpty()) {
                if (bindingAlert.edtDialogDni.text.isNotEmpty() && bindingAlert.edtDialogDni.length() < 8) {
                    context?.Toast(Constants.ERROR_DNI)
                } else {
                    viewModel.addStevedor(
                        bindingAlert.edtDialogName.text.toString(),
                        bindingAlert.edtDialogDni.text.toString(),
                        entity
                    )
                    alertDialog.dismiss()
                }
            } else {
                context?.Toast(Constants.ERROR_FIELDS)
            }
        }
        return alertDialog
    }

}
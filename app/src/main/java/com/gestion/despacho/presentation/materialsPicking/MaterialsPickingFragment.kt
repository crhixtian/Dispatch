package com.gestion.despacho.presentation.materialsPicking

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gestion.despacho.MainActivity
import com.gestion.despacho.R
import com.gestion.despacho.databinding.DialogAddStevedorBinding
import com.gestion.despacho.databinding.DialogEndloadBinding
import com.gestion.despacho.databinding.DialogLogoutBinding
import com.gestion.despacho.databinding.DialogObservationBinding
import com.gestion.despacho.databinding.DialogStartloadBinding
import com.gestion.despacho.databinding.FragmentMaterialsPickingBinding
import com.gestion.despacho.databinding.ItemMaterialBinding
import com.gestion.despacho.model.ClsPickingDetail
import com.gestion.despacho.presentation.validatedPicking.ValidatedPickingActivity
import com.gestion.despacho.utils.BaseAdapter
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.utils.DialogManager
import com.gestion.despacho.utils.SessionManager
import com.gestion.despacho.utils.Toast.Toast

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

                    override fun bind(entity: ClsPickingDetail) {
                        setMaterialDetails(entity)
                        handleSessionStatus(entity)
                        handleRoleBasedUI()
                        return setButtonListeners(entity)
                    }

                    private fun setMaterialDetails(entity: ClsPickingDetail) = with(binding) {
                        tvMaterial.text = entity.material
                        tvQuantity.text = entity.quantity.toString()
                        tvTon.text = entity.ton.toString()
                        tvWeight.text = entity.weight.toString()
                        tvTypeLoad.text = entity.type_load
                        tvStart.text = entity.startDate
                        tvEnd.text = entity.endDate
                        tvObservation.text = entity.observation
                    }

                    private fun handleSessionStatus(entity: ClsPickingDetail) = with(binding) {
                        if (SessionManager().getStatus() == 1) {
                            btnAddStvedor.visibility = View.GONE
                            btnObservation.visibility = View.GONE
                        } else {
                            root.setOnClickListener {
                                handleRootClick(entity)
                            }
                        }
                    }

                    private fun handleRootClick(entity: ClsPickingDetail) {
                        val context = binding.root.context
                        if (SessionManager().getRolId() == 6) {
                            when {
                                entity.startDate.isNullOrEmpty() -> starLoad(entity).show()
                                !entity.endDate.isNullOrEmpty() -> context.Toast(context.getString(R.string.loading_finished))
                                else -> checkStevedores(entity)
                            }
                        }
                    }

                    private fun handleRoleBasedUI() = with(binding) {
                        when (SessionManager().getRolId()) {
                            4, 7 -> {
                                btnAddStvedor.visibility = View.GONE
                                btnObservation.visibility = View.GONE
                            }
                        }
                    }

                    private fun setButtonListeners(entity: ClsPickingDetail) = with(binding) {
                        btnAddStvedor.setOnClickListener { addStevedor(entity).show() }
                        btnObservation.setOnClickListener {
                            handleObservationClick(entity)
                        }
                    }

                    private fun handleObservationClick(entity: ClsPickingDetail) {
                        val context = binding.root.context
                        if (entity.startDate.isNullOrEmpty()) {
                            context.Toast(context.getString(R.string.you_must_first_start_loading_the_material))
                        } else {
                            logOut(Constants.OBSERVE_MATERIAL, entity).show()
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

        binding.btnPdf.setOnClickListener {
            sendMail()
        }

        if (arguments != null) {
            id = requireArguments().getString(Constants.PICKING_FRAGMENT).toString()
            binding.tvPicking.text = id

            status()
            observers()
            events()
        }
    }

    private fun setupView() {
        when (SessionManager().getRolId()) {
            6, 7 -> {
                binding.linearLayoutButton.visibility = View.GONE
                binding.btnValidate.visibility = View.GONE
                binding.btnObserve.visibility = View.GONE
            }
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

    private fun observers() {
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
        }
        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response) sendMail()
        }
        viewModel.success.observe(viewLifecycleOwner) { picking ->
            getSuccess(picking.nbrpicking)
        }
        viewModel.observe.observe(viewLifecycleOwner) {
            if (it) observePicking(id = id).show()
        }
        return viewModel.getObserve.observe(viewLifecycleOwner) {
            if (it) getObserve()
        }
    }

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

    private fun events() {
        binding.btnValidate.setOnClickListener {
            logOut(origin = Constants.VALIDATE, null).show()
        }
        binding.btnObserve.setOnClickListener {
            logOut(origin = Constants.OBSERVE, null).show()
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
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                logOut(Constants.HOME, null).show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun logOut(origin: String, material: ClsPickingDetail?): AlertDialog {
        val bindingAlert = DialogLogoutBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context)
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()

        when (origin) {

            Constants.LOG_OUT -> {

                bindingAlert.tvTitleDialog.text =
                    getString(R.string.are_you_sure_you_want_to_log_out)

                bindingAlert.btnSiLogOut.setOnClickListener {
                    SessionManager().saveStatuSession(status = false)
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                    alertDialog.dismiss()
                }
            }

            Constants.VALIDATE -> {
                bindingAlert.tvTitleDialog.text =
                    getString(R.string.are_you_sure_you_want_to_validate_the_picking)

                bindingAlert.btnSiLogOut.setOnClickListener {
                    viewModel.checkPicking(id = id, Constants.VALIDATE)
                    alertDialog.dismiss()
                }
            }

            Constants.OBSERVE -> {

                bindingAlert.tvTitleDialog.text =
                    getString(R.string.are_you_sure_you_want_to_look_at_the_picking)

                bindingAlert.btnSiLogOut.setOnClickListener {
                    viewModel.checkPicking(id = id, Constants.OBSERVE)
                    alertDialog.dismiss()
                }

            }

            Constants.OBSERVE_MATERIAL -> {
                bindingAlert.tvTitleDialog.text =
                    getString(R.string.are_you_sure_you_want_to_look_at_the_material)

                bindingAlert.btnSiLogOut.setOnClickListener {
                    alertDialog.dismiss()
                    observeMaterial(material).show()
                }
            }

            Constants.HOME -> {
                bindingAlert.tvTitleDialog.text = getString(R.string.do_you_want_to_exit_picking)

                bindingAlert.btnSiLogOut.setOnClickListener {
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
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

    private fun observePicking(id: String): AlertDialog {
        val bindingAlert = DialogObservationBinding.inflate(LayoutInflater.from(requireContext()))
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingAlert.root)
        val alertDialog = builder.create()
        bindingAlert.tvNroPicking.text = id
        bindingAlert.btnStartLoad.setOnClickListener {
            if (bindingAlert.edtNbrLot.text.toString() == getString(R.string.empty)) {
                context?.Toast(getString(R.string.you_must_enter_reason))
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
        bindingAlert.tvTitle.text = getString(R.string.material)
        bindingAlert.tvNroPicking.text = material?.material
        bindingAlert.btnStartLoad.setOnClickListener {
            if (bindingAlert.edtNbrLot.text.toString() == "") {
                context?.Toast(getString(R.string.you_must_enter_reason))
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
            if (bindingAlert.edtNbrLot.text.isNotEmpty()) {
                viewModel.startLoad(entity, bindingAlert.edtNbrLot.text.toString())
                alertDialog.dismiss()
            } else {
                context?.Toast(getString(R.string.you_must_enter_lot_number))
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
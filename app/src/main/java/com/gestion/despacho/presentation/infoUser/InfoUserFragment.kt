package com.gestion.gestionmantenimientosoftware.Presentation.InfoUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.gestion.gestionmantenimientosoftware.Model.User
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.databinding.FragmentInfoUserBinding

class InfoUserFragment : Fragment(R.layout.fragment_info_user) {

    private lateinit var binding: FragmentInfoUserBinding
    private lateinit var globalUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoUserBinding.bind(view)

        arguments?.let { args ->
            globalUser = args.getSerializable(Constants.USER_LOGIN) as? User ?: return@let

            with(binding) {
                tvFullName.text = globalUser.FullName
                TvRolName.text = globalUser.RolName
                tvUserName.text = globalUser.User
            }
        }
    }
}

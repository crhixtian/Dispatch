package com.gestion.despacho.presentation.infoUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.gestion.despacho.model.User
import com.gestion.despacho.R
import com.gestion.despacho.utils.Constants
import com.gestion.despacho.databinding.FragmentInfoUserBinding

@Suppress("DEPRECATION")
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

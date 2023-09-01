package com.gestion.gestionmantenimientosoftware.Presentation.InfoUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.gestion.gestionmantenimientosoftware.Model.User
import com.gestion.gestionmantenimientosoftware.R
import com.gestion.gestionmantenimientosoftware.Utils.Constants
import com.gestion.gestionmantenimientosoftware.databinding.FragmentInfoUserBinding

class InfoUserFragment : Fragment(R.layout.fragment_info_user) {

    lateinit var binding: FragmentInfoUserBinding
    lateinit var globalUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoUserBinding.bind(view)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments != null){
            globalUser = arguments!!.getSerializable(Constants.USER_LOGIN) as User

            binding.tvFullName.text = globalUser.FullName
            binding.TvRolName.text = globalUser.RolName
            binding.tvUserName.text = globalUser.User
            binding.tvVersion.text = Constants.APP_VERSION
        }
    }
}
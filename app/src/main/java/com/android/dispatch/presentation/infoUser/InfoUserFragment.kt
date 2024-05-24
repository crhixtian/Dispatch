package com.android.dispatch.presentation.infoUser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.android.dispatch.R
import com.android.dispatch.databinding.FragmentInfoUserBinding
import com.android.dispatch.model.User
import com.android.dispatch.utils.Constants

class InfoUserFragment : Fragment(R.layout.fragment_info_user) {

    private lateinit var binding: FragmentInfoUserBinding
    private lateinit var globalUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentInfoUserBinding.bind(view)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments != null){
            globalUser = requireArguments().getSerializable(Constants.USER_LOGIN) as User

            binding.tvFullName.text = globalUser.FullName
            binding.TvRolName.text = globalUser.RolName
            binding.tvUserName.text = globalUser.User
        }
    }
}
package com.eactative.entry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.eactative.entry.databinding.FragmentFirstBinding
import com.eactative.ua.entity.ModuleInfo
import com.eactative.ua.entity.Source
import com.eactative.ua.rn.RNActivity
import com.eactative.ua.rn.RNManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                RNManager.boot(requireContext(), requireActivity().application, ModuleInfo(
                    "aa",
                    "1234",
                    1,
                    "Android",
                    "RN",
                    arrayOf(Source("", true)),
                    arrayOf(Source("", true))
                )
                )
            } catch (e: Exception) {
                Log.e("aaa", e.toString())
            }

        }

        binding.buttonFirst.setOnClickListener {
            val intent = Intent(this.context, RNActivity::class.java)
            startActivity(intent)
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
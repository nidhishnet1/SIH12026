package com.example.demosih11.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardNewLot.setOnClickListener {
            findNavController().navigate(R.id.submitEwasteFragment)
        }

        binding.cardPriceBoard.setOnClickListener {
            findNavController().navigate(R.id.priceBoardFragment)
        }

        binding.cardSafetyGuide.setOnClickListener {
            findNavController().navigate(R.id.safetyGuideFragment)
        }

        binding.cardRecyclers.setOnClickListener {
            findNavController().navigate(R.id.recyclerDiscoveryFragment)
        }

        binding.cardRecentTransaction.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("IS_RECYCLER", false) }
            findNavController().navigate(R.id.chatFragment, bundle)
        }

        // Add a logout link or action button for the Kabadi Wala to return back to the login selector
        binding.welcomeText.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.famplan.ui.voting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.famplan.databinding.FragmentVotingBinding

class VotingFragment : Fragment() {

    private var _binding: FragmentVotingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val votingViewModel =
            ViewModelProvider(this).get(VotingViewModel::class.java)

        _binding = FragmentVotingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textVoting
        votingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
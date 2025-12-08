package com.machado001.lilol.rotation.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.machado001.lilol.R
import com.machado001.lilol.databinding.BottomSheetTipsBinding

class TipsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetTipsBinding? = null
    private val binding get() = _binding!!

    private var tips: List<String> = emptyList()
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tips = it.getStringArrayList(ARG_TIPS)?.toList() ?: emptyList()
            title = it.getString(ARG_TITLE).orEmpty()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.isDraggable = true
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BottomSheetTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtSheetTitle.text = title
        addTipsToLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addTipsToLayout() {
        val context = requireContext()
        val container = binding.linearLayoutTips

        tips.forEachIndexed { index, tip ->
            val textView = TextView(
                context,
                null,
                0,
                R.style.Theme_Lilol_TextViewBase_ChampionDetail,
            ).apply {
                text = tip
                textSize = 16.0f
            }
            container.addView(textView)

            if (index != tips.lastIndex) {
                val space = Space(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        16
                    )
                }
                container.addView(space)
            }
        }
    }

    companion object {
        const val TAG = "TipsBottomSheetFragment"
        private const val ARG_TIPS = "tips"
        private const val ARG_TITLE = "title"

        fun newInstance(tips: ArrayList<String>, title: String): TipsBottomSheetFragment {
            val fragment = TipsBottomSheetFragment()
            fragment.arguments = Bundle().apply {
                putStringArrayList(ARG_TIPS, tips)
                putString(ARG_TITLE, title)
            }
            return fragment
        }
    }
}

package com.machado001.lilol.rotation.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.machado001.lilol.common.view.SpellListItem
import com.machado001.lilol.databinding.BottomSheetSpellDetailBinding
import com.squareup.picasso.Picasso

class SpellDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSpellDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var spell: SpellListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            spell = args.getParcelable(ARG_SPELL)!!
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
        _binding = BottomSheetSpellDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindSpell()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindSpell() = with(binding) {
        spellKeyBadge.text = spell.keyboardKey.toString()
        spellName.text = spell.name
        spellDescription.text = spell.description
        spellCooldown.text = getString(com.machado001.lilol.R.string.spell_cooldown_label, spell.cooldownBurn)
        spellCost.text = getString(com.machado001.lilol.R.string.spell_cost_label, spell.costBurn)
        spellRange.text = spell.rangeBurn?.let {
            getString(com.machado001.lilol.R.string.spell_range_label, it)
        } ?: ""
        spellRange.visibility = if (spell.rangeBurn.isNullOrBlank()) View.GONE else View.VISIBLE

        Picasso.get()
            .load(spell.iconUrl)
            .fit()
            .centerCrop()
            .into(spellImage)
    }

    companion object {
        private const val ARG_SPELL = "arg_spell"
        const val TAG = "SpellDetailBottomSheetFragment"

        fun newInstance(spell: SpellListItem): SpellDetailBottomSheetFragment {
            return SpellDetailBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SPELL, spell)
                }
            }
        }
    }
}

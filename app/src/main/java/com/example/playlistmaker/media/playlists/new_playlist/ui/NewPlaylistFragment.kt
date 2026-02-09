package com.example.playlistmaker.media.playlists.new_playlist.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.util.LocalUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private var playlistName: String = ""
    private lateinit var playlistDescription: String
    private var isClickAllowed = true
    private val viewModel by viewModel<NewPlaylistViewModel>()
    private var playlistImgPath: Uri? = null
    private lateinit var exitDialog: MaterialAlertDialogBuilder

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.setImageUri(uri)
            if (uri != null) {
                Glide.with(binding.root)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(LocalUtils().dpToPx(8f, binding.root)))
                    .into(binding.playlistCoverStroke)
                binding.playlistCover.isVisible = false
            } else {
                binding.playlistCover.isVisible = true
                binding.playlistCover.setImageResource(R.drawable.album_cover)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeImageUrl().observe(viewLifecycleOwner) {
            playlistImgPath = it
        }
        viewModel.observeSaveResult().observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigateUp()
                val message =
                    getString(R.string.playlist) + " ${binding.etPlaylistName.text.toString()} " + getString(
                        R.string.created
                    )
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_save_image),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        exitDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(getString(R.string.new_playlist_exit_dialog_name))
            .setMessage(getString(R.string.new_playlist_exit_dialog_description))
            .setNeutralButton(getString(R.string.cansel)) { _, _ -> }
            .setPositiveButton(getString(R.string.complete)) { _, _ ->
                findNavController().navigateUp()
            }

        binding.mtbArrowback.setNavigationOnClickListener {
            exitFromFragment()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitFromFragment()
                }
            }
        )

        binding.playlistCoverStroke.setOnClickListener {
            if (clickDebounce()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        binding.etPlaylistName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btCreate.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

        })

        binding.btCreate.setOnClickListener {
            playlistName = binding.etPlaylistName.text.toString()
            playlistDescription = binding.etPlaylistDescription.text.toString()
            viewModel.addToDb(playlistName, playlistDescription)

        }
    }

    private fun exitFromFragment() {
        if (
            !binding.etPlaylistName.text.isNullOrEmpty() ||
            !binding.etPlaylistDescription.text.isNullOrEmpty() ||
            viewModel.isImageCoverSet()
        ) {
            exitDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 300L
    }
}
package com.andariadar.pixabayimages.ui.fragments

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.andariadar.pixabayimages.R
import com.andariadar.pixabayimages.databinding.FragmentDetailBinding
import com.andariadar.pixabayimages.ui.PixabayViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

private const val PERMISSION_WRITE_EXTERNAL_STORAGE = 5

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class DetailFragment: Fragment(R.layout.fragment_detail) {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = args.image
        val uri = Uri.parse(image.profileUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        binding.btnUserProfile.setOnClickListener {
            context?.startActivity(intent)
        }

        binding.apply {
            val imageUrl = image.largeImageURL

            Glide.with(this@DetailFragment)
                .load(imageUrl)
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }
                })
                .into(imageView)

            btnDownload.setOnClickListener {
                downloadImage(imageUrl)
            }
        }
    }

    private fun downloadImageToDownloadFolder(imageUrl: String) {
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(imageUrl)
        val request = DownloadManager.Request(
            downloadUri
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
        )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverRoaming(false)
            .setTitle("Pixabay")
            .setDescription("Pixabay image")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "PixabayImage.jpg"
            )

        val downloadID = downloadManager.enqueue(request)


            var finishDownload = false
            while (!finishDownload) {
                val cursor: Cursor =
                        downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    val status: Int =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            binding.progressBar.isVisible = false
                            finishDownload = true
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            binding.progressBar.isVisible = false
                        }
                        DownloadManager.STATUS_PENDING -> {
                            binding.progressBar.isVisible = true
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            binding.progressBar.isVisible = true
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                    requireContext(),
                                    "Image was downloaded successfully",
                                    Toast.LENGTH_LONG
                            ).show()
                            finishDownload = true
                        }
                    }
                }
            }
        }

    //Request permission for write storage
    private fun requestPermissionForWrite() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_WRITE_EXTERNAL_STORAGE
        )
    }

    //Check if you already have write storage permission
    private fun checkPermissionForWrite(): Boolean {
        val result: Int =
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun downloadImage(imageUrl: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (checkPermissionForWrite()) {
                downloadImageToDownloadFolder(imageUrl)
            } else {
                requestPermissionForWrite()
            }
        } else {
            downloadImageToDownloadFolder(imageUrl)
        }
    }
}
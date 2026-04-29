package com.example.myfirstapp.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.viewmodel.RegistroUiState
import com.example.myfirstapp.viewmodel.RegistroViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val shimmer = findViewById<ShimmerFrameLayout>(R.id.shimmerLayout)

        // Layout genérico para cada estado
        val layoutState = findViewById<View>(R.id.layoutEmpty)
        val imageState = findViewById<ImageView>(R.id.imageEmpty)
        val titleState = findViewById<TextView>(R.id.textEmptyTitle)
        val descState = findViewById<TextView>(R.id.textEmptyDescription)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RegistroAdapter()
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collectLatest { state ->

                    when (state) {

                        // LOADING
                        is RegistroUiState.Loading -> {
                            shimmer.visibility = View.VISIBLE
                            shimmer.startShimmer()

                            recyclerView.visibility = View.GONE
                            layoutState.visibility = View.GONE
                        }

                        // SUCESSO
                        is RegistroUiState.Success -> {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE

                            if (state.data.isEmpty()) {
                                recyclerView.visibility = View.GONE
                                layoutState.visibility = View.VISIBLE

                                titleState.text = getString(R.string.empty_title)
                                descState.text = getString(R.string.empty_description)
                                imageState.setImageResource(android.R.drawable.ic_menu_report_image)

                            } else {
                                recyclerView.visibility = View.VISIBLE
                                layoutState.visibility = View.GONE
                                adapter.submitList(state.data)
                            }
                        }

                        // ERRO
                        is RegistroUiState.Error -> {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE

                            recyclerView.visibility = View.GONE
                            layoutState.visibility = View.VISIBLE

                            when {
                                state.message.contains("internet", true) -> {
                                    titleState.text = getString(R.string.error_no_connection_title)
                                    descState.text = getString(R.string.error_no_connection_desc)
                                    imageState.setImageResource(android.R.drawable.ic_dialog_alert)
                                }

                                state.message.contains("servidor", true) -> {
                                    titleState.text = getString(R.string.error_server_title)
                                    descState.text = getString(R.string.error_server_desc)
                                    imageState.setImageResource(android.R.drawable.ic_delete)
                                }

                                else -> {
                                    titleState.text = getString(R.string.error_generic_title)
                                    descState.text = getString(R.string.error_generic_desc)
                                    imageState.setImageResource(android.R.drawable.ic_dialog_info)
                                }
                            }
                        }
                    }
                }
            }
        }
        viewModel.carregarRegistros()
    }
}
package com.example.myfirstapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.model.Registro
import com.example.myfirstapp.viewmodel.RegistroUiState
import com.example.myfirstapp.viewmodel.RegistroViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    // RECEBE REGISTRP
    private val registroLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val registro =
                    result.data?.getSerializableExtra(
                        "novo_registro"
                    ) as? Registro

                registro?.let {

                    Log.d(
                        "REGISTRO_DEBUG",
                        "Registro recebido: $it"
                    )

                    viewModel.salvarRegistro(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // FAB
        binding.fabNovoRegistro.setOnClickListener {

            val intent =
                Intent(this, RegistroActivity::class.java)

            registroLauncher.launch(intent)
        }

        // REFRESH
        binding.swipeRefresh.setOnRefreshListener {

            viewModel.carregarRegistros()
        }

        // ESTADOS
        val layoutState = binding.layoutEmpty
        val imageState = binding.imageEmpty
        val titleState = binding.textEmptyTitle
        val descState = binding.textEmptyDescription

        // RECYCLERVIEW
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this)

        val adapter = RegistroAdapter()

        binding.recyclerView.adapter = adapter

        // OBSERVA STATEFLOW
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collectLatest { state ->

                    when (state) {

                        // LOADING
                        is RegistroUiState.Loading -> {

                            binding.shimmerLayout.visibility =
                                View.VISIBLE

                            binding.shimmerLayout.startShimmer()

                            binding.recyclerView.visibility =
                                View.GONE

                            layoutState.visibility =
                                View.GONE

                            binding.fabNovoRegistro.visibility =
                                View.GONE
                        }

                        // SUCESSO
                        is RegistroUiState.Success -> {

                            binding.swipeRefresh.isRefreshing = false

                            binding.shimmerLayout.stopShimmer()

                            binding.shimmerLayout.visibility =
                                View.GONE

                            if (state.data.isEmpty()) {

                                binding.recyclerView.visibility =
                                    View.GONE

                                layoutState.visibility =
                                    View.VISIBLE

                                titleState.text =
                                    getString(R.string.empty_title)

                                descState.text =
                                    getString(
                                        R.string.empty_description
                                    )

                                imageState.setImageResource(
                                    android.R.drawable.ic_menu_report_image
                                )

                            } else {

                                binding.recyclerView.visibility =
                                    View.VISIBLE

                                layoutState.visibility =
                                    View.GONE

                                Log.d(
                                    "REGISTRO_DEBUG",
                                    "submitList chamada com ${state.data.size} itens"
                                )

                                adapter.submitList(state.data)
                            }

                            binding.fabNovoRegistro.visibility =
                                View.VISIBLE
                        }

                        // ERRO
                        is RegistroUiState.Error -> {

                            binding.swipeRefresh.isRefreshing = false

                            binding.shimmerLayout.stopShimmer()

                            binding.shimmerLayout.visibility =
                                View.GONE

                            binding.recyclerView.visibility =
                                View.GONE

                            layoutState.visibility =
                                View.VISIBLE

                            binding.fabNovoRegistro.visibility =
                                View.GONE

                            when {

                                state.message.contains(
                                    "internet",
                                    true
                                ) -> {

                                    titleState.text =
                                        getString(
                                            R.string.error_no_connection_title
                                        )

                                    descState.text =
                                        getString(
                                            R.string.error_no_connection_desc
                                        )

                                    imageState.setImageResource(
                                        android.R.drawable.ic_dialog_alert
                                    )
                                }

                                state.message.contains(
                                    "servidor",
                                    true
                                ) -> {

                                    titleState.text =
                                        getString(
                                            R.string.error_server_title
                                        )

                                    descState.text =
                                        getString(
                                            R.string.error_server_desc
                                        )

                                    imageState.setImageResource(
                                        android.R.drawable.ic_notification_clear_all
                                    )
                                }

                                else -> {

                                    titleState.text =
                                        getString(
                                            R.string.error_generic_title
                                        )

                                    descState.text =
                                        getString(
                                            R.string.error_generic_desc
                                        )

                                    imageState.setImageResource(
                                        android.R.drawable.ic_dialog_info
                                    )
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
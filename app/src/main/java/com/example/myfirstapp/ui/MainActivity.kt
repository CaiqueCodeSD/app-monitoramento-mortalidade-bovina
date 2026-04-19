package com.example.myfirstapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.viewmodel.RegistroViewModel
import com.example.myfirstapp.viewmodel.RegistroUiState
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val shimmer = findViewById<ShimmerFrameLayout>(R.id.shimmerLayout)
        val textEmpty = findViewById<TextView>(R.id.textEmpty)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RegistroAdapter()
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.uiState.collectLatest { state ->

                    when (state) {

                        is RegistroUiState.Loading -> {
                            shimmer.visibility = View.VISIBLE
                            shimmer.startShimmer()

                            recyclerView.visibility = View.GONE
                            textEmpty.visibility = View.GONE
                        }

                        is RegistroUiState.Success -> {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE

                            adapter.submitList(state.data)

                            if (state.data.isEmpty()) {
                                recyclerView.visibility = View.GONE
                                textEmpty.visibility = View.VISIBLE
                            } else {
                                recyclerView.visibility = View.VISIBLE
                                textEmpty.visibility = View.GONE
                            }
                        }

                        is RegistroUiState.Error -> {
                            shimmer.stopShimmer()
                            shimmer.visibility = View.GONE

                            recyclerView.visibility = View.GONE
                            textEmpty.visibility = View.GONE

                            Toast.makeText(
                                this@MainActivity,
                                getString(state.messageResId),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        viewModel.carregarRegistros()
    }
}
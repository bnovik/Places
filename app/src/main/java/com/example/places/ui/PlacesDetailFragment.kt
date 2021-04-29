package com.example.places.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.places.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacesDetailFragment : Fragment() {

    lateinit var viewModel: MapsViewModel

    private val placesAdapter = PlacesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_places_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as MapsActivity).viewModel
        initAdapter()
        initSubscription()
    }

    private fun initSubscription() {
        viewModel.placesViewData.observe(viewLifecycleOwner) {
            placesAdapter.updateList(it)
        }
    }

    private fun initAdapter() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = placesAdapter
    }
}
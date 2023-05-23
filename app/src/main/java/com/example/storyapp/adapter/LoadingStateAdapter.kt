package com.example.storyapp.adapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityLoadingStateAdapterBinding

class LoadingStateAdapter(private val retry:() -> Unit) : LoadStateAdapter<LoadingStateAdapter.ViewHolder>(){

    class ViewHolder(val binding: ActivityLoadingStateAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: LoadingStateAdapter.ViewHolder, loadState: LoadState) {
        if(loadState is LoadState.Loading){
            holder.binding.loadingBar.visibility = View.INVISIBLE
        }else{
            holder.binding.loadingBar.visibility = View.INVISIBLE
        }

        if(loadState is LoadState.Error){
            holder.binding.errorMsg.text = loadState.error.localizedMessage
            holder.binding.errorMsg.visibility = View.INVISIBLE
            holder.binding.retry.visibility = View.INVISIBLE
        }

        holder.binding.retry.setOnClickListener {
            retry.invoke()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateAdapter.ViewHolder {
        return ViewHolder(ActivityLoadingStateAdapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }
}
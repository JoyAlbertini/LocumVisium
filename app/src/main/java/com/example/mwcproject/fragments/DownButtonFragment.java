package com.example.mwcproject.fragments;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleObserver;

import com.example.mwcproject.R;
import com.example.mwcproject.databinding.CameraFragmentBinding;
import com.example.mwcproject.databinding.DownButtonFragmentBinding;

public class DownButtonFragment extends Fragment {

    private FragmentManager fm;
    DownButtonFragmentBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        binding = DownButtonFragmentBinding.inflate(inflater, container, false);
        System.out.println("ALPACA before");
        binding.cameraBtn.setOnClickListener(view -> {
                    System.out.println("ALPACA");
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_map, MapsFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null) // if you want to add it to the back stack
                            .commit();
                }
        );
        return binding.getRoot().findViewById(R.id.down_bts);
    }

    public DownButtonFragment() {
        super(R.layout.down_button_fragment);

    }

}

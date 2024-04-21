package com.example.spotify_wrapped.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spotify_wrapped.API;
import com.example.spotify_wrapped.AuthActivity;
import com.example.spotify_wrapped.LinkActivity;
import com.example.spotify_wrapped.R;
import com.example.spotify_wrapped.databinding.FragmentProfileBinding;
import com.example.spotify_wrapped.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private API api;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        sharedPreferences = requireContext()
                .getSharedPreferences(
                        requireContext().getString(R.string.shared_pref_key), MODE_PRIVATE);
        if (!API.isInstance()) {
            API.setAccessToken(sharedPreferences.getString("access_token", null));
        }

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button unlink = binding.unlink;
        Button logout = binding.logout;
        Button changeLoginDetails = binding.changeLoginDetails;
        Button delete = binding.delete;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        unlink.setOnClickListener(v -> {
            if (homeViewModel.logout()) {
                API.logout();
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                String uid = firebaseAuth.getCurrentUser().getUid();
                usersRef.child(uid).child("user_data").child("refresh_token").removeValue();
                usersRef.child(uid).child("user_data").child("access_token").removeValue();
                usersRef.child(uid).child("user_data").child("expires_at").removeValue();
                Toast.makeText(getContext(), "Unlinked Successfully", Toast.LENGTH_SHORT)
                        .show();
                startActivity(new Intent(requireActivity(), LinkActivity.class));
                getActivity().finish();
            }
        });

        logout.setOnClickListener(v -> {
            API.logout();
            firebaseAuth.signOut();
            homeViewModel.logout();
            startActivity(new Intent(requireActivity(), AuthActivity.class));
            getActivity().finish();
        });

        Bundle args = new Bundle();

        changeLoginDetails.setOnClickListener(v -> {
            args.putString("op", "update");
            Navigation.findNavController(v).navigate(R.id.profileToVerifyLogin, args);
        });

        delete.setOnClickListener(v -> {
            args.putString("op", "delete");
            API.logout();
            homeViewModel.logout();
            Navigation.findNavController(v).navigate(R.id.profileToVerifyLogin, args);
        });

        return root;
    }
}

package com.example.spotify_wrapped.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spotify_wrapped.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeViewModel extends AndroidViewModel {

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private final MutableLiveData<String> mText;

    public HomeViewModel(Application application) {
        super(application);
        mContext = getApplication().getApplicationContext();
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        sharedPreferences = mContext.getSharedPreferences(
                mContext.getString(R.string.shared_pref_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean logout() {
        if (sharedPreferences.contains("access_token")
                || sharedPreferences.contains("refresh_token")) {
            editor.clear();
            editor.apply();
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            String uid = firebaseAuth.getCurrentUser().getUid();
            usersRef.child(uid).child("refresh_token").removeValue();
            usersRef.child(uid).child("access_token").removeValue();
            usersRef.child(uid).child("expires_at").removeValue();
            return true;
        }
        return false;
    }

    public LiveData<String> getText() {
        return mText;
    }
}

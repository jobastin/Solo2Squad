package com.example.solo2squad;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class confirmDelete extends DialogFragment {

    private String eventKey;

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_delete_button, null);

        Button confirmDeleteButton = view.findViewById(R.id.confirmDeleteButton);
        confirmDeleteButton.setOnClickListener(v -> {
            // Perform the delete action here
            if (eventKey != null) {
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Hosted_events").child(eventKey);
                eventRef.removeValue(); // Delete the corresponding data
            }
            dismiss(); // Dismiss the dialog
        });

        builder.setView(view);
        return builder.create();
    }
}

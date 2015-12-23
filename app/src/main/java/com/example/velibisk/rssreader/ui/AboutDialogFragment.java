package com.example.velibisk.rssreader.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.velibisk.rssreader.R;

import javax.inject.Inject;

/**
 * Created by attacco on 23.12.2015.
 */
public class AboutDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    @Inject
    public AboutDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_Alert)
                .setMessage(R.string.dialog_about_text)
                .setPositiveButton(R.string.dialog_positive_button_text, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }
}
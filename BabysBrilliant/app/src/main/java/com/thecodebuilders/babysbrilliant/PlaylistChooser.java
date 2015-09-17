package com.thecodebuilders.babysbrilliant;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by aaronnicholson on 9/10/15.
 */
public class PlaylistChooser extends DialogFragment {
    ArrayList<Playlist> playlists;
    ArrayList<String> playlistNames;
    EditText nameText;
    Dialog newPlaylistDialog;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PlaylistChooserListener {
        public void onPlaylistSelect(int item);
        public void onPlaylistAdd(String name);
    }

    // Use this instance of the interface to deliver action events
    PlaylistChooserListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PlaylistChooserListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PlaylistChooserListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] playlistNamesJavaArray = getPlaylistNames();

        if(playlistNamesJavaArray.length == 0) {
            return createNewPlaylistDialog();

        } else {
            return createChooserDialog(playlistNamesJavaArray);

        }

    }

    private Dialog createChooserDialog(String[] playlistNamesJavaArray) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a Playlist")
                .setItems(playlistNamesJavaArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        mListener.onPlaylistSelect(item);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton("New List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        newPlaylistDialog = createNewPlaylistDialog();
                        newPlaylistDialog.show();
                    }
                })
        ;

        return builder.create();
    }

    private Dialog createNewPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View builderView = inflater.inflate(R.layout.create_new_playlist, null);

        nameText = (EditText) builderView.findViewById(R.id.new_playlist_name);

        builder.setView(builderView)

                .setTitle("Create a new playlist")

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onPlaylistAdd(nameText.getText().toString());
                    }
                })

        ;

        return builder.create();

    }

    @NonNull
    private String[] getPlaylistNames() {
        playlists = MainActivity.playlists;
        playlistNames = new ArrayList<>();

        String[] playlistNamesJavaArray = new String[ playlists.size() ];

        for (int i = 0; i < playlists.size(); i++) {
            String name = playlists.get(i).getName();
            playlistNames.add(name);

        }

        playlistNames.toArray(playlistNamesJavaArray);
        return playlistNamesJavaArray;
    }


}

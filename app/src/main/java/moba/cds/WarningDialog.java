package moba.cds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gutte on 4/8/2017.
 */

public class WarningDialog extends AlertDialog.Builder {

    public WarningDialog(Context context) {
        super(context);
        this.setMessage("This Alert Dialog");

        this.setPositiveButton("Connect", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"test",Toast.LENGTH_SHORT).show();
            }
        });

        this.setNegativeButton("Cancel", null);
        this.create();
    }

}

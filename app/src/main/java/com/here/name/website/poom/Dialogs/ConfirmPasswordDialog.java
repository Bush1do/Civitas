package com.here.name.website.poom.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.here.name.website.poom.R;

/**
 * Created by Charles on 12/16/2017.
 */

public class ConfirmPasswordDialog extends android.support.v4.app.DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";

    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener mOnConfirmPasswordListener;

    //Variables
    TextView mPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_confirm_password,container,false);
        mPassword=(TextView) view.findViewById(R.id.confirm_Password);
        Log.d(TAG, "onCreateView: Started.");

        TextView confirmDialog= (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Password entered and being confirmed");
                String password=mPassword.getText().toString();
                if (!password.equals("")){
                    mOnConfirmPasswordListener.onConfirmPassword(password);
                } else{
                    Toast.makeText(getActivity(), "You must enter a password", Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }
        });

        TextView cancelDialog= (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Closing the dialog");
                getDialog().dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnConfirmPasswordListener=(OnConfirmPasswordListener)getTargetFragment();
        }
        catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: "+ e.getMessage());
        }
    }
}

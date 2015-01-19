package com.retor.githubmodule.clientgithub.clientui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.retor.githubmodule.clientgithub.clientui.gitlib.SmallLib;
import com.retor.githubmodule.clientgithub.clientui.gitlib.TaskListener;



public class PasswordFragment extends Fragment implements TaskListener {

    //Views
    public ButtonRectangle signIn;
    EditText passwordView;
    EditText userView;
    ProgressBarCircularIndeterminate progress;
    ScrollView headForm;
    ScrollView loginForm;

    SmallLib lib;
    MenuItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(View view){
        loginForm = (ScrollView) view.findViewById(R.id.login_form);
        headForm = (ScrollView) view.findViewById(R.id.head_progress);
        signIn = (ButtonRectangle)view.findViewById(R.id.sign_in_button);
        signIn.setEnabled(true);
        userView = (EditText)view.findViewById(R.id.user);
        passwordView = (EditText)view.findViewById(R.id.password);
        progress = (ProgressBarCircularIndeterminate)view.findViewById(R.id.login_progress);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn.setEnabled(false);
                lib = SmallLib.getInstance(PasswordFragment.this);
                lib.setNewListener(PasswordFragment.this);
                if (userView.getText().toString().length()>=2 && passwordView.getText().toString().length()>=2){
                    lib.runAuth(userView.getText().toString(),passwordView.getText().toString());
                }else{
                    userView.setHint("");
                    passwordView.setHint("");
                    userView.setHint("Must be not null !!!");
                    passwordView.setHint("Must be filled");
                    signIn.setEnabled(true);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_password, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onTaskStarted(String msg) {
        loginForm.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskFinish(String msg) {
        if (lib.isAuth){
            ((MainActivity)getActivity()).replaceFragments(new ReposFragment());
            PasswordFragment.this.onDestroy();
            ((MainActivity)getActivity()).item.setEnabled(true);
        }
    }

    @Override
    public void onTaskError(String msg) {
        final String tmpMsg = msg;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = SmallLib.initStatusDialog(getActivity(),"Error",tmpMsg, 1);
                dialog.show();
                progress.setVisibility(View.INVISIBLE);
                loginForm.setVisibility(View.VISIBLE);
                signIn.setEnabled(true);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setRetainInstance(true);
    }
}

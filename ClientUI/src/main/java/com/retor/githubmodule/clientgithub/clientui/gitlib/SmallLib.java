package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.retor.githubmodule.clientgithub.clientui.PasswordFragment;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by retor on 16.01.2015.
 */
public final class SmallLib implements TaskListener{

    final public static List<String> scopes = Arrays.asList("user", "repo", "admin:public_key");
    final public static String appName = "ClientGithub";
    protected static volatile SmallLib instance = null;
    private PicturesLoader picturesLoader = PicturesLoader.instance();

    private String token;
    public boolean isAuth = false;
    private ArrayList<Repository> repositories;
    private Map<String,List<RepositoryCommit>> commits;

    private Context context;
    private TaskListener listener;
    private Fragment fragment;

    private TaskAuth authTask;
    private TaskGetRepos reposTask;
    private TaskGetCommits commitsTask;

    public static SmallLib getInstance(Fragment fragment) {
        SmallLib localInstance = instance;
        if (localInstance == null) {
            synchronized (SmallLib.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SmallLib(fragment);
                }
            }
        }
        return instance;
    }

    public static SmallLib getInstance(String token, Fragment fragment) {
        SmallLib localInstance = instance;
        if (localInstance == null) {
            synchronized (SmallLib.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SmallLib(token, fragment);
                }
            }
        }
        return instance;
    }

    protected SmallLib(Fragment fragment) {
        this.listener = (TaskListener)fragment;
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    protected SmallLib(String token,Fragment fragment) {
        this.token = token;
        this.listener = (TaskListener)fragment;
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    public void dispose(){
        instance = null;
        if(authTask!=null)
            authTask.cancel(true);
        if (reposTask!=null)
            reposTask.cancel(true);
        if (commitsTask!=null)
            commitsTask.cancel(true);
        token = null;
        isAuth = false;
        repositories = null;
        commits = null;
        context = null;
        listener = null;
        fragment = null;
        System.gc();
    }

    public void setNewListener(TaskListener listener){
        this.listener = listener;
        this.context = fragment.getActivity();
    }

    public void setToken(String token) {
        this.token = token;
        if (token!=null)
            PrefWork.savePref(context,token);
    }

    public ArrayList<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(ArrayList<Repository> repositories) {
        this.repositories = repositories;
        runCommitTask();
    }

    public ArrayList<RepositoryCommit> getCommits(String repo) {
        return new ArrayList<RepositoryCommit>(commits.get(repo));
    }

    public void setCommits(Map<String,List<RepositoryCommit>> commits) {
        this.commits = commits;
    }

    public boolean checkListCommits(String repoName){
        if (!commits.isEmpty() && commits.containsKey(repoName))
            return true;
        else
            return false;
    }

    public void runAuth(String userName, String userPass){
        if (isConnected(context)) {
            if (authTask==null) {
                authTask = new TaskAuth(userName, userPass, this);
            }else {
                authTask.cancel(true);
                authTask = new TaskAuth(userName, userPass, this);
            }
            authTask.execute();
        }else{
            AlertDialog d = SmallLib.initStatusDialog(context, "Error", "No internet connection", 0);
            d.setButton(com.gc.materialdesign.widgets.Dialog.BUTTON_POSITIVE, "Open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            d.show();
            ((PasswordFragment)fragment).signIn.setEnabled(true);
        }
    }

    public void runRepoTask(){
        if (isConnected(context)) {
            if (reposTask == null) {
                reposTask = new TaskGetRepos(token, this);
            } else {
                reposTask.cancel(true);
                reposTask = new TaskGetRepos(token, this);
            }
            reposTask.execute();
        }else{
            AlertDialog d = SmallLib.initStatusDialog(context, "Error", "No internet connection", 0);
            d.setButton(com.gc.materialdesign.widgets.Dialog.BUTTON_POSITIVE, "Open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            d.show();
        }
    }

    public void runCommitTask(){
        if (isConnected(context)) {
            if (commitsTask==null) {
                commitsTask = new TaskGetCommits(repositories,token,this);
            }else {
                commitsTask.cancel(true);
                commitsTask = new TaskGetCommits(repositories,token,this);
            }
            commitsTask.execute();
        }else{
            AlertDialog d = SmallLib.initStatusDialog(context, "Error", "No internet connection", 0);
            d.setButton(com.gc.materialdesign.widgets.Dialog.BUTTON_POSITIVE, "Open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            d.show();
        }
    }

    public void fillPicturesCache(ArrayList<Repository> repos){
        for (Repository r:repos){
            picturesLoader.fillCache(r.getOwner().getAvatarUrl());
        }
    }

    public Bitmap getPictureFromCache(String url){
        return picturesLoader.getBitmapFromMemCache(url);
    }

    @Override
    public void onTaskStarted(String msg) {
        listener.onTaskStarted(msg);
        Log.d("Listener", "start");
    }

    @Override
    public void onTaskFinish(String msg) {
        listener.onTaskFinish(msg);
        Log.d("Listener","finish");
    }

    @Override
    public void onTaskError(String msg) {
        listener.onTaskError(msg);
        Log.d("Listener","error");
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected())
            return true;
         else
            return false;
    }

    public static AlertDialog initStatusDialog(Context context, String title, String message, int isbutton) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIcon(android.R.drawable.ic_menu_info_details);
        dialog.setCancelable(false);
        switch (isbutton){
            case 1:
                dialog.setButton(Dialog.BUTTON_POSITIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        });
                dialog.setCancelable(true);
                break;
        }
        return dialog;
    }
}

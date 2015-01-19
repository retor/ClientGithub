package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by retor on 16.01.2015.
 */
public class TaskGetRepos extends AsyncTask<Void,Void,Void>{
    @NonNull
    private TaskListener listener;
    @NonNull
    private String token;
    private SmallLib lib;

    ArrayList<Repository> repos;

    public TaskGetRepos(@NonNull String tokenIn, SmallLib lib) {
        this.token = tokenIn;
        this.lib = lib;
        this.listener = lib;
    }

    private List<Repository> getRepos(String token) throws IOException {
        RepositoryService repoServ = new RepositoryService();
        repoServ.getClient().setOAuth2Token(token);
        return repoServ.getRepositories();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskStarted("RepoTaskStarted");
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            repos = new ArrayList<Repository>(getRepos(token));
        } catch (IOException e) {
            listener.onTaskError(e.getMessage());
            e.printStackTrace();
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        lib.setRepositories(repos);
        lib.fillPicturesCache(repos);
        Log.d("REPOS: ", "End");
    }
}

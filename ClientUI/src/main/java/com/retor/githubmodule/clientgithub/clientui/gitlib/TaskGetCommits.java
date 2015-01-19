package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by retor on 17.01.2015.
 */
public class TaskGetCommits extends AsyncTask<Void,Void,Void> {

    @NonNull
    String token;
    ArrayList<Repository> repos;
    SmallLib lib;
    Map<String,List<RepositoryCommit>> allcommits;

    public TaskGetCommits(ArrayList<Repository> repos, @NonNull String token, SmallLib lib) {
        this.repos = repos;
        this.token = token;
        this.lib = lib;
        allcommits = new HashMap<String, List<RepositoryCommit>>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        CommitService cs = new CommitService();
        cs.getClient().setOAuth2Token(token);
        for (Repository r : repos) {
            try {
                final String id = r.getOwner().getLogin() + "/" + r.getName();
                List<RepositoryCommit> tmp;
                tmp = cs.getCommits(new IRepositoryIdProvider() {
                    @Override
                    public String generateId() {
                        return id;
                    }
                });
                if (tmp != null || tmp.size() > 0)
                    allcommits.put(r.getName(), tmp);
            }catch(IOException e){
                e.printStackTrace();
                    continue;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        lib.setCommits(allcommits);
        lib.onTaskFinish("Finish");
        Log.d("COMMITS", String.valueOf(allcommits.size()));
        super.onPostExecute(aVoid);
    }
}

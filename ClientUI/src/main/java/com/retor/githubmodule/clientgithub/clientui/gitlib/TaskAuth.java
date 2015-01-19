package com.retor.githubmodule.clientgithub.clientui.gitlib;

import android.os.AsyncTask;
import org.eclipse.egit.github.core.Application;
import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.service.OAuthService;

import java.io.IOException;

/**
 * Created by retor on 16.01.2015.
 */
public class TaskAuth extends AsyncTask<Void,Void,Void> {

    private String token;
    private String userName;
    private String userPass;
    private Authorization auth;
    private SmallLib lib;

    public TaskAuth(String userName, String userPassword, SmallLib lib) {
        this.userName = userName;
        this.userPass = userPassword;
        this.lib = lib;
    }

    //Auth section
    private void tryAuth(String user, String pass) throws IOException {
        OAuthService oa = new OAuthService();
        oa.getClient().setCredentials(user, pass);
        auth = getAuth(SmallLib.appName,oa);
    }

    private Authorization getAuth(String appName, OAuthService oAuthService) throws IOException {
        Authorization out = null;
        for (Authorization a : oAuthService.getAuthorizations()) {
            if (a!=null && a.getApp().getName().contains(appName)){
                out = a;
            }
        }
        if (out!=null){
            return out;
        }else{
            return createAuth(oAuthService);
        }
    }

    private Authorization createAuth(OAuthService oAuthService) throws IOException {
        return oAuthService.createAuthorization(new Authorization().setApp(new Application().setName(SmallLib.appName)).setNote(SmallLib.appName).setScopes(SmallLib.scopes));
    }
    //End Auth section

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        lib.onTaskStarted("AuthTaskStarted");
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            tryAuth(userName, userPass);
        } catch (IOException e) {
            lib.onTaskError(e.getMessage());
            e.printStackTrace();
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if ((token = auth.getToken())!=null) {
            lib.setToken(token);
            lib.isAuth = true;
        }
        lib.onTaskFinish("AuthTaskFinish");
    }

}

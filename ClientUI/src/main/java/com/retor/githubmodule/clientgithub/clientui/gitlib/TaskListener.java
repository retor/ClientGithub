package com.retor.githubmodule.clientgithub.clientui.gitlib;

/**
 * Created by retor on 16.01.2015.
 */
public interface TaskListener {
    public void onTaskStarted(String msg);
    public void onTaskFinish(String msg);
    public void onTaskError(String msg);
}

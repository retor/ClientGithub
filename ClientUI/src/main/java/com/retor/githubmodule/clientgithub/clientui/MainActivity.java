package com.retor.githubmodule.clientgithub.clientui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.retor.githubmodule.clientgithub.clientui.gitlib.PrefWork;

public class MainActivity extends FragmentActivity {

    public MenuItem item;
    private int back = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PrefWork.loadPref(this) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.frame, new ReposFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.frame, new PasswordFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        item = menu.findItem(R.id.logout);
        if (PrefWork.loadPref(this)!=null)
            item.setEnabled(true);
        else
            item.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(PrefWork.loadPref(this)!=null){
            PrefWork.delPref(this);
            replaceFragments(new PasswordFragment());
            item.setEnabled(false);
        }
        back = 0;
        return super.onOptionsItemSelected(item);
    }

    public void replaceFragments(Fragment newFragment){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.setCustomAnimations(R.anim.drop_down, R.anim.to_top, R.anim.to_top,R.anim.drop_down);
        transaction.replace(R.id.frame, newFragment);
        transaction.commit();
        back = 0;
    }

    @Override
    public void onBackPressed() {
        back++;
        switch (back){
            case 1:
                super.onBackPressed();
                break;
            case 2:
                finish();
                break;
        }
    }
}

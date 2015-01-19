package com.retor.githubmodule.clientgithub.clientui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.retor.githubmodule.clientgithub.clientui.gitlib.PicturesLoader;
import it.gmariotti.cardslib.library.internal.CardExpand;
import org.eclipse.egit.github.core.Repository;

/**
 * Created by retor on 16.01.2015.
 */
public class MyCard extends CardExpand {

    Repository repository;

    //Views to card
    private TextView authorName;
    private ImageView authorAvatar;
    private TextView forks;
    private TextView watchers;
    private PicturesLoader picturesLoader;

    public MyCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public MyCard(Context context, int innerLayout, Repository repository){
        super(context, innerLayout);
        this.repository = repository;
        picturesLoader = PicturesLoader.instance();
        picturesLoader.fillCache(repository.getOwner().getAvatarUrl());
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        initViews(parent);
        fillViews();
    }

    private void initViews(ViewGroup parent){
        authorAvatar = (ImageView)parent.findViewById(R.id.avatar);
        authorName = (TextView)parent.findViewById(R.id.avtor);
        forks = (TextView)parent.findViewById(R.id.forks);
        watchers = (TextView)parent.findViewById(R.id.watchers);
    }

    private void fillViews(){
        picturesLoader.loadImage(authorAvatar,repository.getOwner().getAvatarUrl());
        if (repository.getOwner().getName()!=null)
            authorName.setText(repository.getOwner().getName());
        else
            authorName.setText(repository.getOwner().getLogin());
        forks.setText(String.valueOf(repository.getForks()));
        watchers.setText(String.valueOf(repository.getWatchers()));
    }
}

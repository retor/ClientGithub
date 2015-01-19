package com.retor.githubmodule.clientgithub.clientui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import com.retor.githubmodule.clientgithub.clientui.gitlib.PicturesLoader;
import com.retor.githubmodule.clientgithub.clientui.gitlib.PrefWork;
import com.retor.githubmodule.clientgithub.clientui.gitlib.SmallLib;
import com.retor.githubmodule.clientgithub.clientui.gitlib.TaskListener;
import it.gmariotti.cardslib.library.internal.*;
import it.gmariotti.cardslib.library.view.CardListView;
import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;

/**
 * Created by retor on 16.01.2015.
 */
public class ReposFragment extends Fragment implements TaskListener {

    CardListView cardsList;
    SmallLib lib;
    PicturesLoader pl = PicturesLoader.instance();
    AlertDialog d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initViews(View view){
        cardsList = (CardListView)view.findViewById(R.id.cardslist);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.repos_list, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PrefWork.loadPref(getActivity())==null)
            lib = SmallLib.getInstance(this);
        else
            lib = SmallLib.getInstance(PrefWork.loadPref(getActivity()), this);
        lib.setNewListener(this);
        lib.runRepoTask();
        initViews(view);
    }

    private void fillCards(ArrayList<Repository> repos){
        ArrayList<Card> cardsArray = new ArrayList<Card>();
        for (final Repository r:repos){
            Card card = new Card(getActivity());
            CardHeader header = new CardHeader(getActivity());
            CardThumbnail thumb = new CardThumbnail(getActivity());
            CardExpand expand = new MyCard(getActivity(),R.layout.card,r);
            header.setButtonExpandVisible(true);
            header.setTitle(r.getName());
            thumb.setExternalUsage(true);
            thumb.setDrawableResource(R.drawable.git_repo);
            thumb.setTitle(r.getDescription());
            thumb.setParentCard(card);
            card.addCardHeader(header);
            card.addCardExpand(expand);
            card.setTitle(r.getDescription());
            card.setShadow(true);
            card.setClickable(true);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    String tmp = card.getCardHeader().getTitle();
                    if (lib.checkListCommits(tmp)) {
                        Bundle args = new Bundle();
                        args.putString("repo", tmp);
                        InfoDialog id = new InfoDialog();
                        /*id.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);*/
                        id.setArguments(args);
                        id.show(getActivity().getSupportFragmentManager(), "Info");
                    } else {
                        SmallLib.initStatusDialog(getActivity(), "Error", "No commits now", 1).show();
                    }
                }
            });
            cardsArray.add(card);
        }
        cardsList.setAdapter(new CardArrayAdapter(getActivity(), cardsArray));
        cardsList.setAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.my_fade_in));
        cardsList.startLayoutAnimation();
    }

    @Override
    public void onTaskStarted(String msg) {
        d = SmallLib.initStatusDialog(getActivity(), "Loading", "Loading information", 0);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                d.show();
            }
        });
    }

    @Override
    public void onTaskFinish(String msg) {
        fillCards(lib.getRepositories());
        d.dismiss();
    }

    @Override
    public void onTaskError(String msg) {

    }

    @Override
    public void onDestroy() {
        lib.dispose();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setRetainInstance(true);
    }
}

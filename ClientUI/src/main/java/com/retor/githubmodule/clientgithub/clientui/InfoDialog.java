package com.retor.githubmodule.clientgithub.clientui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.retor.githubmodule.clientgithub.clientui.gitlib.SmallLib;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;
import org.eclipse.egit.github.core.RepositoryCommit;

import java.util.ArrayList;

/**
 * Created by retor on 18.01.2015.
 */
public class InfoDialog extends DialogFragment {

    CardListView cardsList;
    SmallLib lib;
    ArrayList<RepositoryCommit> commits;

    private void initViews(View view){
        lib = SmallLib.getInstance(this);
        cardsList = (CardListView)view.findViewById(R.id.cardslist);
        cardsList.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.drop_down_item));
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
        initViews(view);
        String arg = getArguments().getString("repo");
        commits = lib.getCommits(arg);
        fillCards(commits);
        getDialog().setTitle(arg +": "+ String.valueOf(commits.size()));
        cardsList.startLayoutAnimation();
    }

    private void fillCards(ArrayList<RepositoryCommit> commits){
        ArrayList<Card> cardsArray = new ArrayList<Card>();
        for (RepositoryCommit c:commits){
            SmallCard cardGP = new SmallCard(getActivity());
            cardGP.setSha_string(c.getSha());
            cardGP.setOwner_string(c.getCommitter().getLogin());
            cardGP.setDescription_string(c.getCommit().getMessage());
            cardGP.setData_string(c.getCommit().getCommitter().getDate().toString());
            Card card = new Card(getActivity());
            CardHeader header = new CardHeader(getActivity());
            header.setTitle(c.getSha()+" "+c.getCommitter().getLogin());
            card.setTitle(c.getCommit().getMessage()+" "+c.getCommit().getCommitter().getDate());
            card.addCardHeader(header);
            cardGP.init();
            cardsArray.add(cardGP);
        }
        cardsList.setAdapter(new CardArrayAdapter(getActivity().getApplicationContext(), cardsArray));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public class SmallCard extends Card {

        protected TextView sha;
        protected TextView owner;
        protected TextView description;
        protected int resourceIdThumbnail;
        protected String sha_string;
        protected String owner_string;
        protected String description_string;
        protected String data_string;

        public SmallCard(Context context) {
            this(context, R.layout.commit_layout);
        }

        public SmallCard(Context context, int innerLayout) {
            super(context, innerLayout);

        }

        private void init() {
            CardThumbnail cardThumbnail = new CardThumbnail(mContext);
            if (resourceIdThumbnail==0)
                cardThumbnail.setDrawableResource(it.gmariotti.cardslib.library.R.drawable.ic_menu_overflow_card_rounded_dark_normal);
            else{
                cardThumbnail.setDrawableResource(resourceIdThumbnail);
            }
            addCardThumbnail(cardThumbnail);
            CardHeader ch = new CardHeader(mContext);
            ch.setTitle(data_string);
            addCardHeader(ch);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            sha = (TextView) parent.findViewById(R.id.card_commit_sha);
            owner = (TextView) parent.findViewById(R.id.card_commit_owner);
            description = (TextView) parent.findViewById(R.id.card_commit_description);
            sha.setText(sha_string);
            owner.setText(owner_string);
            description.setText(description_string);
            init();
        }

        public String getData_string() {
            return data_string;
        }

        public void setData_string(String data_string) {
            this.data_string = data_string;
        }

        public String getSha_string() {
            return sha_string;
        }

        public void setSha_string(String sha_string) {
            this.sha_string = sha_string;
        }

        public String getOwner_string() {
            return owner_string;
        }

        public void setOwner_string(String owner_string) {
            this.owner_string = owner_string;
        }

        public String getDescription_string() {
            return description_string;
        }

        public void setDescription_string(String description_string) {
            this.description_string = description_string;
        }

        public int getResourceIdThumbnail() {
            return resourceIdThumbnail;
        }

        public void setResourceIdThumbnail(int resourceIdThumbnail) {
            this.resourceIdThumbnail = resourceIdThumbnail;
        }
    }
}

package com.application.jorge.whereappu.Cards;

import android.content.Context;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.dexafree.materialList.cards.SimpleCard;

/**
 * Created by Jorge on 01/06/2015.
 */
public class ContactCard extends SimpleCard {
    User contact;
    public ContactCard(Context context, User user) {
        super(context);
        contact = user;
    }

    @Override
    public int getLayout() {
        return R.layout.contact_card;
    }
}

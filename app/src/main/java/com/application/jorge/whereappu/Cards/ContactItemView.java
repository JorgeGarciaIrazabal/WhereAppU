package com.application.jorge.whereappu.Cards;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.jorge.whereappu.R;
import com.dexafree.materialList.model.CardItemView;

/**
 * Created by Jorge on 01/06/2015.
 */
public class ContactItemView extends CardItemView<ContactCard> {
    TextView name;
    TextView lastMessage;
    ImageView photo;
    // Default constructors
    public ContactItemView(Context context) {
        super(context);
    }

    public ContactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(ContactCard card) {
        name = (TextView)findViewById(R.id.nameEdit);
        name.setText(card.contact.Name);
        lastMessage = (TextView)findViewById(R.id.lastMessage);
        lastMessage.setText(card.contact.getLastTask());
        photo = (ImageView) findViewById(R.id.photo);
        photo.setImageDrawable(card.contact.getPhoto());
    }


}

package com.application.jorge.whereappu.Cards;

/**
 * Created by Jorge on 04/07/2015.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private List<User> contacts;
    private Context context;


    public ContactAdapter(Context context, List<User> data) {
        this.context = context;
        this.contacts = data;
    }


    public User getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contact, viewGroup, false);
        ContactViewHolder vh = new ContactViewHolder(itemView, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int i) {
        holder.build(getItem(i));
    }


    @Override
    public long getItemId(int i) {
        return getItem(i).ID;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        final PackageItem item = getItem(position);
//        ViewHolder holder;
//        if (convertView == null) {
//            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = li.inflate(R.layout.package_row, parent, false);
//            holder = new ViewHolder();
//            holder.ivImage = (ImageView) convertView.findViewById(R.id.example_row_iv_image);
//            holder.tvTitle = (TextView) convertView.findViewById(R.id.example_row_tv_title);
//            holder.tvDescription = (TextView) convertView.findViewById(R.id.example_row_tv_description);
//            holder.bAction1 = (Button) convertView.findViewById(R.id.example_row_b_action_1);
//            holder.bAction2 = (Button) convertView.findViewById(R.id.example_row_b_action_2);
//            holder.bAction3 = (Button) convertView.findViewById(R.id.example_row_b_action_3);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        ((SwipeListView)parent).recycle(convertView, position);
//
//        holder.ivImage.setImageDrawable(item.getIcon());
//        holder.tvTitle.setText(item.getName());
//        holder.tvDescription.setText(item.getPackageName());
//
//
//        holder.bAction1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = context.getPackageManager().getLaunchIntentForPackage(item.getPackageName());
//                if (intent != null) {
//                    context.startActivity(intent);
//                } else {
//                    Toast.makeText(context, R.string.cantOpen, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        holder.bAction2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPlayStoreInstalled()) {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id=" + item.getPackageName())));
//                } else {
//                    context.startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + item.getPackageName())));
//                }
//            }
//        });
//
//        holder.bAction3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri packageUri = Uri.parse("package:" + item.getPackageName());
//                Intent uninstallIntent;
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
//                } else {
//                    uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
//                }
//                context.startActivity(uninstallIntent);
//            }
//        });
//
//
//        return convertView;
//    }


}
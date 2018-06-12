package com.meetingroomscheduler.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.meetingroomscheduler.Activity.EditSchedule;
import com.meetingroomscheduler.Class.User;
import com.meetingroomscheduler.Global;
import com.meetingroomscheduler.R;

import java.util.ArrayList;

/**
 * Editing the users that were invited to the meeting
 */

public class EditScheduleInvitedUsersListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<User> existing_users;

    private boolean sent = false;

    public EditScheduleInvitedUsersListAdapter(Context context, ArrayList<User> existing_users) {

        super(context, R.layout.invited_user_item, new String[existing_users.size()]);

        this.context = (Activity) context;
        this.existing_users = existing_users;

        sent = false;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.invited_user_item, null, true);

        TextView fullname = (TextView) rowView.findViewById(R.id.invited_user_fullname);
        ImageView delete = (ImageView) rowView.findViewById(R.id.invited_user_delete);

        fullname.setText(existing_users.get(pos).fullname);

        if(existing_users.get(pos).id.equals(Global.current_user.id)){
            delete.setVisibility(View.GONE);
        }else{
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditSchedule.invited_users.remove(pos);
                    EditSchedule.invitations --;
                    EditSchedule.renderInvitations();
                }
            });
        }

        return rowView;
    }

}
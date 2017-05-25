package com.jainsaab.collegeconnect.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jainsaab.collegeconnect.R;
import com.jainsaab.collegeconnect.Utility;
import com.jainsaab.collegeconnect.signingin.RegisterAsActivity;
import com.jainsaab.collegeconnect.teacher.StudentRequest;

import java.util.HashMap;

public class StudentActivity extends AppCompatActivity {

    SharedPreferences mGetPrefs;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mFirebaseDatabaseReference;
    FirebaseUser mFirebaseUser;
    String mEmail;
    RecyclerView mReminderRecyclerView;
    LinearLayout mReminderEmptyView;
    LinearLayoutManager mLinearLayoutManager;
    FirebaseRecyclerAdapter<StudentRequest, ReminderViewHolder> mRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mEmail = mFirebaseUser.getEmail();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("students")
                .child(Utility.escapeEmailAddress(mEmail));
        mReminderRecyclerView = (RecyclerView) findViewById(R.id.reminders_recycler_view);
        mReminderEmptyView = (LinearLayout) findViewById(R.id.reminders_empty_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mReminderRecyclerView.setLayoutManager(mLinearLayoutManager);

        mFirebaseDatabaseReference.child("requests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(StudentRequest.class) != null) {
                            mRequestAdapter = new FirebaseRecyclerAdapter<StudentRequest, ReminderViewHolder>(StudentRequest.class,
                                    R.layout.request_reminder_layout,
                                    ReminderViewHolder.class,
                                    mFirebaseDatabaseReference.child("requests")) {
                                @Override
                                protected void populateViewHolder(ReminderViewHolder viewHolder, final StudentRequest model, int position) {
                                    mReminderEmptyView.setVisibility(View.GONE);
                                    viewHolder.reminderTextView.setText(model.getmRequests().getmUserName() + " wants to add you to " + model.getmRequests().getmClassName() + " class.");

                                    viewHolder.declineButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mFirebaseDatabaseReference.child("requests")
                                                    .child(Utility.escapeEmailAddress(model.getmRequests().getmEmailId()))
                                                    .child(model.getmRequests().getmClassName()).removeValue();
                                            Toast.makeText(StudentActivity.this, "The request has been declined", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    viewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String accept = "Accept";
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("mStatus", accept);

                                            mFirebaseDatabaseReference.child("requests")
                                                    .child(Utility.escapeEmailAddress(model.getmRequests().getmEmailId()))
                                                    .child(model.getmRequests().getmClassName()).updateChildren(hashMap);

                                            Toast.makeText(StudentActivity.this, "The request has been accepted.", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            };

                            mRequestAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);
                                    int reminderCount = mRequestAdapter.getItemCount();
                                    int lastVisiblePosition =
                                            mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                                    if (lastVisiblePosition == -1 ||
                                            (positionStart >= (reminderCount - 1) &&
                                                    lastVisiblePosition == (positionStart - 1))) {
                                        mReminderRecyclerView.scrollToPosition(positionStart);
                                    }
                                }

                                @Override
                                public void onItemRangeChanged(int positionStart, int itemCount) {
                                    if (itemCount == 0) {
                                        mReminderEmptyView.setVisibility(View.VISIBLE);
                                    }
                                    super.onItemRangeChanged(positionStart, itemCount);
                                }
                            });

                            mReminderRecyclerView.setLayoutManager(mLinearLayoutManager);
                            mReminderRecyclerView.setAdapter(mRequestAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.student_sign_out_menu:
                Utility.signedOutCleanUp(this, mGetPrefs);
                startActivity(new Intent(this, RegisterAsActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTextView;
        Button declineButton;
        Button acceptButton;

        public ReminderViewHolder(View v) {
            super(v);
            reminderTextView = (TextView) itemView.findViewById(R.id.request_text_view);
            acceptButton = (Button) itemView.findViewById(R.id.accept_btn);
            declineButton = (Button) itemView.findViewById(R.id.decline_btn);
        }
    }
}

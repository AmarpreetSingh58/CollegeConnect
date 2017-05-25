package com.jainsaab.collegeconnect.signingin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jainsaab.collegeconnect.MainActivity;
import com.jainsaab.collegeconnect.R;
import com.jainsaab.collegeconnect.Utility;
import com.jainsaab.collegeconnect.student.Student;
import com.jainsaab.collegeconnect.teacher.Teacher;

import java.util.ArrayList;
import java.util.List;

import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.COLLEGE_SELECTED_VALUE;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.DEFAULT_RC_SIGN_IN;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.STUDENT_RC_SIGN_IN;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.TEACHER_RC_SIGN_IN;
import static com.jainsaab.collegeconnect.signingin.RegisterAsActivity.USER_TYPE;

public class SelectCollegeActivity extends AppCompatActivity {

    public static final String COLLEGE_SELECTED = "college_selected";
    public static final String COLLEGE_SELECTED_NAME = "college_selected_name";

    SharedPreferences mGetPrefs;
    FloatingSearchView mFloatingSearchView;
    ListView mCollegesListView;
    ProgressBar mEmptyListProgressBar;
    LinearLayout mEmptyListLinearLayout;
    TextView mEmptyListTextView;
    FloatingActionButton mAddCollegeFab;
    ArrayAdapter<String> mCollegeListAdapter;
    ProgressDialog dialog;
    AppBarLayout mAppBar;
    DatabaseReference mCollegesDatabaseReference;
    FirebaseAuth mFirebaseAuth;
    private int mUserType;
    private String mUserName;
    private String mEmailId, mCollegeName;
    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mUserType = mGetPrefs.getInt(USER_TYPE, DEFAULT_RC_SIGN_IN);
        if (mUserType == TEACHER_RC_SIGN_IN) {
            setTheme(R.style.TeacherSelectCollegeTheme);
        } else if (mUserType == STUDENT_RC_SIGN_IN) {
            setTheme(R.style.StudentSelectCollegeTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_college);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCollegesDatabaseReference = mFirebaseDatabase.getReference().child("colleges");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mUserName = mFirebaseUser.getDisplayName();
            mEmailId = mFirebaseUser.getEmail();
        }

        mFloatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mCollegesListView = (ListView) findViewById(R.id.select_college_list_view);
        mEmptyListProgressBar = (ProgressBar) findViewById(R.id.empty_list_progress_bar);
        mEmptyListLinearLayout = (LinearLayout) findViewById(R.id.empty_list_linear_layout);
        mEmptyListTextView = (TextView) findViewById(R.id.empty_list_text_view);
        mAddCollegeFab = (FloatingActionButton) findViewById(R.id.add_new_college_fab);

        mAppBar = (AppBarLayout) findViewById(R.id.select_college_appbar);
        if (mUserType == TEACHER_RC_SIGN_IN) {
            mAppBar.setBackgroundColor(getResources().getColor(R.color.colorRegisterAsTeacher));
        } else if (mUserType == STUDENT_RC_SIGN_IN) {
            mAppBar.setBackgroundColor(getResources().getColor(R.color.colorRegisterAsStudent));
        }

        mCollegesListView.setEmptyView(mEmptyListLinearLayout);

        initProgressDialog(R.string.dialog_please_wait_title,
                getString(R.string.dialog_loading_your_profile_msg),
                false,
                R.string.dialog_positive_button_text,
                false,
                0);

        startSelectCollegeOrNot(mUserType, mEmailId);

        if (!Utility.isNetworkAvailable(SelectCollegeActivity.this)) {
            mEmptyListProgressBar.setVisibility(View.GONE);
            mEmptyListTextView.setText(R.string.no_network);
        } else {
            mEmptyListProgressBar.setVisibility(View.VISIBLE);
            mEmptyListTextView.setText(R.string.loading_colleges);
        }

        mAddCollegeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.feature_under_construction, Toast.LENGTH_SHORT).show();
            }
        });

        mCollegesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mEmptyListProgressBar.setVisibility(View.VISIBLE);
                mEmptyListTextView.setText(R.string.loading_colleges);

                final List<String> colleges = new ArrayList<>();
                for (DataSnapshot collegeSnapshot : dataSnapshot.getChildren()) {
                    String collegeName = collegeSnapshot.child("college_name").getValue(String.class);
                    colleges.add(collegeName);
                }

                mCollegeListAdapter = new ArrayAdapter<>(SelectCollegeActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, colleges);
                mCollegesListView.setAdapter(mCollegeListAdapter);

                mFloatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
                    @Override
                    public void onSearchTextChanged(String oldQuery, String newQuery) {
                        mCollegeListAdapter.getFilter().filter(newQuery);
                        if (mCollegeListAdapter.getCount() == 0) {
                            mEmptyListProgressBar.setVisibility(View.GONE);
                            mEmptyListTextView.setText(R.string.no_clg_matching_query);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mCollegesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String collegeName = mCollegeListAdapter.getItem(i);
                mCollegeName = collegeName;

                if (mUserType == TEACHER_RC_SIGN_IN) {
                    DatabaseReference teachersDatabaseReference = mFirebaseDatabase.getReference().child("teachers");

                    Teacher newTeacher = new Teacher(mFirebaseUser.getDisplayName(),
                            mEmailId, collegeName);
                    teachersDatabaseReference.child(Utility.escapeEmailAddress(mEmailId)).setValue(newTeacher);
                    if (collegeName != null) {
                        mFirebaseDatabase.getReference().child("colleges")
                                .child(collegeName)
                                .child("teachers")
                                .child(Utility.escapeEmailAddress(mEmailId))
                                .setValue(newTeacher);
                    }
                } else if (mUserType == STUDENT_RC_SIGN_IN) {
                    DatabaseReference studentsDatabaseReference = mFirebaseDatabase.getReference().child("students");
                    Student newStudent = new Student(mFirebaseUser.getDisplayName(),
                            mEmailId, collegeName);
                    studentsDatabaseReference.child(Utility.escapeEmailAddress(mEmailId)).setValue(newStudent);
                    if (collegeName != null) {
                        mFirebaseDatabase.getReference().child("colleges")
                                .child(collegeName)
                                .child("students")
                                .child(Utility.escapeEmailAddress(mEmailId))
                                .setValue(newStudent);
                    }
                }
                startMainActivity();
            }
        });

        mFloatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.select_college_sign_out_menu:
                        Utility.signedOutCleanUp(SelectCollegeActivity.this, mGetPrefs);
                        startActivity(new Intent(SelectCollegeActivity.this, RegisterAsActivity.class));
                        finish();
                }
            }
        });
    }

    private void startSelectCollegeOrNot(final int userType, final String userEmail) {

        DatabaseReference databaseReference;
        final DatabaseReference correctSignInReference;
        final String correctUserTypeStr;
        final int correctUserType;
        if (userType == TEACHER_RC_SIGN_IN) {
            databaseReference = mFirebaseDatabase.getReference().child("teachers");
            correctSignInReference = mFirebaseDatabase.getReference().child("students");
            correctUserTypeStr = getString(R.string.register_as_student);
            correctUserType = STUDENT_RC_SIGN_IN;
        } else {
            databaseReference = mFirebaseDatabase.getReference().child("students");
            correctSignInReference = mFirebaseDatabase.getReference().child("teachers");
            correctUserTypeStr = getString(R.string.register_as_teacher);
            correctUserType = TEACHER_RC_SIGN_IN;
        }

        databaseReference.child(Utility.escapeEmailAddress(userEmail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            mCollegeName = dataSnapshot.getValue(Teacher.class).getCollegeName();
                            initProgressDialog(R.string.dialog_great_title,
                                    getString(R.string.dialog_already_msg)
                                            + mCollegeName,
                                    false,
                                    R.string.dialog_positive_button_text,
                                    true,
                                    R.drawable.ic_school_black_24dp);
                        } else {
                            correctSignInReference.child(Utility.escapeEmailAddress(userEmail))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                if (dialog.isShowing())
                                                    dialog.dismiss();
                                                if (correctUserType == TEACHER_RC_SIGN_IN)
                                                    mCollegeName = dataSnapshot.getValue(Teacher.class).getCollegeName();
                                                else
                                                    mCollegeName = dataSnapshot.getValue(Student.class).getCollegeName();
                                                initAlertDialog(R.drawable.ic_account_box_black_24dp,
                                                        R.string.dialog_wrong_user_title,
                                                        String.format(getString(R.string.dialog_wrong_user_msg),
                                                                Utility.getFirstName(mUserName), correctUserTypeStr),
                                                        false,
                                                        String.format(getString(R.string.dialog_positive_button_wrong_user_text),
                                                                correctUserTypeStr),
                                                        correctUserType);
                                            } else {
                                                initProgressDialog(R.string.welcome_toast,
                                                        getString(R.string.dialog_no_college_msg),
                                                        true,
                                                        R.string.dialog_positive_button_select_college_text,
                                                        true,
                                                        R.drawable.ic_school_black_24dp);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void initProgressDialog(final int titleId, String message, boolean cancelable,
                                    int btnTextId, boolean btnEnable, int drawableId) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = new ProgressDialog(SelectCollegeActivity.this);
        dialog.setTitle(getString(titleId));
        dialog.setMessage(message);
        dialog.setCancelable(cancelable);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(btnTextId),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (titleId == R.string.dialog_great_title) {
                            startMainActivity();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
        if (drawableId != 0)
            dialog.setIndeterminateDrawable(getResources().getDrawable(drawableId));
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(btnEnable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            initAlertDialog(R.drawable.ic_exit_to_app_black_24dp,
                    R.string.exit_dialog_title,
                    getString(R.string.exit_dialog_msg),
                    true,
                    getString(R.string.dialog_yes),
                    0);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initAlertDialog(int iconId, int titleId, String message,
                                 final Boolean cancelable, String positiveText, final int correctUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(iconId);
        builder.setTitle(titleId);
        builder.setMessage(message);
        builder.setCancelable(cancelable);

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!cancelable) {
                    signOutFromSelectCollege();
                }
            }
        });

        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cancelable) {
                    signOutFromSelectCollege();
                } else {
                    SharedPreferences.Editor preferenceEditor = mGetPrefs.edit();
                    preferenceEditor.putInt(USER_TYPE, correctUser);
                    preferenceEditor.apply();
                    startMainActivity();
                }
            }
        });
        builder.show();
    }

    private void signOutFromSelectCollege() {
        mCollegeListAdapter.clear();
        mCollegeListAdapter.notifyDataSetChanged();
        Utility.signedOutCleanUp(SelectCollegeActivity.this, mGetPrefs);
        startActivity(new Intent(SelectCollegeActivity.this, RegisterAsActivity.class));
        finish();
    }

    private void startMainActivity() {
        SharedPreferences.Editor preferenceEditor = mGetPrefs.edit();
        preferenceEditor.putInt(COLLEGE_SELECTED, COLLEGE_SELECTED_VALUE);
        preferenceEditor.putString(COLLEGE_SELECTED_NAME, mCollegeName);
        preferenceEditor.apply();
        if (mUserName != null)
            Toast.makeText(getApplicationContext(), getString(R.string.welcome_toast) + Utility.getFirstName(mUserName),
                    Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        SelectCollegeActivity.this.finish();
    }

}

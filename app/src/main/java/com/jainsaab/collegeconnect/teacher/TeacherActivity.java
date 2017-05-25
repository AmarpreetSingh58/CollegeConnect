package com.jainsaab.collegeconnect.teacher;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jainsaab.collegeconnect.Classroom;
import com.jainsaab.collegeconnect.R;
import com.jainsaab.collegeconnect.Utility;
import com.jainsaab.collegeconnect.signingin.RegisterAsActivity;

public class TeacherActivity extends AppCompatActivity {

    public static final String CLASS_CHILD = "classes";
    public static final String CLASS_STUDENTS = "class_students";
    public static final String COLOR_CODE_TAG = "color_code";
    public static final String CLASS_NAME_TAG = "class_name";
    public static final String NO_OF_STUDENTS_TAG = "no_of_students";
    boolean flag = true;
    SharedPreferences mGetPrefs;
    CardView revealLayout, addClass, selectedColor;
    FrameLayout frameLayout;
    FloatingActionButton floatingActionButton;
    LinearLayout add_student_layout, emptyView;
    Animation alphaAnimation;
    RadioGroup radioGroup1, radioGroup2;
    EditText classNameEditText;
    RadioGroup.OnCheckedChangeListener listener1, listener2;
    RecyclerView mClassroomRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Classroom, ClassViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorRegisterAsTeacher)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorRegisterAsTeacherDark));
        }

        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        revealLayout = (CardView) findViewById(R.id.reveal_layout);
        frameLayout = (FrameLayout) findViewById(R.id.activity_teacher);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        add_student_layout = (LinearLayout) findViewById(R.id.sample_text);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        radioGroup1 = (RadioGroup) findViewById(R.id.radio_group_1);
        radioGroup2 = (RadioGroup) findViewById(R.id.radio_group_2);
        classNameEditText = (EditText) findViewById(R.id.class_name_edit_text);
        addClass = (CardView) findViewById(R.id.add_class);
        emptyView = (LinearLayout) findViewById(R.id.empty_view);
        selectedColor = (CardView) findViewById(R.id.selected_color);
        mClassroomRecyclerView = (RecyclerView) findViewById(R.id.classes_list_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mClassroomRecyclerView.setLayoutManager(mLinearLayoutManager);

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClassView();
            }
        });

        radioGroup1.clearCheck();
        radioGroup2.clearCheck();

        listener1 = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1) {
                    radioGroup2.setOnCheckedChangeListener(null);
                    radioGroup2.clearCheck();
                    radioGroup2.setOnCheckedChangeListener(listener2);
                    getRealCheck();
                }
            }
        };
        radioGroup1.setOnCheckedChangeListener(listener1);

        listener2 = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i != -1) {
                    radioGroup1.setOnCheckedChangeListener(null);
                    radioGroup1.clearCheck();
                    radioGroup1.setOnCheckedChangeListener(listener1);
                    getRealCheck();
                }
            }
        };
        radioGroup2.setOnCheckedChangeListener(listener2);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReveal();
            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C4A7")));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("teachers").child(Utility.escapeEmailAddress(mFirebaseUser.getEmail()));

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Classroom, ClassViewHolder>(Classroom.class,
                R.layout.classes_list_item,
                ClassViewHolder.class,
                mFirebaseDatabaseReference.child(CLASS_CHILD)) {
            @Override
            protected void populateViewHolder(final ClassViewHolder viewHolder, final Classroom model, int position) {
                if (mFirebaseAdapter.getItemCount() != 0) {
                    emptyView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
                viewHolder.className.setText(model.getmClassName());
                viewHolder.noOfStudents.setText("" + model.getmNoOfStudents());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.classCard.setBackgroundTintList(ColorStateList.valueOf(model.getmColorCode()));
                }

                viewHolder.classCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AddStudentActivity.class);
                        intent.putExtra(COLOR_CODE_TAG, model.getmColorCode());
                        intent.putExtra(CLASS_NAME_TAG, model.getmClassName());
                        intent.putExtra(NO_OF_STUDENTS_TAG, model.getmNoOfStudents());
                        startActivity(intent);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int classroomCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (classroomCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mClassroomRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mClassroomRecyclerView.setLayoutManager(mLinearLayoutManager);
        mClassroomRecyclerView.setAdapter(mFirebaseAdapter);

    }

    public void addClassView() {
        if (classNameEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please give your class a name", Toast.LENGTH_SHORT).show();
        } else {
            int color = selectedColor.getCardBackgroundColor().getDefaultColor();
            Classroom classroom = new Classroom(color,
                    classNameEditText.getText().toString(), 0);

            mFirebaseDatabaseReference.child(CLASS_CHILD).child(classNameEditText.getText().toString()).setValue(classroom);
            showReveal();
        }
    }

    private void getRealCheck() {
        int chkId1 = radioGroup1.getCheckedRadioButtonId();
        int chkId2 = radioGroup2.getCheckedRadioButtonId();
        int realCheck = chkId1 == -1 ? chkId2 : chkId1;
        RadioButton radioButton = (RadioButton) findViewById(realCheck);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            selectedColor.setCardBackgroundColor(radioButton.getBackgroundTintList());
        }

    }

    private void showReveal() {
        int x = floatingActionButton.getLeft();
        int y = floatingActionButton.getTop();

        int hypotenuse = (int) Math.hypot(frameLayout.getWidth(), frameLayout.getHeight());

        if (flag) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_clear_white_24dp));
            }
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                floatingActionButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#68C4A7")));
            }

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(revealLayout, x, y, 0, hypotenuse);
            }
            anim.setDuration(600);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    add_student_layout.setVisibility(View.VISIBLE);
                    add_student_layout.startAnimation(alphaAnimation);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            revealLayout.setVisibility(View.VISIBLE);
            anim.start();

            flag = false;
        } else {

            Animator anim = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(revealLayout, x, y, hypotenuse, 0);
            }
            anim.setDuration(400);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    revealLayout.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_group_add_white_24dp));
                    }
                    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#68C4A7")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        floatingActionButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    }
                    add_student_layout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            anim.start();
            flag = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.teacher_sign_out_menu:
                Utility.signedOutCleanUp(this, mGetPrefs);
                startActivity(new Intent(this, RegisterAsActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        TextView noOfStudents;
        LinearLayout classCard;

        public ClassViewHolder(View v) {
            super(v);
            className = (TextView) itemView.findViewById(R.id.class_name);
            noOfStudents = (TextView) itemView.findViewById(R.id.no_of_students);
            classCard = (LinearLayout) itemView.findViewById(R.id.class_card);
        }
    }
}

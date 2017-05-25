package com.jainsaab.collegeconnect.teacher;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.jainsaab.collegeconnect.Requests;
import com.jainsaab.collegeconnect.Utility;
import com.jainsaab.collegeconnect.signingin.SelectCollegeActivity;
import com.jainsaab.collegeconnect.student.Student;

import java.util.HashMap;

import static com.jainsaab.collegeconnect.teacher.TeacherActivity.CLASS_STUDENTS;

public class AddStudentActivity extends AppCompatActivity {

    int mNoOfStudents, mColor;
    boolean flag = true;
    FloatingActionButton floatingActionButton;
    String mEmail, mClassName, mCollegeName, mUserName;
    LinearLayout emptyView, add_student_layout;
    CardView revealLayout, addStudent;
    FrameLayout frameLayout;
    Animation alphaAnimation;
    TextView addTextView, collegeStudentsEmptyView, emptyRecyclerViewTextView;
    EditText studentEmailEditText;
    FirebaseUser mFirebaseUser;
    FirebaseAuth mFirebaseAuth;
    RecyclerView mStudentRecyclerView, mCollegeStudentRecyclerView;
    LinearLayoutManager mLinearLayoutManager1, mLinearLayoutManager2;
    DatabaseReference mFirebaseDatabaseReference, mStudentsInCollegeReference;
    FirebaseRecyclerAdapter<Student, StudentViewHolder> mFirebaseAdapter;
    FirebaseRecyclerAdapter<Student, CollegeStudentsViewHolder> mCollegeStudentAdapter;
    SharedPreferences mGetPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        Intent intent = getIntent();
        if (intent.hasExtra(TeacherActivity.COLOR_CODE_TAG) && intent.hasExtra(TeacherActivity.CLASS_NAME_TAG) &&
                intent.hasExtra(TeacherActivity.NO_OF_STUDENTS_TAG)) {
            mColor = intent.getIntExtra(TeacherActivity.COLOR_CODE_TAG, -1);
            mClassName = intent.getStringExtra(TeacherActivity.CLASS_NAME_TAG);
            mNoOfStudents = intent.getIntExtra(TeacherActivity.NO_OF_STUDENTS_TAG, 0);
        }
        setTitle(mClassName);
        mGetPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        mCollegeName = mGetPrefs.getString(SelectCollegeActivity.COLLEGE_SELECTED_NAME, "");

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_student);
        emptyView = (LinearLayout) findViewById(R.id.empty_view_students);
        revealLayout = (CardView) findViewById(R.id.reveal_layout_student);
        frameLayout = (FrameLayout) findViewById(R.id.activity_student_class);
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        add_student_layout = (LinearLayout) findViewById(R.id.sample_text_student);
        revealLayout.setCardBackgroundColor(mColor);
        addTextView = (TextView) findViewById(R.id.add_text_view);
        addTextView.setTextColor(mColor);
        collegeStudentsEmptyView = (TextView) findViewById(R.id.college_Students_empty_view);
        collegeStudentsEmptyView.setTextColor(mColor);
        emptyRecyclerViewTextView = (TextView) findViewById(R.id.empty_recycler_view_text_view);
        emptyRecyclerViewTextView.setTextColor(mColor);
        addStudent = (CardView) findViewById(R.id.add_student);
        studentEmailEditText = (EditText) findViewById(R.id.add_student_email_edit_text);
        mStudentRecyclerView = (RecyclerView) findViewById(R.id.students_list_view);
        mLinearLayoutManager1 = new LinearLayoutManager(this);
        mStudentRecyclerView.setLayoutManager(mLinearLayoutManager1);
        mCollegeStudentRecyclerView = (RecyclerView) findViewById(R.id.select_from_Students);
        mLinearLayoutManager2 = new LinearLayoutManager(this);
        mCollegeStudentRecyclerView.setLayoutManager(mLinearLayoutManager2);

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudentToClass();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReveal();
            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(mColor));

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            mEmail = mFirebaseUser.getEmail();
            mUserName = mFirebaseUser.getDisplayName();
        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("teachers").child(Utility.escapeEmailAddress(mFirebaseUser.getEmail()));

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Student, StudentViewHolder>(Student.class,
                R.layout.students_list_item,
                StudentViewHolder.class,
                mFirebaseDatabaseReference.child(TeacherActivity.CLASS_CHILD).child(mClassName).child(CLASS_STUDENTS)) {
            @Override
            protected void populateViewHolder(StudentViewHolder viewHolder, final Student model, int position) {
                emptyView.setVisibility(View.GONE);
                viewHolder.studentName.setText(model.getUserName());
                viewHolder.studentMail.setText("" + model.getEmailId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.studentCard.setBackgroundTintList(ColorStateList.valueOf(mColor));
                }
                viewHolder.sendButton.setTextColor(mColor);
                viewHolder.sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Reminder Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int studentCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager1.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (studentCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mStudentRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mStudentRecyclerView.setLayoutManager(mLinearLayoutManager1);
        mStudentRecyclerView.setAdapter(mFirebaseAdapter);

        mStudentsInCollegeReference = FirebaseDatabase.getInstance().getReference();

        mCollegeStudentAdapter = new FirebaseRecyclerAdapter<Student, CollegeStudentsViewHolder>(Student.class,
                R.layout.college_student_list_item,
                CollegeStudentsViewHolder.class,
                mStudentsInCollegeReference.child("colleges").child(mCollegeName).child("students")) {
            @Override
            protected void populateViewHolder(CollegeStudentsViewHolder viewHolder, final Student model, int position) {
                collegeStudentsEmptyView.setVisibility(View.GONE);
                viewHolder.collegeStudentMail.setText(model.getEmailId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    viewHolder.collegeStudentLinearLayout.setBackgroundTintList(ColorStateList.valueOf(mColor));
                }
                viewHolder.collegeStudentLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Student student = new Student();
                        student.setEmailId(model.getEmailId());
                        addStudentToFireBase(student);
                    }
                });
            }
        };
        mCollegeStudentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int studentCount = mCollegeStudentAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager2.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (studentCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mCollegeStudentRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mCollegeStudentRecyclerView.setLayoutManager(mLinearLayoutManager2);
        mCollegeStudentRecyclerView.setAdapter(mCollegeStudentAdapter);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mColor);
        }

    }

    private void addStudentToClass() {
        if (studentEmailEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter student email id.", Toast.LENGTH_SHORT).show();
        } else {
            String email = studentEmailEditText.getText().toString();
            Student student = new Student();
            student.setEmailId(email);
            addStudentToFireBase(student);
        }
    }

    private void addStudentToFireBase(final Student student) {

        final String email = Utility.escapeEmailAddress(student.getEmailId());

        mStudentsInCollegeReference.child("students").child(email)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Student.class) != null) {

                            Requests newRequest = new Requests(mUserName, mClassName, mEmail, "Decline");

                            mStudentsInCollegeReference.child("students").child(email).child("requests")
                                    .child(Utility.escapeEmailAddress(mEmail))
                                    .child(mClassName)
                                    .setValue(newRequest);

                            mStudentsInCollegeReference.child("students").child(email).child("requests")
                                    .child(Utility.escapeEmailAddress(mEmail))
                                    .child("mEmail")
                                    .setValue(mEmail);

                            final DataSnapshot dataSnapshot1 = dataSnapshot;

                            mStudentsInCollegeReference.child("students").child(email).child("requests")
                                    .child(Utility.escapeEmailAddress(mEmail))
                                    .child(mClassName)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Requests requests = dataSnapshot.getValue(Requests.class);
                                            if (requests != null) {
                                                if (requests.getmStatus().equals("Accept")) {
                                                    mNoOfStudents = mNoOfStudents + 1;
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("mNoOfStudents", mNoOfStudents);

                                                    mFirebaseDatabaseReference.child(TeacherActivity.CLASS_CHILD)
                                                            .child(mClassName).updateChildren(hashMap);

                                                    student.setUserName(dataSnapshot1.getValue(Student.class).getUserName());
                                                    mFirebaseDatabaseReference.child(TeacherActivity.CLASS_CHILD)
                                                            .child(mClassName)
                                                            .child(CLASS_STUDENTS)
                                                            .child(Utility.escapeEmailAddress(student.getEmailId()))
                                                            .setValue(student);
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "The request has been sent to the student.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            showReveal();
                        } else {
                            Toast.makeText(getApplicationContext(), "The student does not exists.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                floatingActionButton.setImageTintList(ColorStateList.valueOf(mColor));
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
                        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_person_add_white_24dp));
                    }
                    floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(mColor));
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

    private static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        TextView studentMail;
        Button sendButton;
        LinearLayout studentCard;

        public StudentViewHolder(View v) {
            super(v);
            studentName = (TextView) itemView.findViewById(R.id.student_name);
            studentMail = (TextView) itemView.findViewById(R.id.student_email);
            sendButton = (Button) itemView.findViewById(R.id.send_button);
            studentCard = (LinearLayout) itemView.findViewById(R.id.add_student_item);
        }
    }

    private static class CollegeStudentsViewHolder extends RecyclerView.ViewHolder {
        TextView collegeStudentMail;
        LinearLayout collegeStudentLinearLayout;

        public CollegeStudentsViewHolder(View v) {
            super(v);
            collegeStudentMail = (TextView) itemView.findViewById(R.id.college_students_list_text_view);
            collegeStudentLinearLayout = (LinearLayout) itemView.findViewById(R.id.college_students_linear_layout);
        }
    }
}

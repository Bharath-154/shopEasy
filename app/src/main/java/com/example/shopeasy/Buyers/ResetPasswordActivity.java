package com.example.shopeasy.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopeasy.Prevalent.Prevalent;
import com.example.shopeasy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView pageTitle,titleQuestions;
    private EditText phoneNumber,question1,question2;
    private Button verifyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        check = getIntent().getStringExtra("check");
        pageTitle=findViewById(R.id.page_title);
        titleQuestions=findViewById(R.id.title_questions);
        phoneNumber= findViewById(R.id.find_phone_number);
        question1=findViewById(R.id.question_1);
        question2=findViewById(R.id.question_2);
        verifyButton=findViewById(R.id.verify_btn);

    }



    @Override
    protected void onStart()
    {
        super.onStart();


        if (check.equals("settings"))
        {
          pageTitle.setText("Set Questions");
          titleQuestions.setText("Please set the following security questions");
          phoneNumber.setVisibility(View.GONE);
          verifyButton.setText("Set");
          displayPreviousAnswers();
          verifyButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                setAnswers();
              }
          });
        }
        else if (check.equals("login"))
        {
            pageTitle.setText("Reset Password");
            titleQuestions.setText("Answer below security questions to reset password");
            phoneNumber.setVisibility(View.VISIBLE);
            verifyButton.setText("Verify");
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser() {
        final String phone=phoneNumber.getText().toString();
        final String answer1=question1.getText().toString().toLowerCase();
        final String answer2=question2.getText().toString().toLowerCase();

        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(phone);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("SecurityQuestions"))
                    {
                       String ans1InDatabase=snapshot.child("SecurityQuestions").child("answer1").getValue().toString();
                        String ans2InDatabase=snapshot.child("SecurityQuestions").child("answer2").getValue().toString();
                         if(ans1InDatabase.equals(answer1)&&ans2InDatabase.equals(answer2))
                         {
                            AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                            builder.setTitle("New Password");

                            final EditText newPassword=new EditText(ResetPasswordActivity.this);
                            newPassword.setHint("Enter new password here");

                            builder.setView(newPassword);
                            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @NonNull
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    if(!newPassword.getText().toString().equals(""))
                                    {
                                        ref.child("password")
                                                .setValue(newPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(ResetPasswordActivity.this, "Password changed succesfully", Toast.LENGTH_SHORT).show();
                                                            dialogInterface.dismiss();
                                                            Intent intent=new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                        else
                                                        {
                                                            dialogInterface.dismiss();
                                                            Toast.makeText(ResetPasswordActivity.this, "An unexpected error occured ,please try again", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                            builder.show();
                        }
                          else
                          {
                            Toast.makeText(ResetPasswordActivity.this, "Answer1 or Answer2 is incorrect", Toast.LENGTH_SHORT).show();
                          }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Can't recover this account,answers not found", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(ResetPasswordActivity.this, "Phone Number doesn't exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAnswers()
    {
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();
        if(question1.equals("")||question2.equals(""))
        {
            Toast.makeText(ResetPasswordActivity.this,"Please Enter both the answers",Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1",answer1);
            userdataMap.put("answer2", answer2);
            ref.child("SecurityQuestions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ResetPasswordActivity.this,"Security answers set successfully",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    }
                }
            });
        }
    }
    private void displayPreviousAnswers()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        ref.child("SecurityQuestions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists())
               {
                   String ans1=snapshot.child("answer1").getValue().toString();
                   String ans2=snapshot.child("answer2").getValue().toString();
                   question1.setText(ans1);
                   question2.setText(ans2);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
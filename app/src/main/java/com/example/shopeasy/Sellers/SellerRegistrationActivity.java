package com.example.shopeasy.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopeasy.Buyers.MainActivity;
import com.example.shopeasy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginButton,registerButton;
    private EditText nameInput,phoneInput,passwordInput,emailInput,addressInput;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth=FirebaseAuth.getInstance();
        sellerLoginButton=findViewById(R.id.seller_already_account_button);
        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        passwordInput=findViewById(R.id.seller_password);
        emailInput=findViewById(R.id.seller_email);
        addressInput=findViewById(R.id.seller_address);
        registerButton=findViewById(R.id.seller_register_button);
        loadingBar = new ProgressDialog(this);

        sellerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerRegistrationActivity.this,
                        SellerLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                registerSeller();
            }
        });
    }

    private void registerSeller() {
     final String name=nameInput.getText().toString();
     final String phone=phoneInput.getText().toString();
     final String email=emailInput.getText().toString();
     String password=passwordInput.getText().toString();
     final String address=addressInput.getText().toString();

     if(name.equals("")||phone.equals("")||email.equals("")||password.equals("")||address.equals(""))
     {
         Toast.makeText(this, "Please Enter all the fields", Toast.LENGTH_SHORT).show();
     }
     else
     {
         loadingBar.setTitle("Create seller Account");
         loadingBar.setMessage("Please wait, while we are checking the credentials.");
         loadingBar.setCanceledOnTouchOutside(false);
         loadingBar.show();
         mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful())
                 {
                     final DatabaseReference rootRef;
                     rootRef= FirebaseDatabase.getInstance().getReference();
                     String sid=mAuth.getCurrentUser().getUid();
                     HashMap<String,Object> sellerMap=new HashMap<>();
                     sellerMap.put("sid",sid);
                     sellerMap.put("phone",phone);
                     sellerMap.put("email",email);
                     sellerMap.put("address",address);
                     sellerMap.put("name",name);
                     rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             loadingBar.dismiss();
                             Toast.makeText(SellerRegistrationActivity.this, "Registered successfully,Welcome Seller!", Toast.LENGTH_LONG).show();
                             Intent intent = new Intent(SellerRegistrationActivity.this,
                                     SellerLoginActivity.class);
                             startActivity(intent);
                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             finish();
                         }
                     });
                 }
                 else
                 {
                     loadingBar.dismiss();
                     Toast.makeText(SellerRegistrationActivity.this, "Unable to register,please try again", Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }
    }
}
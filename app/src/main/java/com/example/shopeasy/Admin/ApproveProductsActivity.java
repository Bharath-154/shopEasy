package com.example.shopeasy.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopeasy.Buyers.HomeActivity;
import com.example.shopeasy.Interface.ItemClickListener;
import com.example.shopeasy.Model.Products;
import com.example.shopeasy.R;
import com.example.shopeasy.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ApproveProductsActivity extends AppCompatActivity {
private RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
private DatabaseReference UnapprovedProductsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_products);
  recyclerView=findViewById(R.id.admin_products_check_list);
  recyclerView.setHasFixedSize(true);
  layoutManager=new LinearLayoutManager(this);
  recyclerView.setLayoutManager(layoutManager);
 UnapprovedProductsReference=FirebaseDatabase.getInstance().getReference().child("Products");

    }

    @Override
    protected void onStart() {
       super.onStart();
        FirebaseRecyclerOptions<Products> options=new
                FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(UnapprovedProductsReference.orderByChild("productState").equalTo("Not Approved"),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
            {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Price = " + model.getPrice() + "Rs");
                Picasso.get().load(model.getImage()).into(holder.imageView);

                final Products itemclick=model;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String productID=itemclick.getPid();
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ApproveProductsActivity.this);
                        builder.setTitle("Are you sure that you want to approve this product?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 1) {
                                    dialogInterface.dismiss();
                                } else {
                                    changeProductState(productID);
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductState(String productID) {
        UnapprovedProductsReference.child(productID).child("productState").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ApproveProductsActivity.this, "Product approved,now avaialble to buy", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApproveProductsActivity.this, "An error occured, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
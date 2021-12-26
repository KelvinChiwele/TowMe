package com.techart.towme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.model.MainContext;
import com.techart.towme.model.Order;
import com.techart.towme.model.Profile;
import com.techart.towme.setup.LoginActivity;
import com.techart.towme.ui.OrderViewHolder;
import com.techart.towme.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {
    private Button btStartOrderProcess;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private MainContext mainContext;

    private RecyclerView rvOrder;
    private ImageView ivBackGround;
    private LinearLayout llLayout;
    private FloatingActionButton fabAddOrder;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private Profile profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        loadProfilePicture();

        btStartOrderProcess = findViewById(R.id.bt_start_order);

        rvOrder = findViewById(R.id.rv_order);
        llLayout = findViewById(R.id.ll_layout);
        ivBackGround = findViewById(R.id.iv_back_ground);
        fabAddOrder = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        };

        btStartOrderProcess.setOnClickListener(view -> {
            Intent orderActivity = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(orderActivity);

        });

        fabAddOrder.setOnClickListener(view -> {
            Intent orderActivity = new Intent(MainActivity.this, OrderActivity.class);
            startActivity(orderActivity);
            finish();
        });

        rvOrder.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvOrder.setLayoutManager(linearLayoutManager);
        showOrders();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        firebaseRecyclerAdapter.startListening();
        mainContext = MainContext.getInstance();
        mainContext.setContext(MainActivity.this);
    }


    private void loadProfilePicture() {
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profile = Profile.getInstance();
                Profile.setInstance(dataSnapshot.getValue(Profile.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showOrders() {
        FirebaseRecyclerOptions<Order> response = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(FireBaseUtils.mDatabaseOrder.orderByChild(Constants.USER_URL).equalTo(FireBaseUtils.getUiD()), Order.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(response) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_order, parent, false);
                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull final Order order) {
//                viewHolder.makePortionBold(model.getService()+ " " + model. + model.getPostTitle(), model.getUser());
                llLayout.setVisibility(View.GONE);
                ivBackGround.setVisibility(View.GONE);

                rvOrder.setVisibility(View.VISIBLE);

                viewHolder.tvTrip.setText(order.getService());
                viewHolder.tvStatus.setText(order.getStatus());
                viewHolder.tvAmount.setText("ZMW " + order.getAmountToPay());
                viewHolder.setIvImage(MainActivity.this, R.drawable.placeholder);
                if (order.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(order.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
            }
        };
        rvOrder.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }
}
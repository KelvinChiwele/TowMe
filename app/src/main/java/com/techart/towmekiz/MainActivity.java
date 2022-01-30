package com.techart.towmekiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.techart.towmekiz.constants.Constants;
import com.techart.towmekiz.constants.FireBaseUtils;
import com.techart.towmekiz.databinding.ActivityMainBinding;
import com.techart.towmekiz.enums.Status;
import com.techart.towmekiz.enums.UserType;
import com.techart.towmekiz.model.MainContext;
import com.techart.towmekiz.model.Order;
import com.techart.towmekiz.model.Profile;
import com.techart.towmekiz.model.User;
import com.techart.towmekiz.setup.LoginActivity;
import com.techart.towmekiz.ui.OrderViewHolder;
import com.techart.towmekiz.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private MainContext mainContext;

    private RecyclerView rvOrder;
    private TextView tvMessage;
    private ImageView ivBackGround;
    private LinearLayout llLayout;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private FloatingActionButton fabAddOrder;

    private Profile profile;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.appBarMain2.toolbar;

        Button btStartOrderProcess = findViewById(R.id.bt_start_order);

        rvOrder = findViewById(R.id.rv_order);
        tvMessage = findViewById(R.id.tv_message);
        llLayout = findViewById(R.id.ll_layout);
        ivBackGround = findViewById(R.id.iv_back_ground);
        fabAddOrder = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            } else {
                getUserDetails();
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

        setSupportActionBar(binding.appBarMain2.toolbar);

        setupDrawer(toolbar);
    }

    private void setupDrawer(Toolbar toolbar) {
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            View header = navigationView.getHeaderView(0);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            TextView tvUser = header.findViewById(R.id.tv_user);
            TextView tvEmail = header.findViewById(R.id.tv_email);
            tvUser.setText(FireBaseUtils.getAuthor());
            tvEmail.setText(FireBaseUtils.getEmail());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        if (firebaseRecyclerAdapter != null){
//        firebaseRecyclerAdapter.startListening();

//        }

        mainContext = MainContext.getInstance();
        mainContext.setContext(MainActivity.this);
    }

    private void getUserDetails() {
        FireBaseUtils.mDatabaseUsers.child(FireBaseUtils.getUiD()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                profile = Profile.getInstance();
                Profile.setInstance(dataSnapshot.getValue(Profile.class));
                Profile.setInstance(dataSnapshot.getValue(Profile.class));

                if (user != null && (UserType.GARAGE.toString()).equals(user.getUserType())) {
                    llLayout.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.GONE);
                    ivBackGround.setVisibility(View.GONE);
                    rvOrder.setVisibility(View.VISIBLE);
                    fabAddOrder.setVisibility(View.GONE);
                    showOrders(Constants.STATUS, Status.IN_PROGRESS.toString(), UserType.GARAGE);
                } else if (user != null && (UserType.MOTORIST.toString()).equals(user.getUserType())) {
                    llLayout.setVisibility(View.VISIBLE);
                    ivBackGround.setVisibility(View.VISIBLE);
                    rvOrder.setVisibility(View.GONE);
                    tvMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showOrders(String searchKey, String searchValue, UserType userType) {
        FirebaseRecyclerOptions<Order> response = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(FireBaseUtils.mDatabaseOrder.orderByChild(searchKey).equalTo(searchValue), Order.class)
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
                final String orderUrl = getRef(position).getKey();
                tvMessage.setVisibility(View.GONE);
                if (UserType.MOTORIST.equals(userType)) {
                    viewHolder.setBtAcceptVisibility(View.GONE);
                }
                viewHolder.tvTrip.setText(order.getService());
                viewHolder.tvStatus.setText(order.getStatus());
                viewHolder.tvAmount.setText("ZMW " + order.getAmountToPay());
                viewHolder.setIvImage(MainActivity.this, R.drawable.placeholder);
                if (order.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(order.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.btAccept.setOnClickListener(v -> {
                    updateOrder(orderUrl);
                });
            }
        };
        rvOrder.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
        firebaseRecyclerAdapter.startListening();
    }

    private void updateOrder(String orderUrl) {
        Map<String, Object> values = new HashMap<>();
        values.put(Constants.STATUS, Status.PENDING_DELIVERY);
        values.put(Constants.GARAGE_URL, FireBaseUtils.getUiD());
        FireBaseUtils.mDatabaseOrder.child(orderUrl).updateChildren(values);
    }

    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
                (dialog, button) -> {
                    if (button == DialogInterface.BUTTON_POSITIVE) {
                        FirebaseAuth.getInstance().signOut();
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            llLayout.setVisibility(View.VISIBLE);
            ivBackGround.setVisibility(View.VISIBLE);
            rvOrder.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
        }


        if (id == R.id.nav_orders) {
            llLayout.setVisibility(View.GONE);
            ivBackGround.setVisibility(View.GONE);
            rvOrder.setVisibility(View.VISIBLE);
            fabAddOrder.setVisibility(View.GONE);
            showOrders(Constants.USER_URL, FireBaseUtils.getUiD(), UserType.MOTORIST);
        }

        if (id == R.id.nav_log_out) {
            logOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
package com.techart.towme;

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
import com.techart.towme.constants.Constants;
import com.techart.towme.constants.FireBaseUtils;
import com.techart.towme.databinding.ActivityMain2Binding;
import com.techart.towme.model.MainContext;
import com.techart.towme.model.Order;
import com.techart.towme.model.Profile;
import com.techart.towme.setup.LoginActivity;
import com.techart.towme.ui.OrderViewHolder;
import com.techart.towme.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.appBarMain2.toolbar;

        btStartOrderProcess = findViewById(R.id.bt_start_order);

        rvOrder = findViewById(R.id.rv_order);
        llLayout = findViewById(R.id.ll_layout);
        ivBackGround = findViewById(R.id.iv_back_ground);
        fabAddOrder = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent loginIntent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(loginIntent);
            } else {
                getUserDetails();
            }
        };

        btStartOrderProcess.setOnClickListener(view -> {
            Intent orderActivity = new Intent(Main2Activity.this, OrderActivity.class);
            startActivity(orderActivity);

        });

        fabAddOrder.setOnClickListener(view -> {
            Intent orderActivity = new Intent(Main2Activity.this, OrderActivity.class);
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
        showOrders();
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
        firebaseRecyclerAdapter.startListening();
        mainContext = MainContext.getInstance();
        mainContext.setContext(Main2Activity.this);
    }


    private void getUserDetails() {
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
                viewHolder.setIvImage(Main2Activity.this, R.drawable.placeholder);
                if (order.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(order.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }
            }
        };
        rvOrder.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            FirebaseAuth.getInstance().signOut();
                        }
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

        if (id == R.id.nav_log_out) {
            logOut();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
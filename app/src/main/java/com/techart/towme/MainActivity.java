package com.techart.towme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.techart.towme.model.MainContext;
import com.techart.towme.setup.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private Button btStartOrderProcess;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private MainContext mainContext;

    private RecyclerView rvOrder;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        btStartOrderProcess = findViewById(R.id.bt_start_order);

        rvOrder = findViewById(R.id.rv_order);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        };

        btStartOrderProcess.setOnClickListener(view -> {
//            Intent orderActivity = new Intent(MainActivity.this, OrderActivity.class);
            Intent orderActivity = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(orderActivity);

        });

        rvOrder.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvOrder.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mainContext = MainContext.getInstance();
        mainContext.setContext(MainActivity.this);
    }


//    private void readersView() {
//        FirebaseRecyclerOptions<Order> response = new FirebaseRecyclerOptions.Builder<Order>()
//                .setQuery(FireBaseUtils.mDatabaseOrder.orderByChild(Constants.USER_URL).equalTo(FireBaseUtils.getUiD()), Order.class)
//                .build();
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(response) {
//            @NonNull
//            @Override
//            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.item_order, parent, false);
//                return new OrderViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull final Order model) {
////                viewHolder.makePortionBold(model.getService()+ " " + model. + model.getPostTitle(), model.getUser());
//                rvOrder.setVisibility(View.GONE);
//                rvOrder.setVisibility(View.VISIBLE);
//                if (model.getTimeCreated() != null) {
//                    String time = TimeUtils.timeElapsed( model.getTimeCreated());
//                    viewHolder.tvTime.setText(time);
//                }
//            }
//        };
//        rvOrder.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.notifyDataSetChanged();
//    }
}
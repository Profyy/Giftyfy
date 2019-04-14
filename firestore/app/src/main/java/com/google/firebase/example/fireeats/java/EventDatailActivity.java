package com.google.firebase.example.fireeats.java;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventDatailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot> {

    private static final String TAG = "EventDetail";

    public static final String KEY_EVENT_ID = "key_event_id";

    @BindView(R.id.eventImage)
    ImageView mImageView;

    @BindView(R.id.host)
    TextView mHostView;

    @BindView(R.id.eventCity)
    TextView mCityView;

    @BindView(R.id.eventType)
    TextView mTypeView;

    @BindView(R.id.restaurantPrice)
    TextView mPriceView;

    @BindView(R.id.viewEmptyRatings)
    ViewGroup mEmptyView;

    @BindView(R.id.recyclerRatings)
    RecyclerView mRatingsRecycler;

    private FirebaseFirestore mFirestore;
    private DocumentReference mEventRef;
    private ListenerRegistration mEventRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        // Get event ID from extras
        String eventId = getIntent().getExtras().getString(KEY_EVENT_ID);
        if (eventId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_EVENT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the event
        mEventRef = mFirestore.collection("events").document(eventId);
    }

    @Override
    public void onStart() {
        super.onStart();

        mEventRegistration = mEventRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mEventRegistration != null) {
            mEventRegistration.remove();
            mEventRegistration = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "event:onEvent", e);
            return;
        }

        onEventLoaded(snapshot.toObject(Event.class));
    }

    private void onEventLoaded(Event event) {
        mHostView.setText(event.getHost());
        mCityView.setText(event.getCity());
        mTypeView.setText(event.getType());

        // Background image
        Glide.with(mImageView.getContext())
                .load(event.getPhoto())
                .into(mImageView);
    }

    @OnClick(R.id.restaurantButtonBack)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

package com.example.team30finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.team30finalproject.messages.MessagesAdapter;
import com.example.team30finalproject.messages.MessagesList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private String email;
    private String mobile;
    private String name;
    private final List<MessagesList> messagesLists = new ArrayList<>();

    private RecyclerView messagesRecyclerView;
    // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://team30-final-project-default-rtdb.firebaseio.com/");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EMAIL = "email";
    private static final String ARG_MOBILE = "mobile";
    private static final String ARG_NAME = "name";

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2, String param3) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, param1);
        args.putString(ARG_MOBILE, param2);
        args.putString(ARG_NAME, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //messagesLists.add(new MessagesList("Tejas", "9850357664", "Hello","", 2));
        Log.d("oncreate", "called");
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        messagesRecyclerView = v.findViewById(R.id.messagesRecyclerView);

        final CircleImageView userProfilePic = v.findViewById(R.id.userProfilePic);

        Log.d("oncreateview", "called");
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            mobile = getArguments().getString(ARG_MOBILE);
            name = getArguments().getString(ARG_NAME);
        }

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profilePicUrl = snapshot.child("users").child(mobile).child("profile_pic").getValue(String.class);

                if(!profilePicUrl.isEmpty()){
                    Picasso.get().load(profilePicUrl).into(userProfilePic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesLists.clear();

                for(DataSnapshot dataSnapshot: snapshot.child("users").getChildren()){
                    final String getMobile = dataSnapshot.getKey();

                    if(!getMobile.equalsIgnoreCase(mobile)){
                        final String getName = dataSnapshot.child("name").getValue(String.class);
                        final String getProfilePic = dataSnapshot.child("profile_pic").getValue(String.class);
                        MessagesList messagesList = new MessagesList(getName, getMobile,"", getProfilePic, 0);

                        messagesLists.add(messagesList);
                    }
                }
                messagesRecyclerView.setAdapter(new MessagesAdapter(messagesLists, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        messagesLists.add(new MessagesList("ankita", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("tanish", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("yug", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("bhavisha", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("tutu", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("uk", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("sarita mummy", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("pankaj papa", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("briju chachu", "9850357664", "Hello","", 2));
//        messagesLists.add(new MessagesList("kalpana chachi", "9850357664", "Hello","", 2));

//        messagesRecyclerView.setAdapter(new MessagesAdapter(messagesLists, getContext()));
        return v;
    }
}
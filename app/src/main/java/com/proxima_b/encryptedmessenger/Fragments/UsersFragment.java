package com.proxima_b.encryptedmessenger.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proxima_b.encryptedmessenger.Adapter.OnItemClick;
import com.proxima_b.encryptedmessenger.Adapter.UserAdapter;
import com.proxima_b.encryptedmessenger.Model.User;
import com.proxima_b.encryptedmessenger.R;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    Typeface MR, MRR;
    FrameLayout frameLayout;
    TextView es_descp, es_title;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    static OnItemClick onItemClick;

    EditText search_users;
    RecyclerView rvContacts;
    List<String> listnum=new ArrayList<String>();

    public static UsersFragment newInstance(OnItemClick click) {
        onItemClick = click;
        Bundle args = new Bundle();

        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);


        MRR = Typeface.createFromAsset(getContext().getAssets(), "fonts/myriadregular.ttf");
        MR = Typeface.createFromAsset(getContext().getAssets(), "fonts/myriad.ttf");

        recyclerView = view.findViewById(R.id.recycler_view);
        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);
        frameLayout = view.findViewById(R.id.es_layout);
        es_descp = view.findViewById(R.id.es_descp);
        es_title = view.findViewById(R.id.es_title);

        es_descp.setTypeface(MR);
        es_title.setTypeface(MRR);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mUsers = new ArrayList<>();
        //getAllContacts();
        readUsers();
        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getId().equals(fuser.getUid())){
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(),onItemClick, mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (search_users.getText().toString().equals("")) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        mUsers.add(user);
                       /*if (user!= null && user.getId()!=null && firebaseUser!=null && !user.getId().equals(firebaseUser.getUid())) {
                            if (listnum.contains(user.getPhone())){
                                mUsers.add(user);}
                        }

                        */
                    }

                    if(mUsers.size()==0){
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        frameLayout.setVisibility(View.GONE);
                    }

                    userAdapter = new UserAdapter(getContext(), onItemClick,mUsers, false);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /*private void getAllContacts() {
        List<ContactVO> contactVOList = new ArrayList();
        ContactVO contactVO;

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new ContactVO();
                    contactVO.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber(phoneNumber);
                        listnum.add(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        @SuppressLint("Range") String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }
            }
            for (int i=0;i<listnum.size();i++){
                System.out.println(listnum.get(i)+"hii");
            }

        }
    }

     */
}

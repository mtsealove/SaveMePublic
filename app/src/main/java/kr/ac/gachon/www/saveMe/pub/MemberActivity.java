package kr.ac.gachon.www.saveMe.pub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MemberActivity extends AppCompatActivity {
    TextView titleTV, IDTV, nameTV, birthTV, addressTV, etcTV, friendTV;
    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        titleTV=findViewById(R.id.titleTV);
        IDTV=findViewById(R.id.IDTV);
        nameTV=findViewById(R.id.nameTV);
        birthTV=findViewById(R.id.birthTV);
        addressTV=findViewById(R.id.addressTV);
        etcTV=findViewById(R.id.etcTV);
        friendTV=findViewById(R.id.friendNumberTV);

        init();
    }

    private void init() {
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID"); //ID를 받아옴

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Members").child(ID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                IDTV.setText("ID: "+ID);
                String address=dataSnapshot.child("Address").getValue(String.class);
                addressTV.setText("주소: "+address);
                String birth=dataSnapshot.child("Birth").getValue(String.class);
                birthTV.setText("생년월일: "+birth);
                String etc=dataSnapshot.child("Etc").getValue(String.class);
                etcTV.setText("특이사항: "+etc);
                String name=dataSnapshot.child("Name").getValue(String.class);
                nameTV.setText("이름: "+name);
                titleTV.setText(name+"님의 회원정보");
                String friendNum="지인 연락처:\n";
                for(DataSnapshot snapshot:dataSnapshot.child("FriendNumbers").getChildren()) {
                    String number=snapshot.getValue(String.class);
                    friendNum+=number+"\n";
                }
                friendTV.setText(friendNum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void close(View view) {
        finish();
    }
}
package kr.ac.gachon.www.saveMe.pub;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText searchET;
    Button searchBtn;
    Spinner searchSP;
    ListView resultList;
    TextView noResultTV;
    String parameter, paraEng;;

    ArrayList<String> title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn=findViewById(R.id.searchBtn);
        searchET=findViewById(R.id.searchET);
        searchSP=findViewById(R.id.searchSP);
        resultList=findViewById(R.id.resultList);
        noResultTV=findViewById(R.id.noResultTV);

        searchET.addTextChangedListener(phoneNumberFormattingTextWatcher);

        searchSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                parameter=adapterView.getItemAtPosition(i).toString();
                searchET.setText("");
                if(parameter.equals("지인전화번호")) {
                    searchET.addTextChangedListener(phoneNumberFormattingTextWatcher);
                } else searchET.removeTextChangedListener(phoneNumberFormattingTextWatcher);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {  //엔터키로 검색
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i==EditorInfo.IME_ACTION_SEARCH) {
                    Search();
                }
                return false;
            }
        });

        setDefaultList();
    }

    PhoneNumberFormattingTextWatcher phoneNumberFormattingTextWatcher=new PhoneNumberFormattingTextWatcher();


    private void Search() { //검색
        final String input=searchET.getText().toString();

        switch (parameter) {
            case "이름":
                paraEng="Name";
                break;
            case "전화번호":
                paraEng="PhoneNumber";
                break;
            case "집주소":
                paraEng="Address";
                break;
            case "지인전화번호":
                paraEng="friend";
                break;
            case "특이사항":
                paraEng="Etc";
                break;
        }
        if(input.length()==0) { //입력 없을 시 모든 초기화
            setDefaultList();
        }
        else {
            title=new ArrayList<>();
            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference reference=database.getReference().child("Members");
            if(!paraEng.equals("friend"))   //지인 전화번호가 아니면
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                       String p=snapshot.child(paraEng).getValue(String.class);
                       String phone=snapshot.child("PhoneNumber").getValue(String.class);
                       if(p.contains(input))  { //검색 결과가 입력을 포함하면
                           title.add(phone);    //타이틀에 추가
                       }
                   }
                   ArrayAdapter adapter=new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, title);
                   resultList.setAdapter(adapter);
                    SetListClick(resultList, title);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            else    //지인 전화번호로 검색하면
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        String phone=snapshot.child("PhoneNumber").getValue(String.class);
                        for(int i=0; i<5; i++) {
                            String number=snapshot.child("FriendNumbers").child("friend"+i).getValue(String.class);
                            if(number!=null&&number.contains(input))
                                title.add(phone);
                        }
                    }
                    ArrayAdapter adapter=new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, title);
                    resultList.setAdapter(adapter);
                    SetListClick(resultList, title);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void SetListClick(ListView listView, final ArrayList<String> arrayList) {   //리스트 아이템 클릭 이벤트 설정
        if (arrayList.size()==0) { //검색 결과가 없으면
            noResultTV.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
            noResultTV.setVisibility(View.GONE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this, MemberActivity.class);
                    intent.putExtra("ID", arrayList.get(i));
                    startActivity(intent);
                }
            });
        }
    }

    private void setDefaultList() { //모든 정보를 표시하게 설정
        title=new ArrayList<>();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference().child("Members");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String number=snapshot.child("PhoneNumber").getValue(String.class);
                    title.add(number);  //전화번호를 기반으로 추가
                }
                ArrayAdapter adapter=new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, title);
                resultList.setAdapter(adapter);
                SetListClick(resultList, title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

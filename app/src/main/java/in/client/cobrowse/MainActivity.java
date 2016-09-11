package in.client.cobrowse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public DatabaseReference reference;
    EditText etName;
    EditText etNumber;

    boolean isUserTyping = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialise();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(false);
        database.setLogLevel(Logger.Level.DEBUG);

        reference = database.getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            signIn(auth, "tiwari9773@gmail.com", "firebase");
        } else {

            TextView textView = (TextView) findViewById(R.id.tv_status);
            textView.setText("Firebase Login Successful");
            addListener();
        }
    }

    private void initialise() {
        etName = (EditText) findViewById(R.id.et_name);
        etName.addTextChangedListener(new TextWatcher() {
                                          @Override
                                          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                          }

                                          @Override
                                          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                              if (isUserTyping) {
                                                  reference.child("name").setValue(charSequence.toString());
                                              }
                                          }

                                          @Override
                                          public void afterTextChanged(Editable editable) {
                                              isUserTyping = true;
                                          }
                                      }
        );

        etNumber = (EditText) findViewById(R.id.et_number);
        etNumber.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                                if (isUserTyping) {
                                                    reference.child("number").setValue(charSequence.toString());
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable editable) {
                                                isUserTyping = true;
                                            }
                                        }
        );

    }

    public void addListener() {


        //reference.child("name").addChildEventListener(valueEventListener);
        reference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String value = dataSnapshot.getValue().toString();
                    if (!etName.getText().toString().equalsIgnoreCase(value)) {
                        isUserTyping = false;
                        etName.setText(value);
                        Toast.makeText(MainActivity.this, "Name->" + value, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference.child("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    String value = dataSnapshot.getValue().toString();
                    if (!etNumber.getText().toString().equalsIgnoreCase(value)) {
                        isUserTyping = false;
                        etNumber.setText(value);
                        Toast.makeText(MainActivity.this, "Number->" + value, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signIn(final FirebaseAuth auth, final String email, final String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            TextView textView = (TextView) findViewById(R.id.tv_status);
                            textView.setText("Firebase Login Successful");

                            addListener();
                        } else {
                            register(auth, email, password);
                        }
                    }
                });

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Toast.makeText(MainActivity.this, "onAuthStateChanged Sign In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "onAuthStateChanged user Null", Toast.LENGTH_SHORT).show();
                }
            }
        };

        auth.addAuthStateListener(authListener);
    }

    private void register(final FirebaseAuth auth, final String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Register", Toast.LENGTH_SHORT).show();

                            signIn(auth, email, password);

                        } else {
                            Toast.makeText(MainActivity.this, "Register Fail", Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }


    /*Only Use When You Have Child Node*/
    ChildEventListener valueEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(MainActivity.this, "Testing", Toast.LENGTH_SHORT).show();

        }
    };


}

package com.example.realm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.realm.Model.Person;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {


    private Realm realm;
    TextView txtView;
    EditText txtName,txtAge,txtId,txtGender;
    Button btnAdd,btnView,btnDelete,btnModify,btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();

        txtId= findViewById(R.id.txtId);
        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnDelete = findViewById(R.id.btnDelete);
        txtView = findViewById(R.id.txtView);
        btnModify = findViewById(R.id.btnModify);
        btnSearch = findViewById(R.id.btnSearch);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= txtName.getText().toString();
                String age = txtAge.getText().toString();
                String gender = txtGender.getText().toString();
                addToDatabase(name,age,gender);
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshDatabase();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtId.getText().toString().length()>0) {
                    int id = Integer.parseInt(txtId.getText().toString());
                    String name = txtName.getText().toString();
                    String age = txtAge.getText().toString();
                    String gender = txtGender.getText().toString();

                    deleteFromDatabase(id, name, age, gender);
                } else {

                    if (txtId.getText().toString().isEmpty()) {
                        String name = txtName.getText().toString();
                        String age = txtAge.getText().toString();
                        String gender = txtGender.getText().toString();

                        deleteFromDatabaseNoId(name, age, gender);
                    }
                }
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = txtName.getText().toString();
                String age = txtAge.getText().toString();
                String gender = txtGender.getText().toString();

                if (txtId.getText().toString().isEmpty())  {
                    txtId.setError("Debe introducir un ID");
                } else if (name.isEmpty()) {
                    txtName.setError("Debe introducir un Nombre");
                } else if (age.isEmpty()) {
                    txtAge.setError("Debe introducir una Edad");
                } else if (gender.isEmpty()) {
                    txtGender.setError("Debe introducir un Genero");
                }else{
                    modifyDatabase( name, age, gender);
                }
            }
        });


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtId.getText().toString().length()>0) {
                    int id = Integer.parseInt(txtId.getText().toString());
                    String name = txtName.getText().toString();
                    String age = txtAge.getText().toString();
                    String gender = txtGender.getText().toString();

                    searchInDatabase(id, name, age, gender);
                } else {

                    if (txtId.getText().toString().isEmpty()) {
                        String name = txtName.getText().toString();
                        String age = txtAge.getText().toString();
                        String gender = txtGender.getText().toString();

                        searchInDatabaseNoId(name, age, gender);
                    }
                }
            }
        });
    }


    private void deleteFromDatabase(final int id, final String name, final String age, final String gender) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Person persons = realm.where(Person.class)
                        .equalTo("id", id)
                        .or()
                        .equalTo("name", name)
                        .or()
                        .equalTo("age", age)
                        .or()
                        .equalTo("gender", gender)
                        .findFirst();

                if(persons!= null) {
                    persons.deleteFromRealm();
                    refreshDatabase();
                }}
        });
    }

    private void deleteFromDatabaseNoId(final String name, final String age, final String gender) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Person persons = realm.where(Person.class)
                        .equalTo("name", name)
                        .or()
                        .equalTo("age", age)
                        .or()
                        .equalTo("gender", gender)
                        .findFirst();

                if(persons!= null) {
                    persons.deleteFromRealm();
                    refreshDatabase();
                }}
        });
    }

    private void modifyDatabase(final String name, final String age, final String gender) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int id = Integer.parseInt(String.valueOf(Integer.parseInt(txtId.getText().toString())));
                Person person = realm.where(Person.class)
                        .equalTo("id", id)
                        .findFirst();
                person.setName(name);
                person.setAge(age);
                person.setGender(gender);
                realm.insertOrUpdate(person);
                refreshDatabase();



            }
        });
    }

    private void refreshDatabase() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Person> result = realm.where(Person.class).findAllAsync();
        result.load();
        String output="";

        for(Person person : result){

            output+=person.toString();
        }
        txtView.setText(output);
    }

    private void searchInDatabase(int id, String name, String age, String gender) {
        Realm realm = Realm.getDefaultInstance();
        String output="";

        RealmResults<Person> result = realm.where(Person.class)

                .equalTo("id", id)
                .or()
                .equalTo("name", name)
                .or()
                .equalTo("age", age)
                .or()
                .equalTo("gender", gender)
                .findAll();

        for(Person person : result){

            output+=person.toString();
        }
        txtView.setText(output);
    }

    private void searchInDatabaseNoId(String name, String age, String gender) {
        Realm realm = Realm.getDefaultInstance();
        String output="";

        RealmResults<Person> result = realm.where(Person.class)

                .equalTo("name", name)
                .or()
                .equalTo("age", age)
                .or()
                .equalTo("gender", gender)
                .findAll();

        for(Person person : result){

            output+=person.toString();
        }
        txtView.setText(output);
    }


    private void addToDatabase(final String name, final String age, final String gender) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final int index= calculateId();
                Person person = realm.createObject(Person.class,index);
                person.setName(name);
                person.setAge(age);
                person.setGender(gender);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Transaction was a success.
                Log.v("Success","--------------OK---------------");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                // Transaction failed and was automatically canceled.
                Log.e("Failed", error.getMessage());
            }
        });

    }

    private final static int calculateId(){

        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Person.class).max("id");

        int nextId;
        if (currentIdNum == null){
            nextId = 0;
        }else {
            nextId = currentIdNum.intValue()+1;
        }
        return nextId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}


package com.techkrunch.techkrunch_phpmysql;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.text.TextUtils;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import android.os.AsyncTask;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    EditText StudentName, StudentPhoneNumber, StudentClass;
    Button RegisterStudent, ShowStudents;
    String StudentNameHolder, StudentPhoneHolder, StudentClassHolder;
    Boolean CheckEditText ;
    ProgressDialog progressDialog;
    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    String HttpURL = "http://192.168.0.163/ianphp/StudentRegister.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StudentName = (EditText)findViewById(R.id.editName);
        StudentPhoneNumber = (EditText)findViewById(R.id.editPhoneNumber);
        StudentClass = (EditText)findViewById(R.id.editClass);

        RegisterStudent = (Button)findViewById(R.id.buttonSubmit);
        ShowStudents = (Button)findViewById(R.id.buttonShow);

        RegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checking whether EditText is Empty or Not
                CheckEditTextIsEmptyOrNot();

                if(CheckEditText){

                    // If EditText is not empty and CheckEditText = True then this block will execute.

                    StudentRegistration(StudentNameHolder,StudentPhoneHolder, StudentClassHolder);

                }
                else {

                    // If EditText is empty then this block will execute .
                    Toast.makeText(MainActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();

                }

            }
        });

        ShowStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(getApplicationContext(),ShowAllStudentsActivity.class);

                startActivity(intent);

            }
        });
    }

    public void StudentRegistration(final String S_Name, final String S_Phone, final String S_Class){

        class StudentRegistrationClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                Toast.makeText(MainActivity.this,httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("StudentName",params[0]);

                hashMap.put("StudentPhone",params[1]);

                hashMap.put("StudentClass",params[2]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        StudentRegistrationClass studentRegistrationClass = new StudentRegistrationClass();

        studentRegistrationClass.execute(S_Name,S_Phone,S_Class);
    }


    public void CheckEditTextIsEmptyOrNot(){

        StudentNameHolder = StudentName.getText().toString();
        StudentPhoneHolder = StudentPhoneNumber.getText().toString();
        StudentClassHolder = StudentClass.getText().toString();

        if(TextUtils.isEmpty(StudentNameHolder) || TextUtils.isEmpty(StudentPhoneHolder) || TextUtils.isEmpty(StudentClassHolder))
        {

            CheckEditText = false;

        }
        else {

            CheckEditText = true ;
        }

    }
}
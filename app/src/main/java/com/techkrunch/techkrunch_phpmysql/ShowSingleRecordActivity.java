package com.techkrunch.techkrunch_phpmysql;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class ShowSingleRecordActivity extends AppCompatActivity {
    HttpParse httpParse = new HttpParse();
    ProgressDialog pDialog;

    // Http Url For Filter Student Data from Id Sent from previous activity.
    String HttpURL = "http://192.168.0.163/ianphp/FilterStudentData.php";

    // Http URL for delete Already Open Student Record.
    String HttpUrlDeleteRecord = "http://192.168.0.163/ianphp/DeleteStudent.php";

    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    TextView NAME,PHONE_NUMBER,CLASS;
    String NameHolder, NumberHolder, ClassHolder;
    Button UpdateButton, DeleteButton;
    String TempItem;
    ProgressDialog progressDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_record);
        NAME = (TextView)findViewById(R.id.textName);
        PHONE_NUMBER = (TextView)findViewById(R.id.textPhone);
        CLASS = (TextView)findViewById(R.id.textClass);

        UpdateButton = (Button)findViewById(R.id.buttonUpdate);
        DeleteButton = (Button)findViewById(R.id.buttonDelete);

        //Receiving the ListView Clicked item value send by previous activity.
        TempItem = getIntent().getStringExtra("ListViewValue");

        //Calling method to filter Student Record and open selected record.
        HttpWebCall(TempItem);


        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ShowSingleRecordActivity.this,UpdateActivity.class);

                // Sending Student Id, Name, Number and Class to next UpdateActivity.
                intent.putExtra("Id", TempItem);
                intent.putExtra("Name", NameHolder);
                intent.putExtra("Number", NumberHolder);
                intent.putExtra("Class", ClassHolder);

                startActivity(intent);

                // Finishing current activity after opening next activity.
                finish();

            }
        });

        // Add Click listener on Delete button.
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling Student delete method to delete current record using Student ID.
                StudentDelete(TempItem);

            }
        });

    }

    // Method to Delete Student Record
    public void StudentDelete(final String StudentID) {

        class StudentDeleteClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog2 = ProgressDialog.show(ShowSingleRecordActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog2.dismiss();

                Toast.makeText(ShowSingleRecordActivity.this, httpResponseMsg.toString(), Toast.LENGTH_LONG).show();

                finish();

            }

            @Override
            protected String doInBackground(String... params) {

                // Sending STUDENT id.
                hashMap.put("StudentID", params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpUrlDeleteRecord);

                return finalResult;
            }
        }

        StudentDeleteClass studentDeleteClass = new StudentDeleteClass();

        studentDeleteClass.execute(StudentID);
    }


    //Method to show current record Current Selected Record
    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog = ProgressDialog.show(ShowSingleRecordActivity.this,"Loading Data",null,true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponse(ShowSingleRecordActivity.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("StudentID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            jsonObject = jsonArray.getJSONObject(i);

                            // Storing Student Name, Phone Number, Class into Variables.
                            NameHolder = jsonObject.getString("name").toString() ;
                            NumberHolder = jsonObject.getString("phone_number").toString() ;
                            ClassHolder = jsonObject.getString("class").toString() ;

                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            // Setting Student Name, Phone Number, Class into TextView after done all process .
            NAME.setText(NameHolder);
            PHONE_NUMBER.setText(NumberHolder);
            CLASS.setText(ClassHolder);

        }
    }

}
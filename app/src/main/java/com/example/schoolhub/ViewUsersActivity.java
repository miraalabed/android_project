package com.example.schoolhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersActivity extends AppCompatActivity {

    SearchView searchView;
    Spinner spinnerFilter;
    ListView listView;
    ArrayAdapter<User> adapter;
    List<User> userList = new ArrayList<>();
    RequestQueue requestQueue;

    String serverUrl = "http://10.0.2.2/api/get_users.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        listView = findViewById(R.id.listViewUsers);
        ImageView btnHome = findViewById(R.id.home);
        ImageView btnProfile = findViewById(R.id.profile);
        ImageView btnLogout = findViewById(R.id.logout);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });


        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logout success", Toast.LENGTH_LONG).show();

            finish();
        });

        requestQueue = Volley.newRequestQueue(this);

        String[] roles = {"All", "Teacher", "Student"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);



        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchUsers();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                fetchUsers();
                return true;
            }
        });

        fetchUsers();
    }

    private void fetchUsers() {
        String search = searchView.getQuery().toString();
        String role = spinnerFilter.getSelectedItem().toString();

        String url = serverUrl + "?search=" + Uri.encode(search) + "&role=" + Uri.encode(role);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.d("SERVER_RESPONSE", response);

                JSONArray array = new JSONArray(response);
                userList.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String name = obj.getString("name");
                    String roleFromServer = obj.getString("role");

                    if (roleFromServer.equalsIgnoreCase("Teacher") || roleFromServer.equalsIgnoreCase("Student")) {
                        userList.add(new User(name, roleFromServer));
                    }
                }

                java.util.Collections.shuffle(userList);

                adapter = new ArrayAdapter<User>(this, R.layout.user_item, userList) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View itemView = getLayoutInflater().inflate(R.layout.user_item, parent, false);
                        TextView name = itemView.findViewById(R.id.textViewFullName);
                        TextView role = itemView.findViewById(R.id.textViewRole);

                        User user = getItem(position);
                        name.setText(user.getFullName());
                        role.setText(user.getRole());

                        return itemView;
                    }
                };

                listView.setAdapter(adapter);

            } catch (JSONException e) {
                Toast.makeText(this, "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, error -> {
            Toast.makeText(this, "Network Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });

        requestQueue.add(request);
    }

}

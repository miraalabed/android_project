package com.example.schoolhub;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NewsAdapter adapter;
    List<News> newsList;
    ProgressBar progressBar;
    TextView emptyText;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        recyclerView = findViewById(R.id.recycler_news);
        progressBar = findViewById(R.id.progress_bar);
        emptyText = findViewById(R.id.text_empty);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        newsList = new ArrayList<>();
        adapter = new NewsAdapter(this, newsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadNews();
    }
    private void loadNews() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        newsList.clear();
        adapter.notifyDataSetChanged();

        String url = "http://10.0.2.2/api/get_news.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("status").equals("success")) {
                            JSONArray array = json.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                News news = new News(
                                        obj.getString("title"),
                                        obj.getString("content"),
                                        obj.getString("date")
                                );
                                newsList.add(news);
                            }

                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);

                            if (newsList.isEmpty()) {
                                emptyText.setVisibility(View.VISIBLE);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("No news found.");
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }


}

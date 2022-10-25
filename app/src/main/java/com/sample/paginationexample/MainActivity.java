package com.sample.paginationexample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final int DATA_COUNT = 10000;
    public final int PER_PAGE = 25;

    RecyclerView recyclerView;
    AdapterRecyclerView adapter;
    LinkedList<String> data = new LinkedList<>();

    LinkedList<String> masterData = new LinkedList<>();
    int currentPage = 0;

    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        populateMasterData();
        populateData();
        initAdapter();
        initScrollListener();
    }

    private void populateMasterData() {
        int i = 0;
        while (i < DATA_COUNT) {
            masterData.add("Item " + (i+1));
            i++;
        }
    }

    private void populateData() {
        int startIndex = currentPage * PER_PAGE;
        int endIndex = startIndex + PER_PAGE;

        List<String> list = masterData.subList(startIndex, endIndex);
        data.addAll(list);
    }

    private void initAdapter() {
        adapter = new AdapterRecyclerView(data);
        recyclerView.setAdapter(adapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null) {
                        int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == data.size() - 1) {
                            if (lastCompletelyVisibleItemPosition == DATA_COUNT) {
                                Toast.makeText(MainActivity.this, "End of List", Toast.LENGTH_SHORT).show();
                            } else {
                                loadMore();
                                isLoading = true;
                            }
                        }
                    }
                }
            }

        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMore() {
        if (data.size() == DATA_COUNT) {
            return;
        }
        data.add(null);
        recyclerView.post(() -> adapter.notifyItemInserted(data.size() - 1));

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            data.remove(data.size() - 1);
            int scrollPosition = data.size();
            recyclerView.post(() -> adapter.notifyItemRemoved(scrollPosition));

            currentPage++;
            populateData();

            recyclerView.post(() -> adapter.notifyDataSetChanged());
            isLoading = false;
        }, 1000);

    }
}
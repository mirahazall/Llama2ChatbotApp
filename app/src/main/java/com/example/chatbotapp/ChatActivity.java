package com.example.chatbotapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private EditText editTextUserInput;
    private ImageButton sendButton;
    private RecyclerView chatMessagesRecyclerView;
    private ChatAdapter adapter;
    List<ChatEntry> chatHistory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editTextUserInput = findViewById(R.id.editTextUserInput);
        chatMessagesRecyclerView = findViewById(R.id.chatMessagesRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatMessagesRecyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter
        adapter = new ChatAdapter(this, chatHistory);
        // Set the adapter to the RecyclerView
        chatMessagesRecyclerView.setAdapter(adapter);

        sendButton = findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editTextUserInput.getText().toString().trim();
                if (!TextUtils.isEmpty(userInput)) {
                    sendMessageToApi(userInput);

                    editTextUserInput.setText(""); // Clear the input field
                }
            }
        });
    }

    private void sendMessageToApi(String userMessage) {
        ApiRequest request = new ApiRequest(userMessage, chatHistory);
        ApiService apiService = ApiClient.getClient();
        Call<ApiResponse> call = apiService.sendMessage(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    String message = apiResponse.getMessage();
                    chatHistory.add(new ChatEntry(userMessage, message));
                    adapter.notifyDataSetChanged();
                } else {

                    try {
                        String errorBody = response.errorBody().string();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Failed to send message. Please try again later.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }




}
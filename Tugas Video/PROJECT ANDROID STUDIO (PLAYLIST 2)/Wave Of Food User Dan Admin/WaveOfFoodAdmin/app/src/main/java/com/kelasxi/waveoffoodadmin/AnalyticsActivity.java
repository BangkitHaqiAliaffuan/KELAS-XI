package com.kelasxi.waveoffoodadmin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AnalyticsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Analytics & Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        setupPlaceholder();
    }
    
    private void setupPlaceholder() {
        TextView tvTitle = findViewById(R.id.tv_placeholder_title);
        TextView tvDescription = findViewById(R.id.tv_placeholder_description);
        
        tvTitle.setText("Analytics & Reports");
        tvDescription.setText("This feature will provide:\n\n" +
                "• Daily/Weekly/Monthly sales charts\n" +
                "• Popular food items analysis\n" +
                "• Revenue trends and forecasting\n" +
                "• Customer behavior insights\n" +
                "• Order pattern analysis\n" +
                "• Performance metrics\n" +
                "• Export reports (PDF/Excel)\n\n" +
                "Coming soon with advanced charts and insights!");
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

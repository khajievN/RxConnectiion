package nizomjon.rxconnectionstatusview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import nizomjon.rxconnection.ConnectionManager;
import nizomjon.rxconnection.StatusView;

public class MainActivity extends AppCompatActivity implements StatusView.TimerListener {

    StatusView statusView;
    TextView networkStatus;

    ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusView = (StatusView) findViewById(R.id.status);
        networkStatus = (TextView) findViewById(R.id.networkStatus);

        connectionManager = new ConnectionManager.Builder()
                .setContext(this)
                .setStatusView(statusView)
                .build();
        statusView.setOnRetryClickListener(retryClickListener);
        statusView.setOnTimeChangeListener(this);

    }

    View.OnClickListener retryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            statusView.cancel();
            if (!connectionManager.hasNetwork) {
                statusView.setStartCount(2);
                statusView.start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void timeOnChanged(boolean isFinished) {
            statusView.continueTimer();
    }

    @Override
    public void connectedToNetwork() {

        // Do some tasks
        Toast.makeText(this, "do some tasks", Toast.LENGTH_SHORT).show();
    }
}

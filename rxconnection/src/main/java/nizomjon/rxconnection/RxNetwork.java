package nizomjon.rxconnection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Nizomjon on 03/04/2017.
 */

public class RxNetwork {
    private RxNetwork() {
        // No instances
    }

    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return null != activeNetwork && activeNetwork.isConnected();

    }

    public static Observable<Boolean> stream(Context context) {
        final Context applicationContext = context.getApplicationContext();
        final IntentFilter action = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        return ContentObservable.fromBroadcast(context, action)
                // To get initial connectivity status
                .startWith((Intent) null)
                .map(new Func1<Intent, Boolean>() {
                    @Override public Boolean call(Intent ignored) {
                        return getConnectivityStatus(applicationContext);
                    }
                }).distinctUntilChanged();
    }
}
package nizomjon.rxconnection;

import android.content.Context;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Nizomjon on 14/04/2017.
 */

public class ConnectionManager {

    Context context;
    StatusView statusView;
    public boolean hasNetwork;

    public ConnectionManager(Context context, StatusView statusView) {
        this.context = context;
        this.statusView = statusView;
        initRxNetwork();
    }


    public static class Builder {
        private Context context;
        private StatusView statusView;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setStatusView(StatusView statusView) {
            this.statusView = statusView;
            return this;
        }

        public ConnectionManager build() {
            return new ConnectionManager(context, statusView);
        }
    }

    public void initRxNetwork() {
        RxNetwork.stream(context)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean hasInternet) {
                        hasNetwork = hasInternet;
                        if (!hasInternet) {
                            return hasInternet;
                        }
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                               @Override
                               public void call(Boolean isOnline) {
                                   statusView.setStatus(isOnline ? Status.COMPLETE : Status.LOADING);
                                   if (!isOnline) {
                                       statusView.setStartCount(2);
                                       statusView.start();
                                   }
                               }
                           }
                );
    }
}

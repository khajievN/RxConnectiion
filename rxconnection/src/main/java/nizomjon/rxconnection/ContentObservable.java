package nizomjon.rxconnection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import rx.Observable;


/**
 * Created by Nizomjon on 03/04/2017.
 */

public class ContentObservable {
    private ContentObservable(){
        throw  new AssertionError("No Instance");
    }

    public static Observable<Intent> fromBroadcast(Context context , IntentFilter intentFilter){
        return Observable.create(new OnSubscribeBroadcastRegister(context,intentFilter,null,null));
    }
}

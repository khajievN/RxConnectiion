package nizomjon.rxconnectionstatusview;

/**
 * Created by Nizomjon on 03/04/2017.
 */

public class NetworkState {
    private int statusId;
    private boolean isOnline;

    public NetworkState(int statusId, boolean isOnline) {
        this.statusId = statusId;
        this.isOnline = isOnline;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}

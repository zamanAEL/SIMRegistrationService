package co.celloscope.services.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
 
public class SyncService extends Service {

	private static final String TAG = SyncService.class.getSimpleName();
	private SyncManager mSyncManager;
	private Messenger svcMessenger;

	@Override
	public void onCreate() {
		mSyncManager = new SyncManager(this);
		svcMessenger = new Messenger(new ServiceHandler(this, mSyncManager));
	}

	@Override
	public IBinder onBind(Intent intent) {
		mSyncManager.initialize();
		Log.i(TAG, "Service bounded");
		return svcMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mSyncManager.release();
		Log.i(TAG, "Unbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		mSyncManager.destroy();
		Log.i(TAG, "Service Destroyed");
	}

}
package co.celloscope.services.sync;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

class ServiceHandler extends Handler {

	private static String TAG = ServiceHandler.class.getSimpleName();
	/**
	 * Messengers for different application that will communicate with the
	 * service
	 */
	private final ArrayList<Messenger> mClientMessengers;
	/**
	 * This object manages all synchronization operation
	 */
	private final SyncManager mSyncManager;
	private final Context context;

	/**
	 * Create a new service handler
	 * 
	 * @param mSyncManager
	 *            this object will manages all synchronization operation
	 */
	ServiceHandler(Context context, SyncManager mSyncManager) {
		super();
		this.context = context;
		this.mSyncManager = mSyncManager;
		this.mClientMessengers = new ArrayList<Messenger>();
	}

	/**
	 * Receive messages from client and take action accordingly
	 */
	@Override
	public void handleMessage(Message msg) {

		Bundle bundle = new Bundle();
		switch (msg.what) {
		case ServiceOperations.MSG_REGISTER_CLIENT:
			mClientMessengers.add(msg.replyTo);
			bundle.putString(context.getResources().getString(R.string.text),
					context.getResources()
							.getString(R.string.registered_client)
							+ " "
							+ msg.replyTo.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_REGISTER_CLIENT, bundle));
			break;
		case ServiceOperations.MSG_UNREGISTER_CLIENT:
			bundle.putString(
					context.getResources().getString(R.string.text),
					context.getResources().getString(
							R.string.unregistered_client)
							+ msg.replyTo.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_UNREGISTER_CLIENT, bundle));
			mClientMessengers.remove(msg.replyTo);
			break;
		case ServiceOperations.MSG_DO_OCR:
			String filePath = ((Bundle) msg.obj).getString("text");
			// mSyncManager.doOCR(filePath);
			bundle.putString(context.getResources().getString(R.string.text),
					"OCR request sent to " + mSyncManager.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_DO_OCR, bundle));
			break;
		case ServiceOperations.MSG_OCR_RESULT:
			bundle.putString(context.getResources().getString(R.string.text),
					"OCR text " + msg.obj.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_OCR_RESULT, bundle));
			break;
		default:
			super.handleMessage(msg);
		}
	}

	private void sendMessageToClients(Message message) {

		for (int i = mClientMessengers.size() - 1; i >= 0; i--) {
			try {
				mClientMessengers.get(i).send(message);
			} catch (RemoteException e) {
				mClientMessengers.remove(i);
				Log.e(TAG, e.getMessage());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}

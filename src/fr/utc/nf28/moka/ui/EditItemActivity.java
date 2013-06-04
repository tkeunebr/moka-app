package fr.utc.nf28.moka.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import fr.utc.nf28.moka.R;
import fr.utc.nf28.moka.data.MokaItem;
import fr.utc.nf28.moka.io.receiver.LockingReceiver;
import fr.utc.nf28.moka.io.receiver.MokaReceiver;
import fr.utc.nf28.moka.ui.base.MokaUpActivity;
import fr.utc.nf28.moka.util.CroutonUtils;
import fr.utc.nf28.moka.util.JadeUtils;

import static fr.utc.nf28.moka.util.LogUtils.makeLogTag;

public class EditItemActivity extends MokaUpActivity implements EditItemFragment.Callbacks, LockingReceiver.OnLockingListener {
	public static final String ARG_ITEM = "arg_item";
	public static final int RESULT_DELETE = RESULT_FIRST_USER + 1;
	private static final String TAG = makeLogTag(EditItemActivity.class);
	private final IntentFilter mIntentFilter = new IntentFilter(MokaReceiver.INTENT_FILTER_JADE_SERVER_RECEIVER);
	private MokaItem mSelectedItem;
	/**
	 * Broadcast receiver used to catch locking callback from SMA
	 */
	private LockingReceiver mLockingReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_item_activity);

		mLockingReceiver = new LockingReceiver(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Intent intent = getIntent();
		if (savedInstanceState == null && intent.hasExtra(ARG_ITEM)) {
			mSelectedItem = intent.getExtras().getParcelable(ARG_ITEM);
			if (mSelectedItem == null) {
				throw new IllegalStateException("Selected item cannot be null");
			}

			//send locking request to the SMA
			JadeUtils.getAndroidAgentInterface().lockItem(mSelectedItem.getId());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mLockingReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mLockingReceiver);
	}

	@Override
	public void onItemDeletion(MokaItem item) {
		JadeUtils.getAndroidAgentInterface().deleteItem(item.getId());
		setResult(EditItemActivity.RESULT_DELETE);
		finish();
	}

	@Override
	public void onSuccess() {
		Crouton.makeText(this, "Element locké pour vous ! ", Style.CONFIRM).show();
		resetUi();
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slow_fade_in, R.anim.slow_fade_out)
				.replace(android.R.id.content, EditItemFragment.newInstance(mSelectedItem))
				.commit();
	}

	@Override
	public void onAlreadyLocked(String lockerName) {
		//TODO display retry button
		Crouton.makeText(this, "Element déjà locké par " + lockerName, CroutonUtils.INFO_MOKA_STYLE).show();
		resetUi();
	}

	@Override
	public void onError() {
		//TODO display error or return ?
		Crouton.makeText(this, "locking error ", Style.ALERT).show();
		resetUi();
	}

	private void resetUi() {
		findViewById(R.id.progress).setVisibility(View.GONE);
	}
}
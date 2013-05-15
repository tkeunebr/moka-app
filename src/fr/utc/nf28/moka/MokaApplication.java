package fr.utc.nf28.moka;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import fr.utc.nf28.moka.agent.AndroidAgent;
import fr.utc.nf28.moka.agent.IAndroidAgent;
import fr.utc.nf28.moka.data.ComputerType;
import fr.utc.nf28.moka.data.MediaType;
import fr.utc.nf28.moka.data.MokaType;
import fr.utc.nf28.moka.data.TextType;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;

import java.util.HashMap;
import java.util.UUID;

public class MokaApplication extends Application {
	public static HashMap<String, MokaType> MOKA_TYPES;
	private static final String ANDROID_AGENT_NICKNAME = "AndroidAgent_"+ UUID.randomUUID().toString();

	private MicroRuntimeServiceBinder mMicroRuntimeServiceBinder;
	private Properties mAgentContainerProperties;

	@Override
	public void onCreate() {
		super.onCreate();

		MOKA_TYPES = new HashMap<String, MokaType>() {
			{
				put(MediaType.ImageType.KEY_TYPE, new MediaType.ImageType("Image", "Description d'une image"));
				put(MediaType.VideoType.KEY_TYPE, new MediaType.VideoType("Vidéo", "Description d'une vidéo"));
				put(MediaType.WebType.KEY_TYPE, new MediaType.WebType("Page web", "Description d'une page web"));
				put(TextType.PlainTextType.KEY_TYPE, new TextType.PlainTextType("Texte", "Description d'un texte"));
				put(TextType.ListType.KEY_TYPE, new TextType.ListType("Liste", "Description d'une liste"));
				put(TextType.PostItType.KEY_TYPE, new TextType.PostItType("Post-it", "Description d'un post-it"));
				put(ComputerType.UmlType.KEY_TYPE, new ComputerType.UmlType("Diagramme UML", "Description d'un diagramme UML"));
			}
		};

		mAgentContainerProperties = new Properties();
		mAgentContainerProperties.setProperty(Profile.JVM, Profile.ANDROID);
	}


	public void startJadePlatform(final String host) {
		startJadePlatform(host, Profile.DEFAULT_PORT);
	}

	public void startJadePlatform(final String host, final int port) {
		Log.i("vbarthel", "start jade platform");
		mAgentContainerProperties.setProperty(Profile.MAIN_HOST, host);
		mAgentContainerProperties.setProperty(Profile.MAIN_PORT, String.valueOf(port));
		bindMicroRuntimeService();
	}

	public IAndroidAgent getAndroidAgentInterface() {
		try {
			return MicroRuntime.getAgent(ANDROID_AGENT_NICKNAME).getO2AInterface(IAndroidAgent.class);
		} catch (ControllerException e) {
			e.printStackTrace();
			Log.i("vbarthel", "getAndroidAgentInterface failed");
			return null;
		}
	}

	private void bindMicroRuntimeService() {
		Log.i("vbarthel", "bind micro runtime");
		ServiceConnection serviceConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder service) {
				// Bind successful
				Log.i("vbarthel", "bind micro runtime success");
				mMicroRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
				startAgentContainer();
			};
			public void onServiceDisconnected(ComponentName className) {
				// Bind unsuccessful
				Log.i("vbarthel", "bind micro runtime fail");
				mMicroRuntimeServiceBinder = null;
			}
		};

		bindService(new Intent(getApplicationContext(), MicroRuntimeService.class),
				serviceConnection,
				Context.BIND_AUTO_CREATE);
	}



	private void startAgentContainer() {
		Log.i("vbarthel", "start agent container");
		mMicroRuntimeServiceBinder.startAgentContainer(mAgentContainerProperties,
				new RuntimeCallback<Void>() {
					@Override
					public void onSuccess(Void thisIsNull) {
						Log.i("vbarthel", "start agent container success");
						// Split container successfully started
						startAgent(ANDROID_AGENT_NICKNAME, AndroidAgent.class.getName(), null);
					}
					@Override
					public void onFailure(Throwable throwable) {
						Log.i("vbarthel", "start agent container fail");
						// Split container startup error
					}
				} );
	}

	private void startAgent(final String nickName, final String className, Object[] params) {
		Log.i("vbarthel", "start agent " + nickName);
		mMicroRuntimeServiceBinder.startAgent(nickName, className, params,
				new RuntimeCallback<Void>() {
					@Override
					public void onSuccess(Void aVoid) {
						//Agent successfully started
						Log.i("vbarthel", "start agent " + nickName + " success");
					}

					@Override
					public void onFailure(Throwable throwable) {
						//Agent startup error
						Log.e("vbarthel", "start agent " + nickName + " fail", throwable);
					}
				} );
	}
}

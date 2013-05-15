package fr.utc.nf28.moka.agent;

import android.util.Log;
import jade.core.Agent;


public class AndroidAgent extends Agent implements IAndroidAgent {

	@Override
	protected void setup() {
		super.setup();
		registerO2AInterface(IAndroidAgent.class, this);
	}

	@Override
	public void ping() {
		Log.i("vbarthel", "pong");
	}
}

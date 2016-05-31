package com.example.Example2;

import com.sap.ve.DVLCore;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity
{
	private DVLCore	m_core;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		m_core = new DVLCore(getApplicationContext());

		setContentView(R.layout.main);
	}

	@Override
	protected void onDestroy()
	{
		try {
			m_core.dispose();
		} finally {
			m_core = null;
			super.onDestroy();
		}
	}

	public DVLCore getCore()
	{
		return m_core;
	}
}

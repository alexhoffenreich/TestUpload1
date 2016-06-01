package com.example.Example2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.DVL.DVL;
import com.DVL.DVLNode;
import com.DVL.OnDVLNodeSelectListener;
import com.Documentation.Structure.Module;
import com.Documentation.Structure.ModuleLink;
import com.Documentation.Structure.Modules;
import com.sap.ve.DVLCore;
import com.sap.ve.DVLRenderer;
import com.sap.ve.DVLScene;
import com.sap.ve.SDVLNodeInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    NfcAdapter nfcAdapter;
    Modules modules;
    Module cur_module;
    LinearLayout buttons_layout;
    List<View> dynamic_buttons;
    private DVLCore m_core;
    private Surface dvl_surface;
    DVL dvl = DVL.getInstance();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_core = new DVLCore(getApplicationContext());
        setContentView(R.layout.main);
        dvl.setActivity(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        dvl_surface = (Surface) findViewById(R.id.dvl_surface);

        modules = new Modules(this);
        dynamic_buttons = new ArrayList<>();

        buttons_layout = (LinearLayout) findViewById(R.id.buttons_layout);

        Button show_all_button = (Button) findViewById(R.id.show_all_button);
        show_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvl.resetView();
                clearDynamicButtons();

            }
        });

        dvl.setOnSelectEvent(new OnDVLNodeSelectListener() {
            @Override
            public void onSelectionChanged(DVLCore core, DVLRenderer renderer, DVLScene scene, final List<SDVLNodeInfo> selected_nodes) {
                Toast.makeText(getApplicationContext(),
                        dvl.getNodeNames(selected_nodes.get(0).parentNodes).toString() + ", " + selected_nodes.get(0).nodeName, Toast.LENGTH_SHORT).show();
            }
        });

    }


    //// NFC HANDLING

    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                handleNFCMessage(readTextFromMessage((NdefMessage) parcelables[0]));
            } else {
                Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleNFCMessage(String msg) {
        try {
            String[] split_msg = msg.split("\\|");

            String module_name = split_msg[0].trim();
            String module_serial = split_msg[1].trim();
            Toast.makeText(this, module_name, Toast.LENGTH_LONG).show();
            showItem(module_name);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("handleNFCMessage", "Error reading NFC Tag");
        }

    }


    private String readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            return getTextFromNdefRecord(ndefRecord);
        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
            return "";
        }

    }

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }


    ///// END OF NFC HANDLING





    public void showItem(String item_name) {
        cur_module = modules.getModuleById(item_name);
        dvl.setView(cur_module.getDefaultView());
        dvl.clearIsolation();
        DVLNode node = dvl.getNodesByName(item_name).get(0);
        node.zoomTo(true,3.0f);

        clearDynamicButtons();
        for (int i = 0; i < cur_module.getLinks().size(); i++) {
            ModuleLink cur_link = cur_module.getLinks().get(i);
            Button btn = new Button(this);
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            btn.setId(i);
            btn.setText(cur_link.getTitle());
            dynamic_buttons.add(btn);
            buttons_layout.addView(btn);
        }
    }


    private void clearDynamicButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (View v : dynamic_buttons) {
                    buttons_layout.removeView(v);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        try {
            m_core.dispose();
        } finally {
            m_core = null;
            super.onDestroy();
        }
    }

    public DVLCore getCore() {
        return m_core;
    }





}

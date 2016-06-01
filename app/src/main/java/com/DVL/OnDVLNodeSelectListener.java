package com.DVL;

import com.sap.ve.DVLCore;
import com.sap.ve.DVLRenderer;
import com.sap.ve.DVLScene;
import com.sap.ve.SDVLNodeInfo;

import java.util.List;

/**
 * Created by alex-lenovi on 5/26/2016.
 */
public interface OnDVLNodeSelectListener {
    void onSelectionChanged(DVLCore core, DVLRenderer renderer, DVLScene scene, List<SDVLNodeInfo> selected_nodes);

}

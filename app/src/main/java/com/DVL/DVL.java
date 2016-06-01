package com.DVL;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.Documentation.Procedure.DVLCommand;
import com.sap.ve.DVLCore;
import com.sap.ve.DVLRenderer;
import com.sap.ve.DVLScene;
import com.sap.ve.DVLTypes;
import com.sap.ve.SDVLNodeIDsArrayInfo;
import com.sap.ve.SDVLNodeInfo;
import com.sap.ve.SDVLPartsListInfo;
import com.sap.ve.SDVLPartsListItem;
import com.sap.ve.SDVLProcedure;
import com.sap.ve.SDVLProceduresInfo;
import com.sap.ve.SDVLStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by ADSL on 25/05/2016.
 */
public class DVL {
    private static DVL ourInstance = new DVL();
    private Activity activity;
    private Context applicaion_context;
    private DVLScene scene;
    private DVLCore core;
    private DVLRenderer renderer;
    //private SDVLNodeIDsArrayInfo selected_nodes;
    private float fade_time;
    private boolean init_ok = false;
    private SDVLPartsListInfo m_partsListInfo;
    private Map<String,SDVLProcedure> portfolios;
    private Map<String,SDVLProcedure> procedures;
    private OnDVLNodeSelectListener dvl_node_select_listener;

    private HashMap<String, SDVLPartsListItem> parts;

    private DVL() {

    }

    public static DVL getInstance() {
        return ourInstance;
    }

    public static void triggerSceneStepEvent(int type, long stepId) {

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }



    public void init(DVLCore core) {
        //selected_nodes = new SDVLNodeIDsArrayInfo();
        if (core != null) {
            this.core = core;
            applicaion_context = core.getContext();
            if (core.GetRenderer() != null) {
                this.renderer = core.GetRenderer();
                if (renderer.GetAttachedScene() != null) {
                    this.scene = renderer.GetAttachedScene();
                    init_ok = true;
                    SDVLProceduresInfo m_proceduresInfo = new SDVLProceduresInfo();
                    scene.RetrieveProcedures(m_proceduresInfo);

                    procedures = new HashMap<>();
                    for (SDVLProcedure procedure: m_proceduresInfo.procedures){
                        procedures.put(procedure.name,procedure);
                    }

                    portfolios = new HashMap<>();
                    for (SDVLProcedure procedure: m_proceduresInfo.portfolios){
                        portfolios.put(procedure.name,procedure);
                    }

                    m_partsListInfo = new SDVLPartsListInfo();
                    scene.BuildPartsList(DVLTypes.DVLPARTSLIST.UNLIMITED_uMaxParts,
                            DVLTypes.DVLPARTSLIST.UNLIMITED_uMaxNodesInSinglePart,
                            DVLTypes.DVLPARTSLIST.UNLIMITED_uMaxPartNameLength,
                            DVLTypes.DVLPARTSLISTTYPE.ALL, DVLTypes.DVLPARTSLISTSORT.NAME_ASCENDING,
                            DVLTypes.DVLID_INVALID, "", m_partsListInfo);

                    parts = new HashMap<>();
                    for (SDVLPartsListItem item: m_partsListInfo.parts){
                        parts.put(item.partName,item);
                    }

                } else {
                    Log.e("DVL.init", "AttachedScene = null !!");
                }
            } else {
                Log.e("DVL.init", "Renderer = null !!");
            }

        } else {
            Log.e("DVL.init", "Core = null !!");
        }

        fade_time = 2f;
    }

    public DVLScene getScene() {
        return scene;
    }

    public DVLCore getCore() {
        return core;
    }

    public DVLRenderer getRenderer() {
        return renderer;
    }


 /*   public void selectNodes(String node_name) {
        if (init_ok) {
            scene.FindNodes(DVLTypes.DVLFINDNODETYPE.NODE_NAME, DVLTypes.DVLFINDNODEMODE.EQUAL_CASE_INSENSITIVE, node_name, selected_nodes);
        }
    }*/





    //HERE


    //public void clearSelection() {

        //selected_nodes.nodes.clear();
    //}

    public void setView(String view_name) {
        if (init_ok) {
            try {
                SDVLProceduresInfo m_proceduresInfo = new SDVLProceduresInfo();
                scene.RetrieveProcedures(m_proceduresInfo);
                List<SDVLStep> standard_views = portfolios.get("DefaultViews").steps;

                for (SDVLStep st : standard_views) {
                    if (st.name.equals(view_name)) {
                        scene.ActivateStep(st.id, true, false);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void resetView() {
        if (init_ok) {
            //clearSelection();
            clearIsolation();
            renderer.SetIsolatedNode(-1);
            renderer.ResetView();
        }
    }
    public void clearIsolation(){
        renderer.SetIsolatedNode(-1);
    }

    public void triggerSelectionChanged(long hScene, int numberOfSelectedNodes, final long idFirstSelectedNode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<SDVLNodeInfo> nodes = new ArrayList<>();
                //todo: change implementation to all selected nodes, not only first
                nodes.add(getNodeInfo(idFirstSelectedNode));
                if (dvl_node_select_listener != null) {

                    try {
                        dvl_node_select_listener.onSelectionChanged(core, renderer, scene, nodes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public SDVLNodeInfo getNodeInfo(long node_id) {

        return getNodeInfo(node_id, DVLTypes.DVLNODEINFO.NAME | DVLTypes.DVLNODEINFO.CHILDREN |
                DVLTypes.DVLNODEINFO.UNIQUEID | DVLTypes.DVLNODEINFO.OPACITY |
                DVLTypes.DVLNODEINFO.ASSETID | DVLTypes.DVLNODEINFO.HIGHLIGHT_COLOR | DVLTypes.DVLNODEINFO.URI |
                DVLTypes.DVLNODEINFO.FLAGS | DVLTypes.DVLNODEINFO.PARENTS);
    }

    public SDVLNodeInfo getNodeInfo(long node_id, int DVLNODEINFOFlag) {
        SDVLNodeInfo cur_node = new SDVLNodeInfo();
        scene.RetrieveNodeInfo(node_id, DVLNODEINFOFlag, cur_node);
        return cur_node;
    }

    public List<String> getNodeNames(ArrayList<Long> node_ids) {
        List<String> node_names = new ArrayList<>();
        for (long i : node_ids) {
            node_names.add(getNodeInfo(i, DVLTypes.DVLNODEINFO.NAME).nodeName);
        }
        return node_names;
    }

    public void setOnSelectEvent(OnDVLNodeSelectListener handler) {
        dvl_node_select_listener = handler;

    }



    public List<DVLNode> getNodesByName(String node_name){
        return getNodes(node_name, DVLTypes.DVLFINDNODETYPE.NODE_NAME);
    }
    private List<DVLNode> getNodes (String node_name, DVLTypes.DVLFINDNODETYPE search_type) {
        List<DVLNode> nodes = new ArrayList<>();
        SDVLNodeIDsArrayInfo nodes_info = new SDVLNodeIDsArrayInfo();
        DVLTypes.DVLRESULT res = scene.FindNodes(search_type , DVLTypes.DVLFINDNODEMODE.EQUAL_CASE_INSENSITIVE, node_name, nodes_info);
        if (res == DVLTypes.DVLRESULT.OK){
            for (int i=0; i< nodes_info.nodes.size(); i++){
                nodes.add(new DVLNode(nodes_info.nodes.get(i)));
            }
        }
        return nodes;
    }

    public void applyLabelsXML(String labels_xml) {
        try {

            scene.ExecuteDynamicLabels(labels_xml);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("applyLabelsXMLFromFile", e.getMessage());

        }

    }


    public void applyPaintXML(String paint_xml) {
        try {

            scene.Execute(DVLTypes.DVLEXECUTE.PAINTXML, paint_xml);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("applyPaintXML", e.getMessage());

        }
    }


    public void applyQuery (String query_string){
        try {
            scene.Execute(DVLTypes.DVLEXECUTE.QUERY,query_string);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setNodeFlags(Long node_id, int flags, boolean value){
        scene.ChangeNodeFlags(node_id,flags, value? DVLTypes.DVLFLAGOP.SET| DVLTypes.DVLFLAGOP.MODIFIER_RECURSIVE: DVLTypes.DVLFLAGOP.CLEAR| DVLTypes.DVLFLAGOP.MODIFIER_RECURSIVE );
    }

}

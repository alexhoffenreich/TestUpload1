package com.DVL;

import android.opengl.Matrix;

import com.sap.ve.DVLCore;
import com.sap.ve.DVLRenderer;
import com.sap.ve.DVLScene;
import com.sap.ve.DVLTypes;
import com.sap.ve.SDVLMatrix;
import com.sap.ve.SDVLMetadataNameValuePair;
import com.sap.ve.SDVLNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex-lenovi on 5/30/2016.
 */
public class DVLNode {
    private long id;
    private DVL dvl;
    private SDVLNodeInfo node_info;
    private Map<String, String> meta_data;
    private DVLCore core;
    private DVLRenderer renderer;
    private DVLScene scene;
    private SDVLMatrix initial_matrix;
    private SDVLMatrix current_matrix;

    public DVLNode(long id) {
        this.id = id;
        dvl = DVL.getInstance();
        core = dvl.getCore();
        renderer = dvl.getRenderer();
        scene = dvl.getScene();

        node_info = dvl.getNodeInfo(id);

        meta_data = new HashMap<>();
        ArrayList<SDVLMetadataNameValuePair> meta = new ArrayList<>();
        dvl.getScene().RetrieveMetadata(id, meta);
        for (SDVLMetadataNameValuePair m : meta) {
            meta_data.put(m.name, m.value);
        }

        initial_matrix = getMatrix();
        current_matrix = getMatrix();
    }

    public float getXSize(){
        return current_matrix.m[0];
    }
    public float getYSize(){
        return current_matrix.m[5];
    }
    public float getZSize(){
        return current_matrix.m[10];
    }

    public String getMetadata(String name) {
        if (meta_data.containsKey(name)) return meta_data.get(name);
        else return "[NOT FOUND]";
    }

    public String getName() {
        return node_info.nodeName;
    }
    public String getAssetID() {
        return node_info.assetID;
    }
    public int getHighlightColor() {
        return node_info.highlightColor;
    }

    public List<DVLNode> getChildNodes() {
        List<DVLNode> child_nodes = new ArrayList<>();
        for (Long i : node_info.childNodes) {
            child_nodes.add(new DVLNode(i));
        }
        return child_nodes;
    }

    public void move(float delta_x, float delta_y, float delta_z) {
        SDVLMatrix mat = getMatrix();
        Matrix.translateM(mat.m, 0, delta_x, delta_y, delta_z);
        setMatrix(mat);
        current_matrix = mat;
    }

    public void rotate(float angle, float delta_x, float delta_y, float delta_z) {
        SDVLMatrix mat = getMatrix();
        Matrix.rotateM(mat.m, 0, angle, delta_x, delta_y, delta_z);
        setMatrix(mat);
        current_matrix = mat;
    }

    public void scale(float angle, float x_scale, float y_scale, float z_scale) {
        SDVLMatrix mat = getMatrix();
        Matrix.scaleM(mat.m, 0, x_scale, y_scale, z_scale);
        setMatrix(mat);
        current_matrix = mat;
    }

    public void zoomTo(boolean isolate, float fade_time) {
        if (isolate) {
            renderer.ZoomTo(DVLTypes.DVLZOOMTO.NODE_SETISOLATION, id, fade_time);
        } else {
            renderer.ZoomTo(DVLTypes.DVLZOOMTO.NODE, id, fade_time);
        }
    }

    public SDVLMatrix getMatrix() {
        SDVLMatrix mat = new SDVLMatrix();
        dvl.getScene().GetNodeWorldMatrix(id, mat);
        return mat;
    }

    public void setMatrix(SDVLMatrix matrix) {
        dvl.getScene().SetNodeWorldMatrix(id, matrix);
    }

    public long getId() {
        return id;
    }

    public void select() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.SELECTED, true);
    }

    public void unSelect() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.SELECTED, false);
    }

    public void open() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.CLOSED, true);
    }

    public void close() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.CLOSED, false);
    }

    public void enableSelection() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.UNHITABLE, false);
    }

    public void disableSelection() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.UNHITABLE, true);
    }

    public void show() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.VISIBLE, true);
    }

    public void hide() {
        dvl.setNodeFlags(id, DVLTypes.DVLNODEFLAG.VISIBLE, false);
    }

    public void setHighLight(int color) {
        dvl.getScene().SetNodeHighlightColor(id, color);
    }

    public void clearHighLight() {
        dvl.getScene().SetNodeHighlightColor(id, 0);
    }

    public void setOpacity(float opacity) {
        dvl.getScene().SetNodeOpacity(id,opacity);

    }



}

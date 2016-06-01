package com.Documentation.Structure;

import android.content.Context;


import com.example.Example2.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class Modules {
    private Context context;
    private Document modules_xml;
    private Map<String,Module> module_list;
    public Modules(Context context) {
        this.context = context;
        module_list= new HashMap<>();
        loadXML();
        populateModules();
    }

    public void loadXML(){
        InputStream file_stream = this.context.getResources().openRawResource(R.raw.modules_index);
        //InputSource src = new InputSource(file_stream);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(false);

        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            modules_xml = builder.parse(file_stream);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void populateModules(){
        NodeList module_node_list = modules_xml.getElementsByTagName("module");
        for (int i=0; i<module_node_list.getLength();i++){
            Module new_module = new Module((Element)module_node_list.item(i));
            module_list.put(new_module.getId(), new_module);
        }
    }
    public Module getModuleById (String id){
        return module_list.get(id);
    }

}

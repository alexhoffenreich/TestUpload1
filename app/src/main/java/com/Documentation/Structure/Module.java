package com.Documentation.Structure;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class Module {
    private List<ModuleLink> links;
    private String id;
    private String default_view;

    private Element xml_element;

    public Module(Element xml_element) {
        this.xml_element = xml_element;
        id = xml_element.getAttribute("id");
        links = new ArrayList<>();
        default_view = xml_element.getAttribute("default-view");

        NodeList link_list = null;
        try {
            link_list = ((Element)xml_element.getElementsByTagName("links").item(0)).getElementsByTagName("link");
            for (int i=0; i<link_list.getLength(); i++){
                ModuleLink new_link = new ModuleLink((Element)link_list.item(i));
                links.add(new_link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getDefaultView() {
        return default_view;
    }

    public String getId() {
        return id;
    }

    public List<ModuleLink> getLinks(){
        return links;
    }

}

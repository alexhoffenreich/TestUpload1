package com.Documentation.Structure;

import org.w3c.dom.Element;

/**
 * Created by alex-lenovi on 5/24/2016.
 */
public class ModuleLink {
    private String type;
    private String file_ref;
    private String title;
    private String details;
    private Element link_element;

    public ModuleLink(Element link_element) {
        this.link_element = link_element;
        this.type = link_element.getAttribute("type");
        this.file_ref = link_element.getAttribute("file_ref");
        try{

            this.title = link_element.getElementsByTagName("title").item(0).getTextContent();
            this.details = link_element.getElementsByTagName("details").item(0).getTextContent();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getType() {
        return type;
    }

    public String getFile_ref() {
        return file_ref;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }



}

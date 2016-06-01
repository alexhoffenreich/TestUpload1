package com.Documentation.Procedure;

import android.content.Context;

import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by ADSL on 26/05/2016.
 */
public class Procedures {
    private Document procedure_xml;
    private Context context;

    public Procedures(Context context, String xml_file_name) {
        this.context = context;
        loadXML(xml_file_name);
    }

    public void loadXML(String xml_file_name) {
        InputStream file_stream = context.getResources().openRawResource(
                context.getResources().getIdentifier(xml_file_name,
                        "raw", context.getPackageName()));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setValidating(true);

        DocumentBuilder builder = null;

        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            procedure_xml = builder.parse(file_stream);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

package com.ycya.weixin.util.toXmlCdata;

import java.io.Writer;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class MyXppDriver extends XppDriver{
	public HierarchicalStreamWriter createWriter(Writer out){

        return new MyPrettyPrintWriter(out);

    }

}

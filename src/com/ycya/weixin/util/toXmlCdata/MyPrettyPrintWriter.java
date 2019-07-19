package com.ycya.weixin.util.toXmlCdata;

import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.PrintWriter;
import java.io.Writer;

public class MyPrettyPrintWriter implements ExtendedHierarchicalStreamWriter {
    private final QuickWriter writer;
    private final FastStack elementStack = new FastStack(16);
    private final char[] lineIndenter;
    private boolean tagInProgress;
    private int depth;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;
    private static final char[] AMP = "&".toCharArray();

    private static final char[] LT = "<".toCharArray();
    private static final char[] GT = ">".toCharArray();
    private static final char[] SLASH_R = " ".toCharArray();
    private static final char[] QUOT = "'".toCharArray();
    private static final char[] APOS = "'".toCharArray();
    private static final char[] CLOSE = "</".toCharArray();

    public MyPrettyPrintWriter(Writer writer, char[] lineIndenter) {
        this.writer = new QuickWriter(writer);
        this.lineIndenter = lineIndenter;
    }
    public MyPrettyPrintWriter(Writer writer, String lineIndenter) {
        this(writer,lineIndenter.toCharArray());
    }
    public MyPrettyPrintWriter(PrintWriter writer) {
        this(writer, new char[] { ' ', ' ' });
    }
    public MyPrettyPrintWriter(Writer writer) {
        this(new PrintWriter(writer));
    }

    @Override
    public void addAttribute(String key, String value) {
        writer.write(' ');
        writer.write(key);
        writer.write('=');
        writer.write('\"');
        writeAttributeValue(writer, value);
        writer.write('\"');
    }
    protected void writeAttributeValue(QuickWriter writer, String text) {
        int length = text.length();
        for (int i = 0; i < length;i++) {
            char c = text.charAt(i);
            switch (c) {
                case'&':
                    this.writer.write(AMP);
                    break;
                case'<':
                    this.writer.write(LT);
                    break;
                case'>':

                    this.writer.write(GT);

                    break;

                case'"':

                    this.writer.write(QUOT);

                    break;

                case'\'':

                    this.writer.write(APOS);

                    break;

                case'\r':

                    this.writer.write(SLASH_R);

                    break;

                default:

                    this.writer.write(c);

            }

        }

    }

    @Override
    public void endNode() {

        depth--;

        if (tagIsEmpty) {

            writer.write('/');

            readyForNewLine = false;

            finishTag();

            elementStack.popSilently();

        }else{

            finishTag();

            writer.write(CLOSE);

            writer.write((String) elementStack.pop());

            writer.write('>');

        }

        readyForNewLine = true;

        if (depth == 0) {

            writer.flush();

        }

    }

    @Override
    public void setValue(String text) {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();
        writeText(writer, text);
    }

    protected void writeText(QuickWriter writer, String text) {
        int length = text.length();
        String CDATAPrefix = "<![CDATA[";
        if(!text.startsWith(CDATAPrefix)) {
            for (int i = 0; i < length;i++) {
                char c = text.charAt(i);
                switch (c) {
                    case'&':
                        this.writer.write(AMP);
                        break;
                    case'<':
                        this.writer.write(LT);
                        break;
                    case'>':
                        this.writer.write(GT);
                        break;
                    case'"':
                        this.writer.write(QUOT);
                        break;
                    case'\'':
                        this.writer.write(APOS);
                        break;
                    case'\r':
                        this.writer.write(SLASH_R);
                        break;
                    default:
                        this.writer.write(c);
                }
            }
        }
        else {
            for (int i = 0; i < length;i++) {
                char c = text.charAt(i);
                this.writer.write(c);
            }
        }
    }

    @Override
    public void startNode(String name){
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.push(name);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    private void finishTag() {
        if (tagInProgress) {
            writer.write('>');
        }
        tagInProgress = false;
        if (readyForNewLine) {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine() {
        writer.write('\n');
        for (int i = 0; i < depth; i++) {
            writer.write(lineIndenter);
        }
    }

    public void flush() {
        writer.flush();
    }

    public void close() {
        writer.close();
    }

    @Override
    public HierarchicalStreamWriter underlyingWriter() {
        return this;
    }

    @Override
    public void startNode(String name, @SuppressWarnings("rawtypes")Class arg1) {
        // TODO Auto-generated method stub
        tagIsEmpty = false;
        finishTag();
        writer.write('<');
        writer.write(name);
        elementStack.push(name);
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

}

package org.openelis.report.qasummary;

import java.util.ArrayList;
import java.util.Iterator;

import org.openelis.utils.Counter;

import net.sf.jasperreports.engine.*;

public class CounterDataSource implements JRRewindableDataSource {
    protected Counter  counter;
    protected Iterator iter;
    protected String   key;
    protected Integer  value;

    public boolean next() throws JRException {

        if (iter.hasNext()) {
            key = (String)iter.next();
            value = counter.get(key);
        } else
            return false;

        return true;
    }

    private CounterDataSource(Counter c) {
        ArrayList al;
        counter = c;
        al = counter.getKeys();
        iter = al.iterator();

    }

    public static CounterDataSource getInstance(Counter c) {
        CounterDataSource cds;

        cds = new CounterDataSource(c);

        return cds;
    }

    public Object getFieldValue(JRField field) throws JRException {
        if ("key".equals(field.getName()))
            return key;
        else if ("value".equals(field.getName()))
            return value;
        return null;
    }

    public void moveFirst() throws JRException {
        ArrayList al;
        al = counter.getKeys();
        iter = al.iterator();
    }
}

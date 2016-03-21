package org.openelis.modules.todo1.client;

import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public class Chart extends org.moxieapps.gwt.highcharts.client.Chart implements RequiresResize, ProvidesResize {
        @Override
        public void onResize() {
            if (isAttached() && getOffsetWidth() > 0 && getOffsetHeight() > 0)
                reflow();
       }
}

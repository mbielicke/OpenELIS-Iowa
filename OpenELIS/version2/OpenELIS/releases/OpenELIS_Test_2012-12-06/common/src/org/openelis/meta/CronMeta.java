package org.openelis.meta;

import java.util.Arrays;
import java.util.HashSet;

import org.openelis.gwt.common.Meta;
import org.openelis.gwt.common.MetaMap;

public class CronMeta implements Meta, MetaMap {

    public static final String     ID = "_cron.id", CRON_TAB = "_cron.cronTab",
                    NAME = "_cron.name", IS_ACTIVE = "_cron.isActive", BEAN = "_cron.bean",
                    METHOD = "_cron.method", PARAMETERS = "_cron.parameters",
                    LAST_RUN = "_cron.lasRun";

    private static HashSet<String> names;

    static {
        names = new HashSet<String>(Arrays.asList(ID, CRON_TAB, NAME, IS_ACTIVE, BEAN, METHOD,
                                                  PARAMETERS, LAST_RUN));
    }

    public static String getId() {
        return ID;
    }

    public static String getCronTab() {
        return CRON_TAB;
    }

    public static String getName() {
        return NAME;
    }

    public static String getIsActive() {
        return IS_ACTIVE;
    }

    public static String getBean() {
        return BEAN;
    }

    public static String getMethod() {
        return METHOD;
    }

    public static String getParameters() {
        return PARAMETERS;
    }

    public static String getLastRun() {
        return LAST_RUN;
    }

    public String buildFrom(String where) {
        return "Cron _cron ";
    }

    public boolean hasColumn(String columnName) {
        return names.contains(columnName);
    }
}
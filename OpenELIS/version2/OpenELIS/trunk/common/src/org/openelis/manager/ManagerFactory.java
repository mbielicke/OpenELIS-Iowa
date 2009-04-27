package org.openelis.manager;

public class ManagerFactory {
    public static boolean isClient = false;

    public static SampleManagerIOInt getSampleManagerIO() {
        if (isClient)
            return (SampleManagerIOInt)getManager("org.openelis.manager.SampleItemsManagerIOEJB");
        else
            return (SampleManagerIOInt)getManager("org.openelis.bean.SampleManagerIOEJB");
    }

    public static SampleItemsManagerIOInt getSampleItemManagerIO() {
        if (isClient)
            return (SampleItemsManagerIOInt)getManager("org.openelis.manager.SampleItemsManagerIOClient");
        else
            return (SampleItemsManagerIOInt)getManager("org.openelis.bean.SampleItemsManagerIOEJB");
    }
    
    public static SampleHumanManagerIOInt getSampleHumanManagerIO() {
        if (isClient)
            return (SampleHumanManagerIOInt)getManager("org.openelis.manager.SampleHumanManagerIOClient");
        else
            return (SampleHumanManagerIOInt)getManager("org.openelis.bean.SampleHumanManagerIOEJB");
    }
    
    public static SampleEnvironmentalManagerIOInt getSampleEnvironmentalManagerIO() {
        if (isClient)
            return (SampleEnvironmentalManagerIOInt)getManager("org.openelis.manager.SampleEnvironmentalManagerIOClient");
        else
            return (SampleEnvironmentalManagerIOInt)getManager("org.openelis.bean.SampleEnvironmentalManagerIOEJB");
    }

    private static Object getManager(String className) {
        Class cls;
        Object obj;

        try {
            cls = Class.forName(className);
            obj = cls.newInstance();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            obj = null;
        }
        return obj;
    }
}

package org.openelis.web.modules.report.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletOutputStream;

import org.openelis.gwt.common.ReportProgress;
import org.openelis.gwt.common.data.Query;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.gwt.server.ServiceUtils;
import org.openelis.gwt.widget.QueryFieldUtil;
import org.openelis.util.ReportUtil;
import org.openelis.util.SessionManager;
import org.openelis.util.XMLUtil;
import org.openelis.web.modules.main.server.OpenELISWebService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReportService {
	
	public String getReport(String report) {
		Document doc = null;
		try {
			doc = XMLUtil.createNew("doc");
			Element root = doc.getDocumentElement();
			Element baseEL = doc.createElement("url");
			baseEL.appendChild(doc.createTextNode("Report"));
			root.appendChild(baseEL);
			Element reportEl = doc.createElement("report");
			reportEl.appendChild(doc.createTextNode(report));
			ReportUtil ru = new ReportUtil();
			ru.setBaseURL("http://erudite.uhl.uiowa.edu/"+report);
			//ru.getReportParameters((String) SessionManager.getSession().getAttribute("USER_NAME"));
			ru.getReportParameters("tschmidt");
			for (int i = 0; i < ru.reportParameters.size(); i++) {
				Hashtable nameValuePairs = (Hashtable) ru.reportParameters.elementAt(i);
				String name = (String) nameValuePairs.get(ru.NAME_KEY), prompt = (String) nameValuePairs
						.get(ru.PROMPT_KEY), type = (String) nameValuePairs
						.get(ru.TYPE_KEY), picture = (String) nameValuePairs
						.get(ru.MASK_KEY);

				Vector<String> options = (Vector<String>) nameValuePairs
						.get(ru.OPTION_LIST_KEY);
				if (options != null)
					System.out.println(name + ":" + prompt + ":" + type + ":"
							+ picture + ":" + options.toString());
				else
					System.out.println(name + ":" + prompt + ":" + type + ":"
							+ picture);
				Element param = doc.createElement("param");
				param.setAttribute("name", name);
				param.setAttribute("prompt", prompt);
				param.setAttribute("type", type);
				param.setAttribute("picture", picture);
				if (type.startsWith("array")){
						//&& (!name.equals("printer") || options.size() > 1)) {
					for (String option : options) {
						String display = option.substring(0,
								option.indexOf("|"));
						String value = option.substring(
								option.indexOf("|") + 1, option.length());
						Element optEL = doc.createElement("option");
						optEL.setAttribute("value",value);
						optEL.setAttribute("display",display);
						param.appendChild(optEL);
					}
					param.setAttribute("value", "option");
				}
				/*
				if (name.equals("printer") && options.size() == 1) {
					String option = options.get(0);
					String display = option.substring(0, option.indexOf("|"));
					String value = option.substring(option.indexOf("|") + 1,
							option.length());
					param.setAttribute("value", value);
				}
				*/
				root.appendChild(param);
			}
			return XMLUtil.toString(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ReportProgress run(Query query) {
		ArrayList<QueryData> qds;
		String report;
		URL                 url;
		URLConnection       urlConnection;
		OutputStream        os;
		InputStream         inputStream = null;
		int                 nextByte;
		String              params = "";
		File tempFile;
		FileOutputStream outStream; 
		ReportProgress fp = null;

		qds = query.getFields();
		report = qds.remove(qds.size() -1).query;
	
		
		for(QueryData qd : qds) 
			params += "&"+qd.key+"="+URLEncoder.encode(qd.query);

		try {
			//Connect to the Jasper Report engine
			url           = new URL("http://erudite.uhl.uiowa.edu/timetracker-report/"+report+"?LOGNAME=tschmidt&"+params);
			System.out.println("url = " + url.toString());
			urlConnection = url.openConnection();

			inputStream = urlConnection.getInputStream();
			
			tempFile = new File("/tmp/"+report+SessionManager.getSession().getId()+".pdf");
			outStream = new FileOutputStream(tempFile);
		
			fp = new ReportProgress();
			fp.name = report;
			fp.progress = 0;
			SessionManager.getSession().setAttribute(report, fp);

			long count = 0l;
			try {
				while((nextByte = inputStream.read()) >= 0) {
					count++;
					outStream.write(nextByte);
					//((ReportProgress)SessionManager.getSession().getAttribute("myreport")).generated = (int)(count/(double)fp.size*100.0);
				}
			} catch (IOException ioE) {
				ioE.printStackTrace();
			} finally {
				try {
					inputStream.close();
					outStream.close();
				} catch (IOException ignE) {}            
			}
			fp.size = count;
		}catch(Exception e){
			e.printStackTrace();
		}
		return fp;

	}
}


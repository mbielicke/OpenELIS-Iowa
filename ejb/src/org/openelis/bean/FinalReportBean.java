package org.openelis.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.ejb3.annotation.SecurityDomain;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.OptionListItem;
import org.openelis.domain.ReferenceTable;
import org.openelis.domain.SampleDO;
import org.openelis.domain.SampleFinalReportWebVO;
import org.openelis.gwt.common.DataBaseUtil;
import org.openelis.gwt.common.Datetime;
import org.openelis.gwt.common.InconsistencyException;
import org.openelis.gwt.common.NotFoundException;
import org.openelis.gwt.common.ReportStatus;
import org.openelis.gwt.common.data.QueryData;
import org.openelis.local.AnalysisLocal;
import org.openelis.local.DictionaryLocal;
import org.openelis.local.FinalReportLocal;
import org.openelis.local.LockLocal;
import org.openelis.local.SampleLocal;
import org.openelis.local.SessionCacheLocal;
import org.openelis.meta.SampleMeta;
import org.openelis.meta.SampleWebMeta;
import org.openelis.remote.FinalReportRemote;
import org.openelis.report.Prompt;
import org.openelis.report.finalreport.OrganizationPrint;
import org.openelis.report.finalreport.StatsDataSource;
import org.openelis.util.QueryBuilderV2;
import org.openelis.utils.EJBFactory;
import org.openelis.utils.PrinterList;
import org.openelis.utils.ReportUtil;

@Stateless
@SecurityDomain("openelis")
@Resource(name = "jdbc/OpenELISDB", type = DataSource.class,
          authenticationType = javax.annotation.Resource.AuthenticationType.CONTAINER,
          mappedName = "java:/OpenELISDS")
public class FinalReportBean implements FinalReportRemote, FinalReportLocal {

	@EJB
	private SessionCacheLocal session;

	@EJB
	private SampleLocal sampleBean;

	@EJB
	private LockLocal lockBean;

	@EJB
	private AnalysisLocal analysisBean;

    @EJB
    private DictionaryLocal dictionary;
    
	@Resource
	private SessionContext ctx;
	
	@PersistenceContext(unitName = "openelis")
    private EntityManager                      manager;

	private static int UNFOLDABLE_PAGE_COUNT = 6;
	
	private static Integer organizationReportToId, sampleInErrorId, analysisReleasedId;

    private static final SampleWebMeta meta = new SampleWebMeta();
    
    @PostConstruct
    public void init() {        
            try {
                organizationReportToId = dictionary.fetchBySystemName("org_report_to").getId();
                sampleInErrorId = dictionary.fetchBySystemName("sample_error").getId();
                analysisReleasedId = dictionary.fetchBySystemName("analysis_released").getId();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }    
    
	/**
	 * Returns the prompt for a single re-print
	 */
	public ArrayList<Prompt> getPromptsForSingle() throws Exception {
		ArrayList<OptionListItem> prn;
		ArrayList<Prompt> p;

		try {
			p = new ArrayList<Prompt>();

			p.add(new Prompt("ACCESSION_NUMBER", Prompt.Type.INTEGER)
					.setPrompt("Accession Number:").setWidth(75)
					.setRequired(true));
			/*
			 * p.add(new Prompt("ORGANIZATION_ID",
			 * Prompt.Type.INTEGER).setPrompt("Organization Id:")
			 * .setWidth(150));
			 */
			prn = PrinterList.getInstance().getListByType("pdf");
			prn.add(0, new OptionListItem("-view-", "View PDF"));
			p.add(new Prompt("PRINTER", Prompt.Type.ARRAY)
					.setPrompt("Printer:").setWidth(200).setOptionList(prn)
					.setMutiSelect(false).setRequired(true));
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

    /**
     * Returns the prompt for a batch print
     */
    public ArrayList<Prompt> getPromptsForBatch() throws Exception {
        ArrayList<OptionListItem> prn;
        ArrayList<Prompt> p;

        try {
            p = new ArrayList<Prompt>();

            prn = PrinterList.getInstance().getListByType("pdf");
            p.add(new Prompt("PRINTER", Prompt.Type.ARRAY)
                    .setPrompt("Printer:").setWidth(200).setOptionList(prn)
                    .setMutiSelect(false).setRequired(true));
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

	/**
	 * Final report for a single or reprint. The report is printed for the
	 * primary or secondary organization(s) ordered by organization.
	 */
    public ReportStatus runReportForSingle(ArrayList<QueryData> paramList) throws Exception {
        SampleDO data;
        Integer orgId;
        ReportStatus status;
        OrganizationPrint orgPrint;
        String orgParam, accession, printer;
        HashMap<String, QueryData> param;
        ArrayList<Object[]> results;
        ArrayList<OrganizationPrint> orgPrintList;

        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        session.setAttribute("FinalReport", status);

        /*
         * Recover all the parameters and build a specific where clause
         */
        param = ReportUtil.parameterMap(paramList);

        accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");
        orgParam = ReportUtil.getSingleParameter(param, "ORGANIZATION_ID");
        printer = ReportUtil.getSingleParameter(param, "PRINTER");

        if (DataBaseUtil.isEmpty(accession) || DataBaseUtil.isEmpty(printer))
            throw new InconsistencyException("You must specify the accession number and printer for this report");
        /*
         * find the sample
         */
        try {
            data = sampleBean.fetchByAccessionNumber(Integer.parseInt(accession));
        } catch (NotFoundException e) {
            throw new NotFoundException("A sample with accession number " + accession +
                                        " is not valid or does not exists");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        /*
         * find all the report to organizations for given sample
         */
        orgPrintList = new ArrayList<OrganizationPrint>();
        try {
            results = sampleBean.fetchSamplesForFinalReportSingle(data.getId());
            status.setMessage("Initializing report");
            /*
             * if the user didn't specify an id for an organization then a
             * report is created for all the organizations associated with the
             * sample, otherwise a report is created for the organization, the
             * id for which is specified by the user if it can be found in the
             * list of organizations for that sample
             */
            orgId = null;
            if (orgParam != null)
                orgId = Integer.parseInt(orgParam);

            orgPrint = null;
            for (Object[] result : results) {
                if (orgId == null || DataBaseUtil.isSame(orgId, result[1])) {
                    orgPrint = new OrganizationPrint();
                    orgPrint.setOrganizationId((Integer)result[1]);
                    orgPrint.setSampleIds("=" + data.getId());
                    orgPrintList.add(orgPrint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("Final report for accession number " + accession +
                                             " has incorrect status,\nmissing information, or has no analysis ready to be printed");

        print(orgPrintList, "R", false, status, printer);

        return status;
    }

	/**
	 * Final report for a single or reprint. The report is printed for the
	 * primary or secondary organization(s) ordered by organization.
	 */
	public ReportStatus runReportForPreview(ArrayList<QueryData> paramList) throws Exception {
		SampleDO data;
		ReportStatus status;
		OrganizationPrint orgPrint;
		String accession;
		HashMap<String, QueryData> param;
		Object[] result;
		ArrayList<Object[]> results;
		ArrayList<OrganizationPrint> orgPrintList;

		/*
		 * push status into session so we can query it while the report is
		 * running
		 */
		status = new ReportStatus();
		session.setAttribute("FinalReport", status);

		/*
		 * Recover all the parameters and build a specific where clause
		 */
		param = ReportUtil.parameterMap(paramList);
		accession = ReportUtil.getSingleParameter(param, "ACCESSION_NUMBER");

		/*
		 * find the sample
		 */
		try {
			data = sampleBean.fetchByAccessionNumber(Integer.parseInt(accession));
		} catch (NotFoundException e) {
			throw new NotFoundException("A sample with accession number " + accession + " is not valid or does not exists");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		/*
		 * find all the report to organizations for given sample
		 */
		orgPrintList = new ArrayList<OrganizationPrint>();
		try {
			results = sampleBean.fetchSamplesForFinalReportPreview(data.getId());
			status.setMessage("Initializing report");

			if (results.size() < 1)
				throw new InconsistencyException("Final report for accession number "+ accession+ " has incorrect status,\nmissing information, or has no analysis ready to be printed");

			result = results.get(0);
			orgPrint = new OrganizationPrint();
			orgPrint.setOrganizationId((Integer)result[1]);
			orgPrint.setSampleIds("= "+data.getId());
			orgPrintList.add(orgPrint);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		print(orgPrintList, "C", false, status, "-view-");

		return status;
	}

	/**
	 * Prints final reports for all ready to be printed samples. The routine
	 * time stamps all the analyses' printed date with current time and groups
	 * the output by organization.
	 * 
	 * Additionally, because we use automatic folding machine, the report sorts
	 * the entire output by the # of pages for each organization. A BLANK page
	 * is inserted between all the reports that have less than 6 pages and the
	 * remaining reports to stop the folding process.
	 */
	@RolesAllowed("r_final-select")
	@TransactionTimeout(600)
	public ReportStatus runReportForBatch(ArrayList<QueryData> paramList) throws Exception {
		int i;
		String printer;
		Datetime timeStamp;
		ReportStatus status;
		Object[] result, list;
		StringBuffer sampleIds;
		ArrayList<Integer> lockList;
		ArrayList<Object[]> resultList;
		ArrayList<OrganizationPrint> orgPrintList;
		HashMap<String, QueryData> param;
		HashMap<Integer, HashMap<Integer, Integer>> orgMap;
		HashMap<Integer, Integer> anaMap, samMap;
		Integer samId, prevSamId, orgId, anaId;
		Iterator<Integer> orgIter;
		OrganizationPrint orgPrint;

		/*
		 * Recover the printer
		 */
		param = ReportUtil.parameterMap(paramList);
		printer = ReportUtil.getSingleParameter(param, "PRINTER");

		/*
		 * obtain the list of sample ids, organization ids and analysis ids
		 */
		samMap = null;
		prevSamId = null;
		anaMap = new HashMap<Integer, Integer>();
		lockList = new ArrayList<Integer>();
		orgMap = new HashMap<Integer, HashMap<Integer, Integer>>();

		status = new ReportStatus();
		status.setMessage("Initializing report");
		session.setAttribute("FinalReport", status);

		/*
		 * loop through the list and lock all the samples obtained from
		 * resultList that can be locked; the ones that can't be locked and the
		 * organizations associated with them are excluded from the report being
		 * generated
		 */
		resultList = sampleBean.fetchSamplesForFinalReportBatch();
		for (i = 0; i < resultList.size(); i++) {
			result = resultList.get(i);
			samId = (Integer) result[0];
			orgId = (Integer) result[1];
			anaId = (Integer) result[2];

			if (!samId.equals(prevSamId)) {
				try {
					lockBean.lock(ReferenceTable.SAMPLE, samId);
					lockList.add(samId);
				} catch (Exception e) {
					/*
					 * skip all the samples that can't be locked.
					 */
					while (samId.equals(resultList.get(i)[0]))
						i++;
					prevSamId = null;
					continue;
				}
			}
			/*
			 * we are adding this sample id to the list of samples maintained
			 * for this organization
			 */
			samMap = orgMap.get(orgId);
			if (samMap == null) {
				samMap = new HashMap<Integer, Integer>();
				orgMap.put(orgId, samMap);
			}
			/*
			 * keep a unique analysis id list for update
			 */
			samMap.put(samId, samId);
			anaMap.put(anaId, anaId);
			prevSamId = samId;
		}

		/*
		 * update all the analyses with date printed
		 */
		timeStamp = Datetime.getInstance(Datetime.YEAR, Datetime.MINUTE);
		for (Integer key : anaMap.keySet())
			analysisBean.updatePrintedDate(key, timeStamp);

		/*
		 * start the report
		 */
		orgPrintList = new ArrayList<OrganizationPrint>();
		orgIter = orgMap.keySet().iterator();
		while (orgIter.hasNext()) {
			orgId = orgIter.next();
			samMap = orgMap.get(orgId);
			list = samMap.values().toArray();
			/*
			 * samples with null organizations (such as private well) are
			 * managed as single print rather then a batch for null organization
			 */
			if (orgId == null) {
				for (i = 0; i < list.length; i++) {
					orgPrint = new OrganizationPrint();
					orgPrint.setOrganizationId(orgId);
					orgPrint.setSampleIds("="+list[i]);
					orgPrintList.add(orgPrint);
				}
			} else {
				sampleIds = new StringBuffer();
				sampleIds.append("in (");
				for (i = 0; i < list.length; i++) {
					if (i != 0)
						sampleIds.append(",");
					sampleIds.append(list[i]);
				}
				sampleIds.append(")");
				orgPrint = new OrganizationPrint();
				orgPrint.setOrganizationId(orgId);
				orgPrint.setSampleIds(sampleIds.toString());
				orgPrintList.add(orgPrint);
			}
		}
		print(orgPrintList, "R", true, status, printer);

		/*
		 * unlock all the samples
		 */
		for (Integer id : lockList)
			lockBean.unlock(ReferenceTable.SAMPLE, id);

		return status;
	}
	
	public ReportStatus runReportForWeb(ArrayList<QueryData> paramList) throws Exception {
        QueryData field;
        Integer orgId, samId;
        ReportStatus status;
        OrganizationPrint orgPrint;
        SampleFinalReportWebVO voData;
        String sampleList, domain;
        ArrayList<Integer> sampleIds, samIds;
        String[] selectedVoStr;
        HashMap<Integer, ArrayList<Integer>> map;
        ArrayList<OrganizationPrint> orgPrintList;
        ArrayList<SampleFinalReportWebVO> retrievedList;
        Iterator<Integer> orgIter;

        retrievedList = null;
        sampleList = null;
        map = new HashMap<Integer, ArrayList<Integer>>();
        samIds = new ArrayList<Integer>();
        /*
         * push status into session so we can query it while the report is
         * running
         */
        status = new ReportStatus();
        status.setMessage("Initializing report");
        session.setAttribute("FinalReport", status);

        field = paramList.get(0);
        if((field.query)== null) {
            throw new InconsistencyException("Please select atleast one sample to run the report");
        }            
        selectedVoStr = field.query.split(",");
        
        field = paramList.get(1);
        domain = field.query;          
        
        if(domain.equals("E"))
            retrievedList = (ArrayList<SampleFinalReportWebVO>)session.getAttribute("sampleEnvlist");
        else if(domain.equals("W"))
            retrievedList = (ArrayList<SampleFinalReportWebVO>)session.getAttribute("samplePvtlist");
        
        for (int j = 0; j < selectedVoStr.length; j++ ) {
            voData = retrievedList.get(Integer.parseInt(selectedVoStr[j]));
            orgId = voData.getOrganizationId();
            samId = voData.getId();
            if (map.get(orgId) == null) {
                sampleIds = new ArrayList<Integer>();
                sampleIds.add(samId);
                map.put(orgId, sampleIds);
            } else {
                sampleIds = map.get(orgId);
                sampleIds.add(samId);
                map.put(orgId, sampleIds);
            }
        }

        orgPrintList = new ArrayList<OrganizationPrint>();
        orgIter = map.keySet().iterator();
        while (orgIter.hasNext()) {
            orgId = orgIter.next();
            samIds = map.get(orgId);
            if (samIds.size() > 1) {
                sampleList = "in (";
                for (int i = 0; i < samIds.size(); i++ )
                    sampleList += samIds.get(i) + ",";
                sampleList = sampleList.substring(0, sampleList.length() - 1) + ")";
            } else if (samIds.size() == 1)
                sampleList = " = " + samIds.get(0);
            try {
                orgPrint = null;

                orgPrint = new OrganizationPrint();
                orgPrint.setOrganizationId(orgId);
                orgPrint.setSampleIds(sampleList);
                orgPrintList.add(orgPrint);

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        if (orgPrintList.size() == 0)
            throw new InconsistencyException("Final report  "
                                             + " has incorrect status,\nmissing information, or has no analysis ready to be printed");

        print(orgPrintList, "R", false, status, "");

        return status;
    }  
	
	public ArrayList<SampleFinalReportWebVO> getSampleListForDomain(ArrayList<QueryData> fields) throws Exception {
	    String domain;
	    Query query;
	    QueryData field;
        QueryBuilderV2 builder;
        ArrayList<QueryData> list;
        ArrayList<SampleFinalReportWebVO> returnList; 
        
        returnList=null;
        domain = null;
        builder = new QueryBuilderV2();
        
        for (int i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (field.key).equals(SampleWebMeta.getSampleOrgOrganizationId())) 
                domain = "E";
            else if ( (field.key).equals(SampleWebMeta.getWellOrganizationId())) 
                domain = "W";
        }
        list = createWhereFromParamFields(fields);        
        builder.setMeta(meta);
        if (domain.equals("E"))
            builder = setQueryBuilderForEnvironmental(builder, list);
        else if (domain.equals("W"))
            builder = setQueryBuilderForPrivate(builder, list);
        builder.setOrderBy(SampleWebMeta.getAccessionNumber());

        query = manager.createQuery(builder.getEJBQL());
        builder.setQueryParams(query, list);
        returnList = DataBaseUtil.toArrayList(query.getResultList());
        if (domain.equals("E"))
            session.setAttribute("sampleEnvlist",returnList);
        else if (domain.equals("W"))
            session.setAttribute("samplePvtlist", returnList);
        return returnList;        
	}
	
    private ArrayList<QueryData> createWhereFromParamFields(ArrayList<QueryData> fields) {
        int i;
        QueryData field, fRel, fCol, fAcc;
        ArrayList<QueryData> list;

        list = new ArrayList<QueryData>();
        fRel = fCol = fAcc = null;

        for (i = 0; i < fields.size(); i++ ) {
            field = fields.get(i);
            if ( (field.key).equals("RELEASED_FROM")) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = field.query + ".." + fRel.query;
                    list.add(fRel);
                }
            } else if ( (field.key).equals("RELEASED_TO")) {
                if (fRel == null) {
                    fRel = field;
                    fRel.key = SampleWebMeta.getReleasedDate();
                } else {
                    fRel.query = fRel.query + ".." + field.query;
                    list.add(fRel);
                }
            } else if ( (field.key).equals("COLLECTED_FROM")) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = field.query + ".." + fCol.query;
                    list.add(fCol);
                }
            } else if ( (field.key).equals("COLLECTED_TO")) {
                if (fCol == null) {
                    fCol = field;
                    fCol.key = SampleWebMeta.getCollectionDate();
                } else {
                    fCol.query = fCol.query + ".." + field.query;
                    list.add(fCol);
                }
            } else if ( (field.key).equals("ACCESSION_FROM")) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = field.query + ".." + fAcc.query;
                    list.add(fAcc);
                }
            } else if ( (field.key).equals("ACCESSION_TO")) {
                if (fAcc == null) {
                    fAcc = field;
                    fAcc.key = SampleWebMeta.getAccessionNumber();
                } else {
                    fAcc.query = fAcc.query + ".." + field.query;
                    list.add(fAcc);
                }
            } else if ( (field.key).equals("COLLECTOR_NAME")) {
                field.key = SampleWebMeta.getEnvCollector();
                list.add(field);
            } else if ( (field.key).equals("CLIENT_REFERENCE")) {
                field.key = SampleWebMeta.getClientReference();
                list.add(field);
            } else if ( (field.key).equals("COLLECTION_SITE")) {
                field.key = SampleWebMeta.getEnvLocation();
                list.add(field);
            } else if ( (field.key).equals("COLLECTION_TOWN")) {
                field.key = SampleWebMeta.getLocationAddrCity();
                list.add(field);
            } else if ( (field.key).equals("PROJECT_CODE")) {
                field.key = SampleWebMeta.getProjectId();
                list.add(field);
            } else if ( (field.key).equals(SampleWebMeta.getSampleOrgOrganizationId())) {
                list.add(field);
            } else if ( (field.key).equals(SampleWebMeta.getWellOrganizationId())) {
                list.add(field);
            }
        }
        return list;
    }    
    
    private QueryBuilderV2 setQueryBuilderForEnvironmental (QueryBuilderV2 builder, ArrayList<QueryData> list) throws Exception {
        builder.setSelect("distinct new org.openelis.domain.SampleFinalReportWebVO(" + SampleWebMeta.getId() + ", " +
                          SampleWebMeta.getAccessionNumber() +", "+ SampleWebMeta.getSampleOrgOrganizationId() +", "+
                          SampleWebMeta.getCollectionDate() +", "+SampleWebMeta.getCollectionTime() +", "+ SampleWebMeta.getEnvLocation()+", "+
                          SampleWebMeta.getEnvCollector() +", "+SampleWebMeta.getStatusId()+", "+
                          SampleWebMeta.getLocationAddrCity()+ ") ");
        builder.constructWhere(list);
        builder.addWhere(SampleWebMeta.getEnvSampleId() + "=" +SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getSampleOrgTypeId()+ "=" + organizationReportToId);
        builder.addWhere(SampleWebMeta.getStatusId() + " != " + sampleInErrorId );
        builder.addWhere(SampleWebMeta.getItemSampleId() +" = " + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() +"=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() +" = "+ analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() +" = "+ "'Y'");
        
        return builder;
    }
    
    private QueryBuilderV2 setQueryBuilderForPrivate (QueryBuilderV2 builder, ArrayList<QueryData> list) throws Exception {
        builder.setSelect("distinct new org.openelis.domain.SampleFinalReportWebVO(" +
                          SampleWebMeta.getId() + ", " + SampleWebMeta.getAccessionNumber() + ", " +
                          SampleWebMeta.getWellOrganizationId() + ", " +
                          SampleWebMeta.getCollectionDate() + ", " +
                          SampleWebMeta.getCollectionTime() + ", " +
                          SampleWebMeta.getWellLocation() + ", " + SampleWebMeta.getWellCollector() + ", " + SampleWebMeta.getStatusId() + ", " +
                          SampleWebMeta.getWellOrganizationAddrCity() + ", "+SampleWebMeta.getWellOwner()+") ");
        builder.constructWhere(list);
        builder.addWhere(SampleWebMeta.getWellSampleId() + "=" + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getWellOrganizationAddressId() +"=" +SampleWebMeta.getWellOrganizationAddrId());
        builder.addWhere(SampleWebMeta.getStatusId() + " != " + sampleInErrorId);
        builder.addWhere(SampleWebMeta.getItemSampleId() + " = " + SampleWebMeta.getId());
        builder.addWhere(SampleWebMeta.getAnalysisSampleItemId() + "=" + SampleWebMeta.getItemId());
        builder.addWhere(SampleWebMeta.getAnalysisStatusId() + " = " + analysisReleasedId);
        builder.addWhere(SampleWebMeta.getAnalysisIsReportable() + " = " + "'Y'");
        
        return builder;
    }      
	
    public ArrayList<IdNameVO> getProjectList(ArrayList<QueryData> paramList) throws Exception {
        int i;
        String key,str;
        String[] orgIds;
        QueryData field;
        ArrayList<IdNameVO> resultList;
        ArrayList<Integer> organizationId;

        orgIds = null;
        organizationId = new ArrayList<Integer>();
        key = null;
        resultList = null;
        
        for (i = 0; i < paramList.size(); i++ ) {
            field = paramList.get(i);
            key = field.key;
            str = field.query;
            orgIds = str.split("\\|");
        }
        for (int j = 0; j < orgIds.length; j++ )
            organizationId.add(Integer.parseInt(orgIds[j]));
        if(key.equals(SampleMeta.getWellOrganizationId()))
            resultList = sampleBean.fetchProjectsForPvtOrganizations(organizationId);
        else if(key.equals(SampleMeta.getSampleOrgOrganizationId()))
            resultList = sampleBean.fetchProjectsForOrganizations(organizationId);
        return resultList;
    } 
     
	private void print(ArrayList<OrganizationPrint> orgPrintList, String reportType,
	                   boolean forMailing, ReportStatus status, String printer) throws Exception {
		int i, n;
		URL url;
		File tempFile;
		Integer zero;
		Connection con;
		boolean needBlank;
		JasperReport jreport;
		JRExporter jexport;
		String dir, printstat;
		List<JRPrintPage> pages;
		HashMap<String, Object> jparam;
		JasperPrint masterJprint, blankJprint, statsJprint;
		StatsDataSource ds;

		con = null;
		zero = new Integer(0);
		try {
			con = ReportUtil.getConnection(ctx);
			/*
			 * get all the report instances
			 */
			url = ReportUtil.getResourceURL("org/openelis/report/finalreport/blank.jasper");			
			jreport = (JasperReport) JRLoader.loadObject(url);
			blankJprint = JasperFillManager.fillReport(jreport, null);
			masterJprint = JasperFillManager.fillReport(jreport, null);

			dir = ReportUtil.getResourcePath(url);
			jparam = new HashMap<String, Object>();
			jparam.put("REPORT_TYPE", reportType);
			jparam.put("SUBREPORT_DIR", dir);
			jparam.put("LOGNAME", EJBFactory.getUserCache().getName());
			
			url = ReportUtil.getResourceURL("org/openelis/report/finalreport/main.jasper");
			jreport = (JasperReport) JRLoader.loadObject(url);
			/*
			 * for each organization, print all the samples 
			 */
			for (OrganizationPrint o : orgPrintList) {
			    if (o.getOrganizationId() == null)
			        jparam.put("ORGANIZATION_ID", zero);
			    else
			        jparam.put("ORGANIZATION_ID", o.getOrganizationId());
				jparam.put("SAMPLE_ID", o.getSampleIds());
				jparam.put("ORGANIZATION_PRINT", o);
				o.setJprint(JasperFillManager.fillReport(jreport, jparam, con));
			}

			try {
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			con = null;
		
			/*
			 * Sort the print by # of pages.
			 */
			if (orgPrintList.size() > 1)
				Collections.sort(orgPrintList, new MyComparator());

			/*
			 * assemble all the pages. put in a blank page between 6 and more
			 * pages if we are going to mail them (for folding machine)
			 */
			needBlank = forMailing;
			for (OrganizationPrint o : orgPrintList) {
				pages = o.getJprint().getPages();
				n = pages.size();
				if (n >= UNFOLDABLE_PAGE_COUNT && needBlank) {
					masterJprint.addPage((JRPrintPage) blankJprint.getPages().get(0));
					needBlank = false;
				}
				for (i = 0; i < n; i++)
					masterJprint.addPage((JRPrintPage) pages.get(i));
			}
		
			/*
			 * the stat page at the end will list all the organizations printed in
			 * this run.
			 */
			if (forMailing) {
				ds = new StatsDataSource();
				ds.setStats(orgPrintList);
				url = ReportUtil.getResourceURL("org/openelis/report/finalreport/stats.jasper");
				statsJprint = JasperFillManager.fillReport((JasperReport) JRLoader.loadObject(url), jparam, ds);

				pages = statsJprint.getPages();
				n = pages.size();
				for (i = 0; i < n; i++)
					masterJprint.addPage((JRPrintPage) pages.get(i));
			}
		
			/*
			 * Finally, print the pages
			 */
			masterJprint.removePage(0);
			jexport = new JRPdfExporter();
			tempFile = File.createTempFile("finalreport", ".pdf", new File("/tmp"));
			jexport.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(tempFile));
			jexport.setParameter(JRExporterParameter.JASPER_PRINT, masterJprint);

			status.setMessage("Outputing report").setPercentComplete(20);

			jexport.exportReport();

			status.setPercentComplete(100);

			if (ReportUtil.isPrinter(printer)) {
				printstat = ReportUtil.print(tempFile, printer, 1);
				status.setMessage(printstat).setStatus(ReportStatus.Status.PRINTED);
			} else {
				tempFile = ReportUtil.saveForUpload(tempFile);
				status.setMessage(tempFile.getName())
				      .setPath(ReportUtil.getSystemVariableValue("upload_stream_directory"))
					  .setStatus(ReportStatus.Status.SAVED);
			}
		} catch (Exception e) {
			try {
				if (con != null)
					con.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			con = null;
			throw e;
		}			
	}


	class MyComparator implements Comparator<OrganizationPrint> {
		public int compare(OrganizationPrint o1, OrganizationPrint o2) {
			return o1.getJprint().getPages().size() - o2.getJprint().getPages().size();
        }
    }
}

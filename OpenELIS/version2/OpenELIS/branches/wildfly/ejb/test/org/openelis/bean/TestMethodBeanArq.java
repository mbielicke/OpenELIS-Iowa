package org.openelis.bean;

import static org.openelis.deployment.Deployment.createBaseDeployment;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.domain.IdNameVO;
import org.openelis.domain.MethodDO;
import org.openelis.entity.Method;
import org.openelis.meta.MethodMeta;
import org.openelis.ui.common.NotFoundException;
import org.openelis.ui.common.data.QueryData;


@RunWith(Arquillian.class)
public class TestMethodBeanArq {
	
	@Inject
	MethodBean method;
	
	@Deployment
	public static WebArchive createDeployment() {
		return createBaseDeployment()
				.addClasses(MethodBean.class,LockBean.class,UserCacheBean.class,Method.class);
	}
	
	@Test
	public void fetchById() throws Exception {
		MethodDO data = method.fetchById(1);
		assertEquals(new Integer(1),data.getId());
		assertEquals("sda",data.getName());
	}

	@Test(expected=NotFoundException.class)
	public void fetchById_notFound() throws Exception {
		method.fetchById(-1);
	}
	
	@Test
	public void fetchByName() throws Exception {
		List<MethodDO> data = method.fetchByName("sda", 10);
		assertNotNull(data);
		assertEquals(1,data.size());
		assertEquals(new Integer(1),data.get(0).getId());
	}
	
	@Test
	public void fetchByName_emptySet() {
		assertTrue(method.fetchByName("dasdasdasda", 1).isEmpty());
	}
	
	@Test
	public void fetchByName_maxResults() {
		List<MethodDO> data = method.fetchByName("s%", 10);
		assertEquals(10,data.size());
	}
	
	@Test
	public void fetchActiveByName() {
		List<MethodDO> data = method.fetchActiveByName("sda",1);
		assertNotNull(data);
		assertEquals(1,data.size());
		assertEquals(new Integer(1),data.get(0).getId());
	}
	
	@Test
	public void fetchActiveByName_notActive() {
		assertTrue(method.fetchActiveByName("method9232", 1).isEmpty());
	}
	
	@Test
	public void fetchActiveByName_maxResults() {
		List<MethodDO> data = method.fetchActiveByName("s%", 10);
		assertEquals(10,data.size());
	}
	
	@Test
	public void query() throws Exception {
		ArrayList<QueryData> qds = new ArrayList<>();
		qds.add(new QueryData(MethodMeta.getName(),QueryData.Type.STRING,"sda"));
		ArrayList<IdNameVO> results = method.query(qds, 0, 10);
		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(new Integer(1),results.get(0).getId());
	}
	
	@Test(expected=NotFoundException.class)
	public void query_notFound() throws Exception {
		ArrayList<QueryData> qds = new ArrayList<>();
		qds.add(new QueryData(MethodMeta.getName(),QueryData.Type.STRING,"asdasdasggrrg"));
		method.query(qds, 0, 10);
	}
}

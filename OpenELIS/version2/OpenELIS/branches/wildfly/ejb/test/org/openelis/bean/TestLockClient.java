package org.openelis.bean;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openelis.TestLockServlet;
import org.openelis.deployment.Deployments;

@RunWith(Arquillian.class)
public class TestLockClient {
	
	@ArquillianResource(TestLockServlet.class) 
	URL baseUrl;
	
	@Deployment(testable=false)
	public static WebArchive deploy() {
		return Deployments.createBaseWithApplication()
				.addClasses(LockBean.class,LockCacheBean.class,UserCacheBean.class,TestLockServlet.class);
	}
	
	@Test
	public void loadTest() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			executorService.execute(new LockExecutor(i*1000,(i*1000)+1000));
		}
		executorService.shutdown();
		if (executorService.awaitTermination(30, TimeUnit.SECONDS) ) {
			assertEquals(0,getLockCount());
		} else {
			fail("Time out reached");
		}
	}
	
	private int getLockCount() throws Exception {
		final URL url = new URL(baseUrl,"Lock?count=true");
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String count = reader.readLine();
		reader.close();
		return Integer.parseInt(count);
	}
	
	public class LockExecutor implements Runnable {
		
		int start, end;
		
		public LockExecutor(int start, int end) {
			this.start = start;
			this.end = end;
		}
		
		@Override
		public void run() {
			try  {
				for(int i = start; i < end; i++) {
					final URL url = new URL(baseUrl,"Lock?lock=true&tableId=1&id="+i);
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
					while (reader.readLine() != null);
					reader.close();
				}
				for(int i = start; i < end; i++) {
					final URL url = new URL(baseUrl,"Lock?validate=true&tableId=1&id="+i);
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
					while (reader.readLine() != null);
					reader.close();
				}
				for(int i = start; i < end; i++) {
					final URL url = new URL(baseUrl,"Lock?unlock=true&tableId=1&id="+i);
					BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
					while (reader.readLine() != null);
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
 }

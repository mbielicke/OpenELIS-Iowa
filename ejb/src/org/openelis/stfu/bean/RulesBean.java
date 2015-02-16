package org.openelis.stfu.bean;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.openelis.stfu.domain.RulesTest;
import org.openelis.stfu.scanner.Scanner;

@Startup
@Singleton
public class RulesBean {

	@EJB
	protected ScannerBean scanner;


	public void startUp() {
		try {
		scanner.scan();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	
    public static class Message {

        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }
	
}

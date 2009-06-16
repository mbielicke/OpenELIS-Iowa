package org.openelis.modules.organization.client;

import java.util.ArrayList;

import org.openelis.domain.NoteDO;
import org.openelis.gwt.common.RPC;

public class NotesRPC implements RPC {

	private static final long serialVersionUID = 1L;
	public Integer key;
	public String subject;
	public String text;
	public ArrayList<NoteDO> notes;
}

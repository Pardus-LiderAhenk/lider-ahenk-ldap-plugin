package tr.org.liderahenk.ldap.handlers;


import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.ldap.dialogs.RenameAgentNameDialog;

//TODO use MultipleSelectionHandler if this task support multiple LDAP entries/DNs otherwise use SingleSelectionHandler.
public class RenameAgentNameHandler extends SingleSelectionHandler {
	
	

	private Logger logger = LoggerFactory.getLogger(RenameAgentNameHandler.class);
	
	@Override
	public void executeWithDn(String dn) {

		RenameAgentNameDialog dialog = new RenameAgentNameDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.open();
	}
	
	
}
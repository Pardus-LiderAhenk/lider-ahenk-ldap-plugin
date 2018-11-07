package tr.org.liderahenk.ldap.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldap.constants.LdapConstants;
import tr.org.liderahenk.ldap.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.enums.RestResponseStatus;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.widgets.NotifierColorsFactory.NotifierTheme;

/**
 * Task execution dialog for ldap plugin.
 * 
 */
public class AddUserDialog extends DefaultLiderDialog {

	
	private static final Logger logger = LoggerFactory.getLogger(AddUserDialog.class);
	
	
	private String dn;
	
	private Text textName;
	private Text textSurname;
	private ProgressBar progressBar;


	private Text textUid;


	private Text textGid;


	private Text textUidNumber;


	private Text textPassword;
	
	public AddUserDialog(Shell parentShell, String dn) {
		super(parentShell);
		this.dn=dn;
	}
	
	@Override
	public void create() {
		super.create();
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.getString("add_user"));
	}
	
	
	public Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));	
		
		GridData gridData= new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gridData.widthHint = 600;
		gridData.heightHint = 200;
		
		composite.setLayoutData(gridData);
		
		Label info = new Label(composite, SWT.NONE);
		info.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		info.setText("Seçili değerin altına kullanıcı ekleyebilirsiniz.");
		
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		nameLabel.setText("Ad (cn) :");
		
		textName = new Text(composite, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label surname = new Label(composite, SWT.NONE);
		surname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		surname.setText("Soyad (sn) :");
		
		textSurname = new Text(composite, SWT.BORDER);
		textSurname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label uid = new Label(composite, SWT.NONE);
		uid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		uid.setText("Uid :");
		
		textUid = new Text(composite, SWT.BORDER);
		textUid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label gid = new Label(composite, SWT.NONE);
		gid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		gid.setText("Grup Id (gid) :");
		
		textGid = new Text(composite, SWT.BORDER);
		textGid.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	
		Label uidNumber = new Label(composite, SWT.NONE);
		uidNumber.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		uidNumber.setText("Uid Number :");
		
		textUidNumber = new Text(composite, SWT.BORDER);
		textUidNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label password = new Label(composite, SWT.NONE);
		password.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		password.setText("Password :");
		
		textPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData gdProgress = new GridData(GridData.FILL_HORIZONTAL);
		gdProgress.grabExcessVerticalSpace = true;
		gdProgress.horizontalSpan = 2;
		gdProgress.heightHint = 10;
		progressBar.setLayoutData(gdProgress);
		progressBar.setVisible(false);
		
		return composite;
	
	}
	
	@Override
	protected void okPressed() {

		setReturnCode(OK);
		if (
		(textName != null && textName.getText()=="") 
		|| (textSurname != null && textSurname.getText()=="") 
		|| (textGid != null && textGid.getText()=="") 
		|| (textUid != null && textUid.getText()=="") 
		|| (textUidNumber != null && textUidNumber.getText()=="") 
		|| (textPassword != null && textPassword.getText()=="") 
		)
		{
			Notifier.notifyandShow(null, "", Messages.getString("MANDATORY_FIELD"), "", NotifierTheme.ERROR_THEME);
			return;
		}

		if (LiderConfirmBox.open(Display.getDefault().getActiveShell(), Messages.getString("TASK_EXEC_TITLE"),
				Messages.getString("TASK_EXEC_MESSAGE"))) {
			try {
				progressBar.setVisible(true);

				TaskRequest task = new TaskRequest(getDnSet(), DNType.USER, getPluginName(), getPluginVersion(),
						getCommandId(), getParameterMap(), null, null, new Date());
				
				IResponse response = TaskRestUtils.execute(task);
				
				if (response != null && response.getStatus() == RestResponseStatus.OK) {
					Notifier.success(null, Messages.getString("TASK_EXECUTED"));
				} else if (response != null && response.getStatus() == RestResponseStatus.ERROR) {
					if (response.getMessages() != null && !response.getMessages().isEmpty()) {
						Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"),
								StringUtils.join(response.getMessages(), ""));
					} else {
						Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
					}
				}
				progressBar.setVisible(false);
				
				getButton(IDialogConstants.OK_ID).setEnabled(false);
			} catch (Exception e1) {
				progressBar.setVisible(false);
				logger.error(e1.getMessage(), e1);
				Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
			}
		}

	}
	
	@Override
	protected void cancelPressed() {
		close();
	}
	
	private List<String> getDnSet() {

		List<String> dnList = new ArrayList<>();
		dnList.add(dn);

		return dnList;
	}
	
	public Map<String, Object> getParameterMap() {
		
		Map<String, Object> map = new HashMap<>();
		map.put("dn", dn);
		map.put("cn",textName.getText());
		map.put("gidNumber",textGid.getText());
		map.put("sn",textSurname.getText());
		map.put("uid",textUid.getText());
		map.put("uidNumber", textUidNumber.getText());
		map.put("password", textPassword.getText());
		return map;
	}

	public String getCommandId() {
		return "ADD_USER";
	}

	public String getPluginName() {
		return LdapConstants.PLUGIN_NAME;
	}

	public String getPluginVersion() {
		return LdapConstants.PLUGIN_VERSION;
	}

	
}

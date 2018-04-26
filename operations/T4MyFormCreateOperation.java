package t4dev.operations;

import t4dev.forms.T4MyFormPanel;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.Markpoint;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;

public class T4MyFormCreateOperation extends AbstractAIFOperation {
	protected TCSession session = null;
	protected TCComponent parent = null;
	protected T4MyFormPanel formPanel = null;
	protected TCComponent formComponent = null;
	
	public T4MyFormCreateOperation(TCSession session, TCComponent parent, T4MyFormPanel formPanel){
		this.session = session;
		this.parent = parent;
		this.formPanel = formPanel;
	}
	
	@Override
	public void executeOperation() throws Exception {
		// TODO Auto-generated method stub
		Markpoint markpoint = null;
		try {
			markpoint = new Markpoint(session);
			formComponent = createT4MyForm();
			if (parent != null && formComponent != null){
				parent.add("contents", formComponent);
			}
			markpoint.forget();
		} catch(Exception ex)
		{
			markpoint.rollBack();
			throw ex;
		}
	}

	public TCComponentForm createT4MyForm() throws TCException, ServiceException {
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse createresponse = null;
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn acreatein[] = null;
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput createinput = null;

    	com.teamcenter.services.rac.core.DataManagementService datamanagementservice = com.teamcenter.services.rac.core.DataManagementService.getService(session);
    	acreatein = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
    	acreatein[0] = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn();
    	createinput = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput();
    	createinput.stringProps.put("object_name", formPanel.getObjectName());
    	createinput.stringProps.put("object_desc", formPanel.getObjectDesc());
    	createinput.doubleProps.put("t4MyDouble", formPanel.getMyDouble());
    	createinput.boName = "T4MyForm";

    	acreatein[0].data = createinput;
    	createresponse = datamanagementservice.createObjects(acreatein);
        if(createresponse.serviceData.sizeOfPartialErrors() == 0) {
        	return (TCComponentForm)createresponse.output[0].objects[0];
        }
        com.teamcenter.rac.kernel.ServiceData servicedata = createresponse.serviceData;
        SoaUtil.handlePartialErrors(servicedata, null);
        return null;
	}
	
	public boolean isSuccess() {
		return (formComponent == null) ? false : true;
	}
}

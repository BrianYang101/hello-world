package t4dev.operations;

import t4dev.models.MyTableData;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.Markpoint;
import com.teamcenter.rac.kernel.SoaUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;

import dev.components.MyUtilities;

public class T4MyTableCreateOperation extends AbstractAIFOperation {
	protected TCSession session = null;
	protected TCComponent parent = null;
	protected MyTableData myTableData;
	protected TCComponent tableComponent;
	
	public T4MyTableCreateOperation(TCSession session, TCComponent parent, MyTableData tableData) {
		this.session = session;
		this.parent = parent;
		this.myTableData = tableData;
	}
	
	@Override
	public void executeOperation() throws Exception {
		Markpoint markpoint = null;
		try {
			markpoint = new Markpoint(session);
			tableComponent = createT4MyTable();
			markpoint.forget();
		} catch(Exception ex)
		{
			markpoint.rollBack();
			throw ex;
		}
	}

	public TCComponent createT4MyTable() throws Exception {
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse createresponse = null;
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn acreatein[] = null;
    	com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput createinput = null;

    	com.teamcenter.services.rac.core.DataManagementService datamanagementservice = com.teamcenter.services.rac.core.DataManagementService.getService(session);
    	acreatein = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn[1];
    	acreatein[0] = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn();
    	createinput = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput();
    	createinput.stringProps.put("t4Name", myTableData.name);
    	createinput.stringProps.put("t4Description", myTableData.description);
    	createinput.tagProps.put("t4Owner", MyUtilities.getUser(myTableData.owner));
    	createinput.boName = "T4MyTable";

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
		return (tableComponent == null) ? false : true;
	}
}

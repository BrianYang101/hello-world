package t4dev.operations;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import t4dev.forms.T4MyItemRevisionPanel;
import t4dev.models.MyTableData;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.Markpoint;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateOut;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;

import dev.components.MyUtilities;

public class T4MyItemCreateOperation extends AbstractAIFOperation {
	protected TCSession session = null;
	protected TCComponent parent = null;
	protected T4MyItemRevisionPanel itemRevPanel = null;
	protected TCComponentItem itemComponent = null;
	
	public T4MyItemCreateOperation(TCSession session, TCComponent parent, T4MyItemRevisionPanel itemRevPanel) {
		this.session = session;
		this.parent = parent;
		this.itemRevPanel = itemRevPanel;
	}
	
	@Override
	public void executeOperation() throws Exception {
		Markpoint markpoint = null;
		try {
			markpoint = new Markpoint(session);
			itemComponent = createT4MyItem();
			if (parent != null && itemComponent != null){
				parent.add("contents", itemComponent);
			}
			markpoint.forget();
		} catch(Exception ex)
		{
			markpoint.rollBack();
			throw ex;
		}
	}

	public TCComponentItem createT4MyItem() throws Exception
	{
		DataManagementService dmService = DataManagementService.getService(session);
		
		CreateIn itemDef = new CreateIn();
		CreateInput itemRevisionDef = new CreateInput();

		itemDef.data.boName = "T4MyItem";
		//itemDef.data.stringProps.put("item_id", "Item (item_id)");
		itemDef.data.stringProps.put("object_name", itemRevPanel.getObjectName());
		Calendar myCal = new GregorianCalendar();
		myCal.setTime(itemRevPanel.getDate());
		itemDef.data.dateProps.put("t4Date", myCal);

		itemRevisionDef.boName = "T4MyItemRevision";
		itemRevisionDef.doubleProps.put("t4Double", itemRevPanel.getDouble());
		itemRevisionDef.stringArrayProps.put("t4LOV", itemRevPanel.getLOV());
		TCComponent[] detailComponents = itemRevPanel.getDetails();
		itemRevisionDef.tagArrayProps.put("t4Details", itemRevPanel.getDetails());
		itemDef.data.compoundCreateInput.put("revision", new CreateInput[]{ itemRevisionDef });

		TCComponentItem item = null;
		TCComponentItemRevision itemRev = null;
		TCComponentForm form = null;
		
		try {
			CreateResponse createObjResponse = dmService.createObjects(new CreateIn[]{ itemDef });
			if(!ServiceDataError(createObjResponse.serviceData)) {
				for(CreateOut out : createObjResponse.output) {
					for(TCComponent obj : out.objects) {
						if(obj instanceof TCComponentItem)
							item = (TCComponentItem) obj;
						else if(obj instanceof TCComponentItemRevision)
							itemRev = (TCComponentItemRevision) obj;
						else if(obj instanceof TCComponentForm)
							form = (TCComponentForm) obj;
					}
				}
				
				Vector<MyTableData> tableDataRows = itemRevPanel.getMyTableData();
				if(tableDataRows != null && tableDataRows.size() > 0) {
					itemRev.lock();
					TCComponent[] tableComponents = new TCComponent[tableDataRows.size()];
					for (int k=0; k<tableDataRows.size(); k++) {
						TCComponent tableComponent = MyUtilities.createT4MyTable(session, tableDataRows.get(k));
						tableComponents[k] = tableComponent;
					}	
					TCProperty[] props = itemRev.getTCProperties(new String[] { "t4Table" });
					props[0].setReferenceValueArrayData(tableComponents);
					itemRev.setTCProperties(props);
					itemRev.save();
					itemRev.unlock();
				}
			}
		}
		catch (ServiceException e) 
		{
			e.printStackTrace();
		}
		
		return item;
	}
	
	protected boolean ServiceDataError(final ServiceData data)
	{
		if (data.sizeOfPartialErrors() > 0)	{
			for (int i = 0; i < data.sizeOfPartialErrors(); i++) {
				for (String msg : data.getPartialError(i).getMessages()) {
					System.out.println("ServiceDataError: " + msg);
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean isSuccess() {
		return (itemComponent == null) ? false : true;
	}
}

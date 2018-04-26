package t4dev.operations;

import java.util.List;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AifrcpPlugin;
import com.teamcenter.rac.aifrcp.SelectionHelper;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.BOCreatePropertyDescriptor;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.ui.commands.create.bo.NewBOModel;
import com.teamcenter.rac.ui.commands.create.bo.NewBOOperation;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.handlers.HandlerUtil;

import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.rac.core._2015_07.DataManagement;

public class NewT4MeetingActionItemOperation extends NewBOOperation {
    public NewT4MeetingActionItemOperation() {
    }
    
    public NewT4MeetingActionItemOperation(NewBOWizard newBOWizard, List<ICreateInstanceInput> list) {
        this.setWizard(newBOWizard);
        this.setCreateInput(list);
    }
    
    @Override
    protected List<TCComponent> performCreate(List<ICreateInstanceInput> list) throws TCException {
    	System.out.println("NewT4MeetingActionItemOperation: performCreate");
        IBOCreateDefinition iBOCreateDefinition = this.wizard.getBOModel().getCreateDef();
        NewBOModel newBOModel = this.wizard.getBOModel();
        CreateInput createInput = SOAGenericCreateHelper.getCreateInput(iBOCreateDefinition, new ArrayList<ICreateInstanceInput>(list));
        String actionName = (String) createInput.stringProps.get("t4ActionName");
        Calendar dueDate = (GregorianCalendar)createInput.dateProps.get("t4DueDate");
		try {
			//ISessionService iss = AifrcpPlugin.getSessionService();

			TCUserService userService = ((TCSession)this.getSession()).getUserService();
			InterfaceAIFComponent[] selected_objects = newBOModel.getTargetArray();
            	TCComponent aComponent = (TCComponent)selected_objects[0];
	            int n_input_args = 3; 
	            Object [] input_args = new Object[n_input_args];
	            Date now = new Date();
	            input_args[0] = aComponent;
	            input_args[1] = actionName;
	            input_args[2] = dueDate.getTime();
	            userService.call("T4MeetingMinuteForm_add_T4ActionItem", input_args);

	            aComponent.refresh();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		
		return null;
        //return SOAGenericCreateHelper.createRelateAndSubmitToWorkflow((TCSession)newBOModel.getSession(), (IBOCreateDefinition)iBOCreateDefinition, list, (Map)newBOModel.getDataToBeRelated(), (Map)newBOModel.getWorkFlowData(), (TCComponent)((TCComponent)newBOModel.getTargetArray()[0]), (String)newBOModel.getRelType());
    }
}

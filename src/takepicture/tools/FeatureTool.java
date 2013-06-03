package takepicture.tools;

import java.io.IOException;

import takepicture.model.Picture;

import com.esri.arcgis.geodatabase.IAttachmentManager;
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IDatasetProxy;
import com.esri.arcgis.geodatabase.IFeature;
import com.esri.arcgis.geodatabase.IFeatureClass;
import com.esri.arcgis.geodatabase.ITableAttachments;
import com.esri.arcgis.geodatabase.ITableAttachmentsProxy;
import com.esri.arcgis.geodatabase.IWorkspace;
import com.esri.arcgis.geodatabase.IWorkspaceEdit;
import com.esri.arcgis.geodatabase.IWorkspaceEditProxy;
import com.esri.arcgis.geometry.esriGeometryType;
import com.esri.arcgis.interop.AutomationException;

public class FeatureTool {
	
	public static IWorkspaceEdit startEdit(IFeatureClass featureClass) throws AutomationException, IOException
	{
		IDataset dataset = new IDatasetProxy(featureClass);
        IWorkspace workspace = dataset.getWorkspace();
        IWorkspaceEdit workspaceEdit = new IWorkspaceEditProxy(workspace);

        workspaceEdit.startEditing(false);
        workspaceEdit.startEditOperation();
        return workspaceEdit;
	}
	
	public static void stopEdit(IWorkspaceEdit workspaceEdit) throws AutomationException, IOException
	{
		workspaceEdit.stopEditOperation();
        workspaceEdit.stopEditing(true);
	}
	
	public static void add(IFeatureClass featureClass,Picture picture) throws Exception
	{
		// Ensure the feature class contains points.
	    if (featureClass.getShapeType() != esriGeometryType.esriGeometryPoint){
	        throw new Exception("Feature class contains not points.");
	    }
	    
	    IWorkspaceEdit workspaceEdit = startEdit(featureClass);
	    
	    try
	    {
		    //FEATURE
			IFeature feature = featureClass.createFeature();
			feature.setShapeByRef(picture.point);
			feature.setValue(featureClass.findField("text"),picture.comment);
			feature.store();
			
			//ATTACHMENT
			int oid = feature.getOID();
	
			ITableAttachments tableAttachments = new ITableAttachmentsProxy(featureClass);
			IAttachmentManager attachmentManager = tableAttachments.getAttachmentManager();
			
			attachmentManager.addAttachment(oid, picture.attachment);
	    }
	    catch(Exception e)
	    {
	    	throw new Exception("ERROR Add feature: "+e.getMessage());
	    }
	    finally
	    {
	    	stopEdit(workspaceEdit);
	    }
	}
}

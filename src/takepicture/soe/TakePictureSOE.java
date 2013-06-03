package takepicture.soe;

/*
 COPYRIGHT 1995-2012 ESRI
 TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
 Unpublished material - all rights reserved under the 
 Copyright Laws of the United States and applicable international
 laws, treaties, and conventions.

 For additional information, contact:
 Environmental Systems Research Institute, Inc.
 Attn: Contracts and Legal Services Department
 380 New York Street
 Redlands, California, 92373
 USA

 email: contracts@esri.com
 */
import java.io.IOException;

import com.esri.arcgis.geodatabase.FeatureClass;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.interop.extn.ArcGISExtension;
import com.esri.arcgis.server.IServerObjectExtension;
import com.esri.arcgis.server.IServerObjectHelper;
import com.esri.arcgis.system.ILog;
import com.esri.arcgis.system.ServerUtilities;
import com.esri.arcgis.interop.extn.ServerObjectExtProperties;
import com.esri.arcgis.carto.IMapServerDataAccess;
import com.esri.arcgis.system.IObjectConstruct;
import com.esri.arcgis.system.IPropertySet;
import com.esri.arcgis.server.json.JSONArray;
import com.esri.arcgis.server.json.JSONException;
import com.esri.arcgis.server.json.JSONObject;
import com.esri.arcgis.system.IRESTRequestHandler;
import java.util.HashMap;

import takepicture.model.Picture;
import takepicture.tools.FeatureTool;
import takepicture.tools.SOELogger;
import takepicture.tools.UploadTool;

@ArcGISExtension
@ServerObjectExtProperties(displayName = "Take Picture SOE", description = "Take Picture", properties = { "serverPhpUrl=http://" })
public class TakePictureSOE implements IServerObjectExtension,IObjectConstruct, IRESTRequestHandler 
{
	
	private static final long serialVersionUID = 1L;
	private SOELogger logger;
	private IMapServerDataAccess mapServerDataAccess;

	public TakePictureSOE() throws Exception {
		super();
	}

	public void init(IServerObjectHelper soh) throws IOException, AutomationException 
	{
		this.logger = new SOELogger(ServerUtilities.getServerLogger(), 8001);

		this.mapServerDataAccess = (IMapServerDataAccess) soh.getServerObject();
		
		this.logger.debug("Initialized " + this.getClass().getName() + " SOE.");
	}

	public void shutdown() throws IOException, AutomationException 
	{
		this.logger.debug( "Shutting down " + this.getClass().getName() + " SOE.");
		this.logger = null;
		this.mapServerDataAccess = null;
	}

	public void construct(IPropertySet propertySet) throws IOException 
	{

	}

	private byte[] add(JSONObject operationInput, String outputFormat,JSONObject requestPropertiesJSON,java.util.Map<String, String> responseProperties) throws Exception 
	{
		JSONObject jsonResponse = new JSONObject();
		
		try
		{
			this.logger.debug(operationInput.toString());
			
			//1. Get feature class
			FeatureClass fc = new FeatureClass(this.mapServerDataAccess.getDataSource("", 0));
			
			//2. Create picture object
			Picture picture = new Picture(logger,(JSONObject)operationInput.get("point"),operationInput.getString("comment"),operationInput.getString("picture"));
			
			//3. Add picture and attachment
			FeatureTool.add(fc, picture);
			
			//4. Send picture to a other server. 
			UploadTool.upload(picture);
			
			
			jsonResponse.put("msg", "Success");
			return jsonResponse.toString(4).getBytes("utf-8");
		}
		catch(Exception ex)
		{
			jsonResponse.put("msg","Error add picture:"+ex.getMessage());
			return jsonResponse.toString(4).getBytes("utf-8");
		}
	}

	private byte[] getRootResource(String outputFormat,
			JSONObject requestPropertiesJSON,
			java.util.Map<String, String> responsePropertiesMap)
			throws Exception 
	{
		JSONObject json = new JSONObject();
		json.put("name", "root resource");
		json.put("description", "TakePictureSOE description");
		return json.toString().getBytes("utf-8");
	}

	private byte[] getResource(String capabilitiesList, String resourceName,
			String outputFormat, JSONObject requestPropertiesJSON,
			java.util.Map<String, String> responsePropertiesMap)
			throws Exception 
	{
		if (resourceName.equalsIgnoreCase("") || resourceName.length() == 0) {
			return getRootResource(outputFormat, requestPropertiesJSON,
					responsePropertiesMap);
		}

		return null;
	}

	private byte[] invokeRESTOperation(String capabilitiesList,
			String resourceName, String operationName, String operationInput,
			String outputFormat, JSONObject requestPropertiesJSON,
			java.util.Map<String, String> responsePropertiesMap)
			throws Exception 
	{
		JSONObject operationInputAsJSON = new JSONObject(operationInput);
		byte[] operationOutput = null;

		//permitted capabilities list can be used to allow/block access to operations

		if (resourceName.equalsIgnoreCase("") || resourceName.length() == 0) {
			if (operationName.equalsIgnoreCase("add")) {
				operationOutput = add(operationInputAsJSON, outputFormat,
						requestPropertiesJSON, responsePropertiesMap);
			}
		} else //if non existent sub-resource specified, report error
		{
			return ServerUtilities.sendError(0,
					"No sub-resource by name " + resourceName + " found.",
					new String[] { "" }).getBytes("utf-8");
		}

		return operationOutput;
	}
	
	public byte[] handleRESTRequest(String capabilities, String resourceName,
			String operationName, String operationInput, String outputFormat,
			String requestProperties, String[] responseProperties)
			throws IOException, AutomationException {
		// parse request properties, create a map to hold request properties
		JSONObject requestPropertiesJSON = new JSONObject(requestProperties);

		// create a response properties map to hold properties of response
		java.util.Map<String, String> responsePropertiesMap = new HashMap<String, String>();

		try {
			// if no operationName is specified send description of specified
			// resource
			byte[] response;
			if (operationName.length() == 0) {
				response = getResource(capabilities, resourceName,
						outputFormat, requestPropertiesJSON,
						responsePropertiesMap);
			} else
			// invoke REST operation on specified resource
			{
				response = invokeRESTOperation(capabilities, resourceName,
						operationName, operationInput, outputFormat,
						requestPropertiesJSON, responsePropertiesMap);
			}

			// handle response properties
			JSONObject responsePropertiesJSON = new JSONObject(
					responsePropertiesMap);
			responseProperties[0] = responsePropertiesJSON.toString();

			return response;
		} catch (Exception e) {
			String message = "Exception occurred while handling REST request for SOE "
					+ this.getClass().getName() + ":" + e.getMessage();
			logger.error(message);
			return ServerUtilities.sendError(0, message, null)
					.getBytes("utf-8");
		}
	}

	/**
	 * This method returns the resource hierarchy of a REST based SOE in JSON format.
	 */
	public String getSchema() throws IOException, AutomationException {
		try {
			JSONObject _TakePictureSOE = ServerUtilities.createResource(
					"TakePictureSOE", "TakePictureSOE description", false,
					false);
			JSONArray _TakePictureSOE_OpArray = new JSONArray();
			_TakePictureSOE_OpArray.put(ServerUtilities.createOperation("add",
					"point,picture,comment", "json", true));
			_TakePictureSOE.put("operations", _TakePictureSOE_OpArray);
			return _TakePictureSOE.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

}
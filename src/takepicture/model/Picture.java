package takepicture.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import takepicture.tools.Base64;
import takepicture.tools.ProjectTool;
import takepicture.tools.SOELogger;

import com.esri.arcgis.geodatabase.Attachment;
import com.esri.arcgis.geodatabase.IAttachment;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.Point;
import com.esri.arcgis.server.json.JSONObject;
import com.esri.arcgis.system.IMemoryBlobStream;
import com.esri.arcgis.system.MemoryBlobStream;

public class Picture {
	
	private SOELogger logger;
	
	public IPoint point;
	public String comment;
	public String picture;
	public IAttachment attachment;
	public File file;
	
	public Picture(SOELogger logger,JSONObject jsonPoint,String comment, String picture) throws UnknownHostException, IOException
	{
		logger.debug("PICTURE CONSTRUCTOR");
		
		this.comment = comment;
		this.picture = picture;
		
		//POINT
		this.point = new Point();
		this.point.setX(jsonPoint.getDouble("x"));
		this.point.setY(jsonPoint.getDouble("y"));
		this.point = ProjectTool.project(this.point);
		
		//ATTACHMENT
		String uniqueId = UUID.randomUUID().toString();
		
		byte[] decodedString = Base64.decode(this.picture);

		FileOutputStream fos = new FileOutputStream("c:\\temp\\"+uniqueId+".jpg");
		fos.write(decodedString);
		fos.close();
		
		IMemoryBlobStream memoryBlobStream = new MemoryBlobStream();
		memoryBlobStream.loadFromFile("c:\\temp\\"+uniqueId+".jpg");

		this.file = new File("c:\\temp\\"+uniqueId+".jpg");
		
		// Create an attachment.
		this.attachment = new Attachment();
		this.attachment.setDataByRef(memoryBlobStream);
		this.attachment.setContentType("image/jpeg");
		this.attachment.setName(uniqueId+".jpg");
	}
	
}

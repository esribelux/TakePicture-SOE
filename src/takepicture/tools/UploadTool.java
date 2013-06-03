package takepicture.tools;

import java.io.IOException;
import java.util.List;

import takepicture.model.Picture;

public class UploadTool {

	public static void  upload(Picture picture) throws Exception
	{
		try
		{
			MultipartUtility multipart = new MultipartUtility("http://2013.esribelux.eu/send.php", "UTF8");
	        multipart.addFormField("lat", Double.toString(picture.point.getY()));
	        multipart.addFormField("long", Double.toString(picture.point.getX()));
	        multipart.addFormField("comment", picture.comment);
	        multipart.addFilePart("image", picture.file);
	        List<String> response = multipart.finish();
		}
		catch(IOException ex)
		{
			throw new Exception("ERROR Upload picture: "+ex.getMessage());
		}
	}

}

package takepicture.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
	public static String now()
	{
		String dateNow = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
		return dateNow;
	}
}

package takepicture.tools;

import java.io.IOException;

import com.esri.arcgis.geometry.IGeographicCoordinateSystem;
import com.esri.arcgis.geometry.IPoint;
import com.esri.arcgis.geometry.IProjectedCoordinateSystem;
import com.esri.arcgis.geometry.ISpatialReference;
import com.esri.arcgis.geometry.ISpatialReferenceFactory;
import com.esri.arcgis.geometry.SpatialReferenceEnvironment;
import com.esri.arcgis.geometry.esriSRGeoCSType;
import com.esri.arcgis.interop.AutomationException;

public class ProjectTool {

	public static IPoint project(IPoint point) throws AutomationException, IOException
	{
		ISpatialReferenceFactory srFactory = new SpatialReferenceEnvironment();
		IProjectedCoordinateSystem pcs = srFactory.createProjectedCoordinateSystem(3857);
		ISpatialReference srWebMercator = pcs;
		
		IGeographicCoordinateSystem wgs84 = srFactory.createGeographicCoordinateSystem((int)esriSRGeoCSType.esriSRGeoCS_WGS1984);
		ISpatialReference srWgs84 = wgs84;
		
		point.setSpatialReferenceByRef(srWebMercator);
		point.project(srWgs84);
		
		return point;
	}
}

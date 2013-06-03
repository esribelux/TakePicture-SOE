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
import java.util.HashMap;
import java.util.Set;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.interop.extn.ArcGISCategories;
import com.esri.arcgis.interop.extn.ArcGISExtension;
import com.esri.arcgis.server.BaseSOEPropertyPage;

@ArcGISExtension(categories=ArcGISCategories.AGSExtensionParameterPage)
public class TakePictureSOEPropPage extends BaseSOEPropertyPage implements ActionListener {

	private static final long serialVersionUID = 1L;
	private String soeName = "TakePictureSOE";
	private HashMap<String, JTextField> textfields;
	
	public void apply(HashMap soProps, HashMap extnProps) throws IOException, AutomationException {
		Set<String> keys = extnProps.keySet();
		for (String key : keys) {
			JTextField textfield = (JTextField) textfields.get(key);
			String value = textfield.getText();
			extnProps.put(key, value);
		}
	}
	
	// Return the SOE type you're implementing
	public String getServerObjectExtensionType() throws IOException, AutomationException {
		return soeName;
	}
	
	/* Return the type of a Server Object such as 
	 * MapServer, GeocodeServer, GeodataServer, GeometryServer, GlobeServer
	 * GeoprocessingServer, ImageServer
	 */
	public String getServerObjectType() throws IOException, AutomationException {
		return "MapServer";
	}

	public JFrame initGUI(HashMap soProps, HashMap extnProps) {
		initProps(extnProps);
		JFrame frame = new JFrame(soeName + " Property Page");
		frame.getContentPane().setLayout(new GridBagLayout());
		textfields = new HashMap<String, JTextField>();
		Set<String> keys = extnProps.keySet();
		int index = 0;
		for (String key : keys) {
			JTextField textfield = createPropertyField(frame.getContentPane(), key, (String) extnProps.get(key), index++);
			textfields.put(key, textfield);
		}
		frame.setSize(300, 300);
		return frame;
	}
	
	/**
	 * Creates a pair of JLabel and JTextField representing each property.
	 * 
	 * @param panel - container of the controls
	 * @param name - property name
	 * @param value - the default property value
	 * @param propertyindex - property index
	 * @return textfield
	 */
	private JTextField createPropertyField(Container panel, String name, String value, int propertyindex) {
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0 ;
		c1.gridy = propertyindex;
		c1.insets = new Insets(5,5,5,5);
		
		JLabel label = new JLabel();
		label.setText(name);
		panel.add(label, c1);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = propertyindex;
		c2.weightx = 1;
		c2.insets = new Insets(5,5,5,5);
		c2.fill = GridBagConstraints.HORIZONTAL;
		
		JTextField text = new JTextField(15);
		panel.add(text, c2);
		text.setText(value);
		text.addActionListener(this);
		
		return text;
	}

	void initProps(HashMap extnProps) {
		if ( extnProps.size() > 0 ) return;
		extnProps.put("serverPhpUrl", "http://");
	}
	
	public void actionPerformed(ActionEvent e) {
		try {
			pageChanged();
		} catch (AutomationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
package bdv.jogl.VolumeRenderer.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bdv.jogl.VolumeRenderer.Scene.VolumeDataScene;
import bdv.jogl.VolumeRenderer.ShaderPrograms.MultiVolumeRenderer;
import bdv.jogl.VolumeRenderer.ShaderPrograms.ShaderSources.functions.volumeninterpreter.IsoSurfaceVolumeInterpreter;
import bdv.jogl.VolumeRenderer.ShaderPrograms.ShaderSources.functions.volumeninterpreter.MaximumIntensityProjectionInterpreter;
import bdv.jogl.VolumeRenderer.ShaderPrograms.ShaderSources.functions.volumeninterpreter.TransparentVolumeinterpreter;
import bdv.jogl.VolumeRenderer.TransferFunctions.TransferFunction1D;
import bdv.jogl.VolumeRenderer.TransferFunctions.sampler.PreIntegrationSampler;
import bdv.jogl.VolumeRenderer.TransferFunctions.sampler.RegularSampler;
import bdv.jogl.VolumeRenderer.gui.GLWindow.GLWindow;
import bdv.jogl.VolumeRenderer.gui.TFDataPanel.TransferFunctionDataPanel;
import bdv.jogl.VolumeRenderer.gui.TFDrawPanel.TransferFunctionDrawPanel;
import bdv.jogl.VolumeRenderer.gui.VDataAggregationPanel.AggregatorManager;
import bdv.jogl.VolumeRenderer.gui.VDataAggregationPanel.VolumeDataAggregationPanel;
import bdv.jogl.VolumeRenderer.utils.VolumeDataManager;
import bdv.jogl.VolumeRenderer.utils.VolumeDataManagerAdapter;

/**
 * Class for providing tf scene controls
 * @author michael
 *
 */
public class SceneControlsWindow extends JFrame {

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;

	private TransferFunctionDrawPanel tfpanel = null;

	private final JPanel mainPanel  = new JPanel();

	private TransferFunctionDataPanel tfDataPanel = null;

	private TransferFunction1D transferFunction;

	private VolumeDataAggregationPanel aggregationPanel;

	private JCheckBox usePreIntegration = new JCheckBox("Use pre-integration",false);

	private JCheckBox advancedCheck = new JCheckBox("Advanced configurations",false);

	private JSpinner isoValueSpinner = new JSpinner();

	private JButton backgroundColorButton = new JButton("");

	private JPanel backgroundPanel = new JPanel();

	private final VolumeDataManager dataManager;

	private final JCheckBox rectBorderCheck = new JCheckBox("Show volume Borders",false);

	private final MultiVolumeRenderer renderer;

	private final VolumeDataScene scene;

	private final GLWindow drawWindow;

	//interpreter panels

	private final JPanel isoPanel = new JPanel();

	private final JPanel volumeInterpreterPanel = new JPanel(); 

	private final ButtonGroup volumenInterpreterGroup = new ButtonGroup();

	private final JRadioButton emissionsAbsorbationRadioButton = new JRadioButton("Emission absorbation");

	private final JRadioButton isoRadioButton = new JRadioButton("Isosurface");

	private final JRadioButton maximumIntensityProjectionRadioButton = new JRadioButton("Maximum intensity projection");

	private final JCheckBox showSlice = new JCheckBox("Show slice in 3D View", true);

	private final JPanel samplePanel = new JPanel();

	private final JSpinner sampleSpinner = new JSpinner(new SpinnerNumberModel(256, 1, 10000, 1));

	private final JCheckBox useGradient = new JCheckBox("Use gradients as values",false);

	private final JButton resetButton = new JButton("Reset to full volume view"); 

	public SceneControlsWindow(
			final TransferFunction1D tf,
			final AggregatorManager agm, 
			final VolumeDataManager dataManager, 
			final MultiVolumeRenderer mvr, 
			final GLWindow win,
			final VolumeDataScene scene){
		this.scene = scene;
		this.drawWindow = win;
		this.renderer = mvr;
		transferFunction = tf;
		this.dataManager = dataManager;
		createTFWindow(tf,agm,dataManager);
	}

	private void addComponetenToMainPanel(JComponent c){
		c.setAlignmentX(LEFT_ALIGNMENT);
		mainPanel.add(c);
	}

	private void createTFWindow(final TransferFunction1D tf,final AggregatorManager agm,final VolumeDataManager dataManager){
		tfpanel = new TransferFunctionDrawPanel(tf,dataManager);
		tfDataPanel = new TransferFunctionDataPanel(tf);
		aggregationPanel = new VolumeDataAggregationPanel(agm);


		setTitle("Transfer function configurations");
		setSize(640, 100);
		initAdvancedBox();
		initBackgroundPanel();
		initUsePreIntegration();
		initShowIsoSurface();
		initBorderCheck();
		initShowSlice();
		initSampleSpinner();
		initUseGradient();
		initResetButton();
		initVolumeInterpreterPanel();

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		addComponetenToMainPanel(tfpanel);
		addComponetenToMainPanel(advancedCheck);
		addComponetenToMainPanel(tfDataPanel);
		addComponetenToMainPanel(samplePanel);
		addComponetenToMainPanel(rectBorderCheck);
		addComponetenToMainPanel(showSlice);
		addComponetenToMainPanel(resetButton);
		addComponetenToMainPanel(backgroundPanel);
		addComponetenToMainPanel(usePreIntegration);
		addComponetenToMainPanel(volumeInterpreterPanel);
		//TOOD addComponetenToMainPanel(isoPanel);

		addComponetenToMainPanel(aggregationPanel);
		addComponetenToMainPanel(useGradient);


		tfDataPanel.setVisible(advancedCheck.isSelected());

		getContentPane().add(mainPanel);
		pack();
	}


	private void initVolumeInterpreterPanel() {
		volumeInterpreterPanel.setBorder(BorderFactory.createTitledBorder("Volume interpreters"));
		volumeInterpreterPanel.setLayout(new BoxLayout(volumeInterpreterPanel, BoxLayout.Y_AXIS));

		emissionsAbsorbationRadioButton.setSelected(true);

		emissionsAbsorbationRadioButton.setAlignmentX(LEFT_ALIGNMENT);
		maximumIntensityProjectionRadioButton.setAlignmentX(LEFT_ALIGNMENT);
		isoPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		volumenInterpreterGroup.add(emissionsAbsorbationRadioButton);
		volumenInterpreterGroup.add(isoRadioButton);
		volumenInterpreterGroup.add(maximumIntensityProjectionRadioButton);

		volumeInterpreterPanel.add(emissionsAbsorbationRadioButton);
		volumeInterpreterPanel.add(isoPanel);
		volumeInterpreterPanel.add(maximumIntensityProjectionRadioButton);

	}


	private void initResetButton() {
		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setUseSparseVolumes(false);
				dataManager.resetVolumeData();

			}
		});

	}

	public void updateUseGradient(){
		renderer.setUseGradient(this.useGradient.isSelected());
		drawWindow.getGlCanvas().repaint();
	}
	private void initUseGradient() {
		updateUseGradient();
		useGradient.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				updateUseGradient();
			}
		});

	}

	private void initSampleSpinner() {
		samplePanel.setLayout(new BoxLayout(samplePanel, BoxLayout.X_AXIS));
		samplePanel.add(new JLabel("Render samples: "));
		samplePanel.add(sampleSpinner);

		updateSamples();
		sampleSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateSamples();	
			}
		});
	}

	private void updateSamples() {
		renderer.setSamples(((Number) sampleSpinner.getValue()).intValue());
		drawWindow.getGlCanvas().repaint();
	}

	private void initShowSlice() {
		updateSlice();
		showSlice.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				updateSlice();
			}
		});

	}

	private void updateSlice() {
		renderer.setSliceShown(showSlice.isSelected());
		drawWindow.getGlCanvas().repaint();

	}

	private void updateBorderStatus(){
		scene.enableVolumeBorders(rectBorderCheck.isSelected());
		drawWindow.getGlCanvas().repaint();
	}

	private void initBorderCheck() {
		updateBorderStatus();
		rectBorderCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				updateBorderStatus();
			}
		});

	}

	private void updateBackgroundColors(Color c){
		backgroundColorButton.setBackground(c);
		renderer.setBackgroundColor(c);
		drawWindow.getScene().setBackgroundColor(c);
		drawWindow.getGlCanvas().repaint();
	}

	private void initBackgroundPanel() {

		backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.X_AXIS));
		backgroundPanel.add(new JLabel("Background color: ") );
		backgroundPanel.add(backgroundColorButton);
		updateBackgroundColors( drawWindow.getScene().getBackgroundColor());

		backgroundColorButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(new JFrame(), "color dialog", backgroundColorButton.getBackground());

				if(color == null && !drawWindow.getScene().getBackgroundColor().equals(color)){
					return;
				}

				updateBackgroundColors(color);
			}
		});

	}

	private void changeVolumeInterpreter(){
		if(isoRadioButton.isSelected()){
			renderer.getSource().setVolumeInterpreter(new IsoSurfaceVolumeInterpreter());
		}

		if(emissionsAbsorbationRadioButton.isSelected()){
			renderer.getSource().setVolumeInterpreter(new TransparentVolumeinterpreter());
		}

		if(maximumIntensityProjectionRadioButton.isSelected()){
			renderer.getSource().setVolumeInterpreter(new MaximumIntensityProjectionInterpreter());
		}

	}
	private void updateIsoSurface(){
		renderer.setIsoSurface(((Number)isoValueSpinner.getValue()).floatValue());
	}

	private void initShowIsoSurface() {
		isoValueSpinner.setModel(new SpinnerNumberModel(0.0,0.0, 10000, 1.0f));
		dataManager.addVolumeDataManagerListener(new VolumeDataManagerAdapter() {

			@Override
			public void dataUpdated(Integer i) {
				float maxVolume=Math.min(dataManager.getGlobalMaxVolumeValue(),1000);
				transferFunction.setMaxOrdinates(new Point2D.Float(maxVolume, 1.0f));

			}

			@Override
			public void dataEnabled(Integer i, Boolean flag) {
				drawWindow.getGlCanvas().repaint();
			}
		});	

		changeVolumeInterpreter();
		isoRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeVolumeInterpreter();
				drawWindow.getGlCanvas().repaint();
			}
		});

		emissionsAbsorbationRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeVolumeInterpreter();
				drawWindow.getGlCanvas().repaint();
			}
		});

		maximumIntensityProjectionRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				changeVolumeInterpreter();
				drawWindow.getGlCanvas().repaint();
			}
		});

		updateIsoSurface();
		isoValueSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateIsoSurface();
				drawWindow.getGlCanvas().repaint();
			}
		});

		isoPanel.setLayout(new BoxLayout(isoPanel, BoxLayout.X_AXIS));
		isoPanel.add(isoRadioButton);
		isoPanel.add(isoValueSpinner);

	}

	private void changeTransferfuntionSampler(){
		if(usePreIntegration.isSelected()){
			transferFunction.setSampler(new PreIntegrationSampler());
		}else{
			transferFunction.setSampler(new RegularSampler());
		}
	}

	private void initUsePreIntegration() {
		changeTransferfuntionSampler();
		usePreIntegration.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				changeTransferfuntionSampler();
			}
		});
	}

	private void initAdvancedBox() {
		advancedCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				tfDataPanel.setVisible(advancedCheck.isSelected());
				pack();

			}
		});

	}

	public void destroyTFWindow() {
		dispose();
		tfpanel = null;
	}
}

package bdv.jogl.VolumeRenderer.utils;

import javax.swing.JSpinner;

import bdv.jogl.VolumeRenderer.ShaderPrograms.MultiVolumeRenderer;
import bdv.jogl.VolumeRenderer.gui.GLWindow.GLWindow;

/**
 * 
 * @author michael
 *
 */
public class VolumeRendereSampleController {

	private int lowSamples = 64;

	private final JSpinner sampleSpinner;

	private final MultiVolumeRenderer volumeRenderer;

	private final GLWindow window;

	private int sampleRequests = 0;

	private final Object lock = new Object();

	private boolean active = true;

	public VolumeRendereSampleController(
			final GLWindow window,
			final JSpinner sampleSpinner,
			final MultiVolumeRenderer volumeRenderer,
			int lowSampleSize) {
		super();
		this.sampleSpinner = sampleSpinner;
		this.volumeRenderer = volumeRenderer;
		this.window = window;
		this.lowSamples = lowSampleSize;
	}


	public void upSample(){
		synchronized(lock){
			if(active){
				sampleRequests--;

				if(sampleRequests == 0){

					int chosenSamples = ((Number)sampleSpinner.getValue()).intValue();
					volumeRenderer.setSamples(chosenSamples);
					window.getGlCanvas().repaint();
				}
			}
		}
	}

	public synchronized void downSample(){
		synchronized (lock) {
			if(active){
				volumeRenderer.setSamples(lowSamples);
				sampleRequests++;
			}
		}
	}

	/**
	 * @return the lowSamples
	 */
	public int getLowSamples() {
		return lowSamples;
	}

	/**
	 * @param lowSamples the lowSamples to set
	 */
	public void setLowSamples(int lowSamples) {
		this.lowSamples = lowSamples;
	}


	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		if(active == this.active){
			return;
		}
		this.active = active;
		sampleRequests = 0;
	}

}
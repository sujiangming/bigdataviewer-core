package viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.imglib2.ui.util.GuiUtil;
import viewer.ViewerPanel.Options;
import viewer.gui.InputActionBindings;
import viewer.hdf5.img.Hdf5GlobalCellCache;
import viewer.render.SourceAndConverter;

/**
 * A {@link JFrame} containing a {@link ViewerPanel} and associated
 * {@link InputActionBindings}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ViewerFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	protected final ViewerPanel viewer;

	private final InputActionBindings keybindings;

	/**
	 * Create default {@link Options}.
	 * @return default {@link Options}.
	 */
	public static Options options()
	{
		return new Options();
	}

	public ViewerFrame(
			final int width, final int height,
			final List< SourceAndConverter< ? > > sources,
			final int numTimePoints,
			final Hdf5GlobalCellCache< ? > cache )
	{
		this( width, height, sources, numTimePoints, cache, options() );
	}

	/**
	 *
	 * @param width
	 *            width of the display window.
	 * @param height
	 *            height of the display window.
	 * @param sources
	 *            the {@link SourceAndConverter sources} to display.
	 * @param numTimePoints
	 *            number of available timepoints.
	 * @param cache
	 *            handle to cache. This is used to control io timing. Also, is
	 *            is used to subscribe / {@link #stop() unsubscribe} to the
	 *            cache as a consumer, so that eventually the io fetcher threads
	 *            can be shut down.
	 * @param optional
	 *            optional parameters. See {@link #options()}.
	 */
	public ViewerFrame(
			final int width, final int height,
			final List< SourceAndConverter< ? > > sources,
			final int numTimePoints,
			final Hdf5GlobalCellCache< ? > cache,
			final Options optional )
	{
//		super( "BigDataViewer", GuiUtil.getSuitableGraphicsConfiguration( GuiUtil.ARGB_COLOR_MODEL ) );
		super( "BigDataViewer", GuiUtil.getSuitableGraphicsConfiguration( GuiUtil.RGB_COLOR_MODEL ) );
		viewer = new ViewerPanel( sources, numTimePoints, cache, optional.width( width ).height( height ) );
		keybindings = new InputActionBindings();
		new NavigationKeyHandler( keybindings, viewer );

		getRootPane().setDoubleBuffered( true );
		setPreferredSize( new Dimension( width, height ) );
		add( viewer, BorderLayout.CENTER );
		pack();
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( final WindowEvent e )
			{
				viewer.stop();
			}
		} );

		SwingUtilities.replaceUIActionMap( getRootPane(), keybindings.getConcatenatedActionMap() );
		SwingUtilities.replaceUIInputMap( getRootPane(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, keybindings.getConcatenatedInputMap() );
	}

	// TODO: remove!?
//	public void addHandler( final Object handler )
//	{
//		viewer.addHandler( handler );
//		if ( KeyListener.class.isInstance( handler ) )
//			addKeyListener( ( KeyListener ) handler );
//	}

	public ViewerPanel getViewerPanel()
	{
		return viewer;
	}

	public InputActionBindings getKeybindings()
	{
		return keybindings;
	}
}

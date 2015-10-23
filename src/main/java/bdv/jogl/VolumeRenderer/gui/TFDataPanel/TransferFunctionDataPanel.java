package bdv.jogl.VolumeRenderer.gui.TFDataPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;












import bdv.jogl.VolumeRenderer.TransferFunctions.TransferFunction1D;
import bdv.jogl.VolumeRenderer.TransferFunctions.TransferFunctionAdapter;

/**
 * direct data manipulation panel
 * @author michael
 *
 */
public class TransferFunctionDataPanel extends JPanel {

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;

	private TransferFunction1D transferFunction;

	private JScrollPane colorTableScroller;

	private final JTable colorTable = new JTable();

	private BoxLayout mainLayout = new BoxLayout(this, BoxLayout.Y_AXIS);

	private void initUI(){			
		colorTableScroller.setPreferredSize(new Dimension(this.getWidth(),100));

		setLayout(mainLayout);		
		add(colorTableScroller);	
	}

	private void updateData(){

	
		updateColors();
	}


	private void updateColors() {

		final TreeMap<Point2D.Float, Color> colors = transferFunction.getColors();

		final DefaultTableModel model =new DefaultTableModel(new String[]{"Color position","Colors"},0);
		for(Point2D.Float position: colors.keySet()){
			Color color = colors.get(position);
			model.addRow(new Object[]{position,color});

		}

		colorTable.setModel(model);
		model .addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {

				if(e.getType() == TableModelEvent.UPDATE){

					//color changed
					if(e.getColumn() == 1){
						int row = e.getFirstRow();
						Point2D.Float colorPosition = (Point2D.Float) model.getValueAt(row, 0);
						Color newColor = (Color) model.getValueAt(e.getFirstRow(), 1); 
						transferFunction.setColor(colorPosition, newColor);

					}

					//points changed TODO
					if(e.getColumn() == 0){
						Point2D.Float[] newPoints = new Point2D.Float[colors.size()];
						Point2D.Float[] oldPoints = new Point2D.Float[colors.size()];

						colors.keySet().toArray(newPoints);
						transferFunction.getColors().keySet().toArray(oldPoints);
						transferFunction.moveColor(oldPoints[e.getFirstRow()],newPoints[e.getFirstRow()]);
					}
				}
			}
		});

		ColorCellEditor colorEditor = new ColorCellEditor();		
		PointCellEditor pointEditor = new PointCellEditor(this);

		colorTable.getColumnModel().getColumn(0).setCellEditor(pointEditor);
		colorTable.getColumnModel().getColumn(0).setCellRenderer(pointEditor);
		colorTable.getColumnModel().getColumn(0).setPreferredWidth(500);
		colorTable.getColumnModel().getColumn(0).setMinWidth(500);
		colorTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		//add button
		colorTable.getColumnModel().getColumn(1).setCellEditor(colorEditor);
		colorTable.getColumnModel().getColumn(1).setCellRenderer(colorEditor);

	}

	public void setTransferFunction(TransferFunction1D tf){
		transferFunction = tf;
		transferFunction.addTransferFunctionListener(new TransferFunctionAdapter() {


			@Override
			public void colorChanged(TransferFunction1D transferFunction) {
				updateData();

			}
		});
	}

	public TransferFunction1D getTransferFunction(){
		return transferFunction;
	}
	
	public TransferFunctionDataPanel(final TransferFunction1D tf){

		colorTableScroller = new JScrollPane(colorTable);
		initUI();

		setTransferFunction(tf);
	}
}
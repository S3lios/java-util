package javax.swing.plot;

import javax.swing.JPanel;

public abstract class JPlot extends JPanel {

	public static final int GRID_STYLE_NONE = 0;
	public static final int GRID_STYLE_DOTTED = 1;
	public static final int GRID_STYLE_FULL = 2;

	public static final int GRID_DOTTED_LINE_SIZE = 2;
	public static final int GRID_DOTTED_JUMP_SIZE = 5;

	public static final int LEFT_MARGIN = 50;
	public static final int RIGHT_MARGIN = 50;
	public static final int TOP_MARGIN = 25;
	public static final int BOTTOM_MARGIN = 50;

	public static final int DEFAULT_AXIS_TICK = 5;
	public static final int DEFAULT_AXIS_LABEL_OFFSET = 5;

	protected boolean autoResize = true;

	protected int grid_style = GRID_STYLE_DOTTED;
	protected int grid_dotted_line_size = GRID_DOTTED_LINE_SIZE;
	protected int grid_dotted_jump_size = GRID_DOTTED_JUMP_SIZE;

	protected int left_margin = LEFT_MARGIN;
	protected int right_margin = RIGHT_MARGIN;
	protected int top_margin = TOP_MARGIN;
	protected int bottom_margin = BOTTOM_MARGIN;

	public abstract void resize();

	protected abstract void autoResize();


	/**
	 * Sets the grid style.
	 * @param style new grid style. Can be <code>JPlot3D.GRID_STYLE_NONE</code>, <code>JPlot3D.GRID_STYLE_DOTTED</code> or <code>JPlot3D.GRID_STYLE_FULL</code>.
	 */
	public void setGridStyle(int style) {
		this.grid_style = style;
		this.repaint();
	}

	/**
	 * Sets the grid dotted line size.
	 * The grid dotted line size is the size of each part of the dotted line.
	 * @param size new grid dotted line size
	 */
	public void setGridDottedLineSize(int size) {
		this.grid_dotted_line_size = size;
		if (this.grid_style == GRID_STYLE_DOTTED)
			this.repaint();
	}

	/**
	 * Sets the grid dotted jump size.
	 * The grid dotted jump size is the size of the space between each part of the dotted line.
	 * @param size new grid dotted jump size
	 */
	public void setGridDottedJumpSize(int size) {
		this.grid_dotted_jump_size = size;
		if (this.grid_style == GRID_STYLE_DOTTED)
			this.repaint();
	}

	/**
	 * Sets the left margin.
	 * This will change the <code>getMinimumSize()</code> of the panel.
	 * @param margin new left margin
	 */
	public void setLeftMargin(int margin) {
		this.left_margin = margin;
		this.repaint();
	}

	/**
	 * Sets the right margin.
	 * This will change the <code>getMinimumSize()</code> of the panel.
	 * @param margin new right margin
	 */
	public void setRightMargin(int margin) {
		this.right_margin = margin;
		this.repaint();
	}

	/**
	 * Sets the top margin.
	 * This will change the <code>getMinimumSize()</code> of the panel.
	 * @param margin new top margin
	 */
	public void setTopMargin(int margin) {
		this.top_margin = margin;
		this.repaint();
	}

	/**
	 * Sets the bottom margin.
	 * This will change the <code>getMinimumSize()</code> of the panel.
	 * @param margin new bottom margin
	 */
	public void setBottomMargin(int margin) {
		this.bottom_margin = margin;
		this.repaint();
	}

	
	/**
	 * Sets the auto resize.
	 * If the auto resize is set to true, the plot axis will be resized automatically when the data is set.
	 * Put this to true will not directly resize the axis until you set the data. For directly resizing the axis, use <code>autoResize()</code>.
	 * @param autoResize new auto resize
	 */
	public void setAutoResize(boolean autoResize) {
		this.autoResize = autoResize;
		this.repaint();
	}

	
}

package me.max.chartly.charts;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import me.max.chartly.Chartly;
import me.max.chartly.DataUtils;
import me.max.chartly.Formatting;
import me.max.chartly.components.color.Looks;
import me.max.chartly.components.data.DataPair;
import me.max.chartly.components.data.DataSet;
import me.max.chartly.exceptions.MissingInformationException;

/**
 * Superclass of all axis-having charts.
 * 
 * @author Max Johnson
 */
public abstract class AxisChart implements Chart {

	protected DataSet data;
	protected float current_x, current_y, x_axis_length, y_axis_height, max_y_scale, y_axis_increment;
	protected PFont font;
	protected Looks looks;
	
	private boolean showYLabels = true;
	private String xtitle, ytitle, title;
	
	/**
	 * Draws the axes of the graph
	 * Called after the rest of the graph is drawn
	 * so that it will be on top.
	 * 
	 * @param x Bottom left corner's x-coord
	 * @param y Bottom left corner's y-coord
	 */
	public void draw(float x, float y) {		
		this.current_x = x;
		this.current_y = y;
		
		Chartly.app.stroke(this.getLooks().getAxisColor());
		Chartly.app.fill(this.getLooks().getAxisColor());

		// X Axis
		Chartly.app.rect(x, y, x_axis_length, Formatting.axisAxisWidthFromAxisLength(x_axis_length));

		// X-Axis Labels
		optimizeLabelFontSize();
		int xcount = 0;
		for (DataPair pair : data.getData()) {
			Chartly.app.textAlign(PConstants.CENTER);
			Chartly.app.text(pair.label, x + getTextDistanceFactor(xcount), y + Chartly.app.getFont().getSize());
			xcount++;
		}

		// Y Axis
		Chartly.app.rect(x, y, Formatting.axisAxisWidthFromAxisLength(y_axis_height), -y_axis_height);

		// Y-Axis Labels
		if (showYLabels) {
			int ycount = 0;
			float pxincr = incrToPixels();
			Chartly.app.textSize(y_axis_height/pxincr);
			for (int i = 0; i <= y_axis_height; i+=pxincr) {
				Chartly.app.textAlign(PConstants.RIGHT);
				Chartly.app.text(DataUtils.floatToFormattedString(y_axis_increment * ycount), x - 3, y - (pxincr * ycount) + Chartly.app.getFont().getSize()/2);
				ycount++;
			}
		}
		
		// Titles
		titleFontSize(Formatting.axisMainTitleSizeFromHeight(y_axis_height));
		Chartly.app.textAlign(PConstants.CENTER, PConstants.BOTTOM);
		if (title != null) {
			Chartly.app.text(title, x + x_axis_length/2, y - y_axis_height - 20);
		}
		
		titleFontSize(Formatting.axisAxisTitleFromAxisLength(x_axis_length));
		if (xtitle != null) {
			Chartly.app.text(xtitle, x + x_axis_length/2, y + 40);
		}
		titleFontSize(Formatting.axisAxisTitleFromAxisLength(y_axis_height));
		if (ytitle != null) {
			Chartly.app.pushMatrix();
			Chartly.app.translate(x - 30, y - y_axis_height/2);
			Chartly.app.rotate(-PConstants.PI/2);
			Chartly.app.text(ytitle, 0, 0);
			Chartly.app.popMatrix();
		}
		
		Chartly.cleaner.restore();
	}
	
	protected float getHeightFactor(float value, float yend, float dy) {
		return PApplet.map(value, 0, yend, 0, dy);
	}
	
	private float incrToPixels() {
		return PApplet.map(y_axis_increment, 0, max_y_scale, 0, y_axis_height);
	}
	
	private float getTextDistanceFactor(int count) {
		float w = (float) (x_axis_length/(1.5 * data.size() + .5)); //Math done on whiteboard.
		return (float) (w + w * 1.5 * (count));
	}
	
	public AxisChart setTitles(String xtitle, String ytitle, String title) {
		this.xtitle = xtitle;
		this.ytitle = ytitle;
		this.title = title;
		return this;
	}

	private void optimizeLabelFontSize() {
		float max_size = Float.MAX_VALUE;
		float contrainer = (float) (2 * (x_axis_length/(1.5 * data.size() + .5)));
		for (DataPair pair : data.getData()) {
			String string = pair.label;
			float local_size = 0;
			do {
				Chartly.app.textSize(++local_size);
			} while (Chartly.app.textWidth(string) < contrainer && local_size < Formatting.labelSizeFromAxisLength(y_axis_height, data.getData().size()));
			if (local_size < max_size) max_size = local_size;
		}
		Chartly.app.textSize(max_size);
	}
	
	private void titleFontSize(float level) {
		Chartly.app.textSize(y_axis_height/level);
	}
	
	protected void testComplete() throws MissingInformationException {
		if (this.data.isEmpty()) {
			throw MissingInformationException.noData();
		}
		if (y_axis_increment == 0 && max_y_scale == 0) {
			throw MissingInformationException.noLabels();
		}
	}
	
	/**
	 * Get the width of the Chart
	 * @return the width (in pixels)
	 */
	public float getWidth() {
		return this.x_axis_length;
	}
	
	/**
	 * Get the height of the Chart
	 * @return the height (in pixels)
	 */
	public float getHeight() {
		return this.y_axis_height;
	}
	
	public AxisChart showLabels(boolean show) {
		this.showYLabels = show;
		return this;
	}

}

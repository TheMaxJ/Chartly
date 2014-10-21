package me.max.chartly.charts;

import me.max.chartly.Chartly;
import me.max.chartly.components.color.ColorScheme;
import me.max.chartly.components.data.DataPair;
import me.max.chartly.components.data.DataSet;
import me.max.chartly.exceptions.ExceptionWriter;
import me.max.chartly.exceptions.MissingInformationException;

public class LineChart extends AxisChart {
	
	/**
	 * Constructor
	 * @param dx Width
	 * @param dy Height
	 */
	public LineChart(float dx, float dy) {
		data = new DataSet();
		font = Chartly.app.createFont("Helvetica", 12);
		colorScheme = ColorScheme.getDefaultColorScheme();
		this.x_axis_width = dx;
		this.y_axis_height = dy;
	}
	
	@Override
	public void draw(float x, float y) {
		try {
			testComplete();
		} catch (MissingInformationException ex) {
			ExceptionWriter.write(ex);
			return;
		}
		
		Chartly.app.textFont(font);
		
		float w = (float) (x_axis_width/(1.5 * data.getData().size() + .5)); //Math done on whiteboard.
		int count = 0;
		float[] previous = new float[]{Float.MAX_VALUE, Float.MIN_VALUE}; //placeholders
		for (DataPair pair : data.getData()) {
			float xFactor =  (float) (x + w * (1 + 1.5 * count));
			float yFactor = y + -1 * this.getHeightFactor(pair.value, max_y_scale, y_axis_height);
			int c = colorScheme.next();
			
			Chartly.app.stroke(c);
			Chartly.app.fill(c);
			Chartly.app.strokeWeight(2);
			Chartly.app.ellipse(xFactor, yFactor, 3, 3);
			
			if (previous[0] != Integer.MAX_VALUE && previous[1] != Integer.MIN_VALUE) {
				Chartly.app.line(previous[0], previous[1], xFactor, yFactor);
			}
			
			previous[0] = xFactor;
			previous[1] = yFactor;
			count++;
		}
		super.draw(x, y);
	}

	@Override
	public void refresh() {
		draw(current_x,current_y);	
	}
	
	/**
	 * Provides the height in units and the increment of the yaxis
	 * @param end Max height (in units)
	 * @param incr distance between labels on YAxis
	 * @return this
	 */
	public LineChart setYLabels(float end, float incr) {
		this.max_y_scale = end;
		this.y_axis_increment = incr;
		return this;
	}

	@Override
	public LineChart setData(DataSet data) {
		this.data = data;
		return this;
	}

	@Override
	public DataSet getData() {
		return data;
	}

	@Override
	public ColorScheme getColorScheme() {
		return colorScheme;
	}

	@Override
	public LineChart setColorScheme(ColorScheme scheme) {
		this.colorScheme = scheme;
		return this;
	}
	
	@Override
	public LineChart setTitles(String xtitle, String ytitle, String title) {
		super.setTitles(xtitle, ytitle, title);
		return this;
	}
	
	private void testComplete() throws MissingInformationException {
		if (this.data.getData().isEmpty()) {
			throw MissingInformationException.noData();
		}
		if (y_axis_increment == 0 && max_y_scale == 0) {
			throw MissingInformationException.noLabels();
		}
	}

}

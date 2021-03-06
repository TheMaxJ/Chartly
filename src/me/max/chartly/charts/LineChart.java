package me.max.chartly.charts;

import me.max.chartly.Chartly;
import me.max.chartly.Defaults;
import me.max.chartly.components.color.Looks;
import me.max.chartly.components.data.DataPair;
import me.max.chartly.components.data.DataSet;
import me.max.chartly.exceptions.ExceptionWriter;
import me.max.chartly.exceptions.MissingInformationException;

/**
 * Represents a LineChart (also known as a Line Graph)
 *
 * @author Max Johnson
 * @example Chart_Example
 */
public class LineChart extends AxisChart {
	
	/**
	 * Creates a new LineChart object with the specified width and height
	 * 
	 * @param width Width (in pixels)
	 * @param height Height (in pixels)
	 */
	public LineChart(float width, float height) {
		if (!Chartly.hasApplet()) {
			 ExceptionWriter.write(MissingInformationException.noApplet());
			 return;
		}
		
		data = new DataSet();
		font = Chartly.app.createFont("Helvetica", 12);
		looks = Defaults.getLooks();
		this.x_axis_length = width;
		this.y_axis_height = height;
	}
	
	@Override
	public void draw(float x, float y) {
		Chartly.cleaner.load();
		
		try {
			testComplete();
		} catch (MissingInformationException ex) {
			ExceptionWriter.write(ex);
			return;
		}
		
		Chartly.app.textFont(font);
		
		float w = (float) (x_axis_length/(1.5 * data.getData().size() + .5)); //Math done on whiteboard.
		int count = 0;
		float[] previous = new float[]{Float.MAX_VALUE, Float.MIN_VALUE}; //placeholders
		for (DataPair pair : data.getData()) {
			float xFactor =  (float) (x + w * (1 + 1.5 * count));
			float yFactor = y + -1 * this.getHeightFactor(pair.value, max_y_scale, y_axis_height);
			int c = looks.next();
			
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
	 * @param top Max height (in units)
	 * @param increment distance between labels on YAxis
	 * @return this
	 */
	public LineChart setYLabels(float top, float increment) {
		this.max_y_scale = top;
		this.y_axis_increment = increment;
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
	public Looks getLooks() {
		return looks;
	}

	@Override
	public LineChart setLooks(Looks scheme) {
		this.looks = scheme;
		return this;
	}
	
	@Override
	public LineChart setTitles(String xtitle, String ytitle, String title) {
		super.setTitles(xtitle, ytitle, title);
		return this;
	}

	@Override
	public LineChart showLabels(boolean show) {
		super.showLabels(show);
		return this;
	}

}

package com.gsoft.common;

import com.gsoft.common.Sizing.RectangleF;
import com.gsoft.common.gui.Control;

public class LayoutManager {
	public RectangleF[] resultBounds;
	public int count;
	
	public LayoutManager(RectangleF bounds, boolean isHorizon, 
			float gapX, float gapY, Control[] controls) 
	{
		count = controls.length;
		this.resultBounds = new RectangleF[count];
		float controlWidth, controlHeight;
		float controlX, controlY;
		int i;
		
		if (isHorizon) {
			controlWidth = (bounds.width - gapX * (count+1)) / count;
			controlHeight = bounds.height - 2 * gapY;
			
			controlY = bounds.y + gapY;
			for (i=0; i<count; i++) {
				controlX = bounds.x + gapX*(i+1) + controlWidth*i;
				resultBounds[i] = new RectangleF(controlX, controlY, 
						controlWidth, controlHeight);
			}
			
		}
		else {
			controlHeight = (bounds.height - gapY * (count+1)) / count;
			controlWidth = bounds.width - 2 * gapX;
			
			controlX = bounds.x + gapY;
			for (i=0; i<count; i++) {
				controlY = bounds.y + gapY*(i+1) + controlHeight*i;
				resultBounds[i] = new RectangleF(controlX, controlY, 
						controlWidth, controlHeight);
			}			
		}
		
	}

}
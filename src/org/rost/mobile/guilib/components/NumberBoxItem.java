package org.rost.mobile.guilib.components;

import javax.microedition.lcdui.TextField;

/**
 * I only allow the inputting of numbers, rather than full strings! Useful for TCP port numbers e.g.
 * 
 * @author mmcnamee
 */
public class NumberBoxItem extends TextBoxItem {

	public NumberBoxItem(String s) {
		super(s);
	}
	
	protected int getConstraints() {
		return TextField.NUMERIC;
	}
	
}

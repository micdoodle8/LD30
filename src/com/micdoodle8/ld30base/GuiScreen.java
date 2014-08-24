package com.micdoodle8.ld30base;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiScreen extends Gui
{
	public List<GuiElement> elements = new ArrayList<GuiElement>();
	
	protected void addElement(GuiElement element)
	{
		element.init();
		elements.add(element);
	}

    @Override
    public void init()
    {
        elements.clear();
    }

    @Override
	public void update(int deltaTicks)
	{
		for (GuiElement element : elements)
		{
			element.update(deltaTicks);
		}
	}

	@Override
	public void draw()
	{
		for (GuiElement element : elements)
		{
			element.draw();
		}
	}
	
	public void onMouseClick(int x, int y)
	{
		for (GuiElement element : new ArrayList<GuiElement>(elements))
		{
            if (element.isEnabled())
            {
                element.onMouseClick(x, y);
                if (x > element.position.x - element.size.x / 2 &&
                        x < element.position.x + element.size.x / 2 &&
                        y > element.position.y - element.size.y / 2 &&
                        y < element.position.y + element.size.y / 2)
                {
                    this.onElementClicked(element);
                }
            }
		}
	}
	
	protected abstract void onElementClicked(GuiElement element);
}

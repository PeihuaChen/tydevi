package tydevi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * Canvas component to paint image
 */
class Canvas extends JComponent
{
	private static final long serialVersionUID = 1L;

	private final BufferedImage theImage;

	public Canvas(final BufferedImage thisImage)
	{
		super();
		this.theImage = thisImage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(this.theImage.getWidth(), this.theImage.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(this.theImage.getWidth(), this.theImage.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(this.theImage.getWidth(), this.theImage.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(final Graphics g)
	{
		final Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(this.theImage, 0, 0, this);
		g2.finalize();
	}
}
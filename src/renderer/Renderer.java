package renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Renderer
{
	// vertical margins
	static private final int BOTTOMMARGIN = 30;

	static private final int TOPMARGIN = 15;

	// distance between edges
	static private final int VSPACE = 20;

	static private final int HSPACE = 5;

	// offset from base line
	static private final int POSVOFFSET = 0 * Renderer.VSPACE;

	static private final int WORDVOFFSET = 1 * Renderer.VSPACE;

	static private final int EDGEVOFFSET = 2 * Renderer.VSPACE;

	// word space
	static private final int WORDSPACE = 20;

	static private final int NODELABELOFFSET = 2;

	static private final int EDGELABELOFFSET = 2;

	// arrow
	static private final int ARROWBASE = 2;

	static private final int ARROWHEIGHT = 4;

	static private final int ANCHORHEIGHT = 6;

	// initial edge height
	static private final int STARTHEIGHT = 3;

	// fonts
	static private final String WORDFONTNAME = "SansSerif";

	static private final String POSFONTNAME = "SansSerif";

	static private final String RELFONTNAME = "SansSerif";

	static private final int WORDFONTSIZE = 12;

	static private final int POSFONTSIZE = 8;

	static private final int RELFONTSIZE = 10;

	// colors
	static private final Color BACKCOLOR = Color.WHITE;

	static private final Color EDGECOLOR = Color.BLACK;

	static private final Color ANCHORCOLOR = Color.GRAY;

	static private final Color WORDCOLOR = Color.BLACK;

	static private final Color POSCOLOR = Color.GRAY;

	static private final Color RELCOLOR = Color.BLUE;

	// strokes

	static private final Stroke SOLID = new BasicStroke();

	static private final Stroke DOTTED = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 1.0f, 1.0f }, 0f);

	@SuppressWarnings("unused")
	static private final Stroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[] { 2.0f }, 0f);

	static private final Stroke EDGESTROKE = Renderer.SOLID;

	static private final Stroke ANCHORSTROKE = Renderer.DOTTED;

	/**
	 * Reference theGraph
	 */
	final IGraph theGraph;

	// scaled values

	final int hSpace;

	final int vSpace;

	final int topMargin;

	final int bottomMargin;

	final int wordVOffset;

	final int posVOffset;

	final int edgeVOffset;

	final int nodeLabelOffset;

	final int edgeLabelOffset;

	final int wordSpace;

	final int arrowBase;

	final int arrowHeight;

	final int anchorHeight;

	// scaled fonts

	final Font wordFont;

	final Font posFont;

	final Font relFont;

	/**
	 * Construct
	 * 
	 * @param thisGraph
	 *            graph to render
	 * @param scale
	 *            scale
	 */
	public Renderer(final IGraph thisGraph, final float scale)
	{
		this.theGraph = thisGraph; // scale
		this.hSpace = (int) (Renderer.HSPACE * scale);
		this.vSpace = (int) (Renderer.VSPACE * scale);

		this.topMargin = (int) (Renderer.TOPMARGIN * scale);
		this.bottomMargin = (int) (Renderer.BOTTOMMARGIN * scale);

		this.wordVOffset = (int) (Renderer.WORDVOFFSET * scale);
		this.posVOffset = (int) (Renderer.POSVOFFSET * scale);
		this.edgeVOffset = (int) (Renderer.EDGEVOFFSET * scale);

		this.nodeLabelOffset = (int) (Renderer.NODELABELOFFSET * scale);
		this.edgeLabelOffset = (int) (Renderer.EDGELABELOFFSET * scale);
		this.wordSpace = (int) (Renderer.WORDSPACE * scale);
		this.arrowBase = (int) (Renderer.ARROWBASE * scale);
		this.arrowHeight = (int) (Renderer.ARROWHEIGHT * scale);
		this.anchorHeight = (int) (Renderer.ANCHORHEIGHT * scale);

		this.wordFont = new Font(Renderer.WORDFONTNAME, Font.PLAIN, (int) (Renderer.WORDFONTSIZE * scale));
		this.posFont = new Font(Renderer.POSFONTNAME, Font.PLAIN, (int) (Renderer.POSFONTSIZE * scale));
		this.relFont = new Font(Renderer.RELFONTNAME, Font.PLAIN, (int) (Renderer.RELFONTSIZE * scale));
	}

	// U T I L S

	/**
	 * Get label dimensions
	 * 
	 * @param thisText
	 *            label text
	 * @param thisFont
	 *            font
	 * @return dimension
	 */
	static private Dimension getLabelDimension(final String thisText, final Font thisFont)
	{
		final FontRenderContext thisContext = new FontRenderContext(null, true, false);
		final TextLayout thisLayout = new TextLayout(thisText, thisFont, thisContext);
		final Rectangle2D theseBounds = thisLayout.getBounds();
		final int w = (int) theseBounds.getWidth();
		final int h = (int) thisLayout.getAscent();
		return new Dimension(w, h);
	}

	/**
	 * Get centering offset for a node with n edges and given width
	 * 
	 * @param n
	 *            number of incoming and outgoing nodes
	 * @param w
	 *            width
	 * @return centering offset for a node with n edges
	 */
	private int getCenteringOffset(final int n, final int w)
	{
		final int nLanes = n - 1;
		final float offsetInLanes = nLanes / 2.f;
		final float centerX = w / 2;
		return (int) (centerX - offsetInLanes * this.hSpace);
	}

	// N O D E L A Y O U T

	/**
	 * Layout
	 * 
	 * @param theseNodes
	 *            nodes
	 * @param thisFont
	 *            font
	 * @param thisSpaceWidth
	 *            space
	 * @return (total width, node 2 position map)
	 */
	@SuppressWarnings("boxing")
	static private Pair<Integer, Map<INode, Rectangle>> layout(final Collection<INode> theseNodes, final Font thisFont, final int thisSpaceWidth)
	{
		final Map<INode, Rectangle> thisNode2RectangleMap = new HashMap<INode, Rectangle>();
		int thisCursor = thisSpaceWidth;
		for (final INode thisNode : theseNodes)
		{
			final Dimension size = Renderer.getLabelDimension(thisNode.getLabel(), thisFont);
			final int w = size.width;
			final int h = size.height;
			thisNode2RectangleMap.put(thisNode, new Rectangle(thisCursor, 0, w, h));
			thisCursor += w + thisSpaceWidth;
		}
		return new Pair<Integer, Map<INode, Rectangle>>(thisCursor, thisNode2RectangleMap);
	}

	// E D G E S

	// vertically

	/**
	 * Layout edge heights
	 * 
	 * @param thisGraph
	 *            graph
	 * @return (max haight slot, edge 2 height slot map)
	 */
	@SuppressWarnings("boxing")
	static private Pair<Integer, Map<IEdge, Integer>> layoutHeights(final IGraph thisGraph)
	{
		int thisHeightCursor = 0;
		final Map<IEdge, Integer> thisEdge2HeightMap = new HashMap<IEdge, Integer>();
		for (final INode node : thisGraph.getNodes())
		{
			// iterate over this edge's outgoing nodes
			for (final IEdge thisEdge : thisGraph.getOutgoingEdges(node))
			{
				// height
				final int h = Renderer.allocateHeight(thisEdge, thisGraph.getEdges(), thisEdge2HeightMap);
				if (h > thisHeightCursor)
				{
					thisHeightCursor = h;
				}
				thisEdge2HeightMap.put(thisEdge, h);
			}
		}
		return new Pair<Integer, Map<IEdge, Integer>>(thisHeightCursor, thisEdge2HeightMap);
	}

	/**
	 * Allocate height slot (0-based)
	 * 
	 * @param thatEdge
	 *            target edge to find height for
	 * @param theseEdges
	 *            edge set
	 * @param thisEdge2HeightMap
	 *            edge->height map
	 * @return allocated height slot
	 */
	@SuppressWarnings("boxing")
	static private int allocateHeight(final IEdge thatEdge, final Collection<IEdge> theseEdges, final Map<IEdge, Integer> thisEdge2HeightMap)
	{
		for (int thisCandidateHeight = 0;; thisCandidateHeight++)
		{
			// clear 'conflict' flag for this round
			boolean conflicts = false;

			for (final IEdge thisEdge : theseEdges)
			{
				// ignore self
				if (thatEdge == thisEdge)
				{
					continue;
				}

				// ignore edges that are not visible
				final boolean isVisible = thisEdge2HeightMap.containsKey(thisEdge);
				if (!isVisible)
				{
					continue;
				}

				// determine if the target edge conflicts with this (visible) edge
				final int thisHeight = thisEdge2HeightMap.get(thisEdge);
				if (thisHeight == thisCandidateHeight && (Renderer.overlap(thisEdge, thatEdge) || Renderer.join(thatEdge, thisEdge)))
				{
					// signal conflict
					conflicts = true;

					// height is tainted : no need to continue iteration on edges for this height, try higher
					break;
				}
			}

			// if candidate height does not conflict with any edge elect it
			if (!conflicts)
				return thisCandidateHeight;
		}
	}

	/**
	 * Get edge ends (lowest first, as if undirected)
	 * 
	 * @param thisEdge
	 *            edge
	 * @return ordered pair of ends
	 */
	static private Pair<INode, INode> getEnds(final IEdge thisEdge)
	{
		final boolean isBackwards = thisEdge.getSource().compareTo(thisEdge.getTarget()) >= 0;
		final INode thisSource = thisEdge.getSource();
		final INode thisTarget = thisEdge.getTarget();
		final INode thisIndex1 = isBackwards ? thisTarget : thisSource;
		final INode thisIndex2 = isBackwards ? thisSource : thisTarget;
		return new Pair<INode, INode>(thisIndex1, thisIndex2);
	}

	/**
	 * Whether edges overlap
	 * 
	 * @param thisEdge
	 *            edge
	 * @param thatEdge
	 *            edge
	 * @return true if edges overlap
	 */
	static private boolean overlap(final IEdge thisEdge, final IEdge thatEdge)
	{
		final Pair<INode, INode> theseEnds1 = Renderer.getEnds(thisEdge);
		final Pair<INode, INode> theseEnds2 = Renderer.getEnds(thatEdge);
		if (theseEnds1.first.compareTo(theseEnds2.first) == -1 && theseEnds2.first.compareTo(theseEnds1.second) == -1 || // [ < { < ] [---{xxx]...
				theseEnds1.first.compareTo(theseEnds2.second) == -1 && theseEnds2.second.compareTo(theseEnds1.second) == -1 || // [ < } < ] ...[xxx}---]
				theseEnds2.first.compareTo(theseEnds1.first) == -1 && theseEnds1.first.compareTo(theseEnds2.second) == -1 || // { < [ < } {...[xxx}---
				theseEnds2.first.compareTo(theseEnds1.second) == -1 && theseEnds1.second.compareTo(theseEnds2.second) == -1 // { < ] < } ---{xxx]...}
		)
			return true;
		return false;
	}

	/**
	 * Whether first edge joins second (its target is the second's source or target)
	 * 
	 * @param thisEdge1
	 *            edge 1
	 * @param thisEdge2
	 *            edge 2
	 * @return true if first edge reaches second
	 */
	static private boolean join(final IEdge thisEdge1, final IEdge thisEdge2)
	{
		final INode thisTarget1 = thisEdge1.getTarget();
		final Pair<INode, INode> theseEnds2 = Renderer.getEnds(thisEdge2);
		return thisTarget1.equals(theseEnds2.first) || thisTarget1.equals(theseEnds2.second);
	}

	// horizontally

	/**
	 * Allocate node slots
	 * 
	 * @param thisGraph
	 *            graph
	 * @return allocation scheme : node -> (edge->slot number)
	 */
	@SuppressWarnings("boxing")
	static private Map<INode, Map<IEdge, Integer>> allocateNodeSlots(final IGraph thisGraph)
	{
		final Map<INode, Map<IEdge, Integer>> thisNode2AllocationMap = new HashMap<INode, Map<IEdge, Integer>>();

		for (final INode thisNode : thisGraph.getNodes())
		{
			int i = 0;
			final Map<IEdge, Integer> thisNodeAllocationScheme = new HashMap<IEdge, Integer>();
			for (final IEdge thisEdge : thisGraph.getIncomingEdges(thisNode))
			{
				thisNodeAllocationScheme.put(thisEdge, i);
				i++;
			}
			for (final IEdge thisEdge : thisGraph.getOutgoingEdges(thisNode))
			{
				thisNodeAllocationScheme.put(thisEdge, i);
				i++;
			}
			thisNode2AllocationMap.put(thisNode, thisNodeAllocationScheme);
		}
		return thisNode2AllocationMap;
	}

	// R E N D E R T O I M A G E

	/**
	 * Make buffered image of the graph
	 * 
	 * @return image
	 */
	@SuppressWarnings("boxing")
	public BufferedImage makeImage()
	{
		// layout nodes
		final Pair<Integer, Map<INode, Rectangle>> thisNodeLayoutResult = Renderer.layout(this.theGraph.getNodes(), this.wordFont, this.wordSpace);
		final int thisLayoutSpan = thisNodeLayoutResult.first;
		final Map<INode, Rectangle> thisLayout = thisNodeLayoutResult.second;

		// layout edge heights
		final Pair<Integer, Map<IEdge, Integer>> thisEdgeLayoutResult = Renderer.layoutHeights(this.theGraph);
		final int thisMaxHeight = Renderer.STARTHEIGHT + thisEdgeLayoutResult.first;
		final Map<IEdge, Integer> thisEdge2HeightMap = thisEdgeLayoutResult.second;

		// image dimension
		final int thisImageWidth = (int) Math.ceil(thisLayoutSpan);
		final int thisImageHeight = this.bottomMargin + this.edgeVOffset + this.theGraph.getNodes().size() * this.vSpace + this.topMargin;

		// image
		final BufferedImage thisImage = new BufferedImage(thisImageWidth, thisImageHeight, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2 = thisImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		// base lines
		final int yBase = thisImageHeight - this.bottomMargin;
		final int yWord = yBase - this.wordVOffset;
		final int yPos = yBase - this.posVOffset;
		final int yEdge = yBase - this.edgeVOffset;
		final int arrowTop = yEdge - this.arrowHeight;

		// background
		g2.setBackground(Renderer.BACKCOLOR);
		g2.clearRect(0, 0, thisImageWidth, thisImageHeight);

		// guides
		// g2.setColor(Color.LIGHT_GRAY);
		// g2.drawLine(0, baseline, thisImageWidth, yBase);
		// g2.drawLine(0, yWord, thisImageWidth, yWord);
		// g2.drawLine(0, yPos, thisImageWidth, yPos);
		// g2.drawLine(0, yEdge, thisImageWidth, yEdge);

		// draw node lexes
		g2.setColor(Renderer.WORDCOLOR);
		g2.setFont(this.wordFont);

		for (final INode thisNode : this.theGraph.getNodes())
		{
			final Rectangle where = thisLayout.get(thisNode);
			final int x = where.x;
			g2.drawString(thisNode.getLabel(), x, yWord);
		}

		// draw anchor
		g2.setColor(Renderer.ANCHORCOLOR);
		g2.setStroke(Renderer.ANCHORSTROKE);

		for (final INode thisNode : this.theGraph.getNodes())
		{
			final Rectangle where = thisLayout.get(thisNode);
			final int x = where.x + where.width / 2;
			final int bottom = yWord - where.height;
			final int top = bottom - this.anchorHeight;
			g2.drawLine(where.x, top, where.x + where.width, top);
			g2.drawLine(x, top, x, bottom);
		}

		// draw node poses
		g2.setFont(this.posFont);
		g2.setColor(Renderer.POSCOLOR);

		for (final INode thisNode : this.theGraph.getNodes())
		{
			final Rectangle where = thisLayout.get(thisNode);
			final int x = where.x;
			g2.drawString(thisNode.getPos(), x, yPos);
		}

		// allocate slots for edges
		final Map<INode, Map<IEdge, Integer>> theseSlotAllocations = Renderer.allocateNodeSlots(this.theGraph);

		// draw edges
		g2.setColor(Renderer.EDGECOLOR);
		g2.setStroke(Renderer.EDGESTROKE);

		for (final IEdge thisEdge : this.theGraph.getEdges())
		{
			// end nodes
			final INode thisSource = thisEdge.getSource();
			final INode thisTarget = thisEdge.getTarget();

			// edge's ends' positions
			final Rectangle fromWhere = thisLayout.get(thisSource);
			final Rectangle toWhere = thisLayout.get(thisTarget);

			// slots
			final Map<IEdge, Integer> thisSourceSlotAllocation = theseSlotAllocations.get(thisSource);
			final Map<IEdge, Integer> thisTargetSlotAllocation = theseSlotAllocations.get(thisTarget);
			final int thisSourceSlot = thisSourceSlotAllocation.get(thisEdge);
			final int thisTargetSlot = thisTargetSlotAllocation.get(thisEdge);

			// centerers
			final int sourceOffset = getCenteringOffset(thisSourceSlotAllocation.size(), fromWhere.width);
			final int targetOffset = getCenteringOffset(thisTargetSlotAllocation.size(), toWhere.width);

			// height
			final int h = Renderer.STARTHEIGHT + thisEdge2HeightMap.get(thisEdge);

			// edge data
			final int fromX = fromWhere.x;
			final int toX = toWhere.x;
			final int startx = fromX + sourceOffset + thisSourceSlot * this.hSpace;
			final int stopx = toX + targetOffset + thisTargetSlot * this.hSpace;
			final int top = yBase - h * this.vSpace;

			// draw edge
			g2.drawLine(startx, top, startx, yEdge);
			g2.drawLine(startx, top, stopx, top);
			g2.drawLine(stopx, top, stopx, yEdge);

			// arrow data
			final int arrowLeft = stopx - this.arrowBase;
			final int arrowRight = stopx + this.arrowBase;

			// draw arrow
			g2.drawLine(arrowLeft, arrowTop, stopx, yEdge);
			g2.drawLine(arrowRight, arrowTop, stopx, yEdge);
		}

		// relation labels
		g2.setColor(Renderer.RELCOLOR);
		g2.setFont(this.relFont);

		for (final IEdge thisEdge : this.theGraph.getEdges())
		{
			// end nodes
			final INode thisSource = thisEdge.getSource();
			final INode thisTarget = thisEdge.getTarget();

			// edge's ends' positions
			final Rectangle fromWhere = thisLayout.get(thisSource);
			final Rectangle toWhere = thisLayout.get(thisTarget);

			// slots
			final Map<IEdge, Integer> thisSourceSlotAllocation = theseSlotAllocations.get(thisSource);
			final Map<IEdge, Integer> thisTargetSlotAllocation = theseSlotAllocations.get(thisTarget);
			final int thisSourceSlot = thisSourceSlotAllocation.get(thisEdge);
			final int thisTargetSlot = thisTargetSlotAllocation.get(thisEdge);

			// centerers
			final int sourceOffset = getCenteringOffset(thisSourceSlotAllocation.size(), fromWhere.width);
			final int targetOffset = getCenteringOffset(thisTargetSlotAllocation.size(), toWhere.width);

			// height
			final int h = Renderer.STARTHEIGHT + thisEdge2HeightMap.get(thisEdge);

			// edge data
			final int fromX = fromWhere.x;
			final int toX = toWhere.x;
			final int startx = fromX + sourceOffset + thisSourceSlot * this.hSpace;
			final int stopx = toX + targetOffset + thisTargetSlot * this.hSpace;
			final int top = yBase - h * this.vSpace; // draw top edge

			// label dimensions
			final Dimension size = Renderer.getLabelDimension(thisEdge.getLabel(), this.relFont);
			final int cx = (int) Math.ceil(size.getWidth());
			final int cy = (int) Math.ceil(size.getHeight()) + this.nodeLabelOffset;

			// label position
			final int x = startx >= stopx ? stopx : startx;
			final int ybase = top - this.edgeLabelOffset;
			final int y = ybase - cy;

			// label background
			g2.clearRect(x, y, cx, cy);
			g2.drawString(thisEdge.getLabel(), x, ybase);
		}

		// crop
		final int h = this.bottomMargin + (thisMaxHeight + 1) * this.vSpace + this.topMargin;
		final int yCrop = thisImageHeight - h;
		// g2.setStroke(DASHED);
		// g2.drawLine(0, yCrop, thisImageWidth, yCrop);

		// release context
		g2.dispose();

		return thisImage.getSubimage(0, yCrop, thisImageWidth, h);
	}
}

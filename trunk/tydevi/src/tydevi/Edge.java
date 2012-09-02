package tydevi;

import renderer.IEdge;
import renderer.INode;

public class Edge implements IEdge
{
	/**
	 * Edge index
	 */
	private final int idx;

	/**
	 * Source node
	 */
	private final INode source;

	/**
	 * Target node
	 */
	private final INode target;

	/**
	 * Label
	 */
	private final String label;

	/**
	 * Construct
	 * 
	 * @param idx
	 *            index (not a requirement)
	 * @param source
	 *            source node
	 * @param target
	 *            target node
	 * @param label
	 *            label
	 */
	public Edge(final int idx, final INode source, final INode target, final String label)
	{
		this.idx = idx;
		this.source = source;
		this.target = target;
		this.label = label;
	}

	/**
	 * Get index
	 * 
	 * @return index
	 */
	public int getIdx()
	{
		return this.idx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IEdge#getSource()
	 */
	@Override
	public INode getSource()
	{
		return this.source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IEdge#getTarget()
	 */
	@Override
	public INode getTarget()
	{
		return this.target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.IEdge#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return this.label;
	}

	@Override
	public String toString()
	{
		return String.format("%s--%s->%s", getSource().toString(), getLabel(), getTarget().toString());
	}
}

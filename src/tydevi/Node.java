package tydevi;

import renderer.INode;

public class Node implements INode
{
	/**
	 * Index
	 */
	private final int idx;

	/**
	 * Lex
	 */
	private final String lex;

	/**
	 * Part-of-speech
	 */
	private final String pos;

	/**
	 * Constructor
	 * 
	 * @param idx
	 *            index
	 * @param lex
	 *            lex
	 * @param pos
	 *            part-of-speech
	 */
	public Node(final int idx, final String lex, final String pos)
	{
		this.idx = idx;
		this.lex = lex;
		this.pos = pos;
	}

	/**
	 * Get index of word in sentence
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
	 * @see tydevi.INode#getLex()
	 */
	@Override
	public String getLex()
	{
		return this.lex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.INode#getPos()
	 */
	@Override
	public String getPos()
	{
		return this.pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tydevi.INode#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return this.lex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("[%s]", getLabel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final INode thisNode)
	{
		final int idx1 = getIdx();
		final int idx2 = ((Node) thisNode).getIdx();
		if (idx1 < idx2)
			return -1;
		if (idx1 > idx2)
			return +1;
		return 0;
	}
}

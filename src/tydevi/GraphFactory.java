package tydevi;

import tydevi.Parser.Parse;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

public class GraphFactory
{
	/**
	 * Make theGraph from parse tree
	 * 
	 * @param parse
	 *            parse tree
	 * @return theGraph
	 */
	static public Graph makeGraph(final Parse parse)
	{
		// create theGraph
		final Graph g = new Graph();

		// nodes
		for (final TaggedWord taggedWord : parse.first.taggedYield())
		{
			final String lex = taggedWord.word();
			final String pos = taggedWord.tag();
			//System.out.println(String.format("lex=%s pos=%s", lex, pos));
			g.addNode(lex, pos);
		}

		// edges
		for (final TypedDependency td : parse.second)
		{
			final int sourceIndex = td.gov().index() - 1;
			final int targetIndex = td.dep().index() - 1;
			final String label = td.reln().toString();
			if(sourceIndex == -1)
			{
				//System.out.println(String.format("%d-->%d %s", sourceIndex, targetIndex, label));
				continue;
			}
			//System.out.println(String.format("%d-->%d %s", sourceIndex, targetIndex, label));
			g.addEdge(sourceIndex, targetIndex, label);
		}

		return g;
	}
}

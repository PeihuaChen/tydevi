package tydevi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import renderer.IGraph;
import renderer.Pair;
import renderer.Renderer;
import tydevi.Parser.Parse;
import edu.stanford.nlp.ling.Word;

public class Main
{
	static private final float SCALE = 1.0f;

	static private final String HELPSTRING = "Usage:\n" + //
			"tydevi <options> : interactive input\n" + //
			"tydevi <options> <sentence> [<sentence> ..] : input from command line\n" + //
			"tydevi <options> -in <textfile> : input from text file\n" + //
			"<options>\n" + //
			"\t-out <imagefile basename> : write output to file(s), suffix will be added\n" + //
			"\t-grammar <grammar> : path to alternative grammar\n" + //
			"\t-factory ALL|ALLNOEXTRA|COLLAPSED|COLLAPSEDNOEXTRA|COLLAPSEDTREE|CCPROCESSED|CCPROCESSEDNOEXTRA : expected result\n" + //
			"\t-frames : output in separate frames (no tabs)\n"; //

	@SuppressWarnings("boxing")
	public static void main(final String args[]) throws Exception
	{
		UIManager.put("swing.boldMetal", false);

		// arg parsing
		boolean frames = false;
		boolean write = false;
		String inFile = null;
		String outFile = null;

		int firstArg = 0;
		try
		{
			for (int i = 0; i < args.length; i++)
			{
				if (!args[i].startsWith("-"))
				{
					break;
				}
				if (args[i].equals("-in"))
				{
					inFile = args[++i];
					firstArg = i + 1;
				}
				if (args[i].equals("-factory"))
				{
					Analyzer.setFactory(args[++i]);
					firstArg = i + 1;
				}
				if (args[i].equals("-grammar"))
				{
					Parser.setGrammar(args[++i]);
					firstArg = i + 1;
				}
				if (args[i].equals("-out"))
				{
					write = true;
					outFile = args[++i];
					firstArg = i + 1;
				}
				if (args[i].equals("-frames"))
				{
					frames = true;
					firstArg = i + 1;
				}
				if (args[i].equals("-help"))
				{
					System.err.println(Main.HELPSTRING);
					System.exit(0);
				}
			}
		}
		catch (final IndexOutOfBoundsException e)
		{
			System.err.println(Main.HELPSTRING);
			System.exit(1);
		}

		try
		{
			// data
			List<List<Word>> theseSentences = null;
			if (inFile != null)
			{
				// file processing
				theseSentences = Main.readFile(inFile);
			}
			else
			{
				if (firstArg >= args.length)
				{
					// interactive
					theseSentences = Segmenter.getTokenizedSentences(Segmenter.getDocument(Main.getText()));
				}
				else
				{
					// command line feed
					theseSentences = new ArrayList<List<Word>>();
					for (int i = firstArg; i < args.length; i++)
					{
						theseSentences.add(Segmenter.getDocument(args[i]));
					}
				}
			}

			// init
			Parser.init();
			Analyzer.init();

			// process
			if (write)
			{
				int i = 1;
				for (final List<Word> thisSentence : theseSentences)
				{
					Main.writeImage(outFile + i++, thisSentence);
				}
				System.exit(0);
			}
			else if (frames)
			{
				for (final List<Word> thisSentence : theseSentences)
				{
					Main.displayImage(thisSentence);
				}
			}
			else
			{
				Main.displayImages(theseSentences);
			}
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			System.exit(2);
		}
	}

	// I N P U T

	public static String getText()
	{
		// text
		String thisText = null;
		final TextDialog thisTextDialog = new TextDialog();
		thisTextDialog.setModal(true);
		thisTextDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		thisTextDialog.setVisible(true);
		if (thisTextDialog.ok)
		{
			thisText = thisTextDialog.theTextArea.getText();
		}
		if (thisTextDialog.cancel)
		{
			System.exit(0);
		}

		if (thisText == null || thisText.isEmpty())
			throw new IllegalArgumentException("empty sentence");
		return thisText;
	}

	// R E A D

	static public List<List<Word>> readFile(final String thisInfile) throws IOException
	{
		final Reader thisReader = new FileReader(thisInfile);
		final Pair<List<String>, List<List<Word>>> thisResult = Segmenter.getSentences(Segmenter.getDocument(thisReader, thisInfile));
		// final List<String> theseSentences = thisResult.first;
		// for (final String thisSentence : theseSentences)
		// {
		// System.out.println(thisSentence.replaceAll("\n", " "));
		// }
		return thisResult.second;
	}

	// D I S P L A Y

	/**
	 * Display
	 * 
	 * @param thisSentence
	 *            sentence
	 */
	static public void displayImage(final List<Word> thisSentence)
	{
		final BufferedImage thisImage = Main.makeImage(thisSentence);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				final JFrame thisFrame = new JFrame("Typed Dependency Viewer");
				thisFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				thisFrame.getContentPane().setLayout(new BorderLayout());
				thisFrame.getContentPane().add(new Canvas(thisImage), BorderLayout.CENTER);
				thisFrame.pack();
				thisFrame.setVisible(true);
			}
		});
	}

	/**
	 * Display in tabs
	 * 
	 * @param theseSentences
	 *            sentences
	 */
	static public void displayImages(final List<List<Word>> theseSentences)
	{
		if (theseSentences.size() == 1)
		{
			Main.displayImage(theseSentences.get(0));
			return;
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				final JFrame thisFrame = new JFrame("Typed Dependency Viewer");
				thisFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				final JTabbedPane thisTabbedPane = new JTabbedPane();
				thisTabbedPane.setTabPlacement(SwingConstants.BOTTOM);
				int i = 0;
				for (final List<Word> thisSentence : theseSentences)
				{
					// image
					final BufferedImage thisImage = Main.makeImage(thisSentence);
					final JComponent thisImageComponent = new Canvas(thisImage);
					thisImageComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

					// panel
					final JPanel thisPanel = new JPanel();
					thisPanel.setBackground(Color.WHITE);
					thisPanel.setLayout(new BoxLayout(thisPanel, BoxLayout.Y_AXIS));
					thisPanel.add(Box.createVerticalGlue());
					thisPanel.add(thisImageComponent);
					JComponent thisTabbedComponent = thisPanel;

					// scroll
					// if (thisImage.getWidth() > 800)
					{
						final JScrollPane thisScrollPane = new JScrollPane(thisPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						thisScrollPane.setPreferredSize(new Dimension(800, thisPanel.getPreferredSize().height));
						thisTabbedComponent = thisScrollPane;
					}

					// add
					thisTabbedPane.addTab("#" + (i + 1), thisTabbedComponent);
					if (i < 9)
					{
						thisTabbedPane.setMnemonicAt(i, KeyEvent.VK_A + i);
					}
					i++;
				}
				thisFrame.getContentPane().add(thisTabbedPane);
				thisFrame.pack();
				thisFrame.setVisible(true);
			}
		});
	}

	/**
	 * Write image
	 * 
	 * @param thisOutFileBase
	 *            output filename base (no extension)
	 * @param thisSentence
	 *            sentence
	 * @throws IOException
	 */
	public static void writeImage(final String thisOutFileBase, final List<Word> thisSentence) throws IOException
	{
		final String thisOutFileName = thisOutFileBase + ".png";
		final BufferedImage thisImage = Main.makeImage(thisSentence);
		ImageIO.write(thisImage, "png", new File(thisOutFileName));
		System.err.println(thisOutFileName + " done");
	}

	// I M A G E F A C T O R Y

	/**
	 * Make image
	 * 
	 * @param sentence
	 *            source sentence
	 * @return image
	 */
	public static BufferedImage makeImage(final List<Word> sentence)
	{
		final Parse parse = Parser.parse(sentence);
		return Main.makeImage(parse);
	}

	/**
	 * Make image
	 * 
	 * @param parse
	 *            parse tree
	 * @return image
	 */
	public static BufferedImage makeImage(final Parse parse)
	{
		return Main.makeImage(parse, Main.SCALE);
	}

	/**
	 * Make image
	 * 
	 * @param parse
	 *            parse tree
	 * @param scale
	 *            scale
	 * @return scaled image
	 */
	public static BufferedImage makeImage(final Parse parse, final float scale)
	{
		final IGraph graph = GraphFactory.makeGraph(parse);
		// System.err.println("theGraph:\n" + graph);
		return new Renderer(graph, scale).makeImage();
	}
}

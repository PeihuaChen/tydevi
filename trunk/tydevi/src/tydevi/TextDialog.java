package tydevi;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Text dialog
 * 
 * @author Bernard Bou
 */
public class TextDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Ok flag
	 */
	public boolean ok;

	/**
	 * Cancel flag
	 */
	public boolean cancel;

	// C O M P O N E N T S

	/**
	 * Text
	 */
	public JTextArea theTextArea;

	/**
	 * Constructor
	 */
	public TextDialog()
	{
		super();
		initialize();
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		setTitle("Dependency Viewer - Text input");
		setResizable(true);

		// label
		final JLabel thisLabel = new JLabel("Text:");
		thisLabel.setToolTipText("Enter Text");

		// input
		this.theTextArea = new JTextArea();
		this.theTextArea.setColumns(32);
		this.theTextArea.setRows(12);
		this.theTextArea.setLineWrap(true);
		this.theTextArea.setFont(new Font("Dialog", 0, 10));
		this.theTextArea.setToolTipText("Input text");

		// buttons
		final JButton thisOkButton = new JButton("OK");
		thisOkButton.addActionListener(new java.awt.event.ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				TextDialog.this.ok = true;
				setVisible(false);
			}
		});
		final JButton thisCancelButton = new JButton("Cancel");
		thisCancelButton.addActionListener(new java.awt.event.ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				TextDialog.this.cancel = true;
				setVisible(false);
			}
		});

		// button panel
		final JPanel thisButtonPanel = new JPanel();
		thisButtonPanel.add(thisCancelButton, null);
		thisButtonPanel.add(thisOkButton, null);

		// assemble
		final JPanel thisPanel = new JPanel();
		thisPanel.setLayout(new GridBagLayout());
		thisPanel.add(thisLabel, new GridBagConstraints(0, 1, 1, 1, 0., 0., GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
		thisPanel.add(new JScrollPane(this.theTextArea), new GridBagConstraints(0, 2, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
		thisPanel.add(thisButtonPanel, new GridBagConstraints(0, 3, 1, 1, 0., 0., GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		setContentPane(thisPanel);

		// default button
		getRootPane().setDefaultButton(thisOkButton);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean thisFlag)
	{
		if (thisFlag)
		{
			this.ok = false;
			this.cancel = false;
			this.theTextArea.setText("");
			this.theTextArea.selectAll();
			pack();
		}
		super.setVisible(thisFlag);
	}
}

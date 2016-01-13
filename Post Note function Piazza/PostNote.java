import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GUI representation of the Post Note function in Piazza
 * 
 * @author Kyle Jeffries
 * @version 2
 */
public class PostNote extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static PostNote app = null;
    private JRadioButton classRadioButton = null;
    private JButton postButton = null;
    private JButton cancelButton = null;
    private JTextField summary = null;
    private JTextArea details = null;
    private JCheckBox hw1 = null;
    private JCheckBox hw2 = null;
    private JCheckBox hw3 = null;
    private JCheckBox hw4 = null;
    private JCheckBox hw5 = null;
    private JCheckBox hw6 = null;
    private JCheckBox po1 = null;
	private JCheckBox po2 = null;
	private JCheckBox po3 = null;

	
	/**
	 * creates an instance of the PostNote class
	 * 
	 * @param title The title of the application window
	 */
	public PostNote(String title) 
	{
		super(title);
		try 
		{
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		setLayout(new GridBagLayout());

		//create the post to label
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.25;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		//add the label to the layout
		add(new JLabel("Post To", JLabel.RIGHT), c);

		//create the radio buttons
		c.gridx = 1;
		c.weightx = 0.75;
		JPanel postToPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		classRadioButton = new JRadioButton("Entire Class");
		classRadioButton.setSelected(true);
		JRadioButton individualRadioButton = new JRadioButton("Individual Student(s) / Instructor(s)");
		//add the radio buttons to a button group
		ButtonGroup group = new ButtonGroup();
	    group.add(classRadioButton);
	    group.add(individualRadioButton);
	    //add the radio buttons to a panel
	    postToPanel.add(classRadioButton);
	    postToPanel.add(individualRadioButton);
		c.gridy = 0;	
		//add the panel to the layout
		add(postToPanel, c);
		
		//add the select folders section
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Select Folder(s)", JLabel.RIGHT), c);
		
		//set up the check boxes and lay them out
		hw1 = new JCheckBox("hw1");
		hw2 = new JCheckBox("hw2");
		hw3 = new JCheckBox("hw3");
		hw4 = new JCheckBox("hw4");
		hw5 = new JCheckBox("hw5");
		hw6 = new JCheckBox("hw6");
		c.gridx = 1;
		//create a panel to add the check boxes to
		JPanel postToPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		postToPanel2.add(hw1);
		postToPanel2.add(hw2);
		postToPanel2.add(hw3);
		postToPanel2.add(hw4);
		postToPanel2.add(hw5);
		postToPanel2.add(hw6);
		//add the panel to the layout
		add(postToPanel2, c);
		
		//add the summary section
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		JLabel exSum = new JLabel("<html><P ALIGN=Right>Summary<br><i>(100 characters or less)</html>", JLabel.RIGHT);
		add(exSum, c);
		summary = new JTextField();
		c.gridx = 1;
		add(summary, c);
		
		//add the details section
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel("Details", JLabel.RIGHT), c);
		c.weighty = 1;
		c.gridx = 1;
		//create a JScrollPane with a JTextArea inside
		details = new JTextArea();
		JScrollPane pane = new JScrollPane(details);
		//add the pane to the layout
		add(pane, c);

		
		//add the posting options
		c.gridx = 0;
		c.gridy = 4;
		c.weighty = 0;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Posting Options", JLabel.RIGHT), c);
		//use a gridlayout to format the check boxes vertically
		GridLayout options = new GridLayout(3, 1);
		//when creating the checkboxes use html to italicize certain sections
		po1 = new JCheckBox("<html>Make this an announcement <i>(note appears on the course page)</html>");
		po2 = new JCheckBox("<html>Send email notifications immediately <i>(bypassing students' email preferences, if necessary)</html>");
		po3 = new JCheckBox("<html>Disable receiving email notifications <i>(class members will not be notified of content creation)</html>");
		JPanel postToPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		c.gridx = 1;
		postToPanel3.setLayout(options);
		postToPanel3.add(po1);
		postToPanel3.add(po2);
		postToPanel3.add(po3);
		add(postToPanel3, c);
		
		
		//add the buttons for postNote and cancel
		c.gridx = 1;
		c.gridy = 5;
		c.insets = new Insets(5,5,5,5);
		c.fill = GridBagConstraints.HORIZONTAL;
		postButton = new JButton("Post my Note!");
		cancelButton = new JButton("Cancel");
		//set the color for the postNote button
		postButton.setBackground(Color.ORANGE);
		postButton.setForeground(Color.WHITE);
		//create a button group and add the two buttons to it
		ButtonGroup group4 = new ButtonGroup();
		JPanel postToPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		group4.add(postButton);
		group4.add(cancelButton);
		postToPanel4.add(postButton);
		postToPanel4.add(cancelButton);
		//add the listeners to the buttons
		postButton.addActionListener(this);
		cancelButton.addActionListener(this);
		add(postToPanel4, c);
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) 
	{
		app = new PostNote("Post Note");
        app.addWindowListener(new WindowAdapter() 
        {
            public void windowClosing(WindowEvent windowevent) 
            {
                app.dispose();
                System.exit(0);
            }
        });
		app.setSize(800, 400);
		app.setVisible(true);
	}

	/**
	 * Print out the not information when the postNote button is pressed
	 * Reset the choices to default and clear text boxes when cancel button is pressed
	 * 
	 * @param e Action Event object
	 */
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if (source == postButton) 
		{
			//display where it's posted
			System.out.println("Post to:");
			if(classRadioButton.isSelected())
			{
				System.out.println("Entire Class");
			}
			else
				System.out.println("Individual Student(s) / Instructor(s)");
			
			//display where it's posted under
			//since multiple check boxes can be selected, use if instead of if/else statements so that they all
			//can be displayed if they're all selected
			System.out.println("\nSelect Folder(s):");
			if(hw1.isSelected())
			{
				System.out.println("hw1");
			}
			if(hw2.isSelected())
			{
				System.out.println("hw2");
			}
			if(hw3.isSelected())
			{
				System.out.println("hw3");
			}
			if(hw4.isSelected())
			{
				System.out.println("hw4");
			}
			if(hw5.isSelected())
			{
				System.out.println("hw5");
			}
			if(hw6.isSelected())
			{
				System.out.println("hw6");
			}
			
			//display summary
			System.out.println("\nSummary: \n"+summary.getText());
			//display details
			System.out.println("\nDetails: \n" + details.getText());
			
			//display posting options
			System.out.println("\nPosting Options:");
			if(po1.isSelected())
			{
				System.out.println("Make this an announcement");
			}
			if(po2.isSelected())
			{
				System.out.println("Send email notifications immediately");
			}
			if(po3.isSelected())
			{
				System.out.println("Disable receiving email notifications");
			}
		}
		else if (source == cancelButton) 
		{
			//set selections back to the default
			//radio buttons
			classRadioButton.setSelected(true);
			
			//text fields
			summary.setText(null);
			details.setText(null);
			
			//check boxes
			hw1.setSelected(false);
			hw2.setSelected(false);
			hw3.setSelected(false);
			hw4.setSelected(false);
			hw5.setSelected(false);
			hw6.setSelected(false);
			po1.setSelected(false);
			po2.setSelected(false);
			po3.setSelected(false);
		}
	}


}

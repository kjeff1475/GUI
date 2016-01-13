import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.jogamp.common.nio.Buffers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


/**
 * standalone Java Swing application that will display a GLJ panel and 
 * draw to it from a given csv file that will give coordinates. The user
 * can choose how the coordinates are connected with the JComboBox
 * 
 * @author Kyle Jeffries
 * @version 1
 */
@SuppressWarnings("serial")
public class SimpleEditor extends JFrame implements ActionListener, GLEventListener, ChangeListener, TableModelListener, ListSelectionListener
{ 
	private final static String TITLE = "Simple Editor";
	private Color bgColor = null;
	private Color fgColor = null;
	private Color sColor = null;
	private JMenuBar menuBar = null;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem closeMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem quitMenuItem = null;
	private JMenuItem copyMenuItem = null;
	private JMenuItem pasteMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JScrollPane scrollPane = null;
	private ColorDialog cd = null;
	private JSplitPane split = null;
	private GLJPanel panel = null;
	private JTextPane textPane = null;
	private JPanel colorPanel = null;
	private File file = null;
	private JTable table = null;
	private JButton bg = null;
	private JButton fg = null;
	private JButton s = null;
	private JColorChooser color = null;
	JComboBox<String> choices = new JComboBox();
	private int fragmentShader = 0;
	private int vertexShader = 0;
	private int shaderProgram = 0;
	private int vertexType = 0;
	private float vertexData[] = null;
	private float selVert[] = null;
	private IntBuffer intBuffer = null;
	private FloatBuffer floatBuffer = null;
	private int location;
	private int buttonPressed = 90;
	private float fGreen;
	private float fBlue;
	private float fRed;
	private float fgRed;
	private float fgBlue;
	private float fgGreen;
	private float sRed;
	private float sBlue;
	private float sGreen;
	private DefaultTableModel model;
	private static final String VERTEX_SHADER =
			"#version 150\n" +
					"in vec4 vPosition;\n" +
					"\n" +
					"void main(void) {\n" +
					"  gl_PointSize = 5.0;\n" +
					"  gl_Position = vec4(vPosition.x, vPosition.y, 0.0, 1.0);\n" +
					"}\n";
	private static final String FRAGMENT_SHADER =
			"#version 150\n" +
					"uniform vec4 color;\n" +
					"out vec4 fColor;\n" +
					"\n" +
					"void main(void) {\n" +
					"  fColor = color;\n" +
					"}\n";
	
	/**
	 * Creates an instance of <code>SimpleEditor</code> class.
	 * The default title is used.
	 */
	public SimpleEditor() {
		this(TITLE);
	}

	/**
	 * Creates an instance of <code>SimpleEditor</code> class.
	 * 
	 * @param title The title of the application window.
	 */
	public SimpleEditor(String title) 
	{
		super(title);
		
		//create the menu items that run along the top
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		
		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);
		
		closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(this);
		fileMenu.add(closeMenuItem);
		
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);

		quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(this);
		fileMenu.add(quitMenuItem);

		menuBar.add(fileMenu);

		editMenu = new JMenu("Edit");

		copyMenuItem = new JMenuItem("Copy");
		copyMenuItem.addActionListener(this);
		editMenu.add(copyMenuItem);

		pasteMenuItem = new JMenuItem("Paste");
		pasteMenuItem.addActionListener(this);
		editMenu.add(pasteMenuItem);

		menuBar.add(editMenu);

		helpMenu = new JMenu("Help");
		aboutMenuItem = new JMenuItem("About Simple Editor");
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		
		//----------------------------------------------------------------------------------------
		model = new DefaultTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.getTableHeader().setBackground(Color.YELLOW);
		
		//set up the scrollable table left side
		scrollPane = new JScrollPane(table);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		//---------------------------------------------------------------------------------------
		
		//add the buttons and the dropdown menu to the top toolbar
		JToolBar top = new JToolBar();
		top.setLayout(new FlowLayout(FlowLayout.LEFT));
		String[] c = {"GL_POINTS", "GL_LINES", "GL_LINE_STRIP", "GL_LINE_LOOP", "GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN"};
		choices = new JComboBox<String>(c);
		choices.addActionListener(this);
		top.add(choices);
		bg = new JButton("Background");
		bg.addActionListener(this);
		fg = new JButton("Foreground");
		fg.addActionListener(this);
		s = new JButton("Selection");
		s.addActionListener(this);
		
		//set the button color and add them to the top
		bgColor = Color.WHITE;
		fgColor = Color.BLUE;
		sColor = Color.RED;
		bg.setBackground(bgColor);
		fg.setBackground(fgColor);
		s.setBackground(sColor);
		top.add(bg);
		top.add(fg);
		top.add(s);
        		
		//add top to the layout
		add(top, BorderLayout.PAGE_START);
		top.setVisible(true);
		
		//set up the splitpane
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(200);
		split.setLeftComponent(scrollPane);
		
		panel = new GLJPanel(new GLCapabilities(GLProfile.getMaxProgrammable(false))); 
		panel.addGLEventListener(this);
		
		split.setRightComponent(panel);
		split.setVisible(true);
		
		add(split, BorderLayout.CENTER);
		
	}

	/**
	 * The main method.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
        final SimpleEditor editor = new SimpleEditor();
        editor.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                editor.dispose();
                System.exit(0);
            }
        });

        editor.setSize(600, 400);
        editor.setVisible(true);
	}

	/**
	 * The action event handler that determines the source of the event
	 * and processes the event accordingly.
	 * 
	 * @param e The generated event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == openMenuItem) {
			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				file = fileChooser.getSelectedFile();
				loadCSV();
				model.addTableModelListener(this);
				table.getSelectionModel().addListSelectionListener(this);
				
				vertexData = new float[model.getColumnCount()*model.getRowCount()];
				for(int k = 0; k<model.getColumnCount(); k++)
				{
					for(int x = 0; x <model.getRowCount(); x++)
					{
						vertexData[x*model.getColumnCount() + k] = 
								Float.parseFloat((String)model.getValueAt(x, k));
					}
				}
			}
		}
		else if (source == closeMenuItem) 
		{
			file = null;
			model.setNumRows(0);
			model.setColumnCount(0);
			panel.repaint();
		}
		else if (source == saveMenuItem) 
		{
            try 
            {
            	FileWriter fw = new FileWriter(file);
            	textPane.write(fw);
            	fw.close();
            }
            catch (IOException ex) 
            {
                System.err.println(ex);
            }
		}
		else if (source == quitMenuItem) 
		{
			System.exit(0);
		}
		else if (source == copyMenuItem) 
		{
			textPane.copy();
		}
		else if (source == pasteMenuItem) 
		{
			textPane.paste();
		}
		else if (source == aboutMenuItem) 
		{
			JOptionPane.showMessageDialog(this, "Simple Editor version 1.");
		}
		//background button
		else if (source == bg)
		{
			buttonPressed = 1;
			cd = new ColorDialog(this, bgColor, this);	
	        cd.setVisible(true);			
		}
		//foreground button
		else if (source == fg)
		{
	        buttonPressed = 2;
			cd = new ColorDialog(this, fgColor, this);	
	        cd.setVisible(true);

		}
		//selection button
		else if (source == s)
		{
	        buttonPressed = 3;
			cd = new ColorDialog(this, sColor, this);	
	        cd.setVisible(true);
		}
		//	handles the listener for the JComboBox 
		else if (source == choices)
		{
			panel.repaint();
		}
	}

	/**
	 * draw and display what will be the GLJPanel
	 * @param arg0 open GL drawable
	 */
	public void display(GLAutoDrawable arg0) 
	{
		GL3 gl = arg0.getGL().getGL3();
		
		fRed = bgColor.getRed() / 255.0f;
		fBlue = bgColor.getBlue() / 255.0f;
		fGreen = bgColor.getGreen() / 255.0f;
		
		// Set the background color (white).
		gl.glClearColor(fRed, fGreen, fBlue, 1.0f);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
		
		gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);

		
		// Use the shader		
		gl.glUseProgram(shaderProgram);

		
		// Set the VBO.
		intBuffer = Buffers.newDirectIntBuffer(1);
		gl.glGenBuffers(1, intBuffer);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, intBuffer.get(0));
		
		
		// Use vertex data in the vertexData array as vPosition vertex shader variable.
		location = gl.glGetAttribLocation(shaderProgram, "vPosition");
		gl.glVertexAttribPointer(location, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(location);
		
		// Set the foreground color (white).
		fgRed = fgColor.getRed() / 255.0f;
		fgBlue = fgColor.getBlue() / 255.0f;
		fgGreen = fgColor.getGreen() / 255.0f;
		
		location = gl.glGetUniformLocation(shaderProgram, "color");
		gl.glUniform4f(location, fgRed, fgGreen, fgBlue, 1.0f);
		
		//set up the vertices
		if(vertexData != null)
		{			
			floatBuffer = Buffers.newDirectFloatBuffer(vertexData);
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexData.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);
		
			//JComboBox options
			//"GL_POINT", "GL_LINES", "GL_LINE_STRIP", "GL_LINE_LOOP",
			//"GL_TRIANGLES", "GL_TRIANGLE_STRIP", "GL_TRIANGLE_FAN"	
			
			if(choices.getSelectedItem() == ("GL_POINTS"))
			{
				gl.glDrawArrays(GL3.GL_POINTS, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINES"))
			{
				gl.glDrawArrays(GL3.GL_LINES, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINE_STRIP"))
			{
				gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINE_LOOP"))
			{
				gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLES"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLE_STRIP"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, vertexData.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLE_FAN"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, vertexData.length/2);
			}
		}
		//set up the selected vertices
		if(selVert != null)
		{
			// Set the foreground color (white).
			sRed = sColor.getRed() / 255.0f;
			sBlue = sColor.getBlue() / 255.0f;
			sGreen = sColor.getGreen() / 255.0f;
			
			//set foreground color
			location = gl.glGetUniformLocation(shaderProgram, "color");
			gl.glUniform4f(location, sRed, sGreen, sBlue, 1.0f);
			
			//set VAO
			floatBuffer = Buffers.newDirectFloatBuffer(selVert);
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, selVert.length * Buffers.SIZEOF_FLOAT, floatBuffer, GL3.GL_STATIC_DRAW);
			
			if(choices.getSelectedItem() == ("GL_POINTS"))
			{
				gl.glDrawArrays(GL3.GL_POINTS, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINES"))
			{
				gl.glDrawArrays(GL3.GL_LINES, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINE_STRIP"))
			{
				gl.glDrawArrays(GL3.GL_LINE_STRIP, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_LINE_LOOP"))
			{
				gl.glDrawArrays(GL3.GL_LINE_LOOP, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLES"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLES, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLE_STRIP"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLE_STRIP, 0, selVert.length/2);
			}
			else if(choices.getSelectedItem() == ("GL_TRIANGLE_FAN"))
			{
				gl.glDrawArrays(GL3.GL_TRIANGLE_FAN, 0, selVert.length/2);
			}
		}
	}
	/**
	 * Detaches and deletes the created shaders and the shader program.
	 *
	 * @param drawable OpenGL drawable.
	 */
	public void dispose(GLAutoDrawable arg0) 
	{
		GL3 gl = arg0.getGL().getGL3();
		gl.glDetachShader(shaderProgram, vertexShader);
		gl.glDeleteShader(vertexShader);
		gl.glDetachShader(shaderProgram, fragmentShader);
		gl.glDeleteShader(fragmentShader);
		gl.glDeleteProgram(shaderProgram);
		
	}
	/**
	 * Creates the shader program from source.
	 *
	 * @param drawable OpenGL drawable.
	 */
	public void init(GLAutoDrawable arg0) 
	{
		GL3 gl = arg0.getGL().getGL3();

		vertexShader = compile(gl, GL3.GL_VERTEX_SHADER, VERTEX_SHADER);
		fragmentShader = compile(gl, GL3.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
		shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vertexShader);
		gl.glAttachShader(shaderProgram, fragmentShader);
		gl.glLinkProgram(shaderProgram);
		
	}
	
	/**
	 * Overridden as an empty method.
	 *
	 * @param drawable OpenGL drawable.
	 * @param x The x coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param y The y coordinate of the lower left corner of the viewport rectangle, in pixels.
	 * @param width The width of the viewport.
	 * @param height The height of the viewport.
	 */
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) 
	{
		// TODO Auto-generated method stub
		
	}
/**
 * handles the events if a change listener is activated
 * 
 * @param arg0 the event that happens
 */
	public void stateChanged(ChangeEvent arg0) 
	{		
		Object source = arg0.getSource();
		if(source instanceof ColorSelectionModel)
		{
			System.out.println(buttonPressed);
				if(buttonPressed == 1)
				{
					bgColor = ((ColorSelectionModel) source).getSelectedColor();
					bg.setBackground(bgColor);
					panel.repaint();
				}
				else if(buttonPressed == 2)
				{
					fgColor = ((ColorSelectionModel) source).getSelectedColor();
					fg.setBackground(fgColor);
					panel.repaint();
				}
				else if(buttonPressed == 3)
				{
					sColor = ((ColorSelectionModel) source).getSelectedColor();
					s.setBackground(sColor);
					table.setSelectionBackground(sColor);
				}
	        
		}
   	}
	
	/**
	  * Repaints the graph when the model is updated.
	  * 
	  * @param e Table model event
	  */
	public void tableChanged(TableModelEvent arg0) 
	{
		for(int k = 0; k<model.getColumnCount(); k++)
		{
			for(int x = 0; x <model.getRowCount(); x++)
			{
				vertexData[x*model.getColumnCount() + k] = 
						Float.parseFloat((String)model.getValueAt(x, k));
			}
		}
		valueChanged(null);
		panel.repaint();
	}
	/**
	 * load the csv
	 */
	private void loadCSV()
	{
		try
		{
			try(BufferedReader in = new BufferedReader(new FileReader(file)))
			{
				parseColumnNames(in.readLine());
				String line;
				while ((line = in.readLine()) != null)
				{
					addData(line);
				}
			}
		}
		catch (IOException ex)
		{
			System.err.println(ex);
		}
	}
	/**
	 * splits line into sections and add them as columns to the model
	 * @param line string to be split
	 */
	private void parseColumnNames(String line)
	{
		String[] columns = line.split(",");
		for(String s : columns)
		{
			model.addColumn(s);
		}
	}
	/**
	 * splits the line into sections and add them as rows to the model
	 * @param line
	 */
	private void addData(String line)
	{
		String[] data = line.split(",");
		model.addRow(data);
	}
	/**
	 * resets the table when a value is changed
	 * @param arg0 List Selection Event
	 */
	public void valueChanged(ListSelectionEvent arg0) 
	{
		int k = table.getSelectedRows().length;
		selVert = new float[k*2];
		int count = 0;
		if(k>0)
		{
			int []rows = table.getSelectedRows();
			for(int x = 0; x<rows.length; x++)
			{
				selVert[count++] = Float.parseFloat((String)model.getValueAt(rows[x], 0));
				selVert[count++] = Float.parseFloat((String)model.getValueAt(rows[x], 1));
			}
		}
		else
		{
			selVert = null;
		}
			panel.repaint();
		
	}
	/**
	 * A utility method to create a shader
	 * 
	 * @param gl The OpenGL context.
	 * @param shaderType The type of the shader.
	 * @param program The string containing the program.
	 * @return The created shader.
	 */
	private int compile(GL3 gl, int shaderType, String program) 
	{
		int shader = gl.glCreateShader(shaderType);
		String[] lines = new String[] { program };
		int[] lengths = new int[] { lines[0].length() };
		gl.glShaderSource(shader, lines.length, lines, lengths, 0);
		gl.glCompileShader(shader);
		int[] compiled = new int[1];
		gl.glGetShaderiv(shader, GL3.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) 
		{
			int[] logLength = new int[1];
			gl.glGetShaderiv(shader, GL3.GL_INFO_LOG_LENGTH, logLength, 0);
			byte[] log = new byte[logLength[0]];
			gl.glGetShaderInfoLog(shader, logLength[0], (int[])null, 0, log, 0);
			System.err.println("Error compiling the shader: " + new String(log));
			System.exit(1);
		}
		return shader;
	}
	public void saveFile(String name)
	{
	try
	{
		FileWriter fWriter = new FileWriter(name);
		
		fWriter.append("X");
		fWriter.append(",");
		fWriter.append("Y");
		fWriter.append("\n");
		
		for(int k = 0; k < vertexData.length; k ++)
		{
			if(((k+1)%2)==0)
			{
				fWriter.append("" + vertexData[k]);
				fWriter.append("\n");
			}
			else
			{
				fWriter.append("" + vertexData[k] + ", ");
			}
		}
		fWriter.flush();
		fWriter.close();
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}
	}
}
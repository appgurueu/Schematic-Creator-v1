/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appguru;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author lars
 */
class fs implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser source = (JFileChooser) e.getSource();
        JFrame w = (JFrame) SwingUtilities.getRoot(source);
        w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
        if (source.getSelectedFile() != null && source.getSelectedFile().exists()) {
            try {
                if (SchematicCreator.im.isSelected()) {
                    SchematicCreator.layer_img = source.getSelectedFile().getName();
                    BufferedImage loaded = ImageIO.read(source.getSelectedFile());
                    SchematicCreator.canvas.setImage(loaded, "image", "preview");
                } else {
                    SchematicCreator.layer_dmap = source.getSelectedFile().getName();
                    BufferedImage loaded = ImageIO.read(source.getSelectedFile());
                    SchematicCreator.canvas.setImage(loaded, "depthmap", "depth_preview");
                }
            } catch (Exception ex) {
                System.err.println(ex);
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error ! Invalid Input !",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class cop implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser sav = new JFileChooser("Save Schematic");
        FileNameExtensionFilter ext = new FileNameExtensionFilter("Appguru Schematic(.ams)", ".ams");
        FileNameExtensionFilter ext2 = new FileNameExtensionFilter("World Edit Schematic(.we)", ".we");
        sav.addChoosableFileFilter(ext);
        sav.addChoosableFileFilter(ext2);
        int approved = sav.showSaveDialog(new JFrame("Save schematic"));
        if (approved == JFileChooser.APPROVE_OPTION) {
            Thread t = new Thread() {
                @Override
                public synchronized void run() {
                    try {
                        String p = sav.getSelectedFile().getCanonicalPath();
                        FileFilter extension = sav.getFileFilter();
                        String ts;
                        if (extension.getDescription().equals(ext.getDescription())) {
                            p += ".ams";
                            ts = new Schematic(Canvas.layers).toString();
                        } else {
                            p += ".we";
                            ts = new Schematic(Canvas.layers).toWe();
                        }
                        File f = new File(p);
                        f.createNewFile();
                        BufferedWriter w = new BufferedWriter(new FileWriter(f));
                        SchematicCreator.progress.setValue(50);
                        SchematicCreator.progress.setString("50 %");
                        w.write(ts);
                        System.gc();
                        SchematicCreator.progress.setValue(100);
                        SchematicCreator.progress.setString("100 %");
                        w.close();
                    } catch (Exception ex) {
                        /*System.err.println(*/
                        ex.printStackTrace(System.err);/*);*/
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Error ! Invalid Input !",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);

                    }
                }
            };
            t.start();
            SchematicCreator.progress.setString("0 %");
        }
    }

}

class sf implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame window2 = new JFrame();
        window2.setTitle("Select image");
        window2.setVisible(true);
        window2.setSize(400, 400);
        JFileChooser f = new JFileChooser();
        File file = new File("");
        f.setCurrentDirectory(file);
        f.addActionListener(new fs());
        window2.add(f);
    }

}

class hl implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(new JFrame(),
                "Schematic Creator v1 © Lars Müller @appguru.eu\n"
                + "A simple tool to create Appguru/Worldedit Schematics\n"
                + "Use it the following : \n"
                + "- create an image/blueprint, from top view\n"
                + "- you can also create a depthmap(greyscale)\n"
                + "Viewing Field left-blueprint, right-depthmap\n"
                + "Nodelist - (r,g,b,a)-mod:nodename\n"
                + "Layerlist - blueprint-depthmap-height\n"
                + "Select image - select an image\n"
                + "Image/Depthmap - is an image or depthmap being selected"
                + "Progress - Schematic Creation Progress\n"
                + "Help - this help popup\n"
                + "Post-editing is highly recommended, for example to rotate stairs etc.",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }

}

public class SchematicCreator {

    public static Color picked_color = Color.GREEN;
    public static Canvas canvas;
    public static JFrame window;
    public static JLabel path;
    public static File file;
    static boolean file_selected;
    public static JPanel pane;
    public static JProgressBar progress;
    public static JRadioButton im;
    public static JRadioButton dmap;
    public static JList<String> nodes;
    public static JList<String> layers;
    public static JSpinner height;
    public static String layer_dmap = "white", layer_img = "white";
    public static JScrollPane its_scroll;
    public static JScrollPane its_scroll2;

    public static void updateNodes() {
        String[] registered = Schematic.nodesList().split("\n");
        nodes.setListData(registered);
    }

    public static void main(String[] args) throws IOException {
        window = new JFrame();
        window.setTitle("Schematic Creator v1");
        window.setVisible(true);
        window.setSize(900, 350);
        //JFileChooser f=new JFileChooser();
        //f.addActionListener(l);
        nodes = new JList(new String[]{});
        nodes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        layers = new JList(new String[]{});
        layers.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        dmap = new JRadioButton("Depthmap");
        im = new JRadioButton("Image");
        im.setSelected(true);
        ButtonGroup imagetype = new ButtonGroup();
        imagetype.add(dmap);
        imagetype.add(im);
        JButton select_file = new JButton("Select Image");
        select_file.addActionListener(new sf());
        JButton create_objs = new JButton("Create Schematic");
        create_objs.addActionListener(new cop());
        JButton help = new JButton("Help");
        help.addActionListener(new hl());
        height = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        path = new JLabel();
        path = new JLabel();
        JLabel creator = new JLabel("© Lars Müller @appguru.eu");
        progress = new JProgressBar();
        progress.setString("0 %");
        progress.setStringPainted(true);
        //button.setSize(40,20);
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.RIGHT);
        JPanel lists = new JPanel();
        GridLayout grid2 = new GridLayout();
        grid2.setRows(1);
        grid2.setColumns(2);
        lists.setLayout(grid2);
        its_scroll = new JScrollPane(nodes);
        its_scroll2 = new JScrollPane(layers);
        JButton select_color = new JButton("Register node");
        select_color.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame a = new JFrame("Register node");
                a.setLayout(new GridLayout(3, 2));
                a.setSize(275, 75);
                JButton sc = new JButton("Select color");
                sc.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Color c = JColorChooser.showDialog(new JFrame(), "Pick a color", picked_color);
                        if (c != null) {
                            picked_color = c;
                        }
                    }
                });

                JTextField enter_nodename = new JTextField("mod:node");
                a.add(sc);
                JButton ll = new JButton("Load list");
                ll.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser sl = new JFileChooser("Select nodelist");
                        FileNameExtensionFilter ext = new FileNameExtensionFilter("Appguru Nodelist(.anl)", ".anl");
                        sl.setFileFilter(ext);
                        int chosen = sl.showOpenDialog(new JFrame("Select nodelist"));
                        if (chosen == JFileChooser.APPROVE_OPTION) {
                            FileReader f = null;
                            try {
                                File file = sl.getSelectedFile();
                                if (!file.exists()) {
                                    file = new File(file.getCanonicalPath() + ".anl");
                                }
                                f = new FileReader(file);
                                String s = "";
                                int c = f.read();
                                while (c != -1) {
                                    s += (char) c;
                                    c = f.read();
                                }
                                Schematic.loadNodeList(s);
                                updateNodes();
                                a.dispatchEvent(new WindowEvent(a, WindowEvent.WINDOW_CLOSING));
                            } catch (FileNotFoundException ex) {
                                System.err.println(ex);
                                JOptionPane.showMessageDialog(new JFrame(),
                                        "Error ! Invalid Input !",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(new JFrame(),
                                        "Error ! Invalid Input !",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } finally {
                                try {
                                    f.close();
                                } catch (Exception ex) {
                                    System.err.println(ex);
                                    JOptionPane.showMessageDialog(new JFrame(),
                                            "Error ! Invalid Input !",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                });
                a.add(ll);
                a.add(new JLabel("Enter nodename : "));
                a.add(enter_nodename);
                JButton r = new JButton("Register");
                r.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Schematic.nodes.put(picked_color, enter_nodename.getText());
                        updateNodes();
                        a.dispatchEvent(new WindowEvent(a, WindowEvent.WINDOW_CLOSING));
                    }
                });
                a.add(r);
                a.setVisible(true);
            }
        });
        JButton rn = new JButton("Remove node");
        rn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nodes.getSelectedIndex() == -1) {
                    return;
                }
                String[] selection = nodes.getSelectedValue().split("-")[0].split(",");
                Color farbe = new Color(Integer.parseInt(selection[0]), Integer.parseInt(selection[1]), Integer.parseInt(selection[2]), Integer.parseInt(selection[3]));
                Schematic.nodes.remove(farbe);
                updateNodes();
            }
        });
        JButton sv = new JButton("Save nodelist");
        sv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser sav = new JFileChooser("Save nodelist");
                FileNameExtensionFilter ext = new FileNameExtensionFilter("Appguru Nodelist(.anl)", ".anl");
                sav.setFileFilter(ext);
                int approved = sav.showSaveDialog(new JFrame("Save nodelist"));
                if (approved == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = new File(sav.getSelectedFile().getCanonicalPath() + ".anl");

                        f.createNewFile();
                        FileWriter fw = new FileWriter(f);
                        fw.write(Schematic.nodesList());
                        fw.close();
                    } catch (Exception ex) {
                        System.err.println(ex);
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Error ! Invalid Input !",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        JPanel nodes_pane = new JPanel(new BorderLayout());
        JPanel c = new JPanel(new GridLayout(1, 2));
        c.add(rn);
        c.add(select_color);
        nodes_pane.add(sv, BorderLayout.PAGE_START);
        nodes_pane.add(c);
        nodes_pane.add(its_scroll, BorderLayout.PAGE_END);
        JPanel layers_pane = new JPanel(new BorderLayout());
        JPanel radios = new JPanel(new BorderLayout());
        radios.add(im, BorderLayout.LINE_START);
        radios.add(dmap, BorderLayout.LINE_END);
        JPanel ar = new JPanel(new GridLayout(2, 2));
        JButton al = new JButton("Add layer");
        al.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = layers.getSelectedIndex();
                if (index == -1) {
                    index = Canvas.layers.size();
                }
                Canvas.layers.add(index, new Tuple(new BufferedImage[]{canvas.image, canvas.depthmap}, (int) height.getValue()));
                ListModel<String> m = layers.getModel();
                String[] registered = new String[m.getSize() + 1];
                for (int i = 0; i < m.getSize(); i++) {
                    registered[i] = m.getElementAt(i);
                }
                registered[registered.length - 1] = layer_img + "-" + layer_dmap + "-" + Integer.toString((int) height.getValue());
                layers.setListData(registered);
            }
        });
        ar.add(al);
        JButton rl = new JButton("Remove layer");
        rl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = layers.getSelectedIndex();
                if (index == -1) {
                    return;
                }
                Canvas.layers.remove(index);
                ListModel<String> m = layers.getModel();
                String[] registered = new String[m.getSize() - 1];
                int rc = 0;
                for (int i = 0; i < m.getSize(); i++) {
                    if (i != index) {
                        registered[rc] = m.getElementAt(i);
                        rc++;
                    }
                }
                layers.setListData(registered);
            }
        });
        ar.add(rl);
        ar.add(new JLabel("Height : "));
        ar.add(height);
        layers_pane.add(select_file, BorderLayout.LINE_START);
        layers_pane.add(radios, BorderLayout.LINE_END);
        layers_pane.add(ar, BorderLayout.PAGE_START);
        layers_pane.add(its_scroll2, BorderLayout.PAGE_END);
        lists.add(nodes_pane);
        lists.add(layers_pane);
        pane = new JPanel();
        GridLayout grid = new GridLayout();
        grid.setRows(4);
        grid.setColumns(2);
        pane.setLayout(grid);
        pane.add(create_objs);
        pane.add(help);
        pane.add(new JLabel("Progress : "));
        pane.add(progress);
        pane.add(creator);
        //window.add(canvas);
        canvas = new Canvas();
        canvas.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        canvas.setFocusable(true);
        //canvas.setAlignmentX(0);
        //canvas.setAlignmentY(0);
        window.getContentPane().add(canvas);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent c) {
                System.out.println("Application exited !");
                System.exit(0);
            }
        });
        //window.add(pane);
        //pane.add(canvas);
        JPanel container = new JPanel(new BorderLayout());
        container.add(lists, BorderLayout.PAGE_START);
        container.add(pane, BorderLayout.PAGE_END);
        canvas.add(container);
        canvas.setLayout(flow);
        //window.add(canvas);
        long then = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - then > 50) {
                window.repaint();
                window.revalidate();
                canvas.revalidate();
                canvas.repaint();
                //pane.repaint();
                then = System.currentTimeMillis();
                System.gc();
            }
        }
        //window.getContentPane().add(button);
        // TODO code application logic here
    }
}

class Schematic {

    public static HashMap<Color, String> nodes = new HashMap();
    public ArrayList<String> used;
    public int[][][] layers;

    public static String nodesList() {
        if (nodes.size() == 0) {
            return "";
        }
        String s = "";
        for (Entry<Color, String> n : nodes.entrySet()) {
            s += "\n";
            s += String.format("%d,%d,%d,%d-", n.getKey().getRed(), n.getKey().getGreen(), n.getKey().getBlue(), n.getKey().getAlpha()) + n.getValue();
        }
        return s.substring(1);
    }

    public static void loadNodeList(String s) {
        String[] lines = s.split("\n");
        for (String line : lines) {
            String[] parts = line.split("-");
            String[] rgba = parts[0].split(",");
            int[] rgba_i = new int[]{Integer.parseInt(rgba[0]), Integer.parseInt(rgba[1]), Integer.parseInt(rgba[2]), Integer.parseInt(rgba[3])};
            nodes.put(new Color(rgba_i[0], rgba_i[1], rgba_i[2], rgba_i[3]), parts[1]);
        }
    }

    public static String best_node(Color c) {
        double c_red = c.getRed() / 255.0f;
        double c_blue = c.getBlue() / 255.0f;
        double c_green = c.getGreen() / 255.0f;
        double c_alpha = c.getAlpha() / 255.0f;
        String n = nodes.get(c);
        if (n != null) {
            return n;
        }
        String best_node = "?";
        double best_weight = 0.0f;
        for (Entry<Color, String> e : nodes.entrySet()) {
            Color b = e.getKey();
            double b_red = b.getRed() / 255.0f;
            double b_blue = b.getBlue() / 255.0f;
            double b_green = b.getGreen() / 255.0f;
            double b_alpha = b.getAlpha() / 255.0f;
            double d_red = 1.0f - Math.abs(b_red - c_red);
            double d_blue = 1.0f - Math.abs(b_blue - c_blue);
            double d_green = 1.0f - Math.abs(b_green - c_green);
            double d_alpha = 1.0f - Math.abs(b_alpha - c_alpha);
            double this_w = Math.cbrt(d_red * d_blue * d_green * d_alpha);
            if (this_w >= best_weight) {
                best_weight = this_w;
                best_node = e.getValue();
            }
        }
        return best_node;
    }

    public Schematic(ArrayList<Tuple<BufferedImage[], Integer>> blueprints) {
        used = new ArrayList();
        used.add("default:air");
        int w = blueprints.get(0).v1[0].getWidth();
        int l = blueprints.get(0).v1[0].getHeight();
        int h = 0;
        for (int i = 0; i < blueprints.size(); i++) {
            Tuple<BufferedImage[], Integer> t = blueprints.get(i);
            h += t.v2;
        }
        layers = new int[h][w][l];
        h = 0;
        for (int i = 0; i < blueprints.size(); i++) {
            Tuple<BufferedImage[], Integer> t = blueprints.get(i);
            for (int x = 0; x < t.v1[0].getWidth(); x++) {
                for (int z = 0; z < t.v1[0].getHeight(); z++) {
                    String node_there = best_node(new Color(t.v1[0].getRGB(x, z), true));
                    int node = -1;
                    for (int f = 0; f < used.size(); f++) {
                        if (used.get(f).equals(node_there)) {
                            node = f;
                        }
                    }
                    if (node == -1) {
                        node = used.size();
                        used.add(node_there);
                    }
                    int height = (int) ((new Color(t.v1[1].getRGB(x, z)).getRed() / 255.0f) * t.v2);
                    for (int j = 0; j < height; j++) {
                        layers[h + j][x][z] = node;
                    }
                }
            }
            h += t.v2;
        }
    }

    @Override
    public String toString() {
        String s = String.format("Dimensions\n%d %d %d", layers[0].length, layers.length, layers[0][0].length);
        s += "\nNodes\n";
        for (int i = 0; i < used.size(); i++) {
            s += used.get(i);
            if (i < used.size() - 1) {
                s += ", ";
            }
        }
        s += "\nLayers\n";
        for (int[][] layer : layers) {
            for (int[] row : layer) {
                int i = 0;
                for (int node : row) {
                    s += Integer.toString(node);
                    i++;
                    if (i != row.length) {
                        s += "|";
                    }
                }
                s += "\n";
            }
            s += "-\n";
        }
        return s;
    }

    public String toWe() {
        String s = "5:return {";
        for (int y = 0; y < layers.length; y++) {
            for (int x = 0; x < layers[y].length; x++) {
                for (int z = 0; z < layers[y][x].length; z++) {
                    if (layers[y][x][z] != 0) {
                        String nodename = used.get(layers[y][x][z]);
                        s += String.format("{[\"x\"] = %d, [\"meta\"] = {[\"fields\"] = {}, [\"inventory\"] = {}}, [\"y\"] = %d, [\"z\"] = %d, [\"name\"] = \"%s\"},", x, y, z, nodename);
                    }
                }
            }
        }
        return s.substring(0, s.length() - 1) + "}";
    }
}

class Canvas extends JPanel {

    public static ArrayList<Tuple<BufferedImage[], Integer>> layers = new ArrayList();
    public int dim;
    public BufferedImage image;
    public BufferedImage preview;
    public BufferedImage bg;
    public static final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    public BufferedImage depthmap;
    public BufferedImage depth_preview;

    public Canvas() {
        try {
            dim = 152;
            bg = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
            Graphics2D bgg = bg.createGraphics();
            int c = 0;
            Color g1 = new Color(70, 70, 70);
            Color g2 = new Color(140, 140, 140);
            for (int x = 0; x <= dim / 4; x++) {
                for (int y = 0; y <= dim / 4; y++) {
                    bgg.setColor(g2);
                    if (c % 2 == 0) {
                        bgg.setColor(g1);
                    }
                    bgg.fillRect(x * 4, y * 4, 4, 4);
                    c++;
                }
            }
            BufferedImage white = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            BufferedImage white2 = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < white.getWidth(); x++) {
                for (int y = 0; y < white.getHeight(); y++) {
                    white.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
            for (int x = 0; x < white2.getWidth(); x++) {
                for (int y = 0; y < white2.getHeight(); y++) {
                    white2.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
            setImage(white2, "depthmap", "depth_preview");
            setImage(white, "image", "preview");
            //setImage(new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB));
        } catch (Exception ex) {
            System.err.println(ex);
            JOptionPane.showMessageDialog(new JFrame(),
                    "Error ! Invalid Input !",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    public void setImage(BufferedImage img, String targets, String preview_targets) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field target_field = this.getClass().getField(targets);
        Field preview_target_field = this.getClass().getField(preview_targets);
        BufferedImage target = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                target.setRGB(x, y, img.getRGB(x, y));
            }
        }
        target_field.set(this, target);
        BufferedImage preview_target = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scale = new AffineTransform();
        double relx;
        double rely;
        relx = (double) dim / img.getWidth();
        rely = relx;
        scale.scale(relx, rely);
        AffineTransformOp apply_scale = new AffineTransformOp(scale, antialiasing);
        apply_scale.filter(img, preview_target);
        preview_target_field.set(this, preview_target);
    }

    public void drawImage(Graphics2D g, BufferedImage img, int x, int y) {
        g.drawImage(bg, x + 2, y + 2, this);
        if (img != null) {
            g.drawImage(img, x + 2, y + 2, this);
        }
        g.setStroke(new BasicStroke(2));
        g.setColor(new Color(155, 155, 255));
        g.drawRect(x + 1, y + 1, dim + 2, dim + 2);
    }

    @Override
    public void paintComponent(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
        drawImage(g, preview, 0, 0);
        drawImage(g, depth_preview, dim + 1, 0);
    }
}

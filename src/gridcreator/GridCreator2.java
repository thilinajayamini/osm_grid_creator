/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gridcreator;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 *
 * @author Thilina Jayamini - thilinajayamini@gmail.com
 */
public class GridCreator2 {

    public static void main(String[] args) {
        new GridCreator2().genarate();
    }

    private DecimalFormat fm;
    UIM m;

    public void genarate() {
        //UI m = new UI();
        m = new UI2();
        m.setVisible(true);

        Grid g = getGrid();

        saveImg(g.getUrl());
        saveCSS(g);

        saveHTML(g);

        m.dispose();
        display("All Done !!!");
    }

    private Grid getGrid() {
        Properties p = getProps();

        double top = 0, bottom = 0, left = 0, right = 0, presetX = 0, presetY = 0;

        try {
            top = Double.parseDouble(p.getProperty("top"));
            bottom = Double.parseDouble(p.getProperty("bottom"));
            left = Double.parseDouble(p.getProperty("left"));
            right = Double.parseDouble(p.getProperty("right"));

            presetX = Double.parseDouble(p.getProperty("presetX"));
            presetY = Double.parseDouble(p.getProperty("presetY"));

        } catch (NumberFormatException e) {
            displayErr("There is is non number value with in \n bounding box parameter (top,botom,left, right)\n or in preset values (presetX,presetY)");
        }

        String format = p.getProperty("format");
        fm = new DecimalFormat(format);

        return new Grid(top, bottom, left, right, presetX, presetY, p.getProperty("uid"));
    }

    private Properties getProps() {
        display("Loading configuration");
        Properties p = new Properties();
        try {
            p.load(new FileReader("settings.cfg"));
        } catch (IOException ex) {
            displayErr("Error: No settings file (settings.cfg) found . ");
        }
        return p;
    }

    private String getHeadder() {
        String h = "<html>\n<head><title>OSM Update Utility</title>\n"
                + "<link href=\"grid.css\" rel=\"stylesheet\" type=\"text/css\"/>\n"
                //+ "<script type=\"text/javascript\">\n   init();\n </script>"
                + "<!-- Generated from OSM update utility(version 1.0) -->\n"
                + "<script src=\"script.js\" type=\"text/javascript\"></script>\n"
                + "<!-- Thilina Jayamini | http://lkosm.freeforums.net/ -->\n</head>\n"
                + "\n<body onload=\"init()\">\n";
        return h;

    }

    private void saveImg(String url) {
        display("Creating image");

        try {
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("back.png");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            displayErr("Error : error in connecting to openstrett map server. "
                    + "\n Please check you innetnet connection.\n You should have direct connection."
                    + "\n Proxy not supported. \n Or application does't have permission to write here");
        }
    }

    private void saveCSS(Grid g) {
        int[] dim = calculateWidhHeight();
        StringBuilder sb = new StringBuilder();

        sb.append(".table_body{ background-image:url('back.png'); width: ").append(dim[0]).append("px;}");
        sb.append(lineSeparator());

        sb.append(".table_row1{ height: ").append(1 + (int) (dim[1] / (g.getFactorY() + 1))).append("px;}");
        sb.append(lineSeparator());

        sb.append(".table_row2{ height: ").append((int) (dim[1] / (g.getFactorY() + 1))).append("px;}");
        sb.append(lineSeparator());

        saveFile(sb, "grid.css");

    }

    private void saveHTML(Grid g) {
        display("Creating html");
        StringBuilder sb = new StringBuilder();
        sb.append(getHeadder()).append(lineSeparator());
        sb.append("<div id=\"uid\" hidden=\"\">").append(g.getId()).append("</div>\n").append("<table class=\"table_body\" border=\"1\">");

        int idx = 0;

        for (int i = 0; i < g.getFactorY(); i++) {

            if (i % 2 == 0) {
                sb.append("<tr class=\"table_row1\" >").append(lineSeparator());
            } else {
                sb.append("<tr class=\"table_row2\" >").append(lineSeparator());
            }

            for (int j = 0; j < g.getFactorX(); j++) {

                sb.append("<td><div id=\"").append(idx++);
                sb.append("\" onContextMenu=\"veryfy(this);return false;\"><a href=\"http://localhost:8111/load_and_zoom?top=");

                sb.append(fm.format(g.getTop(i))).append("&amp;bottom=").append(fm.format(g.getBottom(i)));

                sb.append("&amp;left=").append(fm.format(g.getLeft(j))).append("&amp;right=").append(fm.format(g.getRight(j)));

                sb.append("\" target=\"josm\" title=\"JOSM\">J</a></div></td>").append(lineSeparator());

            }
            sb.append("</tr>").append(lineSeparator());
        }

        sb.append("</table>\n<p>visit us  <a href=\"http://lkosm.freeforums.net\" >http://lkosm.freeforums.net</a></p>\n</body>\n</html>").append(lineSeparator());
        saveFile(sb, "Grid.htm");
    }

    private void saveFile(StringBuilder sb, String fileName) {
        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.print(sb.toString());
            pw.flush();
        } catch (Exception ex) {
            displayErr("Application does't have permission to write here");
        }

    }

    private int[] calculateWidhHeight() {
        BufferedImage bimg;
        int dim[] = new int[2];
        try {
            bimg = ImageIO.read(new File("back.png"));
            dim[0] = bimg.getWidth();
            dim[1] = bimg.getHeight();
        } catch (IOException ex) {
            displayErr("Error : Cannot find image file");
        }
        return dim;
    }

    private void stay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {

        }
    }

    private void display(String msg) {
        m.setContent(msg);
        m.repaint();
        stay();
    }

    private void displayErr(String msg) {
        m.setMyFont(new Font("Arial", Font.PLAIN, 10));
        display(msg);
        m.dispose();
        System.exit(0);
    }

    private String lineSeparator() {
        return "\n";
    }

}

class Grid {

    private final double top, left, latiDiff, longDiff, latiDelta, longDelta;
    private final String url, id;
    private final int factorY;
    private final int factorX;

    public Grid(double top, double bottom, double left, double right, double presetX, double presetY, String uid) {
        this.top = top;
        this.left = left;

        this.latiDiff = top - bottom;
        this.longDiff = left - right;

        this.factorY = Math.abs((int) (latiDiff / presetY));
        this.factorX = Math.abs((int) (longDiff / presetX));

        latiDelta = latiDiff / factorY;
        longDelta = longDiff / factorX;

        this.id = uid;
        this.url = "http://render.openstreetmap.org/cgi-bin/export?bbox=" + left + "," + bottom + "," + right + "," + top + "&scale=" + (factorX * 10000) + "&format=png";
    }

    public String getId() {
        if (this.id == null || this.id.trim().isEmpty()) {
            return String.valueOf(Math.round(0.5f));
        }
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getFactorX() {
        return factorX;
    }

    public int getFactorY() {
        return factorY;
    }

    public double getTop(int i) {
        return this.top - (latiDelta * i);
    }

    public double getBottom(int i) {
        i++;
        return getTop(i);
    }

    public double getLeft(int j) {
        return this.left - (longDelta * j);
    }

    public double getRight(int j) {
        j++;
        return getLeft(j);
    }

}

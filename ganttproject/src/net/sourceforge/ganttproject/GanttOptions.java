/***************************************************************************
 GanttOptions.java  -  description
 -------------------
 begin                : mar 2003
 copyright            : (C) 2003 by Thomas Alexandre
 email                : alexthomas(at)ganttproject.org
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package net.sourceforge.ganttproject;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JToolBar;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.ganttproject.document.Document;
import net.sourceforge.ganttproject.document.DocumentCreator;
import net.sourceforge.ganttproject.document.DocumentManager;
import net.sourceforge.ganttproject.document.DocumentsMRU;
import net.sourceforge.ganttproject.gui.GanttLookAndFeelInfo;
import net.sourceforge.ganttproject.gui.GanttLookAndFeels;
import net.sourceforge.ganttproject.gui.UIConfiguration;
import net.sourceforge.ganttproject.gui.options.model.GP1XOptionConverter;
import net.sourceforge.ganttproject.gui.options.model.GPOption;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.io.CSVOptions;
import net.sourceforge.ganttproject.io.GanttXMLOpen;
import net.sourceforge.ganttproject.language.GanttLanguage;
import net.sourceforge.ganttproject.parser.IconPositionTagHandler;
import net.sourceforge.ganttproject.parser.RoleTagHandler;
import net.sourceforge.ganttproject.roles.Role;
import net.sourceforge.ganttproject.roles.RoleManager;
import net.sourceforge.ganttproject.roles.RoleSet;
import net.sourceforge.ganttproject.util.ColorConvertion;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The class aible to load and save options on the file
 */
public class GanttOptions {

    private GanttLanguage language = GanttLanguage.getInstance();

    // private Color color;

    private int x = 0, y = 0, width = 800, height = 600;

    private String styleClass = "", styleName = "";

    private GanttLookAndFeelInfo lookAndFeel;

    private boolean isloaded;

    private boolean automatic;

    // private boolean dragTime;

    private boolean openTips;

    private boolean redline;

    private int lockDAVMinutes;

    private int undoNumber;

    private String xslDir;

    private String xslFo;

    private String workingDir = "";

    private final RoleManager myRoleManager;

    private DocumentsMRU documentsMRU;

    private UIConfiguration myUIConfig;

    private Font myChartMainFont;

    private String sTaskNamePrefix;

    private int toolBarPosition;

    private boolean bShowStatusBar;

    private String iconSize;

    public static final int ICONS = 0;

    public static final int ICONS_TEXT = 1;

    public static final int TEXT = 2;

    private int buttonsshow;

    /** FTP options */
    private String FTPUrl = "";

    private String FTPDirectory = "";

    private String FTPUser = "";

    private String FTPPwd = "";

    /** Export options. */
    private boolean bExportName;

    private boolean bExportComplete;

    private boolean bExportRelations;

    private boolean bExport3DBorders;

    /** CVS export options. */
    private CSVOptions csvOptions;

    private int[] iconListAsIntArray;

    private int[] deletedIconListAsIntArray;

    private String iconListAsString;

    private String deletedIconListAsString;

    private boolean isOnlyViewer;

    public static final int SEPARATOR = 0;

    public static final int EXIT = 1;

    public static final int NEW = 2;

    public static final int OPEN = 3;

    public static final int SAVE = 4;

    public static final int SAVEAS = 5;

    public static final int IMPORT = 6;

    public static final int EXPORT = 7;

    public static final int PRINT = 8;

    public static final int CUT = 9;

    public static final int COPY = 10;

    public static final int PASTE = 11;

    public static final int NEWTASK = 12;

    public static final int DELETE = 13;

    public static final int PROPERTIES = 14;

    public static final int UNLINK = 15;

    public static final int LINK = 16;

    public static final int IND = 17;

    public static final int UNIND = 18;

    public static final int UP = 19;

    public static final int DOWN = 20;

    public static final int PREV = 21;

    public static final int CENTER = 22;

    public static final int NEXT = 23;

    public static final int ZOOMOUT = 24;

    public static final int ZOOMIN = 25;

    public static final int UNDO = 26;

    public static final int REDO = 27;

    public static final int CRITICAL = 28;

    public static final int ABOUT = 29;

    public static final int SAVECURRENT = 30;

    public static final int COMPAREPREV = 31;

    public static final int REFRESH = 32;

    public static final int PREVIEWPRINT = 33;

    private Map/*<String,GPOption>*/ myGPOptions = new LinkedHashMap();
    private Map/*<String,GP1XOptionConverter>*/ myTagDotAttribute_Converter = new HashMap();

	private final DocumentManager myDocumentManager;
    /** Default constructor. */
    public GanttOptions(RoleManager roleManager, DocumentManager documentManager, boolean isOnlyViewer) {
    	myDocumentManager = documentManager;
        myRoleManager = roleManager;
        this.isOnlyViewer = isOnlyViewer;
        initByDefault();
        try {
            this.workingDir = System.getProperty("user.home");
        } catch (AccessControlException e) {
            // This can happen when running in a sandbox (Java WebStart)
            System.err.println(e + ": " + e.getMessage());
        }
    }

    /** Init the options by default. */
    public void initByDefault() {
        automatic = false;
        // dragTime = true;
        openTips = true;
        redline = false;
        lockDAVMinutes = 240;
        undoNumber = 50;
        xslDir = GanttOptions.class.getResource("/xslt").toString();
        xslFo = GanttOptions.class.getResource("/xslfo/ganttproject.xsl")
                .toString();
        sTaskNamePrefix = "";
        toolBarPosition = JToolBar.HORIZONTAL;
        bShowStatusBar = true;
        iconSize = "16"; // must be 16 small, 24 for big (32 for extra big
        // not directly include on UI)
        buttonsshow = GanttOptions.ICONS;
        /** Export options. */
        bExportName = true;
        bExportComplete = true;
        bExportRelations = true;
        bExport3DBorders = false;
        /** CVS export options. */
        csvOptions = new CSVOptions();

        // Icon status bar
        iconListAsString = getDefaultIconListAsString();
        iconListAsIntArray = getDefaultIconListIntArray();
        deletedIconListAsString = getDefaultDeletedIconListAsString();
        deletedIconListAsIntArray = getDefaultDeletedIconListIntArray();

    }

    // iconListAsIntArray = initIconList ();

    /**
     * Constructor. public GanttOptions(Color c, int x, int y, int width, int
     * height, GanttLookAndFeelInfo lookAndFeel, boolean automatic, boolean
     * dragTime, String xslDir, String xslFo,String workingDir, boolean tips,
     * boolean redline, int lockDAVMinutes, DocumentsMRU documentsMRU,
     * UIConfiguration uiConfiguration, RoleManager roleManager) { myRoleManager =
     * roleManager; color = c; this.x = x; this.y = y; this.width = width;
     * this.height = height; this.lookAndFeel = lookAndFeel;
     * this.automatic=automatic; this.dragTime=dragTime; this.openTips=tips;
     * this.redline=redline; this.lockDAVMinutes=lockDAVMinutes; this.myUIConfig =
     * uiConfiguration; if(xslFo!=null) this.xslFo=xslFo; if (xslDir != null)
     * this.xslDir = xslDir; try { this.workingDir =
     * System.getProperty("user.home"); } catch (AccessControlException e) { //
     * This can happen when running in a sandbox (Java WebStart)
     * System.err.println (e + ": " + e.getMessage()); } if (workingDir != null &&
     * new File(workingDir).exists()) this.workingDir = workingDir;
     * this.documentsMRU = documentsMRU; }
     */
    private void startElement(String name, Attributes attrs,
            TransformerHandler handler) throws SAXException {
        handler.startElement("", name, name, attrs);
    }

    private void endElement(String name, TransformerHandler handler)
            throws SAXException {
        handler.endElement("", name, name);
    }

    private void addAttribute(String name, String value, AttributesImpl attrs) {
        if (value != null) {
            attrs.addAttribute("", name, name, "CDATA", value);
        } else {
            System.err.println("[GanttOptions] attribute '" + name
                    + "' is null");
        }
    }

    private void emptyElement(String name, AttributesImpl attrs,
            TransformerHandler handler) throws SAXException {
        startElement(name, attrs, handler);
        endElement(name, handler);
        attrs.clear();
    }

    /**
     * Save the options file
     */
    public void save() {
        try {

            String sFileName = ".ganttproject";
            /*
             * if(System.getProperty("os.name").startsWith("Windows") ||
             * System.getProperty("os.name").startsWith("Mac")) sFileName =
             * "ganttproject.ini";
             */

            File file = new File(System.getProperty("user.home")
                    + System.getProperty("file.separator") + sFileName);
            // DataOutputStream fout = new DataOutputStream(new
            // FileOutputStream(file));
            TransformerHandler handler = ((SAXTransformerFactory) SAXTransformerFactory
                    .newInstance()).newTransformerHandler();
            Transformer serializer = handler.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");
            handler.setResult(new StreamResult(file));
            handler.startDocument();
            // handler.startDTD("ganttproject.sourceforge.net",
            // "-//GanttProject.org//DTD GanttProject-1.x//EN",
            // "http://ganttproject.sourceforge.net/dtd/ganttproject.dtd");
            // handler.endDTD();
            //
            AttributesImpl attrs = new AttributesImpl();
            handler.startElement("", "ganttproject-options",
                    "ganttproject-options", attrs);
            //
            attrs.addAttribute("", "selection", "selection", "CDATA", language
                    .getText("shortLanguage"));
            handler.startElement("", "language", "language", attrs);
            handler.endElement("", "language", "language");
            attrs.clear();
            // write the task Color
            /*
             * Color color = getUIConfiguration().getTaskColor();
             * attrs.addAttribute("", "red", "red", "CDATA", ""+color.getRed());
             * attrs.addAttribute("", "green", "green", "CDATA",
             * ""+color.getGreen()); attrs.addAttribute("", "blue", "blue",
             * "CDATA", ""+color.getBlue()); handler.startElement("",
             * "task-color", "task-color", attrs); handler.endElement("",
             * "task-color", "task-color"); attrs.clear();
             */

            Color resourceColor = myUIConfig.getResourceColor();
            if (resourceColor != null)
                attrs.addAttribute("", "resources", "resources", "CDATA", ""
                        + ColorConvertion.getColor(resourceColor));
            Color resourceOverloadColor = myUIConfig.getResourceOverloadColor();
            if (resourceOverloadColor != null)
                attrs.addAttribute("", "resourcesOverload",
                        "resourcesOverload", "CDATA", ""
                                + ColorConvertion
                                        .getColor(resourceOverloadColor));
            Color resourceUnderloadColor = myUIConfig
                    .getResourceUnderloadColor();
            if (resourceUnderloadColor != null)
                attrs.addAttribute("", "resourcesUnderload",
                        "resourcesUnderload", "CDATA", ""
                                + ColorConvertion
                                        .getColor(resourceUnderloadColor));
            Color weekEndColor = myUIConfig.getWeekEndColor();
            if (weekEndColor != null)
                attrs.addAttribute("", "weekEnd", "weekEnd", "CDATA", ""
                        + ColorConvertion.getColor(weekEndColor));
            Color daysOffColor = myUIConfig.getDayOffColor();
            if (daysOffColor != null)
                attrs.addAttribute("", "daysOff", "daysOff", "CDATA", ""
                        + ColorConvertion.getColor(daysOffColor));
            handler.startElement("", "colors", "colors", attrs);
            handler.endElement("", "colors", "colors");
            attrs.clear();

            // Geometry of the window
            addAttribute("x", "" + x, attrs);
            addAttribute("y", "" + y, attrs);
            addAttribute("width", "" + width, attrs);
            addAttribute("height", "" + height, attrs);
            emptyElement("geometry", attrs, handler);
            // look'n'feel
            addAttribute("name", lookAndFeel.getName(), attrs);
            addAttribute("class", lookAndFeel.getClassName(), attrs);
            emptyElement("looknfeel", attrs, handler);

            // ToolBar position
            addAttribute("position", "" + toolBarPosition, attrs);
            addAttribute("icon-size", "" + iconSize, attrs);
            addAttribute("show", "" + buttonsshow, attrs);
            emptyElement("toolBar", attrs, handler);
            addAttribute("show", "" + bShowStatusBar, attrs);
            emptyElement("statusBar", attrs, handler);

            // Export options
            addAttribute("name", "" + bExportName, attrs);
            addAttribute("complete", "" + bExportComplete, attrs);
            addAttribute("border3d", "" + bExport3DBorders, attrs);
            addAttribute("relations", "" + bExportRelations, attrs);
            emptyElement("export", attrs, handler);

            // csv export options
            startElement("csv-export", attrs, handler);
            addAttribute("fixed", "" + csvOptions.bFixedSize, attrs);
            addAttribute("separatedChar", "" + csvOptions.sSeparatedChar, attrs);
            addAttribute("separatedTextChar", ""
                    + csvOptions.sSeparatedTextChar, attrs);
            emptyElement("csv-general", attrs, handler);

            addAttribute("id", "" + csvOptions.bExportTaskID, attrs);
            addAttribute("name", "" + csvOptions.bExportTaskName, attrs);
            addAttribute("start-date", "" + csvOptions.bExportTaskStartDate,
                    attrs);
            addAttribute("end-date", "" + csvOptions.bExportTaskEndDate, attrs);
            addAttribute("percent", "" + csvOptions.bExportTaskPercent, attrs);
            addAttribute("duration", "" + csvOptions.bExportTaskDuration, attrs);
            addAttribute("webLink", "" + csvOptions.bExportTaskWebLink, attrs);
            addAttribute("resources", "" + csvOptions.bExportTaskResources,
                    attrs);
            addAttribute("notes", "" + csvOptions.bExportTaskNotes, attrs);
            emptyElement("csv-tasks", attrs, handler);

            addAttribute("id", "" + csvOptions.bExportResourceID, attrs);
            addAttribute("name", "" + csvOptions.bExportResourceName, attrs);
            addAttribute("mail", "" + csvOptions.bExportResourceMail, attrs);
            addAttribute("phone", "" + csvOptions.bExportResourcePhone, attrs);
            addAttribute("role", "" + csvOptions.bExportResourceRole, attrs);
            emptyElement("csv-resources", attrs, handler);

            endElement("csv-export", handler);

            // automatic popup launch
            addAttribute("value", "" + automatic, attrs);
            emptyElement("automatic-launch", attrs, handler);
            // automaticdrag time on the chart
            // addAttribute("value", ""+dragTime, attrs);
            emptyElement("dragTime", attrs, handler);
            // automatic tips of the day launch
            addAttribute("value", "" + openTips, attrs);
            emptyElement("tips-on-startup", attrs, handler);
            // Should WebDAV resources be locked, when opening them?
            addAttribute("value", "" + lockDAVMinutes, attrs);
            emptyElement("lockdavminutes", attrs, handler);
            // write the xsl directory
            addAttribute("dir", xslDir, attrs);
            emptyElement("xsl-dir", attrs, handler);
            // write the xslfo directory
            addAttribute("file", xslFo, attrs);
            emptyElement("xsl-fo", attrs, handler);
            // write the working directory directory
            addAttribute("dir", workingDir, attrs);
            emptyElement("working-dir", attrs, handler);
            // write the task name prefix
            addAttribute("prefix", sTaskNamePrefix, attrs);
            emptyElement("task-name", attrs, handler);
            // The last opened files
            {
                startElement("files", attrs, handler);
                for (Iterator iterator = documentsMRU.iterator(); iterator
                        .hasNext();) {
                    Document document = (Document) iterator.next();
                    addAttribute("path", document.getPath(), attrs);
                    emptyElement("file", attrs, handler);
                }
                endElement("files", handler);
            }
            addAttribute("category", "menu", attrs);
            addAttribute("spec",
                    getFontSpec(getUIConfiguration().getMenuFont()), attrs);
            emptyElement("font", attrs, handler);
            //
            addAttribute("category", "chart-main", attrs);
            addAttribute("spec", getFontSpec(getUIConfiguration()
                    .getChartMainFont()), attrs);
            emptyElement("font", attrs, handler);
            //

            saveIconPositions(handler);
            saveRoleSets(handler);
            for (Iterator options = myGPOptions.entrySet().iterator(); options.hasNext();) {
                Map.Entry nextEntry = (Entry) options.next();
                GPOption nextOption = (GPOption)nextEntry.getValue();
                if (nextOption.getPersistentValue()!=null) {
                    addAttribute("id", nextEntry.getKey().toString(), attrs);
                    addAttribute("value", nextOption.getPersistentValue(), attrs);
                    emptyElement("option", attrs, handler);
                }
            }
            endElement("ganttproject-options", handler);
            //
            GPLogger.log("[GanttOptions] save(): finished!!");
            handler.endDocument();
        } catch (Exception e) {
        	if (!GPLogger.log(e)) {
        		e.printStackTrace(System.err);
        	}
        }
    }

    private String getFontSpec(Font font) {
        return font.getFamily() + "-" + getFontStyle(font) + "-"
                + font.getSize();
    }

    private String getFontStyle(Font font) {
        String result;
        final int BOLDITALIC = Font.BOLD + Font.ITALIC;
        switch (font.getStyle()) {
        case Font.PLAIN: {
            result = "plain";
            break;
        }
        case Font.BOLD: {
            result = "bold";
            break;
        }
        case Font.ITALIC: {
            result = "italic";
            break;
        }
        case BOLDITALIC: {
            result = "bolditalic";
            break;
        }
        default: {
            throw new RuntimeException("Illegal value of font style. style="
                    + font.getStyle() + " font=" + font);
        }
        }
        return result;
    }

    public String correct(String s) {
        String res;
        res = s.replaceAll("&", "&#38;");
        res = res.replaceAll("<", "&#60;");
        res = res.replaceAll(">", "&#62;");
        res = res.replaceAll("/", "&#47;");
        res = res.replaceAll("\"", "&#34;");
        return res;
    }

    /** Load the options file */
    public boolean load() {
        // Use an instance of ourselves as the SAX event handler
        DefaultHandler handler = new GanttXMLOptionsParser();

        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            String sFileName = ".ganttproject";
            /*
             * if(System.getProperty("os.name").startsWith("Windows") ||
             * System.getProperty("os.name").startsWith("Mac")) sFileName =
             * "ganttproject.ini";
             */

            File file = new File(System.getProperty("user.home")
                    + System.getProperty("file.separator") + sFileName);
            if (!file.exists()) {
                return false;
            }

            documentsMRU.clear();

            // Parse the input
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, handler);

            GanttLookAndFeelInfo info = GanttLookAndFeels
                    .getGanttLookAndFeels().getInfoByClass(styleClass);
            if (null == info)
                info = GanttLookAndFeels.getGanttLookAndFeels().getInfoByName(
                        styleName);
            if (null != info)
                lookAndFeel = info;

            if (null == lookAndFeel)
                lookAndFeel = GanttLookAndFeels.getGanttLookAndFeels()
                        .getDefaultInfo();

            loadRoleSets(file);

        } catch (Exception e) {
        	if (!GPLogger.log(e)) {
        		e.printStackTrace(System.err);
        	}
            return false;
        }

        isloaded = true;
        return true;
    }

    private void loadRoleSets(File optionsFile) {
        GanttXMLOpen loader = new GanttXMLOpen(null);
        IconPositionTagHandler iconHandler = new IconPositionTagHandler();
        loader.addTagHandler(iconHandler);

        loader.addTagHandler(new RoleTagHandler(getRoleManager()));
        loader.load(optionsFile);
        if (iconHandler.getList() != null) {
            iconListAsIntArray = iconHandler.getList();
            iconListAsString = iconHandler.getPositions();
        }
        if (iconHandler.getDeletedList() != null) {
            deletedIconListAsIntArray = iconHandler.getDeletedList();
            deletedIconListAsString = iconHandler.getDeletedPositions();
        }
    }

    private void saveRoleSets(TransformerHandler handler)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError, SAXException {
        RoleSet[] roleSets = getRoleManager().getRoleSets();
        for (int i = 0; i < roleSets.length; i++) {
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "roleset-name", "roleset-name", "CDATA",
                    roleSets[i].getName());
            handler.startElement("", "roles", "roles", attrs);
            saveRoles(roleSets[i], handler);
            handler.endElement("", "roles", "roles");
        }
    }

    private void saveRoles(RoleSet roleSet, TransformerHandler handler)
            throws SAXException {
        Role[] roles = roleSet.getRoles();
        AttributesImpl attrs = new AttributesImpl();
        for (int i = 0; i < roles.length; i++) {
            Role next = roles[i];
            addAttribute("id", "" + next.getID(), attrs);
            addAttribute("name", next.getName(), attrs);
            emptyElement("role", attrs, handler);
        }

    }

    private void saveIconPositions(TransformerHandler handler)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError, SAXException {
        AttributesImpl attrs = new AttributesImpl();
        addAttribute("icons-list", iconListAsString, attrs);
        addAttribute("deletedIcons-list", deletedIconListAsString, attrs);
        emptyElement("positions", attrs, handler);
    }

    public UIConfiguration getUIConfiguration() {
        if (myUIConfig == null) {
            myUIConfig = new UIConfiguration(null, null, new Color(140, 182,
                    206), redline) {

                public Font getMenuFont() {
                    return myMenuFont == null ? super.getMenuFont()
                            : myMenuFont;
                }

                public Font getChartMainFont() {
                    return myChartMainFont == null ? super.getChartMainFont()
                            : myChartMainFont;
                }
            };
            // Color.black
        }
        return myUIConfig;
    }

    private RoleManager getRoleManager() {
        return myRoleManager;
    }

    private Font myMenuFont;

    /** Class to parse the xml option file */
    class GanttXMLOptionsParser extends DefaultHandler {

        public void startElement(String namespaceURI, String sName, // simple
                // name
                String qName, // qualified name
                Attributes attrs) throws SAXException {

            int r = 0, g = 0, b = 0;

            if ("option".equals(qName)) {
                GPOption option = (GPOption) myGPOptions.get(attrs.getValue("id"));
                if (option!=null) {
                    option.lock();
                    option.loadPersistentValue(attrs.getValue("value"));
                    option.commit();
                }
                return;
            }

            if (attrs != null) {
                for (int i = 0; i < attrs.getLength(); i++) {
                    String aName = attrs.getQName(i); // Attr name
                    String value = attrs.getValue(i);

                    String tagDotAttribute = qName+"."+aName;
                    GP1XOptionConverter converter = (GP1XOptionConverter) myTagDotAttribute_Converter.get(tagDotAttribute);
                    if (converter!=null) {
                        converter.loadValue(value);
                        continue;
                    }
                    if (qName.equals("language")) {
                        if (aName == "selection") {
                            if (value.equals("English") || value.equals("en")) {
                                language.setLocale(Locale.US);
                            } else if (value.equals("English (UK)") || value.equals("en_GB")) {
                                language.setLocale(Locale.UK);
                            } else if (value.equals("Fran\u00e7ais")
                                    || value.equals("fr")) {
                                language.setLocale(Locale.FRANCE);

                            } else if (value.equals("Espa\u00f1ol")
                                    || value.equals("es")) {
                                language.setLocale(new Locale("es", "ES"));

                            } else if (value.equals("Portugues")
                                    || value.equals("pt")) {
                                language.setLocale(new Locale("pt", "PT"));

                            } else if (value.equals("Portugu\u00eas do Brasil")
                                    || value.equals("pt_BR")) {
                                language.setLocale(new Locale("pt", "BR"));

                            } else if (value.equals("Deutsch")
                                    || value.equals("de")) {
                                language.setLocale(Locale.GERMANY);

                            } else if (value.equals("Norsk")
                                    || value.equals("no")) {
                                language.setLocale(new Locale("no", "NO"));

                            } else if (value.equals("Italiano")
                                    || value.equals("it")) {
                                language.setLocale(Locale.ITALY);

                            } else if (value.equals("Japanese")
                                    || value.equals("jpn")) {
                                language.setLocale(new Locale("ja", "JP"));

                            } else if (value.equals("T\u00FCrk\u00E7e")
                                    || value.equals("tr")) {
                                language.setLocale(new Locale("tr", "TR"));

                            } else if (value.equals("Simplified Chinese")
                                    || value.equals("SIMPLIFIED_CHINESE")
                                    || value.equals("CHINA")
                                    || value.equals("zh_CN")) {
                                language.setLocale(Locale.CHINA);

                            } else if (value.equals("Traditional Chinese")
                                    || value.equals("TRADITIONAL_CHINESE")
                                    || value.equals("TAIWAN")
                                    || value.equals("zh_TW")) {
                                language.setLocale(Locale.TAIWAN);

                            } else if (value.equals("Polski")
                                    || value.equals("pl")) {
                                language.setLocale(new Locale("pl", "PL"));

                            } else if (value
                                    .equals("\u0420\u0443\u0441\u0441\u043a\u0438\u0439")
                                    || value.equals("ru")) {
                                language.setLocale(new Locale("ru", "RU"));

                            } else if (value.equals("Estonian")
                                    || value.equals("et")) {
                                language.setLocale(new Locale("et", "ET"));

                            } else if (value.equals("fi")) {
                                language.setLocale(new Locale("fi", "FI"));
                            } else if (value.equals("Hungarian")
                                    || value.equals("hu")) {
                                language.setLocale(new Locale("hu", "HU"));
                            }
                            else if (value.equals("Hrvatski")
                                || value.equals("hr")) {
                            language.setLocale(new Locale("hr", "HR"));
                            }
                            else if (value
                                    .equals("\u05d0\u05e0\u05d2\u05dc\u05d9\u05ea")
                                    || value.equals("iw")) {
                                language.setLocale(new Locale("iw", "IW"));

                            } else if (value.equals("Sloven\u0161\u010dina")
                                    || value.equals("si")) {
                                language.setLocale(new Locale("sl", "SL"));
                            } else if (value.equals("Slovensky")
                                    || value.equals("sk")) {
                                language.setLocale(new Locale("sk", "SK"));

                            } else if (value.equals("Svenska")
                                    || value.equals("sv")) {
                                language.setLocale(new Locale("sv", "SV"));

                            } else if (value.equals("Romanian")
                                    || value.equals("ro")) {
                                language.setLocale(new Locale("ro", "RO"));

                            } else if (value.equals("Nederlands")
                                    || value.equals("nl")) {
                                language.setLocale(new Locale("nl", "NL"));

                            } else if (value.equals("\u010cesky")
                                    || value.equals("cz")) {
                                language.setLocale(new Locale("cs", "CZ"));

                            } else if (value.equals("Dansk")
                                    || value.equals("da")) {
                                language.setLocale(new Locale("da", "DK"));

                            } else if (value.equals("Ti\u1ebfng anh")
                                    || value.equals("vi")) {
                                language.setLocale(new Locale("vi", "VN"));

                            }
                        }
                    } else if (qName.equals("task-color")) {
                        if (aName.equals("red")) {
                            r = new Integer(value).hashCode();
                        } else if (aName.equals("green")) {
                            g = new Integer(value).hashCode();
                        } else if (aName.equals("blue")) {
                            b = new Integer(value).hashCode();
                        }
                    } else if (qName.equals("geometry")) {
                        if (aName.equals("x")) {
                            x = new Integer(value).hashCode();
                        }
                        if (aName.equals("y")) {
                            y = new Integer(value).hashCode();
                        }
                        if (aName.equals("width")) {
                            width = new Integer(value).hashCode();
                        }
                        if (aName.equals("height")) {
                            height = new Integer(value).hashCode();
                        }
                    } else if (qName.equals("looknfeel")) {
                        if (aName.equals("name")) {
                            styleName = value;
                        }
                        if (aName.equals("class")) {
                            styleClass = value;
                        }
                    } else if (qName.equals("file")) {
                        if (aName.equals("path")) {
                            documentsMRU.append(myDocumentManager
                                    .getDocument(value));
                        }
                    } else if (qName.equals("automatic-launch")) {
                        if (aName.equals("value")) {
                            automatic = (new Boolean(value)).booleanValue();
                        }
                    } else if (qName.equals("tips-on-startup")) {
                        if (aName.equals("value")) {
                            openTips = (new Boolean(value)).booleanValue();
                        }
                    } else if (qName.equals("lockdavminutes")) {
                        if (aName.equals("value")) {
                            lockDAVMinutes = (new Integer(value)).intValue();
                        }
                    } else if (qName.equals("xsl-dir")) {
                        if (aName.equals("dir")) {
                            if (new File(value).exists())
                                xslDir = value;
                        }
                    } else if (qName.equals("xsl-fo")) {
                        if (aName.equals("file")) {
                            if (new File(value).exists())
                                xslFo = value;
                        }
                    } else if (qName.equals("working-dir")) {
                        if (aName.equals("dir")) {
                            if (new File(value).exists())
                                workingDir = value;
                        }
                    }

                    else if (qName.equals("task-name")) {
                        if (aName.equals("prefix"))
                            sTaskNamePrefix = value;
                    } else if (qName.equals("toolBar")) {
                        if (aName.equals("position"))
                            toolBarPosition = (new Integer(value)).intValue();
                        else if (aName.equals("icon-size"))
                            iconSize = value;
                        else if (aName.equals("show"))
                            buttonsshow = (new Integer(value)).intValue();
                    } else if (qName.equals("statusBar")) {
                        if (aName.equals("show"))
                            bShowStatusBar = (new Boolean(value))
                                    .booleanValue();
                    } else if (qName.equals("export")) {
                        if (aName.equals("name"))
                            bExportName = (new Boolean(value)).booleanValue();
                        else if (aName.equals("complete"))
                            bExportComplete = (new Boolean(value))
                                    .booleanValue();
                        else if (aName.equals("relations"))
                            bExportRelations = (new Boolean(value))
                                    .booleanValue();
                        else if (aName.equals("border3d"))
                            bExport3DBorders = (new Boolean(value))
                                    .booleanValue();
                    } else if (qName.equals("colors")) {
                        if (aName.equals("resources")) {
                            Color colorR = ColorConvertion
                                    .determineColor(value);
                            if (colorR != null)
                                setResourceColor(colorR);
                        } else if (aName.equals("resourcesOverload")) {
                            Color colorR = ColorConvertion
                                    .determineColor(value);
                            if (colorR != null)
                                setResourceOverloadColor(colorR);
                        } else if (aName.equals("resourcesUnderload")) {
                            Color colorR = ColorConvertion
                                    .determineColor(value);
                            if (colorR != null)
                                setResourceUnderloadColor(colorR);
                        } else if (aName.equals("weekEnd")) {
                            Color colorR = ColorConvertion
                                    .determineColor(value);
                            if (colorR != null)
                                setWeekEndColor(colorR);
                        } else if (aName.equals("daysOff")) {
                            Color colorD = ColorConvertion
                                    .determineColor(value);
                            if (colorD != null)
                                setDaysOffColor(colorD);
                        }
                    } else if (qName.equals("csv-general")) {
                        if (aName.equals("fixed"))
                            csvOptions.bFixedSize = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("separatedChar"))
                            csvOptions.sSeparatedChar = value;
                        if (aName.equals("separatedTextChar"))
                            csvOptions.sSeparatedTextChar = value;
                    } else if (qName.equals("csv-tasks")) {
                        if (aName.equals("id"))
                            csvOptions.bExportTaskID = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("name"))
                            csvOptions.bExportTaskName = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("start-date"))
                            csvOptions.bExportTaskStartDate = (new Boolean(
                                    value)).booleanValue();
                        if (aName.equals("end-date"))
                            csvOptions.bExportTaskEndDate = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("percent"))
                            csvOptions.bExportTaskPercent = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("duration"))
                            csvOptions.bExportTaskDuration = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("webLink"))
                            csvOptions.bExportTaskWebLink = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("resources"))
                            csvOptions.bExportTaskResources = (new Boolean(
                                    value)).booleanValue();
                        if (aName.equals("notes"))
                            csvOptions.bExportTaskNotes = (new Boolean(value))
                                    .booleanValue();
                    } else if (qName.equals("csv-resources")) {
                        if (aName.equals("id"))
                            csvOptions.bExportResourceID = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("name"))
                            csvOptions.bExportResourceName = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("mail"))
                            csvOptions.bExportResourceMail = (new Boolean(value))
                                    .booleanValue();
                        if (aName.equals("phone"))
                            csvOptions.bExportResourcePhone = (new Boolean(
                                    value)).booleanValue();
                        if (aName.equals("role"))
                            csvOptions.bExportResourceRole = (new Boolean(value))
                                    .booleanValue();
                    }
                }
            }

            // old version of the color version
            if (qName.equals("task-color")) {
                // Color color = new Color(r, g, b);
                // getUIConfiguration().setTaskColor(color);
                setDefaultTaskColor(new Color(r, g, b));

            }

            if (qName.equals("font")) {
                String category = attrs.getValue("category");
                if ("menu".equals(category)) {
                    myMenuFont = Font.decode(attrs.getValue("spec"));
                } else if ("chart-main".equals(category)) {
                    myChartMainFont = Font.decode(attrs.getValue("spec"));
                }

            }

        }

    }

    private String getFilePath(String value) {
        String result = null;
        String filePath;
        try {
            URL fileUrl = new URL(value);
            filePath = fileUrl.getPath();
        } catch (MalformedURLException e) {
            filePath = value;
        }
        if (new File(filePath).exists()) {
            result = filePath;
        }
        return result;
    }

    /** @return the language. */
    public GanttLanguage getLanguage() {
        return language;
    }

    /** @return the default color for tasks. */
    public Color getDefaultColor() {
        return getUIConfiguration().getTaskColor();
    }

    /** @return the color for resources. */
    public Color getResourceColor() {
        return getUIConfiguration().getResourceColor();
    }

    // TODO This method was not used...
    // /** @return the color for resources overload. */
    // public Color getResourceOverloadColor() {
    // return getUIConfiguration().getResourceOverloadColor();
    // }
    /** @return the lock DAV Minutes. */
    public int getLockDAVMinutes() {
        return lockDAVMinutes;
    }

    /** @return the undo number */
    public int getUndoNumber() {
        return undoNumber;
    }

    /** @return the working directory. */
    public String getWorkingDir() {
        return workingDir;
    }

    /** @return the xsl directory. */
    public String getXslDir() {
        return xslDir;
    }

    /** @return the xsl-fo file. */
    public String getXslFo() {
        return (new File(xslFo).exists()) ? xslFo : getClass().getResource(
                "/xslfo/ganttproject.xsl").toString();

    }

    /** @return if you want to open the tips at the start of GP. */
    public boolean getOpenTips() {
        return openTips;
    }

    /** @return if the mouse is used to drag on the chart. */
    // public boolean getDragTime() {
    // return dragTime;
    // }
    /** @return automatic launch properties box when create a new task. */
    public boolean getAutomatic() {
        return automatic;
    }

    /** @return the lookAndFeel infos. */
    public GanttLookAndFeelInfo getLnfInfos() {
        return lookAndFeel;
    }

    /** @return true is options are loaded from the options file. */
    public boolean isLoaded() {
        return isloaded;
    }

    /** @return true if show the status bar. */
    public boolean getShowStatusBar() {
        return bShowStatusBar;
    }

    /** set show the status bar. */
    public void setShowStatusBar(boolean showStatusBar) {
        bShowStatusBar = showStatusBar;
    }

    /** @return the top left x position of the window. */
    public int getX() {
        return x;
    }

    /** @return the top left y position of the window. */
    public int getY() {
        return y;
    }

    /** @return the width of the window. */
    public int getWidth() {
        return width;
    }

    /** @return the height of the window. */
    public int getHeight() {
        return height;
    }

    /** @return the cvsOptions. */
    public CSVOptions getCSVOptions() {
        return csvOptions;
    }

    /** @return the task name prefix. */
    public String getTaskNamePrefix() {
        if (sTaskNamePrefix == null || sTaskNamePrefix.equals(""))
            return language.getText("newTask");
        return sTaskNamePrefix;
    }

    public String getTrueTaskNamePrefix() {
        if (sTaskNamePrefix == null || sTaskNamePrefix.equals("")
                || sTaskNamePrefix.equals(language.getText("newTask")))
            return null;
        return sTaskNamePrefix;
    }

    /** @return the toolbar position. */
    public int getToolBarPosition() {
        return toolBarPosition;
    }

    /** @return the size of the icons on the toolbar. */
    public String getIconSize() {
        return iconSize;
    }

    /**
     * @return true if you want to export the name of the task on the exported
     *         chart.
     */
    public boolean getExportName() {
        return bExportName;
    }

    /**
     * @return true if you want to export the complete percent of the task on
     *         the exported chart.
     */
    public boolean getExportComplete() {
        return bExportComplete;
    }

    /**
     * @return true if you want to export the relations of the task on the
     *         exported chart.
     */
    public boolean getExportRelations() {
        return bExportRelations;
    }

    /** @return the 3d borders export. */
    public boolean getExport3dBorders() {
        return bExport3DBorders;
    }

    public GanttExportSettings getExportSettings() {
        return new GanttExportSettings(bExportName, bExportComplete,
                bExportRelations, bExport3DBorders);
    }

    public void setExportName(boolean exportName) {
        bExportName = exportName;
    }

    public void setExportComplete(boolean exportComplete) {
        bExportComplete = exportComplete;
    }

    public void setExportRelations(boolean eportRelations) {
        bExportRelations = eportRelations;
    }

    public void setExport3dBorders(boolean borders3d) {
        bExport3DBorders = borders3d;
    }

    public int[] getIconList() {
        return iconListAsIntArray;
    }

    public int[] getDeletedIconList() {
        return deletedIconListAsIntArray;
    }

    public void setIconList(String list) {
        iconListAsString = list;
    }

    public void setDeletedIconList(String list) {
        deletedIconListAsString = list;
    }

    /**
     * @return the button show attribute must be GanttOptions.ICONS or
     *         GanttOptions.TEXT ir GanttOptions.ICONS_TEXT
     */
    public int getButtonShow() {
        return buttonsshow;
    }

    /**
     * set a new button show value must be GanttOptions.ICONS or
     * GanttOptions.TEXT ir GanttOptions.ICONS_TEXT
     */
    public void setButtonShow(int buttonShow) {
        if (buttonShow != GanttOptions.ICONS && buttonShow != GanttOptions.TEXT
                && buttonShow != GanttOptions.ICONS_TEXT)
            buttonShow = GanttOptions.ICONS;
        buttonsshow = buttonShow;
    }

    /** Set a new icon size. Must be 16, 24 (or 32 exceptionnally) */
    public void setIconSize(String sIconSize) {
        iconSize = sIconSize;
    }

    /** set the toolbar position. */
    public void setToolBarPosition(int iToolBarPosition) {
        toolBarPosition = iToolBarPosition;
    }

    /** Set new window position (top left corner) */
    public void setWindowPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Set new window position (top left corner) */
    public void setWindowSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** Set new working directory value. */
    public void setWorkingDirectory(String workingDirectory) {
        workingDir = workingDirectory;
    }

    /** set a new value for web dav locking. */
    public void setLockDAVMinutes(int minuteslock) {
        lockDAVMinutes = minuteslock;
    }

    /** set a new value for undo number. */
    public void setUndoNumber(int number) {
        undoNumber = number;
    }

    /** set a new default tasks color. */
    public void setDefaultTaskColor(Color color) {
        getUIConfiguration().setTaskColor(color);
    }

    /** set a new default resources color. */
    public void setResourceColor(Color color) {
        getUIConfiguration().setResourceColor(color);
    }

    /** set a new resources overload tasks color. */
    public void setResourceOverloadColor(Color color) {
        getUIConfiguration().setResourceOverloadColor(color);
    }

    /** set a new resources underload tasks color. */
    public void setResourceUnderloadColor(Color color) {
        getUIConfiguration().setResourceUnderloadColor(color);
    }

    /** set a new previous tasks color. */
    public void setPreviousTaskColor(Color color) {
        getUIConfiguration().setPreviousTaskColor(color);
    }

    /** set a new earlier previous tasks color. */
    public void setEarlierPreviousTaskColor(Color color) {
        getUIConfiguration().setEarlierPreviousTaskColor(color);
    }

    /** set a new later previous tasks color. */
    public void setLaterPreviousTaskColor(Color color) {
        getUIConfiguration().setLaterPreviousTaskColor(color);
    }

    /** Set a new week end color. */
    public void setWeekEndColor(Color color) {
        getUIConfiguration().setWeekEndColor(color);
    }

    /** Set a new week end color. */
    public void setDaysOffColor(Color color) {
        getUIConfiguration().setDayOffColor(color);
    }

    /** Set a new xsl directory for html export. */
    public void setXslDir(String xslDir) {
        this.xslDir = xslDir;
    }

    /** Set a new xsl-fo file for pdf export. */
    public void setXslFo(String xslFo) {
        this.xslFo = xslFo;
    }

    public void setDocumentsMRU(DocumentsMRU documentsMRU) {
        this.documentsMRU = documentsMRU;
    }

    public void setUIConfiguration(UIConfiguration uiConfiguration) {
        this.myUIConfig = uiConfiguration;
    }

    /** set new open tips value. */
    public void setOpenTips(boolean openTips) {
        this.openTips = openTips;
    }

    /** set new automatic launch value. */
    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    /** set new drag time with mouse value. */
    // public void setDragTime(boolean dragTime){
    // this.dragTime = dragTime;
    // }
    public void setLookAndFeel(GanttLookAndFeelInfo lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
    }

    public void setTaskNamePrefix(String taskNamePrefix) {
        sTaskNamePrefix = taskNamePrefix;
    }

    public void setRedline(boolean isOn) {
        this.redline = isOn;
        getUIConfiguration().setRedlineOn(isOn);
    }

    /**
     * @return
     */
    public String getFTPDirectory() {
        return FTPDirectory;
    }

    /**
     * @return
     */
    public String getFTPPwd() {
        return FTPPwd;
    }

    /**
     * @return
     */
    public String getFTPUrl() {
        return FTPUrl;
    }

    /**
     * @return
     */
    public String getFTPUser() {
        return FTPUser;
    }

    /**
     * @param pvString
     */
    public void setFTPDirectory(String pvString) {
        FTPDirectory = pvString;
    }

    /**
     * @param pvString
     */
    public void setFTPPwd(String pvString) {
        FTPPwd = pvString;
    }

    /**
     * @param pvString
     */
    public void setFTPUrl(String pvString) {
        FTPUrl = pvString;
    }

    /**
     * @param pvString
     */
    public void setFTPUser(String pvString) {
        FTPUser = pvString;
    }

    /**
     * @return the default string to configure the iconList
     */
    public String getDefaultIconListAsString() {
        // String iconAsString = "1,3,4,5,6,7,8,0,21,22,23,24,25,0,28,0,29";
        //
        // if (!isOnlyViewer)
        // iconAsString =
        // "2,3,4,5,6,7,8,0,9,10,11,0,12,13,14,15,16,17,18,19,20,0,21,22,23,24,25,0,26,27,0,30,31,0,28";
        //
        // return iconAsString;
        return getStringList(getDefaultIconListIntArray());
    }

    private static String getStringList(int[] tabInt) {
        String res = "";
        int i = 0;
        for (i = 0; i < tabInt.length - 1; i++) {
            res += tabInt[i] + ",";
        }
        res += tabInt[i];
        return res;
    }

    /**
     * @return the default integer array to configure the iconList
     */
    public int[] getDefaultIconListIntArray() {

        if (!isOnlyViewer)
            return new int[] { SAVE,
                    SEPARATOR, CUT, COPY, PASTE, SEPARATOR, UNDO, REDO,
                    SEPARATOR, NEWTASK, DELETE, PROPERTIES, UNLINK, LINK, IND,
                    UNIND, UP, DOWN, SEPARATOR, PREV, CENTER, NEXT, ZOOMOUT,
                    ZOOMIN, SEPARATOR, SAVECURRENT, COMPAREPREV, SEPARATOR,
                    CRITICAL };

        /* else */
        return new int[] { EXIT, OPEN, SAVE, SAVEAS, IMPORT, EXPORT, PRINT,
                PREVIEWPRINT, SEPARATOR, PREV, CENTER, NEXT, ZOOMOUT, ZOOMIN,
                SEPARATOR, CRITICAL, SEPARATOR, ABOUT };
    }

    /**
     * @return the default string to configure the iconList
     */
    public String getDefaultDeletedIconListAsString() {
        return "";
    }

    /**
     * @return the default integer array to configure the iconList
     */
    public int[] getDefaultDeletedIconListIntArray() {
        return null;
    }

    public void addOptionGroups(GPOptionGroup[] optionGroups) {
        for (int i=0; i<optionGroups.length; i++) {
            GPOptionGroup nextGroup = optionGroups[i];
            addOptions(nextGroup);
        }
    }

    private void addOptions(GPOptionGroup optionGroup) {
        GPOption[] options = optionGroup.getOptions();
        for (int i=0;i<options.length; i++) {
            GPOption nextOption = options[i];
            myGPOptions.put(optionGroup.getID()+"."+nextOption.getID(), nextOption);
            if (nextOption instanceof GP1XOptionConverter) {
                GP1XOptionConverter nextConverter = (GP1XOptionConverter) nextOption;
                myTagDotAttribute_Converter.put(nextConverter.getTagName()+"."+nextConverter.getAttributeName(), nextConverter);
            }
        }
    }
}

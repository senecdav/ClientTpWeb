package client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rest.model.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CLient REST.
 */
public class Client {

    // Nom de l'application
    private static final String APP_NAME = "Editeur CV";
    // Dimensions par défaut de l'application
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 600;
    // URL REST
    private static final QName QNAME = new QName("", "");
    private static final String URL = "http://projetrest.senecdav.cloudbees.net/resume";

    // REST
    private Service service;
    private JAXBContext jc;

    private boolean[] errorList = new boolean[4];
    private static final String[] SK_LEVELS = { "Débutant", "Intermédiaire", "Professionnel"};
    private static final String[] LG_LEVELS = { "Niveau scolaire", "Usage professionnel", "Courant"};

    // Composants
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JScrollPane cvScroll;
    private JScrollPane allCVscroll;
    private JScrollPane formScroll;
    private JPanel formPanel;
    private JButton show;
    private JButton reload;
    private JButton save;
    private JButton addITSkill;
    private JButton addLang;
    private JButton addExperiences;
    private JButton addEducation;
    private JTextField cvId;
    private JTextField cvFirstName;
    private JTextField cvName;
    private JTextField cvObjectives;
    private JTextField cvSkill;
    private JTextArea allCvTextArea;
    private JTextArea cvTextArea;
    private List<JTextField> educationsList;
    private List<JTextField> experiencesList;
    private List<JTextField> langsList;
    private List<JTextField> skillsList;
    private List<JComboBox> skillsLevels;
    private List<JComboBox> langsLevels;

    public Client() {
        try {
            jc = JAXBContext.newInstance(
                    Cv.class,
                    Education.class,
                    Experience.class,
                    ITSkill.class,
                    Lang.class,
                    String.class
            );
        } catch (JAXBException je) {
            JOptionPane.showMessageDialog(this.frame, "Impossible de créer le context JAXB !");
            System.exit(0);
        }
        createView();
        placeComponents();
        //createModel();
        createController();
        getAllCV(); // récupère tous les CV au lancement
    }

    private void createView() {
        // Frame
        frame = new JFrame(APP_NAME);
        frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        // Tabs
        tabbedPane = new JTabbedPane();
        // Scrolls
        cvScroll = new JScrollPane();
        cvScroll.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        allCVscroll = new JScrollPane();
        formScroll = new JScrollPane();
        // Text Area
        allCvTextArea = new JTextArea("");
        cvTextArea = new JTextArea("");
        allCvTextArea.setEditable(false);
        cvTextArea.setEditable(false);
        // Buttons
        show = new JButton("Afficher");
        reload = new JButton("Recharger");
        save = new JButton("Ajouter");
        addITSkill = new JButton("+");
        addExperiences = new JButton("+");
        addLang = new JButton("+");
        addEducation = new JButton("+");
        // textField
        cvId = new JTextField(4);
        initFormFields();
    }

    private void initFormFields() {
        cvFirstName = new JTextField(20);
        cvName = new JTextField(20);
        cvObjectives = new JTextField(20);
        cvSkill = new JTextField(20);
        educationsList = new ArrayList<JTextField>();
        educationsList.add(new JTextField(20));
        educationsList.add(new JTextField(20));
        skillsList = new ArrayList<JTextField>();
        skillsList.add(new JTextField(20));
        langsList = new ArrayList<JTextField>();
        langsList.add(new JTextField(20));
        experiencesList = new ArrayList<JTextField>();
        experiencesList.add(new JTextField(20));
        experiencesList.add(new JTextField(20));
        experiencesList.add(new JTextField(20));
        skillsLevels = new ArrayList<JComboBox>();
        skillsLevels.add(new JComboBox(SK_LEVELS));
        langsLevels = new ArrayList<JComboBox>();
        langsLevels.add(new JComboBox(LG_LEVELS));
    }

    private void placeComponents() {
        JPanel p;

        p = new JPanel(new BorderLayout()); {
            JPanel q = new JPanel(new FlowLayout(FlowLayout.CENTER)); {
                q.add(new JLabel("Liste de tous les CV :"));
                q.add(reload);
            }
            p.add(q, BorderLayout.NORTH);
            allCVscroll.getViewport().add(allCvTextArea);
            p.add(allCVscroll, BorderLayout.CENTER);
        }
        tabbedPane.addTab("Tous les CV", p);

        p = new JPanel(new BorderLayout()); {
            JPanel q = new JPanel(new FlowLayout(FlowLayout.CENTER));
            {
                JPanel r = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                {
                    r.add(new JLabel("Numero du CV : "));
                }
                q.add(r);
                r = new JPanel(new FlowLayout(FlowLayout.CENTER));
                {
                    r.add(cvId);
                }
                q.add(r);
                r = new JPanel(new FlowLayout(FlowLayout.LEFT));
                {
                    r.add(show);
                }
                q.add(r);
            }
            p.add(q, BorderLayout.NORTH);
            cvScroll.getViewport().add(cvTextArea);
            p.add(cvScroll, BorderLayout.CENTER);
        }
        tabbedPane.addTab("Rechercher CV", p);

        formPanel = cvForm();
        formScroll.getViewport().add(formPanel);
        tabbedPane.addTab("Ajouter CV", formScroll);
        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel cvForm() {
        JPanel p;
        JPanel o = new JPanel(new BorderLayout());
        p = new JPanel(new GridBagLayout()); {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel q = new JPanel(new SpringLayout());
            {
                q.add(new JLabel("Nom :", JLabel.TRAILING));
                q.add(cvFirstName);
                if (errorList[0]) {
                    cvFirstName.setBorder(BorderFactory.createLineBorder(Color.RED));
                } else {
                    cvFirstName.setBorder(UIManager.getBorder("TextField.border"));
                }

                q.add(new JLabel("Prenom :", JLabel.TRAILING));
                q.add(cvName);
                if (errorList[1]) {
                    cvName.setBorder(BorderFactory.createLineBorder(Color.RED));
                } else {
                    cvName.setBorder(UIManager.getBorder("TextField.border"));
                }

                q.add(new JLabel("Objectifs :", JLabel.TRAILING));
                q.add(cvObjectives);
                if (errorList[2]) {
                    cvObjectives.setBorder(BorderFactory.createLineBorder(Color.RED));
                } else {
                    cvObjectives.setBorder(UIManager.getBorder("TextField.border"));
                }

                q.add(new JLabel("Compétences :", JLabel.TRAILING));
                q.add(cvSkill);
                if (errorList[3]) {
                    cvSkill.setBorder(BorderFactory.createLineBorder(Color.RED));
                } else {
                    cvSkill.setBorder(UIManager.getBorder("TextField.border"));
                }
            }

            makeFieldSet(q, "Informations");
            p.add(q, gbc);

            q = new JPanel(new SpringLayout());
            {
                for (int i = 0; i < educationsList.size(); i += 2) {
                    q.add(new JLabel("Nom: ", JLabel.TRAILING));
                    q.add(educationsList.get(i));
                    q.add(new JLabel("Date: ", JLabel.TRAILING));
                    q.add(educationsList.get(i + 1));
                }

                q.add(new JLabel());
                q.add(addEducation);
            }

            makeFieldSet(q, "Formations");
            p.add(q, gbc);

            q = new JPanel(new SpringLayout());
            {
                for (int i = 0; i < experiencesList.size(); i += 3) {
                    q.add(new JLabel("Nom :", JLabel.TRAILING));
                    q.add(experiencesList.get(i));
                    q.add(new JLabel("Date :", JLabel.TRAILING));
                    q.add(experiencesList.get(i + 1));
                    q.add(new JLabel("Description :", JLabel.TRAILING));
                    q.add(experiencesList.get(i + 2));
                }

                q.add(new JLabel());
                q.add(addExperiences);
            }

            makeFieldSet(q, "Expériences");
            p.add(q, gbc);

            q = new JPanel(new SpringLayout());
            {
                int j = 0;
                for (int i = 0; i < skillsList.size(); i += 2) {
                    q.add(new JLabel("Compétence :", JLabel.TRAILING));
                    q.add(skillsList.get(i));
                    q.add(new JLabel("Niveau :", JLabel.TRAILING));
                    q.add(skillsLevels.get(j++));
                }

                q.add(new JLabel());
                q.add(addITSkill);
            }

            makeFieldSet(q, "Compétences Informatiques");
            p.add(q, gbc);

            q = new JPanel(new SpringLayout());
            {
                int j = 0;
                for (int i = 0; i < langsList.size(); i++) {
                    q.add(new JLabel("Langue :", JLabel.TRAILING));
                    q.add(langsList.get(i));
                    q.add(new JLabel("Niveau :", JLabel.TRAILING));
                    q.add(langsLevels.get(j++));
                }

                q.add(new JLabel());
                q.add(addLang);
            }

            makeFieldSet(q, "Langues");
            p.add(q, gbc);
        }

        o.add(p, BorderLayout.CENTER);
        o.add(save, BorderLayout.SOUTH);
        return o;
    }

    private void makeFieldSet(JPanel p, String title) {
        SpringUtilities.makeCompactGrid(
                p,
                p.getComponents().length / 2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6        //xPad, yPad
        );
        p.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLACK), title)
        );
    }

    private void redrawForm() {
        //Rectangle rect = formScroll.getVisibleRect();
        formScroll.remove(formPanel);
        formPanel = cvForm();
        formScroll.getViewport().add(formPanel);
        //formScroll.scrollRectToVisible(rect);
        //formScroll.revalidate();
    }

    private void createController() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane.addChangeListener(new ChangeListener() {
            private int lastSelected = 0;

            @Override
            public void stateChanged(ChangeEvent e) {
                int current = tabbedPane.getSelectedIndex();
                // 0 == allCv
                if (current == 0 && lastSelected != 0) {
                    int res = JOptionPane.showConfirmDialog(
                            frame,
                            "Voulez vous recharger la liste des CV ?",
                            "Recharger CV",
                            JOptionPane.YES_NO_OPTION);
                    if (res == JOptionPane.YES_OPTION) {
                        allCvTextArea.setText("");
                        getAllCV();
                    }
                }
                lastSelected = current;
            }
        });

        reload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                allCvTextArea.setText("");
                getAllCV();
            }
        });

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idCV = Integer.valueOf(cvId.getText());
                if (idCV >= 0) {
                    getCV(idCV);
                } else {
                    cvTextArea.setText("Id invalide.");
                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    putCV();
                } catch (JAXBException e1) {
                    // Erreur lors de l'enregistrement du CV.
                }
                redrawForm();
            }
        });

        addITSkill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                skillsList.add(new JTextField(20));
                skillsLevels.add(new JComboBox(SK_LEVELS));
                redrawForm();
            }
        });

        addEducation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                educationsList.add(new JTextField(20));
                educationsList.add(new JTextField(20));
                redrawForm();
            }
        });

        addLang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                langsList.add(new JTextField(20));
                langsLevels.add(new JComboBox(LG_LEVELS));
                redrawForm();
            }
        });

        addExperiences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                experiencesList.add(new JTextField(20));
                experiencesList.add(new JTextField(20));
                experiencesList.add(new JTextField(20));
                redrawForm();
            }
        });

    }

    private void putCV() throws JAXBException {
        Cv cv = new Cv();
        cv.setId(-1);

        String nam = cvName.getText();
        String fname = cvFirstName.getText();
        String obj = cvObjectives.getText();
        String sk = cvSkill.getText();
        boolean valid = true;

        if (nam.isEmpty()) {
            errorList[1] = true;
            valid = false;
        } else {
            errorList[1] = false;
            cv.setLastName(nam);
        }

        if (fname.isEmpty()) {
            errorList[0] = true;
            valid = false;
        } else {
            errorList[0] = false;
            cv.setFirstName(fname);
        }

        if (obj.isEmpty()) {
            errorList[2] = true;
            valid = false;
        } else {
            errorList[2] = false;
            cv.setObjectives(obj);
        }

        if (sk.isEmpty()) {
            errorList[3] = true;
            valid = false;
        } else {
            errorList[3] = false;
            cv.setSkills(sk);
        }

        if (!valid) {
            return;
        }

        List<Education> eduList = new ArrayList<Education>();
        for (int i = 0; i < educationsList.size(); i++) {
            String school = educationsList.get(i).getText();
            String year = educationsList.get(++i).getText();
            System.out.println(school);
            System.out.println(year);
            if (!school.isEmpty() && !year.isEmpty()) {
                eduList.add(new Education(
                        school,
                        year
                ));
            }
        }
        cv.setEducations(eduList);

        List<Experience> expList = new ArrayList<Experience>();
        for (int i = 0; i < experiencesList.size(); i++) {
            String name = experiencesList.get(i).getText();
            String year = experiencesList.get(++i).getText();
            String desc = experiencesList.get(++i).getText();
            if (!name.isEmpty() && !year.isEmpty() && !desc.isEmpty()) {
                expList.add(new Experience(
                        name,
                        desc,
                        year
                ));
            }
        }
        cv.setExperiences(expList);


        List<ITSkill> skList = new ArrayList<ITSkill>();
        for (int i = 0; i < skillsList.size(); i++) {
            String name = skillsList.get(i).getText();
            int level = skillsLevels.get(i).getSelectedIndex() + 1;
            if (!name.isEmpty()) {
                skList.add(new ITSkill(
                        name,
                        level
                ));
            }
        }
        cv.setItSkills(skList);

        List<Lang> lgList = new ArrayList<Lang>();
        for (int i = 0; i < langsList.size(); i++) {
            String name = langsList.get(i).getText();
            int level = langsLevels.get(i).getSelectedIndex() + 1;
            if (!name.isEmpty()) {
                lgList.add(new Lang(
                        name,
                        level
                ));
            }
        }
        cv.setLangs(lgList);

        service = Service.create(QNAME);
        service.addPort(QNAME, HTTPBinding.HTTP_BINDING, URL);
        Dispatch<Source> dispatcher = service.createDispatch(QNAME,
                Source.class, Service.Mode.MESSAGE);
        Map<String, Object> requestContext = dispatcher.getRequestContext();
        requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "PUT");
        Source result = null;
        try {
            result = dispatcher.invoke(new JAXBSource(jc, cv));
            initFormFields();
            JOptionPane.showMessageDialog(frame, "Le CV a été ajouté");
        } catch (WebServiceException e) {
            JOptionPane.showMessageDialog(frame, "Le CV n'a pas été ajouté");
        }
        //printSource(result);
    }

    public static void printSource(Source s) {
        try {
            System.out.println("============================= Response Received =========================================");
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(s, new StreamResult(System.out));
            System.out.println("\n======================================================================");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private String readCV(Document document) {
        String str = "";
        NodeList cv = document.getElementsByTagName("cv");
        int cvs = cv.getLength();
        for (int i = 0; i < cvs; i++) {
            Node item = cv.item(i);
            if(item.getNodeType() == Node.ELEMENT_NODE){
                Element itemEm = (Element) item;
                Node id = itemEm.getElementsByTagName("id").item(0);
                str += "Identifiant : " +
                        id.getFirstChild().getNodeValue() + "\n";
                Node nom = itemEm.getElementsByTagName("lastName").item(0);
                str += "Nom : " +
                        nom.getFirstChild().getNodeValue() + "\n";
                Node prenom = itemEm.getElementsByTagName("firstName").item(0);
                str += "Prénom : " +
                        prenom.getFirstChild().getNodeValue() + "\n";
                Node objectifs = itemEm.getElementsByTagName("objectives").item(0);
                str += "Objectifs : " +
                        objectifs.getFirstChild().getNodeValue() + "\n";
                Node competences = itemEm.getElementsByTagName("skills").item(0);
                str += "Compétences : " +
                        competences.getFirstChild().getNodeValue() + "\n";
                NodeList schools = itemEm.getElementsByTagName("school");
                str += "Formations : \n";
                if (schools.getLength() == 0) {
                    str += "  Aucunes\n";
                }
                for (int j = 0; j < schools.getLength(); j++) {
                    NodeList school = schools.item(j).getChildNodes();
                    if (school.getLength() >= 1) {
                        str += "  Nom : " + school.item(0).getFirstChild().getNodeValue() + "  ";
                        if (school.getLength() == 2) {
                            str += "  Date : " + school.item(1).getFirstChild().getNodeValue();
                        }
                        str += "\n";
                    }
                }
                NodeList experiences = itemEm.getElementsByTagName("experience");
                str += "Expériences : \n";
                if (experiences.getLength() == 0) {
                    str += "  Aucunes\n";
                }
                for (int j = 0; j < experiences.getLength(); j++) {
                    NodeList experience = experiences.item(j).getChildNodes();
                    if (experience.getLength() >= 1) {
                        str += "  Nom : " + experience.item(0).getFirstChild().getNodeValue() + "  ";
                        if (experience.getLength() >= 2) {
                            str += "  Date : " + experience.item(1).getFirstChild().getNodeValue() + "  ";
                            if (experience.getLength() == 3) {
                                str += "  Description : " + experience.item(2).getFirstChild().getNodeValue();
                            }
                        }
                       str += "\n";
                    }
                }
                NodeList compskills = itemEm.getElementsByTagName("computeskill");
                str += "Compétences informatiques : \n";
                if (compskills.getLength() == 0) {
                    str += "  Aucunes\n";
                }
                for (int j = 0; j < compskills.getLength(); j++) {
                    Node n = compskills.item(j);
                    NodeList compskill = n.getChildNodes();
                    str += "  Compétence : " + compskill.item(0).getFirstChild().getNodeValue() + "  ";
                    if (n.hasAttributes()) {
                        try {
                            str += "  Niveau : ";
                            int level = Integer.parseInt(n.getAttributes().getNamedItem("level").getNodeValue());
                            switch (level) {
                                case 1:
                                    str += SK_LEVELS[0];
                                    break;
                                case 2:
                                    str += SK_LEVELS[1];
                                    break;
                                case 3:
                                    str += SK_LEVELS[2];
                                    break;
                                default:
                                    str += "Non défini";
                            }
                        } catch (NumberFormatException e) {
                            str += "  Non défini";
                        }
                    }
                    str += "\n";
                }
                NodeList langs = itemEm.getElementsByTagName("lang");
                str += "Langues : \n";
                if (langs.getLength() == 0) {
                    str += "  Aucunes\n";
                }
                for (int j = 0; j < langs.getLength(); j++) {
                    Node n = langs.item(j);
                    NodeList lang = n.getChildNodes();
                    str += "  Langue : " + lang.item(0).getFirstChild().getNodeValue() + "  ";
                    if (n.hasAttributes()) {
                        try {
                            str += "  Niveau : ";
                            int level = Integer.parseInt(n.getAttributes().getNamedItem("level").getNodeValue());
                            switch (level) {
                                case 1:
                                    str += LG_LEVELS[0];
                                    break;
                                case 2:
                                    str += LG_LEVELS[1];
                                    break;
                                case 3:
                                    str += LG_LEVELS[2];
                                    break;
                                default:
                                    str += "Non défini";
                            }
                        } catch (NumberFormatException e) {
                            str += "  Non défini";
                        }
                    }
                    str += "\n";
                }
            }
            str += "---------------------------------------\n\n";
        }
        return str;
    }

    private void getCV(int id) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            cvTextArea.setText("Impossible de créer le builder");
        }

        try {
            Document document = builder.parse(URL + "/" + id);
            document.getDocumentElement().normalize();
            cvTextArea.setText(readCV(document));
        } catch (Exception e) {
            cvTextArea.setText("Impossible de récupérer le CV n°" + id + ".");
        }
    }

    private void getAllCV() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            allCvTextArea.setText("Impossible de créer le builder");
        }

        try {
            Document document = builder.parse(URL);
            document.getDocumentElement().normalize();
            allCvTextArea.setText(readCV(document));
        } catch (Exception e) {
            allCvTextArea.setText("Impossible de récupérer les CVs.");
        }
    }

    public void display() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Client().display();
            }
        });
    }

}
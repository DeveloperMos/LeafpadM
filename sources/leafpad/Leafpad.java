package leafpad;

import javafx.stage.FileChooser;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


class Leafpad extends JFrame {

    private JPanel menuPanel = new JPanel();
    private JScrollPane textPanel = new JScrollPane();

    private StringBuffer buffer = new StringBuffer();
    private FileChooser fileChooser;
    private FileReader reader;
    private File mfile;
    private String string;
    private boolean isAction = false;


    private JMenuBar menuBar = new JMenuBar();
    private JMenu file = new JMenu("File");
    private JMenu edit = new JMenu("Edit");
    private JMenu find = new JMenu("Find");
    private JMenu about = new JMenu("About");

    private JTextArea textArea = new JTextArea();

    public Leafpad(){
        super("Leafpad");

        JMenuItem open = new JMenuItem("Open"),
                save = new JMenuItem("Save"),
                saveAs = new JMenuItem("Save AS"),
                exit = new JMenuItem("Exit");

        JMenuItem copy = new JMenuItem("Copy"),
                paste = new JMenuItem("Paste"),
                clear = new JMenuItem("Clear");

        JMenuItem search = new JMenuItem("Search");
        JMenuItem aboutT = new JMenuItem("About");


        menuPanel.add(menuBar);
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(find);
        menuBar.add(about);

        /*---------------------file-----------------------*/
        open.setIcon(new ImageIcon(getClass().getResource("/leafpad/icons/folder.png")));
        file.add(open);
        file.addSeparator();
        file.add(save);
        save.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/save.png")));
        file.add(saveAs);
        saveAs.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/saveas.png")));
        file.addSeparator();
        file.add(exit);
        exit.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/power.png")));
        /*--------------------edit----------------------*/
        edit.add(copy);
        copy.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/copy.png")));
        edit.add(paste);
        paste.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/paste.png")));
        edit.addSeparator();
        edit.add(clear);
        clear.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/delete.png")));
        /*-------------------find and about---------------*/
        find.add(search);
        search.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/search.png")));
        about.add(aboutT);
        aboutT.setIcon( new ImageIcon(getClass().getResource("/leafpad/icons/about.png")));

        /*------------------Actions-----------------------*/

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAction = true;
                JFileChooser fileChooser = new JFileChooser();

                if (fileChooser.showOpenDialog(open) == JFileChooser.APPROVE_OPTION) {

                    mfile = fileChooser.getSelectedFile();
                    try {
                        reader = new FileReader(mfile);

                        int symbol;
                        while((symbol = reader.read()) != -1)
                        {
                            buffer.append((char)symbol);
                        }
                        textArea.setText(buffer.toString());
                        string = buffer.toString();

                        if(string.isEmpty()){  //Only check
                            isAction = false;
                        }

                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();

                if(isAction == false || text != null){
                    fileChooser = new FileChooser();

                    fileChooser.setInitialFileName(JOptionPane.showInputDialog(menuPanel,
                            "Имя файла",
                            "*.txt"));

                    if(fileChooser.getInitialFileName() != null){

                        try (FileWriter writer = new FileWriter(fileChooser.getInitialFileName(), false)){
                            writer.write(text);
                            JOptionPane.showMessageDialog(menuPanel, "Файл создан");

                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }else return;
                }

                if( text == null || text.equals(string) || string == null){

                    JOptionPane.showMessageDialog(menuPanel, "Нет изменений для сохранения");
                }else {
                    try (FileWriter writer = new FileWriter( mfile.getPath(), false)){
                        writer.write(text);
                        JOptionPane.showMessageDialog(menuPanel, mfile.getName() + " Успешно перезаписан");

                    } catch (IOException e1) {

                        System.out.println("6");
                    }

                }
            }

        });

        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                if(text != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "*.*");
                    fileChooser.setFileFilter(filter);
                    if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){

                        try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())){
                            writer.write(text);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int dialog = 0;
                String text = textArea.getText();

                if( text == null || text.equals(string) || string == null){
                    System.exit(1);
                }else{

                    dialog = JOptionPane.showConfirmDialog(menuPanel,
                            "Есть изменения, сохранить их?",
                            mfile.getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if(dialog == JOptionPane.OK_OPTION)
                    {
                        try(FileWriter writer = new FileWriter(mfile.getPath(), false)){
                            writer.write(text);
                        }catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }else System.exit(1);
                }
            }
        });

        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String s = textArea.getSelectedText();
                if(s==null) return;
                if(s.length()<1) return;

                Transferable data = new StringSelection(s);

                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                c.setContents(data, null);
            }
        });



        paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

                Transferable t = c.getContents(this);
                if (t == null)
                    return;
                try {
                    textArea.setText(textArea.getText() + t.getTransferData(DataFlavor.stringFlavor));
                } catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(menuPanel, "В РАЗРАБОТКЕ");
            }
        });


        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(null);
            }
        });

        aboutT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(menuPanel,
                        "Leafpad - является свободным программным обеспечением\n\r" +
                                "которая распространяется под свободной лицензией,\n\r" +
                                "пользователи которого имеют права на его неограниченную установку,\n\r" +
                                "запуск, свободное использование, изучение, распространение\n\r" +
                                "и изменение (совершенствование), а также \n\r" +
                                "распространение копий и результатов изменения \n\r" +
                                "\n\r" +
                                "\n\r" +
                                "Create by Movses\n\r" +
                                "Idea - Karen Vardanyan\n\r ", "About", 1);
            }
        });


        textPanel.getViewport().add(textArea);

        add(menuPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        setSize(600, 600);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public static void main(String[] args) {
        new Leafpad();
    }
}


package mineverse.Aust1n46.chat.config;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//This class is a GUI that will help the user edit their config without the risk of mistakes.  It's not
//fully implemented yet.
@SuppressWarnings("serial")
public class ConfigAssist extends JFrame { //unimplemented
    private static JButton buttonFilters = new JButton("Filters");
    private static JButton buttonBack = new JButton("Back");
    private static JButton buttonChannels = new JButton("Channels");
    private static JButton buttonExit = new JButton("Exit");
    private JLabel filtersMessage = new JLabel("Edit List of Filters", SwingConstants.CENTER);
    private JLabel channelsMessage = new JLabel("Edit List of Channels", SwingConstants.CENTER);
    private JLabel blank = new JLabel("");
    private FiltersButtonHandler handlerFilters = new FiltersButtonHandler();
    private BackButtonHandler handlerBack = new BackButtonHandler();
    private ChannelsButtonHandler handlerChannels = new ChannelsButtonHandler();
    private ExitButtonHandler handlerExit = new ExitButtonHandler();
    private Container pane = this.getContentPane();
    private JTextField filtersBox = new JTextField();
    private JTextField channelsBox = new JTextField();
    
    private List<ConfigChannel> channels = new ArrayList<ConfigChannel>();
    
    public static void main(String[] args) {
        new ConfigAssist();
    }
    
    public ConfigAssist() {
        this.init();
    }
       
    public void init() {   
        //buttonFilters.setBackground(new Color(255, 0, 0));      
        buttonFilters.addActionListener(handlerFilters); 
        buttonBack.addActionListener(handlerBack);
        buttonChannels.addActionListener(handlerChannels);
        buttonExit.addActionListener(handlerExit);      
        this.setTitle("Config");      
        setMenu();
        //pane.setBackground(new Color(255, 0, 0));
        this.setSize(500, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ConfigChannel global = new ConfigChannel("Global");
        ConfigChannel local = new ConfigChannel("Local");
        channels.add(global);
        channels.add(local);
        setVisible(true);       
    }
    
    private void setMenu() {
        pane.removeAll();
        pane.setLayout(new GridLayout(3,2));
        pane.add(blank);
        pane.add(buttonExit);
        pane.add(filtersMessage);
        pane.add(buttonFilters); 
        pane.add(channelsMessage);
        pane.add(buttonChannels);
        this.refresh();
    }
    
    private void setFiltersMenu() {
        pane.removeAll();
        pane.setLayout(new GridLayout(2,1));        
        pane.add(buttonBack);
        pane.add(buttonExit);      
        pane.add(filtersMessage);
        pane.add(filtersBox);
        this.refresh();
    }
    
    private void setChannelsMenu() {
        pane.removeAll();
        pane.setLayout(new GridLayout(channels.size() + 1, 1));      
        pane.add(buttonBack);
        pane.add(buttonExit);      
        pane.add(channelsMessage);
        String text = "";
        for(ConfigChannel channel : channels) {
            text += channel.getName() + " ";
        }
        channelsBox.setText(text);
        pane.add(channelsBox);
        this.refresh();
    }
    
    private void refresh() {
        pane.setVisible(false);
        pane.setVisible(true);
    }
    
    private class FiltersButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {                    
           setFiltersMenu();
        }
    }
    
    private class BackButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {  
            setMenu();
        }        
    }
    
    private class ChannelsButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {  
            setChannelsMenu();
        }        
    }
    
    private class ExitButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {  
            System.exit(0);
        }   
    }
    
    private class ConfigChannel {
        private String name;
        
        public ConfigChannel(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
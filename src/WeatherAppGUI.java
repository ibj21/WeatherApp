import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.SystemColor.text;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI(){
        //setup gui and add title
        super("Weather App");
        //configure gui to end the program's process once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //size of gui in pixels
        setSize(450, 450);
        //location on screen
        setLocationRelativeTo(null);
        //make layout manager null to manually position components
        setLayout(null);
        //prevent resize;
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();

        //set location and size of our component
        searchTextField.setBounds(15,15,351, 45);
        //change font style and size
        searchTextField.setFont(new Font ("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        //weather image
        JLabel weatherConditionImage= new JLabel(loadImage("assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temp
        JLabel temperatureText= new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 46));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity
        JLabel humidityImage= new JLabel(loadImage("assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        //humidity text
        JLabel humidityText= new JLabel("<html><b>Humidity</b></html>");
        humidityText.setBounds(90,500,84,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed image
        JLabel windspeedImage= new JLabel(loadImage("assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        //windspeed text
        JLabel windspeedText= new JLabel("<html><b>Windspeed</b></html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //search button
        JButton searchButton = new JButton(loadImage("assets/search.png"));
        //change cursor to a hand cursor when hovering
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location
                String userInput=searchTextField.getText();

                //validate
                if(userInput.replaceAll("\\s","").length() <=0){
                    return;
                }
                //retrieve weather data
                weatherData= WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition= (String)weatherData.get("weather_condiiton");

                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("assets/snow.png"));
                        break;
                }
                double temperature= (double) weatherData.get("temperature");
                temperatureText.setText(temperature+"C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity= (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b>" +humidity+ "%</html> ");

                double windspeed= (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b>" +windspeed+ "km/h</html> ");
            }
        });
        add(searchButton);
    }
    //used to create images
    private ImageIcon loadImage(String resourcePath){
        try{//read image file from path given
            BufferedImage image = ImageIO.read(new File(resourcePath));
        //returns an image
            return new ImageIcon(image);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
}

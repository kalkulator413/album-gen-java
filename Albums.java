//FNU Kalkin, 2021
//fix text panel sizes, make it so more accessible on windows\
//make it so files update when gui used
//make it so images aren't blocked

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;

class Albums extends JFrame {

  static int listItemWidth = 18;

  public static boolean LIVE = true;

  public static List<String[]> albums = new ArrayList<String[]>();
  public static List<String[]> randoms = new ArrayList<String[]>();

  public static int genreNum = 1;
  public static String[] genreTypes = {"Any", 
        "New", 
        "Hip Hop", 
        "Pop", 
        "Rock", 
        "Folk", 
        "Shoegaze", 
        "Dream Pop", 
        "Indie Rock", 
        "Experimental", 
        "Punk", 
        "Blues", 
        "Jazz", 
        "Screamo"};

  public static void main(String[] args) throws IOException {

    // System.setProperty("file.encoding","UTF-8");

    JFrame start = new JFrame();
    DefaultListModel<String> genre = new DefaultListModel<>(); 
    JList<String> list1 = new JList<>(genre);
    JButton bStart =new JButton("Generate");

    selectionScreen(start, list1, bStart, genre);

    bStart.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                int gNum = list1.getSelectedIndex() + 1;
                genreNum = gNum;
                start.setVisible(false);

                if (LIVE) {
                  String site = getSite();
                  try{
                    int numPages = getNumPages(gNum);

                    for (int i = 1; i <= numPages; i ++) {
                      URL url;
                      if (i == 1)
                        url = new URL(site);
                      else
                        url = new URL(site + i + "/#results");
                      getAlbums(url);
                    }

                  } catch (IOException ioe) {
                    System.out.println(ioe);
                  }

                } else {

                  try {
                    getAlbums();
                  } catch (FileNotFoundException fnf) {
                    System.out.println(fnf);
                  }
                }

                writeFiles();
                randomizeAlbums();

                showAlbums();
              }  
           });

    start.getContentPane().setBackground(new Color(30, 30, 30));
    start.setLayout(null);
    start.setVisible(true);

  }

  public static int getNumPages(int gNum) {
    if (gNum == 1)
      return 5;
    else if (gNum == 2)
      return 3;
    else
      return 4;
  }

  public static String[] album(String title, String artist, String rating, String pic, String genres) {
    return new String[]{title, artist, rating, pic, genres};
  }

  public static String getTitle(String[] album) {
    return album[0];
  }

  public static String getArtist(String[] album) {
    return album[1];
  }

  public static String getRating(String[] album) {
    return album[2];
  }

  public static String getPic(String[] album) {
    return album[3];
  }

  public static String getGenres(String[] album) {
    return album[4];
  }

  public static void selectionScreen(JFrame start, JList<String> list1, JButton bStart, DefaultListModel<String> genre) {
    start.setSize(900, 660);

    for (String s : genreTypes) {
      genre.addElement(s);
    }

    list1.setBounds(300, 250, 100, listItemWidth*genreTypes.length);
    start.add(list1);

    bStart.setBounds(500,275,100,50);
    bStart.setBackground(new Color(30, 30, 30));
    start.add(bStart);

    final JLabel label = new JLabel("Select Genre:");          
    label.setBounds(400, 150, 100, 50);
    label.setForeground(new Color(200, 200, 200));
    start.add(label);
  }

  public static String fixString(String title) {
    title = title.replace("</div>", "");
    title = title.replace("</a>", "");
    title = title.replace("amp;", "");
    title = title.replace("&#39;", "'");
    title = title.replace("&#&quot;;", "\"");
    title = title.replace("<br", "");
    title = title.replace("</span>", "");
    title = title.replace("\">", "");
    title = title.replace("&quot;", "\"");

    return title;
  }

  public static String getSite() {
    String genre = genreTypes[genreNum - 1];

    if (genre == "New") {
      try {
        String year = getYear();
        return "https://rateyourmusic.com/charts/top/album,mixtape/" + year + "/";
      } catch (IOException e) {
        System.out.println(e);
      }
    }

    String site = "https://rateyourmusic.com/charts/top/album,mixtape/all-time/";

    site += "g:" + genre.toLowerCase().replace(" ", "-") + "/";

    return site;
  }

  public static String getYear() throws IOException {
    URL url = new URL("https://www.calendardate.com/todays.htm");

    Scanner sc = new Scanner(url.openStream());

    while(sc.hasNext()) {
      String s = sc.next();
      
      if (s.equals("src=\"//pagead2.googlesyndication.com/pagead/js/adsbygoogle.js\"></script>")) {
        for (int i = 0; i < 7; i ++)
          s = sc.next();
        // System.out.println("got date " + s);
        return s;
      }
    }

    return "2022";
  }

  public static void getAlbums() throws FileNotFoundException {
    String fileName = genreTypes[genreNum - 1].toLowerCase();
    fileName = fileName.replace(" ", "-") + ".txt";

    File file = new File("./Genres/" + fileName);

    Scanner sc = new Scanner(file);
    for (int i = 0; i < 120; i ++) {
      String title = sc.nextLine();
      String artist = sc.nextLine();
      String image = sc.nextLine();
      String genres = sc.nextLine();
      String rating = sc.nextLine();
      sc.nextLine();

      albums.add(album(title, artist, rating, image, genres));
    }
  }

  public static void getAlbums(URL url) throws IOException {
    Scanner sc = new Scanner(url.openStream());

    while(sc.hasNext()) {
       String s = sc.next();
       
       if (s.contains("/release/album/") || s.contains("/release/mixtape/")) {

        String n = sc.next();
        int i = n.indexOf("\"");

        if (i > 0 && !n.contains("\"[Album")) {
          
          String title = "";

          String c = sc.next();

          //cover image
          while(!c.contains("topcharts")) {
            c = sc.next();
          }

          c = sc.next();
          String picLink = c.substring(c.indexOf("/") + 2 ,c.length() - 1);

          //title
          while (c.length() < 13 || !c.substring(0, 13).equals("title=\"[Album")) {
            c = sc.next();
          }

          title = c.substring(c.indexOf(">") + 1);
          c = sc.next();

          while (c.charAt(0) != '<') {
            title += " " + c;
            c = sc.next();
          }

          //artist
          while(c.length() < 15 || !c.substring(0, 15).equals("class=\"artist\">")) {
            c = sc.next();
          }

          i = c.indexOf(">");
          String artist = c.substring(i+1);

          boolean multiple = false;
          c = sc.next();
          while (c.charAt(0) != '<') {
            if (c.charAt(0) == '/') {
              multiple = true;
              break;
            }
            artist += " " + c;
            c = sc.next();
          }

          //if multiple artists
          if (multiple) {
            while(c.length() < 15 || !c.substring(0, 15).equals("class=\"artist\">")) {
              c = sc.next();
            }
            i = c.indexOf(">");
            artist += ", " + c.substring(i+1);

            c = sc.next();
            while (c.charAt(0) != '<') {
              artist += " " + c;
              c = sc.next();
            }
          }

          //rating
          while(!c.contains("topcharts_avg_rating_stat")) {
            c = sc.next();
          }

          String rating = c.substring(c.indexOf(">") + 1);
          rating = fixString(rating);

          Double r = 1.5 + 2 * Double.parseDouble(rating);
          r = Math.round(100 * r)/ 100.0;
          if (r > 10.0) {
            r = 10.0;
          }
          rating = Double.toString(r);

          //genres
          while(!c.contains("topcharts_item_genres\"")) {
            c = sc.next();
          }

          c = sc.next();
          String genres = c.substring(c.indexOf(">") + 1);
          c = sc.next();
          while (c.charAt(0) != '<') {
            genres += " " + c;
            c = sc.next();
          }

          //if multiple
          while (genres.charAt(genres.length() - 1) == ',') {
            while(!c.contains("topcharts_item_genres\"")) {
              c = sc.next();
            }

            c = sc.next();
            String tempW = c.substring(c.indexOf(">") + 1);
            c = sc.next();
            while (c.charAt(0) != '<') {
              tempW += " " + c;
              c = sc.next();
            }

            genres += " " + tempW;
          }

          artist = fixString(artist);
          title = fixString(title);
          genres = fixString(genres);

          //hardcoded fixes
          if (artist.equals("The Velvet Underground &"))
            artist = "The Velvet Underground & Nico";   
          if (s.contains("★"))
            title = "Blackstar";
          if (title.equals("In Rainbows"))
            picLink = "upload.wikimedia.org/wikipedia/en/1/14/Inrainbowscover.png";
          if (title.equals("The Money Store"))
            picLink = "static.wikia.nocookie.net/4chanmusic/images/2/29/Tms-1200.jpg/revision/latest/scale-to-width-down/500?cb=20160210044217";
          if (title.equals("Electric Ladyland"))
            picLink = "images-na.ssl-images-amazon.com/images/I/51VWpTObMvL.jpg";
          if (title.equals("A Promise"))
            picLink = "i.scdn.co/image/ab67616d0000b2737c2f4ecdb972f4a9b698d08a";
          if (title.equals("Is This It"))
            picLink = "upload.wikimedia.org/wikipedia/en/e/e7/The_Strokes_-_Ist_Tis_It_US_cover.png";
          if (title.equals("Nevermind"))
            picLink = "upload.wikimedia.org/wikipedia/en/b/b7/NirvanaNevermindalbumcover.jpg";
          if (title.equals("The Long Goodbye: LCD Soundsystem Live at Madison Square Garden"))
            picLink = "upload.wikimedia.org/wikipedia/en/d/d6/LCD_Soundsystem_-_The_Long_Goodbye_cover_art.jpg";

          if (title.equals("E·MO·TION")) {
            rating = "11.0";
            albums.add(0, album(title, artist, rating, picLink, genres));
          } else {
            albums.add(album(title, artist, rating, picLink, genres));
          }

        }

       }
    }

  }

  public static void randomizeAlbums() {
    //System.out.println("How many albums would you like to generate? (MAX 120)");

    int numAlbums = 9;//console.nextInt();
    int num = 1;

    ArrayList<String[]> albumsC = new ArrayList<String[]>();
    for (String[] s : albums) {
      albumsC.add(s);
    }

    while(numAlbums > 0) {
      int i = (int) (albumsC.size() * Math.random());


      //System.out.println(num + ". " + albums.get(i)[0] + " - " + albums.get(i)[1]);
      randoms.add(album(getTitle(albumsC.get(i)), getArtist(albumsC.get(i)), getRating(albumsC.get(i)), getPic(albumsC.get(i)), getGenres(albumsC.get(i))));
      
      albumsC.remove(i);

      numAlbums--;
      num++;
    }
  }

  public static void writeFiles() {
    try {
      FileWriter myWriter = new FileWriter("./Genres/" + genreTypes[genreNum - 1].replace(" ", "-").toLowerCase() + ".txt");
      for (String[] s : albums) {
        myWriter.write(getTitle(s) + "\n");
        myWriter.write(getArtist(s)+ "\n");
        myWriter.write(getPic(s)+ "\n");
        myWriter.write(getGenres(s)+ "\n");
        myWriter.write(getRating(s)+ "\n");
        myWriter.write("\n");
      }
      myWriter.close();
    } catch(IOException e) {
      System.out.println(e);
    }
  }

  
  public static void showAlbums() {
    Albums test = new Albums();
    test.setSize(900, 600/9 * randoms.size() + 60);
    test.setResizable(false);
    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JButton rStart = new JButton("New Genre");
    rStart.setBounds(775, 65, 110, 50);
    rStart.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){ 
            test.setVisible(false);
            for (int i = 0; i < randoms.size(); i=i) {
              randoms.remove(i);
            }
            for (int i = 0; i < albums.size(); i=i) {
              albums.remove(i);
            }
            try{
              main(null);
            } catch(IOException ioe) {
              System.out.println(ioe);
            }
        }  
    });

    JButton b = new JButton("New Albums");
    b.setBounds(775, 5, 110, 50);
    b.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){ 
            for (int i = 0; i < randoms.size(); i=i) {
              randoms.remove(i);
            } 
            randomizeAlbums();
            Graphics g = test.getGraphics();
            test.paint(g);

            test.add(b);
            test.add(rStart);

          test.setLayout(null);
          test.setVisible(true);
        }  
    });

    test.add(b);
    test.add(rStart);

    test.getContentPane().setBackground(new Color(30, 30, 30));
    test.setLayout(null);
    test.setVisible(true);
  }

  public void paint(Graphics g) {

    g.setColor(new Color(30, 30, 30));
    g.fillRect(0, 0, 900, 600/9 * randoms.size() + 60);

    g.setColor(Color.WHITE);

    for (int i = 0; i < randoms.size(); i ++) {
      //number
      g.setFont(new Font("SANS_SERIF", 0, 20));
      g.drawString(Integer.toString(i + 1) + ". ", 80, 50 + i*70);

      //title
      g.setFont(new Font("SANS_SERIF", 2, 20));
      g.drawString(getTitle(randoms.get(i)), 103, 50 + i * 70);

      //artist
      g.setFont(new Font("SANS_SERIF", 0, 17));
      g.drawString(getArtist(randoms.get(i)), 105, 67 + i * 70);

      //genres
      g.setFont(new Font("MONOSPACED", 0, 13));
      g.drawString(getGenres(randoms.get(i)), 106, 80 + i * 70);

      //rating
      //more red if low rating, more green if high rating

      double r = Double.parseDouble(getRating(randoms.get(i)));

      int red = 1000 - (int) (100 * r);
      if (red > 255)
        red = 255;
      
      int green = -745 + (int) (100 * r);
      if (green < 0)
        green = 0;

      if (getTitle(randoms.get(i)).equals("E·MO·TION"))
        g.setColor(new Color(255, 215, 0));
      else
        g.setColor(new Color(red, green, 0));

      g.drawString(getRating(randoms.get(i)), 110 + (int) (getGenres(randoms.get(i)).length() * 13 * ((0.5 + 0.75)/2 + 0.75)/2), 80 + i * 70);
      g.setColor(Color.WHITE);

      //images
      g.fillRect(9, 34+i*70, 52, 52);
      try {
        BufferedImage img = ImageIO.read(new URL("https://" + getPic(randoms.get(i))));
        g.drawImage(img, 10, 35 + i * 70, 50, 50, null);
      } catch (IOException ioe) {
        System.out.println(ioe);
      }
    }    
  }

}
//FNU Kalkin, 2021

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.FileWriter;

public class Printer{

	public static List<String[]> albums = new ArrayList<String[]>();

	public static String[] genreTypes = {"Any", "Hip Hop", "Pop", "Rock", "Folk", "Shoegaze", "Dream Pop", "Experimental", "Punk", "Blues", "Jazz", "Screamo"};

	public static void main(String[] args) throws IOException {

    for (int g = 1; g <= genreTypes.length; g++) {

		//int g = 1;

        String site = getSite(g);

        int numPages;
        if (g == 1)
          numPages = 5;
        else
          numPages = 4;

        for (int i = 1; i <= numPages; i ++) {
          URL url;
          if (i == 1)
            url = new URL(site);
          else
            url = new URL(site + i + "/#results");
          getAlbums(url);
        }

        FileWriter myWriter = new FileWriter("./Genres/" + genreTypes[g - 1].replace(" ", "-").toLowerCase() + ".txt");
        for (String[] s : albums) {
          for (String str: s) {
            myWriter.write(str + "\n");
          }
          myWriter.write("\n");
        }
        myWriter.close();

        for (int i = 0; i < albums.size(); i=i) {
          albums.remove(i);
        }

    	}

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

          if (title.equals("A Promise"))
            picLink = "i.scdn.co/image/ab67616d0000b2737c2f4ecdb972f4a9b698d08a";
          
          albums.add(new String[]{title, artist, picLink, genres, rating});
        }

       }
    	}
	}

	public static String getSite(int genreNum) {
    	String site = "https://rateyourmusic.com/charts/top/album,mixtape/all-time/";

    
      // String s = genreTypes[genreNum - 1];
      // s = s.toLowercase();

      site += "g:" + genreTypes[genreNum - 1].toLowerCase().replace(" ", "-") + "/";
      return site;
    }


}
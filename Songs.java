import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class Songs {

	public static List<String[]> songs = new ArrayList<String[]>();

	static int numPages = 5;

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

	public static String[] single(String title, String artist, String rating, String pic, String genres) {
	    return new String[]{title, artist, rating, pic, genres};
	}

	public static String getTitle(String[] single) {
	    return single[0];
	}

	public static String getArtist(String[] single) {
	    return single[1];
	}

	public static String getRating(String[] single) {
	    return single[2];
	}

	public static String getPic(String[] single) {
	    return single[3];
	}

	public static String getGenres(String[] single) {
	    return single[4];
	}

 	public static void getSingles(URL url) throws IOException {
	    Scanner sc = new Scanner(url.openStream());

	    while(sc.hasNext()) {
	       String s = sc.next();
	       
	       if (s.contains("/release/single/")) {

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

	          Double r = 1.4 + 2 * Double.parseDouble(rating);
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

	          
	          songs.add(single(title, artist, rating, picLink, genres));

	        }

	       }
	    }
	}

	public static void writeFiles() {
	    try {
	      FileWriter myWriter = new FileWriter("./Genres/singles.txt");
	      for (String[] s : songs) {
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

	public static void main(String[] args) throws IOException {
		String site = "https://rateyourmusic.com/charts/top/single/all-time/";

		for (int i = 1; i <= numPages; i ++) {
			URL url;
			if (i == 1)
				url = new URL(site);
			else
				url = new URL(site + i + "/#results");
			getSingles(url);
		}

		writeFiles();
	}
}
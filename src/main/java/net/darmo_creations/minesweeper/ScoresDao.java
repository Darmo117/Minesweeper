/*
 * Copyright © 2017 Damien Vergnet
 * 
 * This file is part of Minesweeper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.darmo_creations.minesweeper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.minesweeper.model.Score;
import net.darmo_creations.utils.JarUtil;

/**
 * This DAO handles scores loading/writing.
 *
 * @author Damien Vergnet
 */
public class ScoresDao {
  private static ScoresDao instance;

  /**
   * Returns the global instance.
   */
  public static ScoresDao getInstance() {
    if (instance == null)
      instance = new ScoresDao();
    return instance;
  }

  /**
   * Loads the scores.
   * 
   * @return the scores
   */
  public Map<Difficulty, List<Score>> load() {
    Map<Difficulty, List<Score>> scores = new HashMap<>();

    try {
      File fXmlFile = new File(URLDecoder.decode(JarUtil.getJarDir() + "scores.xml", "UTF-8"));
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(fXmlFile);

      doc.getDocumentElement().normalize();

      Element root = (Element) doc.getElementsByTagName("Scores").item(0);
      if (root != null) {
        NodeList difficultiesList = root.getElementsByTagName("Difficulty");

        for (int i = 0; i < difficultiesList.getLength(); i++) {
          Element difficultyElement = ((Element) difficultiesList.item(i));
          NodeList timesList = difficultyElement.getElementsByTagName("Score");
          Difficulty difficulty = Difficulty.valueOf(difficultyElement.getAttribute("name").toUpperCase());
          List<Score> scoresList = new ArrayList<>();

          for (int j = 0; j < timesList.getLength(); j++) {
            Element timeElement = (Element) timesList.item(j);
            try {
              Duration duration = Duration.ofSeconds(Long.parseLong(timeElement.getTextContent()));
              Score score = new Score(timeElement.getAttribute("username"), duration);
              scoresList.add(score);
            }
            catch (NumberFormatException ex) {}
          }
          scores.put(difficulty, scoresList);
        }
      }
    }
    catch (NullPointerException | ClassCastException | ParserConfigurationException | SAXException | IOException ex) {}

    return scores;
  }

  /**
   * Writes the scores to the disk.
   * 
   * @param scores the scores
   */
  public void save(Map<Difficulty, List<Score>> scores) {
    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
      Document doc = docBuilder.newDocument();

      Element root = doc.createElement("Scores");

      for (Map.Entry<Difficulty, List<Score>> entry : scores.entrySet()) {
        Element difficultyElement = doc.createElement("Difficulty");
        difficultyElement.setAttribute("name", entry.getKey().name().toLowerCase());
        for (Score score : entry.getValue()) {
          Element scoreElement = doc.createElement("Score");

          scoreElement.setAttribute("username", score.getUsername());
          scoreElement.setTextContent("" + score.getDuration().getSeconds());
          difficultyElement.appendChild(scoreElement);
        }
        root.appendChild(difficultyElement);
      }

      doc.appendChild(root);

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      StreamResult result = new StreamResult(new File(URLDecoder.decode(JarUtil.getJarDir() + "scores.xml", "UTF-8")));

      transformer.transform(new DOMSource(doc), result);
    }
    catch (ParserConfigurationException | TransformerException | UnsupportedEncodingException ex) {}
  }

  public boolean sendScore(Score score, Difficulty difficulty) {
    try {
      // TODO use real URL.
      String url = "http://darmo-creations.local/products/minesweeper/post_scores.php";
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      con.setRequestMethod("POST");

      StringJoiner urlParameters = new StringJoiner("&");
      urlParameters.add("username=" + score.getUsername());
      urlParameters.add("time=" + score.getDuration().getSeconds());
      urlParameters.add("difficulty=" + difficulty.name().toLowerCase());

      con.setDoOutput(true);
      try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
        wr.writeBytes(urlParameters.toString());
        wr.flush();
      }

      if (con.getResponseCode() == 200) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
          String inputLine;
          StringBuffer response = new StringBuffer();

          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }

          DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          Document doc = dBuilder.parse(new InputSource(new StringReader(response.toString())));
          Element root = (Element) doc.getElementsByTagName("response").item(0);
          if (root != null) {
            Node node = root.getElementsByTagName("code").item(0);
            if (node != null) {
              int code = Integer.parseInt(node.getTextContent());

              if (code == 200)
                return true;
            }
          }
        }
      }

      return false;
    }
    catch (IOException | SAXException | ParserConfigurationException | NumberFormatException ex) {
      return false;
    }
  }

  private ScoresDao() {}
}

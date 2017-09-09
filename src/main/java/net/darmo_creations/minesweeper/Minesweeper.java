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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import net.darmo_creations.gui_framework.Application;
import net.darmo_creations.gui_framework.ApplicationRegistry;
import net.darmo_creations.gui_framework.config.Language;
import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.minesweeper.gui.MainFrame;
import net.darmo_creations.utils.version.Version;

public class Minesweeper implements Application {
  public static boolean debug;

  public static final Version CURRENT_VERSION = new Version(1, 1, 0, true);

  @Override
  public void preInit() {
    List<Language> l = new ArrayList<>();
    l.add(new Language("English", Locale.US));
    l.add(new Language("Français", Locale.FRANCE));
    l.add(new Language("Esperanto", new Locale("eo")));

    ApplicationRegistry.setLanguages(l);

    WritableConfig.registerTag(ConfigTags.SEND_SCORES, true);
    WritableConfig.registerTag(ConfigTags.BUTTONS_SIZE, 15);
  }

  @Override
  public MainFrame initFrame(WritableConfig config) {
    return new MainFrame(config);
  }

  @Override
  public String getName() {
    return "Minesweeper";
  }

  @Override
  public Version getCurrentVersion() {
    return CURRENT_VERSION;
  }

  @Override
  public Optional<String> getIcon() {
    return Optional.of("/assets/icons/icon.png");
  }

  @Override
  public Optional<String> getLicenseIcon() {
    return Optional.of("/assets/icons/gplv3-127x51.png");
  }

  @Override
  public InputStream getLanguageFilesStream(Language language) {
    return Minesweeper.class.getResourceAsStream("/assets/langs/" + language.getCode() + ".lang");
  }

  @Override
  public boolean checkUpdates() {
    return true;
  }

  @Override
  public Optional<String> getRssUpdatesLink() {
    return Optional.of("https://github.com/Darmo117/Minesweeper/releases.atom");
  }

  @Override
  public boolean hasAboutDialog() {
    return true;
  }

  @Override
  public Optional<String> getAboutFilePath() {
    return Optional.of("/assets/about.html");
  }

  @Override
  public boolean hasHelpDocumentation() {
    return false;
  }

  @Override
  public Optional<String> getHelpDocumentationLink(Language language) {
    return Optional.empty();
  }
}

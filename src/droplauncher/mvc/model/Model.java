/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.mvc.model;

import adakite.prefs.Prefs;
import adakite.util.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.DropLauncher;
import droplauncher.mvc.view.SimpleAlert;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import javafx.scene.control.Alert.AlertType;

public class Model {

  public static final int AUTO_EJECT_DELAY = 3500; /* milliseconds */
  public static final int AUTO_REJOIN_DELAY = 3500; /* milliseconds */

  private BWHeadless bwheadless;

  public Model() {
    this.bwheadless = new BWHeadless();
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  public void ensureDefaultSettings() {
    if (!Model.hasPrefValue(DropLauncher.PropertyKey.VERSION.toString())) {
      /* Version is not set. */
      Model.setPref(DropLauncher.PropertyKey.VERSION.toString(), DropLauncher.PROGRAM_VERSION);
    } else {
      /* Check if current version was loaded last time. */
      String previousVersion = Model.getPref(DropLauncher.PropertyKey.VERSION.toString());
      if (!previousVersion.equalsIgnoreCase(DropLauncher.PROGRAM_VERSION)) {
        /* Newer/older version detected. */
        try {
          /* Compile newer/older message. */
          boolean isNewer = (DropLauncher.PROGRAM_VERSION.compareTo(previousVersion) > 0);
          String message = "";
          message += "Different version detected!" + AdakiteUtils.newline(2);
          message += "Previously installed version: " + previousVersion + AdakiteUtils.newline();
          message += "Currently installed version: " + DropLauncher.PROGRAM_VERSION + AdakiteUtils.newline(2);
          message += "You are loading a";
          if (isNewer) {
            message += " newer";
          } else {
            message += "n older";
          }
          message += " version of " + DropLauncher.PROGRAM_NAME + ". All previous program settings have been automatically cleared to ensure stability. Please restart the application.";
          /* Display compiled message. */
          new SimpleAlert().showAndWait(
              AlertType.INFORMATION,
              View.DialogTitle.PROGRAM_NAME,
              message
          );
          /* Clear all settings. */
          DropLauncher.PREF_ROOT.clear();
        } catch (Exception ex) {
          /* Do nothing. */
        }
        /* Close program. */
        System.exit(0);
      }
    }
    if (!Model.hasPrefValue(Starcraft.PropertyKey.CLEAN_SC_DIR.toString())) {
      Model.setPrefEnabled(Starcraft.PropertyKey.CLEAN_SC_DIR.toString(), true);
    }
    if (!Model.hasPrefValue(BWAPI.PropertyKey.COPY_WRITE_READ.toString())) {
      Model.setPrefEnabled(BWAPI.PropertyKey.COPY_WRITE_READ.toString(), true);
    }
    if (!Model.hasPrefValue(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString())) {
      Model.setPrefEnabled(BWAPI.PropertyKey.WARN_UNKNOWN_BWAPI_DLL.toString(), true);
    }
    if (!Model.hasPrefValue(View.PropertyKey.SHOW_LOG_WINDOW.toString())) {
      Model.setPrefEnabled(View.PropertyKey.SHOW_LOG_WINDOW.toString(), true);
    }
    /* Disabled for now. Force user to be aware and select which StarCraft directory will be used. */
//    if (!Model.hasPrefValue(Starcraft.Property.STARCRAFT_EXE.toString())) {
//      /* Attempt to determine StarCraft directory from registry. */
//      try {
//        String dir = WinRegistry.strValue(Starcraft.REG_ENTRY_EXE_32BIT, "Program");
//        if (!AdakiteUtils.isNullOrEmpty(dir)) {
//          Model.setPref(Starcraft.Property.STARCRAFT_EXE.toString(), dir);
//        }
//      } catch (Exception ex) {
//        /* Do nothing. */
//      }
//    }
    if (!Model.hasPrefValue(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString())) {
      Model.setPrefEnabled(DropLauncher.PropertyKey.AUTO_EJECT_BOT.toString(), true);
    }
    if (!Model.hasPrefValue(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString())) {
      Model.setPrefEnabled(DropLauncher.PropertyKey.AUTO_BOT_REJOIN.toString(), false);
    }
  }

  private static Prefs getPrefs(String uniqueKey) {
    uniqueKey = uniqueKey.toLowerCase(Locale.US);

    for (DropLauncher.PropertyKey val : DropLauncher.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return DropLauncher.PREF_ROOT;
      }
    }

    for (BWAPI.PropertyKey val : BWAPI.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWAPI.PREF_ROOT;
      }
    }

    for (View.PropertyKey val : View.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return View.PREF_ROOT;
      }
    }

    for (Starcraft.PropertyKey val : Starcraft.PropertyKey.values()) {
      if (uniqueKey.equals(val.toString())) {
        return Starcraft.PREF_ROOT;
      }
    }

    throw new IllegalArgumentException("not found: uniqueKey=" + uniqueKey);
  }

  /**
   * Removes this preference node and all of its descendants, invalidating
   * any preferences contained in the removed nodes.
   *
   * @see java.util.prefs.Preferences#removeNode()
   * @throws BackingStoreException if this operation cannot be completed
   *     due to a failure in the backing store, or inability to
   *     communicate with it.
   */
  public static void clearPrefs() throws BackingStoreException {
    DropLauncher.PREF_ROOT.clear();
  }

  /**
   * Returns the associated value with the specified key.
   *
   * @param key specified key
   * @throws IllegalStateException if the specified key does not exist
   */
  public static String getPref(String key) {
    return getPrefs(key).get(key);
  }

  /**
   * Tests whether the specified key has an associated value.
   *
   * @param key specified key
   */
  public static boolean hasPrefValue(String key) {
    try {
      getPref(key);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Sets the specified key to the specified value regardless if
   * the specified key existed previously.
   *
   * @param key specified key
   * @param val specified value
   */
  public static void setPref(String key, String val) {
    getPrefs(key).set(key, val);
  }

  /**
   * Tests whether the specified key has a TRUE or FALSE value. Returns
   * FALSE even if the specified key does not exist.
   *
   * @param key specified key
   */
  public static boolean isPrefEnabled(String key) {
    return getPrefs(key).isEnabled(key);
  }

  /**
   * Sets the specified key to the specified boolean value regardless if
   * the specified key existed previously.
   *
   * @param key specified key
   * @param enabled specified boolean value
   */
  public static void setPrefEnabled(String key, boolean enabled) {
    getPrefs(key).set(key, Boolean.toString(enabled));
  }

}

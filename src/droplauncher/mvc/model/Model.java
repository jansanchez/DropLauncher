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
import droplauncher.bot.Bot;
import droplauncher.bwapi.BWAPI;
import droplauncher.bwheadless.BWHeadless;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.DropLauncher;
import java.util.Locale;
import java.util.prefs.BackingStoreException;

public class Model {

  private Bot bot;
  private BWHeadless bwheadless;

  public Model() {
    this.bot = new Bot();
    this.bwheadless = new BWHeadless();
  }

  public Bot getBot() {
    return this.bot;
  }

  public BWHeadless getBWHeadless() {
    return this.bwheadless;
  }

  public void ensureDefaultSettings() {
    if (!Model.hasPrefValue(DropLauncher.Property.VERSION.toString())
        || (Model.hasPrefValue(DropLauncher.Property.VERSION.toString())
          && !Model.getPref(DropLauncher.Property.VERSION.toString()).equals(DropLauncher.PROGRAM_VERSION))) {
      Model.setPref(DropLauncher.Property.VERSION.toString(), DropLauncher.PROGRAM_VERSION);
    }
    if (!Model.hasPrefValue(Starcraft.Property.CLEAN_SC_DIR.toString())) {
      Model.setPrefEnabled(Starcraft.Property.CLEAN_SC_DIR.toString(), true);
    }
    if (!Model.hasPrefValue(BWAPI.Property.COPY_WRITE_READ.toString())) {
      Model.setPrefEnabled(BWAPI.Property.COPY_WRITE_READ.toString(), true);
    }
    if (!Model.hasPrefValue(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString())) {
      Model.setPrefEnabled(BWAPI.Property.WARN_UNKNOWN_BWAPI_DLL.toString(), true);
    }
    if (!Model.hasPrefValue(View.Property.SHOW_LOG_WINDOW.toString())) {
      Model.setPrefEnabled(View.Property.SHOW_LOG_WINDOW.toString(), true);
    }
  }

  private static Prefs getPrefs(String uniqueKey) {
    uniqueKey = uniqueKey.toLowerCase(Locale.US);

    for (DropLauncher.Property val : DropLauncher.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return DropLauncher.PREF_ROOT;
      }
    }

    for (BWAPI.Property val : BWAPI.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWAPI.PREF_ROOT;
      }
    }

    for (BWHeadless.Property val : BWHeadless.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return BWHeadless.PREF_ROOT;
      }
    }

    for (View.Property val : View.Property.values()) {
      if (uniqueKey.equals(val.toString())) {
        return View.PREF_ROOT;
      }
    }

    for (Starcraft.Property val : Starcraft.Property.values()) {
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

  public static String getPref(String key) {
    return getPrefs(key).get(key);
  }

  public static boolean hasPrefValue(String key) {
    try {
      getPref(key);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public static void setPref(String key, String val) {
    getPrefs(key).set(key, val);
  }

  public static boolean isPrefEnabled(String key) {
    return getPrefs(key).isEnabled(key);
  }

  public static void setPrefEnabled(String key, boolean enabled) {
    getPrefs(key).set(key, Boolean.toString(enabled));
  }

}

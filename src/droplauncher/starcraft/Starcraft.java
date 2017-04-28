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

package droplauncher.starcraft;

import adakite.util.AdakiteUtils;
import java.util.Locale;

/**
 * Utilities and constants class for StarCraft.
 */
public class Starcraft {

  /**
   * Enum for race selections in StarCraft.
   */
  public enum Race {

    TERRAN("Terran"),
    PROTOSS("Protoss"),
    ZERG("Zerg"),
    RANDOM("Random"),
    ;

    private final String str;

    private Race(String str) {
      this.str = str;
    }

    /**
     * Returns the corresponding Race object.
     *
     * @param str specified string
     * @return
     *     the corresponding Race object if valid,
     *     otherwise null
     */
    public static Race get(String str) {
      str = str.toLowerCase(Locale.US);
      for (Race val : Race.values()) {
        if (str.equals(val.toString().toLowerCase(Locale.US))) {
          return val;
        }
      }
      return null;
    }

    @Override
    public String toString() {
      return this.str;
    }

  }

  /* Maximum profile name length in Brood War 1.16.1 */
  public static final int MAX_PROFILE_NAME_LENGTH = 24;

  private Starcraft() {}

  /**
   * Returns a filtered string compatible with a StarCraft profile name.
   *
   * @param str specified string
   */
  public static String cleanProfileName(String str) {
    if (AdakiteUtils.isNullOrEmpty(str, true)) {
      return "";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if ((ch >= 'A' && ch <= 'Z')
          || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')
          || ch == ' ') {
        sb.append(ch);
      }
    }

    String ret = sb.toString().trim();
    if (ret.length() > MAX_PROFILE_NAME_LENGTH) {
      ret = ret.substring(0, MAX_PROFILE_NAME_LENGTH);
    }
    return ret;
  }

}

/*
> bwheadless.exe --help
Usage: bwheadless.exe [option]...
A tool to start StarCraft: Brood War as a console application, with no graphics, sound or user input.

  -e, --exe         The exe file to launch. Default 'StarCraft.exe'.
  -h, --host        Host a game instead of joining.
  -j, --join        Join instead of hosting. The first game that is found
                    will be joined.
  -n, --name NAME   The player name. Default 'playername'.
  -g, --game NAME   The game name when hosting. Defaults to the player name.
                    If this option is specified when joining, then only games
                    with the specified name will be joined.
  -m, --map FILE    The map to use when hosting.
  -r, --race RACE   Zerg/Terran/Protoss/Random/Z/T/P/R (case insensitive).
  -l, --dll DLL     Load DLL into StarCraft. This option can be
                    specified multiple times to load multiple dlls.
      --networkprovider NAME  Use the specified network provider.
                              'UDPN' is LAN (UDP), 'SMEM' is Local PC (provided
                              by BWAPI). Others are provided by .snp files and
                              may or may not work. Default SMEM.
      --lan         Sets the network provider to LAN (UDP).
      --localpc     Sets the network provider to Local PC (this is default).
      --lan-sendto IP  Overrides the IP that UDP packets are sent to. This
                       can be used together with --lan to connect to a
                       specified IP-address instead of broadcasting for games
                       on LAN (The ports used is 6111 and 6112).
      --installpath PATH  Overrides the InstallPath value that would usually
                          be read from the registry. This is used by BWAPI to
                          locate bwapi-data/bwapi.ini.
*/

package droplauncher.bwheadless;

import adakite.utils.AdakiteUtils;
import adakite.utils.FileOperation;
import droplauncher.ini.IniFile;
import droplauncher.starcraft.Race;
import droplauncher.starcraft.Starcraft;
import droplauncher.util.Constants;
import java.io.File;
import java.util.logging.Logger;

/**
 * Class for handling execution and communication with the
 * bwheadless.exe process.
 */
public class BWHeadless {

  private static final Logger LOGGER = Logger.getLogger(BWHeadless.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

//  public static final File BW_HEADLESS_EXE = new File("bwheadless.exe");
  public static final String BW_HEADLESS_INI_SECTION = "bwheadless";

  public static final String DEFAULT_BOT_NAME = "BOT";
  public static final Race DEFAULT_BOT_RACE = Race.TERRAN;
  public static final GameType DEFAULT_GAME_TYPE = GameType.LAN;
  public static final JoinMode DEFAULT_JOIN_MODE = JoinMode.JOIN;

  private File starcraftExe; /* required */
  private File bwapiDll; /* required */
  private String botName; /* required */
  private File botDll; /* required only when client is absent */
  private File botClient; /* *.exe or *.jar, required only when DLL is absent  */
  private Race botRace; /* required */
  private GameType gameType; /* required */
  private JoinMode joinMode; /* required */

  private IniFile ini;

  public BWHeadless() {
    this.starcraftExe = null;
    this.bwapiDll = null;
    this.botName = DEFAULT_BOT_NAME;
    this.botDll = null;
    this.botClient = null;
    this.botRace = DEFAULT_BOT_RACE;
    this.gameType = DEFAULT_GAME_TYPE;
    this.joinMode = DEFAULT_JOIN_MODE;

    this.ini = null;
  }

  public boolean isReady() {
    return (getReadyStatus() == ReadyStatus.READY);
  }

  public ReadyStatus getReadyStatus() {
    if (this.starcraftExe == null
        || !(new FileOperation(this.starcraftExe).doesFileExist())) {
      return ReadyStatus.STARTCRAFT_EXE;
    } else if (this.bwapiDll == null
        || !(new FileOperation(this.bwapiDll).doesFileExist())) {
      return ReadyStatus.BWAPI_DLL;
    } else if (AdakiteUtils.isNullOrEmpty(this.botName, true)
        || this.botName.length() > Starcraft.MAX_PROFILE_NAME_LENGTH) {
      //TODO: Also check for invalid characters.
      return ReadyStatus.BOT_NAME;
    } else if (
        (this.botDll == null || !(new FileOperation(this.botDll).doesFileExist()))
        && (this.botClient == null || !(new FileOperation(this.botClient).doesFileExist()))
    ) {
      /* If both the bot DLL and bot client fields are missing. */
      return ReadyStatus.BOT_FILE;
    } else if (this.botRace == null) {
      return ReadyStatus.BOT_RACE;
    } else if (this.gameType == null) {
      return ReadyStatus.GAME_TYPE;
    } else if (this.joinMode == null) {
      return ReadyStatus.JOIN_MODE;
    } else {
      return ReadyStatus.READY;
    }
  }

  public boolean start() {
    if (isReady()) {
      System.out.println("BWH: Ready");
      return true;
    } else {
      System.out.println("BWH: Not Ready");
      return false;
    }
  }

  public void stop() {
    System.out.println("BWH: Stop");
  }

  public IniFile getIniFile() {
    return this.ini;
  }

  public void setIniFile(IniFile ini) {
    this.ini = ini;
  }

  public File getStarcraftExe() {
    return this.starcraftExe;
  }

  public boolean setStarcraftExe(File starcraftExe) {
    if (!(new FileOperation(starcraftExe)).doesFileExist()) {
      this.starcraftExe = null;
      updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), "");
      return false;
    }
    this.starcraftExe = starcraftExe;
    updateSettingsFile(PredefinedVariable.STARCRAFT_EXE.toString(), this.starcraftExe.getAbsolutePath());
    return true;
  }

  public File getBwapiDll() {
    return this.bwapiDll;
  }

  public boolean setBwapiDll(File bwapiDll) {
    if (!(new FileOperation(bwapiDll)).doesFileExist()) {
      this.bwapiDll = null;
      updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), "");
      return false;
    }
    this.bwapiDll = bwapiDll;
    updateSettingsFile(PredefinedVariable.BWAPI_DLL.toString(), this.bwapiDll.getAbsolutePath());
    return true;
  }

  public String getBotName() {
    return this.botName;
  }

  public boolean setBotName(String botName) {
    this.botName = Starcraft.cleanProfileName(botName);
    if (AdakiteUtils.isNullOrEmpty(this.botName)) {
      this.botName = DEFAULT_BOT_NAME;
    }
    updateSettingsFile(PredefinedVariable.BOT_NAME.toString(), this.botName);
    return true;
  }

  public File getBotDll() {
    return this.botDll;
  }

  public boolean setBotDll(File botDll) {
    if (!(new FileOperation(botDll)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
      return false;
    }
    this.botClient = null;
    this.botDll = botDll;
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), this.botDll.getAbsolutePath());
    return true;
  }

  public File getBotClient() {
    return this.botClient;
  }

  public boolean setBotClient(File botClient) {
    if (!(new FileOperation(botClient)).doesFileExist()) {
      this.botClient = null;
      this.botDll = null;
      updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), "");
      updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
      return false;
    }
    this.botDll = null;
    this.botClient = botClient;
    updateSettingsFile(PredefinedVariable.BOT_DLL.toString(), "");
    updateSettingsFile(PredefinedVariable.BOT_CLIENT.toString(), this.botClient.getAbsolutePath());
    return true;
  }

  public Race getBotRace() {
    return this.botRace;
  }

  public boolean setBotRace(Race botRace) {
    if (botRace == null) {
      this.botRace = DEFAULT_BOT_RACE;
      updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());
      return false;
    }
    this.botRace = botRace;
    updateSettingsFile(PredefinedVariable.BOT_RACE.toString(), this.botRace.toString());
    return true;
  }

  public GameType getGameType() {
    return this.gameType;
  }

  public boolean setGameType(GameType gameType) {
    if (gameType == null) {
      this.gameType = DEFAULT_GAME_TYPE;
      updateSettingsFile(PredefinedVariable.GAME_TYPE.toString(), this.gameType.toString());
      return false;
    }
    this.gameType = gameType;
    updateSettingsFile(PredefinedVariable.GAME_TYPE.toString(), this.gameType.toString());
    return true;
  }

  public JoinMode getJoinMode() {
    return this.joinMode;
  }

  public boolean setJoinMode(JoinMode joinMode) {
    if (joinMode == null) {
      this.joinMode = DEFAULT_JOIN_MODE;
      updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
      return false;
    }
    this.joinMode = joinMode;
    updateSettingsFile(PredefinedVariable.JOIN_MODE.toString(), this.joinMode.toString());
    return true;
  }

  private void updateSettingsFile(String name, String key, String val) {
    this.ini.setVariable(name, key, val);
  }

  private void updateSettingsFile(String key, String val) {
    updateSettingsFile(BW_HEADLESS_INI_SECTION, key, val);
  }

  public void readSettingsFile(IniFile ini) {
    if (ini == null) {
      return;
    }
    String val;
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.STARCRAFT_EXE.toString()))) {
      setStarcraftExe(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BWAPI_DLL.toString()))) {
      setBwapiDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_NAME.toString()))) {
      setBotName(val);
    } else {
      setBotName(DEFAULT_BOT_NAME);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_DLL.toString()))) {
      setBotDll(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_CLIENT.toString()))) {
      setBotClient(new File(val));
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.BOT_RACE.toString()))) {
      if (val.equalsIgnoreCase(Race.TERRAN.toString())) {
        setBotRace(Race.TERRAN);
      } else if (val.equalsIgnoreCase(Race.ZERG.toString())) {
        setBotRace(Race.ZERG);
      } else if (val.equalsIgnoreCase(Race.PROTOSS.toString())) {
        setBotRace(Race.PROTOSS);
      } else {
        setBotRace(Race.TERRAN);
      }
    } else {
      setBotRace(DEFAULT_BOT_RACE);
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.GAME_TYPE.toString()))) {
      if (val.equalsIgnoreCase(GameType.LAN.toString())) {
        setGameType(GameType.LAN);
      } else {
        setGameType(DEFAULT_GAME_TYPE);
      }
    }
    if (!AdakiteUtils.isNullOrEmpty(val = ini.getValue(BW_HEADLESS_INI_SECTION, PredefinedVariable.JOIN_MODE.toString()))) {
      if (val.equalsIgnoreCase(JoinMode.JOIN.toString())) {
        setJoinMode(JoinMode.JOIN);
      } else {
        setJoinMode(DEFAULT_JOIN_MODE);
      }
    }
  }

}
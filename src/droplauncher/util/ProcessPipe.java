package droplauncher.util;

import adakite.debugging.Debugging;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Generic class for handling communication between the main program and
 * a process.
 */
public class ProcessPipe {

  private static final Logger LOGGER = Logger.getLogger(ProcessPipe.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  public static final double DEFAULT_READ_TIMEOUT = (double)0.25; /* seconds */

  private File file;
  private String[] args;
  private Process process;
  private InputStream is;
  private BufferedReader br; /* read from process */
  private OutputStream os;
  private BufferedWriter bw; /* write to process */
  private boolean isOpen;

  public ProcessPipe() {
    this.file = null;
    this.args = null;
    this.process = null;
    this.is = null;
    this.br = null;
    this.os = null;
    this.bw = null;
    this.isOpen = false;
  }

  public boolean isOpen() {
    return this.isOpen;
  }

  public boolean open(File file, String[] args) {
    if (file == null) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, Debugging.nullObject("file"));
      }
      return false;
    }

    try {
      String[] command;
      if (args == null) {
        command = new String[1];
      } else {
        this.args = Arrays.copyOf(args, args.length);
        command = new String[this.args.length + 1];
        System.arraycopy(this.args, 0, command, 1, this.args.length);
      }

      this.file = file;
      command[0] = this.file.getAbsolutePath();
      this.process = new ProcessBuilder(command).start();

      //      this.is = this.process.getInputStream();
      //      this.br = new BufferedReader(new InputStreamReader(this.is));
//      StreamGobbler gobblerStdout = new StreamGobbler(this.process.getInputStream());
//      StreamGobbler gobblerStderr = new StreamGobbler(this.process.getErrorStream());
//      gobblerStdout.start();
//      gobblerStderr.start();

      this.os = this.process.getOutputStream();
      this.bw = new BufferedWriter(new OutputStreamWriter(this.os));

      this.isOpen = true;

      return true;
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }

    this.isOpen = false;

    return false;
  }

  public boolean open(File file) {
    return open(file, null);
  }

  /**
   * Closes the pipe and destroys the process.
   *
   * @return
   *     true if all pipe-related objects were closed and no errors
   *         were encountered,
   *     otherwise false
   */
  public boolean close() {
    this.isOpen = false;
    try {
      if (this.br != null && this.is != null
          && this.bw != null && this.os != null
          && this.process != null && this.process.isAlive()) {
        this.br.close();
        this.is.close();
        this.bw.close();
        this.os.close();

      }
      this.process.destroy();
      return true;
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }
    return false;
  }

}
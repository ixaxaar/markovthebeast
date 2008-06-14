package thebeast.pml;

import thebeast.nod.NoDServer;
import thebeast.nodmem.MemNoDServer;
import thebeast.pml.parser.Shell;

import java.io.*;

/**
 * Created by IntelliJ IDEA. User: s0349492 Date: 21-Jan-2007 Time: 16:27:47
 */
public class TheBeast {

  private NoDServer nodServer = new MemNoDServer();

  private final static TheBeast singleton = new TheBeast();

  public final static String VERSION = "0.0.2";

  /**
   * Private constructor as TheBeast is a singleton.
   */
  private TheBeast() {

  }

  /**
   * Gets The "one and only" Beast object.
   *
   * @return TheBeast singleton object.
   */
  public static TheBeast getInstance() {
    return singleton;
  }

  /**
   * Creates a new signature with its own dedicated No D database server.
   *
   * @return a signature object which can be used to create types and predicates and databases.
   */
  public Signature createSignature() {
    return new Signature();
  }

  /**
   * Loads a model (and its signature from a pml file)
   *
   * @param inputStream an input stream in pml format
   * @return a model with the formulas and signature given in the file.
   * @throws Exception if the inputstream can't be read or has syntax errors.
   */
  public Model loadModel(InputStream inputStream) throws Exception {
    Signature signature = createSignature();
    Model model = signature.createModel();
    Shell shell = new Shell(System.in, System.err, System.err);
    shell.load(inputStream, model);
    return model;
  }

  /**
   * Create a new model with an own signature
   *
   * @return an empty model with a fresh empty signature.
   */
  public Model createModel() {
    Signature signature = createSignature();
    return signature.createModel();
  }

  /**
   * Saves the model (and its signature) to the stream
   *
   * @param model        the model to save
   * @param outputStream the stream to write to.
   */
  public void saveModel(Model model, OutputStream outputStream) {

  }

  /**
   * Returns the Database server this beast is using.
   *
   * @return the Database server this beast is using.
   */
  public NoDServer getNodServer() {
    return nodServer;
  }

  /**
   * Main program, just starts the shell.
   *
   * @param args command line arguments (can take a pml file to load)
   * @throws java.io.IOException if the input stream breaks.
   */
  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      File file = new File(args[0]);
      Shell shell = new Shell(new FileInputStream(file), System.out, System.err);
      shell.setArgs(args);
      shell.setDirectory(file.getParentFile() == null ? System.getProperty("user.dir") :
              file.getParentFile().getPath());
      shell.execute();
    } else {
      String home = System.getProperty("home");
      Shell shell = new Shell();
      if (home != null) shell.setDirectory(home);
      shell.interactive();
    }
  }

}

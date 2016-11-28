package com.devonfw.devcon.input;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO This is the class which launch Devcon GUI.
 *
 * @author ssarmoka
 */
public class GUIAppManager extends Application {

  /**
   * CommandRegistry instance
   */
  public static CommandRegistry registry;

  /**
   * CommandManager instance
   */
  public static CommandManager cmdManager;

  private Stage primaryStage;

  /**
   * @param registry CommandRegistry instance
   * @param commandManager CommandManager instance
   * @param args arguments
   */
  public static void main(CommandRegistry registry, CommandManager commandManager, String[] args) {

    GUIAppManager.registry = registry;
    GUIAppManager.cmdManager = commandManager;

    main(args);
  }

  /**
   * @param args input arguments
   */
  public static void main(String[] args) {

    launch(args);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("hiding")
  @Override
  public void start(Stage primaryStage) throws Exception {

    try {

      String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";

      String image = GUIAppManager.class.getClassLoader().getResource(root + Constants.DEVCON_LOGO).toExternalForm();
      String icon = GUIAppManager.class.getClassLoader().getResource(root + Constants.DEVCON_ICON).toExternalForm();

      this.primaryStage = primaryStage;

      primaryStage.setTitle("Devcon");

      BorderPane borderPane = new BorderPane();
      borderPane.setTop(getMenus());

      borderPane.setStyle("-fx-background-image: url('" + image + "'); " + "-fx-background-position: center center; "
          + "-fx-background-repeat: stretch; -fx-background-color: #5b5150;");

      Scene scene = new Scene(borderPane, 700, 800);
      primaryStage.setScene(scene);
      primaryStage.getIcons().add(new Image(icon));
      primaryStage.show();
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

  }

  public MenuBar getMenus() {

    // anti-pattern. These kind of functionality a) should NOT be in generic "Utils" classed
    // and b) not instantiated manually (no "New"). Either you set them as instances or you usse single-tons (jn Java:
    // statics)
    // Need refactoring
    Utils utils = new Utils();

    final MenuBar menuBar = new MenuBar();

    List<CommandModuleInfo> modules =
        utils.sortModules(registry.getCommandModules(), new NumericSortComparator<CommandModuleInfo>());

    for (int i = 0; i < modules.size(); i++) {

      if (!modules.get(i).isVisible())
        continue;

      final Menu menu = new Menu(modules.get(i).getName());

      Optional<CommandModuleInfo> commands = GUIAppManager.registry.getCommandModule(modules.get(i).getName());
      Collection<Command> sortedCommands =
          utils.sortCommands(commands.get().getCommands(), new NumericSortComparator<Command>());
      Iterator<Command> itrCommands = sortedCommands.iterator();
      while (itrCommands.hasNext()) {
        Command cmd = itrCommands.next();
        MenuItem item = new MenuItem(cmd.getName());
        menu.getItems().add(item);
        item.setOnAction(new ShowCommandHandler(cmd, GUIAppManager.cmdManager, this.primaryStage));
      }
      if (i == 0) {
        MenuItem item = new MenuItem("Exit");
        menu.getItems().add(item);
        item.setOnAction(new ShowCommandHandler());
      }
      menuBar.getMenus().add(menu);
    }
    return menuBar;

  }

}

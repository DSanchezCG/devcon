package com.devonfw.devcon.common.impl;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModule;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.utils.Constants;
import com.google.common.base.Optional;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class CommandRegistryImpl implements CommandRegistry {

  private HashMap<String, CommandModule> modules;

  private Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public CommandRegistryImpl() {
    this.modules = new HashMap<>();
  }

  public CommandRegistryImpl(String pkgName) {
    this();
    registerModules(pkgName);
  }

  public void registerModules(String pkgName) {

    for (Class<?> moduleClass : this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class)) {

      Annotation annotation = moduleClass.getAnnotation(CmdModuleRegistry.class);
      CmdModuleRegistry moduleAnnotation = (CmdModuleRegistry) annotation;

      CommandModule cmdmodule = new CommandModuleImpl(moduleAnnotation.name(), moduleAnnotation.description(),
          moduleAnnotation.visible(), moduleClass);
      this.modules.put(cmdmodule.getName(), cmdmodule);
    }
  }

  @Override
  public Optional<CommandModule> getCommandModule(String module) {

    if (this.modules.containsKey(module)) {
      return Optional.of(this.modules.get(module));
    } else {
      return Optional.absent();
    }
  }

  @Override
  public Optional<Command> getCommand(String module, String command) {

    Optional<CommandModule> mod_ = getCommandModule(module);
    if (mod_.isPresent())
      return mod_.get().getCommand(command);
    else
      return Optional.absent();
  }

  /**
   * the name for the element
   */
  private String name;

  /**
   * the description of the element
   */
  private String description;

  /**
   * determines whether component is visible on the console
   */
  private boolean visible = true;

  /**
   * @return visible
   */
  public boolean isVisible() {

    return this.visible;
  }

  /**
   * @param visible new value of {@link #getvisible}.
   */
  public void setVisible(boolean visible) {

    this.visible = visible;
  }

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @param name new value of {@link #getname}.
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @param description new value of {@link #getdescription}.
   */
  public void setDescription(String description) {

    this.description = description;
  }

}

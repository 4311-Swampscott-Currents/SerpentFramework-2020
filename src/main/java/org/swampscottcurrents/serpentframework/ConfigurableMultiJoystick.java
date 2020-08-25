package org.swampscottcurrents.serpentframework;

import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;

/** Represents a group of customizable joysticks, with button/axis bindings changeable on Shuffleboard. */
public class ConfigurableMultiJoystick {
    
    private HashMap<Integer, Joystick> joysticks = new HashMap<Integer, Joystick>();
    private HashMap<String, Integer> axes = new HashMap<String, Integer>();
    private HashMap<String, Integer> buttons = new HashMap<String, Integer>();
    private HashMap<String, Double> controlParameters = new HashMap<String, Double>();
    private ArrayList<Integer> allEntryListeners = new ArrayList<Integer>();
    private HashMap<String, ButtonState> buttonStates = new HashMap<String, ButtonState>();

    private static Preferences prefs;
    private static NetworkTableInstance nt;

    public ConfigurableMultiJoystick() {
        prefs = Preferences.getInstance();
        nt = NetworkTableInstance.getDefault();

        if(Preferences.getInstance().getBoolean("sf.joystick", false)) {
            reloadJoystickPreferences();
            loadNewJoysticks();
        }
        else {
            resetJoystick();
        }
    }

    private void loadNewJoysticks() {
        for(int i : axes.values()) {
            int composite = joystickNumberFromCompositeAttribute(i);
            if(!joysticks.containsKey(composite)) {
                joysticks.put(composite, new Joystick(composite));
            }
        }
        for(int i : buttons.values()) {
            int composite = joystickNumberFromCompositeAttribute(i);
            if(!joysticks.containsKey(composite)) {
                joysticks.put(composite, new Joystick(composite));
            }
        }
    }

    private void resetJoystick() {
        clearNetworkTableEntryListeners();

        prefs.putBoolean("sf.joystick", true);
        allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick"), notification -> {
            if(!prefs.getBoolean("sf.joystick", false)) {
                resetJoystick();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));

        buttons = getDefaultButtonBindings();
        axes = getDefaultAxes();
        controlParameters = getDefaultControlParameters();
        buttonStates = new HashMap<String, ButtonState>();
        for(String key : buttons.keySet()) {
            buttonStates.put(key, ButtonState.Up);
            prefs.putInt("sf.joystick/button/" + key, buttons.get(key));
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/button/" + key), notification -> {
                buttons.remove(key);
                buttons.put(key, (int)notification.value.getDouble());
                loadNewJoysticks();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
        for(String key : axes.keySet()) {
            prefs.putInt("sf.joystick/axes/" + key, axes.get(key));
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/axes/" + key), notification -> {
                axes.remove(key);
                axes.put(key, (int)notification.value.getDouble());
                loadNewJoysticks();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
        for(String key : controlParameters.keySet()) {
            prefs.putDouble("sf.joystick/parameter/" + key, controlParameters.get(key));
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/parameter/" + key), notification -> {
                controlParameters.remove(key);
                controlParameters.put(key, notification.value.getDouble());
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
        loadNewJoysticks();
    }

    private void reloadJoystickPreferences() {
        clearNetworkTableEntryListeners();
        allEntryListeners.add(NetworkTableInstance.getDefault().addEntryListener(NetworkTableInstance.getDefault().getTable("Preferences").getEntry("sf.joystick"), notification -> {
            if(!Preferences.getInstance().getBoolean("sf.joystick", false)) {
                resetJoystick();
            }
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));

        buttons = new HashMap<String, Integer>();
        axes = new HashMap<String, Integer>();
        controlParameters = new HashMap<String, Double>();
        for(String key : getDefaultButtonBindings().keySet()) {
            buttonStates.put(key, ButtonState.Up);
            if(!prefs.containsKey("sf.joystick/button/" + key)) {
                prefs.putInt("sf.joystick/button/" + key, buttons.get(key));
            }
            buttons.put(key, prefs.getInt("sf.joystick/button/" + key, 0));
            
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/button/" + key), notification -> {
                buttons.remove(key);
                buttons.put(key, (int)notification.value.getDouble());
                loadNewJoysticks();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
        for(String key : getDefaultAxes().keySet()) {
            if(!prefs.containsKey("sf.joystick/axes/" + key)) {
                prefs.putInt("sf.joystick/axes/" + key, axes.get(key));
            }
            axes.put(key, prefs.getInt("sf.joystick/axes/" + key, 0));
            
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/axes/" + key), notification -> {
                axes.remove(key);
                axes.put(key, (int)notification.value.getDouble());
                loadNewJoysticks();
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
        for(String key : getDefaultControlParameters().keySet()) {
            if(!prefs.containsKey("sf.joystick/parameter/" + key)) {
                prefs.putDouble("sf.joystick/parameter/" + key, controlParameters.get(key));
            }
            controlParameters.put(key, prefs.getDouble("sf.joystick/parameter/" + key, 0));
            
            allEntryListeners.add(nt.addEntryListener(nt.getTable("Preferences").getEntry("sf.joystick/parameter/" + key), notification -> {
                controlParameters.remove(key);
                controlParameters.put(key, notification.value.getDouble());
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kDelete));
        }
    }

    private void clearNetworkTableEntryListeners() {
        for(Integer i : allEntryListeners) {
            NetworkTableInstance.getDefault().removeEntryListener(i);
        }
        allEntryListeners.clear();
    }

    /** Returns the current button binding for a certain action. */
    public final int getCompositeButtonNumber(String buttonName) {
        return buttons.get(buttonName);
    }

    /** Returns the current setting for a joystick parameter. */
    public final double getJoystickParameter(String parameter) {
        return controlParameters.get(parameter);
    }

    /** This function returns a map of all default button bindings.  It should be overridden when defining new behaviors. */
    public HashMap<String, Integer> getDefaultButtonBindings() {
        return new HashMap<String, Integer>();
    }

    /** This function returns a map of all default axes bindings.  It should be overridden when defining new behaviors. */
    public HashMap<String, Integer> getDefaultAxes() {
        return new HashMap<String, Integer>();
    }

    /** This function returns a map of all default controller parameters.  It should be overridden when defining new behaviors. */
    public HashMap<String, Double> getDefaultControlParameters() {
        HashMap<String, Double> toReturn = new HashMap<String, Double>();
        toReturn.put("Deadzone", 0.1);
        return toReturn;
    }

    /** Returns the position of the specified axis on the specified controller's joystick. */
    public double getAxis(String axis) {
        int axisNum = axes.get(axis);
        return processAxisInput(joysticks.get(joystickNumberFromCompositeAttribute(axisNum)).getRawAxis(attributeNumberFromCompositeAttribute(axisNum)));
    }

    /** Returns whether a button bound to the specified name has just been pressed down. */
    public final boolean getButtonPressed(String buttonName) {
        return buttonStates.get(buttonName) == ButtonState.Pressed;
    }

    /** Returns whether a button bound to the specified name is pressed or being held down. */
    public final boolean getButton(String buttonName) {
        return buttonStates.get(buttonName) == ButtonState.Pressed || buttonStates.get(buttonName) == ButtonState.Held;
    }

    /** Returns whether a button bound to the specified name has just been released. */
    public final boolean getButtonReleased(String buttonName) {
        return buttonStates.get(buttonName) == ButtonState.Released;
    }

    /** Returns the current press-state of the button bound to the specified name. */
    public final ButtonState getButtonState(String buttonName) {
        return buttonStates.get(buttonName);
    }

    /** Applies mathematical operations to controller input such as deadband, squaring, et cetera. */
    public double processAxisInput(double input) {
        if(Math.abs(input) < controlParameters.get("Deadzone")) {
            return 0;
        }
        return Math.signum(input) * (input * input);
    }

    /** Returns the specified joystick number from a composite joystick-attribute number.  By default, composite attributes are written in the format (JOYSTICK NUMBER)(BUTTON NUMBER)(BUTTON NUMBER), such as "254," where the number represents joystick 2, attribute 54. */
    public int joystickNumberFromCompositeAttribute(int attributeNumber) {
        return (attributeNumber - (attributeNumber % 100)) / 100;
    }

    /** Returns the specified attribute number from a composite joystick-attribute number.  By default, composite attributes are written in the format (JOYSTICK NUMBER)(BUTTON NUMBER)(BUTTON NUMBER), such as "254," where the number represents joystick 2, attribute 54. */
    public int attributeNumberFromCompositeAttribute(int attributeNumber) {
        return attributeNumber % 100;
    }

    /** This method updates all of the button states, checking to see which are held down, pressed, and released.  This should be called once per cycle. */
    public final void update() {
        for(String buttonName : buttonStates.keySet()) {
            int buttonNum = buttons.get(buttonName);
            boolean isPressedNow = joysticks.get(joystickNumberFromCompositeAttribute(buttonNum)).getRawButton(attributeNumberFromCompositeAttribute(buttonNum));
            ButtonState state = buttonStates.get(buttonName);
            if(isPressedNow) {
                if(state == ButtonState.Up || state == ButtonState.Released) {
                    buttonStates.replace(buttonName, ButtonState.Pressed);
                }
                else if(state == ButtonState.Pressed) {
                    buttonStates.replace(buttonName, ButtonState.Held);
                }
            }
            else {
                if(state == ButtonState.Held || state == ButtonState.Pressed) {
                    buttonStates.replace(buttonName, ButtonState.Released);
                }
                else if(state == ButtonState.Released) {
                    buttonStates.replace(buttonName, ButtonState.Up);
                }
            }
        }
    }

    public enum ButtonState {
        Pressed,
        Released,
        Held,
        Up
    }
}
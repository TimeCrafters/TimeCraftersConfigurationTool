# TimeCrafters Configuration Tool
An app for editing, either locally or remotely, JSON configuration files on the Robot Controller/Rev Control Hub.

## Features
* TACNET - **T**imeCrafters **A**uxiliary **C**onfiguration **NET**work
  * Enables syncing configurations between devices

* Multiple Configurations
  * Create multiple configurations for specific robots/projects.

* Presets
  * Save Groups or Actions as Presets to quickly add pre-configurated Groups and Actions.

* Search
  * Search through the active configurations Groups, Actions, Variables and Presets.

## Usage
### Via TACNET
#### (Driver Station <=> Robot Controller)

* Install this app on both the Driver Station and Robot Controller
* Open app on both devices and navigate to the TACNET menu
* On the Robot Controller, click "Start Server" button
* On the Driver Station, click "Connect" button
* On the Driver Station, navigate to the Settings menu and click on "Configurations"
* Create a new configuration
* Navigate to the editor and start creating

#### (Driver Station <=> REV Control Hub)
Instructions coming soon...

### Local
#### (Robot Controller or Rev Control Hub)
* Install the app on the Robot Controller/Rev Control Hub
* Navigate to the Settings menu and click on "Configurations"
* Create a new configuration
* Navigate to the editor and start creating

## Using the Configuration
See the [TimeCraftersConfigurationTool module in our UltimateGoal repo](https://github.com/TimeCrafters/UltimateGoal/tree/master/TimeCraftersConfigurationTool/src/main/java/org/timecrafters/TimeCraftersConfigurationTool)

## Installing
* Clone repo
* Open in Android Studio
* Wait for dependencies to download
* Install

## Contributing
* Clone this repo and create a new branch for your feature/patch.
* Author your changes
* Commit your changes and push to your fork
* Open a pull request

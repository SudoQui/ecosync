# Eco Sync Android

Eco Sync is a gamified sustainability fitness app that turns everyday movement and habits into progress toward global journeys, team challenges, and real world rewards.

## Features

### Journey

* Set long term step goals such as walking from one city to another or even from Earth to Mars
* Watch your daily steps move an avatar along a world or space map
* Break big goals into smaller milestones so users stay motivated

### Community

* Create or join teams for companies, clubs, or friend groups
* Compete on step and impact leaderboards
* Track shared progress such as total steps, virtual distance, and estimated emissions saved

### Rewards

* Convert steps and eco actions into points
* Redeem points for rewards with local partners such as cafes or small businesses
* Support basic partner campaigns such as limited time offers and streak based bonuses

### Scan

* Scan QR codes on bins, posters, or partner venues
* Show clear recycling instructions for common materials and packaging types
* Award extra points when users recycle correctly or complete challenges

### Energy

* Connect to supported Internet of Things energy devices or custom integrations
* Visualise daily and weekly energy use inside the app
* Link energy savings to the same points system so users see one combined impact score

## Tech stack

* Language: Kotlin
* UI: Jetpack Compose with Material three components
* Architecture: MVVM with ViewModels and state holders
* Asynchronous work: Coroutines and Flow
* Local data: Room or DataStore for caching journeys, rewards, and user state
* Dependency injection: Hilt or Koin
* Build tools: Gradle and Android Studio

You can trim or update this list to match your actual implementation.

## Getting started

### Prerequisites

* Android Studio Iguana or newer
* Android device or emulator running Android twelve or newer
* Java Development Kit set up in Android Studio

### Clone and open

* Clone this repository
* Open the project folder in Android Studio
* Let Android Studio sync Gradle and download dependencies

### Run the app

* Select a device or emulator from the device selector
* Press Run in Android Studio
* Create a test profile in the app and try out Journey, Community, Rewards, Scan, and Energy pages

## Project structure

Adapt the section below to match your package names.

* `app` main Android module  
  * `ui` Jetpack Compose screens, navigation, and theming  
  * `data` repositories, local storage, and remote data sources  
  * `domain` use cases and business logic  
  * `model` Kotlin data classes for journeys, users, rewards, and energy metrics  

## Configuration

If your app uses any remote services, note the configuration here.

Examples

* Environment file or local properties for API keys
* Backend base URLs for production and test
* Flags to turn IoT integration or rewards on and off in debug builds

## Roadmap

Short term

* Finish core Journey flow with attractive visuals
* Add basic Community leaderboards
* Create simple local Rewards and Scan flows without a backend

Medium term

* Connect to a real backend for user accounts, leaderboards, and partner data
* Integrate QR codes for recycling education and rewards
* Add first Internet of Things energy integration

Long term

* Partner with universities, councils, or companies for real pilot programs
* Design more advanced journeys such as walking across continents or to planets
* Refine the points and rewards system to better encourage long term healthy habits

## Contributing

Right now this project is primarily a portfolio and learning piece.

If you would like to suggest ideas, open an issue with

* A short summary of the idea
* Why it helps users or partners
* Any technical notes if you have them

## License

Choose a license for the project, for example MIT License, and place it in a separate LICENSE file at the root of the repository.

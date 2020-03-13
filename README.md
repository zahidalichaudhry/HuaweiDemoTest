## HuaweiDemoTest  Application
- This application have only 1 Screen first you will see Map When First open map will go to you current location and display Star on it


- In this application I used MVVM architecture and repository file for network call for adding another abstraction layer.
- For network call Retrofit
- For  asynchronous I used Coroutines of Kotlin


## Flow of this Application
- First applicaiton open Map loaded
- Check Location Permissions then when they are granted you will go to you current location by invoking function on mainViewModel 
- On you current location Star is displayed 
- When you click on the star you get address of your location 
- Above you location star on  screen you will marker you can drag that marker and when you click on that marker you will get address
- If you click anywhere on map you then draggable marker will move to that location and you can check address by clicking on that Location 
## How to Test
- You can install Apk in root folder named debugTest.apk
- You can clone this repository from GitHub and open it in Android studio and run this project  real device it will run fine and check code also.

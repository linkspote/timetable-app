# Timetable App
This is the repository for the completely new version of our old timetable app.

# Shortcuts for UI elements, variables and co
Please be sure to replace "Identifier" with the name that you want to use and that you are using camelCase!
## UI elements
* WebView => wv_Identifier
* ListView => lv_Identifier
* TextView => tv_Identifier

## Variables
When you have to use a data type, e.g. when using arrays, make sure you add the prefix of the data type at first like s_arrIdentifier.
* Int => iIdentifier
* Float => fIdentifier
* Double => dIdentifier
* Char => cIdentifier
* String => sIdentifier
* Boolean => bIdentifier
* Array => arrIdentifier
* List => lsIdentifier
* ArrayAdapter => arradIdentifier
* View => vwIdentifier
* ListView => lvIdentifier

# TODO list
* Improve file handling, e.g. flush them on URI changes
* Improve functionality behind fragment switching in drawer
* Get previously disabled settings to work
* Create a class that handles all the background stuff (e.g. get class list, refresh class URI and so on) for the class selection setting
* Create new app icon and maybe change the name
* Add missing/disabled features, e.g. marks and notes
* Fix week view, should open in app and not in browser _> Maybe replace this functionality by a simple "Show plan on website" button
* Add "About me" settings including name, gender, etc. for personalized notifications
* Create a mechanism to check the genders a name belongs to
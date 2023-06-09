# Txipirones
A mastodon REST client in javafx

The entirety of the client's code was developed for the "Software Engineering I" subject in the second year of the "Computer Engineering" university degree.
It contains some very good code, some mediocre code and bunch of horrible, spaguetti code. I'm publishing it here for the sake of posterity.

## Description
This application provides basic support in order to interact with the mastodon social network. It's a multi-user client with support for accounts in the mastodon.social server. Among other things, this client is able to:
- View "home" timeline of toots.
- View any user's profile.
- Favourite/Reboot/Bookmark any status on any account.
- Search users and toots
- Follow/Unfollow users
- View media attached to a toot
- Post/Schedule a toot
- Change your profile photo
- Adapt to your native language
- Customize its appearance (Light/Dark mode)
- Alert you and show your incoming and past notifications
- Manage multiple accounts

## Some views
![Timeline with media view](https://i.imgur.com/DEaF0jG.png)
![Profile View](https://i.imgur.com/0l628SX.png)
![Authentication View](https://github.com/UPV-EHU-Bilbao/txipirones-mastodonFX/assets/61473739/f9b91a6d-cb77-43d1-a36c-cef7f91475c2)

## Chef's touch
For developers these are features related to code quality:

> Proper error handling for all use cases. Custom UI made for errors

> Everything is downloaded in dynamically, in real-time from the mastodon API

> A custom HTML Parser was coded for parsing the toot's content into a TextFlow, removing the heavy WebView engine

> Extra fxml components were used from the JExtras library, and css stylesheets were tailored to the application 

> All methods leverage an asynchronous implementation, so no ui-blocking is made on the user's end

> Cross-compatible with all operating systems

> Infinite scrolling was implemented for the timeline and profile view

> Some Junit tests were made in order to check the correctness of our java API and backend methods

> Log4j is used to debug and save application logs. Log retention policy is set to 14 days and log compression is enabled.

> Javadoc documents all classes & all methods


## Download and Installation
Application installers for all operating systems are included in the "assets" section in the [latest release page](https://github.com/nicoagr/txipironclient/releases/latest).

### Legal
*This project does NOT have an open-source license. For more information about open source licenses, click [here](https://opensource.org/faq). If you want more information about what does mean to NOT have an open-source license, click [here](https://choosealicense.com/no-permission/)*

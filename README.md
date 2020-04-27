# Description

This android app uses **Netatmo API** to retrieve data from personal Weather Station.

This repo is working, and is a first step toward creating own personal API on server to use with personal weather station.

# Features

- Authentication flow
  - OAuth2 authorization step.
  - OAuth2 access token request with `redirect_uri` code.
  - OAuth2 refresh token step.

- Temperature
  - Display indoor and outdoor temperature. Refresh whenever app is launched and when dashboard nav is opened.
  - Display indoor/outdoor min/max temperatures.

- Status
  - Display various status information like app version and weather station status.


# Netatmo API

[API documentation](https://dev.netatmo.com)

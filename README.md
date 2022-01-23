# foursquare-android

This project follows the MVVM architecture to use Foursquare's API to fetch places in an area and display them on a map. 

## Features
1. Upon panning the map, new places are fetched and displayed. 
2. All results are cached for when the user returns to a previously visited spot and displayed early
3. The cached items are only displayed if they fall in the bounds of the google map's current viewport
4. On selecting a place/marker, the marker is highlighted and the bottom sheet is populated with more data for that venue
5. On panning and releasing the map, the selected marker is not removed from the map to maintain the user's context
6. The bottomsheet can then be scrolled up or the Details button can be pressed to animate it to fullscreen to allow the user to view the details

## Notes
1. Local cache is currently held as a simple variable instead of the Room database as no persistence was required for the cached items
2. Unit tests are currently mid-development

# Architecture 
![Architecture](https://drive.google.com/uc?export=view&id=1aN4Sy3WA_1KeOaFa-Uk3yKDlZxOMCkn5)

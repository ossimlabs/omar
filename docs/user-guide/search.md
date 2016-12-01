# Search


## Filters
### Keyword
* **BE:**
Basic Encyclopedia Number - An alphanumeric sequence representing a particular target.
* **CC:**
Country Code - Two letters representing a particular country.
* **File:**
Filename - The filename of an image.
* **Image:**
Image ID - The Image ID found in the metadata.
* **Mission:**
Mission ID - The image's mission ID.
* **Sensor:**
Sensor ID - The image's sensor ID.
* **Target:**
Target ID - The image's target ID.
* **WAC:**
World Area Code - A numeric sequence representing a particular are on the globe.

### Ranges
* **Cloud Cover:**
A number between 0 and 100.
* **NIIRS:**
A number between 0 and 9.
* **Azimuth:**
A number between 0 and 360.
* **Graze/Elev:**
A number between 0 and 90.
* **Sun Azimuth:**
A number between 0 and 360.
* **Sun Elevation:**
A number between -90 and 90.

### Spatial
* **Map Viewport:**
This filter is on by default. It constrains the query to the boundaries of the current map extent.
* **Point:**
Single clicking on the map will return a potential list of images at that location.
* **Polygon:**
Left-click and hold with the ALT key to create a box that will return a potential list of images.

### Temporal
* **Date Type:**
Specify which metadata field is to be compared, acquisition date or ingest date.
* **Duration:**
Specify the start and stop dates.

## Map
The map will show footprints for all the imagery in the database according to whatever filters are enabled. Every time the map is moved, a new query is issued and the footprints as well as search results are updated.

TIP: You can right-click the map to get that point's coordinate. Also, the mouse-coordinate format found at the bottom-right of the map can be changed simply by clicking on it.

* **Mouse Coordinates:**
The current coordinate of the mouse is displayed in the lower-right corner of the map. You can click the box to cycle through DD, DMS and MGRS formats. Right-click anywhere on the map to get a dialog box of that coordinate in all three formats.

* **Rotation:**
You can rotate the map by holding Shift + Alt while clicking and dragging.

## Results
Search results are displayed in a box to the right of the map. Each image will have the corresponding links associated with it.

* **<span class="fa fa-desktop"></span>&nbsp;:**
View an image in image/raw space.
* **<span class="fa fa-history"></span>&nbsp;:**
View an orthorectified version of an image in TLV.
* **<span class="fa fa-map"></span>&nbsp;:**
Download a superover KML for an image.
* **<span class="fa fa-share-alt"></span>&nbsp;:**
Get a sharable link of an image.
* **<span class="fa fa-download"></span>&nbsp;:**
Download a raw image file.
* **<span class="fa fa-file-image-o"></span>&nbsp;:**
Get a JPIP stream URL.
* **<span class="fa fa-image"></span>&nbsp;:**
Get a JPIP stream URL that has been orthorectified.

### Export
* **CSV:**
Comma Separated Values - Produces a listing of the images and metadata in CSV format.
* **GML2:**
Geography Markup Language - Produces a listing of the images and metadata in GML2 format.
* **GML3:**
Geography Markup Language - Produces a listing of the images and metadata in GML3 format.
* **GML32:**
Geography Markup Language - Produces a listing of the images and metadata in GML32 format.
* **JSON:**
Javascript Object Notation - Produces a listing of the images and metadata in JSON format.
* **KML:**
Keyhole Markup Language - Produces a listing of the images and metadata in KML format.
* **TLV:**
Time Lapse Viewer - Opens a new window to view

### Sort
* **Acquired (Newest):**
Sorts the list such that the image with the most recent acquisition date is first.
* **Acquired (Oldest):**
Sorts the list such that the image with the most recent acquisition date is last.
* **Ingested (Newest):**
Sorts the list such that the image with the most recent ingest date is last.
* **Ingested (Oldest):**
Sorts the list such that the image with the most recent acquisition date is last.
* **Image ID (Asc):**
Sorts the list in alphabetical order according to image ID.
* **Image ID (Desc):**
Sorts the list in reverse alphabetical order according to image ID.
* **Mission (Asc):**
Sorts the list in alphabetical order according to mission ID.
* **Mission (Desc):**
Sorts the list in reverse alphabetical order according to mission ID.
* **Sensor (Asc):**
Sorts the list in alphabetical order according to sensor ID.
* **Sensor (Desc):**
Sorts the list in reverse alphabetical order sensor to image ID.
